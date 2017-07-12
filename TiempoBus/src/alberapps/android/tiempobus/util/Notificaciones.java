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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.res.ResourcesCompat;

import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
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
     * Servicio
     */
    public static int NOTIFICACION_SERVICIO_ALERTAS = 6;

    /**
     * Notificaciones de Base de Datos
     *
     * @param contexto
     * @param accion
     */
    public static Builder notificacionBaseDatos(Context contexto, String accion, Builder mBuilderN, Integer incrementa) {

        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);

        if (accion.equals(NOTIFICACION_BD_INICIAL)) {

            NotificationCompat.Builder mBuilder = null;

            mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.recarga_bd))
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

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICACION_BASE_DATOS, mBuilder.build());

            return mBuilder;

        } else if (accion.equals(NOTIFICACION_BD_INCREMENTA)) {

            mBuilderN.setProgress(100, incrementa, false);

            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICACION_BASE_DATOS, mBuilderN.build());

            return mBuilderN;

        } else if (accion.equals(NOTIFICACION_BD_FINAL)) {
            mBuilderN.setContentText(contexto.getString(R.string.recarga_bd_desc_final));

            mBuilderN.setAutoCancel(true);

            // ticker
            CharSequence tickerText = contexto.getString(R.string.recarga_bd_desc_final);
            mBuilderN.setTicker(tickerText);

            mBuilderN.setProgress(0, 0, false);

            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICACION_BASE_DATOS, mBuilderN.build());

            return mBuilderN;

        } else if (accion.equals(NOTIFICACION_BD_ERROR)) {
            mBuilderN.setContentText(contexto.getString(R.string.recarga_bd_desc_error));

            mBuilderN.setAutoCancel(true);

            // ticker
            CharSequence tickerText = contexto.getString(R.string.recarga_bd_desc_error);
            mBuilderN.setTicker(tickerText);

            mBuilderN.setProgress(0, 0, false);

            // mId allows you to update the notification later on.
            mNotificationManager.notify(NOTIFICACION_BASE_DATOS, mBuilderN.build());

            return mBuilderN;

        }

        return null;

    }

    /**
     * Notificacion nuevas noticias
     *
     * @param contexto
     */
    public static void notificacionNoticias(Context contexto, String[] extendido, int nuevas) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = null;

        mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.nuevas_noticias_bus))
                .setContentText(contexto.getString(R.string.nuevas_noticias_b)).setNumber(nuevas)
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mBuilder.setAutoCancel(true);

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

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICACION_NOTICIAS, mBuilder.build());

    }

    /**
     * Notificacion nuevas noticias
     *
     * @param contexto
     */
    public static void notificacionAvisosTram(Context contexto, String[] extendido) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = null;

        mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.nuevas_noticias_tram))
                .setContentText(contexto.getString(R.string.nuevas_noticias_b))
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mBuilder.setAutoCancel(true);

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

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICACION_NOTICIAS_TRAM, mBuilder.build());

    }

    /**
     * Notificacion nuevas noticias ALBERAPPS
     *
     * @param contexto
     */
    public static void notificacionAvisosAlberApps(Context contexto, String[] extendido) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = null;

        mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.nuevas_noticias_alberapps))
                .setContentText(contexto.getString(R.string.nuevas_noticias_b))
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mBuilder.setAutoCancel(true);

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

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICACION_NOTICIAS_ALBERAPPS, mBuilder.build());

    }


    /**
     * Notificacion alarma
     *
     * @param contexto
     */
    public static void notificacionAlarma(Context contexto, CharSequence aviso, int parada) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = null;

        String texto = "";
        if (DatosPantallaPrincipal.esTram(Integer.toString(parada))) {
            texto = contexto.getString(R.string.notification_title_tram);
        } else {
            texto = contexto.getString(R.string.notification_title);
        }

        mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(texto).setContentText(aviso)
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

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

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICACION_ALARMAS, mBuilder.build());

    }


    /**
     * Notificacion servicio alertas
     *
     * @param contexto
     */
    public static Notification notificacionServicioAlerta(Context contexto, CharSequence aviso) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = null;

        String texto = contexto.getString(R.string.foreground_service);


        mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(texto).setContentText(aviso)
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

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        //mNotificationManager.notify(NOTIFICACION_SERVICIO_ALERTAS, mBuilder.build());

        return mBuilder.build();

    }


    /**
     * Notificacion alarma diaria
     *
     * @param contexto
     */
    public static void notificacionAlarmaDiaria(Context contexto, List<String> extendido) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = null;

        mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.share_3))
                .setContentText(contexto.getString(R.string.nuevas_noticias_b))
                .setLargeIcon(((BitmapDrawable) ResourcesCompat.getDrawable(contexto.getResources(), R.drawable.ic_tiempobus_5, null)).getBitmap())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mBuilder.setAutoCancel(true);

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

        // ticker
        CharSequence tickerText = contexto.getString(R.string.nuevas_noticias);
        mBuilder.setTicker(tickerText);

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();

        // Sets a title for the Inbox style big view
        String text = "";
        for(int i = 0; i< extendido.size(); i++) {
            if(!text.equals("")){
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

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICACION_ALARMA_DIARIA, mBuilder.build());

    }

    /**
     * Notificacion nuevas noticias
     *
     * @param contexto
     */
    public static void notificacionAvisosMensajesTiempoBus(Context contexto, String titulo, String contenido, String extendido) {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        NotificationCompat.Builder mBuilder = null;

        mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_4).setContentTitle(contexto.getString(R.string.avisos_push_tiempobus))
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

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICACION_NOTICIAS_TRAM, mBuilder.build());

    }


}
