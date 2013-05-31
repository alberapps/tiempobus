/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
 *  based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.android.tiempobus.alarma;

import alberapps.android.tiempobus.service.TiemposForegroundService;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.android.tiempobus.util.PreferencesUtil;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Receiver que escucha los avisos y muestra el mensaje en la barra de estatus
 * 
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {
	public static final int ALARM_ID = 1;
	
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		CharSequence tickerText = extras.getCharSequence("alarmTxt");
		int parada = extras.getInt("poste");
		
		//SharedPreferences preferencias =  PreferenceManager.getDefaultSharedPreferences(context);
		
		//SharedPreferences preferenciasAlertas = context.getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);
		
		/*
		// Get a reference to the notification manager
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

		// Instantiate the Notification
		int icon = R.drawable.ic_stat_tiempobus_notif;
		CharSequence tickerText = extras.getCharSequence("alarmTxt");
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		// Define the Notification's expanded message and Intent:
		CharSequence contentTitle = context.getString(R.string.notification_title);
		CharSequence contentText = "" + extras.getString("alarmTxt");
		
		
		//Sonido seleccionado
		String strRingtonePreference = preferencias.getString("alarma_tono", "DEFAULT_SOUND");        
		
		Uri alertSound = null;
		
		if(strRingtonePreference == "DEFAULT_SOUND"){
			alertSound = RingtoneManager.getDefaultUri( Notification.DEFAULT_SOUND );
		}else{
			alertSound = Uri.parse(strRingtonePreference);
		}		
		
				
		// play a sound
		//Uri alertSound = RingtoneManager.getDefaultUri( Notification.DEFAULT_SOUND );
		if(alertSound!=null) {
			notification.sound = alertSound;
		}
		
		//Usar o no la vibracion
		boolean controlVibrar = preferencias.getBoolean("alarma_vibrar", true);
		
		if(controlVibrar){
			// vibrate
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.vibrate = new long[] {0,100,200,300};
		}
		
		
		// leds
		notification.ledARGB = Color.YELLOW;
		notification.ledOnMS = 300;
		notification.ledOffMS = 1000;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;	

		// the asociated item
		Intent notificationIntent = new Intent(context, MainActivity.class);		
				
		notificationIntent.putExtra("poste", extras.getInt("poste"));
		 
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Rock&Roll
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		
		mNotificationManager.notify(ALARM_ID, notification);
		
		*/
		
		Log.d("", "Notificar alarma");
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		PendingIntent alarmReceiver = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		alarmManager.cancel(alarmReceiver);
		
		alarmReceiver.cancel();
		
		
		
		
		//Quitar info de la alarma
	    //SharedPreferences.Editor editor = preferenciasAlertas.edit();
		//editor.putString("alerta", "");
		//editor.commit();
		
		PreferencesUtil.clearAlertaInfo(context);
		
		Log.d("", "Alarma finalizada1");
		
		//Parar el servic
		context.stopService(new Intent(context,
                TiemposForegroundService.class));
		
		Log.d("", "Alarma finalizada2");
		
		Notificaciones.notificacionAlarma(context, tickerText, parada);
		
		
		
	}
}