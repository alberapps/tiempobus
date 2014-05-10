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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;
import android.os.Build;
import android.text.Html;
import android.util.Log;

public class ProcesarMapaServiceV3 {

	public static final int MODE_ANY = 0;
	public static final int MODE_CAR = 1;
	public static final int MODE_WALKING = 2;

	public static final String LOG_NAME = "ProcesarMapaServiceV3";

	/**
	 * Parsear fichero kml
	 * 
	 * @param url
	 * @return
	 */
	public static DatosMapa[] getDatosMapa(String url) {

		InputStream isZip = null;

		// ByteArrayInputStream is = null;
		InputStream is = null;

		DatosMapa[] datosMapa = { null, null };
		try {

			isZip = Conectividad.conexionGetIsoStream(url);

			// Verificar si kml llega comprimido en zip
			boolean esZip = Utilidades.isZipFile(isZip);

			// Provisional
			// if (url.equals("http://www.subus.es/K/TuribusP.xml")) {
			if (esZip) {

				ZipInputStream zis = new ZipInputStream(isZip);

				ZipEntry ze;
				while ((ze = zis.getNextEntry()) != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int count;
					while ((count = zis.read(buffer)) != -1) {
						baos.write(buffer, 0, count);
					}
					String filename = ze.getName();

					if (filename.equals("doc.kml")) {

						byte[] bytes = baos.toByteArray();
						// do something with 'filename' and 'bytes'...

						is = new ByteArrayInputStream(bytes);

					}

				}

			} else {

				is = isZip;
			}

			if (is != null) {

				Datos parseado = parse(is);

				List<PlaceMark> listaIda = parseado.getPlaceMarksIda();

				if (listaIda != null && !listaIda.isEmpty()) {

					datosMapa[0] = new DatosMapa();

					datosMapa[0].setPlacemarks(listaIda);

					datosMapa[0].setCurrentPlacemark(listaIda.get(0));

				} else {

					datosMapa[0] = null;

				}

				List<PlaceMark> listaVuelta = parseado.getPlaceMarksVuelta();

				if (listaVuelta != null && !listaVuelta.isEmpty()) {

					datosMapa[1] = new DatosMapa();

					datosMapa[1].setPlacemarks(listaVuelta);

					datosMapa[1].setCurrentPlacemark(listaVuelta.get(0));

				} else {

					datosMapa[1] = null;

				}

			} else {
				datosMapa = null;
			}

		} catch (Exception e) {

			e.printStackTrace();

			datosMapa = null;
		} finally {
			try {
				isZip.close();
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
	public static Datos parse(InputStream is) {
		// Instanciamos la fábrica para DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<PlaceMark> placeMarks = new ArrayList<PlaceMark>();

		Datos datos = new Datos();

		try {
			// Creamos un nuevo parser DOM
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Realizamos lalectura completa del XML
			Document dom = builder.parse(is);

			// Nos posicionamos en el nodo principal del árbol (<kml>)
			Element root = dom.getDocumentElement();

			// Folder principal
			NodeList folderPrincipalList = root.getElementsByTagName("Folder");

			// Control para determinar ida y vuelta
			Element folderIda = null;
			Element folderVuelta = null;

			String folderName1 = ((Element) folderPrincipalList.item(1)).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
			String folderName2 = ((Element) folderPrincipalList.item(2)).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();

			if (folderName1.equals("Ida")) {
				folderIda = (Element) folderPrincipalList.item(1);
				folderVuelta = (Element) folderPrincipalList.item(2);
			} else {
				folderIda = (Element) folderPrincipalList.item(2);
				folderVuelta = (Element) folderPrincipalList.item(1);
			}

			// Localizamos todos los elementos <Placemark>
			NodeList items = folderIda.getElementsByTagName("Placemark");

			datos.setPlaceMarksIda(parsePlacemarks(items));

			NodeList itemsVuelta = folderVuelta.getElementsByTagName("Placemark");

			datos.setPlaceMarksVuelta(parsePlacemarks(itemsVuelta));

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return datos;
	}

	private static List<PlaceMark> parsePlacemarks(NodeList items) {

		List<PlaceMark> placeMarks = new ArrayList<PlaceMark>();

		// Recorremos la lista de puntos
		for (int i = 0; i < items.getLength(); i++) {
			PlaceMark placeMark = new PlaceMark();

			// Obtenemos la parada actual
			Node item = items.item(i);

			// Obtenemos la lista de datos de la parada actual
			NodeList datosPlaceMark = item.getChildNodes();

			// Procesamos cada dato del recorrido
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

					if (placeMark.getCodigoParada() != null) {
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

					} else {
						placeMark.setSentido("");

						placeMark.setObservaciones("");
					}

					if (placeMark.getObservaciones() != null) {
						placeMark.setObservaciones(placeMark.getObservaciones().trim());
					}

					// Extraer lineas
					int pos2 = desc.indexOf("Líneas");
					if (pos >= 0 && pos2 >= 0) {
						placeMark.setLineas(desc.substring(pos2 + 7, pos));
					} else {
						placeMark.setLineas("");
					}

					if (placeMark.getLineas() != null) {
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

		return placeMarks;

	}

	/**
	 * Parsear fichero kml
	 * 
	 * @param url
	 * @return
	 */
	public static String[] getDatosMapaRecorrido(String url) {

		InputStream is = null;

		String coordenadas[] = { null, null };

		try {
			is = Conectividad.conexionGetIsoStream(url);

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

		// if (coordenadas != null && !coordenadas.equals("")) {

		return coordenadas;

		// } else {
		// return null;
		// }
	}

	public static String[] parseRecorrido(InputStream is) {
		// Instanciamos la fábrica para DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		String coordenadas[] = { null, null };

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

			coordenadas[0] = textoSegunVersion(item).trim();

			Node item2 = items.item(1);

			coordenadas[1] = textoSegunVersion(item2).trim();

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
