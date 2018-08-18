/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2014 Alberto Montiel
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.java.tram.webservice;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import alberapps.java.tram.DatosTRAM;
import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

public class GetPasoParadaXmlWebservice {

    public static final int URL1 = 1;
    public static final int URL2 = 2;

    /**
     * Consulta del servicioWeb y mapeo de la respuesta
     *
     * @param linea
     * @param parada
     * @return
     * @throws Exception
     */
    public GetPasoParadaResult consultarServicio(String linea, String parada, int consulta) throws Exception {

        InputStream is = null;

        GetPasoParadaResult resultados = new GetPasoParadaResult();

        String url = null;

        if (consulta == 1) {
            url = DatosTRAM.URL_1_DINAMICA;
        } else {
            url = DatosTRAM.URL_2_DINAMICA;
        }

        try {

            is = Utilidades.stringToStream(Conectividad.conexionPostUtf8(url, datosPost(linea, parada), false));

            if (is != null) {

                resultados = parse(is);

            } else {

                // resultados

            }

        } catch (Exception e) {

            Log.d("webservice", "Error consulta tiempos: " + linea + " - " + parada);

            e.printStackTrace();

            try {

                is.close();
            } catch (Exception ex) {

            }

            // Respuesta no esperada del servicio
            throw e;

        } finally {
            try {

                is.close();
            } catch (Exception e) {

            }
        }

        return resultados;

    }

    /**
     * Parsear entrada
     *
     * @param is
     * @return
     */
    public GetPasoParadaResult parse(InputStream is) {
        // Instanciamos la fábrica para DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<PasoParada> pasoParadaList = new ArrayList<>();

        GetPasoParadaResult resultados = new GetPasoParadaResult();

        try {
            // Creamos un nuevo parser DOM
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Realizamos lalectura completa del XML
            Document dom = builder.parse(is);

            // Nos posicionamos en el nodo principal del árbol (<kml>)
            Element root = dom.getDocumentElement();

            // Folder principal
            NodeList pasoParadaResultList = root.getElementsByTagName("PasoParada");

            for (int i = 0; i < pasoParadaResultList.getLength(); i++) {

                // pasoParada
                Node pasoParada = pasoParadaResultList.item(i);

                NodeList datosParada = pasoParada.getChildNodes();

                // e1 minutos
                String minutos1 = datosParada.item(1).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();

                // e2 minutos
                String minutos2 = datosParada.item(2).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();

                // linea
                String linea = datosParada.item(3).getChildNodes().item(0).getNodeValue();

                // parada
                String parada = datosParada.item(4).getChildNodes().item(0).getNodeValue();

                // ruta
                String ruta = datosParada.item(6).getChildNodes().item(0).getNodeValue();

                PasoParada pasoP = new PasoParada();

                pasoP.getE1().setMinutos(getFormatoTiempoEspera(minutos1));

                pasoP.getE2().setMinutos(getFormatoTiempoEspera(minutos2));

                pasoP.setLinea(linea);
                pasoP.setParada(parada);
                pasoP.setRuta(ruta);

                pasoParadaList.add(pasoP);

            }

            //NodeList statusList = root.getElementsByTagName("status");

            // Status
            //String status = statusList.item(0).getChildNodes().item(0).getNodeValue();

            //resultados.setStatus(status);

            resultados.setPasoParadaList(pasoParadaList);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return resultados;
    }

    /**
     * Construir post con parada o con linea-parada
     *
     * @param linea
     * @param parada
     * @return string
     */
    private String datosPost(String linea, String parada) {

        StringBuilder sr = new StringBuilder(150);
        sr.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        sr.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        sr.append("<soap:Body> <GetPasoParada xmlns=\"http://tempuri.org/\">");

        // Linea
        if (linea != null && !linea.equals("")) {
            sr.append("<linea>");
            sr.append(linea);
            sr.append("</linea>");
        }

        // '<linea>24</linea>'+
        // '<parada>4450</parada>'+

        // Parada
        sr.append("<parada>");
        sr.append(parada);
        sr.append("</parada>");

        sr.append("<status>0</status>");
        sr.append("</GetPasoParada> </soap:Body> </soap:Envelope>");

        return sr.toString();

    }

    /********************************************************************/

    /**
     * Forma string con los minutos faltantes y la hora aproximada de llegada
     *
     * @param minutosLlegada
     * @return
     */
    private String getFormatoTiempoEspera(String minutosLlegada) {

        String formatoMinHora = "";

        GregorianCalendar cl = new GregorianCalendar();
        cl.setTimeInMillis((new Date()).getTime());
        cl.add(Calendar.MINUTE, Integer.parseInt(minutosLlegada));

        SimpleDateFormat sf = new SimpleDateFormat("HH:mm", Locale.US);
        String horaString = sf.format(cl.getTime());

        formatoMinHora = minutosLlegada + " min. (" + horaString + ")";

        return formatoMinHora;

    }

}
