/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.android.tiempobus.alarma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.tasks.LoadTiemposAsyncTask;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.DatosRespuesta;

/**
 * Receiver que escucha los avisos y muestra el mensaje en la barra de estatus
 */
public class AlarmaDiariaReceiver extends BroadcastReceiver {
    public static final int ALARM_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
       /* Bundle extras = intent.getExtras();

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

        Notificaciones.notificacionAlarma(context, tickerText, parada);*/

        Toast.makeText(context, "Prueba", Toast.LENGTH_LONG).show();


        //String[] listaTextos = {"prueba1", "prueba2"};

        //Notificaciones.notificacionAlarmaDiaria(context, listaTextos);

        cargarTiempos(context);

    }


    /**
     * Recalcular alarma
     */
    public static void cargarTiempos(final Context context) {


        try {

            //String aviso = PreferencesUtil.getAlertaInfo(this);

            int parada = 2503;
            String linea = "24";

            /**
             * Sera llamado cuando la tarea de cargar tiempos termine
             */
            LoadTiemposAsyncTask.LoadTiemposAsyncTaskResponder loadTiemposAsyncTaskResponder = new LoadTiemposAsyncTask.LoadTiemposAsyncTaskResponder() {
                public void tiemposLoaded(DatosRespuesta datosRespuesta) {

                    try {

                        List<String> infoList = new ArrayList<String>();

                        ArrayList<BusLlegada> tiempos = null;

                        if (datosRespuesta != null) {
                            tiempos = datosRespuesta.getListaBusLlegada();

                            //Reordenar en funcion de fijado
                            //tiempos = datosPantallaPrincipal.ordenarTiemposPorTarjetaFija(tiempos);

                        }

                        if (tiempos != null) {

                            for (int i = 0; i < tiempos.size(); i++) {

                                infoList.add(tiempos.get(i).getLinea() + " (" + tiempos.get(i).getDestino() + ") : " +
                                        tiempos.get(i).getProximoMinutos() + context.getString(R.string.literal_min) +
                                        " " +
                                        tiempos.get(i).getSiguienteMinutos() + context.getString(R.string.literal_min));

                            }

                            Notificaciones.notificacionAlarmaDiaria(context, infoList);


                        } else {
                            // Error al recuperar datos
                            //showProgressBar(false);

                            //errorTiempos();

                        }

                        if (datosRespuesta != null && datosRespuesta.getError() != null && datosRespuesta.getError().equals(TiempoBusException.ERROR_STATUS_SERVICIO)) {

                            //Toast.makeText(getApplicationContext(), getString(R.string.error_status), Toast.LENGTH_SHORT).show();

                        }


                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();

                    }


                }
            };

            PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
            SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);

            String paradaDestinoTram = preferencias.getString("parada_destino_tram", "");

            // Control de disponibilidad de conexion
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new LoadTiemposAsyncTask(loadTiemposAsyncTaskResponder).execute(parada, context.getApplicationContext(), false, paradaDestinoTram);
            } else {
                //Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();

            }


        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(context.getApplicationContext(), context.getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();

        }

    }


}