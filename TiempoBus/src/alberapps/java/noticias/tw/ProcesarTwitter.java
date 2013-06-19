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
package alberapps.java.noticias.tw;

import java.util.Collections;
import java.util.List;

import alberapps.java.noticias.tw.tw4j.ProcesarTwitter4j;

public class ProcesarTwitter {

	public static final String tw_alicante_ruta = "http://twitter.com/Alicante_City";

	public static final String tw_alberapps_ruta = "http://twitter.com/alberapps";

	public static final String tw_campello_ruta = "http://twitter.com/campelloturismo";

	public static final String tw_sanvi_ruta = "http://twitter.com/aytoraspeig";

	public static final String tw_santjoan_ruta = "http://twitter.com/sant_joan";

	public static final String TW_STATUS = "/status/";

	/**
	 * listas que se quieran
	 * 
	 * @return listado
	 */
	public static List<TwResultado> procesar(List<Boolean> cargar, String cantidad) {

		List<TwResultado> lista;

		// Iniciar
		ProcesarTwitter4j procesar4j = new ProcesarTwitter4j();
		procesar4j.setUp();

		lista = procesar4j.recuperarTimeline("alberapps", tw_alberapps_ruta, Integer.parseInt(cantidad));

		if (cargar.get(0)) {

			lista.addAll(procesar4j.recuperarTimeline("Alicante_City", tw_alicante_ruta, Integer.parseInt(cantidad)));

		}

		if (cargar.get(1)) {

			lista.addAll(procesar4j.recuperarTimeline("campelloturismo", tw_campello_ruta, Integer.parseInt(cantidad)));

		}

		if (cargar.get(2)) {

			lista.addAll(procesar4j.recuperarTimeline("aytoraspeig", tw_sanvi_ruta, Integer.parseInt(cantidad)));

		}

		if (cargar.get(3)) {

			lista.addAll(procesar4j.recuperarTimeline("sant_joan", tw_santjoan_ruta, Integer.parseInt(cantidad)));

		}

		if (lista != null && !lista.isEmpty()) {

			// Ordenar por fecha
			Collections.sort(lista);

		} else {

			return null;

		}

		return lista;

	}

}
