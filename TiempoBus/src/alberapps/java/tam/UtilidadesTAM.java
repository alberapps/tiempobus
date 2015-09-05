/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.java.tam;

import java.util.Arrays;
import java.util.List;

/**
 * Datos utiles del bus
 */
public class UtilidadesTAM {

    public static String[] LINEAS_DESCRIPCION = {"21 ALICANTE-P.S.JUAN-EL CAMPELLO", "22 ALICANTE-C. HUERTAS-P.S. JUAN", "23 ALICANTE-SANT JOAN-MUTXAMEL", "24 ALICANTE-UNIVERSIDAD-S.VICENTE", "25 ALICANTE-VILLAFRANQUEZA", "26 ALICANTE-VILLAFRANQUEZA-TANGEL", "27 ALICANTE(O.ESPLA) - URBANOVA", "30 SAN VICENTE-LA ALCORAYA", "C-55 EL CAMPELLO-UNIVERSIDAD", "35 ALICANTE-PAULINAS-MUTXAMEL", "36 SAN GABRIEL-UNIVERSIDAD", "38 P.S.JUAN-H.ST.JOAN-UNIVERSIDAD", "39 EXPLANADA - C. TECNIFICACIÓN", "21N ALICANTE- P.S.JUAN-EL CAMPELLO", "22N ALICANTE- PLAYA SAN JUAN", "23N ALICANTE-SANT JOAN- MUTXAMEL", "24N ALICANTE-UNIVERSIDAD-S.VICENTE", "01 S. GABRIEL-JUAN XXIII  (1ºS)", "02 LA FLORIDA-SAGRADA FAMILIA", "03 CIUDAD DE ASIS-COLONIA REQUENA", "04 CEMENTERIO-TOMBOLA", "05 EXPLANADA-SAN BLAS-RABASA", "06 AV.ÓSCAR ESPLÁ - COLONIA REQUENA", "07 AV.ÓSCAR ESPLÁ-EL REBOLLEDO", "08 EXPLANADA -VIRGEN REMEDIO", "09 AV.ÓSCAR ESPLÁ - AV. NACIONES", "10 EXPLANADA - VIA PARQUE", "11 V.REMEDIO-AV DENIA (JESUITAS)", "11H V.REMEDIO-AV. DENIA-HOSP.ST JOAN", "12 AV. CONSTITUCION-S. BLAS(PAUI)", "16 PZA. ESPAÑA-MERCADILLO TEULADA", "17 ZONA NORTE-MERCADILLO TEULADA", "191 PLA - CAROLINAS - RICO PEREZ", "192 C. ASIS - BENALUA - RICO PEREZ", "M MUTXAMEL-URBANITZACIONS", "136 MUTXAMEL - CEMENTERIO", "C2 VENTA LANUZA - EL CAMPELLO", "C-51 MUTXAMEL - BUSOT", "C-52 BUSOT - EL CAMPELLO", "C-53 HOSPITAL SANT JOAN - EL CAMPELLO", "C-54 UNIVERSIDAD-HOSP. SANT JOAN", "C-6 ALICANTE-AEROPUERTO", "45 HOSPITAL-GIRASOLES-MANCHEGOS", "46A HOSPITAL-VILLAMONTES-S.ANTONIO", "46B HOSPITAL-P.CANASTELL-P.COTXETA", "TURI BUS TURÍSTICO (TURIBUS)", "31 MUTXAMEL-ST.JOAN-PLAYA S. JUAN", "30P SAN VICENTE-PLAYA SAN JUAN", "C-6* ALICANTE-URBANOVA-AEROPUERTO", "123 ESPECIAL SANTA FAZ", "13 ALICANTE - VILLAFRANQUEZA", "SE ESPECIAL SAN VICENTE", "TRANSPORTE URBANO XIXONA"};

    //public static String[] LINEAS_CODIGO_KML = {"ALC21", "ALC22", "ALC23", "ALC24", "ALC25", "ALC26", "ALC27", "ALC30", "ALCC55", "ALC35", "ALC36", "ALC38", "ALC39", "ALC21N", "ALC22N", "ALC23N", "ALC24N", "MAS01", "MAS02", "MAS03", "MAS04", "MAS05", "MAS06", "MAS07", "MAS8A", "MAS09", "MAS10", "MAS11", "MAS11H", "MAS12", "MAS16", "MAS17", "MAS8B", "MAS191", "MAS192", "MUTM", "MUT136", "CAMPC2", "ALCC51", "ALCC52", "ALCC53", "ALCC54", "ALCC6", "ALCS45", "ALCS46A", "ALCS46B", "Turibus", "ALC31", "ALC30B", "ALCC6","","","",""};

