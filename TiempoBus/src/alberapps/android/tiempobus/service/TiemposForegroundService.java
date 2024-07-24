/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p>
 * based on code by The Android Open Source Project
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.alarma.AlarmReceiver;
import alberapps.android.tiempobus.alarma.GestionarAlarmas;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.tasks.LoadTiemposLineaParadaAsyncTask;
import alberapps.android.tiempobus.tasks.LoadTiemposLineaParadaAsyncTask.LoadTiemposLineaParadaAsyncTaskResponder;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.java.tam.BusLlegada;

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

    private static final Class<?>[] mSetForegroundSignature = new Class[]{boolean.class};
    private static final Class<?>[] mStartForegroundSignature = new Class[]{int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[]{boolean.class};

    private NotificationManager mNM;
    private Method mSetForeground;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];

    private Handler manejador = new Handler();

    String parada = "";

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && mStartForeground.getName().equals("startForeground")) {

                Object[] mStartForegroundArgs2 = new Object[3];
                mStartForegroundArgs2[0] = mStartForegroundArgs[0];
                mStartForegroundArgs2[1] = mStartForegroundArgs[1];
                mStartForegroundArgs2[2] = ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;

                //invokeMethod(mStartForeground, mStartForegroundArgs2);
                startForeground((Integer) mStartForegroundArgs[0], (Notification) mStartForegroundArgs[1], ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
            } else {
                invokeMethod(mStartForeground, mStartForegroundArgs);
            }


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

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d("SERVICIO", "Servicio creado");

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

        Log.d("SERVICIO", "Servicio destruido");

        manejador.removeCallbacks(mRecarga);

        // Make sure our notification is gone.
        stopForegroundCompat(R.string.foreground_service_started);
    }

    // This is the old onStart method that will be called on the pre-2.0
    // platform. On 2.0 or later we override onStartCommand() so this
    // method will not be called.
    @Override
    public void onStart(Intent intent, int startId) {

        Log.d("SERVICIO", "Servicio iniciado");

        handleCommand(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("SERVICIO", "Servicio iniciado");

        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    void handleCommand(Intent intent) {

        Log.d("SERVICIO", "Manejar comando");

        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey("PARADA")) {

            try {

                parada = Integer.toString(intent.getExtras().getInt("PARADA"));

                if (ACTION_FOREGROUND.equals(intent.getAction())) {
                    // In this sample, we'll use the same text for the ticker and the
                    // expanded notification

                    CharSequence text = getString(R.string.foreground_service_started, new Object[]{preferencias.getString("servicio_recarga", "60")});

                    // CharSequence text = getText(R.string.foreground_service_started);

                    // Set the icon, scrolling text and timestamp
                    //Notification notification = new Notification(R.drawable.ic_stat_tiempobus_4, text, System.currentTimeMillis());

                    // The PendingIntent to launch our activity if the user selects this
                    // notification
                    //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

                    // Set the info for the views that show in the notification panel.
                    //notification.setLatestEventInfo(this, getText(R.string.foreground_service) + " Parada: " + parada, text, contentIntent);

                    Notification notification = Notificaciones.notificacionServicioAlerta(this, text);

                    startForegroundCompat(R.string.foreground_service_started, notification);

                } else if (ACTION_BACKGROUND.equals(intent.getAction())) {
                    stopForegroundCompat(R.string.foreground_service_started);
                }

                Log.d("TiemposService", "Iniciando Recargas");
                recargaTimer();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();
            }


        } else {
            Toast.makeText(this, getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void recargaTimer() {

        Log.d("SERVICIO", "Recargar Timer");

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        manejador.removeCallbacks(mRecarga);

        manejador.postDelayed(mRecarga, frecuenciaRecarga());

    }

    private Runnable mRecarga = new Runnable() {

        public void run() {

            refrescarAlarma();
            manejador.removeCallbacks(mRecarga);
            manejador.postDelayed(this, frecuenciaRecarga());

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

            String aviso = PreferencesUtil.getAlertaInfo(this);

            /**
             * Sera llamado cuando la tarea de cargar tiempos termine
             */
            LoadTiemposLineaParadaAsyncTaskResponder loadTiemposLineaParadaAsyncTaskResponder = new LoadTiemposLineaParadaAsyncTaskResponder() {
                public void tiemposLoaded(BusLlegada tiempos) {

                    String aviso = PreferencesUtil.getAlertaInfo(getApplicationContext());

                    if (aviso != null && !aviso.equals("") && tiempos != null) {

                        String[] datos = aviso.split(";");

                        int tiempo = Integer.parseInt(datos[3]);

                        int item = Integer.parseInt(datos[4]);

                        long milisegundosAlarma = Long.parseLong(datos[5]);

                        // long mins = ((item + 1) * 5);

                        long mins = GestionarAlarmas.obtenerMinutos(item);

                        Date actual = new Date();

                        boolean cambioTiempo = false;

                        if (DatosPantallaPrincipal.esTram(parada) && tiempos.getSegundoTram() != null) {
                            if (tiempo == 3 && tiempos.getSiguienteMinutos() >= mins && tiempos.getSegundoTram().getProximoMinutos() > mins) {
                                tiempo = 2;
                                cambioTiempo = true;
                            }

                            if (tiempo == 4 && tiempos.getSegundoTram().getProximoMinutos() >= mins && tiempos.getSegundoTram().getSiguienteMinutos() > mins) {
                                tiempo = 3;
                                cambioTiempo = true;
                            }

                        } else if (!DatosPantallaPrincipal.esTram(parada) && tiempos.getSegundoBus() != null) {
                            if (tiempo == 3 && tiempos.getSiguienteMinutos() >= mins && tiempos.getSegundoBus().getProximoMinutos() > mins) {
                                tiempo = 2;
                                cambioTiempo = true;
                            }

                            if (tiempo == 4 && tiempos.getSegundoBus().getProximoMinutos() >= mins && tiempos.getSegundoBus().getSiguienteMinutos() > mins) {
                                tiempo = 3;
                                cambioTiempo = true;
                            }

                        }

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

                        if (DatosPantallaPrincipal.esTram(parada) && tiempos.getSegundoTram() != null) {

                            if (tiempo == 3) {
                                milisegundosActuales = (actual.getTime() + (tiempos.getSegundoTram().getProximoMinutos() * 60000)) - (mins * 60000);
                            } else if (tiempo == 4) {
                                milisegundosActuales = (actual.getTime() + (tiempos.getSegundoTram().getSiguienteMinutos() * 60000)) - (mins * 60000);
                            }

                        } else if (!DatosPantallaPrincipal.esTram(parada) && tiempos.getSegundoBus() != null) {

                            if (tiempo == 3) {
                                milisegundosActuales = (actual.getTime() + (tiempos.getSegundoBus().getProximoMinutos() * 60000)) - (mins * 60000);
                            } else if (tiempo == 4) {
                                milisegundosActuales = (actual.getTime() + (tiempos.getSegundoBus().getSiguienteMinutos() * 60000)) - (mins * 60000);
                            }

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
                //linea;parada;hora;tiempo;item;milisegundos;destino

                // Control de disponibilidad de conexion
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    if (DatosPantallaPrincipal.esLineaTram(datos[0])) {

                        Log.d("TiemposService", "Recalculando para TRAM");

                        new LoadTiemposLineaParadaAsyncTask(loadTiemposLineaParadaAsyncTaskResponder).execute(datos[0], datos[1], datos[6]);
                    } else {
                        new LoadTiemposLineaParadaAsyncTask(loadTiemposLineaParadaAsyncTaskResponder).execute(datos[0], datos[1]);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
                }

            }

        } catch (Exception e) {

            e.printStackTrace();

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

        long mins = GestionarAlarmas.obtenerMinutos(item); // ((item + 1) * 5);

        // TRAM
        if (DatosPantallaPrincipal.esTram(parada)) {

            // Que tiempo usar
            // Si el primer bus no cumple, se usa el segundo
            if (theBus.getProximoMinutos() < mins) {

                if (theBus.getSiguienteMinutos() < mins && theBus.getSegundoTram() != null) {

                    BusLlegada theBus2 = theBus.getSegundoTram();

                    if (theBus.getProximoMinutos() < mins) {
                        et = theBus2.getSiguienteMinutos();
                        tiempo = 4;
                    } else {
                        et = theBus2.getProximoMinutos();
                        tiempo = 3;
                    }

                } else if (theBus.getSiguienteMinutos() < mins && theBus.getSegundoBus() != null) {

                    BusLlegada theBus2 = theBus.getSegundoBus();

                    if (theBus.getProximoMinutos() < mins) {
                        et = theBus2.getSiguienteMinutos();
                        tiempo = 4;
                    } else {
                        et = theBus2.getProximoMinutos();
                        tiempo = 3;
                    }

                } else {
                    et = theBus.getSiguienteMinutos();
                    tiempo = 2;
                }
            } else {
                et = theBus.getProximoMinutos();
                tiempo = 1;
            }

        } else {

            // Que tiempo usar
            // Si el primer bus no cumple, se usa el segundo
            if (theBus.getProximoMinutos() < mins) {
                et = theBus.getSiguienteMinutos();
                tiempo = 2;
            } else {
                et = theBus.getProximoMinutos();
                tiempo = 1;
            }

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

        String texto = "";
        if (DatosPantallaPrincipal.esTram(parada) || DatosPantallaPrincipal.esTramRt(parada)) {
            texto = context.getString(R.string.alarm_tram);
        } else {
            texto = context.getString(R.string.alarm_bus);
        }

        String txt = String.format(texto, "" + theBus.getLinea(), "" + parada);
        intent.putExtra("alarmTxt", txt);
        intent.putExtra("poste", parada);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmReceiver = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            alarmReceiver = PendingIntent.getBroadcast(context, 0, intent, 0);
        }

        Date actual = new Date();

        long milisegundos = (actual.getTime() + (et * 60000)) - (mins * 60000);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, milisegundos, alarmReceiver);

        SimpleDateFormat ft = new SimpleDateFormat("HH:mm", Locale.US);

        String horaT = ft.format(milisegundos);

        //String alertaDialog = theBus.getLinea() + ";" + parada + ";" + horaT + ";" + tiempo + ";" + item + ";" + milisegundos + ";" + theBus.getDestino();

        String alertaDialog = theBus.getLinea() + ";" + parada + ";" + horaT + " (" + mins + " " + context.getString(R.string.literal_min) + ")" + ";" + tiempo + ";" + item + ";" + milisegundos + ";" + theBus.getDestino();

        //linea;parada;hora;tiempo;item;milisegundos;destino

        Log.d("TiemposService", "Tiempo actualizado a: " + horaT);

        PreferencesUtil.putAlertaInfo(this, alertaDialog);

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

            PreferencesUtil.clearAlertaInfo(this);

            if (avisar) {
                Toast.makeText(this, getString(R.string.alarma_cancelada), Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Frecuencia configurable
     *
     * @return frecuencia
     */
    public long frecuenciaRecarga() {

        String preFrec = preferencias.getString("servicio_recarga", "60");

        long frecuencia = Long.parseLong(preFrec) * 1000;

        return frecuencia;

    }

}
