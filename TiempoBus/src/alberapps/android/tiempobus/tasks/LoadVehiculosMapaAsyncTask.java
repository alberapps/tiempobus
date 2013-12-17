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

import java.util.List;

import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.vehiculos.ProcesarVehiculosService;
import alberapps.java.tam.webservice.vehiculos.InfoVehiculo;
import alberapps.java.tram.vehiculos.ProcesarCochesService;
import alberapps.java.tram.webservice.GetPasoParadaWebservice;
import alberapps.java.util.Utilidades;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Tarea para la carga de datos de los mapas
 * 
 * 
 */
public class LoadVehiculosMapaAsyncTask extends AsyncTask<String, Void, DatosMapa> {

	/**
	 * Interfaz que deberian implementar las clases que la quieran usar Sirve
	 * como callback una vez termine la tarea asincrona
	 * 
	 */
	public interface LoadVehiculosMapaAsyncTaskResponder {
		public void vehiculosMapaLoaded(DatosMapa datosMapa);
	}

	private LoadVehiculosMapaAsyncTaskResponder responder;

	/**
	 * Constructor. Es necesario que nos pasen un objeto para el callback
	 * 
	 * @param responder
	 */
	public LoadVehiculosMapaAsyncTask(LoadVehiculosMapaAsyncTaskResponder responder) {
		this.responder = responder;
	}

	/**
	 * Ejecuta el proceso en segundo plano
	 */
	@Override
	protected DatosMapa doInBackground(String... datos) {
		DatosMapa datosMapa = null;

		String linea = null;

		int url1 = 1;
		int url2 = 1;

		linea = datos[0];

		if (DatosPantallaPrincipal.esLineaTram(linea)) {

			// Ip a usar de forma aleatoria
			boolean iprandom = Utilidades.ipRandom();

			if (iprandom) {

				url1 = GetPasoParadaWebservice.URL1;
				url2 = GetPasoParadaWebservice.URL2;

				Log.d("TIEMPOS", "Combinacion url 1");

			} else {

				url2 = GetPasoParadaWebservice.URL1;
				url1 = GetPasoParadaWebservice.URL2;

				Log.d("TIEMPOS", "Combinacion url 2");

			}

		}

		List<InfoVehiculo> vehiculosList = null;

		try {

			if (DatosPantallaPrincipal.esLineaTram(linea)) {

				Log.d("mapas", "Procesar vehiculos tram: " + datos[0]);

				vehiculosList = ProcesarCochesService.procesaVehiculos(datos[0], url1);

				Log.d("mapas", "vehiculos recuperados: " + vehiculosList.size());

			} else {
				vehiculosList = ProcesarVehiculosService.procesaVehiculos(datos[0]);
			}

			datosMapa = new DatosMapa();

			datosMapa.setVehiculosList(vehiculosList);

		} catch (Exception e) {
			// Probar con acceso secundario
			if (DatosPantallaPrincipal.esLineaTram(linea)) {

				try {

					Log.d("TIEMPOS", "Accede a la segunda ruta de tram");

					vehiculosList = ProcesarCochesService.procesaVehiculos(datos[0], url2);

					datosMapa = new DatosMapa();

					datosMapa.setVehiculosList(vehiculosList);

				} catch (Exception e1) {

					e1.printStackTrace();

					return null;

				}
			} else {

				return null;

			}
			
			e.printStackTrace();
			
		}

		return datosMapa;
	}

	/**
	 * Se ha terminado la ejecucion comunicamos el resultado al llamador
	 */
	@Override
	protected void onPostExecute(DatosMapa result) {
		if (responder != null) {
			responder.vehiculosMapaLoaded(result);
		}
	}

}
