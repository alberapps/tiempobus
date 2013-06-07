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

public class UtilidadesTRAM {

	public static String[] LINEAS_NUM = {"L1","L3","L4","L9","4L"};
	
	
	
	public static int[] L1_ORDEN = { 2,3,4,5,6,8,17,19,20,21,22,25,26,27,28,29,30,31,32,33 };
	
	public static int[] L3_ORDEN = { 2,3,4,5,6,7,8,9,51,17,19,20,21,22,25,26,27,28,29,30,31,32,33 };

	
	public static String[] DESC_TIPO = {"","Luceros-Benidorm","Luceros-El Campello","Luceros-Pl. La Coruña","Benidorm - Dénia","Puerta del Mar-Sangueta"};
	
	public static int[] TIPO = {1,2,3,4,5};
	
	
	public static List<PlaceMark> posicionesRecorrido(String linea, List<PlaceMark> recorrido) {

		if ( linea.equals("L1")) {
			for (int i = 0; i < L1_ORDEN.length; i++) {

				PlaceMark busqueda = new PlaceMark();
				busqueda.setCodigoParada(Integer.toString(L1_ORDEN[i]));

				try{
					recorrido.get(recorrido.lastIndexOf(busqueda)).setOrden(i);
				}catch(Exception e){
					
				}
				
				
			}
		}else if ( linea.equals("L3")) {
			for (int i = 0; i < L3_ORDEN.length; i++) {

				PlaceMark busqueda = new PlaceMark();
				busqueda.setCodigoParada(Integer.toString(L3_ORDEN[i]));

				try{
					recorrido.get(recorrido.lastIndexOf(busqueda)).setOrden(i);
				}catch(Exception e){
					
				}
				
				
			}
		}
		
		return recorrido;
	}

	
	/**
	 * Buscar la posicion de la linea seleccionada
	 * @param buscar
	 * @return
	 */
	public static int getIdLinea(String buscar){
		
		for(int i = 0; i< LINEAS_NUM.length;i++){
			
			if(LINEAS_NUM[i].equals(buscar)){
				return i;
			}
			
		}
		
		return -1;
		
	}
	
}
