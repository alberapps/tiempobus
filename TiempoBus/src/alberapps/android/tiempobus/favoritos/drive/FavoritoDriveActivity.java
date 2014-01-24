/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
 *  based on code by ZgzBus Copyright (C) 2010 Francho Joven
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

import alberapps.android.tiempobus.tasks.BackupDriveAsyncTask;
import alberapps.android.tiempobus.tasks.BackupDriveAsyncTask.BackupDriveAsyncTaskResponder;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.OnNewContentsCallback;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

/**
 * Guarda un nuevo favorito
 * 
 * 
 */
@SuppressLint("NewApi")
public class FavoritoDriveActivity extends BaseDriveActivity {

	private static final String TAG = "CreateFileWithCreatorActivity";

	protected static final int REQUEST_CODE_CREATOR = 1;

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_CREATOR:
			if (resultCode == RESULT_OK) {
				DriveId driveId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
				showMessage("File created with ID: " + driveId);

				if (driveId != null) {

					guardarDatos(driveId);

					
					
				}

			}
			// finish();
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
	}

	private void guardarDatos(DriveId id) {

		DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), id);

		BackupDriveAsyncTaskResponder backupDriveAsyncTaskResponder = new BackupDriveAsyncTaskResponder() {
			public void backupLoaded(Boolean resultado) {

				getGoogleApiClient().disconnect();
								
				finish();
				
				
				
			}
		};

		new BackupDriveAsyncTask(backupDriveAsyncTaskResponder).execute(file, this);

	}

}
