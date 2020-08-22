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
package alberapps.android.tiempobus.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.DatosRespuesta;
import alberapps.java.tam.ProcesarTiemposService;

/**
 * Tarea asincrona que se encarga de consultar los tiempos
 */
public class LoadTiemposAsyncTask extends AsyncTask<Object, Void, DatosRespuesta> {

    /**
     * Interfaz que deberian implementar las clases que la quieran usar Sirve
     * como callback una vez termine la tarea asincrona
     */
    public interface LoadTiemposAsyncTaskResponder {
        void tiemposLoaded(DatosRespuesta buses);
    }

    private LoadTiemposAsyncTaskResponder responder;

    /**
     * Constructor. Es necesario que nos pasen un objeto para el callback
     *
     * @param responder
     */
    public LoadTiemposAsyncTask(LoadTiemposAsyncTaskResponder responder) {
        this.responder = responder;
    }

    /**
     * Ejecuta el proceso en segundo plano
     */
    @Override
    protected DatosRespuesta doInBackground(Object... datos) {

        ArrayList<BusLlegada> llegadasBus = null;

        DatosRespuesta datosRespuesta = new DatosRespuesta();

        String parada = null;
        int paradaI = 0;

        parada = ((Integer) datos[0]).toString();
        paradaI = (Integer) datos[0];
        Context context = (Context) datos[1];

        /////////
        //PrecargasV3.precargarDatosLineas(context);
        //PrecargasV3.precargarDatosLineasRecorrido(context);
        /////////

        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(context);

        boolean opcionTR = false;

        Boolean cacheTiempos = (Boolean) datos[2];


        try {

            if (DatosPantallaPrincipal.esTram(parada)) {

                BusLlegada bus = new BusLlegada();
                llegadasBus = new ArrayList<>();

                if (opcionTR) {
                    bus.setErrorServicio(true);
                    bus.setTiempoReal(true);
                } else {
                    bus.setTiempoReal(false);
                    bus.setSinDatos(true);
                }
                llegadasBus.add(bus);


            } else {
                llegadasBus = ProcesarTiemposService.procesaTiemposLlegada(paradaI, cacheTiempos);
            }

            datosRespuesta.setListaBusLlegada(llegadasBus);


        } catch (TiempoBusException e) {

            e.printStackTrace();

            if (e.getCodigo() == TiempoBusException.ERROR_005_SERVICIO) {

                datosRespuesta.setError(e.getCodigo());
                datosRespuesta.setListaBusLlegada(new ArrayList<BusLlegada>());

            } else {

                return null;

            }


        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

        return datosRespuesta;
    }


    @Override
    protected void onCancelled(DatosRespuesta datosRespuesta) {

        datosRespuesta = null;

        super.onCancelled(datosRespuesta);
    }

    /**
     * Se ha terminado la ejecucion comunicamos el resultado al llamador
     */
    @Override
    protected void onPostExecute(DatosRespuesta result) {
        if (responder != null) {
            responder.tiemposLoaded(result);
        }
    }

}
