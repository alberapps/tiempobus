/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
 *  based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.android.tiempobus.tasks;

import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.ProcesarMapaService;
import alberapps.java.tam.mapas.ProcesarMapaServiceV3;
import android.os.AsyncTask;

/**
 * Tarea para la carga de datos de los mapas
 * 
 * 
 */
public class LoadDatosMapaV3AsyncTask extends AsyncTask<String, Void, DatosMapa[]> {

	/**
	 * Interfaz que deberian implementar las clases que la quieran usar Sirve
	 * como callback una vez termine la tarea asincrona
	 * 
	 */
	public interface LoadDatosMapaV3AsyncTaskResponder {
		public void datosMapaV3Loaded(DatosMapa[] datosMapa);
	}

	private LoadDatosMapaV3AsyncTaskResponder responder;

	/**
	 * Constructor. Es necesario que nos pasen un objeto para el callback
	 * 
	 * @param responder
	 */
	public LoadDatosMapaV3AsyncTask(LoadDatosMapaV3AsyncTaskResponder responder) {
		this.responder = responder;
	}

	/**
	 * Ejecuta el proceso en segundo plano
	 */
	@Override
	protected DatosMapa[] doInBackground(String... datos) {
		DatosMapa[] datosMapa = { null, null };
		try {

			if (datos != null && datos.length > 0 && datos[0] != null) {

				DatosMapa[] paradas = ProcesarMapaServiceV3.getDatosMapa(datos[0]);

				datosMapa[0] = paradas[0];
				datosMapa[1] = paradas[1];

				if (datos.length == 2) {

					String[] recorridos = ProcesarMapaServiceV3.getDatosMapaRecorrido(datos[1]);

					datosMapa[0].setRecorrido(recorridos[0]);
					datosMapa[1].setRecorrido(recorridos[1]);

				}

			} else {
				return datosMapa;
			}

		} catch (Exception e) {

			e.printStackTrace();

			return datosMapa;
		}

		return datosMapa;
	}

	/**
	 * Se ha terminado la ejecucion comunicamos el resultado al llamador
	 */
	@Override
	protected void onPostExecute(DatosMapa[] result) {
		if (responder != null) {
			responder.datosMapaV3Loaded(result);
		}
	}

}
