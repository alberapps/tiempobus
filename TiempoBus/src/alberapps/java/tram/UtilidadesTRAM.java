package alberapps.java.tram;

import java.util.Collections;
import java.util.List;

import alberapps.java.tam.mapas.PlaceMark;

public class UtilidadesTRAM {

	public static int[] L1_ORDEN = { 2,3,4,5,6,8,17,19,20,21,22,25,26,27,28,29,30,31,32,33 };
	
	public static int[] L3_ORDEN = { 2,3,4,5,6,7,8,9,51,17,19,20,21,22,25,26,27,28,29,30,31,32,33 };

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

}
