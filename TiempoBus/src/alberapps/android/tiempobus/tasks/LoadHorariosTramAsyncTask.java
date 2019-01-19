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

import android.content.Context;
import android.os.AsyncTask;

import alberapps.java.tram.horarios.DatosConsultaHorariosTram;
import alberapps.java.tram.horarios.HorarioTram;
import alberapps.java.tram.horarios.ProcesarHorariosTram;

/**
 * Consulta asincrona de los horarios de la linea
 */
public class LoadHorariosTramAsyncTask extends AsyncTask<Object, Void, HorarioTram> {

    public interface LoadHorariosTramAsyncTaskResponder {
        void datosHorariosTramLoaded(HorarioTram datos);
    }

    private LoadHorariosTramAsyncTaskResponder responder;

    public LoadHorariosTramAsyncTask(LoadHorariosTramAsyncTaskResponder responder) {
        this.responder = responder;
    }

    @Override
    protected HorarioTram doInBackground(Object... datos) {
        HorarioTram horarioTram = null;
        try {


            DatosConsultaHorariosTram datosConsulta = (DatosConsultaHorariosTram) datos[0];

            Context context = null;

            if (datos.length >= 2) {
                context = (Context) datos[1];
            }


            horarioTram = ProcesarHorariosTram.getHorarios(datosConsulta, context);


        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }

        return horarioTram;
    }

    @Override
    protected void onPostExecute(HorarioTram result) {
        if (responder != null) {
            responder.datosHorariosTramLoaded(result);
        }
    }

}
