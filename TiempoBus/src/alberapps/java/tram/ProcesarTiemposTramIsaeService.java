/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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
package alberapps.java.tram;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tram.webservice.dinamica.DinamicaPasoParadaParser;
import alberapps.java.tram.webservice.dinamica.DomGetPasoParadaXmlWebservice;
import alberapps.java.tram.webservice.dinamica.GetPasoParadaResult;

/**
 * Consulta de tiempos TRAM
 */
public class ProcesarTiemposTramIsaeService {

    //private static GetPasoParadaXmlWebservice service = new GetPasoParadaXmlWebservice();
    private static DinamicaPasoParadaParser service = new DinamicaPasoParadaParser();


    public static int TIEMPO_MAXIMO = 120;

    /**
     * Procesa tiempos
     *
     * @param parada
     * @return
     * @throws Exception
     */
    public static ArrayList<BusLlegada> procesaTiemposLlegada(int parada, int consulta, Boolean cacheTiempos) throws Exception {

        ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();

        ArrayList<BusLlegada> busesList = new ArrayList<BusLlegada>();

        // ProcesarTiemposTramService.enviarDebug("Inicia proceso para parada= "
        // + parada);

        // for (int i = 0; i < UtilidadesTRAM.LINEAS_A_CONSULTAR.length; i++) {

        try {

            // busesList =
            // getParadaConLineaTRAM(UtilidadesTRAM.LINEAS_A_CONSULTAR[i],
            // Integer.toString(parada), consulta);

            // Nuevo modo de consulta. * recupera todas las lineas
            busesList = getParadaConLineaTRAM("*", Integer.toString(parada), consulta, cacheTiempos);

        } catch (Exception e) {

            Log.d("TIEMPOS TRAM", "TRAM: " + e.getMessage());

            // ProcesarTiemposTramService.enviarDebug("Error procesado= " +
            // e.getMessage() + " - " + e.getClass());

            busesList = null;

            throw e;
        }

        if (busesList != null) {
            buses.addAll(busesList);
        }

        // }

        // ProcesarTiemposTramService.enviarDebug("Proceso finalizado para: " +
        // parada + " resultados= " + buses.size());

        // Control errores del status
        if (buses == null || buses.isEmpty()) {

            throw new TiempoBusException(TiempoBusException.ERROR_STATUS_SERVICIO);

        }

        Collections.sort(buses);
        return buses;
    }

    /**
     *
     * @param busesList
     */
    public static void combinarRegistros(List<BusLlegada> busesList) {

        for (int i = 0; i < busesList.size(); i++) {
            for (int j = 0; j < busesList.size(); j++) {
                if (i != j && busesList.get(i).getLinea().equals(busesList.get(j).getLinea()) && busesList.get(i).getDestino().equals(busesList.get(j).getDestino())) {

                    // Añadir como repetido
                    busesList.get(i).setSegundoTram(busesList.remove(j));

                    Log.d("TRAM", "Unificados registros: " + busesList.get(i).getLinea() + " - " + busesList.get(i).getSegundoTram().getLinea());

                    j = 0;

                    ordenarTiempos(busesList.get(i));

                    // Si el tercero es mayor que el maximo
                    if (busesList.get(i).getSegundoTram().getSiguienteMinutos() > 60) {
                        busesList.get(i).getSegundoTram().cambiarSiguiente(9999);
                    }

                    // Eliminar si excede tiempo maximo
                    if (busesList.get(i).getSegundoTram().getProximoMinutos() > 60 || busesList.get(i).getSegundoTram().getProximoMinutos() < 0) {
                        busesList.get(i).setSegundoTram(null);
                    }

                    continue;

                }
            }

        }

    }

    /**
     * Ordenacion de los tiempos en caso de repetirse
     *
     * @param bus
     */
    private static void ordenarTiempos(BusLlegada bus) {

        Log.d("TRAM", "REORDENAR");

        Integer tiempo1 = bus.getProximoMinutos();
        Integer tiempo2 = bus.getSiguienteMinutos();
        Integer tiempo3 = bus.getSegundoTram().getProximoMinutos();
        Integer tiempo4 = bus.getSegundoTram().getSiguienteMinutos();

        List<Integer> lista = Arrays.asList(tiempo1, tiempo2, tiempo3, tiempo4);

        Collections.sort(lista);

        bus.cambiarProximo(lista.get(0));
        bus.cambiarSiguiente(lista.get(1));
        bus.getSegundoTram().cambiarProximo(lista.get(2));
        bus.getSegundoTram().cambiarSiguiente(lista.get(3));

        Log.d("TRAM", "REORDENAR: " + bus.getProximo());
        Log.d("TRAM", "REORDENAR2: " + bus.getSegundoTram().getProximo());


    }

