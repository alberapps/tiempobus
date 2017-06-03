/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2015 Alberto Montiel
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
package alberapps.java.tam.webservice.estructura.polylinea;

import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import alberapps.java.tam.DatosTam;
import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

public class EstructuraGetPolylineaSublineaParser {

    //private String URL = "http://isaealicante.subus.es/services/estructura.asmx";

    /**
     * Consulta del servicioWeb y mapeo de la respuesta
     *
     * @param linea
     * @param sublinea
     * @return
     * @throws Exception
     */
    public GetPolylineaSublineaResult consultarServicio(String linea, String sublinea, Boolean cache) throws Exception {

        InputStream is = null;

        GetPolylineaSublineaResult resultados = new GetPolylineaSublineaResult();

        try {

            //is = Utilidades.stringToStream(Conectividad.conexionPostUtf8(URL, datosPost(linea, sublinea), cache));

            Uri.Builder builder = Uri.parse(DatosTam.URL_SERVIDOR_ESTRUCTURA_POLYLINEA).buildUpon();
            builder.appendQueryParameter("linea", linea);
            builder.appendQueryParameter("sublinea", sublinea);
            builder.build();

            String resp = Conectividad.conexionGetUtf8(builder.toString());
            resp = resp.substring(resp.indexOf("<soap:Envelope"));
            is = Utilidades.stringToStream(resp);


            if (is != null) {

                resultados = parse(is);

            } else {

                // resultados

            }

        } catch (Exception e) {

            Log.d("webservice", "Error consulta datos mapa polylinea: " + linea + " - " + sublinea);

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
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    public GetPolylineaSublineaResult parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag(); //soap:Envelope
            parser.nextTag(); //soap:Body
            return readPolylineaSublineaResponse(parser);
        } finally {
            in.close();
        }
    }


    /**
     * @param parser
     * @return
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private GetPolylineaSublineaResult readPolylineaSublineaResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
        GetPolylineaSublineaResult rutaResult = new GetPolylineaSublineaResult();

        parser.require(XmlPullParser.START_TAG, ns, "GetPolylineaSublineaResponse");


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("GetPolylineaSublineaResult")) {
                rutaResult.setInfoCoordList(readGetPolylineaSublineaResult(parser));
            } else {
                skip(parser);
            }
        }
        return rutaResult;
    }


    /**
     * @param parser
     * @return
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private List<InfoCoord> readGetPolylineaSublineaResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<InfoCoord> infoCoordList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "GetPolylineaSublineaResult");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("InfoCoord")) {
                infoCoordList.add(readRegistro(parser));
            } else {
                skip(parser);
            }
        }
        return infoCoordList;
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private InfoCoord readRegistro(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "InfoCoord");
        String x = null;
        String y = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("x")) {
                x = readX(parser);
            } else if (name.equals("y")) {
                y = readY(parser);
            } else {
                skip(parser);
            }
        }
        return new InfoCoord(x, y);
    }


    // Processes title tags in the feed.
    private String readX(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "x");
        String x = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "x");
        return x;
    }

    // Processes title tags in the feed.
    private String readY(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "y");
        String y = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "y");
        return y;
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
    private String readDistancia(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "distancia");
        String distancia = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "distancia");
        return distancia;
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
     * Construir post con linea y sublinea
     *
     * @param label
     * @param sublinea
     * @return string
     */
    private String datosPost(String label, String sublinea) {

        StringBuffer sr = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

        sr.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        sr.append("<soap:Body> <GetPolylineaSublinea xmlns=\"http://tempuri.org/\">");

        // label

        sr.append("<label_linea>");
        sr.append(label);
        sr.append("</label_linea>");


        // sublinea
        sr.append("<num_sublinea>");
        sr.append(sublinea);
        sr.append("</num_sublinea>");

        sr.append("</GetPolylineaSublinea> </soap:Body> </soap:Envelope>");

        return sr.toString();

    }


}
