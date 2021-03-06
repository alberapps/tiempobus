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
package alberapps.java.tam.webservice.vehiculos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GetVehiculosResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8238534853834758312L;

	private List<InfoVehiculo> infoVehiculoList;

	public GetVehiculosResult() {

		infoVehiculoList = new ArrayList<>();

	}

	public List<InfoVehiculo> getInfoVehiculoList() {
		return infoVehiculoList;
	}

	public void setInfoVehiculoList(List<InfoVehiculo> infoVehiculoList) {
		this.infoVehiculoList = infoVehiculoList;
	}

}
