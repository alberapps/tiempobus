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
package alberapps.java.tram.webservice.vehiculos;

import java.io.Serializable;

import alberapps.java.tam.webservice.vehiculos.InfoVehiculo;

public class InfoCoche implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1736225819399570972L;

	private String coche;

	private String vehiculo;

	private String servBus;

	private String conductor;

	private String servCond;

	private String estado;

	private String estadoLocReal;

	// posicion

	private String offset;

	private String seccion;

	private String xcoord;

	private String ycoord;

	public InfoCoche() {

	}

	public String getVehiculo() {
		return vehiculo;
	}

	public void setVehiculo(String vehiculo) {
		this.vehiculo = vehiculo;
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

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public String getSeccion() {
		return seccion;
	}

	public void setSeccion(String seccion) {
		this.seccion = seccion;
	}

	public InfoVehiculo toInfoVehiculo() {

		InfoVehiculo iv = new InfoVehiculo();

		iv.setCoche(this.getCoche());
		iv.setConductor(this.getConductor());
		iv.setEstado(this.getEstado());
		iv.setEstadoLocReal(this.getEstadoLocReal());
		iv.setLinea("");
		iv.setServBus(this.getServBus());
		iv.setServCond(this.getServCond());
		iv.setSublinea("");
		iv.setVehiculo(this.getVehiculo());
		iv.setXcoord(this.getXcoord());
		iv.setYcoord(this.getYcoord());

		return iv;

	}

}