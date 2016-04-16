/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2014 Alberto Montiel
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
package alberapps.java.tram;

import android.util.Log;

import java.util.List;

import alberapps.java.tam.mapas.PlaceMark;

/**
 * Informacion constante para las funciones del tram
 */
public class UtilidadesTRAM {

    public static boolean ACTIVADO_TRAM = true;

    public static boolean ACTIVADO_L9 = true;

    // Para la carga en BD
    public static String[] LINEAS_NUM = {"L1", "L3", "L4", "L9", "4L", "L2"};

    // Para el servicio de consultas
    public static String[] LINEAS_A_CONSULTAR = {"L1", "L3", "L4", "L2"};

    public static int[] L1_ORDEN_SIN_L3 = {2, 3, 4, 5, 6, 8, 17, 19, 20, 21, 22, 25, 26, 27, 28, 29, 30, 31, 32, 33};

    public static int[] L1_ORDEN_CAMPELLO = {17, 19, 20, 21, 22, 25, 26, 27, 28, 29, 30, 31, 32, 33};

    // Con L3 fines de semana
    public static int[] L1_ORDEN = {2, 3, 4, 5, 6, 7, 8, 9, 51, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22, 25, 26, 27, 28, 29, 30, 31, 32, 33};

    public static int[] L3_ORDEN = {2, 3, 4, 5, 6, 7, 8, 9, 51, 10, 11, 12, 13, 14, 15, 16, 17};

    public static int[] L4_ORDEN = {2, 3, 4, 5, 6, 7, 8, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113};

    public static int[] L9_ORDEN = {33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50};

    public static int[] L4L_ORDEN = {101, 102, 5};

    public static int[] L2_ORDEN = {2, 3, 4, 1201, 1202, 1203, 1204, 1205, 1206, 1207, 1208, 1209, 1210, 1211};

    public static int[] L2_ORDEN_HORARIOS = {2, 3, 4, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124};

    public static String[] DESC_TIPO = {"", "TRAM - L1", "TRAM - L3", "TRAM - L4", "TRAM - L9", "TRAM - 4L", "TRAM - L2"};

    public static String[] DESC_LINEA = {"", "Luceros-Benidorm", "Luceros-El Campello", "Luceros-Pl. La Coruña", "Benidorm - Dénia", "Puerta del Mar-Sangueta", "Luceros-San Vicente"};

    public static String OBSERVACIONES_L9 = "L9: Actualmente en fase de implantación.";

    public static String OBSERVACIONES_L1 = "L1: Realiza parada los fines de semana y festivos.";

    public static int[] TIPO = {1, 2, 3, 4, 5, 6};

    public static final String URL_DOCS = "https://docs.google.com/gview?embedded=true&url=";
    public static final String[] PDF_URL = {"http://www.tramalicante.es/descargas/pdf/L1%20L3%20a%20Campello%20y%20Benidorm.pdf",
            "http://www.tramalicante.es/descargas/pdf/L1%20L3%20a%20Luceros%20%28Alicante%29.pdf", "http://www.tramalicante.es/descargas/pdf/Horario%20L2.pdf",
            "http://www.tramalicante.es/descargas/pdf/Horario%20L4.pdf", "http://www.tramalicante.es/descargas/pdf/Horario%20L9.pdf"};


    public static final String CODIGO_TRAM_LONDRES = "109";

    public static final String CODIGO_TRAM_LUCEROS = "2";

    public static final String CODIGO_TRAM_MERCADO = "3";

    public static final String CODIGO_TRAM_BENIDORM = "33";

    public static final String L2_SANTVICENT = "SANT VICENT";

    public static final Integer[] HORARIOS_COD_ESTACION = {7, 2, 49, 38, 20, 108, 106, 33, 44, 115, 31, 107, 22, 42, 35, 51, 40, 11, 121, 9, 10, 29, 21, 28, 50, 34, 37, 17, 14, 43, 116, 39, 119, 46, 113, 117, 48, 30, 111, 36, 114, 6, 27, 47, 13, 109, 8, 118, 4, 3, 103, 12, 41, 112, 26, 16, 110, 19, 15, 5, 124, 122, 104, 32, 45, 105, 123, 25, 120};


    /**
     * Ordena las posiciones del recorrido del tram
     *
     * @param linea
     * @param recorrido
     * @return lista
     */
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

    /**
     * Carga de observaciones en BD
     *
     * @param parada
     * @return observacion
     */
    public static String getObservacionesL1(String parada) {

        for (int i = 0; i < L1_ORDEN_SIN_L3.length; i++) {
            if (parada.equals(Integer.toString(L1_ORDEN_SIN_L3[i]))) {
                return "";
            }

        }

        for (int i = 0; i < L1_ORDEN_CAMPELLO.length; i++) {
            if (parada.equals(Integer.toString(L1_ORDEN_CAMPELLO[i]))) {
                return "";
            }

        }

        return OBSERVACIONES_L1;

    }

    /**
     * Observaciones fijas
     *
     * @param parada
     * @return string
     */
    public static String getObservacionesL1enL4(String parada) {

        if (parada.equals("7")) {
            return OBSERVACIONES_L1;
        } else {
            return "";
        }

    }

    /**
     * La linea pertenece al tram
     *
     * @param linea
     * @return boolean
     */
    public static boolean esLineaTram(String linea) {

        for (int i = 0; i < LINEAS_NUM.length; i++) {

            if (LINEAS_NUM[i].equals(linea)) {
                return true;
            }

        }

        return false;

    }

    /**
     * Es una parada de la L9
     *
     * @param parada
     * @return boolean
     */
    public static boolean esParadaL9(String parada) {

        for (int i = 1; i < L9_ORDEN.length; i++) {
            if (parada.equals(Integer.toString(L9_ORDEN[i]))) {
                return true;
            }

        }

        return false;

    }


    /**
     * Es una parada de la L2
     *
     * @param parada
     * @return boolean
     */
    public static boolean esParadaL2(String parada) {

        for (int i = 0; i < L2_ORDEN.length; i++) {
            if (parada.equals(Integer.toString(L2_ORDEN[i]))) {
                return true;
            }

        }

        return false;

    }

    /**
     * Es una parada de la L3
     *
     * @param parada
     * @return boolean
     */
    public static boolean esParadaL3(String parada) {

        for (int i = 0; i < L3_ORDEN.length; i++) {
            if (parada.equals(Integer.toString(L3_ORDEN[i]))) {
                return true;
            }

        }

        return false;

    }

    /**
     * Es una parada de la L1
     *
     * @param parada
     * @return boolean
     */
    public static boolean esParadaL1(String parada) {

        for (int i = 0; i < L1_ORDEN.length; i++) {
            if (parada.equals(Integer.toString(L1_ORDEN[i]))) {
                return true;
            }

        }

        return false;

    }

    /**
     * Es una parada de la L4
     *
     * @param parada
     * @return boolean
     */
    public static boolean esParadaL4(String parada) {

        for (int i = 0; i < L4_ORDEN.length; i++) {
            if (parada.equals(Integer.toString(L4_ORDEN[i]))) {
                return true;
            }

        }

        return false;

    }


    /**
     * Codigo de parada para tener en cuenta que en horarios los codigos de la L2 cambian
     *
     * @param parada
     * @return
     */
    public static Integer getParadaParaHorarios(Integer parada){


        for (int i = 0; i < L2_ORDEN.length; i++) {
            if (parada.equals(L2_ORDEN[i])) {
                return L2_ORDEN_HORARIOS[i];
            }

        }

        return parada;

    }


}
