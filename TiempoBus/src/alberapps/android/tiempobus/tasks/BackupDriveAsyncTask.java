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

import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import alberapps.android.tiempobus.favoritos.drivedeprecated.FavoritoDriveActivity;
import alberapps.java.data.backup.DatosDriveBackup;

/**
 * Tarea asincrona copia de seguridad en drive
 */
public class BackupDriveAsyncTask extends AsyncTask<Object, Void, Boolean> {

    public interface BackupDriveAsyncTaskResponder {
        void backupLoaded(Boolean result);
    }

    private BackupDriveAsyncTaskResponder responder;

    public BackupDriveAsyncTask(BackupDriveAsyncTaskResponder responder) {
        this.responder = responder;
    }

    @Override
    protected Boolean doInBackground(Object... datos) {
        Boolean resultado = null;

        try {

            DriveFile file = (DriveFile) datos[0];

            FavoritoDriveActivity context = (FavoritoDriveActivity) datos[1];

            String entrada = (String) datos[2];

            if (entrada.equals("importar")) {

                DriveApi.DriveContentsResult contentsResult = file.open(context.getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
                if (!contentsResult.getStatus().isSuccess()) {
                    return false;
                }

                DriveContents driveContents = contentsResult.getDriveContents();

                resultado = DatosDriveBackup.recuperar(driveContents);

                driveContents.discard(context.getGoogleApiClient());

                // Limpiar archivos temporales y backup
                DatosDriveBackup.borrarArchivosBackup();

                return resultado;

            } else {

                DriveApi.DriveContentsResult contentsResult = file.open(context.getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
                if (!contentsResult.getStatus().isSuccess()) {
                    return false;
                }

                DriveContents driveContents = contentsResult.getDriveContents();

                resultado = DatosDriveBackup.exportar(driveContents);

                com.google.android.gms.common.api.Status status = driveContents.commit(context.getGoogleApiClient(), null).await();

                return resultado && status.getStatus().isSuccess();

            }

        } catch (Exception e) {
            return false;
        }

    }


    @Override
    protected void onPostExecute(Boolean result) {
        if (responder != null) {
            responder.backupLoaded(result);
        }
    }

}
