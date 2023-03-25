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
package alberapps.java.tam.webservice.estructura.rutas;

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

public class EstructuraGetRutasSublineaParser {

    //private String URL = "http://isaealicante.subus.es/services/estructura.asmx";

    /**
     * Consulta del servicioWeb y mapeo de la respuesta
     *
     * @param linea
     * @param sublinea
     * @return
     * @throws Exception
     */
    public GetRutaSublineaResult consultarServicio(String linea, String sublinea, Boolean cache) throws Exception {

        InputStream is = null;

        GetRutaSublineaResult resultados = new GetRutaSublineaResult();

        try {

            //is = Utilidades.stringToStream(Conectividad.conexionPostUtf8(URL, datosPost(linea, sublinea), cache));

            Uri.Builder builder = Uri.parse(DatosTam.URL_SERVIDOR_ESTRUCTURA_RUTAS).buildUpon();
            builder.appendQueryParameter("line", linea);
            //builder.appendQueryParameter("sublinea", sublinea);
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

            Log.d("webservice", "Error consulta recorrido: " + linea + " - " + sublinea);

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
    public GetRutaSublineaResult parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag(); //soap:Envelope
            parser.nextTag(); //soap:Body
            return readRutasSublineaResponse(parser);
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
    private GetRutaSublineaResult readRutasSublineaResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
        GetRutaSublineaResult rutaResult = new GetRutaSublineaResult();

        parser.require(XmlPullParser.START_TAG, ns, "GetRutasSublineaResponse");


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("GetRutasSublineaResult")) {
                rutaResult.setInfoRutaList(readGetPasoParadaResult(parser));
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
    private List<InfoRuta> readGetPasoParadaResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<InfoRuta> infoRutaList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "GetRutasSublineaResult");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("InfoRuta")) {
                infoRutaList.add(readGetInfoRuta(parser));
            } else {
                skip(parser);
            }
        }
        return infoRutaList;
    }

    /**
     * @param parser
     * @return
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private InfoRuta readGetInfoRuta(XmlPullParser parser) throws XmlPullParserException, IOException {
        InfoRuta infoRuta = new InfoRuta();

        String nombre = null;
        String idLinea = null;

        parser.require(XmlPullParser.START_TAG, ns, "InfoRuta");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("nombre")) {
                nombre = readNombre(parser);
            } else if (name.equals("secciones")) {

                infoRuta.setInfoSeccion(readSecciones(parser));
                infoRuta.setNombre(nombre);
                infoRuta.setIdLinea(idLinea);

            } else if (name.equals("idLinea")) {
                idLinea = readIdLinea(parser);
            } else {
                skip(parser);
            }
        }
        return infoRuta;
    }


    /**
     * @param parser
     * @return
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    private List<InfoSeccion> readSecciones(XmlPullParser parser) throws XmlPullParserException, IOException {

        List<InfoSeccion> infoSeccionList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "secciones");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("InfoSeccion")) {
                infoSeccionList.add(readInfoSeccion(parser));


            } else {
                skip(parser);
            }
        }
        return infoSeccionList;
    }

    private InfoSeccion readInfoSeccion(XmlPullParser parser) throws XmlPullParserException, IOException {

        InfoSeccion infoSeccion = new InfoSeccion();

        parser.require(XmlPullParser.START_TAG, ns, "InfoSeccion");

        String seccion = null;


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("seccion")) {

                seccion = readSeccion(parser);

            } else if (name.equals("nodos")) {

                infoSeccion.setNodos(readInfoNodoSeccion(parser));
                infoSeccion.setSeccion(seccion);

            } else {
                skip(parser);
            }
        }
        return infoSeccion;
    }


    private List<InfoNodoSeccion> readInfoNodoSeccion(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<InfoNodoSeccion> infoNodoSeccionList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "nodos");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the PasoParada tag
            if (name.equals("InfoNodoSeccion")) {
                infoNodoSeccionList.add(readRegistro(parser));
            } else {
                skip(parser);
            }
        }
        return infoNodoSeccionList;
    }


    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private InfoNodoSeccion readRegistro(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "InfoNodoSeccion");
        String nodo = null;
        String tipo = null;
        String nombre = null;
        String distancia = null;
        String coordenadas = null;
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
            } else if (name.equals("distancia")) {
                distancia = readDistancia(parser);
            } else if (name.equals("coordenadas")) {
                coordenadas = readCoordenadas(parser);
            } else {
                skip(parser);
            }
        }
        return new InfoNodoSeccion(nodo, tipo, nombre, distancia, coordenadas);
    }


    // Processes title tags in the feed.
    private String readNombre(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "nombre");
        String nombre = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "nombre");
        return nombre;
    }

    private String readIdLinea(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "idLinea");
        String nombre = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "idLinea");
        return nombre;
    }


    // Processes title tags in the feed.
    private String readCoordenadas(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "coordenadas");
        String coordenadas = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "coordenadas");
        return coordenadas;
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

    // Processes title tags in the feed.
    private String readSeccion(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "seccion");
        String seccion = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "seccion");
        return seccion;
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
        sr.append("<soap:Body> <GetRutasSublinea xmlns=\"http://tempuri.org/\">");

        // label

        sr.append("<label>");
        sr.append(label);
        sr.append("</label>");


        // sublinea
        sr.append("<sublinea>");
        sr.append(sublinea);
        sr.append("</sublinea>");

        sr.append("</GetRutasSublinea> </soap:Body> </soap:Envelope>");

        return sr.toString();

    }


}
