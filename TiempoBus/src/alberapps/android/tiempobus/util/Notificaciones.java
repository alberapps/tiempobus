/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
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
package alberapps.android.tiempobus.util;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.noticias.NoticiasTabsPager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;

/**
 * Gestion de las notificaciones
 * 
 * 
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

	/**
	 * Alarmas
	 */
	public static int NOTIFICACION_ALARMAS = 1;

	/**
	 * Notificaciones de Base de Datos
	 * 
	 * @param contexto
	 * @param accion
	 */
	public static Builder notificacionBaseDatos(Context contexto, String accion, Builder mBuilderN, Integer incrementa) {

		NotificationManager mNotificationManager = (NotificationManager) contexto
				.getSystemService(Context.NOTIFICATION_SERVICE);

		if (accion.equals(NOTIFICACION_BD_INICIAL)) {

			NotificationCompat.Builder mBuilder = null;

			mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_3)
					.setContentTitle(contexto.getString(R.string.recarga_bd))
					.setContentText(contexto.getString(R.string.recarga_bd_desc));

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

		}

		else if (accion.equals(NOTIFICACION_BD_FINAL)) {
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
	public static void notificacionNoticias(Context contexto) {

		PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
		SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

		NotificationCompat.Builder mBuilder = null;

		mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_3)
				.setContentTitle(contexto.getString(R.string.nuevas_noticias))
				.setContentText(contexto.getString(R.string.nuevas_noticias_b));

		mBuilder.setAutoCancel(true);

		// Led
		int defaults = Notification.DEFAULT_LIGHTS;

		// Sonido seleccionado
		String strRingtonePreference = preferencias.getString("noticias_tono", "DEFAULT_SOUND");

		if (strRingtonePreference == "DEFAULT_SOUND") {
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

		NotificationManager mNotificationManager = (NotificationManager) contexto
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICACION_NOTICIAS, mBuilder.build());

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

		mBuilder = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_tiempobus_3)
				.setContentTitle(contexto.getString(R.string.notification_title)).setContentText(aviso);

		// Led
		int defaults = Notification.DEFAULT_LIGHTS;

		// Sonido seleccionado
		String strRingtonePreference = preferencias.getString("alarma_tono", "DEFAULT_SOUND");

		if (strRingtonePreference == "DEFAULT_SOUND") {
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

		NotificationManager mNotificationManager = (NotificationManager) contexto
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICACION_ALARMAS, mBuilder.build());

	}

}
