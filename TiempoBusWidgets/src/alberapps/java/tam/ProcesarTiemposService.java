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

import alberapps.java.tam.webservice.GetPasoParadaResult;
import alberapps.java.tam.webservice.GetPasoParadaXmlWebservice;

/**
 * Consulta de tiempos
 * 
 */
public class ProcesarTiemposService {

	/**
	 * Recupera tiempos para una parada y linea indicadas
	 * 
	 * @param linea
	 * @param parada
	 * @return
	 * @throws Exception
	 */
	public static BusLlegada getPosteConLinea(String linea, String parada) throws Exception {

		BusLlegada buses = null;

		GetPasoParadaXmlWebservice service = new GetPasoParadaXmlWebservice();

		GetPasoParadaResult serviceResult = service.consultarServicio(linea, parada);

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

			BusLlegada bus = new BusLlegada(serviceResult.getPasoParadaList().get(i).getLinea(), serviceResult.getPasoParadaList().get(i).getRuta(), infoSalidas, parada);

			buses = bus;

		}

		Log.d("TIEMPOS", buses.getLinea());

		return buses;
	}

}
