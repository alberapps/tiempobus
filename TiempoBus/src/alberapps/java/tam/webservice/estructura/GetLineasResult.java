/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
package alberapps.java.tam.webservice.estructura;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetLineasResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6381814647861271111L;

	List<InfoLinea> infoLineaList;

	private HashMap<String, HashMap<String,List<String>>> hashDatosParadas;
	
	
	public GetLineasResult() {

		infoLineaList = new ArrayList<InfoLinea>();
		
		hashDatosParadas = new HashMap<String, HashMap<String,List<String>>>();
		
		
	}

	public List<InfoLinea> getInfoLineaList() {
		return infoLineaList;
	}

	public void setInfoLineaList(List<InfoLinea> infoLineaList) {
		this.infoLineaList = infoLineaList;
	}

	public HashMap<String, HashMap<String, List<String>>> getHashDatosParadas() {
		return hashDatosParadas;
	}

	public void setHashDatosParadas(HashMap<String, HashMap<String, List<String>>> hashDatosParadas) {
		this.hashDatosParadas = hashDatosParadas;
	}

	

}
