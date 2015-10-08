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

import android.content.Context;
import android.os.AsyncTask;

import alberapps.java.directions.Direction;
import alberapps.java.directions.DirectionsApi;

/**
 * Tarea asincrona para recuperar datos de directions
 */
public class LoadDirectionsAsyncTask extends AsyncTask<Object, Void, Direction> {

    /**
     *
     *
     */
    public interface LoadDirectionsAsyncTaskResponder {
        public void directionsLoaded(Direction direction);
    }

    private LoadDirectionsAsyncTaskResponder responder;

    /**
     * @param responder
     */
    public LoadDirectionsAsyncTask(LoadDirectionsAsyncTaskResponder responder) {
        this.responder = responder;
    }

    /**
     *
     */
    @Override
    protected Direction doInBackground(Object... datos) {
        Direction direction = null;

        Context context = null;

        try {

            String origen = (String) datos[0];
            String destino = (String) datos[1];


            direction = DirectionsApi.getDirections(origen,destino,"");



        } catch (Exception e) {

            e.printStackTrace();



            return null;

        }

        return direction;
    }

    /**
     *
     */
    @Override
    protected void onPostExecute(Direction result) {
        if (responder != null) {
            responder.directionsLoaded(result);
        }

    }

}
