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

import android.os.AsyncTask;

import alberapps.java.horarios.DatosHorarios;
import alberapps.java.horarios.ProcesarHorarios;
import alberapps.java.tam.BusLinea;

/**
 * Consulta asincrona de los horarios de la linea
 */
public class LoadHorariosInfoLineasAsyncTask extends AsyncTask<Object, Void, DatosHorarios> {

    public interface LoadHorariosInfoLineasAsyncTaskResponder {
        void datosHorariosInfoLineasLoaded(DatosHorarios datos);
    }

    private LoadHorariosInfoLineasAsyncTaskResponder responder;

    public LoadHorariosInfoLineasAsyncTask(LoadHorariosInfoLineasAsyncTaskResponder responder) {
        this.responder = responder;
    }

    @Override
    protected DatosHorarios doInBackground(Object... datos) {
        DatosHorarios datosHorarios = null;
        try {


            BusLinea datosLinea = (BusLinea) datos[0];

            datosHorarios = ProcesarHorarios.getDetalleHorario(datosLinea);


        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }

        return datosHorarios;
    }

    @Override
    protected void onPostExecute(DatosHorarios result) {
        if (responder != null) {
            responder.datosHorariosInfoLineasLoaded(result);
        }
    }

}
