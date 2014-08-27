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

import alberapps.java.actualizador.DescargarActualizaBD;

/**
 * Tarea asincrona para recuperar informacion de wikipedia
 */
public class ActualizarBDAsyncTask extends AsyncTask<Object, Void, String> {

    /**
     *
     *
     */
    public interface LoadActualizarBDAsyncTaskResponder {
        public void ActualizarBDLoaded(String resultado);
    }

    private LoadActualizarBDAsyncTaskResponder responder;

    /**
     * @param responder
     */
    public ActualizarBDAsyncTask(LoadActualizarBDAsyncTaskResponder responder) {
        this.responder = responder;
    }

    /**
     *
     */
    @Override
    protected String doInBackground(Object... datos) {

        String respuesta = "false";

        boolean control = true;

        if (datos != null && datos.length > 0 && datos[0] != null && ((Boolean) datos[0]).equals(true)) {

            control = true;

        } else {
            control = false;
        }

        try {

            if (control) {

                respuesta = DescargarActualizaBD.controlActualizacion();

            } else {

                if (DescargarActualizaBD.iniciarActualizacion()) {
                    respuesta = "true";
                } else {
                    respuesta = "false";
                }

            }

        } catch (Exception e) {

            e.printStackTrace();

            respuesta = "false";

        }

        return respuesta;
    }

    /**
     *
     */
    @Override
    protected void onPostExecute(String result) {
        if (responder != null) {
            responder.ActualizarBDLoaded(result);
        }

    }

}
