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
package alberapps.android.tiempobus.tasks;

import alberapps.java.actualizador.DescargarActualizaBD;
import alberapps.java.weather.ProcesarDatosWeatherService;
import alberapps.java.weather.WeatherQuery;
import android.os.AsyncTask;

/**
 * Tarea asincrona para recuperar informacion de wikipedia
 * 
 * 
 */
public class ActualizarBDAsyncTask extends AsyncTask<Object, Void, Boolean> {

	/**
	 * 
	 *
	 */
	public interface LoadActualizarBDAsyncTaskResponder {
		public void ActualizarBDLoaded(Boolean resultado);
	}

	private LoadActualizarBDAsyncTaskResponder responder;

	/**
	 * 
	 * 
	 * @param responder
	 */
	public ActualizarBDAsyncTask(LoadActualizarBDAsyncTaskResponder responder) {
		this.responder = responder;
	}

	/**
	 * 
	 */
	@Override
	protected Boolean doInBackground(Object... datos) {
		
		try {

			
			DescargarActualizaBD.descargarArchivo();

		} catch (Exception e) {

			e.printStackTrace();

			return null;

		}

		return true;
	}

	/**
	 * 
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		if (responder != null) {
			responder.ActualizarBDLoaded(result);
		}

	}

}
