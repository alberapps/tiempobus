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

public class InfoVehiculo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1736225819399570972L;

	private String vehiculo;

	private String linea;

	private String sublinea;

	private String coche;

	private String servBus;

	private String conductor;

	private String servCond;

	private String estado;

	private String estadoLocReal;

	private String xcoord;

	private String ycoord;

	public InfoVehiculo() {

	}

	public String getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(String vehiculo) {
		this.vehiculo = vehiculo;
	}

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}

	public String getSublinea() {
		return sublinea;
	}

	public void setSublinea(String sublinea) {
		this.sublinea = sublinea;
	}

	public String getCoche() {
		return coche;
	}

	public void setCoche(String coche) {
		this.coche = coche;
	}

	public String getServBus() {
		return servBus;
	}

	public void setServBus(String servBus) {
		this.servBus = servBus;
	}

	public String getConductor() {
		return conductor;
	}

	public void setConductor(String conductor) {
		this.conductor = conductor;
	}

	public String getServCond() {
		return servCond;
	}

	public void setServCond(String servCond) {
		this.servCond = servCond;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getEstadoLocReal() {
		return estadoLocReal;
	}

	public void setEstadoLocReal(String estadoLocReal) {
		this.estadoLocReal = estadoLocReal;
	}

	public String getXcoord() {
		return xcoord;
	}

	public void setXcoord(String xcoord) {
		this.xcoord = xcoord;
	}

	public String getYcoord() {
		return ycoord;
	}

	public void setYcoord(String ycoord) {
		this.ycoord = ycoord;
	}

}