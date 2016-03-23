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
import android.util.Log;

import java.util.List;

import alberapps.java.noticias.tw.ProcesarTwitter;
import alberapps.java.noticias.tw.TwResultado;

/**
 * Tarea asincrona que se encarga de consultar las Twitter
 */
public class LoadTwitterAsyncTask extends AsyncTask<Object, Void, List<TwResultado>> {


    public interface LoadTwitterAsyncTaskResponder {
        void TwitterLoaded(List<TwResultado> Twitter);
    }

    private LoadTwitterAsyncTaskResponder responder;


    public LoadTwitterAsyncTask(LoadTwitterAsyncTaskResponder responder) {
        this.responder = responder;
    }


    @Override
    protected List<TwResultado> doInBackground(Object... datos) {
        List<TwResultado> twList = null;
        try {

            List<Boolean> lista = (List<Boolean>) datos[0];

            String cantidad = (String) datos[1];

            twList = ProcesarTwitter.procesar(lista, cantidad);

            Log.d("tw", "lista: " + twList.size());


        } catch (Exception e) {

            e.printStackTrace();

            return null;


        }

        return twList;
    }


    @Override
    protected void onPostExecute(List<TwResultado> result) {
        if (responder != null) {
            responder.TwitterLoaded(result);
        }


    }

}
