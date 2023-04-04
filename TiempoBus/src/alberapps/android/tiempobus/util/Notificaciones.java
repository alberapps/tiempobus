/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import alberapps.android.tiempobus.BuildConfig;
import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.barcodereader.BarcodeGraphic;
import alberapps.android.tiempobus.barcodereader.ui.camera.GraphicOverlay;
import alberapps.android.tiempobus.noticias.NoticiasTabsPager;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;

/**
 * Gestion de las notificaciones
 */
public class Notificaciones {

    /**
     * Base de datos
     */
    public static int NOTIFICACION_BASE_DATOS = 5;
    public static String NOTIFICACION_BD_INICIAL = "inicial";
    public static String NOTIFICACION_BD_FINAL = "final";
    public static String NOTIFICACION_BD_INCREMENTA = "incrementa";
    public static String NOTIFICACION_BD_ERROR = "error";

    /**
     * Noticias
     */
    public static int NOTIFICACION_NOTICIAS = 2;

    public static int NOTIFICACION_NOTICIAS_TRAM = 3;

    public static int NOTIFICACION_NOTICIAS_ALBERAPPS = 4;


    /**
     * Alarmas
     */
    public static int NOTIFICACION_ALARMAS = 1;

    public static int NOTIFICACION_ALARMA_DIARIA = 2;


    /**
     * Channel
     */
    public static String CHANNEL_DEFAULT = "default";
    public static String CHANNEL_ALERTABUS = "alertaBus";
    public static String CHANNEL_NOTICIAS = "noticias";
    public static String CHANNEL_LOW = "low";

    public static final int RC_HANDLE_NOTIFICATION_PERM = 2;

    /**
     * Servicio
     */
    public static int NOTIFICACION_SERVICIO_ALERTAS = 6;


    public static void initChannels(Context context) {

        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        //Default
        NotificationChannel channel = new NotificationChannel(CHANNEL_DEFAULT, context.getString(R.string.channel_default), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(context.getString(R.string.channel_default));


        // Led
        channel.enableLights(false);
        channel.enableVibration(false);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        notificationManager.createNotificationChannel(channel);

        //Low
        NotificationChannel channelLow = new NotificationChannel(CHANNEL_LOW, context.getString(R.string.channel_low), NotificationManager.IMPORTANCE_LOW);
        channelLow.setDescription(context.getString(R.string.channel_low));

        channelLow.enableLights(false);
        channelLow.enableVibration(false);
        channelLow.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        notificationManager.createNotificationChannel(channelLow);


        //Alertas Bus
        NotificationChannel channelAvisoBus = new NotificationChannel(CHANNEL_ALERTABUS, context.getString(R.string.notification_title), NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(context.getString(R.string.notification_title));

        // Sonido seleccionado
        String strRingtonePreference = preferencias.getString("alarma_tono", "DEFAULT_SOUND");

        if (!strRingtonePreference.equals("DEFAULT_SOUND")) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            channelAvisoBus.setSound(Uri.parse(strRingtonePreference), audioAttributes);

        }

        // Led
        channelAvisoBus.enableLights(true);

        // Usar o no la vibracion
        if (preferencias.getBoolean("alarma_vibrar", true)) {
            channelAvisoBus.enableVibration(true);
        }

        channelAvisoBus.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        notificationManager.createNotificationChannel(channelAvisoBus);


        //Noticias
        NotificationChannel channelNoticias = new NotificationChannel(CHANNEL_NOTICIAS, context.getString(R.string.tit_noticias), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(context.getString(R.string.tit_noticias));

        // Sonido seleccionado
        String strRingtonePreferenceNoticias = preferencias.getString("noticias_tono", "DEFAULT_SOUND");

        if (!strRingtonePreferenceNoticias.equals("DEFAULT_SOUND")) {

            AudioAttributes audioAttributesNoticias = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            channelNoticias.setSound(Uri.parse(strRingtonePreferenceNoticias), audioAttributesNoticias);

        }

        // Led
        channelNoticias.enableLights(true);

        // Usar o no la vibracion
        if (preferencias.getBoolean("noticias_vibrar", true)) {
            channelNoticias.enableVibration(true);
        }

        channelNoticias.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        notificationManager.createNotificationChannel(channelNoticias);

    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void requestNotificationPermission(AppCompatActivity activity) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            final String[] permissions = new String[]{ Manifest.permission.POST_NOTIFICATIONS };

            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.POST_NOTIFICATIONS)) {
                ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_NOTIFICATION_PERM);
                return;
            }

