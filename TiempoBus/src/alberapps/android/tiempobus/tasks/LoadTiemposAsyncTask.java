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
import java.util.Random;

import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.DatosRespuesta;
import alberapps.java.tam.ProcesarTiemposService;
import alberapps.java.tram.ProcesarTiemposTramIsaeService;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.tram.webservice.GetPasoParadaWebservice;
import alberapps.java.util.Utilidades;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Tarea asincrona que se encarga de consultar los tiempos
 * 
 * 
 */
public class LoadTiemposAsyncTask extends AsyncTask<Object, Void, DatosRespuesta> {

	/**
	 * Interfaz que deberian implementar las clases que la quieran usar Sirve
	 * como callback una vez termine la tarea asincrona
	 * 
	 */
	public interface LoadTiemposAsyncTaskResponder {
		public void tiemposLoaded(DatosRespuesta buses);
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
	protected DatosRespuesta doInBackground(Object... datos) {
		ArrayList<BusLlegada> llegadasBus = null;
		DatosRespuesta datosRespuesta = new DatosRespuesta();

		String parada = null;
		int paradaI = 0;

		parada = ((Integer) datos[0]).toString();

		paradaI = (Integer) datos[0];

		int url1 = 1;
		int url2 = 1;

		if (DatosPantallaPrincipal.esTram(parada)) {

			// Verificar linea 9
			if (!UtilidadesTRAM.ACTIVADO_L9 && UtilidadesTRAM.esParadaL9(parada)) {
				return null;
			}

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

		try {

			// llegadasBus =
			// ProcesarTiemposService.procesaTiemposLlegada(datos[0]);

			// Context contexto = (Context) datos[1];

			if (DatosPantallaPrincipal.esTram(parada)) {
				// llegadasBus =
				// ProcesarTiemposTramService.procesaTiemposLlegada(contexto,paradaI);

				llegadasBus = ProcesarTiemposTramIsaeService.procesaTiemposLlegada(paradaI, url1);
			} else {
				llegadasBus = ProcesarTiemposService.procesaTiemposLlegada(paradaI);
			}

			datosRespuesta.setListaBusLlegada(llegadasBus);

		} catch (EOFException e1) {

			e1.printStackTrace();

			return null;

		} catch (TiempoBusException e) {

			datosRespuesta.setError(e.getCodigo());
			datosRespuesta.setListaBusLlegada(new ArrayList<BusLlegada>());

			e.printStackTrace();

		} catch (Exception e) {

			// Probar con acceso secundario
			if (DatosPantallaPrincipal.esTram(parada)) {

				try {

					Log.d("TIEMPOS", "Accede a la segunda ruta de tram");

					llegadasBus = ProcesarTiemposTramIsaeService.procesaTiemposLlegada(paradaI, url2);

					datosRespuesta.setListaBusLlegada(llegadasBus);
				} catch (Exception e1) {

					e1.printStackTrace();

					return null;

				}
			} else {

				return null;

			}
			
			e.printStackTrace();
			
		}

		return datosRespuesta;
	}

	/**
	 * Se ha terminado la ejecucion comunicamos el resultado al llamador
	 */
	@Override
	protected void onPostExecute(DatosRespuesta result) {
		if (responder != null) {
			responder.tiemposLoaded(result);
		}
	}

}
