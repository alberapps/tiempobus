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

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.DatosRespuesta;
import alberapps.java.tam.ProcesarTiemposService;
import alberapps.java.tram.ProcesarTiemposTramIsaeService;
import alberapps.java.tram.ProcesarTiemposTramPorHorarios;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.tram.webservice.dinamica.DinamicaPasoParadaParser;
import alberapps.java.util.Utilidades;

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
        ArrayList<BusLlegada> llegadasDiesel = null;

        DatosRespuesta datosRespuesta = new DatosRespuesta();

        String parada = null;
        int paradaI = 0;

        parada = ((Integer) datos[0]).toString();

        paradaI = (Integer) datos[0];

        Boolean cacheTiempos = (Boolean) datos[2];

        int url1 = 1;
        int url2 = 1;

        if (DatosPantallaPrincipal.esTram(parada)) {

            // Verificar linea 9
            if (!UtilidadesTRAM.ACTIVADO_L9 && UtilidadesTRAM.esParadaL9(parada)) {
                return null;
            }

            // Ip a usar de forma aleatoria
            boolean iprandom = Utilidades.ipRandom();

            if (iprandom) {

                url1 = DinamicaPasoParadaParser.URL1;
                url2 = DinamicaPasoParadaParser.URL2;

                Log.d("TIEMPOS", "Combinacion url 1");

            } else {

                url2 = DinamicaPasoParadaParser.URL1;
                url1 = DinamicaPasoParadaParser.URL2;

                Log.d("TIEMPOS", "Combinacion url 2");

            }

        }

        try {

            if (parada.equals(UtilidadesTRAM.CODIGO_TRAM_BENIDORM)) {

                try {
                    //Tiempos isae tram diesel
                    //llegadasDiesel = ProcesarTiemposTramL9Texto.procesaTiemposLlegada(paradaI);

                    llegadasDiesel = ProcesarTiemposTramPorHorarios.procesaTiemposLlegada(paradaI);

                } catch (Exception e) {
                    llegadasDiesel = null;
                }

                try {
                    //Tiempos isae tram
                    llegadasBus = ProcesarTiemposTramIsaeService.procesaTiemposLlegada(paradaI, url1, cacheTiempos);

                } catch (Exception e) {

                    //En caso de error en servicio de tram pero ok en servcio diesel

                    if (llegadasDiesel == null) {
                        throw e;
                    }

                    e.printStackTrace();
                }


                if (llegadasDiesel != null && !llegadasDiesel.isEmpty()) {

                    if (llegadasBus == null) {
                        llegadasBus = new ArrayList<>();
                    }

                    llegadasBus.addAll(llegadasDiesel);
                }

            } else if (UtilidadesTRAM.esParadaL9(parada)) {

                //llegadasBus = ProcesarTiemposTramL9Texto.procesaTiemposLlegada(paradaI);
                llegadasBus = ProcesarTiemposTramPorHorarios.procesaTiemposLlegada(paradaI);

            } else if (DatosPantallaPrincipal.esTram(parada)) {

                llegadasBus = ProcesarTiemposTramIsaeService.procesaTiemposLlegada(paradaI, url1, cacheTiempos);
            } else {
                llegadasBus = ProcesarTiemposService.procesaTiemposLlegada(paradaI, cacheTiempos);
            }

            datosRespuesta.setListaBusLlegada(llegadasBus);


        } catch (TiempoBusException e) {

            datosRespuesta.setError(e.getCodigo());
            datosRespuesta.setListaBusLlegada(new ArrayList<BusLlegada>());

            e.printStackTrace();

        } catch (Exception e) {

            // Probar con acceso secundario
            if (DatosPantallaPrincipal.esTram(parada)) {

                try {

                    Log.d("TIEMPOS", "Accede a la segunda ruta de tram");

                    llegadasBus = ProcesarTiemposTramIsaeService.procesaTiemposLlegada(paradaI, url2, cacheTiempos);

                    if (UtilidadesTRAM.esParadaL9(parada) && llegadasDiesel != null && !llegadasDiesel.isEmpty()) {
                        llegadasBus.addAll(llegadasDiesel);
                    }

                    datosRespuesta.setListaBusLlegada(llegadasBus);
                } catch (TiempoBusException e2) {

                    datosRespuesta.setError(e2.getCodigo());
                    datosRespuesta.setListaBusLlegada(new ArrayList<BusLlegada>());

                    e.printStackTrace();

                } catch (Exception e1) {

                    e1.printStackTrace();

                    return null;

                }
            } else {

                return null;

            }

            e.printStackTrace();

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
