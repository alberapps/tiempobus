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
package alberapps.java.tram.webservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import alberapps.android.tiempobus.util.Comunes;

public class GetPasoParadaWebservice {

    private int timeout = Comunes.TIMEOUT_WEBSERVICE;

    public static final int URL1 = 1;
    public static final int URL2 = 2;

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
/*	public GetPasoParadaResult consultarServicio(String linea, String parada, int consulta) throws Exception {

		SoapObject request = new SoapObject(DatosTRAM.NAMESPACE, DatosTRAM.METHOD_NAME_PASO_PARADA);

		if (linea != null)
			request.addProperty("linea", linea);

		// request.addProperty("linea", "L1");

		request.addProperty("parada", parada);

		// request.addProperty("status","1");

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		soapEnvelope.dotNet = true;
		soapEnvelope.setOutputSoapObject(request);

		String url = null;

		if (consulta == 1) {
			url = DatosTRAM.URL_1_DINAMICA;
		} else {
			url = DatosTRAM.URL_2_DINAMICA;
		}

		HttpTransportSE aht = new HttpTransportSE(url, timeout);

		try {
			aht.call(DatosTRAM.SOAP_ACTION_PASO_PARADA, soapEnvelope);

			// Object resp = soapEnvelope.getResponse();

			// resp

			// Vector<Object> respuesta = (Vector<Object>)
			// soapEnvelope.getResponse();

			Object respuesta = soapEnvelope.getResponse();

			GetPasoParadaResult resultados = new GetPasoParadaResult();
			List<PasoParada> pasoParadaList = new ArrayList<PasoParada>();

			// Status
			// String status = respuesta.get(1).toString();

			// Lista de lineas
			// SoapObject respuestaDatos = (SoapObject) respuesta.get(0);
			SoapObject respuestaDatos = (SoapObject) respuesta;

			int totalCount = 0;

			try {
				totalCount = respuestaDatos.getPropertyCount();
			} catch (NullPointerException e) {
				// Sin resultados
			}

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

        SimpleDateFormat sf = new SimpleDateFormat("HH:mm", Locale.US);
        String horaString = sf.format(cl.getTime());

        formatoMinHora = minutosLlegada + " min. (" + horaString + ")";

        return formatoMinHora;

    }

}
