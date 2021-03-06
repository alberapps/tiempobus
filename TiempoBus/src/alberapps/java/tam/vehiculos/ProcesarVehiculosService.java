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
package alberapps.java.tam.vehiculos;

import java.util.List;

import alberapps.java.tam.webservice.vehiculos.GetVehiculosXmlWebservice;
import alberapps.java.tam.webservice.vehiculos.InfoVehiculo;

/**
 * Consulta de poscion de vehiculos
 */
public class ProcesarVehiculosService {

    /**
     * Procesa vehiculos
     *
     * @param linea
     * @return lista
     * @throws Exception
     */

    public static List<InfoVehiculo> procesaVehiculos(String linea, Boolean tiemposCache) throws Exception {

        GetVehiculosXmlWebservice service = new GetVehiculosXmlWebservice();

        List<InfoVehiculo> vehiculosList = null;

        //PARCHE 11
        String lineab = linea;
        if(linea.equals("11H")){
            lineab = "11";
        }

        vehiculosList = service.consultarServicio(lineab, tiemposCache).getInfoVehiculoList();

        return vehiculosList;
    }

}
