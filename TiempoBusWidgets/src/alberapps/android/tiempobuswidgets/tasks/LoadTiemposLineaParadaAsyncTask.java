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
package alberapps.android.tiempobuswidgets.tasks;

import java.util.ArrayList;
import java.util.List;

import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.ProcesarTiemposService;
import android.os.AsyncTask;

/**
 * Tarea asincrona que se encarga de consultar los tiempos para una linea y
 * parada
 * 
 * 
 */
public class LoadTiemposLineaParadaAsyncTask extends AsyncTask<String, Void, List<BusLlegada>> {

	/**
	 * Interfaz que deberian implementar las clases que la quieran usar Sirve
	 * como callback una vez termine la tarea asincrona
	 * 
	 */
	public interface LoadTiemposLineaParadaAsyncTaskResponder {
		public void tiemposLoaded(List<BusLlegada> buses);
	}

	private LoadTiemposLineaParadaAsyncTaskResponder responder;

	/**
	 * Constructor. Es necesario que nos pasen un objeto para el callback
	 * 
	 * @param responder
	 */
	public LoadTiemposLineaParadaAsyncTask(LoadTiemposLineaParadaAsyncTaskResponder responder) {
		this.responder = responder;
	}

	/**
	 * Ejecuta el proceso en segundo plano
	 */
	@Override
	protected List<BusLlegada> doInBackground(String... datos) {
		List<BusLlegada> llegadasBus = null;
		try {

			String[] lineasParadaList = datos[0].split(";");

			BusLlegada llegadaBus = null;

			llegadasBus = new ArrayList<BusLlegada>();

			for (int i = 0; i < lineasParadaList.length; i++) {

				String[] lineaParada = lineasParadaList[i].split(",");

				llegadaBus = ProcesarTiemposService.getPosteConLinea(lineaParada[0], lineaParada[1]);

				if (llegadaBus == null) {

					llegadaBus = new BusLlegada(lineaParada[0], "", "Sin información", lineaParada[1]);

				}

				llegadasBus.add(llegadaBus);

			}

		} catch (Exception e) {
			return null;
		}

		return llegadasBus;
	}

	/**
	 * Se ha terminado la ejecucion comunicamos el resultado al llamador
	 */
	@Override
	protected void onPostExecute(List<BusLlegada> result) {
		if (responder != null) {
			responder.tiemposLoaded(result);
		}
	}

}
