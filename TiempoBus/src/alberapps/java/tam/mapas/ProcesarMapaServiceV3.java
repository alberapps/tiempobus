/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2017 Alberto Montiel
 * <p>
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
package alberapps.java.tam.mapas;

import android.content.Context;
import android.net.Uri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

public class ProcesarMapaServiceV3 {


    public static final String LOG_NAME = "ProcesarMapaServiceV3";

    //private static String URL = "https://alicante.vectalia.es/ajax/data/line-kml?lang=es&__internal__=1&type=line&id=52&_=1&idx=0";


    //horarios
    //https://alicante.vectalia.es/ajax/microsite/linea-content?lang=es&__internal__=1&id=673


    /**
     * Parsear fichero kml
     *
     * @param context
     * @return
     */
    public static String[] getDatosRecorrido(Context context, String idRuta, String linea) {

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("alicante.vectalia.es");
        builder.appendPath("ajax");
        builder.appendPath("data");
        builder.appendPath("line-kml");
        builder.appendQueryParameter("lang", "es");
        builder.appendQueryParameter("__internal__", "1");
        builder.appendQueryParameter("type", "line");
        builder.appendQueryParameter("id", idRuta);
        builder.appendQueryParameter("_", "1");
        builder.appendQueryParameter("idx", "0");


        String url = builder.toString();

        //String url = URL;

        InputStream isZip = null;

        // ByteArrayInputStream is = null;
        InputStream is = null;
        DataInputStream entrada = null;

        String[] datosRecorridos = {null, null};
        try {

            String userAgentDefault = Utilidades.getAndroidUserAgent(context);

            isZip = Conectividad.conexionGetStream(url, userAgentDefault);

            // Verificar si kml llega comprimido en zip
            entrada = new DataInputStream(isZip);
            boolean esZip = Utilidades.isZipFile(isZip, entrada);

            if (esZip) {

                ZipInputStream zis = new ZipInputStream(isZip);

                ZipEntry ze;
                if ((ze = zis.getNextEntry()) != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;
                    try {
                        while ((count = zis.read(buffer)) != -1) {
                            baos.write(buffer, 0, count);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String filename = ze.getName();

                    if (filename.contains(".kml")) {

                        byte[] bytes = baos.toByteArray();
                        // do something with 'filename' and 'bytes'...

                        is = new ByteArrayInputStream(bytes);

                    }

                }

            } else {

                //is = isZip;
                is = null;
            }

            if (is != null) {


                datosRecorridos = parseRecorrido(is);


            } else {
                datosRecorridos = null;
            }

        } catch (Exception e) {

            e.printStackTrace();

            datosRecorridos = null;
        } finally {
            try {
                if (isZip != null) {
                    isZip.close();
                }
                if (is != null) {
                    is.close();
                }
                if (entrada != null) {
                    entrada.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return datosRecorridos;
    }


    public static String[] parseRecorrido(InputStream is) {
        // Instanciamos la fábrica para DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        String coordenadas[] = {null, null};

        try {
            // Creamos un nuevo parser DOM
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Realizamos lalectura completa del XML
            Document dom = builder.parse(is);

            // Nos posicionamos en el nodo principal del árbol (<kml>)
            Element root = dom.getDocumentElement();

            // Localizamos todos los elementos <Placemark>
            NodeList items = root.getElementsByTagName("coordinates");

            Node item = items.item(0);

            coordenadas[0] = item.getTextContent().trim();

            try {
                if (items.getLength() > 1 && items.item(1) != null) {
                    Node item2 = items.item(1);
                    coordenadas[1] = item2.getTextContent().trim();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return coordenadas;
    }


}
