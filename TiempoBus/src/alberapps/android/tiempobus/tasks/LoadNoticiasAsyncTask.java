/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.tasks;

import android.os.AsyncTask;

import java.util.List;

import alberapps.java.noticias.Noticias;
import alberapps.java.noticias.ProcesarNoticias;

/**
 * Tarea asincrona que se encarga de consultar las noticias
 */
public class LoadNoticiasAsyncTask extends AsyncTask<Object, Void, List<Noticias>> {


    public interface LoadNoticiasAsyncTaskResponder {
        public void noticiasLoaded(List<Noticias> noticias);
    }

    private LoadNoticiasAsyncTaskResponder responder;


    public LoadNoticiasAsyncTask(LoadNoticiasAsyncTaskResponder responder) {
        this.responder = responder;
    }


    @Override
    protected List<Noticias> doInBackground(Object... datos) {
        List<Noticias> noticiasList = null;
        try {

            Boolean usarCache = true;

            if (datos.length == 1) {

                usarCache = (Boolean) datos[0];

            }

            String userAgent = null;

            if (datos.length == 2) {
                userAgent = (String) datos[1];
            }


            noticiasList = ProcesarNoticias.getTamNews(usarCache, userAgent);

        } catch (Exception e) {
            return null;
        }

        return noticiasList;
    }


    @Override
    protected void onPostExecute(List<Noticias> result) {
        if (responder != null) {
            responder.noticiasLoaded(result);
        }

    }

}
