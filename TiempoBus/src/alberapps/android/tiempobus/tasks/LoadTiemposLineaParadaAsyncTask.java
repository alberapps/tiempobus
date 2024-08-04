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
package alberapps.android.tiempobus.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.ProcesarTiemposService;
import alberapps.java.tram.ProcesarTiemposTramIsaeService;
import alberapps.java.tram.linea9.ProcesarTiemposTramL9Texto;

/**
 * Tarea asincrona que se encarga de consultar los tiempos para una linea y
 * parada
 */
public class LoadTiemposLineaParadaAsyncTask extends AsyncTask<Object, Void, BusLlegada> {

    /**
     * Interfaz que deberian implementar las clases que la quieran usar Sirve
     * como callback una vez termine la tarea asincrona
     */
    public interface LoadTiemposLineaParadaAsyncTaskResponder {
        void tiemposLoaded(BusLlegada buses);
    }

    private LoadTiemposLineaParadaAsyncTaskResponder responder;

    /**
     * Constructor. Es necesario que nos pasen un objeto para el callback
     *
     * @param responder
     */
    public LoadTiemposLineaParadaAsyncTask(LoadTiemposLineaParadaAsyncTaskResponder responder) {
        this.responder = responder;
    }

    /**
     * Ejecuta el proceso en segundo plano
     */
    @Override
    protected BusLlegada doInBackground(Object... datos) {

        BusLlegada llegadasBus = null;

        try {

            int paradaI = Integer.parseInt((String)datos[1]);

            String linea = (String) datos[0];

            Context context = (Context) datos[3];

            PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
            SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);

            /* if(linea.equals("L9")){

                //Tiempos isae tram diesel
                llegadasBus = ProcesarTiemposTramL9Texto.procesaTiemposLlegadaConLineaConDestino((String) datos[0],paradaI, (String) datos[2]);

            } else if (DatosPantallaPrincipal.esLineaTram((String) datos[0])) {
                //linea, parada, destino
                llegadasBus = ProcesarTiemposTramIsaeService.getParadaConLineaConDestino((String) datos[0], (String) datos[1], (String) datos[2]);

            } else {*/
                //linea, parada
                llegadasBus = ProcesarTiemposService.procesaTiemposLlegadaConParadaLinea((String) datos[0], (String) datos[1], preferencias.getBoolean("enable_https_alberapps", true));
            //}


        } catch (Exception e) {
            return null;
        }

        return llegadasBus;
    }

    /**
     * Se ha terminado la ejecucion comunicamos el resultado al llamador
     */
    @Override
    protected void onPostExecute(BusLlegada result) {
        if (responder != null) {
            responder.tiemposLoaded(result);
        }
    }

}
