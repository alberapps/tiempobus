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
package alberapps.android.tiempobus.database;

/**
 * 
 * Informacion de la parada
 * 
 */
public class Parada {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parada == null) ? 0 : parada.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parada other = (Parada) obj;
		if (parada == null) {
			if (other.parada != null)
				return false;
		} else if (!parada.equals(other.parada))
			return false;
		return true;
	}

	private String lineaNum;

	private String lineaDesc;

	private String destino;

	private String parada;

	private String coordenadas;

	private String direccion;

	private String conexion;

	private Integer latitud;

	private Integer longitud;

	public String getLineaNum() {
		return lineaNum;
	}

	public void setLineaNum(String lineaNum) {
		this.lineaNum = lineaNum;
	}

	public String getLineaDesc() {
		return lineaDesc;
	}

	public void setLineaDesc(String lineaDesc) {
		this.lineaDesc = lineaDesc;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public String getParada() {
		return parada;
	}

	public void setParada(String parada) {
		this.parada = parada;
	}

	public String getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(String coordenadas) {
		this.coordenadas = coordenadas;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getConexion() {
		return conexion;
	}

	public void setConexion(String conexion) {
		this.conexion = conexion;
	}

	public Integer getLatitud() {
		return latitud;
	}

	public void setLatitud(Integer latitud) {
		this.latitud = latitud;
	}

	public Integer getLongitud() {
		return longitud;
	}

	public void setLongitud(Integer longitud) {
		this.longitud = longitud;
	}

}
