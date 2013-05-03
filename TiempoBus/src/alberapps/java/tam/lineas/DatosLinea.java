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
package alberapps.java.tam.lineas;

/**
 * 
 * Datos de la linea
 * 
 */
public class DatosLinea {

	private String lineaDescripcion;
	private String lineaCodigoKML;
	private String lineaNum;

	//
	private String tituloCabecera;

	private String grupoLinea;

	public String getLineaDescripcion() {
		return lineaDescripcion;
	}

	public void setLineaDescripcion(String lineaDescripcion) {
		this.lineaDescripcion = lineaDescripcion;
	}

	public String getLineaCodigoKML() {
		return lineaCodigoKML;
	}

	public void setLineaCodigoKML(String lineaCodigoKML) {
		this.lineaCodigoKML = lineaCodigoKML;
	}

	public String getLineaNum() {
		return lineaNum;
	}

	public void setLineaNum(String lineaNum) {
		this.lineaNum = lineaNum;
	}

	public String getTituloCabecera() {
		return tituloCabecera;
	}

	public void setTituloCabecera(String tituloCabecera) {
		this.tituloCabecera = tituloCabecera;
	}

	public String getGrupoLinea() {
		return grupoLinea;
	}

	public void setGrupoLinea(String grupoLinea) {
		this.grupoLinea = grupoLinea;
	}

}
