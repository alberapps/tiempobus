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
package alberapps.java.tam.webservice.estructura.nodosmap;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

public class EstructuraGetNodosMapSublineaParser {

    private String URL = "http://isaealicante.subus.es/services/estructura.asmx";

    /**
     * Consulta del servicioWeb y mapeo de la respuesta
     *
     * @param linea
     * @param sublinea
     * @return
     * @throws Exception
     */
    public GetNodosMapSublineaResult consultarServicio(String linea, String sublinea, Boolean cache) throws Exception {

        InputStream is = null;

        GetNodosMapSublineaResult resultados = new GetNodosMapSublineaResult();

        try {

            is = Utilidades.stringToStream(Conectividad.conexionPostUtf8(URL, datosPost(linea, sublinea), cache));

            if (is != null) {

                resultados = parse(is);

            } else {

                // resultados

            }

        } catch (Exception e) {

            Log.d("webservice", "Error consulta datos mapa: " + linea + " - " + sublinea);

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
    public GetNodosMapSublineaResult parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag(); //soap:Envelope
            parser.nextTag(); //soap:Body
            return readNodosMapSublineaResponse(parser);
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
    private GetNodosMapSublineaResult readNodosMapSublineaResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
        GetNodosMapSublineaResult rutaResult = new GetNodosMapSublineaResult();

        parser.require(XmlPullParser.START_TAG, ns, "GetNodosMapSublineaResponse");


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("GetNodosMapSublineaResult")) {
                rutaResult.setInfoNodoMapList(readGetNodosMapSublineaResult(parser));
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
    private List<InfoNodoMap> readGetNodosMapSublineaResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<InfoNodoMap> infoNodoMapList = new ArrayList<InfoNodoMap>();

        parser.require(XmlPullParser.START_TAG, ns, "GetNodosMapSublineaResult");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("InfoNodoMap")) {
                infoNodoMapList.add(readRegistro(parser));
            } else {
                skip(parser);
            }
        }
        return infoNodoMapList;
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private InfoNodoMap readRegistro(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "InfoNodoMap");
        String nodo = null;
        String tipo = null;
        String nombre = null;
        String label = null;
        String posx = null;
        String posy = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("nodo")) {
                nodo = readNodo(parser);
            } else if (name.equals("tipo")) {
                tipo = readTipo(parser);
            } else if (name.equals("nombre")) {
                nombre = readNombre(parser);
            } else if (name.equals("label")) {
                label = readLabel(parser);
            } else if (name.equals("posx")) {
                posx = readPosx(parser);
            } else if (name.equals("posy")) {
                posy = readPosy(parser);
            } else {
                skip(parser);
            }
        }
        return new InfoNodoMap(nodo, tipo, nombre, label, posx, posy);
    }


    // Processes title tags in the feed.
    private String readNombre(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "nombre");
        String nombre = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "nombre");
        return nombre;
    }

    // Processes title tags in the feed.
    private String readNodo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "nodo");
        String nodo = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "nodo");
        return nodo;
    }

    // Processes title tags in the feed.
    private String readTipo(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "tipo");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "tipo");
        return title;
    }

    // Processes title tags in the feed.
    private String readLabel(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "label");
        String label = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "label");
        return label;
    }

    // Processes title tags in the feed.
    private String readPosx(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "posx");
        String posx = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "posx");
        return posx;
    }

    // Processes title tags in the feed.
    private String readPosy(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "posy");
        String posy = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "posy");
        return posy;
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
        sr.append("<soap:Body> <GetNodosMapSublinea xmlns=\"http://tempuri.org/\">");

        // label

        sr.append("<label>");
        sr.append(label);
        sr.append("</label>");


        // sublinea
        sr.append("<sublinea>");
        sr.append(sublinea);
        sr.append("</sublinea>");

        sr.append("</GetNodosMapSublinea> </soap:Body> </soap:Envelope>");

        return sr.toString();

    }


}
