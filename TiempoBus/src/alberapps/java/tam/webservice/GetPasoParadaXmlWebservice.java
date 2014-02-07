/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
package alberapps.java.tam.webservice;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import alberapps.java.util.Conectividad;
import android.util.Log;

public class GetPasoParadaXmlWebservice {

	
	private String URL = "http://isaealicante.subus.es/services/dinamica.asmx";
	
	
	/**
	 * Consulta del servicioWeb y mapeo de la respuesta
	 * 
	 * @param linea
	 * @param parada
	 * @return
	 * @throws Exception
	 */
	public GetPasoParadaResult consultarServicio(String linea, String parada) throws Exception {
		
		InputStream is = null;
		
		GetPasoParadaResult resultados = new GetPasoParadaResult();
		List<PasoParada> pasoParadaList = new ArrayList<PasoParada>();
		
		try {
			
			
			
			is = Conectividad.stringToStream(Conectividad.postContenido(URL, datosPost(parada)));
			
			if (is != null) {
				
				
				
				resultados = parse(is);
				
			}else{
				
				//resultados
				
			}
			
			
			
		}catch (Exception e) {

			Log.d("webservice", "Error consulta tiempos: " + linea + " - " + parada);

			e.printStackTrace();

			// Respuesta no esperada del servicio
			throw e;
			
			
		} finally {
			try {
				
				is.close();
			} catch (Exception e) {

			}
		}
		
		
		// Status
		//			String status = respuesta.get(1).toString();

	//				resultados.setStatus(status);

					//resultados.setPasoParadaList(pasoParadaList);

					return resultados;
		
		
		
	}
	
	
	/**
	 * Parsear entrada
	 * 
	 * @param is
	 * @return
	 */
	public GetPasoParadaResult parse(InputStream is) {
		// Instanciamos la fábrica para DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<PasoParada> pasoParadaList = new ArrayList<PasoParada>();

		GetPasoParadaResult resultados = new GetPasoParadaResult();

		try {
			// Creamos un nuevo parser DOM
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Realizamos lalectura completa del XML
			Document dom = builder.parse(is);

			// Nos posicionamos en el nodo principal del árbol (<kml>)
			Element root = dom.getDocumentElement();

			// Folder principal
			NodeList pasoParadaResultList = root.getElementsByTagName("PasoParada");
			
			for(int i = 0;i< pasoParadaResultList.getLength();i++){
				
				//pasoParada
				Node pasoParada = pasoParadaResultList.item(i);
				
				NodeList datosParada = pasoParada.getChildNodes();
				
				//e1 minutos
				String minutos1 = datosParada.item(1).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
			
				//e2 minutos
				String minutos2 = datosParada.item(2).getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
				
				//linea
				String linea = datosParada.item(3).getChildNodes().item(0).getNodeValue();
				
				
				//parada
				String parada = datosParada.item(4).getChildNodes().item(0).getNodeValue();
				
				//ruta
				String ruta = datosParada.item(5).getChildNodes().item(0).getNodeValue();
				
				
				
				
				PasoParada pasoP = new PasoParada();

				

				
				pasoP.getE1().setMinutos(getFormatoTiempoEspera(minutos1));

				pasoP.getE2().setMinutos(getFormatoTiempoEspera(minutos2));

				pasoP.setLinea(linea);
				pasoP.setParada(parada);
				pasoP.setRuta(ruta);

				pasoParadaList.add(pasoP);
				
				
				
				
				
				
				
				
				
				
			}
			
			
			NodeList statusList = root.getElementsByTagName("status");
			
			// Status
			String status = statusList.item(0).getChildNodes().item(0).getNodeValue();

						resultados.setStatus(status);

						resultados.setPasoParadaList(pasoParadaList);

						
			
			
			/*Element folderIda = (Element) folderPrincipalList.item(1);
			Element folderVuelta = (Element) folderPrincipalList.item(2);

			// Localizamos todos los elementos <Placemark>
			NodeList items = folderIda.getElementsByTagName("Placemark");

			datos.setPlaceMarksIda(parsePlacemarks(items));

			NodeList itemsVuelta = folderVuelta.getElementsByTagName("Placemark");

			datos.setPlaceMarksVuelta(parsePlacemarks(itemsVuelta));
*/
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		return resultados;
	}
	
	
	private String datosPost(String parada){
		
		
		String sr = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ "<soap:Body>" + "<GetPasoParada xmlns=\"http://tempuri.org/\">" +
				// '<linea>24</linea>'+
				// '<parada>4450</parada>'+
				"<parada>" + parada + "</parada>" + "<status>0</status>"
				+ "</GetPasoParada>" + "</soap:Body>" + "</soap:Envelope>";

		//oReq.setRequestHeader('Content-Type', 'text/xml; charset=utf-8');
	
		
		return sr;
		
	}
	
	
	
	
	/********************************************************************/
	
	/*
	private String SOAP_ACTION = "http://tempuri.org/GetPasoParada";
	private String METHOD_NAME = "GetPasoParada";
	private String NAMESPACE = "http://tempuri.org/";
	private String URL = "http://isaealicante.subus.es/services/dinamica.asmx";
*/
	//private int timeout = Comunes.TIMEOUT_WEBSERVICE;

	
	
	
	/**
	 * Consulta del servicioWeb y mapeo de la respuesta
	 * 
	 * @param linea
	 * @param parada
	 * @return
	 * @throws Exception
	 */
	/*public GetPasoParadaResult consultarServicio(String linea, String parada) throws Exception {

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		if (linea != null)
			request.addProperty("linea", linea);

		request.addProperty("parada", parada);

		// request.addProperty("status","1");

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		soapEnvelope.dotNet = true;
		soapEnvelope.setOutputSoapObject(request);
		HttpTransportSE aht = new HttpTransportSE(URL, timeout);

		try {
			aht.call(SOAP_ACTION, soapEnvelope);
			Vector<Object> respuesta = (Vector<Object>) soapEnvelope.getResponse();

			GetPasoParadaResult resultados = new GetPasoParadaResult();
			List<PasoParada> pasoParadaList = new ArrayList<PasoParada>();

			// Lista de lineas
			SoapObject respuestaDatos = (SoapObject) respuesta.get(0);

			int totalCount = respuestaDatos.getPropertyCount();
			if (totalCount > 0) {

				for (int detailCount = 0; detailCount < totalCount; detailCount++) {

					PasoParada pasoParada = new PasoParada();

					SoapObject contenedor = (SoapObject) respuestaDatos.getProperty(detailCount);

					String minutosPrimerBus = ((SoapObject) contenedor.getProperty(1)).getProperty("minutos").toString();
					String minutosSegundoBus = ((SoapObject) contenedor.getProperty(2)).getProperty("minutos").toString();

					pasoParada.getE1().setMinutos(this.getFormatoTiempoEspera(minutosPrimerBus));

					pasoParada.getE2().setMinutos(this.getFormatoTiempoEspera(minutosSegundoBus));

					pasoParada.setLinea(contenedor.getProperty("linea").toString());
					pasoParada.setParada(contenedor.getProperty("parada").toString());
					pasoParada.setRuta(contenedor.getProperty("ruta").toString());

					pasoParadaList.add(pasoParada);

				}
			}

			// Status
			String status = respuesta.get(1).toString();

			resultados.setStatus(status);

			resultados.setPasoParadaList(pasoParadaList);

			return resultados;

		} catch (Exception e) {

			Log.d("webservice", "Error consulta tiempos: " + linea + " - " + parada);

			e.printStackTrace();

			// Respuesta no esperada del servicio
			throw e;

		}

	}*/

	/**
	 * Forma string con los minutos faltantes y la hora aproximada de llegada
	 * 
	 * @param minutosLlegada
	 * @return
	 */
	private String getFormatoTiempoEspera(String minutosLlegada) {

		String formatoMinHora = "";

		GregorianCalendar cl = new GregorianCalendar();
		cl.setTimeInMillis((new Date()).getTime());
		cl.add(Calendar.MINUTE, Integer.parseInt(minutosLlegada));

		SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
		String horaString = sf.format(cl.getTime());

		formatoMinHora = minutosLlegada + " min. (" + horaString + ")";

		return formatoMinHora;

	}

}
