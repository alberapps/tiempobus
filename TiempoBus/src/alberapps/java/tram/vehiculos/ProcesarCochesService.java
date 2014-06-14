/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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
package alberapps.java.tram.vehiculos;

import java.util.ArrayList;
import java.util.List;

import alberapps.java.tam.webservice.vehiculos.InfoVehiculo;
import alberapps.java.tram.webservice.vehiculos.GetCochesXmlWebservice;
import alberapps.java.tram.webservice.vehiculos.InfoCoche;

/**
 * Consulta de poscion de vehiculos
 * 
 */
public class ProcesarCochesService {

	/**
	 * Procesa vehiculos
	 * 
	 * @param linea
	 * @return lista
	 * @throws Exception
	 */

	public static List<InfoVehiculo> procesaVehiculos(String linea, int consulta) throws Exception {

		GetCochesXmlWebservice service = new GetCochesXmlWebservice();

		List<InfoCoche> cochesList = null;
		List<InfoVehiculo> vehiculosList = new ArrayList<InfoVehiculo>();

		cochesList = service.consultarServicio(linea, consulta).getInfoVehiculoList();

		if (cochesList == null) {
			return null;
		}

		InfoVehiculo iv = null;

		for (int i = 0; i < cochesList.size(); i++) {

			iv = cochesList.get(i).toInfoVehiculo();

			vehiculosList.add(iv);

		}

		return vehiculosList;
	}

}
