/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
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
package alberapps.java.tram.linea9;

import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alberapps.java.tam.BusLlegada;
import alberapps.java.tram.DatosTRAM;
import alberapps.java.util.Conectividad;

/**
 * Procesa los datos recuperados de las lineas
 */
public class ProcesarTiemposTramL9Texto {


    /**
     * Procesa tiempos
     *
     * @param parada
     * @return
     * @throws Exception
     */

    public static ArrayList<BusLlegada> procesaTiemposLlegada(int parada) throws Exception {

        ArrayList<BusLlegada> listaTrenes = new ArrayList<BusLlegada>();

        InputStream is = null;

        String url = DatosTRAM.URL_L9 + Integer.toString(parada);


        try {

            //is = Utilidades.recuperarStreamConexionSimpleDepuracion(contexto, url);

            is = Conectividad.conexionGetIsoStream(url, false, null);

            // TODO test
            // String test =
            // "L1 BENIDORM pr: < 1 min.\nL3 CAMPELLO: 9 min.\nL4 PL. CORUÑA: >60 min.";
            // String test =
            // "Sin Estimaciones para la Parada 18 y Linea *. Disculpe las molestias.";
            // String test =
            // "//L1 P.ESPAÑOL: En este momento no hay tranvias a menos de 60 min.";
            // String test =
            // "L4L SANGUETA: 22 min.siguiente -1 min.\nL4L SANGUETA: -1 min.";
            // String test = "L4 LUCEROS: 12 min.\n 42 min. ";

            // String test = "ERROR1 ERROR2 ERROR3 ";

            // is = new ByteArrayInputStream(test.getBytes("UTF-8"));

            if (is != null) {

                BufferedReader input = new BufferedReader(new InputStreamReader(is, HTTP.UTF_8));

                String l = "";

                ArrayList<String> lineasFichero = new ArrayList<String>();


                while ((l = input.readLine()) != null) {

                    lineasFichero.add(l);


                }


                listaTrenes = procesa(lineasFichero);

                if (listaTrenes == null || listaTrenes.isEmpty()) {


                } else {


                }

            } else {
                // listaTrenes = null;


            }

        } catch (MalformedURLException e) {


        } catch (IOException e) {


        } catch (Exception e) {

            // listaTrenes = null;


        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {

            }
        }

        // Collections.sort(buses);
        return listaTrenes;
    }

    /**
     * @param lineasFichero
     * @return
     */
    private static ArrayList<BusLlegada> procesa(List<String> lineasFichero) {

        ArrayList<BusLlegada> listaTren = null;

        if (lineasFichero != null && !lineasFichero.isEmpty()) {

            BusLlegada tren = null;

            for (int i = 0; i < lineasFichero.size(); i++) {

                String[] linea = lineasFichero.get(i).split(":");

                // Sin datos
                if (lineasFichero.size() == 1 && linea.length > 0 && linea.length < 2) {
                    return new ArrayList<BusLlegada>();
                } else if (linea.length < 2) {
                    // Segundo tiempo con salto de linea
                    // listaTren.get(i - 1).setProximo(listaTren.get(i -
                    // 1).getProximo() + " " + lineasFichero.get(i));
                    continue;
                }

                String dato = linea[0];
                String tiempo = linea[1];

                int dato_s = dato.indexOf(" ");

                String lineaNum = dato.substring(0, dato_s);
                String lineaDesc = dato.substring(dato_s);

                tren = new BusLlegada();

                tren.setLinea(lineaNum);
                tren.setDestino(lineaDesc);

                // Recupera tiempo y añade la hora
                // Si llega otro dato lo devuelve como esta
                String tiempoHora = procesarLineaFichero(tiempo);

                if (tiempoHora != null && !tiempoHora.equals("-1")) {

                    tren.setProximo("enlaparada;sinestimacion");
                    tren.cambiarProximo(Integer.parseInt(tiempoHora));

                    if (listaTren == null) {
                        listaTren = new ArrayList<BusLlegada>();
                    }

                    listaTren.add(tren);

                } else if (tiempoHora != null && tiempoHora.equals("-1")) {
                    // Descartar los que llegan con -1 min.
                    continue;

                } else {
                    //tiempoHora = tiempo.trim();
                }

                //tren.setProximo("TRAM;" + tiempoHora);


            }

            return listaTren;

        } else {
            return null;
        }

    }


    /**
     * @param lineaFichero
     * @return
     */
    public static String procesarLineaFichero(String lineaFichero) {

        //22  min.

        try {

            if (lineaFichero.trim().charAt(0) == '<') {
                return "0";
            } else if (lineaFichero.trim().charAt(0) == '>') {
                return "-1";
            } else if (lineaFichero.trim().charAt(0) == '-') {
                return "-1";
            } else if (lineaFichero.trim().charAt(0) == 'S') {
                return null;

            } else {
                Pattern p = Pattern.compile("([0-9]+)  min.");
                Matcher m = p.matcher(lineaFichero.trim());
                if (m.find()) {
                    return m.group(1);
                } else {
                    return null;
                }
            }

        } catch (Exception e) {
            return null;
        }

    }

    /**
     * @param tiempo
     * @return
     */
    public static String tiempoTRAM(String tiempo) {

        if (tiempo.trim().charAt(0) == '<') {
            return null;
        } else if (tiempo.trim().charAt(0) == '>') {
            return null;
        } else if (tiempo.trim().charAt(0) == '-') {
            return "-";
        } else if (tiempo.trim().charAt(0) == 'E') {
            return null;

        } else {
            Pattern p = Pattern.compile("([0-9]+) min.");
            Matcher m = p.matcher(tiempo);
            if (m.find()) {
                return m.group(1);
            } else {
                return null;
            }
        }

    }

    /**
     * Forma string con los minutos faltantes y la hora aproximada de llegada
     *
     * @param minutosLlegada
     * @return
     */
    private static String getFormatoTiempoEspera(String minutosLlegada) {

        String formatoMinHora = "";

        GregorianCalendar cl = new GregorianCalendar();
        cl.setTimeInMillis((new Date()).getTime());
        cl.add(Calendar.MINUTE, Integer.parseInt(minutosLlegada));

        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        String horaString = sf.format(cl.getTime());

        formatoMinHora = minutosLlegada + " min. (" + horaString + ")";

        return formatoMinHora;

    }


}
