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

import alberapps.android.tiempobus.favoritos.drive.FavoritoDriveActivity;
import alberapps.java.data.backup.DatosDriveBackup;
import android.os.AsyncTask;

import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveFile;

/**
 * Tarea asincrona copia de seguridad en drive
 * 
 * 
 */
public class BackupDriveAsyncTask extends AsyncTask<Object, Void, Boolean> {

	public interface BackupDriveAsyncTaskResponder {
		public void backupLoaded(Boolean result);
	}

	private BackupDriveAsyncTaskResponder responder;

	/**
	 * Constructor. Es necesario que nos pasen un objeto para el callback
	 * 
	 * @param responder
	 */
	public BackupDriveAsyncTask(BackupDriveAsyncTaskResponder responder) {
		this.responder = responder;
	}

	/**
	 * Procesado
	 */
	@Override
	protected Boolean doInBackground(Object... datos) {
		Boolean resultado = null;

		try {

			DriveFile file = (DriveFile) datos[0];

			FavoritoDriveActivity context = (FavoritoDriveActivity) datos[1];

			String entrada = (String) datos[2];

			if (entrada.equals("importar")) {

				ContentsResult contentsResult = file.openContents(context.getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
				if (!contentsResult.getStatus().isSuccess()) {
					return false;
				}

				resultado = DatosDriveBackup.recuperar(contentsResult);

				file.discardContents(context.getGoogleApiClient(), contentsResult.getContents()).await();

				return resultado;

			} else {

				ContentsResult contentsResult = file.openContents(context.getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
				if (!contentsResult.getStatus().isSuccess()) {
					return false;
				}

				resultado = DatosDriveBackup.exportar(contentsResult);

				com.google.android.gms.common.api.Status status = file.commitAndCloseContents(context.getGoogleApiClient(), contentsResult.getContents()).await();

				if (resultado && status.getStatus().isSuccess()) {
					return true;
				} else {
					return false;
				}

			}

		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * Resultado
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		if (responder != null) {
			responder.backupLoaded(result);
		}
	}

}
