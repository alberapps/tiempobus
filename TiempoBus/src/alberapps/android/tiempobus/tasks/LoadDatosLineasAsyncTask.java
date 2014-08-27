/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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

import android.os.AsyncTask;

import java.util.ArrayList;

import alberapps.java.tam.BusLinea;
import alberapps.java.tam.lineas.ProcesarDatosLineasIsaeService;

/**
 * Cargar informacion de las lineas
 */
public class LoadDatosLineasAsyncTask extends AsyncTask<String, Void, ArrayList<BusLinea>> {


    public interface LoadDatosLineasAsyncTaskResponder {
        public void busesLoaded(ArrayList<BusLinea> buses);
    }

    private LoadDatosLineasAsyncTaskResponder responder;


    public LoadDatosLineasAsyncTask(LoadDatosLineasAsyncTaskResponder responder) {
        this.responder = responder;
    }


    @Override
    protected ArrayList<BusLinea> doInBackground(String... datos) {
        ArrayList<BusLinea> lineasBus = null;
        try {

            String datosOffline = null;

            if (datos != null && datos.length > 0 && datos[0] != null) {
                datosOffline = datos[0];
            }

            lineasBus = ProcesarDatosLineasIsaeService.getLineasBus(datosOffline);


        } catch (Exception e) {
            lineasBus = null;
        }

        return lineasBus;
    }

    @Override
    protected void onPostExecute(ArrayList<BusLinea> result) {
        if (responder != null) {
            responder.busesLoaded(result);
        }
    }

}
