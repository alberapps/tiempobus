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
package alberapps.android.tiempobus.favoritos.drive;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.tasks.BackupDriveAsyncTask;
import alberapps.android.tiempobus.tasks.BackupDriveAsyncTask.BackupDriveAsyncTaskResponder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.OnNewContentsCallback;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

/**
 * Exportar e importar desde drive
 * 
 * 
 */
@SuppressLint("NewApi")
public class FavoritoDriveActivity extends BaseDriveActivity {

	private static final String TAG = "CreateFileWithCreatorActivity";

	protected static final int REQUEST_CODE_CREATOR = 1;

	protected static final int REQUEST_CODE_OPENER = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setTheme(android.R.style.Theme_Holo_Light_Dialog);
		} else {
			setTheme(android.R.style.Theme_Dialog);
		}

		setContentView(R.layout.favoritos_drive);

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);

		// nuevoArchivoDrive();

		cargarFicheroDrive();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CREATOR:
			if (resultCode == RESULT_OK) {
				DriveId driveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
				showMessage("File created with ID: " + driveId);

				if (driveId != null) {

					guardarDatos(driveId);

				} else {

					getGoogleApiClient().disconnect();

					finish();

				}

			}

			break;

		case REQUEST_CODE_OPENER:
			if (resultCode == RESULT_OK) {
				DriveId driveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
				showMessage("Selected file's ID: " + driveId);

				if (driveId != null) {
					cargarDatos(driveId);
				} else {

					getGoogleApiClient().disconnect();

					finish();

				}

			} else {

				getGoogleApiClient().disconnect();

				finish();

			}

			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	private void nuevoArchivoDrive() {

		OnNewContentsCallback onContentsCallback = new OnNewContentsCallback() {

			public void onNewContents(ContentsResult result) {
				MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setMimeType("application/octet-stream").setTitle("tiempobus.db").build();
				IntentSender intentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(metadataChangeSet).setInitialContents(result.getContents()).build(getGoogleApiClient());
				try {
					startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
				} catch (SendIntentException e) {
					Log.w(TAG, "Unable to send intent", e);
				}
			}
		};
		Drive.DriveApi.newContents(getGoogleApiClient()).addResultCallback(onContentsCallback);

	}

	private void guardarDatos(DriveId id) {

		DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), id);

		BackupDriveAsyncTaskResponder backupDriveAsyncTaskResponder = new BackupDriveAsyncTaskResponder() {
			public void backupLoaded(Boolean resultado) {

				if (resultado != null && resultado) {

					// dialog.dismiss();
					Toast.makeText(FavoritoDriveActivity.this, getString(R.string.ok_exportar_drive), Toast.LENGTH_LONG).show();

				} else {
					// dialog.dismiss();
					Toast.makeText(FavoritoDriveActivity.this, getString(R.string.error_exportar_drive), Toast.LENGTH_SHORT).show();

				}

				getGoogleApiClient().disconnect();

				finish();

			}
		};

		new BackupDriveAsyncTask(backupDriveAsyncTaskResponder).execute(file, this, "exportar");

	}

	private void cargarFicheroDrive() {

		IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder().setMimeType(new String[] { "application/octet-stream" }).build(getGoogleApiClient());
		try {
			startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
		} catch (SendIntentException e) {
			Log.w(TAG, "Unable to send intent", e);
		}

	}

	private void cargarDatos(DriveId id) {

		DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), id);

		BackupDriveAsyncTaskResponder backupDriveAsyncTaskResponder = new BackupDriveAsyncTaskResponder() {
			public void backupLoaded(Boolean resultado) {

				if (resultado != null && resultado) {

					// consultarDatos(orden);
					// dialog.dismiss();
					Toast.makeText(FavoritoDriveActivity.this, getString(R.string.ok_importar_drive), Toast.LENGTH_SHORT).show();

				} else {
					// Error al recuperar datos

					// dialog.dismiss();

					Toast.makeText(FavoritoDriveActivity.this, getString(R.string.error_importar_drive), Toast.LENGTH_LONG).show();

				}

				getGoogleApiClient().disconnect();

				finish();

			}
		};

		new BackupDriveAsyncTask(backupDriveAsyncTaskResponder).execute(file, this, "importar");

	}

}
