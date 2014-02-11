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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import alberapps.android.tiempobus.util.Comunes;

public class GetPasoParadaWebservice {

	private String SOAP_ACTION = "http://tempuri.org/GetPasoParada";
	private String METHOD_NAME = "GetPasoParada";
	private String NAMESPACE = "http://tempuri.org/";
	private String URL = "http://isaealicante.subus.es/services/dinamica.asmx";

	private int timeout = Comunes.TIMEOUT_WEBSERVICE;

	public GetPasoParadaWebservice() {
	}

	/**
	 * Consulta del servicioWeb y mapeo de la respuesta
	 * 
	 * @param linea
	 * @param parada
	 * @return
	 * @throws Exception
	 */
/*	public GetPasoParadaResult consultarServicio(String linea, String parada) throws Exception {

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
