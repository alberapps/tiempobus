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

import android.content.Context;
import android.os.AsyncTask;

import alberapps.java.localizacion.GeocoderInfo;
import alberapps.java.localizacion.Localizacion;

/**
 * Tarea asincrona para recuperar informacion de localizacion
 * 
 * 
 */
public class LoadLocationAsyncTask extends AsyncTask<Object, Void, Localizacion> {

	/**
	 *
	 *
	 */
	public interface LoadLocationAsyncTaskResponder {
		public void LocationLoaded(Localizacion localizacion);
	}

	private LoadLocationAsyncTaskResponder responder;

	/**
	 *
	 *
	 * @param responder
	 */
	public LoadLocationAsyncTask(LoadLocationAsyncTaskResponder responder) {
		this.responder = responder;
	}

	/**
	 * 
	 */
	@Override
	protected Localizacion doInBackground(Object... datos) {
		Localizacion localizacion = null;
		try {

			String lat = (String) datos[0];
			String lon = (String) datos[1];

            Context context = (Context) datos[2];

            localizacion = GeocoderInfo.getDatosLocalizacion(lat, lon, context);



		} catch (Exception e) {

			e.printStackTrace();

			return null;

		}

		return localizacion;
	}

	/**
	 * 
	 */
	@Override
	protected void onPostExecute(Localizacion result) {
		if (responder != null) {
			responder.LocationLoaded(result);
		}

	}

}
