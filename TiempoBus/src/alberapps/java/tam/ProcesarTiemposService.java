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
package alberapps.java.tam;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.webservice.GetPasoParadaResult;
import alberapps.java.tam.webservice.GetPasoParadaXmlWebservice;

/**
 * Consulta de tiempos
 */
public class ProcesarTiemposService {

    /**
     * Procesa tiempos
     *
     * @param parada
     * @return lista bus
     * @throws Exception
     */
    public static ArrayList<BusLlegada> procesaTiemposLlegada(int parada) throws Exception {

        ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();

        GetPasoParadaXmlWebservice service = new GetPasoParadaXmlWebservice();

        GetPasoParadaResult serviceResult = service.consultarServicio(null, Integer.toString(parada));

        // Control errores del status
        if (serviceResult != null && (serviceResult.getPasoParadaList() == null || serviceResult.getPasoParadaList().isEmpty()) && serviceResult.getStatus().equals("-1")) {

            throw new TiempoBusException(TiempoBusException.ERROR_STATUS_SERVICIO);

        }

        for (int i = 0; i < serviceResult.getPasoParadaList().size(); i++) {

            String infoSalidas = "";

            if (serviceResult.getPasoParadaList().get(i).getE1().getMinutos().substring(0, 1).equals("0")) {

                infoSalidas += "enlaparada";

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

            buses.add(bus);

        }

        // Filtrar repetidos
        combinarRegistros(buses);

        Collections.sort(buses);
        return buses;
    }

    /**
     * Combinar los registros cuando llegan duplicados
     *
     * @param busesList
     */
    public static void combinarRegistros(List<BusLlegada> busesList) {

        if (busesList == null) {
            return;
        }

        for (int i = 0; i < busesList.size(); i++) {
            for (int j = 0; j < busesList.size(); j++) {
                if (i != j && busesList.get(i).getLinea().equals(busesList.get(j).getLinea()) && busesList.get(i).getDestino().equals(busesList.get(j).getDestino())) {

                    // Añadir como repetido
                    busesList.get(i).setSegundoBus(busesList.remove(j));

                    Log.d("TRAM", "Unificados registros: " + busesList.get(i).getLinea() + " - " + busesList.get(i).getSegundoBus().getLinea());

                    j = 0;

                    ordenarTiempos(busesList.get(i));

                    continue;

                }
            }

        }

    }

    /**
     * Ordenar duplicados por tiempos
     *
     * @param bus
     */
    private static void ordenarTiempos(BusLlegada bus) {

        Log.d("TRAM", "REORDENAR");

        Integer tiempo1 = bus.getProximoMinutos();
        Integer tiempo2 = bus.getSiguienteMinutos();
        Integer tiempo3 = bus.getSegundoBus().getProximoMinutos();
        Integer tiempo4 = bus.getSegundoBus().getSiguienteMinutos();

        List<Integer> lista = Arrays.asList(tiempo1, tiempo2, tiempo3, tiempo4);

        Collections.sort(lista);

        bus.cambiarProximo(lista.get(0));
        bus.cambiarSiguiente(lista.get(1));
        bus.getSegundoBus().cambiarProximo(lista.get(2));
        bus.getSegundoBus().cambiarSiguiente(lista.get(3));

        Log.d("TRAM", "REORDENAR: " + bus.getProximo());
        Log.d("TRAM", "REORDENAR2: " + bus.getSegundoBus().getProximo());

    }

    /**
     * Recupera tiempos para una parada y linea indicadas
     *
     * @param linea
     * @param poste
     * @return bus
     * @throws Exception
     */
    public static BusLlegada getPosteConLinea(String linea, String poste) throws Exception {

        ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();

        GetPasoParadaXmlWebservice service = new GetPasoParadaXmlWebservice();

        GetPasoParadaResult serviceResult = service.consultarServicio(linea, poste);

        for (int i = 0; i < serviceResult.getPasoParadaList().size(); i++) {

            String infoSalidas = "";

            if (serviceResult.getPasoParadaList().get(i).getE1().getMinutos().substring(0, 1).equals("0")) {

                infoSalidas += "enlaparada";

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

            buses.add(bus);

        }

        // Filtrar repetidos
        combinarRegistros(buses);

        if (buses == null || buses.isEmpty()) {
            return null;
        }

        return buses.get(0);

    }

}
