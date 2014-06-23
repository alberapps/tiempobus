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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tram.webservice.GetPasoParadaResult;
import alberapps.java.tram.webservice.GetPasoParadaWebservice;
import alberapps.java.tram.webservice.GetPasoParadaXmlWebservice;
import android.util.Log;

/**
 * Consulta de tiempos TRAM
 * 
 */
public class ProcesarTiemposTramIsaeService {

	
	private static GetPasoParadaXmlWebservice service = new GetPasoParadaXmlWebservice();
	
	/**
	 * Procesa tiempos
	 * 
	 * @param parada
	 * @return
	 * @throws Exception
	 */

	public static ArrayList<BusLlegada> procesaTiemposLlegada(int parada, int consulta) throws Exception {

		ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();

		ArrayList<BusLlegada> busesList = new ArrayList<BusLlegada>();

		// ProcesarTiemposTramService.enviarDebug("Inicia proceso para parada= "
		// + parada);

		//for (int i = 0; i < UtilidadesTRAM.LINEAS_A_CONSULTAR.length; i++) {

			try {

				//busesList = getParadaConLineaTRAM(UtilidadesTRAM.LINEAS_A_CONSULTAR[i], Integer.toString(parada), consulta);
				
				//Nuevo modo de consulta. * recupera todas las lineas
				busesList = getParadaConLineaTRAM("*", Integer.toString(parada), consulta);

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

		//}

		// ProcesarTiemposTramService.enviarDebug("Proceso finalizado para: " +
		// parada + " resultados= " + buses.size());

		// Control errores del status
		if (buses == null || buses.isEmpty()) {

			throw new TiempoBusException(TiempoBusException.ERROR_STATUS_SERVICIO);

		}

		Collections.sort(buses);
		return buses;
	}

	public static void combinarRegistros(List<BusLlegada> busesList) {

		for (int i = 0; i < busesList.size(); i++) {
			for (int j = 0; j < busesList.size(); j++) {
				if (i != j && busesList.get(i).getLinea().equals(busesList.get(j).getLinea()) && busesList.get(i).getDestino().equals(busesList.get(j).getDestino())) {

					// Añadir como repetido
					busesList.get(i).setSegundoTram(busesList.remove(j));

					Log.d("TRAM", "Unificados registros: " + busesList.get(i).getLinea() + " - " + busesList.get(i).getSegundoTram().getLinea());

					j = 0;

					ordenarTiempos(busesList.get(i));

					continue;

				}
			}

		}

	}

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
	 * 
	 * @param linea
	 * @param parada
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<BusLlegada> getParadaConLineaTRAM(String linea, String parada, int consulta) throws Exception {

		ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();

		

		GetPasoParadaResult serviceResult = service.consultarServicio(linea, parada, consulta);

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

			// >60min
			//if (bus.getProximoMinutos() > 60) {
				// Quitar
			//} else {
				buses.add(bus);
			//}

			// Filtrar repetidos
			combinarRegistros(buses);

		}

		return buses;
	}

	/**
	 * Recupera tiempos para una parada y linea indicadas
	 * 
	 * @param linea
	 * @param poste
	 * @return
	 * @throws Exception
	 */

	public static BusLlegada getParadaConLineaConDestino(String linea, String parada, String destino) throws Exception {

		Log.d("TIEMPOS TRAM", "LINEA: " + linea + " PARADA: " + parada + " destino: " + destino);

		BusLlegada buses = null;

		ArrayList<BusLlegada> busesList = new ArrayList<BusLlegada>();

		try {

			//busesList = getParadaConLineaTRAM(linea, parada, GetPasoParadaWebservice.URL1);
			
			//Cambio de metodo por discrepancias en cabeceras
			busesList = getParadaConLineaTRAM("*", parada, GetPasoParadaXmlWebservice.URL1);
			
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

}
