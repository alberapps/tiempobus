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

		Log.d("", "Notificar alarma");

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		PendingIntent alarmReceiver = PendingIntent.getBroadcast(context, 0, intent, 0);

		alarmManager.cancel(alarmReceiver);

		alarmReceiver.cancel();

		PreferencesUtil.clearAlertaInfo(context);

		Log.d("", "Alarma finalizada1");

		// Parar el servic
		context.stopService(new Intent(context, TiemposForegroundService.class));

		Log.d("", "Alarma finalizada2");

		Notificaciones.notificacionAlarma(context, tickerText, parada);

	}
}