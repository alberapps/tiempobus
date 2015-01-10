/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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
package alberapps.java.tam.webservice.vehiculos;

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
import alberapps.java.util.Utilidades;

public class GetVehiculosXmlWebservice {

	private String URL = "http://isaealicante.subus.es/services/dinamica.asmx";

	/**
	 * Consulta del servicioWeb y mapeo de la respuesta
	 * 
	 * @param linea
	 * @param parada
	 * @return
	 * @throws Exception
	 */
	public GetVehiculosResult consultarServicio(String linea, Boolean tiemposCache) throws Exception {

		InputStream is = null;

		GetVehiculosResult resultados = new GetVehiculosResult();

		try {

			is = Utilidades.stringToStream(Conectividad.conexionPostUtf8(URL, datosPost(linea), tiemposCache));

			if (is != null) {

				resultados = parse(is);

			} else {

				// resultados

			}

		} catch (Exception e) {

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
	public GetVehiculosResult parse(InputStream is) {
		// Instanciamos la fábrica para DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<InfoVehiculo> vehiculosList = new ArrayList<InfoVehiculo>();

		GetVehiculosResult resultados = new GetVehiculosResult();

		try {
			// Creamos un nuevo parser DOM
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Realizamos lalectura completa del XML
			Document dom = builder.parse(is);

			// Nos posicionamos en el nodo principal del árbol (<kml>)
			Element root = dom.getDocumentElement();

			// Folder principal
			NodeList getVehiculosResultList = root.getElementsByTagName("InfoVehiculo");

			for (int i = 0; i < getVehiculosResultList.getLength(); i++) {

				// vehiculos
				Node infoVehiculo = getVehiculosResultList.item(i);

				NodeList iv = infoVehiculo.getChildNodes();

				// recuperar de xml
				String vehiculo = iv.item(0).getChildNodes().item(0).getNodeValue();
				String linea = iv.item(1).getChildNodes().item(0).getNodeValue();
				String sublinea = iv.item(2).getChildNodes().item(0).getNodeValue();
				String coche = iv.item(3).getChildNodes().item(0).getNodeValue();
				String servBus = iv.item(4).getChildNodes().item(0).getNodeValue();
				String conductor = iv.item(5).getChildNodes().item(0).getNodeValue();
				String servCond = iv.item(6).getChildNodes().item(0).getNodeValue();
				String estado = iv.item(7).getChildNodes().item(0).getNodeValue();
				String estadoLocReal = iv.item(8).getChildNodes().item(0).getNodeValue();
				String xcoord = iv.item(9).getChildNodes().item(0).getNodeValue();
				String ycoord = iv.item(10).getChildNodes().item(0).getNodeValue();

				// Carga de datos
				InfoVehiculo infoVehiculoData = new InfoVehiculo();
				infoVehiculoData.setVehiculo(vehiculo);
				infoVehiculoData.setLinea(linea);
				infoVehiculoData.setSublinea(sublinea);
				infoVehiculoData.setCoche(coche);
				infoVehiculoData.setServBus(servBus);
				infoVehiculoData.setConductor(conductor);
				infoVehiculoData.setServCond(servCond);
				infoVehiculoData.setEstado(estado);
				infoVehiculoData.setEstadoLocReal(estadoLocReal);
				infoVehiculoData.setXcoord(xcoord);
				infoVehiculoData.setYcoord(ycoord);

				vehiculosList.add(infoVehiculoData);

			}

			resultados.setInfoVehiculoList(vehiculosList);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return resultados;
	}

	/**
	 * Construir post de vehiculos
	 * 
	 * @param linea
	 * @return string
	 */
	private String datosPost(String linea) {

		StringBuffer sr = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");

		sr.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sr.append("<soap:Body> <GetVehiculos xmlns=\"http://tempuri.org/\">");

		// Linea
		if (linea != null && !linea.equals("")) {
			sr.append("<linea>");
			sr.append(linea);
			sr.append("</linea>");
		}

		sr.append("</GetVehiculos> </soap:Body> </soap:Envelope>");

		return sr.toString();

	}

}
