/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package alberapps.android.tiempobus.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import alberapps.android.tiempobus.AlarmReceiver;
import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.tasks.LoadTiemposLineaParadaAsyncTask;
import alberapps.android.tiempobus.tasks.LoadTiemposLineaParadaAsyncTask.LoadTiemposLineaParadaAsyncTaskResponder;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.java.tam.BusLlegada;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * This is an example of implementing an application service that can run in the
 * "foreground". It shows how to code this to work well by using the improved
 * Android 2.0 APIs when available and otherwise falling back to the original
 * APIs. Yes: you can take this exact code, compile it against the Android 2.0
 * SDK, and it will against everything down to Android 1.0.
 */
public class TiemposForegroundService extends Service {
	public static final String ACTION_FOREGROUND = "alberapps.android.tiempobus.service.FOREGROUND";
	public static final String ACTION_BACKGROUND = "alberapps.android.tiempobus.service.BACKGROUND";

	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] { int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	private Handler manejador = new Handler();

	String poste = "";

	PendingIntent alarmReceiver = null;

	AlarmManager alarmManager;

	SharedPreferences preferencias = null;

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		} catch (IllegalAccessException e) {
			// Should not happen.
			Log.w("ApiDemos", "Unable to invoke method", e);
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);
			return;
		}

		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNM.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		mSetForegroundArgs[0] = Boolean.FALSE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		try {
			mStartForeground = getClass().getMethod("startForeground", mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground", mStopForegroundSignature);
			return;
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}
		try {
			mSetForeground = getClass().getMethod("setForeground", mSetForegroundSignature);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException("OS doesn't have Service.startForeground OR Service.setForeground!");
		}

		// Avisos

	}

	@Override
	public void onDestroy() {
		// Make sure our notification is gone.
		stopForegroundCompat(R.string.foreground_service_started);
	}

	// This is the old onStart method that will be called on the pre-2.0
	// platform. On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId) {
		handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	void handleCommand(Intent intent) {

		poste = Integer.toString(intent.getExtras().getInt("PARADA"));

		if (ACTION_FOREGROUND.equals(intent.getAction())) {
			// In this sample, we'll use the same text for the ticker and the
			// expanded notification
			CharSequence text = getText(R.string.foreground_service_started);

			// Set the icon, scrolling text and timestamp
			Notification notification = new Notification(R.drawable.ic_stat_tiempobus_notif, text, System.currentTimeMillis());

			// The PendingIntent to launch our activity if the user selects this
			// notification
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

			// Set the info for the views that show in the notification panel.
			notification.setLatestEventInfo(this, getText(R.string.foreground_service) + " Parada: " + poste, text, contentIntent);

			startForegroundCompat(R.string.foreground_service_started, notification);

		} else if (ACTION_BACKGROUND.equals(intent.getAction())) {
			stopForegroundCompat(R.string.foreground_service_started);
		}

		Log.d("TiemposService", "Iniciando Recargas");
		recargaTimer();

	}

	private void recargaTimer() {

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		manejador.removeCallbacks(mRecarga);
		manejador.postDelayed(mRecarga, 60000);

	}

	private Runnable mRecarga = new Runnable() {

		public void run() {

			/*
			 * String alarma = preferencias.getString("alerta", "");
			 * 
			 * if(alarma.equals("")){ Log.d("TiemposService",
			 * "Se detiene el servicio"); stopSelf(); }else{
			 */
			refrescarAlarma();
			manejador.removeCallbacks(mRecarga);
			manejador.postDelayed(this, 60000);

			// }
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Recalcular alarma
	 */
	public void refrescarAlarma() {

		try {

			// showProgressBar(true);

			//SharedPreferences preferencias2 = PreferenceManager.getDefaultSharedPreferences(this);

			//SharedPreferences preferenciasAlertas = getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);
			
			
			String aviso = PreferencesUtil.getAlertaInfo(this);
			
			//String aviso = preferenciasAlertas.getString("alerta", "");

			/**
			 * Sera llamado cuando la tarea de cargar tiempos termine
			 */
			LoadTiemposLineaParadaAsyncTaskResponder loadTiemposLineaParadaAsyncTaskResponder = new LoadTiemposLineaParadaAsyncTaskResponder() {
				public void tiemposLoaded(BusLlegada tiempos) {

					//SharedPreferences preferencias2 = PreferenceManager.getDefaultSharedPreferences(TiemposForegroundService.this);

					//SharedPreferences preferenciasAlertas = getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);
					
					
					String aviso = PreferencesUtil.getAlertaInfo(getApplicationContext());
					
					//String aviso = preferenciasAlertas.getString("alerta", "");

					if (aviso != null && !aviso.equals("") && tiempos != null) {

						String[] datos = aviso.split(";");

						int tiempo = Integer.parseInt(datos[3]);

						int item = Integer.parseInt(datos[4]);

						long milisegundosAlarma = Long.parseLong(datos[5]);

						long mins = ((item + 1) * 5);
						Date actual = new Date();

						boolean cambioTiempo = false;

						// Posible cambio de orden
						// Si estamos en el segundo tiempo y el primero ya es
						// inferior a lo esperado
						if (tiempo == 2 && tiempos.getProximoMinutos() >= mins && tiempos.getSiguienteMinutos() > mins) {
							tiempo = 1;
							cambioTiempo = true;
						}

						Long milisegundosActuales = null;

						if (tiempo == 1) {
							milisegundosActuales = (actual.getTime() + (tiempos.getProximoMinutos() * 60000)) - (mins * 60000);
						} else if (tiempo == 2) {
							milisegundosActuales = (actual.getTime() + (tiempos.getSiguienteMinutos() * 60000)) - (mins * 60000);
						}

						// Verificar si hay que restablecer o se mantiene
						if (tiempo == 1 && (milisegundosAlarma > (milisegundosActuales + 60000) || milisegundosAlarma < (milisegundosActuales - 60000))) {

							calcularAlarma(tiempos, tiempo, item);

							Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_actualizada), Toast.LENGTH_SHORT).show();

							Log.d("TiemposService", "Actualiza 1 actual: " + milisegundosAlarma + " Siguientes: " + milisegundosActuales);

						} else if (tiempo == 2 && (milisegundosAlarma > (milisegundosActuales + 60000) || milisegundosAlarma < (milisegundosActuales - 60000))) {

							calcularAlarma(tiempos, tiempo, item);

							Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_actualizada), Toast.LENGTH_SHORT).show();

							Log.d("TiemposService", "Actualiza 2 actual: " + milisegundosAlarma + " Siguientes: " + milisegundosActuales);

						} else if (cambioTiempo) {

							calcularAlarma(tiempos, tiempo, item);
							Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_actualizada), Toast.LENGTH_SHORT).show();

							Log.d("TiemposService", "Actualiza 3 actual: " + milisegundosAlarma + " Siguientes: " + milisegundosActuales);

						} else {

							Log.d("TiemposService", "No actualiza: actual: " + milisegundosAlarma + " Siguientes: " + milisegundosActuales);

						}

					} else {
						if (tiempos == null) {
							Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();
						}
					}

				}

			};

			if (aviso != null && !aviso.equals("")) {

				String[] datos = aviso.split(";");

				// Control de disponibilidad de conexion
				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					new LoadTiemposLineaParadaAsyncTask(loadTiemposLineaParadaAsyncTaskResponder).execute(datos[0], datos[1]);
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
				}

			}

		} catch (Exception e) {

			Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();

		}

	}

	/**
	 * Calcula y estable la alarma
	 * 
	 * @param theBus
	 * @param tiempo
	 * @param item
	 */
	private void calcularAlarma(BusLlegada theBus, int tiempo, int item) {

		Context context = getApplicationContext();

		Intent intent = new Intent(context, AlarmReceiver.class);

		long et;

		long mins = ((item + 1) * 5);

		// Que tiempo usar
		// Si el primer bus no cumple, se usa el segundo
		if (theBus.getProximoMinutos() < mins) {
			et = theBus.getSiguienteMinutos();
			tiempo = 2;
		} else {
			et = theBus.getProximoMinutos();
			tiempo = 1;
		}

		// Control de tiempo insuficiente o excesivo
		if (et < mins) {
			Toast.makeText(context, String.format(getString(R.string.err_bus_cerca), et), Toast.LENGTH_SHORT).show();
			return;
		} else if (et == 9999) {
			Toast.makeText(context, String.format(getString(R.string.err_bus_sin), et), Toast.LENGTH_SHORT).show();
			return;
		}

		cancelarAlarmas(false);

		String txt = String.format(getString(R.string.alarm_bus), "" + theBus.getLinea(), "" + poste);
		intent.putExtra("alarmTxt", txt);
		intent.putExtra("poste", poste);

		alarmReceiver = PendingIntent.getBroadcast(context, 0, intent, 0);

		Date actual = new Date();

		long milisegundos = (actual.getTime() + (et * 60000)) - (mins * 60000);

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		alarmManager.set(AlarmManager.RTC_WAKEUP, milisegundos, alarmReceiver);

		SimpleDateFormat ft = new SimpleDateFormat("HH:mm");

		String horaT = ft.format(milisegundos);

		String alertaDialog = theBus.getLinea() + ";" + poste + ";" + horaT + ";" + tiempo + ";" + item + ";" + milisegundos;

		Log.d("TiemposService", "Tiempo actualizado a: " + horaT);

		//SharedPreferences preferenciasAlertas = context.getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);
		
		PreferencesUtil.putAlertaInfo(this, alertaDialog);
		
		
		//SharedPreferences.Editor editor = preferenciasAlertas.edit();
		//editor.putString("alerta", alertaDialog);
		//editor.commit();

	}

	/**
	 * Cancelar alarmas establecidas
	 */
	private void cancelarAlarmas(boolean avisar) {

		// Cancelar posible notificacion
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(ns);
		mNotificationManager.cancel(AlarmReceiver.ALARM_ID);

		// Cancelar alarma si hay una definida
		if (alarmReceiver != null) {

			alarmManager.cancel(alarmReceiver);

			alarmReceiver.cancel();

			alarmReceiver = null;

			//SharedPreferences preferenciasAlertas = getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);
			
			
			//SharedPreferences.Editor editor = preferenciasAlertas.edit();
			//editor.putString("alerta", "");
			//editor.commit();

			PreferencesUtil.clearAlertaInfo(this);
			
			if (avisar) {
				Toast.makeText(this, getString(R.string.alarma_cancelada), Toast.LENGTH_SHORT).show();
			}

		}
	}

	// ----------------------------------------------------------------------

	/**
	 * <p>
	 * Example of explicitly starting and stopping the
	 * {@link TiemposForegroundService}.
	 * 
	 * <p>
	 * Note that this is implemented as an inner class only keep the sample all
	 * together; typically this code would appear in some separate class.
	 */
	/*
	 * public static class Controller extends Activity {
	 * 
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * 
	 * setContentView(R.layout.foreground_service_controller);
	 * 
	 * // Watch for button clicks. Button button =
	 * (Button)findViewById(R.id.start_foreground);
	 * button.setOnClickListener(mForegroundListener); button =
	 * (Button)findViewById(R.id.start_background);
	 * button.setOnClickListener(mBackgroundListener); button =
	 * (Button)findViewById(R.id.stop);
	 * button.setOnClickListener(mStopListener); }
	 * 
	 * private OnClickListener mForegroundListener = new OnClickListener() {
	 * public void onClick(View v) { Intent intent = new
	 * Intent(TiemposForegroundService.ACTION_FOREGROUND);
	 * intent.setClass(Controller.this, TiemposForegroundService.class);
	 * startService(intent); } };
	 * 
	 * private OnClickListener mBackgroundListener = new OnClickListener() {
	 * public void onClick(View v) { Intent intent = new
	 * Intent(TiemposForegroundService.ACTION_BACKGROUND);
	 * intent.setClass(Controller.this, TiemposForegroundService.class);
	 * startService(intent); } };
	 * 
	 * private OnClickListener mStopListener = new OnClickListener() { public
	 * void onClick(View v) { stopService(new Intent(Controller.this,
	 * TiemposForegroundService.class)); } }; }
	 */
}
