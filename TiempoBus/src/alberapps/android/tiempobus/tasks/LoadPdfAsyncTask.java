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

import alberapps.java.horarios.horariospdf.PdfHorariosBus;

/**
 * Tarea asincrona para recuperar informacion de wikipedia
 */
public class LoadPdfAsyncTask extends AsyncTask<Object, Void, Object> {

    /**
     *
     *
     */
    public interface LoadPdfAsyncTaskResponder {
        void PdfLoaded(Object result);
    }

    private LoadPdfAsyncTaskResponder responder;

    /**
     * @param responder
     */
    public LoadPdfAsyncTask(LoadPdfAsyncTaskResponder responder) {
        this.responder = responder;
    }

    /**
     *
     */
    @Override
    protected Object doInBackground(Object... datos) {

        try {

            String linea = (String) datos[0];
            Context context = (Context) datos[1];
            String userAgentDefault = (String) datos[2];

            String url = PdfHorariosBus.getUrlPdfLinea(linea, userAgentDefault, true, context);

            if(url != null && !url.equals("")) {
                PdfHorariosBus.abrirPdf(url, context);
            }else{
                return false;
            }

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }


        return true;

    }

    /**
     *
     */
    @Override
    protected void onPostExecute(Object result) {
        if (responder != null) {
            responder.PdfLoaded(result);
        }

    }

}
