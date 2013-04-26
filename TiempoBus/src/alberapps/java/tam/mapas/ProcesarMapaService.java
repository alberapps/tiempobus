/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
package alberapps.java.tam.mapas;

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

import alberapps.java.util.Utilidades;
import android.os.Build;
import android.text.Html;

public class ProcesarMapaService {

	public static final int MODE_ANY = 0;
	public static final int MODE_CAR = 1;
	public static final int MODE_WALKING = 2;

	/**
	 * Parsear fichero kml
	 * 
	 * @param url
	 * @return
	 */
	public static DatosMapa getDatosMapa(String url) {

		InputStream is = null;

		DatosMapa datosMapa = null;
		try {

			is = Utilidades.recuperarStream(url);

			if (is != null) {

				List<PlaceMark> lista = parse(is);

				if (lista != null && !lista.isEmpty()) {

					datosMapa = new DatosMapa();

					datosMapa.setPlacemarks(lista);

					datosMapa.setCurrentPlacemark(lista.get(0));

				} else {

					datosMapa = null;

				}

			} else {
				datosMapa = null;
			}

		} catch (Exception e) {

			datosMapa = null;
		} finally {
			try {
				is.close();
			} catch (Exception e) {

			}
		}

		return datosMapa;
	}

	/**
	 * Parsear entrada
	 * 
	 * @param is
	 * @return
	 */
	public static List<PlaceMark> parse(InputStream is) {
		// Instanciamos la fábrica para DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<PlaceMark> placeMarks = new ArrayList<PlaceMark>();

		try {
			// Creamos un nuevo parser DOM
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Realizamos lalectura completa del XML
			Document dom = builder.parse(is);

			// Nos posicionamos en el nodo principal del árbol (<kml>)
			Element root = dom.getDocumentElement();

			// Localizamos todos los elementos <Placemark>
			NodeList items = root.getElementsByTagName("Placemark");

			// Recorremos la lista de puntos
			for (int i = 0; i < items.getLength(); i++) {
				PlaceMark placeMark = new PlaceMark();

				// Obtenemos la parada actual
				Node item = items.item(i);

				// Obtenemos la lista de datos de la parada actual
				NodeList datosPlaceMark = item.getChildNodes();

				// Procesamos cada dato de la noticia
				for (int j = 0; j < datosPlaceMark.getLength(); j++) {
					Node dato = datosPlaceMark.item(j);
					String etiqueta = dato.getNodeName();

					if (etiqueta.equals("description")) {

						String texto = textoSegunVersion(dato);

						placeMark.setDescription(Html.fromHtml(texto).toString());

						// parsear datos

						String desc = placeMark.getDescription();
						int pos = desc.indexOf("parada:");

						// Solucion a literal cambiado ejm: linea 23
						if (pos < 0) {

							pos = desc.indexOf("Parada:");

							// Tipo especial
							if (pos >= 0)
								placeMark.setCodigoParada(desc.substring(pos + 7, pos + 7 + 5));
							else
								placeMark.setCodigoParada("");

						} else {

							// Tipo normal
							if (pos >= 0)
								placeMark.setCodigoParada(desc.substring(pos + 8, pos + 8 + 5));
							else
								placeMark.setCodigoParada("");

						}
						
						if(placeMark.getCodigoParada() != null){
							placeMark.setCodigoParada(placeMark.getCodigoParada().trim());
						}

						// Extraer sentido
						pos = desc.indexOf("Sentido");
						if (pos >= 0) {

							int posOb = desc.indexOf("Observaciones:");

							if (posOb < 0)
								placeMark.setSentido(desc.substring(pos + 8));
							else {

								placeMark.setSentido(desc.substring(pos + 8, posOb));

								placeMark.setObservaciones(desc.substring(posOb + 14));

							}

						} else{
							placeMark.setSentido("");
						
							placeMark.setObservaciones("");
						}
						
						if(placeMark.getObservaciones() != null){
							placeMark.setObservaciones(placeMark.getObservaciones().trim());
						}
							
						// Extraer lineas
						int pos2 = desc.indexOf("Líneas");
						if (pos >= 0 && pos2 >= 0) {
							placeMark.setLineas(desc.substring(pos2 + 7, pos));
						}else{
							placeMark.setLineas("");
						}
						
						if(placeMark.getLineas() != null){
							placeMark.setLineas(placeMark.getLineas().trim());
						}

					} else if (etiqueta.equals("name")) {

						placeMark.setTitle(textoSegunVersion(dato));

					} else if (etiqueta.equals("Point")) {
						NodeList points = dato.getChildNodes();

						for (int z = 0; z < points.getLength(); z++) {

							Node dato2 = points.item(z);
							String etiqueta2 = dato2.getNodeName();

							if (etiqueta2.equals("coordinates")) {

								String texto = textoSegunVersion(dato2);

								// String texto = dato2.getTextContent();
								placeMark.setCoordinates(texto);
							}

						}

					}

				}

				placeMarks.add(placeMark);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return placeMarks;
	}

	/**
	 * Parsear fichero kml
	 * 
	 * @param url
	 * @return
	 */
	public static String getDatosMapaRecorrido(String url) {

		InputStream is = null;

		String coordenadas = null;

		try {
			is = Utilidades.recuperarStream(url);

			if (is != null) {

				coordenadas = parseRecorrido(is);

			} else {
				coordenadas = null;
			}

		} catch (Exception e) {

		} finally {
			try {
				is.close();
			} catch (IOException e) {

			}
		}

		if (coordenadas != null && !coordenadas.equals("")) {

			return coordenadas;

		} else {
			return null;
		}
	}

	public static String parseRecorrido(InputStream is) {
		// Instanciamos la fábrica para DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		String coordenadas = null;

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

			coordenadas = textoSegunVersion(item).trim();

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return coordenadas;
	}

	/**
	 * Cotrol de version de android
	 * 
	 * @param node
	 * @return
	 */
	private static String textoSegunVersion(Node node) {

		String texto = null;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			texto = textAlternativo(node);

			if (texto != null) {

				String textoProc = (Html.fromHtml(texto)).toString();

				texto = textoProc;

			}

		} else {
			texto = (new VersionHelper().getTextContent(node));
		}

		return texto;

	}

	/**
	 * Funcion auxiliar para que funcione en la version 2.1 y anteriores
	 * 
	 * @param node
	 * @return
	 */
	private static String textAlternativo(Node node) {

		Node child;
		String sContent = node.getNodeValue() != null ? node.getNodeValue() : "";

		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			child = nodes.item(i);

			if (child.getNodeValue() != null) {
				sContent += child.getNodeValue() != null ? child.getNodeValue() : "";
			} else {
				sContent += "&" + child.getNodeName();
			}

			if (nodes.item(i).getChildNodes().getLength() > 0) {
				sContent += textAlternativo(nodes.item(i));
			}
		}

		return sContent;
	}

}

class VersionHelper {
	public String getTextContent(Node node) {

		return node.getTextContent();

	}
}
