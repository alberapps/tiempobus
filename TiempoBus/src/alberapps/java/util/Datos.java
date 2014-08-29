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
package alberapps.java.util;

/**
 * Datos
 * 
 */
public class Datos {

	private String linea;

	private String parada;

    private String destino;

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}

	public String getParada() {
		return parada;
	}

	public void setParada(String parada) {
		this.parada = parada;
	}

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getDestino() {
        return destino;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Datos datos = (Datos) o;

        if (destino != null ? !destino.equals(datos.destino) : datos.destino != null) return false;
        if (linea != null ? !linea.equals(datos.linea) : datos.linea != null) return false;
        if (parada != null ? !parada.equals(datos.parada) : datos.parada != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = linea != null ? linea.hashCode() : 0;
        result = 31 * result + (parada != null ? parada.hashCode() : 0);
        result = 31 * result + (destino != null ? destino.hashCode() : 0);
        return result;
    }
}
