/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
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

import alberapps.java.datos.Datos;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.ProcesarTiemposService;
import android.os.AsyncTask;

/**
 * Tarea asincrona que se encarga de consultar los tiempos para una linea y
 * parada
 * 
 * 
 */
public class LoadTiemposLineaParadaAsyncTask extends AsyncTask<List<Datos>, Void, List<BusLlegada>> {

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
	protected List<BusLlegada> doInBackground(List<Datos>... datos) {
		List<BusLlegada> llegadasBus = null;
		try {

			if (datos == null || datos[0] == null) {
				return null;
			}

			List<Datos> lineasParadaList = datos[0];

			BusLlegada llegadaBus = null;

			llegadasBus = new ArrayList<BusLlegada>();

			for (int i = 0; i < lineasParadaList.size(); i++) {

				llegadaBus = ProcesarTiemposService.getPosteConLinea(lineasParadaList.get(i).getLinea(), lineasParadaList.get(i).getParada());

				if (llegadaBus == null) {

					// Sin datos
					llegadaBus = new BusLlegada(lineasParadaList.get(i).getLinea(), "", "", lineasParadaList.get(i).getParada());

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
