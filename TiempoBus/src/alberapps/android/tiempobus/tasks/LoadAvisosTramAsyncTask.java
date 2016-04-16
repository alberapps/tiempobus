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

import java.util.List;

import alberapps.java.noticias.tw.ProcesarTwitter;
import alberapps.java.noticias.tw.TwResultado;
import alberapps.java.tram.avisos.Aviso;
import alberapps.java.tram.avisos.AvisosTram;
import alberapps.java.tram.avisos.ProcesarAvisosTram;

/**
 * Tarea asincrona que se encarga de consultar los avisos del tram
 */
public class LoadAvisosTramAsyncTask extends AsyncTask<Object, Void, AvisosTram> {


    public interface LoadAvisosTramAsyncTaskResponder {
        void AvisosTramLoaded(AvisosTram avisosTram);
    }

    private LoadAvisosTramAsyncTaskResponder responder;


    public LoadAvisosTramAsyncTask(LoadAvisosTramAsyncTaskResponder responder) {
        this.responder = responder;
    }


    @Override
    protected AvisosTram doInBackground(Object... datos) {

        boolean consultarWeb = false;

        if(datos.length > 0){

            if(((String)datos[0]).equals("TRAM_WEB")){
                consultarWeb = true;
            }

        }

        AvisosTram avisosTram = new AvisosTram();

        //Avisos TW
        List<TwResultado> twList = null;
        try {

            twList = ProcesarTwitter.procesarTram();

            avisosTram.setAvisosTw(twList);


        } catch (Exception e) {

            e.printStackTrace();

            avisosTram.setAvisosTw(null);

            //return null;


        }

        //Consultar alertas en la web
        if(consultarWeb) {
            //Avisos WEB
            List<Aviso> avisoList = null;
            try {

                avisoList = ProcesarAvisosTram.getAvisosTram();

                avisosTram.setAvisosWeb(avisoList);


            } catch (Exception e) {

                e.printStackTrace();

                avisosTram.setAvisosWeb(null);

                //return null;


            }

        }



        return avisosTram;
    }


    @Override
    protected void onPostExecute(AvisosTram result) {
        if (responder != null) {
            responder.AvisosTramLoaded(result);
        }


    }

}
