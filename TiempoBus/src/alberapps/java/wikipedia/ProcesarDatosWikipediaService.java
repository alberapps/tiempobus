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
package alberapps.java.wikipedia;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

/**
 * Consulta de datos geolocalizados de la wikipedia
 */
public class ProcesarDatosWikipediaService {

    private static final String URL_IDIOMA = "https://";

    private static final String URL = ".wikipedia.org/w/api.php?action=query&list=geosearch&gsradius=10000&format=xml&gscoord=";

    // 38.343676|-0.494515

    /**
     * Consultar datos wikipedia con geosearch
     *
     * @param lat
     * @param lon
     * @return lista
     * @throws Exception
     */
    public static WikiQuery getDatosWikiLatLon(String lat, String lon) throws Exception {


        // 38346452
        // -489110

        String glat = Double.toString((Integer.parseInt(lat) / 1E6));
        String glon = Double.toString((Integer.parseInt(lon) / 1E6));

        InputStream is = null;

        WikiQuery resultados = new WikiQuery();

        String caracter = URLEncoder.encode("|", "UTF-8");

        String urlGet = URL_IDIOMA + UtilidadesUI.getIdiomaWiki() + URL + glat + caracter + glon;


        try {

            is = Utilidades.stringToStream(Conectividad.conexionGetIso(urlGet, true, null, true));

            if (is != null) {

                resultados = parse(is);

            } else {

                // resultados

            }

        } catch (Exception e) {

            Log.d("webservice", "Error consulta wiki");

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
    public static WikiQuery parse(InputStream is) {
        // Instanciamos la fábrica para DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<WikiData> wikiDataList = new ArrayList<>();

        WikiQuery resultados = new WikiQuery();

        try {
            // Creamos un nuevo parser DOM
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Realizamos lalectura completa del XML
            Document dom = builder.parse(is);

            // Nos posicionamos en el nodo principal del árbol
            Element root = dom.getDocumentElement();

            // Folder principal
            NodeList gsList = root.getElementsByTagName("gs");

            for (int i = 0; i < gsList.getLength(); i++) {

                // gs
                Node gs = gsList.item(i);

                NamedNodeMap atributos = gs.getAttributes();

                String pageId = atributos.getNamedItem("pageid").getNodeValue();

                String title = atributos.getNamedItem("title").getNodeValue();

                String latW = atributos.getNamedItem("lat").getNodeValue();

                String lonW = atributos.getNamedItem("lon").getNodeValue();

                String distancia = atributos.getNamedItem("dist").getNodeValue();

                WikiData data = new WikiData();

                data.setPageId(pageId);
                data.setTitle(title);
                data.setLat(latW);
                data.setLon(lonW);
                data.setDist(distancia);

                wikiDataList.add(data);

            }

            resultados.setListaDatos(wikiDataList);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return resultados;
    }

}
