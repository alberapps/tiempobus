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

import java.util.ArrayList;

import alberapps.java.tam.BusLinea;
import alberapps.java.tam.UtilidadesTAM;
import alberapps.java.tam.lineas.ProcesarDatosLineasService;
import android.os.AsyncTask;

/**
 * Cargar informacion de las lineas
 * 
 * 
 */
public class LoadDatosLineasAsyncTask extends AsyncTask<Void, Void, ArrayList<BusLinea>> {
	
	/**
	 * Interfaz que deberin implementar las clases que la quieran usar
	 * Sirve como callback una vez termine la tarea asincrona
	 * 
	 */
	public interface LoadDatosLineasAsyncTaskResponder {
	    public void busesLoaded(ArrayList<BusLinea> buses);
	  }
	private LoadDatosLineasAsyncTaskResponder responder;
	
	/**
	 * Constructor. Es necesario que nos pasen un objeto para el callback
	 * 
	 * @param responder
	 */
	public LoadDatosLineasAsyncTask(LoadDatosLineasAsyncTaskResponder responder) {
		this.responder = responder;
	}
	
	/**
	 * Ejecuta el proceso en segundo plano
	 */
	@Override
	protected ArrayList<BusLinea> doInBackground(Void... datos) {
		ArrayList<BusLinea> lineasBus = null;
		try {
			
			
			//lineasBus = ProcesarLineasService.getLineasBus();
			
			//ProcesarDatosLineasService.getLineasInfo();
			
				
			lineasBus = new ArrayList<BusLinea>();

			for (int i = 0; i < UtilidadesTAM.LINEAS_CODIGO_KML.length; i++) {

				lineasBus.add(new BusLinea(UtilidadesTAM.LINEAS_CODIGO_KML[i], UtilidadesTAM.LINEAS_DESCRIPCION[i], UtilidadesTAM.LINEAS_NUM[i]));

			}
			
		} catch (Exception e) {
			lineasBus = null;
		}
		
		return lineasBus;
	}

	/**
	 * Se ha terminado la ejecucion comunicamos el resultado al llamador
	 */
	@Override
	protected void onPostExecute(ArrayList<BusLinea> result) {
		if(responder != null) {
			responder.busesLoaded(result);
		}
	}

	
}
