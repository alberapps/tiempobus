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
package alberapps.java.weather.aemet;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;
import alberapps.java.weather.EstadoCielo;
import alberapps.java.weather.WeatherData;
import alberapps.java.weather.WeatherQuery;

/**
 * Consulta de datos geolocalizados de la wikipedia
 */
public class ProcesarDatosWeatherService {


    private static final String URL = "http://www.aemet.es/xml/municipios/localidad_03014.xml";


    /**
     * Consultar datos wikipedia con geosearch
     *
     * @throws Exception
     */
    public static WeatherQuery getDatosClima() throws Exception {


        InputStream is = null;

        WeatherQuery resultados = new WeatherQuery();

        //String caracter = URLEncoder.encode("|", "UTF-8");

        String urlGet = URL;


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
    public static WeatherQuery parse(InputStream is) {
        // Instanciamos la fábrica para DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<WeatherData> weatherDataList = new ArrayList<WeatherData>();

        WeatherQuery resultados = new WeatherQuery();

        try {
            // Creamos un nuevo parser DOM
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Realizamos lalectura completa del XML
            Document dom = builder.parse(is);

            // Nos posicionamos en el nodo principal del árbol (<kml>)
            Element root = dom.getDocumentElement();

            // Folder principal
            NodeList diaList = root.getElementsByTagName("dia");

            for (int i = 0; i < diaList.getLength(); i++) {

                WeatherData data = new WeatherData();

                // gs
                Node gs = diaList.item(i);

                for (int j = 0; j < gs.getChildNodes().getLength(); j++) {

                    if (gs.getChildNodes().item(j).getNodeName() != null && gs.getChildNodes().item(j).getNodeName().equals("estado_cielo")) {


                        NamedNodeMap atributos = gs.getChildNodes().item(j).getAttributes();

                        String periodo = atributos.getNamedItem("periodo").getNodeValue();
                        String descripcion = atributos.getNamedItem("descripcion").getNodeValue();

                        String valor = "";

                        if (gs.getChildNodes().item(j).getFirstChild() != null && gs.getChildNodes().item(j).getFirstChild().getNodeValue() != null) {
                            valor = gs.getChildNodes().item(j).getFirstChild().getNodeValue();
                        }

                        EstadoCielo estadoCielo = new EstadoCielo();

                        estadoCielo.setPeriodo(periodo);
                        estadoCielo.setDescripcion(descripcion);
                        estadoCielo.setValor(valor);

                        if (data.getEstadoCielo() == null) {
                            data.setEstadoCielo(new ArrayList<EstadoCielo>());
                        }

                        data.getEstadoCielo().add(estadoCielo);

                    } else if (gs.getChildNodes().item(j).getNodeName() != null && gs.getChildNodes().item(j).getNodeName().equals("temperatura")) {


                        String max = gs.getChildNodes().item(j).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                        String min = gs.getChildNodes().item(j).getChildNodes().item(3).getChildNodes().item(0).getNodeValue();

                        data.setTempMaxima(max);
                        data.setTempMinima(min);


                    }


                }


				/*NamedNodeMap atributos = gs.getAttributes();

				String pageId = atributos.getNamedItem("pageid").getNodeValue();

				String title = atributos.getNamedItem("title").getNodeValue();

				String latW = atributos.getNamedItem("lat").getNodeValue();

				String lonW = atributos.getNamedItem("lon").getNodeValue();

				String distancia = atributos.getNamedItem("dist").getNodeValue();

				WeatherData data = new WeatherData();

				data.setPageId(pageId);
				data.setTitle(title);
				data.setLat(latW);
				data.setLon(lonW);
				data.setDist(distancia);*/

                weatherDataList.add(data);

                break;

            }

            resultados.setListaDatos(weatherDataList);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return resultados;
    }

}
