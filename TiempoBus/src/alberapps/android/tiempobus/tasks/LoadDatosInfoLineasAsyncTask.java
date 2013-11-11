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
package alberapps.android.tiempobus.tasks;

import alberapps.android.tiempobus.infolineas.DatosInfoLinea;
import alberapps.java.tam.UtilidadesTAM;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.ProcesarMapaService;
import alberapps.java.tam.mapas.ProcesarMapaServiceV3;
import android.os.AsyncTask;

/**
 * Consulta asincrona del track de la linea
 * 
 * 
 */
public class LoadDatosInfoLineasAsyncTask extends AsyncTask<DatosInfoLinea, Void, DatosInfoLinea> {

	public interface LoadDatosInfoLineasAsyncTaskResponder {
		public void datosInfoLineasLoaded(DatosInfoLinea datos);
	}

	private LoadDatosInfoLineasAsyncTaskResponder responder;

	public LoadDatosInfoLineasAsyncTask(LoadDatosInfoLineasAsyncTaskResponder responder) {
		this.responder = responder;
	}

	@Override
	protected DatosInfoLinea doInBackground(DatosInfoLinea... datos) {
		DatosInfoLinea datosVuelta = new DatosInfoLinea();
		try {

			if (!UtilidadesTAM.ACTIVAR_MAPS_V3) {
				datosVuelta.setResult(ProcesarMapaService.getDatosMapa(datos[0].getUrl()));
			} else {

				DatosMapa[] paradas = ProcesarMapaServiceV3.getDatosMapa(datos[0].getUrl());

				datosVuelta.setResultIda(paradas[0]);
				datosVuelta.setResultVuelta(paradas[1]);
			}

		} catch (Exception e) {
			return null;
		}

		return datosVuelta;
	}

	@Override
	protected void onPostExecute(DatosInfoLinea result) {
		if (responder != null) {
			responder.datosInfoLineasLoaded(result);
		}
	}

}
