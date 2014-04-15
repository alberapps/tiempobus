/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import alberapps.android.tiempobus.util.Comunes;

public class GetVehiculosWebservice {

	private String SOAP_ACTION = "http://tempuri.org/GetVehiculos";
	private String METHOD_NAME = "GetVehiculos";
	private String NAMESPACE = "http://tempuri.org/";
	private String URL = "http://isaealicante.subus.es/services/dinamica.asmx";

	private int timeout = Comunes.TIMEOUT_WEBSERVICE;

	public GetVehiculosWebservice() {
	}

	/**
	 * Consulta del servicioWeb y mapeo de la respuesta
	 * 
	 * @param linea
	 * @param parada
	 * @return
	 * @throws Exception
	 */
/*	public GetVehiculosResult consultarServicio(String linea) throws Exception {

		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		request.addProperty("linea", linea);

		// request.addProperty("status","1");

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		soapEnvelope.dotNet = true;
		soapEnvelope.setOutputSoapObject(request);
		HttpTransportSE aht = new HttpTransportSE(URL, timeout);

		try {
			aht.call(SOAP_ACTION, soapEnvelope);
			// Vector<Object> respuesta = (Vector<Object>)
			// soapEnvelope.getResponse();

			SoapObject respuesta = (SoapObject) soapEnvelope.getResponse();

			GetVehiculosResult resultados = new GetVehiculosResult();
			List<InfoVehiculo> infoVehiculoList = new ArrayList<InfoVehiculo>();

			// Lista de lineas
			SoapObject respuestaDatos = respuesta;

			int totalCount = respuestaDatos.getPropertyCount();
			if (totalCount > 0) {

				for (int detailCount = 0; detailCount < totalCount; detailCount++) {

					InfoVehiculo infoVehiculo = new InfoVehiculo();

					SoapObject contenedor = (SoapObject) respuestaDatos.getProperty(detailCount);

					infoVehiculo.setVehiculo(contenedor.getProperty("vehiculo").toString());

					infoVehiculo.setLinea(contenedor.getProperty("linea").toString());

					infoVehiculo.setSublinea(contenedor.getProperty("sublinea").toString());

					infoVehiculo.setCoche(contenedor.getProperty("coche").toString());

					infoVehiculo.setServBus(contenedor.getProperty("serv_bus").toString());

					infoVehiculo.setConductor(contenedor.getProperty("conductor").toString());

					infoVehiculo.setServCond(contenedor.getProperty("serv_cond").toString());

					infoVehiculo.setEstado(contenedor.getProperty("estado").toString());

					infoVehiculo.setEstadoLocReal(contenedor.getProperty("estadoLocReal").toString());

					infoVehiculo.setXcoord(contenedor.getProperty("xcoord").toString());

					infoVehiculo.setYcoord(contenedor.getProperty("ycoord").toString());

					infoVehiculoList.add(infoVehiculo);

				}
			}

			resultados.setInfoVehiculoList(infoVehiculoList);

			return resultados;

		} catch (Exception e) {

			Log.d("webservice", "Error consulta vehiculos: " + linea);

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

		SimpleDateFormat sf = new SimpleDateFormat("HH:mm", Locale.US);
		String horaString = sf.format(cl.getTime());

		formatoMinHora = minutosLlegada + " min. (" + horaString + ")";

		return formatoMinHora;

	}

}
