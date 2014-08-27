/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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

import android.os.AsyncTask;

import alberapps.java.noticias.Noticias;
import alberapps.java.noticias.ProcesarDetalleNoticia;

/**
 * Tarea asincrona que se encarga de consulta del detalle de las noticias
 */
public class LoadDetalleNoticiaAsyncTask extends AsyncTask<String, Void, Noticias> {

    public interface LoadDetalleNoticiaAsyncTaskResponder {
        public void detalleNoticiaLoaded(Noticias noticias);
    }

    private LoadDetalleNoticiaAsyncTaskResponder responder;

    public LoadDetalleNoticiaAsyncTask(LoadDetalleNoticiaAsyncTaskResponder responder) {
        this.responder = responder;
    }

    @Override
    protected Noticias doInBackground(String... datos) {
        Noticias noticia = null;
        try {

            noticia = ProcesarDetalleNoticia.getDetalleNoticia(datos[0]);

        } catch (Exception e) {
            return null;
        }

        return noticia;
    }

    @Override
    protected void onPostExecute(Noticias result) {
        if (responder != null) {
            responder.detalleNoticiaLoaded(result);
        }
    }


}
