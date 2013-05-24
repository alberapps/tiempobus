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

import java.io.EOFException;
import java.util.ArrayList;

import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.ProcesarTiemposService;
import alberapps.java.tram.ProcesarTiemposTramService;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Tarea asincrona que se encarga de consultar los tiempos
 * 
 * 
 */
public class LoadTiemposAsyncTask extends AsyncTask<Integer, Void, ArrayList<BusLlegada>> {

	/**
	 * Interfaz que deberian implementar las clases que la quieran usar Sirve
	 * como callback una vez termine la tarea asincrona
	 * 
	 */
	public interface LoadTiemposAsyncTaskResponder {
		public void tiemposLoaded(ArrayList<BusLlegada> buses);
	}

	private LoadTiemposAsyncTaskResponder responder;

	/**
	 * Constructor. Es necesario que nos pasen un objeto para el callback
	 * 
	 * @param responder
	 */
	public LoadTiemposAsyncTask(LoadTiemposAsyncTaskResponder responder) {
		this.responder = responder;
	}

	/**
	 * Ejecuta el proceso en segundo plano
	 */
	@Override
	protected ArrayList<BusLlegada> doInBackground(Integer... datos) {
		ArrayList<BusLlegada> llegadasBus = null;
		try {
			
			
			//llegadasBus = ProcesarTiemposService.procesaTiemposLlegada(datos[0]);
			
			String parada = ((Integer)datos[0]).toString();
			
			if(parada.length() == 4){
				llegadasBus = ProcesarTiemposService.procesaTiemposLlegada(datos[0]);
			}else if( parada.length() < 4){
				llegadasBus = ProcesarTiemposTramService.procesaTiemposLlegada(datos[0]);
			}
			
			
			
			
		} catch (EOFException e1) {

			Log.d("tiempos", "Tiempos error intento 1");

			try {

				Log.d("tiempos", "Tiempos intento 2");

				llegadasBus = ProcesarTiemposService.procesaTiemposLlegada(datos[0]);
			} catch (Exception e2) {

				Log.d("tiempos", "Tiempos error intento 2");

				return null;
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
	protected void onPostExecute(ArrayList<BusLlegada> result) {
		if (responder != null) {
			responder.tiemposLoaded(result);
		}
	}

}