    public static String[] LINEAS_NUM = {"21", "22", "23", "24", "25", "26", "27", "30", "C-55", "35", "36", "38", "39", "21N", "22N", "23N", "24N", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "11H", "12", "16", "17", "191", "192", "M", "136", "C2", "C-51", "C-52", "C-53", "C-54", "C-6", "45", "46A", "46B", "TURI", "31", "30P", "C-6*", "123", "13", "SE","U-1","13N"};

    public static int[] TIPO = {14, 14, 2, 2, 2, 2, 14, 14, 4, 2, 4, 4, 1, 8, 15, 8, 8, 1, 14, 1, 1, 14, 1, 1, 1, 14, 1, 1, 1, 1, 3, 3, 5, 5, 9, 9, 7, 10, 10, 10, 4, 10, 11, 11, 11, 12, 6, 6, 10, 12, 1, 12, 13, 15};

	/*
     * Eliminada nov 2013: 37; 25N
	 */
	
	/*
	 * Eliminar: 34
	 * Problemas: 30P, 
	 * 
	 */
	
	/*
	 * 1: ALICANTE URBANO TAM: TODO EL AÑO
	 * 2: ALICANTE INTERURBANO TAM: TODO EL AÑO
	 * 3: ALICANTE URBANO TAM: ESPECIALES MERCADILLO
	 * 4: ALICANTE INTERURBANO TAM: UNIVERSIDAD / ESCOLARES
	 * 5: ALICANTE URBANO TAM: ESPECIALES FÚTBOL
	 * 6: ALICANTE INTERURBANO TAM: SERVICIOS VERANO
	 * 7: EL CAMPELLO URBANO
	 * 8: ALICANTE INTERURBANO TAM: NOCTURNOS
	 * 9: MUTXAMEL URBANO
	 * 10: ALICANTE INTERURBANO
	 * 11: SAN VICENTE URBANO
	 * 12: ESPECIALES 
	 *
	 *
	 * 13: XIXONA URBANO
	 * 14: PLAYAS
	 * 15: URBANO TAM NOCTURNOS
	 *
	 */

    public static String[] DESC_TIPO = {"", "ALICANTE URBANO TAM: TODO EL AÑO", "ALICANTE INTERURBANO TAM: TODO EL AÑO", "ALICANTE URBANO TAM: ESPECIALES MERCADILLO", "ALICANTE INTERURBANO TAM: UNIVERSIDAD / ESCOLARES", "ALICANTE URBANO TAM: ESPECIALES FÚTBOL", "ALICANTE INTERURBANO TAM: SERVICIOS VERANO", "EL CAMPELLO URBANO", "ALICANTE INTERURBANO TAM: NOCTURNOS", "MUTXAMEL URBANO", "ALICANTE INTERURBANO", "SAN VICENTE URBANO", "ESPECIALES", "XIXONA URBANO", "PLAYAS", "URBANO TAM NOCTURNOS"};


    //MICRO SERVICIO DE MICRO - sin datos

    //34L -> C54, 20C -> C53
    //31 VERANO

    //retiradas 31(temporada) 30P(temporada) VTC(eliminada) C1(eliminada) 87(eliminada) añadida 11H

    //Nueva
    //136

    //Eliminada
    //8B

    /*public static String[] LINEAS_AZUL_TAM = {"21","22","23","24","25","26","27","30","31","35","21N","22N","23N","24N","36","38","39","30P","31"};
    public static String[] LINEAS_AZUL_INTER = {"C-6","C-51","C-52", "C-53","C-54","C-55"};
    public static String[] LINEAS_AZUL_CAMPELLO = {"C2"};
    public static String[] LINEAS_AZUL_MUTXAMEL = {"M","136"};
    public static String[] LINEAS_AZUL_SANTVICENT = {"45","46A","46B"};*/
    public static String[] LINEAS_URBANAS = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "11H", "12", "13", "16", "17", "191", "192", "22", "22N", "27", "39", "45", "13N"};

    //public static List<String> lineasAzules = Arrays.asList(LINEAS_AZUL);
    public static List<String> lineasUrbanas = Arrays.asList(LINEAS_URBANAS);

    /**
     * Si es urbano
     *
     * @param linea
     * @return
     */
    public static boolean isBusUrbano(String linea) {

        if (lineasUrbanas.contains(linea)) {
            return true;
        } else {
            return false;
        }

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
