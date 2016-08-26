package alberapps.android.tiempobus.alarma;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by albert on 20/08/16.
 */
public class AlarmaDiaria {


    /**
     * Cotexto principal
     */
    private Context context;

    private SharedPreferences preferencias;

    AlarmManager alarmManager;

    public AlarmaDiaria(Context contexto) {

        context = contexto;

        //preferencias = preferencia;

        //alarmManager = alarmMa;

    }


    public void establecerAlarma() {

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmaDiariaReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.cancel(alarmIntent);
        //alarmIntent.cancel();


        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 29);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 1, alarmIntent);

        Toast.makeText(context, "Prueba Establecida 4", Toast.LENGTH_LONG).show();



    }

}
