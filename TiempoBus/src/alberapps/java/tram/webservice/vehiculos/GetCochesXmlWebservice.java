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
package alberapps.java.tram.webservice.vehiculos;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import alberapps.java.tram.DatosTRAM;
import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

public class GetCochesXmlWebservice {

	public static final int URL1 = 1;
	public static final int URL2 = 2;

	/**
	 * Consulta del servicioWeb y mapeo de la respuesta
	 * 
	 * @param linea
	 * @param parada
	 * @return
	 * @throws Exception
	 */
	public GetCochesResult consultarServicio(String linea, int consulta, Boolean tiemposCache) throws Exception {

		InputStream is = null;

		GetCochesResult resultados = new GetCochesResult();

		String url = null;

		if (consulta == 1) {
			url = DatosTRAM.URL_1;
		} else {
			url = DatosTRAM.URL_2;
		}

		try {

			is = Utilidades.stringToStream(Conectividad.conexionPostUtf8NoKeepAlive(url, DatosTRAM.datosPost(linea), tiemposCache));

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
	public GetCochesResult parse(InputStream is) {
		// Instanciamos la fábrica para DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<InfoCoche> vehiculosList = new ArrayList<InfoCoche>();

		GetCochesResult resultados = new GetCochesResult();

		try {
			// Creamos un nuevo parser DOM
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Realizamos lalectura completa del XML
			Document dom = builder.parse(is);

			// Nos posicionamos en el nodo principal del árbol (<kml>)
			Element root = dom.getDocumentElement();

			// Folder principal
			NodeList getVehiculosResultList = root.getElementsByTagName("InfoCoche");

			for (int i = 0; i < getVehiculosResultList.getLength(); i++) {

				// vehiculos
				Node infoVehiculo = getVehiculosResultList.item(i);

				NodeList iv = infoVehiculo.getChildNodes();

				// recuperar de xml
				String coche = iv.item(0).getChildNodes().item(0).getNodeValue();
				String vehiculo = iv.item(1).getChildNodes().item(0).getNodeValue();
				String servBus = iv.item(2).getChildNodes().item(0).getNodeValue();
				String conductor = iv.item(3).getChildNodes().item(0).getNodeValue();
				
				
				String servCond = "";
				if(iv.item(4).getChildNodes() != null && iv.item(4).getChildNodes().getLength() > 0 ){
					servCond = iv.item(4).getChildNodes().item(0).getNodeValue();
				}
				
				String estado = iv.item(5).getChildNodes().item(0).getNodeValue();
				String estadoLocReal = iv.item(6).getChildNodes().item(0).getNodeValue();

				// TODO VERIFICAR MAPEOS

				String seccion = iv.item(7).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
				String offset = iv.item(7).getChildNodes().item(1).getChildNodes().item(0).getNodeValue();

				String xcoord = iv.item(7).getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
				String ycoord = iv.item(7).getChildNodes().item(3).getChildNodes().item(0).getNodeValue();

				// Carga de datos
				InfoCoche infoVehiculoData = new InfoCoche();
				infoVehiculoData.setVehiculo(vehiculo);

				infoVehiculoData.setCoche(coche);
				infoVehiculoData.setServBus(servBus);
				infoVehiculoData.setConductor(conductor);
				infoVehiculoData.setServCond(servCond);
				infoVehiculoData.setEstado(estado);
				infoVehiculoData.setEstadoLocReal(estadoLocReal);
				infoVehiculoData.setXcoord(xcoord);
				infoVehiculoData.setYcoord(ycoord);

				infoVehiculoData.setOffset(offset);
				infoVehiculoData.setSeccion(seccion);

				vehiculosList.add(infoVehiculoData);

			}

			resultados.setInfoVehiculoList(vehiculosList);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return resultados;
	}

}