            Notificaciones.requestNotificationPermissionSnackbar(activity);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void requestNotificationPermissionSnackbar(AppCompatActivity activity) {

        final String[] permissions = new String[]{ Manifest.permission.POST_NOTIFICATIONS };
        final Activity thisActivity = activity;

        View.OnClickListener listener = view -> ActivityCompat.requestPermissions(thisActivity, permissions,
                RC_HANDLE_NOTIFICATION_PERM);

        activity.findViewById(R.id.drawer_layout).setOnClickListener(listener);
        View view = activity.findViewById(R.id.bottomNavigation);
        Snackbar.make(view, R.string.notification_perm,
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();

    }

    public static void requestNotificationPermissionToast(AppCompatActivity activity) {

        View view = activity.findViewById(R.id.bottomNavigation);
        Snackbar sb = Snackbar.make(view, R.string.notification_perm,
                Snackbar.LENGTH_INDEFINITE);

        View.OnClickListener listener = view2 -> sb.dismiss();
        activity.findViewById(R.id.drawer_layout).setOnClickListener(listener);
        sb.setAction(R.string.ok, listener).show();

    }

    public static NotificationCompat.Builder initBuilder(Context contexto, String channel) {

        NotificationCompat.Builder mBuilder = null;

        if (BuildConfig.FLAVOR.equals("legacy")) {
            mBuilder = new NotificationCompat.Builder(contexto);
        } else {
            mBuilder = new NotificationCompat.Builder(contexto, channel);
        }

        return mBuilder;

    }

    /**
     * Notificaciones de Base de Datos
     *
     * @param contexto
     * @param accion
     */

    @SuppressLint("MissingPermission")
    public static Builder notificacionBaseDatos(Context contexto, String accion, Builder mBuilderN, Integer incrementa) {

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(contexto);

        //if (mNotificationManager.areNotificationsEnabled()) {

            if (accion.equals(NOTIFICACION_BD_INICIAL)) {

                NotificationCompat.Builder mBuilder = initBuilder(contexto, CHANNEL_LOW);


                mBuilder.setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.recarga_bd))
                        .setContentText(contexto.getString(R.string.recarga_bd_desc))
                        .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap());

                mBuilder.setAutoCancel(false);

                // ticker
                CharSequence tickerText = contexto.getString(R.string.recarga_bd_desc);
                mBuilder.setTicker(tickerText);

                mBuilder.setProgress(100, 0, false);

                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(contexto, MainActivity.class);

                // The stack builder object will contain an artificial back stack
                // for
                // the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out
                // of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);

