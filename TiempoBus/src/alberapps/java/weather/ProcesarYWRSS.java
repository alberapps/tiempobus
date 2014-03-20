/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
 * 
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
package alberapps.java.weather;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import alberapps.java.util.Conectividad;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;

/**
 * Procesar informacion del tiempo de yahoo
 * 
 * 
 */
public class ProcesarYWRSS {

	public static final String URL = "http://weather.yahooapis.com/forecastrss?w=752101&u=c";

	public static final String URL_IMAGEN = "http://l.yimg.com/a/i/us/we/52/"; // 28.gif";

	/**
	 * Datos del clima
	 * 
	 * @return clima
	 * @throws Exception
	 */
	public static WeatherQuery getDatosClima() throws Exception {

		WeatherQuery resultados = new WeatherQuery();

		resultados.setListaDatos(parsea(URL));

		return resultados;

	}

	/**
	 * Parsear los datos RSS de yahoo
	 * 
	 * @param urlEntrada
	 * @return datos
	 */
	public static List<WeatherData> parsea(String urlEntrada) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		List<WeatherData> listaWeather = new ArrayList<WeatherData>();
		WeatherData data = null;

		InputStream st = null;

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			st = Conectividad.conexionGetIsoStream(urlEntrada);
			Document dom = builder.parse(st);
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("item");
			for (int i = 0; i < items.getLength(); i++) {

				data = new WeatherData();

				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j = 0; j < properties.getLength(); j++) {
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("title")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setTitle(textoProc);

					} else if (name.equalsIgnoreCase("link")) {

						data.setLink(property.getFirstChild().getNodeValue());

					} else if (name.equalsIgnoreCase("description")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setDescription(textoProc);

					} else if (name.equalsIgnoreCase("geo:lat")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setGeolat(textoProc);

					} else if (name.equalsIgnoreCase("geo:long")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setGeolong(textoProc);

					} else if (name.equalsIgnoreCase("pubDate")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setPubDate(textoProc);

					} else if (name.equalsIgnoreCase("yweather:condition")) {

						for (int k = 0; k < property.getAttributes().getLength(); k++) {

							if (property.getAttributes().item(k).getNodeName().equals("text")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setContitionText(textoProc);

							} else if (property.getAttributes().item(k).getNodeName().equals("code")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setContitionCode(textoProc);

								// Imagen
								data.setImagen(recuperaImagen(URL_IMAGEN + textoProc + ".gif"));

							} else if (property.getAttributes().item(k).getNodeName().equals("temp")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setContitionTemp(textoProc);

							}

						}

					} else if (name.equalsIgnoreCase("yweather:forecast")) {

						for (int k = 0; k < property.getAttributes().getLength(); k++) {

							if (property.getAttributes().item(k).getNodeName().equals("low")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setLow(textoProc);

							} else if (property.getAttributes().item(k).getNodeName().equals("high")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setHigh(textoProc);

							}
						}

						break;

					}

				}
				listaWeather.add(data);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {

			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

		}

		return listaWeather;

	}

	/**
	 * Recuperar la imagen
	 * 
	 * @param urlParam
	 * @return imagen
	 */
	private static Bitmap recuperaImagen(String urlParam) {

		InputStream st = null;

		Bitmap bm = null;

		try {

			st = Conectividad.conexionGetIsoStream(urlParam);

			bm = BitmapFactory.decodeStream(st);

		} catch (Exception e) {

			bm = null;

		} finally {

			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException e) {

			}

		}

		return bm;

	}

}
