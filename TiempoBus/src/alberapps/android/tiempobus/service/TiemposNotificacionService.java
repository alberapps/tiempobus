/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 *
 *  based on code by The Android Open Source Project
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.service;

import android.app.Notification;
import android.app.NotificationManager;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import alberapps.android.tiempobus.R;
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
public class TiemposNotificacionService extends Service {
    public static final String ACTION_FOREGROUND = "alberapps.android.tiempobus.service.notifica.FOREGROUND";
    public static final String ACTION_BACKGROUND = "alberapps.android.tiempobus.service.notifica.BACKGROUND";

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

            recargarDatos();
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
    public void recargarDatos() {

        try {

            String aviso = PreferencesUtil.getAlertaInfo(this);

            /**
             * Sera llamado cuando la tarea de cargar tiempos termine
             */
            LoadTiemposLineaParadaAsyncTaskResponder loadTiemposLineaParadaAsyncTaskResponder = new LoadTiemposLineaParadaAsyncTaskResponder() {
                public void tiemposLoaded(BusLlegada tiempos) {


                }

            };


            // Control de disponibilidad de conexion
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {


                //new LoadTiemposLineaParadaAsyncTask(loadTiemposLineaParadaAsyncTaskResponder).execute(datos[0], datos[1], datos[6]);

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
            }


        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();

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
