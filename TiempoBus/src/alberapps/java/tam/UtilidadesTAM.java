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
package alberapps.java.tam;

public class UtilidadesTAM {
	
	public static String[] LINEAS_DESCRIPCION = {"21 ALICANTE-P.S.JUAN-EL CAMPELLO","22 ALICANTE-C. HUERTAS-P.S. JUAN","23 ALICANTE-SANT JOAN-MUTXAMEL","24 ALICANTE-UNIVERSIDAD-S.VICENTE","25 ALICANTE-VILLAFRANQUEZA","26 ALICANTE-VILLAFRANQUEZA-TANGEL","27 ALICANTE-URBANOVA","30 SAN VICENTE-EL REBOLLEDO","C55 EL CAMPELLO-UNIVERSIDAD","34 LANZADERA UNIVERSIDAD","35 ALICANTE-PAULINAS-MUTXAMEL","36 SAN GABRIEL-UNIVERSIDAD","37 PADRE ESPLA-UNIVERSIDAD","38 P.S.JUAN-H.ST.JOAN-UNIVERSIDAD","39 EXPLANADA - C. TECNIFICACIÓN","21N ALICANTE- P.S.JUAN-EL CAMPELLO","22N ALICANTE- PLAYA SAN JUAN","23N ALICANTE- MUTXAMEL","24N ALICANTE-UNIVERSIDAD-S.VICENTE","01 S. GABRIEL-JUAN XXIII  (1ºS)","02 LA FLORIDA-SAGRADA FAMILIA","03 CIUDAD DE ASIS-COLONIA REQUENA","04 CEMENTERIO-TOMBOLA","05 EXPLANADA-SAN BLAS-RABASA","06 E.AUTOBUSES - COLONIA REQUENA","07 AV.ÓSCAR ESPLÁ-REBOLLEDO","8A VIRGEN DEL REMEDIO-EXPLANADA","09 AV.OSCAR ESPLA - AV. NACIONES","10 EXPLANADA-C.C. VISTAHERMOSA","11 PZ.LUCEROS-AV. DENIA-H.ST.JOAN","11H PZ.LUCEROS-H.ST JOAN","12 AV. CONSTITUCION-S. BLAS(PAUI)","16 PZA. ESPAÑA-MERCADILLO TEULADA","17 ZONA NORTE-MERCADILLO TEULADA","8B EXPLANADA-VIRGEN DEL REMEDIO","191 PLA - CAROLINAS - RICO PEREZ","192 C. ASIS - BENALUA - RICO PEREZ","M MUTXAMEL-URBANITZACIONS","C2 VENTA LANUZA - EL CAMPELLO","C51 MUTXAMEL - BUSOT","C52 BUSOT - EL CAMPELLO","C53 HOSPITAL SANT JOAN - EL CAMPELLO","C54 UNIVERSIDAD-HOSP. SANT JOAN","C6 ALICANTE-AEROPUERTO","45 HOSPITAL-GIRASOLES-MANCHEGOS","46A HOSPITAL-VILLAMONTES-S.ANTONIO","46B HOSPITAL-P.CANASTELL-P.COTXETA","TURI BUS TURÍSTICO (TURIBUS)"};
	
	public static String[] LINEAS_CODIGO_KML = {"ALC21","ALC22","ALC23","ALC24","ALC25","ALC26","ALC27","ALC30","ALCC55","ALC34","ALC35","ALC36","ALC37","ALC38","ALC39","ALC21N","ALC22N","ALC23N","ALC24N","MAS01","MAS02","MAS03","MAS04","MAS05","MAS06","MAS07","MAS8A","MAS09","MAS10","MAS11","MAS11H","MAS12","MAS16","MAS17","MAS8B","MAS191","MAS192","MUTM","CAMPC2","ALCC51","ALCC52","ALCC53","ALCC54","ALCC6","ALCS45","ALCS46A","ALCS46B","Turibus"};
	
	public static String[] LINEAS_NUM = {"21","22","23","24","25","26","27","30","C55","34","35","36","37","38","39","21N","22N","23N","24N","01","02","03","04","05","06","07","8A","09","10","11","11H","12","16","17","8B","191","192","M","C2","C51","C52","C53","C54","C6","S45","S46A","S46B","TURI"};
		
	
	
