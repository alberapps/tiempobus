/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
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
package alberapps.java.tam.webservice.dinamica;

import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.DatosTam;
import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

public class DinamicaPasoParadaParser {

    /**
     * Consulta del servicioWeb y mapeo de la respuesta
     *
     * @param linea
     * @param parada
     * @return
     * @throws Exception
     */
    public GetPasoParadaResult consultarServicio(String linea, String parada, Boolean cacheTiempos, boolean enableHttps) throws Exception {

        InputStream is = null;

        GetPasoParadaResult resultados = new GetPasoParadaResult();

        try {

            //is = Utilidades.stringToStream(Conectividad.conexionPostUtf8(URL, datosPost(linea, parada), cacheTiempos));

            Uri.Builder builder = Uri.parse(DatosTam.URL_SERVIDOR_DINAMICA_PASOPARADA).buildUpon();

            if(!enableHttps) {
                builder.scheme("http");
            }

            if (linea != null) {
                builder.appendQueryParameter("line", linea);
            }
            builder.appendQueryParameter("stop", parada);
            builder.build();

            String resp = Conectividad.conexionGetUtf8(builder.toString());

            if (resp != null && !resp.trim().equals("") && !resp.contains("005: Error del servicio")) {

                resp = resp.substring(resp.indexOf("<soap:Envelope"));
                is = Utilidades.stringToStream(resp);

                if (is != null) {

                    resultados = parse(is);

                } else {

                    // resultados

                }

            } else if (resp != null && !resp.trim().equals("") && resp.contains("005: Error del servicio")) {
                throw new TiempoBusException(TiempoBusException.ERROR_005_SERVICIO);
            } else {
                throw new TiempoBusException(TiempoBusException.ERROR_NO_DEFINIDO);
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


    private static final String ns = null;


    /**
     * @param in
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public GetPasoParadaResult parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag(); //soap:Envelope
            parser.nextTag(); //soap:Body
            return readPasoParadaResponse(parser);
        } finally {
            in.close();
        }


    }


    /**
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private GetPasoParadaResult readPasoParadaResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
        GetPasoParadaResult pasoParadaResult = new GetPasoParadaResult();

        parser.require(XmlPullParser.START_TAG, ns, "GetPasoParadaResponse");

        String status = null;
        List<PasoParada> pasoParadaList = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("GetPasoParadaResult")) {
                pasoParadaList = readGetPasoParadaResult(parser);
            } else if (name.equals("status")) {
                status = readText(parser);
            } else {
                skip(parser);
            }
        }
        return new GetPasoParadaResult(pasoParadaList, status);
    }


    /**
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<PasoParada> readGetPasoParadaResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<PasoParada> pasoParadaList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "GetPasoParadaResult");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("PasoParada")) {
                pasoParadaList.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return pasoParadaList;
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private PasoParada readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "PasoParada");
        InfoParada e1 = null;
        InfoParada e2 = null;
        String linea = null;
        String parada = null;
        String ruta = null;
        String sublinea = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("e1")) {
                e1 = readE1(parser);
            } else if (name.equals("e2")) {
                e2 = readE2(parser);
            } else if (name.equals("linea")) {
                linea = readLinea(parser);
            } else if (name.equals("parada")) {
                parada = readParada(parser);
            } else if (name.equals("ruta")) {
                ruta = readRuta(parser);
            } else if (name.equals("sublinea")) {
                sublinea = readSublinea(parser);
            } else {
                skip(parser);
            }
        }
        return new PasoParada(e1, e2, linea, parada, ruta, sublinea);
    }


    /**
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private InfoParada readE1(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "e1");
        String minutos = null;
        String metros = null;
        String tipo = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("minutos")) {
                minutos = readMinutos(parser);
            } else if (name.equals("metros")) {
                metros = readMetros(parser);
            } else if (name.equals("linea")) {
                tipo = readTipo(parser);
            } else {
                skip(parser);
            }
        }
        return new InfoParada(minutos, metros, tipo);
    }

    /**
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private InfoParada readE2(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "e2");
        String minutos = null;
        String metros = null;
        String tipo = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("minutos")) {
                minutos = readMinutos(parser);
            } else if (name.equals("metros")) {
                metros = readMetros(parser);
            } else if (name.equals("linea")) {
                tipo = readTipo(parser);
            } else {
                skip(parser);
            }
        }
        return new InfoParada(minutos, metros, tipo);
    }


    // Processes title tags in the feed.
    private String readMinutos(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "minutos");
        String minutos = getFormatoTiempoEspera(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "minutos");
        return minutos;
    }

    // Processes title tags in the feed.
    private String readMetros(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "metros");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "metros");
        return title;
    }

    // Processes title tags in the feed.
    private String readTipo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "tipo");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "tipo");
        return title;
    }


    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Processes title tags in the feed.
    private String readLinea(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "linea");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "linea");
        return title;
    }

    // Processes title tags in the feed.
    private String readParada(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "parada");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "parada");
        return title;
    }

    // Processes title tags in the feed.
    private String readRuta(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ruta");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "ruta");
        return title;
    }

    // Processes title tags in the feed.
    private String readSublinea(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "sublinea");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "sublinea");
        return title;
    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


    /**
     * Construir post con parada o con linea-parada
     *
     * @param linea
     * @param parada
     * @return string
     */
    private String datosPost(String linea, String parada) {

        StringBuffer sr = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

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