                PendingIntent resultPendingIntent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
                } else {
                    resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                mBuilder.setContentIntent(resultPendingIntent);

                if (mNotificationManager.areNotificationsEnabled()) {
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(NOTIFICACION_BASE_DATOS, mBuilder.build());
                }

                return mBuilder;

            } else if (accion.equals(NOTIFICACION_BD_INCREMENTA)) {

                mBuilderN.setProgress(100, incrementa, false);
                
                if (mNotificationManager.areNotificationsEnabled()) {
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(NOTIFICACION_BASE_DATOS, mBuilderN.build());
                }

                return mBuilderN;

            } else if (accion.equals(NOTIFICACION_BD_FINAL)) {
                mBuilderN.setContentText(contexto.getString(R.string.recarga_bd_desc_final));

                mBuilderN.setAutoCancel(true);

                // ticker
                CharSequence tickerText = contexto.getString(R.string.recarga_bd_desc_final);
                mBuilderN.setTicker(tickerText);

                mBuilderN.setProgress(0, 0, false);

                if (mNotificationManager.areNotificationsEnabled()) {
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(NOTIFICACION_BASE_DATOS, mBuilderN.build());
                }

                return mBuilderN;

            } else if (accion.equals(NOTIFICACION_BD_ERROR)) {
                mBuilderN.setContentText(contexto.getString(R.string.recarga_bd_desc_error));

                mBuilderN.setAutoCancel(true);

                // ticker
                CharSequence tickerText = contexto.getString(R.string.recarga_bd_desc_error);
                mBuilderN.setTicker(tickerText);

                mBuilderN.setProgress(0, 0, false);

                if (mNotificationManager.areNotificationsEnabled()) {
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(NOTIFICACION_BASE_DATOS, mBuilderN.build());
                }

                return mBuilderN;

            }

        //}
        return null;

    }

    /**
     * Notificacion nuevas noticias
     *
     * @param contexto
     */

    @SuppressLint("MissingPermission")
    public static void notificacionNoticias(Context contexto, String[] extendido, int nuevas) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = initBuilder(contexto, CHANNEL_NOTICIAS);


        mBuilder.setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.nuevas_noticias_bus))
                .setContentText(contexto.getString(R.string.nuevas_noticias_b)).setNumber(nuevas)
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mBuilder.setAutoCancel(true);


        if (Build.VERSION.SDK_INT < 26) {
            // Led
            int defaults = Notification.DEFAULT_LIGHTS;

            // Sonido seleccionado
            String strRingtonePreference = preferencias.getString("noticias_tono", "DEFAULT_SOUND");

            if (strRingtonePreference.equals("DEFAULT_SOUND")) {
                // Sonido por defecto
                defaults = defaults | Notification.DEFAULT_SOUND;

            } else {

                // Sonido seleccionado
                mBuilder.setSound(Uri.parse(strRingtonePreference));

            }

            // Usar o no la vibracion
            boolean controlVibrar = preferencias.getBoolean("noticias_vibrar", true);

            if (controlVibrar) {
                // Vibrate por defecto
                defaults = defaults | Notification.DEFAULT_VIBRATE;

            }

            // Opciones por defecto seleccionadas
            mBuilder.setDefaults(defaults);
        }

        // ticker
        CharSequence tickerText = contexto.getString(R.string.nuevas_noticias);
        mBuilder.setTicker(tickerText);

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        String[] events = extendido;
        // Sets a title for the Inbox style big view
        inboxStyle.bigText(extendido[0] + "\n" + extendido[1]);

        // Moves events into the big view
        // for (int i=0; i < events.length; i++) {

        // inboxStyle..addLine(events[i]);
        // }
        inboxStyle.setSummaryText(contexto.getString(R.string.app_name) + " (" + contexto.getString(R.string.tab_noticias) + ")");
        // Moves the big view style object into the notification object.
        mBuilder.setStyle(inboxStyle);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(contexto, NoticiasTabsPager.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NoticiasTabsPager.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(contexto);

        /*if (mNotificationManager.areNotificationsEnabled()) {
            mNotificationManager.notify(NOTIFICACION_NOTICIAS, mBuilder.build());
        }*/

        if (mNotificationManager.areNotificationsEnabled()) {
            mNotificationManager.notify(NOTIFICACION_NOTICIAS, mBuilder.build());
        }

    }

    /**
     * Notificacion nuevas noticias
     *
     * @param contexto
     */
    @SuppressLint("MissingPermission")
    public static void notificacionAvisosTram(Context contexto, String[] extendido) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = initBuilder(contexto, CHANNEL_NOTICIAS);


        mBuilder.setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.nuevas_noticias_tram))
                .setContentText(contexto.getString(R.string.nuevas_noticias_b))
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mBuilder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT < 26) {
            // Led
            int defaults = Notification.DEFAULT_LIGHTS;

            // Sonido seleccionado
            String strRingtonePreference = preferencias.getString("noticias_tono", "DEFAULT_SOUND");

            if (strRingtonePreference.equals("DEFAULT_SOUND")) {
                // Sonido por defecto
                defaults = defaults | Notification.DEFAULT_SOUND;

            } else {

                // Sonido seleccionado
                mBuilder.setSound(Uri.parse(strRingtonePreference));

            }

            // Usar o no la vibracion
            boolean controlVibrar = preferencias.getBoolean("noticias_vibrar", true);

            if (controlVibrar) {
                // Vibrate por defecto
                defaults = defaults | Notification.DEFAULT_VIBRATE;

            }

            // Opciones por defecto seleccionadas
            mBuilder.setDefaults(defaults);
        }

        // ticker
        CharSequence tickerText = contexto.getString(R.string.nuevas_noticias);
        mBuilder.setTicker(tickerText);

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        String[] events = extendido;
        // Sets a title for the Inbox style big view
        inboxStyle.bigText(extendido[0] + "\n" + extendido[1]);

        // Moves events into the big view
        // for (int i=0; i < events.length; i++) {

        // inboxStyle..addLine(events[i]);
        // }
        inboxStyle.setSummaryText(contexto.getString(R.string.app_name) + " (" + contexto.getString(R.string.nuevas_tram) + ")");
        // Moves the big view style object into the notification object.
        mBuilder.setStyle(inboxStyle);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(contexto, NoticiasTabsPager.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NoticiasTabsPager.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(contexto);

        if (mNotificationManager.areNotificationsEnabled()) {
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICACION_NOTICIAS_TRAM, mBuilder.build());
        }

    }

    /**
     * Notificacion nuevas noticias ALBERAPPS
     *
     * @param contexto
     */
    @SuppressLint("MissingPermission")
    public static void notificacionAvisosAlberApps(Context contexto, String[] extendido) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = initBuilder(contexto, CHANNEL_NOTICIAS);


        mBuilder.setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.nuevas_noticias_alberapps))
                .setContentText(contexto.getString(R.string.nuevas_noticias_b))
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mBuilder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT < 26) {
            // Led
            int defaults = Notification.DEFAULT_LIGHTS;

            // Sonido seleccionado
            String strRingtonePreference = preferencias.getString("noticias_tono", "DEFAULT_SOUND");

            if (strRingtonePreference.equals("DEFAULT_SOUND")) {
                // Sonido por defecto
                defaults = defaults | Notification.DEFAULT_SOUND;

            } else {

                // Sonido seleccionado
                mBuilder.setSound(Uri.parse(strRingtonePreference));

            }

            // Usar o no la vibracion
            boolean controlVibrar = preferencias.getBoolean("noticias_vibrar", true);

            if (controlVibrar) {
                // Vibrate por defecto
                defaults = defaults | Notification.DEFAULT_VIBRATE;

            }

            // Opciones por defecto seleccionadas
            mBuilder.setDefaults(defaults);
        }

        // ticker
        CharSequence tickerText = contexto.getString(R.string.nuevas_noticias);
        mBuilder.setTicker(tickerText);

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        String[] events = extendido;
        // Sets a title for the Inbox style big view
        inboxStyle.bigText(extendido[0] + "\n" + extendido[1]);

        // Moves events into the big view
        // for (int i=0; i < events.length; i++) {

        // inboxStyle..addLine(events[i]);
        // }
        inboxStyle.setSummaryText(contexto.getString(R.string.app_name) + " (" + contexto.getString(R.string.alberapps) + ")");
        // Moves the big view style object into the notification object.
        mBuilder.setStyle(inboxStyle);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(contexto, NoticiasTabsPager.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NoticiasTabsPager.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(contexto);

        if (mNotificationManager.areNotificationsEnabled()) {
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICACION_NOTICIAS_ALBERAPPS, mBuilder.build());
        } 

    }


    /**
     * Notificacion alarma
     *
     * @param contexto
     */
    @SuppressLint("MissingPermission")
    public static void notificacionAlarma(Context contexto, CharSequence aviso, int parada) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = initBuilder(contexto, CHANNEL_ALERTABUS);

        String texto = "";
        if (DatosPantallaPrincipal.esTram(Integer.toString(parada)) || DatosPantallaPrincipal.esTramRt(Integer.toString(parada))) {
            texto = contexto.getString(R.string.notification_title_tram);
        } else {
            texto = contexto.getString(R.string.notification_title);
        }


        mBuilder.setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(texto).setContentText(aviso)
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if (Build.VERSION.SDK_INT < 26) {
            // Led
            int defaults = Notification.DEFAULT_LIGHTS;

            // Sonido seleccionado
            String strRingtonePreference = preferencias.getString("alarma_tono", "DEFAULT_SOUND");

            if (strRingtonePreference.equals("DEFAULT_SOUND")) {
                // Sonido por defecto
                defaults = defaults | Notification.DEFAULT_SOUND;

            } else {

                // Sonido seleccionado
                mBuilder.setSound(Uri.parse(strRingtonePreference));

            }

            // Usar o no la vibracion
            boolean controlVibrar = preferencias.getBoolean("alarma_vibrar", true);

            if (controlVibrar) {

                // Vibrate por defecto
                defaults = defaults | Notification.DEFAULT_VIBRATE;

            }

            // Opciones por defecto seleccionadas
            mBuilder.setDefaults(defaults);
        }

        mBuilder.setAutoCancel(true);

        // ticker

        mBuilder.setTicker(aviso);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(contexto, MainActivity.class);

        resultIntent.putExtra("poste", parada);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(contexto);

        if (mNotificationManager.areNotificationsEnabled()) {
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICACION_ALARMAS, mBuilder.build());
        }

    }


    /**
     * Notificacion servicio alertas
     *
     * @param contexto
     */
    public static Notification notificacionServicioAlerta(Context contexto, CharSequence aviso) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = initBuilder(contexto, CHANNEL_LOW);

        String texto = contexto.getString(R.string.foreground_service);


        mBuilder.setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(texto).setContentText(aviso)
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);


        // ticker
        mBuilder.setTicker(texto);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(contexto, MainActivity.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(contexto);

        if (mNotificationManager.areNotificationsEnabled()) {
            // mId allows you to update the notification later on.
            //mNotificationManager.notify(NOTIFICACION_SERVICIO_ALERTAS, mBuilder.build());
        }

        return mBuilder.build();

    }


    /**
     * Notificacion alarma diaria
     *
     * @param contexto
     */
    @SuppressLint("MissingPermission")
    public static void notificacionAlarmaDiaria(Context contexto, List<String> extendido) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = initBuilder(contexto, CHANNEL_DEFAULT);


        mBuilder.setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.share_3))
                .setContentText(contexto.getString(R.string.nuevas_noticias_b))
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mBuilder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT < 26) {
            // Led
            int defaults = Notification.DEFAULT_LIGHTS;

            // Sonido seleccionado
            String strRingtonePreference = preferencias.getString("noticias_tono", "DEFAULT_SOUND");

            if (strRingtonePreference.equals("DEFAULT_SOUND")) {
                // Sonido por defecto
                defaults = defaults | Notification.DEFAULT_SOUND;

            } else {

                // Sonido seleccionado
                mBuilder.setSound(Uri.parse(strRingtonePreference));

            }

            // Usar o no la vibracion
            boolean controlVibrar = preferencias.getBoolean("noticias_vibrar", true);

            if (controlVibrar) {
                // Vibrate por defecto
                defaults = defaults | Notification.DEFAULT_VIBRATE;

            }

            // Opciones por defecto seleccionadas
            mBuilder.setDefaults(defaults);
        }

        // ticker
        CharSequence tickerText = contexto.getString(R.string.nuevas_noticias);
        mBuilder.setTicker(tickerText);

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();

        // Sets a title for the Inbox style big view
        String text = "";
        for (int i = 0; i < extendido.size(); i++) {
            if (!text.equals("")) {
                text = text + "\n";
            }
            text = text + extendido.get(i);
        }
        inboxStyle.bigText(text);

        // Moves events into the big view
        inboxStyle.setSummaryText(contexto.getString(R.string.app_name));
        // Moves the big view style object into the notification object.
        mBuilder.setStyle(inboxStyle);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(contexto, NoticiasTabsPager.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NoticiasTabsPager.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(contexto);

        if (mNotificationManager.areNotificationsEnabled()) {
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICACION_ALARMA_DIARIA, mBuilder.build());
        }

    }

    /**
     * Notificacion nuevas noticias
     *
     * @param contexto
     */
    @SuppressLint("MissingPermission")
    public static void notificacionAvisosMensajesTiempoBus(Context contexto, String titulo, String contenido, String extendido) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = initBuilder(contexto, CHANNEL_DEFAULT);


        mBuilder.setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.avisos_push_tiempobus))
                .setContentText(contenido)
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mBuilder.setAutoCancel(true);

        /*
        // Led
        int defaults = Notification.DEFAULT_LIGHTS;

        // Sonido seleccionado
        String strRingtonePreference = preferencias.getString("noticias_tono", "DEFAULT_SOUND");

        if (strRingtonePreference.equals("DEFAULT_SOUND")) {
            // Sonido por defecto
            defaults = defaults | Notification.DEFAULT_SOUND;

        } else {

            // Sonido seleccionado
            mBuilder.setSound(Uri.parse(strRingtonePreference));

        }

        // Usar o no la vibracion
        boolean controlVibrar = preferencias.getBoolean("noticias_vibrar", true);

        if (controlVibrar) {
            // Vibrate por defecto
            defaults = defaults | Notification.DEFAULT_VIBRATE;

        }


        // Opciones por defecto seleccionadas
        mBuilder.setDefaults(defaults);
*/

        // ticker
        CharSequence tickerText = contexto.getString(R.string.avisos_push_tiempobus);
        mBuilder.setTicker(tickerText);

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();

        // Sets a title for the Inbox style big view
        inboxStyle.bigText(extendido);

        // Moves events into the big view
        // for (int i=0; i < events.length; i++) {

        // inboxStyle..addLine(events[i]);
        // }
        inboxStyle.setSummaryText(contexto.getString(R.string.app_name));
        // Moves the big view style object into the notification object.
        mBuilder.setStyle(inboxStyle);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(contexto, NoticiasTabsPager.class);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(contexto);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(NoticiasTabsPager.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(contexto);

        if (mNotificationManager.areNotificationsEnabled()) {
            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICACION_NOTICIAS_TRAM, mBuilder.build());
        }

    }


}
