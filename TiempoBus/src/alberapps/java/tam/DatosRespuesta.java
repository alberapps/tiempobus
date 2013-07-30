/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
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

import java.io.Serializable;
import java.util.ArrayList;

public class DatosRespuesta implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6379507005761833173L;
	
	private ArrayList<BusLlegada> listaBusLlegada;
	
	private Integer error = 0;

	public ArrayList<BusLlegada> getListaBusLlegada() {
		return listaBusLlegada;
	}

	public void setListaBusLlegada(ArrayList<BusLlegada> listaBusLlegada) {
		this.listaBusLlegada = listaBusLlegada;
	}

	public Integer getError() {
		return error;
	}

	public void setError(Integer error) {
		this.error = error;
	}

}
