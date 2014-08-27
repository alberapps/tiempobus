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
package alberapps.android.tiempobus.alarma;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.service.TiemposForegroundService;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.java.tam.BusLlegada;

/**
 * Gestion de alarmas
 */
public class GestionarAlarmas {

    /**
     * Cotexto principal
     */
    private MainActivity context;

    private SharedPreferences preferencias;

    AlarmManager alarmManager;

    public GestionarAlarmas(MainActivity contexto, SharedPreferences preferencia, AlarmManager alarmMa) {

        context = contexto;

        preferencias = preferencia;

        alarmManager = alarmMa;

    }

    /**
     * Cancelar alarmas establecidas
     */
    public void cancelarAlarmas(boolean avisar) {

        // Cancelar posible notificacion
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
        mNotificationManager.cancel(AlarmReceiver.ALARM_ID);

        // Iniciar receiver
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent alarmReceiver = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Cancelar alarma si hay una definida
        if (alarmReceiver != null) {

            alarmManager.cancel(alarmReceiver);

            alarmReceiver.cancel();

            alarmReceiver = null;

            PreferencesUtil.clearAlertaInfo(context);

            if (avisar) {
                Toast.makeText(context, context.getString(R.string.alarma_cancelada), Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Calcular y activar la alarma
     *
     * @param theBus
     * @param tiempo
     * @param item
     * @param paradaActual
     */
    public boolean calcularAlarma(BusLlegada theBus, int tiempo, int item, int paradaActual) {

        long et;

        // Obtener los minutos de la seleccion
        long mins = obtenerMinutos(item);// ((item + 1) * 5);

        Log.d("ALARMA", "minutos: " + mins + " item: " + item);


        // TRAM
        if (DatosPantallaPrincipal.esTram(Integer.toString(paradaActual))) {

            // Que tiempo usar
            // Si el primer bus no cumple, se usa el segundo
            if (theBus.getProximoMinutos() < mins) {

                //Controlar si hay tiempos combinados
                if (theBus.getSiguienteMinutos() < mins && theBus.getSegundoTram() != null) {

                    BusLlegada theBus2 = theBus.getSegundoTram();

                    if (theBus2.getProximoMinutos() < mins) {
                        et = theBus2.getSiguienteMinutos();
                        tiempo = 4;
                    } else {
                        et = theBus2.getProximoMinutos();
                        tiempo = 3;
                    }

                } else if (theBus.getSiguienteMinutos() < mins && theBus.getSegundoBus() != null) {

                    BusLlegada theBus2 = theBus.getSegundoBus();

                    if (theBus2.getProximoMinutos() < mins) {
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

        // }

        // Control de tiempo insuficiente o excesivo
        if (et < mins) {
            Toast.makeText(context, String.format(context.getString(R.string.err_bus_cerca), et), Toast.LENGTH_SHORT).show();
            return false;
        } else if (et == 9999) {
            Toast.makeText(context, String.format(context.getString(R.string.err_bus_sin), et), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Establecer la alarma
        establecerAlarma(et, mins, theBus, tiempo, item, paradaActual);

        return true;
    }

    /**
     * @param et
     * @param mins
     * @param theBus
     * @param tiempo
     * @param item
     * @param paradaActual
     */
    public void establecerAlarma(long et, long mins, BusLlegada theBus, int tiempo, int item, int paradaActual) {

        PendingIntent alarmReceiver;

        Date actual = new Date();

        long milisegundos = (actual.getTime() + (et * 60000)) - (mins * 60000);

        // Iniciar receiver
        Intent intent = new Intent(context, AlarmReceiver.class);

        String texto = "";
        if (DatosPantallaPrincipal.esTram(Integer.toString(paradaActual))) {
            texto = context.getString(R.string.alarm_tram);
        } else {
            texto = context.getString(R.string.alarm_bus);
        }

        String txt = String.format(texto, "" + theBus.getLinea(), "" + paradaActual);
        intent.putExtra("alarmTxt", txt);
        intent.putExtra("poste", paradaActual);

        alarmReceiver = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, milisegundos, alarmReceiver);

        SimpleDateFormat ft = new SimpleDateFormat("HH:mm", Locale.US);

        String horaT = ft.format(milisegundos);

        String alertaDialog = theBus.getLinea() + ";" + paradaActual + ";" + horaT + " (" + mins + " " + context.getString(R.string.literal_min) + ")" + ";" + tiempo + ";" + item + ";" + milisegundos + ";" + theBus.getDestino();

        PreferencesUtil.putAlertaInfo(context, alertaDialog);

    }

    /**
     * @param item
     * @return minutos
     */
    public static long obtenerMinutos(int item) {

        long mins = 0;

        switch (item) {

            case 0:

                // 3 min

                mins = 3;

                break;

            case 1:

                // 5 min

                mins = 5;

                break;

            case 2:

                // 10 min

                mins = 10;

                break;

            case 3:

                // 15 min

                mins = 15;

                break;

            case 4:

                // 20min

                mins = 20;

                break;

        }
        ;

        return mins;

    }

    /**
     * @param theBus
     * @param paradaActual
     * @return
     */
    public String prepararReceiver(BusLlegada theBus, int paradaActual) {

        String texto = "";
        if (DatosPantallaPrincipal.esTram(Integer.toString(paradaActual))) {
            texto = context.getString(R.string.alarm_tram);
        } else {
            texto = context.getString(R.string.alarm_bus);
        }

        String txt = String.format(texto, "" + theBus.getLinea(), "" + paradaActual);

        return txt;

    }

    /**
     * Nuevo selector de tiempos
     *
     * @param bus
     */
    public void mostrarModalTiemposAlerta(BusLlegada bus, final int paradaActual, final String textoReceiver) {

        final BusLlegada theBus = bus;

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(context.getString(R.string.tit_choose_alarm));

        LayoutInflater li = context.getLayoutInflater();
        View vista = li.inflate(R.layout.seleccionar_tiempo, null, false);

        final Spinner spinner = (Spinner) vista.findViewById(R.id.spinner_tiempos);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.spinner_minutos, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        // Por defecto 5
        spinner.setSelection(1);

        dialog.setView(vista);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                // Anular si existe una alarma anterior
                cancelarAlarmas(false);

                int seleccion = spinner.getSelectedItemPosition();

                boolean correcto = calcularAlarma(theBus, 1, seleccion, paradaActual);

                // Si se ha podido establecer
                if (correcto) {

                    Intent intent = new Intent(TiemposForegroundService.ACTION_FOREGROUND);
                    intent.setClass(context, TiemposForegroundService.class);
                    intent.putExtra("PARADA", paradaActual);

                    boolean checkActivo = preferencias.getBoolean("activarServicio", false);

                    if (checkActivo) {
                        context.startService(intent);
                    }

                    dialog.dismiss();

                    mostrarModalAlertas(paradaActual);

                } else {
                    dialog.dismiss();
                }

            }

        });

        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }

        });

        dialog.show();

    }

    /**
     * Modal con informacion de la alarma activa
     */
    public void mostrarModalAlertas(int paradaActual) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(context.getString(R.string.alarma_modal));

        String aviso = PreferencesUtil.getAlertaInfo(context);

        if (aviso != null && !aviso.equals("")) {

            String[] datos = aviso.split(";");

            String alertaDialog = context.getString(R.string.alarma_establecida_linea) + ": " + datos[0] + "\n" + context.getString(R.string.alarma_establecida_parada) + ": " + datos[1] + "\n"
                    + context.getString(R.string.alarma_establecida_hora) + ": " + datos[2] + "\n" + context.getString(R.string.alarma_que_tiempo) + ": " + datos[3] + "\n" + "\n"
                    + (context.getString(R.string.alarma_auto_aviso, new Object[]{preferencias.getString("servicio_recarga", "60")}));


            dialog.setIcon(R.drawable.ic_alarm_modal);

            LayoutInflater li = context.getLayoutInflater();
            View vista = li.inflate(R.layout.alertas_info, null, false);

            TextView texto = (TextView) vista.findViewById(R.id.textAlerta);

            texto.setText(alertaDialog);

            dialog.setView(vista);

            CheckBox check = (CheckBox) vista.findViewById(R.id.checkBoxAlerta);


            boolean checkActivo = preferencias.getBoolean("activarServicio", false);
            check.setChecked(checkActivo);

            dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }

            });

            dialog.setNegativeButton(R.string.menu_cancelar_alarma, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {

                    Intent intent = new Intent(TiemposForegroundService.ACTION_FOREGROUND);
                    intent.setClass(context, TiemposForegroundService.class);

                    context.stopService(intent);

                    cancelarAlarmas(true);
                }

            });

            dialog.show();

        } else {

            Toast.makeText(context, context.getString(R.string.alarma_activa_no), Toast.LENGTH_SHORT).show();

        }

    }

}
