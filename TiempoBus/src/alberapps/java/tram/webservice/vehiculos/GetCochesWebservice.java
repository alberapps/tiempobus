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
package alberapps.java.tram.webservice.vehiculos;

import alberapps.android.tiempobus.util.Comunes;

public class GetCochesWebservice {

	private int timeout = Comunes.TIMEOUT_WEBSERVICE;

	public static final int URL1 = 1;
	public static final int URL2 = 2;

	public GetCochesWebservice() {
	}

	/**
	 * Consulta del servicioWeb y mapeo de la respuesta
	 * 
	 * @param linea
	 * @param parada
	 * @return
	 * @throws Exception
	 */
/*	public GetCochesResult consultarServicio(String linea, int consulta) throws Exception {

		SoapObject request = new SoapObject(DatosTRAM.NAMESPACE, DatosTRAM.METHOD_NAME);

		request.addProperty("linea", linea);

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		String url = null;

		if (consulta == 1) {
			url = DatosTRAM.URL_1;
		} else {
			url = DatosTRAM.URL_2;
		}

		soapEnvelope.dotNet = true;
		soapEnvelope.setOutputSoapObject(request);
		HttpTransportSE aht = new HttpTransportSE(url, timeout);

		try {
			aht.call(DatosTRAM.SOAP_ACTION, soapEnvelope);
			// Vector<Object> respuesta = (Vector<Object>)
			// soapEnvelope.getResponse();

			SoapObject respuesta = (SoapObject) soapEnvelope.getResponse();

			GetCochesResult resultados = new GetCochesResult();
			List<InfoCoche> infoCocheList = new ArrayList<InfoCoche>();

			// Lista de lineas
			SoapObject respuestaDatos = respuesta;

			int totalCount = respuestaDatos.getPropertyCount();
			if (totalCount > 0) {

				for (int detailCount = 0; detailCount < totalCount; detailCount++) {

					InfoCoche infoCoche = new InfoCoche();

					SoapObject contenedor = (SoapObject) respuestaDatos.getProperty(detailCount);

					infoCoche.setCoche(contenedor.getProperty("coche").toString());

					infoCoche.setVehiculo(contenedor.getProperty("vehiculo").toString());

					infoCoche.setServBus(contenedor.getProperty("serv_bus").toString());

					infoCoche.setConductor(contenedor.getProperty("conductor").toString());

					infoCoche.setServCond(contenedor.getProperty("serv_cond").toString());

					infoCoche.setEstado(contenedor.getProperty("estado").toString());

					infoCoche.setEstadoLocReal(contenedor.getProperty("estadoLocReal").toString());

					SoapObject posReal = (SoapObject) contenedor.getProperty("pos_real");

					infoCoche.setOffset(posReal.getProperty("offset").toString());

					infoCoche.setSeccion(posReal.getProperty("seccion").toString());

					infoCoche.setXcoord(posReal.getProperty("x").toString());

					infoCoche.setYcoord(posReal.getProperty("y").toString());

					infoCocheList.add(infoCoche);

				}
			}

			Log.d("vehiculos", "servicio coches: " + infoCocheList.size());

			resultados.setInfoVehiculoList(infoCocheList);

			return resultados;

		} catch (Exception e) {

			Log.d("webservice", "Error consulta coches: " + linea);

			e.printStackTrace();

			// Respuesta no esperada del servicio
			throw e;

		}

		
		
	}*/

}
