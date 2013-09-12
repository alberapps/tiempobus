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

import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.vehiculos.ProcesarVehiculosService;
import alberapps.java.tam.webservice.vehiculos.InfoVehiculo;
import android.os.AsyncTask;

/**
 * Tarea para la carga de datos de los mapas
 * 
 * 
 */
public class LoadVehiculosMapaAsyncTask extends AsyncTask<String, Void, DatosMapa> {
	
	/**
	 * Interfaz que deberian implementar las clases que la quieran usar
	 * Sirve como callback una vez termine la tarea asincrona
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
		try {
			List<InfoVehiculo> vehiculosList = ProcesarVehiculosService.procesaVehiculos(datos[0]);
			
			datosMapa = new DatosMapa();
			
			datosMapa.setVehiculosList(vehiculosList);
			
		} catch (Exception e) {
			return null;
		}
		
		return datosMapa;
	}

	/**
	 * Se ha terminado la ejecucion comunicamos el resultado al llamador
	 */
	@Override
	protected void onPostExecute(DatosMapa result) {
		if(responder != null) {
			responder.vehiculosMapaLoaded(result);
		}
	}

	
}
