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

import alberapps.java.data.backup.DatosBackup;

/**
 * Tarea asincrona copia de seguridad
 */
public class BackupAsyncTask extends AsyncTask<String, Void, Boolean> {

    public interface BackupAsyncTaskResponder {
        void backupLoaded(Boolean result);
    }

    private BackupAsyncTaskResponder responder;

    public BackupAsyncTask(BackupAsyncTaskResponder responder) {
        this.responder = responder;
    }

    @Override
    protected Boolean doInBackground(String... datos) {
        Boolean resultado = null;
        try {

            String entrada = (String) datos[0];

            if (entrada.equals("importar")) {
                resultado = DatosBackup.recuperar(false);
            } else {
                resultado = DatosBackup.exportar(false);
            }
        } catch (Exception e) {
            return false;
        }

        return resultado;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (responder != null) {
            responder.backupLoaded(result);
        }
    }

}