    /**
     * @param linea
     * @param parada
     * @return
     * @throws Exception
     */
    public static ArrayList<BusLlegada> getParadaConLineaTRAM(String linea, String parada, int consulta, Boolean cacheTiempos) throws Exception {

        ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();


        //TODO PARCHE PARA PARADA LONDRES
        if (parada.equals(UtilidadesTRAM.CODIGO_TRAM_LONDRES)) {

            linea = UtilidadesTRAM.LINEAS_A_CONSULTAR[2];

        }


        GetPasoParadaResult serviceResult = service.consultarServicio(linea, parada, consulta, cacheTiempos);

        for (int i = 0; i < serviceResult.getPasoParadaList().size(); i++) {

            String infoSalidas = "";

            if (serviceResult.getPasoParadaList().get(i).getE1().getMinutos().substring(0, 1).equals("0")) {

                infoSalidas += "enlaparada";

            } else if (serviceResult.getPasoParadaList().get(i).getE1().getMinutos().substring(0, 1).equals("-")) {

                infoSalidas += "sinestimacion";

            } else {

                infoSalidas += serviceResult.getPasoParadaList().get(i).getE1().getMinutos();

            }

            infoSalidas += ";";

            if (serviceResult.getPasoParadaList().get(i).getE2().getMinutos().substring(0, 2).equals("-1")) {

                infoSalidas += "sinestimacion";

            } else {

                infoSalidas += serviceResult.getPasoParadaList().get(i).getE2().getMinutos();

            }

            BusLlegada bus = new BusLlegada(serviceResult.getPasoParadaList().get(i).getLinea(), serviceResult.getPasoParadaList().get(i).getRuta(), infoSalidas);

            if (bus.getSiguienteMinutos() > TIEMPO_MAXIMO) {
                bus.cambiarSiguiente(9999);
            }

            //Control L9
            if(bus.getLinea().equals("L9") && parada.equals(UtilidadesTRAM.CODIGO_TRAM_BENIDORM)){
                continue;
            }

            // >60min
            if (bus.getProximoMinutos() > TIEMPO_MAXIMO) {
                // Quitar
            } else {
                buses.add(bus);
            }




            // Filtrar repetidos
            combinarRegistros(buses);

        }

        //TODO PARCHE L2 PARADA 2
        parcheL2en2(parada, buses);


        return buses;
    }

    /**
     * Recupera tiempos para una parada y linea indicadas
     *
     * @param linea
     * @param parada
     * @return
     * @throws Exception
     */
    public static BusLlegada getParadaConLineaConDestino(String linea, String parada, String destino) throws Exception {

        Log.d("TIEMPOS TRAM", "LINEA: " + linea + " PARADA: " + parada + " destino: " + destino);

        BusLlegada buses = null;

        ArrayList<BusLlegada> busesList = new ArrayList<BusLlegada>();

        try {

            // busesList = getParadaConLineaTRAM(linea, parada,
            // GetPasoParadaWebservice.URL1);

            // Cambio de metodo por discrepancias en cabeceras
            busesList = getParadaConLineaTRAM("*", parada, DomGetPasoParadaXmlWebservice.URL1, false);


            for (int i = 0; i < busesList.size(); i++) {

                if (busesList.get(i).getLinea().equals(linea) && busesList.get(i).getDestino().equals(destino)) {

                    if (busesList.get(i).getProximoMinutos() > -1 && busesList.get(i).getProximoMinutos() < 60) {

                        buses = busesList.get(i);

                        Log.d("TIEMPOS TRAM", "tiempo valido: " + buses.getProximo());

                    }

                }

            }

        } catch (Exception e) {

            // ProcesarTiemposTramService.enviarDebug("Error procesado= " +
            // e.getMessage() + " - " + e.getClass());

            buses = null;
        }

        return buses;

    }


    /**
     * Parche para usar tiempos de la parada 3 en la 2
     *
     * @param parada
     * @param busesList
     */
    private static void parcheL2en2(String parada, ArrayList<BusLlegada> busesList) {

        //Sustituir los tiempos de la L2 en 2 por los tiempos de la 3 restandole 2 minutos
        //Para evitar los tiempos inexactos que devuelve el servicio
        if (parada.equals(UtilidadesTRAM.CODIGO_TRAM_LUCEROS)) {

            try {

                ArrayList<BusLlegada> busesListAux = new ArrayList<BusLlegada>();

                busesListAux = getParadaConLineaTRAM("*", UtilidadesTRAM.CODIGO_TRAM_MERCADO, DomGetPasoParadaXmlWebservice.URL1, false);

                BusLlegada busAux = null;

                //Recuperar el dato de la parada 3
                for (int i = 0; i < busesListAux.size(); i++) {

                    if (busesListAux.get(i).getLinea().equals(UtilidadesTRAM.LINEAS_A_CONSULTAR[3]) && busesListAux.get(i).getDestino().contains(UtilidadesTRAM.L2_SANTVICENT)) {

                        busAux = busesListAux.get(i);

                        if (busAux.getProximoMinutos() > 2) {
                            busAux.cambiarProximo(busAux.getProximoMinutos() - 2);
                            busAux.cambiarSiguiente(busAux.getSiguienteMinutos() - 2);
                        } else if (busAux.getProximoMinutos() == 2) {
                            busAux.cambiarProximo(0);
                            busAux.cambiarSiguiente(busAux.getSiguienteMinutos() - 2);
                        } else if (busAux.getProximoMinutos() < 2) {
                            busAux.cambiarProximo(busAux.getSiguienteMinutos() - 2);
                            busAux.cambiarSiguiente(9999);
                        }

                        busAux.setSegundoTram(null);
                        busAux.setSegundoBus(null);

                        //busAux.setDestino(busAux.getDestino() + " *");

                    }

                }

                //Si disponible
                if (busAux != null) {

                    if (busAux.getSiguienteMinutos() > TIEMPO_MAXIMO) {
                        busAux.cambiarSiguiente(9999);
                    }

                    // >60min
                    if (busAux.getProximoMinutos() > TIEMPO_MAXIMO) {
                        // Quitar
                        busAux = null;
                    }

                    //Sustituir el actual
                    boolean encontrado = false;
                    if(busAux != null) {
                        for (int i = 0; i < busesList.size(); i++) {

                            if (busesList.get(i).getLinea().equals(UtilidadesTRAM.LINEAS_A_CONSULTAR[3]) && busesList.get(i).getDestino().contains(UtilidadesTRAM.L2_SANTVICENT)) {

                                busesList.set(i, busAux);

                                encontrado = true;
                                break;

                            }
                        }
                    }

                    if(!encontrado){

                            busesList.add(busAux);


                    }

                }

            } catch (Exception e) {

                //Ignorar si hay error en este parche

            }

        }

    }


}