	//public static String[] LINEAS_DESCRIPCION = {"21 ALICANTE-P.S.JUAN-EL CAMPELLO","22 ALICANTE-C. HUERTAS-P.S. JUAN","23 ALICANTE-SANT JOAN-MUTXAMEL","24 ALICANTE-UNIVERSIDAD-S.VICENTE","25 ALICANTE-VILLAFRANQUEZA","26 ALICANTE-VILLAFRANQUEZA-TANGEL","27 ALICANTE-URBANOVA","30 SAN VICENTE-EL REBOLLEDO","31 MUTXAMEL-ST.JOAN-PLAYA S. JUAN","32 EL CAMPELLO-UNIVERSIDAD","30P SAN VICENTE-PLAYA SAN JUAN","34 LANZADERA UNIVERSIDAD","35 ALICANTE-PAULINAS-MUTXAMEL","36 SAN GABRIEL-UNIVERSIDAD","37 PADRE ESPLA-UNIVERSIDAD","38 P.S.JUAN-H.ST.JOAN-UNIVERSIDAD","39 EXPLANADA - C. TECNIFICACIÓN","87 APEADERO RENFE-UNIVERSIDAD","21N ALICANTE- P.S.JUAN-EL CAMPELLO","22N ALICANTE- PLAYA SAN JUAN","23N ALICANTE- MUTXAMEL","24N ALICANTE-UNIVERSIDAD-S.VICENTE","01 S. GABRIEL-JUAN XXIII  (1ºS)","02 LA FLORIDA-SAGRADA FAMILIA","03 CIUDAD DE ASIS-COLONIA REQUENA","04 CEMENTERIO-TOMBOLA","05 EXPLANADA-SAN BLAS-RABASA","06 E.AUTOBUSES - COLONIA REQUENA","07 AV.ÓSCAR ESPLÁ-REBOLLEDO","8A VIRGEN DEL REMEDIO-EXPLANADA","09 E.AUTOBUSES - PLAYA SAN JUAN","10 EXPLANADA-C.C. VISTAHERMOSA","11 PZ.LUCEROS-AV. DENIA-H.ST.JOAN","12 AV. CONSTITUCION-S. BLAS(PAUI)","16 PZA. ESPAÑA-MERCADILLO TEULADA","17 ZONA NORTE-MERCADILLO TEULADA","8B EXPLANADA-VIRGEN DEL REMEDIO","191 PLA - CAROLINAS - RICO PEREZ","192 C. ASIS - BENALUA - RICO PEREZ","M MUTXAMEL-URBANITZACIONS","C1 VENTA LANUZA CIRCULAR","C2 VENTA LANUZA - EL CAMPELLO","C-51 MUTXAMEL - BUSOT","C-52 BUSOT - EL CAMPELLO","C-53 HOSPITAL SANT JOAN - EL CAMPELLO","C-54 UNIVERSIDAD DE ALICANTE - MUTXAMEL SANT JOAN - HOSPITAL DE SANT JOAN","C6 ALICANTE-AEROPUERTO","45 HOSPITAL-GIRASOLES-MANCHEGOS","46A HOSPITAL-VILLAMONTES-S.ANTONIO","46B HOSPITAL-P.CANASTELL-P.COTXETA","TURI BUS TURÍSTICO (TURIBUS)","VTC VIVE TU CASTILLO"};
	//public static String[] LINEAS_CODIGO_KML = {"ALC21","ALC22","ALC23","ALC24","ALC25","ALC26","ALC27","ALC30","ALC31","ALC32","ALC30B","ALC34","ALC35","ALC36","ALC37","ALC38","ALC39","ALC87","ALC21N","ALC22N","ALC23N","ALC24N","MAS01","MAS02","MAS03","MAS04","MAS05","MAS06","MAS07","MAS8A","MAS09","MAS10","MAS11","MAS12","MAS16","MAS17","MAS8B","MAS191","MAS192","MUTM","CAMPC1","CAMPC2","ALCC51","ALCC52","ALCC53","ALCC54","ALCC6","ALCS45","ALCS46A","ALCS46B","Turibus","VIVECASTILLO"};
	//public static String[] LINEAS_NUM = {"21","22","23","24","25","26","27","30","31","32","30B","34","35","36","37","38","39","87","21N","22N","23N","24N","01","02","03","04","05","06","07","8A","09","10","11","12","16","17","8B","191","192","M","C1","C2","C51","C52","C53","C54","C6","S45","S46A","S46B","TURI","VTC"};
	
	
	//MICRO SERVICIO DE MICRO - sin datos
	
	//34L -> C54, 20C -> C53
	//31 VERANO
	
	//retiradas 31(temporada) 30P(temporada) VTC(eliminada) C1(eliminada) 87(eliminada) añadida 11H
	
	/**
	 * KML Paradas ida
	 * 
	 * @param codigo
	 * @return
	 */
	public static String getKMLParadasIda(String codigo){
		
		String kml = null;
		
		if(codigo != null && !codigo.equals("")){
			
			kml = "http://www.subus.es/Lineas/kml/" + codigo + "ParadasIda.xml";
			
		}
		
		return kml;
	}
	
	/**
	 * KML Paradas vuelta
	 * 
	 * @param codigo
	 * @return
	 */
	public static String getKMLParadasVuelta(String codigo){
		
		String kml = null;
		
		if(codigo != null && !codigo.equals("")){
			
			kml = "http://www.subus.es/Lineas/kml/" + codigo + "ParadasVuelta.xml";
			
		}
		
		return kml;
	}
	
	
	public static String getKMLRecorridoIda(String codigo){
		
		String kml = null;
		
		if(codigo != null && !codigo.equals("")){
			
			kml = "http://www.subus.es/Lineas/kml/" + codigo + "RecorridoIda.xml";
			
		}
		
		return kml;
	}
	
	public static String getKMLRecorridoVuelta(String codigo){
		
		String kml = null;
		
		if(codigo != null && !codigo.equals("")){
			
			kml = "http://www.subus.es/Lineas/kml/" + codigo + "RecorridoVuelta.xml";
			
		}
		
		return kml;
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
	
	public static int getNumLinea(String buscar){
		
		for(int i = 0; i< LINEAS_CODIGO_KML.length;i++){
			
			if(LINEAS_CODIGO_KML[i].equals(buscar)){
				return i;
			}
			
		}
		
		return -1;
		
	}
	
	
	
	
}
