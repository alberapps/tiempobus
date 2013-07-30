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
package alberapps.java.exception;

/**
 *  
 */
public class TiempoBusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2041443242078108937L;

	public static int ERROR_STATUS_SERVICIO = 1;
	public static String ERROR_STATUS_SERVICIO_MSG = "Error en el status del servicio";

	private int codigo;

	/**
	 * 
	 */
	public TiempoBusException() {

	}

	public TiempoBusException(int cod) {

		super(ERROR_STATUS_SERVICIO_MSG);

		codigo = cod;
	}

	/**
	 * @param detailMessage
	 */
	public TiempoBusException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public TiempoBusException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public TiempoBusException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

}
