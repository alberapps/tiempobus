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

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import alberapps.java.noticias.rss.NoticiaRss;
import alberapps.java.tram.news.AlberappsNewsParser;
import alberapps.java.tram.news.TramNewsParser;

/**
 * Tarea asincrona que se encarga de consultar las noticias rss del tram
 */
public class LoadNoticiasRssAsyncTask extends AsyncTask<Object, Void, List<NoticiaRss>> {


    public interface LoadNoticiasRssAsyncTaskResponder {
        void noticiasRssLoaded(List<NoticiaRss> noticias);
    }

    private LoadNoticiasRssAsyncTaskResponder responder;


    public LoadNoticiasRssAsyncTask(LoadNoticiasRssAsyncTaskResponder responder) {
        this.responder = responder;
    }


    @Override
    protected List<NoticiaRss> doInBackground(Object... datos) {
        List<NoticiaRss> noticiasList = null;
        try {

            //String idioma = UtilidadesUI.getIdiomaRssTram();


            //Uri.Builder builder = new Uri.Builder();
            //builder.scheme("https").authority("www.tramalicante.es").appendPath("rss.php")
              //      .appendQueryParameter("idioma", idioma);

            //Uri urlNoticias = builder.build();


            //noticiasList = ParserXML.parsea(urlNoticias.toString());

            Boolean usarCache = true;
            Context context = null;

            boolean alberapps = false;

            if (datos.length > 1) {

                usarCache = (Boolean) datos[0];

            }

            String userAgent = null;

            if (datos.length >= 2) {
                userAgent = (String) datos[1];
            }

            if (datos.length >= 3) {
                context = (Context) datos[2];
            }

            if (datos.length >= 4) {
                alberapps = (boolean) datos[3];
            }

            if (alberapps) {
                noticiasList = AlberappsNewsParser.getAlberappsNews(usarCache, userAgent, context);
            } else {
                noticiasList = TramNewsParser.getTramNews(usarCache, userAgent, context);
            }


        } catch (Exception e) {
            return null;
        }

        return noticiasList;
    }


    @Override
    protected void onPostExecute(List<NoticiaRss> result) {
        if (responder != null) {
            responder.noticiasRssLoaded(result);
        }


    }

}
