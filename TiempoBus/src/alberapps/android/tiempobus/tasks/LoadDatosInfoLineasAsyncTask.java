/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
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

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.infolineas.DatosInfoLinea;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.webservice.estructura.ProcesarEstructura;

/**
 * Consulta asincrona del track de la linea
 */
public class LoadDatosInfoLineasAsyncTask extends AsyncTask<DatosInfoLinea, Void, DatosInfoLinea> {

    public interface LoadDatosInfoLineasAsyncTaskResponder {
        void datosInfoLineasLoaded(DatosInfoLinea datos);
    }

    private LoadDatosInfoLineasAsyncTaskResponder responder;

    public LoadDatosInfoLineasAsyncTask(LoadDatosInfoLineasAsyncTaskResponder responder) {
        this.responder = responder;
    }

    @Override
    protected DatosInfoLinea doInBackground(DatosInfoLinea... datos) {
        DatosInfoLinea datosVuelta = new DatosInfoLinea();
        try {

            if (datos != null && datos.length > 0 && datos[0].getLinea() != null) {

                //DatosMapa[] paradas = ProcesarMapaServiceV3.getDatosMapa(datos[0].getUrl());

                PreferenceManager.setDefaultValues(datos[0].getContext(), R.xml.preferences, false);
                SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(datos[0].getContext());

                //Nuevo acceso por servicio web
                DatosMapa[] paradas = ProcesarEstructura.getDatosLineas(datos[0].getLinea(), datos[0].getSublinea(), true, preferencias.getBoolean("enable_https_alberapps", true));

                //Cargar transbordos de base de datos local
                ProcesarEstructura.cargarDatosTransbordosBD(paradas[0], datos[0].getContext());
                ProcesarEstructura.cargarDatosTransbordosBD(paradas[1], datos[0].getContext());


                datosVuelta.setResultIda(paradas[0]);
                datosVuelta.setResultVuelta(paradas[1]);

            } else {
                return null;
            }

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }

        return datosVuelta;
    }

    @Override
    protected void onPostExecute(DatosInfoLinea result) {
        if (responder != null) {
            responder.datosInfoLineasLoaded(result);
        }
    }

}
