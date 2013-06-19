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

import alberapps.java.noticias.Noticias;
import alberapps.java.noticias.ProcesarDetalleNoticia;
import android.os.AsyncTask;

/**
 * Tarea asincrona que se encarga de consultar las noticias
 * 
 * 
 */
public class LoadDetalleNoticiaAsyncTask extends AsyncTask<String, Void, Noticias> {
	
	/**
	 * Interfaz que deberian implementar las clases que la quieran usar
	 * Sirve como callback una vez termine la tarea asincrona
	 * 
	 */
	public interface LoadDetalleNoticiaAsyncTaskResponder {
	    public void detalleNoticiaLoaded(Noticias noticias);
	  }
	private LoadDetalleNoticiaAsyncTaskResponder responder;
	
	/**
	 * Constructor. Es necesario que nos pasen un objeto para el callback
	 * 
	 * @param responder
	 */
	public LoadDetalleNoticiaAsyncTask(LoadDetalleNoticiaAsyncTaskResponder responder) {
		this.responder = responder;
	}
	
	/**
	 * Ejecuta el proceso en segundo plano
	 */
	@Override
	protected Noticias doInBackground(String... datos) {
		Noticias noticia = null;
		try {
			//noticiasList = TamNews.getTamNews();
			
			noticia = ProcesarDetalleNoticia.getDetalleNoticia(datos[0]);
			
		} catch (Exception e) {
			return null;
		}
		
		return noticia;
	}

	/**
	 * Se ha terminado la ejecucion comunicamos el resultado al llamador
	 */
	@Override
	protected void onPostExecute(Noticias result) {
		if(responder != null) {
			responder.detalleNoticiaLoaded(result);
		}
	}

	
}
