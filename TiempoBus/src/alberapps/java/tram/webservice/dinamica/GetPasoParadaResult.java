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
package alberapps.java.tram.webservice.dinamica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetPasoParadaResult implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8238534853834758312L;

    private List<PasoParada> pasoParadaList;

	private String status;

    public GetPasoParadaResult() {

        pasoParadaList = new ArrayList<>();

    }

    public GetPasoParadaResult(List<PasoParada> pasoParadaList, String status) {
        this.pasoParadaList = pasoParadaList;
        this.status = status;
    }

    public List<PasoParada> getPasoParadaList() {
        return pasoParadaList;
    }

    public void setPasoParadaList(List<PasoParada> pasoParadaList) {
        this.pasoParadaList = pasoParadaList;
    }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
