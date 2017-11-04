/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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
package alberapps.android.tiempobus.mapas;

/**
 * Spinner de lineas
 */
public class SpinnerItem {

	int id;
	String descripcion;
	
	public SpinnerItem(int idP, String descripcionP) {
		
		id= idP;
		descripcion = descripcionP;
		
	}
	
	@Override
	public String toString() {
		
		return descripcion;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SpinnerItem that = (SpinnerItem) o;

		return descripcion.trim().toUpperCase().equals(that.descripcion.trim().toUpperCase());

	}

	@Override
	public int hashCode() {
		return descripcion.hashCode();
	}
}
