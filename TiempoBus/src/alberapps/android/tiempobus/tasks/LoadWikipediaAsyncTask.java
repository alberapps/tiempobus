/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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

import alberapps.java.wikipedia.ProcesarDatosWikipediaService;
import alberapps.java.wikipedia.WikiQuery;

/**
 * Tarea asincrona para recuperar informacion de wikipedia
 */
public class LoadWikipediaAsyncTask extends AsyncTask<Object, Void, WikiQuery> {

    /**
     *
     *
     */
    public interface LoadWikipediaAsyncTaskResponder {
        void WikipediaLoaded(WikiQuery Wikipedia);
    }

    private LoadWikipediaAsyncTaskResponder responder;

    /**
     * @param responder
     */
    public LoadWikipediaAsyncTask(LoadWikipediaAsyncTaskResponder responder) {
        this.responder = responder;
    }

    /**
     *
     */
    @Override
    protected WikiQuery doInBackground(Object... datos) {
        WikiQuery wiki = null;
        try {

            String lat = (String) datos[0];
            String lon = (String) datos[1];

            wiki = ProcesarDatosWikipediaService.getDatosWikiLatLon(lat, lon);

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

        return wiki;
    }

    /**
     *
     */
    @Override
    protected void onPostExecute(WikiQuery result) {
        if (responder != null) {
            responder.WikipediaLoaded(result);
        }

    }

}
