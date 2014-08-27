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

import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.ProcesarMapaService;

/**
 * Tarea para la carga de datos de los mapas
 */
public class LoadDatosMapaAsyncTask extends AsyncTask<String, Void, DatosMapa> {


    public interface LoadDatosMapaAsyncTaskResponder {
        public void datosMapaLoaded(DatosMapa datosMapa);
    }

    private LoadDatosMapaAsyncTaskResponder responder;


    public LoadDatosMapaAsyncTask(LoadDatosMapaAsyncTaskResponder responder) {
        this.responder = responder;
    }


    @Override
    protected DatosMapa doInBackground(String... datos) {
        DatosMapa datosMapa = null;
        try {
            datosMapa = ProcesarMapaService.getDatosMapa(datos[0]);

            if (datos.length == 2) {
                datosMapa.setRecorrido(ProcesarMapaService.getDatosMapaRecorrido(datos[1]));
            }

        } catch (Exception e) {
            return null;
        }

        return datosMapa;
    }


    @Override
    protected void onPostExecute(DatosMapa result) {
        if (responder != null) {
            responder.datosMapaLoaded(result);
        }
    }


}
