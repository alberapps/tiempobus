/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
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
package alberapps.android.tiempobus.tasks;

import android.content.Context;
import android.os.AsyncTask;

import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.webservice.estructura.ProcesarEstructura;

/**
 * Tarea para la carga de datos de los recorridos de lineas
 */
public class LoadDatosRecorridosAsyncTask extends AsyncTask<Object, Void, DatosMapa[]> {

    public interface LoadDatosRecorridosAsyncTaskResponder {
        void datosRecorridosLoaded(DatosMapa[] datosMapa);
    }

    private LoadDatosRecorridosAsyncTaskResponder responder;

    public LoadDatosRecorridosAsyncTask(LoadDatosRecorridosAsyncTaskResponder responder) {
        this.responder = responder;
    }

    @Override
    protected DatosMapa[] doInBackground(Object... datos) {
        DatosMapa[] datosMapa = {null, null};
        try {

            if (datos != null && datos.length > 0 && datos[0] != null) {

                //Nuevo acceso por servicio web
                DatosMapa[] paradas = ProcesarEstructura.getDatosNodosMapa((String) datos[0], (String) datos[1], true);

                //Cargar transbordos de base de datos local
                ProcesarEstructura.cargarDatosTransbordosBD(paradas[0], (Context) datos[2]);
                ProcesarEstructura.cargarDatosTransbordosBD(paradas[1], (Context) datos[2]);

                datosMapa[0] = paradas[0];
                datosMapa[1] = paradas[1];

                String[] recorridos = ProcesarEstructura.getDatosPolyLineaKml((String) datos[0], (String) datos[1], true, (Context) datos[2], paradas[0].getIdRuta());

                datosMapa[0].setRecorrido(recorridos[0]);
                datosMapa[1].setRecorrido(recorridos[1]);


            } else {
                return datosMapa;
            }

        } catch (Exception e) {

            e.printStackTrace();

            return datosMapa;
        }

        return datosMapa;
    }

    @Override
    protected void onPostExecute(DatosMapa[] result) {
        if (responder != null) {
            responder.datosRecorridosLoaded(result);
        }
    }

}
