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

import java.text.SimpleDateFormat;
import java.util.Date;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.tasks.BackupDriveAsyncTask;
import alberapps.android.tiempobus.tasks.BackupDriveAsyncTask.BackupDriveAsyncTaskResponder;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
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

	private static final String TAG = "FavoritoDriveActivity";

	protected static final int REQUEST_CODE_CREATOR = 1;

	protected static final int REQUEST_CODE_OPENER = 2;

	public static final String MODO_EXPORTAR = "exportar";
	public static final String MODO_IMPORTAR = "importar";

	private String modo = MODO_EXPORTAR;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setTheme(android.R.style.Theme_Holo_Light_Dialog);
		} else {
			setTheme(android.R.style.Theme_Dialog);
		}

		setContentView(R.layout.favoritos_drive);

		if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("MODO")) {

			modo = this.getIntent().getExtras().getString("MODO");
		}

	}

	private void activarProgreso() {
		//dialog = ProgressDialog.show(this, "", getString(R.string.dialog_procesando), true);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);

		if (modo.equals(MODO_EXPORTAR)) {

			nuevoArchivoDrive();

		} else if (modo.equals(MODO_IMPORTAR)) {

			cargarFicheroDrive();

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CREATOR:
			if (resultCode == RESULT_OK) {
				DriveId driveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
				// showMessage("File created with ID: " + driveId);

				if (driveId != null) {

					activarProgreso();

					guardarDatos(driveId);

				} else {

					if (dialog != null && dialog.isShowing()) {
						dialog.dismiss();
					}

					// Relanzar
					// relanzar();

				}

			} else {

				terminar(null);

			}

			break;

		case REQUEST_CODE_OPENER:
			if (resultCode == RESULT_OK) {
				DriveId driveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
				// showMessage("Selected file's ID: " + driveId);

				if (driveId != null) {

					activarProgreso();

					cargarDatos(driveId);
				} else {

					// terminar(null);

				}

			} else {

				terminar(null);

			}

			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	/**
	 * Crear archivo en drive
	 */
	private void nuevoArchivoDrive() {

		OnNewContentsCallback onContentsCallback = new OnNewContentsCallback() {

			public void onNewContents(ContentsResult result) {

				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm");

				String fecha = sdf.format(new Date());

				String nombre = "tiempobus_" + fecha + ".db";

				MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder().setMimeType("application/octet-stream").setTitle(nombre).build();
				IntentSender intentSender = Drive.DriveApi.newCreateFileActivityBuilder().setInitialMetadata(metadataChangeSet).setInitialContents(result.getContents()).build(getGoogleApiClient());
				try {
					startIntentSenderForResult(intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
				} catch (SendIntentException e) {
					Log.w(TAG, "Unable to send intent", e);
				}
			}
		};

		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

		Drive.DriveApi.newContents(getGoogleApiClient()).addResultCallback(onContentsCallback);

	}

	/**
	 * Guardar datos en el archivo
	 * 
	 * @param id
	 */
	private void guardarDatos(DriveId id) {

		activarProgreso();

		DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), id);

		BackupDriveAsyncTaskResponder backupDriveAsyncTaskResponder = new BackupDriveAsyncTaskResponder() {
			public void backupLoaded(Boolean resultado) {

				if (resultado != null && resultado) {

					Toast.makeText(FavoritoDriveActivity.this, getString(R.string.ok_exportar_drive), Toast.LENGTH_LONG).show();

				} else {

					Toast.makeText(FavoritoDriveActivity.this, getString(R.string.error_exportar_drive), Toast.LENGTH_SHORT).show();

				}

				terminar(null);

			}
		};

		new BackupDriveAsyncTask(backupDriveAsyncTaskResponder).execute(file, this, "exportar");

	}

	/**
	 * Cargar el fichero desde drive
	 */
	private void cargarFicheroDrive() {

		IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder().setMimeType(new String[] { "application/octet-stream" }).build(getGoogleApiClient());
		try {
			startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
		} catch (SendIntentException e) {
			Log.w(TAG, "Unable to send intent", e);
		}

	}

	/**
	 * Cargar los datos del archivo
	 * 
	 * @param id
	 */
	private void cargarDatos(DriveId id) {

		activarProgreso();

		DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), id);

		BackupDriveAsyncTaskResponder backupDriveAsyncTaskResponder = new BackupDriveAsyncTaskResponder() {
			public void backupLoaded(Boolean resultado) {

				if (resultado != null && resultado) {

					Toast.makeText(FavoritoDriveActivity.this, getString(R.string.ok_importar_drive), Toast.LENGTH_SHORT).show();

				} else {
					// Error al recuperar datos
					Toast.makeText(FavoritoDriveActivity.this, getString(R.string.error_importar_drive), Toast.LENGTH_LONG).show();

				}

				terminar(RESULT_OK);

			}
		};

		new BackupDriveAsyncTask(backupDriveAsyncTaskResponder).execute(file, this, "importar");

	}

	private void terminar(Integer resultado) {
		getGoogleApiClient().disconnect();

		if (resultado != null) {
			setResult(resultado);
		}

		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}

		finish();
	}

	/*
	 * private void relanzar() {
	 * 
	 * AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 * builder.setMessage
	 * (getString(R.string.favoritos_pregunta)).setCancelable(false
	 * ).setPositiveButton(getString(R.string.barcode_si), new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int id) {
	 * 
	 * finish(); startActivity(getIntent());
	 * 
	 * 
	 * } }).setNegativeButton(getString(R.string.barcode_no), new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialog, int id) { dialog.cancel();
	 * 
	 * terminar(null);
	 * 
	 * } }); AlertDialog alert = builder.create();
	 * 
	 * alert.show();
	 * 
	 * }
	 */

}
