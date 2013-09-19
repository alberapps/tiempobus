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
package alberapps.java.tram;

import java.util.List;

import alberapps.java.tam.mapas.PlaceMark;
import android.util.Log;

public class UtilidadesTRAM {

	public static boolean ACTIVADO_TRAM = false;
		
	
	public static String[] LINEAS_NUM = { "L1", "L3", "L4", "L9", "4L", "L2" };

	public static int[] L1_ORDEN = { 2, 3, 4, 5, 6, 8, 17, 19, 20, 21, 22, 25, 26, 27, 28, 29, 30, 31, 32, 33 };

	public static int[] L3_ORDEN = { 2, 3, 4, 5, 6, 7, 8, 9, 51, 10, 11, 12, 13, 14, 15, 16, 17 };

	public static int[] L4_ORDEN = { 2, 3, 4, 5, 6, 7, 8, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113 };

	public static int[] L9_ORDEN = { 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50 };

	public static int[] L4L_ORDEN = { 101, 102, 5 };
	
	public static int[] L2_ORDEN = { 2, 3, 4, 1201, 1202, 1203, 1204, 1205, 1206, 1207, 1208, 1209, 1210, 1211 };

		
	public static String[] DESC_TIPO = { "", "TRAM - L1", "TRAM - L3", "TRAM - L4", "TRAM - L9", "TRAM - 4L", "TRAM - L2" };


	public static String[] DESC_LINEA = { "", "Luceros-Benidorm", "Luceros-El Campello", "Luceros-Pl. La Coruña", "Benidorm - Dénia", "Puerta del Mar-Sangueta", "Luceros-San Vicente" };
	
	public static String OBSERVACIONES_L9 = "** Actualmente la L9 no ofrece información de tiempos.";
	
	
	public static int[] TIPO = { 1, 2, 3, 4, 5, 6 };

	public static List<PlaceMark> posicionesRecorrido(String linea, List<PlaceMark> recorrido) {

		if (linea.equals("L1")) {
			for (int i = 0; i < L1_ORDEN.length; i++) {

				PlaceMark busqueda = new PlaceMark();
				busqueda.setCodigoParada(Integer.toString(L1_ORDEN[i]));

				try {
					recorrido.get(recorrido.lastIndexOf(busqueda)).setOrden(i);
				} catch (Exception e) {
					Log.d("", L1_ORDEN[i] + " :L1");
				}

			}
		} else if (linea.equals("L3")) {
			for (int i = 0; i < L3_ORDEN.length; i++) {

				PlaceMark busqueda = new PlaceMark();
				busqueda.setCodigoParada(Integer.toString(L3_ORDEN[i]));

				try {
					recorrido.get(recorrido.lastIndexOf(busqueda)).setOrden(i);
				} catch (Exception e) {
					Log.d("", L3_ORDEN[i] + " :L3");
				}

			}
		} else if (linea.equals("L4")) {
			for (int i = 0; i < L4_ORDEN.length; i++) {

				PlaceMark busqueda = new PlaceMark();
				busqueda.setCodigoParada(Integer.toString(L4_ORDEN[i]));

				try {
					recorrido.get(recorrido.lastIndexOf(busqueda)).setOrden(i);
				} catch (Exception e) {
					Log.d("", L4_ORDEN[i] + " :L4");
				}

			}
		} else if (linea.equals("L9")) {
			for (int i = 0; i < L9_ORDEN.length; i++) {

				PlaceMark busqueda = new PlaceMark();
				busqueda.setCodigoParada(Integer.toString(L9_ORDEN[i]));

				try {
					recorrido.get(recorrido.lastIndexOf(busqueda)).setOrden(i);
				} catch (Exception e) {
					Log.d("", L9_ORDEN[i] + " :L9");
				}

			}
		} else if (linea.equals("4L")) {
			for (int i = 0; i < L4L_ORDEN.length; i++) {

				PlaceMark busqueda = new PlaceMark();
				busqueda.setCodigoParada(Integer.toString(L4L_ORDEN[i]));

				try {
					recorrido.get(recorrido.lastIndexOf(busqueda)).setOrden(i);
				} catch (Exception e) {
					Log.d("", L4L_ORDEN[i] + " :L4L");
				}

			}
		} else if (linea.equals("L2")) {
			for (int i = 0; i < L2_ORDEN.length; i++) {

			PlaceMark busqueda = new PlaceMark();
			busqueda.setCodigoParada(Integer.toString(L2_ORDEN[i]));

			try {
				recorrido.get(recorrido.lastIndexOf(busqueda)).setOrden(i);
			} catch (Exception e) {
				Log.d("", L2_ORDEN[i] + " :L2");
			}

		}
	}

		return recorrido;
	}

	/**
	 * Buscar la posicion de la linea seleccionada
	 * 
	 * @param buscar
	 * @return
	 */
	public static int getIdLinea(String buscar) {

		for (int i = 0; i < LINEAS_NUM.length; i++) {

			if (LINEAS_NUM[i].equals(buscar)) {
				return i;
			}

		}

		return -1;

	}

}
