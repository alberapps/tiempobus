/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package alberapps.android.tiempobus.favoritos.googledrive;

import androidx.fragment.app.FragmentActivity;

/**
 * An abstract activity that handles authorization and connection to the Drive services.
 */
public abstract class BaseGoogleDriveActivity extends FragmentActivity {
//
//    private static final String TAG = "BaseGoogleDriveActivity";
//
//    /**
//     * Request code for Google Sign-in
//     */
//    protected static final int REQUEST_CODE_SIGN_IN = 0;
//
//    /**
//     * Request code for the Drive picker
//     */
//    protected static final int REQUEST_CODE_OPEN_ITEM = 1;
//
//    /**
//     * Handles high-level drive functions like sync
//     */
//    private DriveClient mDriveClient;
//
//    /**
//     * Handle access to Drive resources/files.
//     */
//    private DriveResourceClient mDriveResourceClient;
//
//    /**
//     * Tracks completion of the drive picker
//     */
//    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        signIn();
//    }
//
//    /**
//     * Handles resolution callbacks.
//     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_CODE_SIGN_IN:
//                if (resultCode != RESULT_OK) {
//                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
//                    // required and is fatal. For apps where sign-in is optional, handle
//                    // appropriately
//                    Log.e(TAG, "Sign-in failed.");
//                    finish();
//                    return;
//                }
//
//                Task<GoogleSignInAccount> getAccountTask =
//                        GoogleSignIn.getSignedInAccountFromIntent(data);
//                if (getAccountTask.isSuccessful()) {
//                    initializeDriveClient(getAccountTask.getResult());
//                } else {
//                    Log.e(TAG, "Sign-in failed.");
//                    finish();
//                }
//                break;
//            case REQUEST_CODE_OPEN_ITEM:
//                if (resultCode == RESULT_OK) {
//                    DriveId driveId = data.getParcelableExtra(
//                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
//                    mOpenItemTaskSource.setResult(driveId);
//                } else {
//                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    /**
//     * Starts the sign-in process and initializes the Drive client.
//     */
//    protected void signIn() {
//        Set<Scope> requiredScopes = new HashSet<>(2);
//        requiredScopes.add(Drive.SCOPE_FILE);
//        requiredScopes.add(Drive.SCOPE_APPFOLDER);
//        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
//            initializeDriveClient(signInAccount);
//        } else {
//            GoogleSignInOptions signInOptions =
//                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                            .requestScopes(Drive.SCOPE_FILE)
//                            .requestScopes(Drive.SCOPE_APPFOLDER)
//                            .build();
//            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
//            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
//        }
//    }
//
//    /**
//     * Continues the sign-in process, initializing the Drive clients with the current
//     * user's account.
//     */
//    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
//        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
//        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
//        onDriveClientReady();
//    }
//
//    /**
//     * Prompts the user to select a text file using OpenFileActivity.
//     *
//     * @return Task that resolves with the selected item's ID.
//     */
//    protected Task<DriveId> pickTextFile() {
//        OpenFileActivityOptions openOptions =
//                new OpenFileActivityOptions.Builder()
//                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/octet-stream"))
//                        .setActivityTitle(getString(R.string.select_file))
//                        .build();
//        return pickItem(openOptions);
//    }
//
//    /**
//     * Prompts the user to select a folder using OpenFileActivity.
//     *
//     * @return Task that resolves with the selected item's ID.
//     */
//    protected Task<DriveId> pickFolder() {
//        OpenFileActivityOptions openOptions =
//                new OpenFileActivityOptions.Builder()
//                        .setSelectionFilter(
//                                Filters.eq(SearchableField.MIME_TYPE, DriveFolder.MIME_TYPE))
//                        .setActivityTitle(getString(R.string.select_folder))
//                        .build();
//        return pickItem(openOptions);
//    }
//
//    /**
//     * Prompts the user to select a folder using OpenFileActivity.
//     *
//     * @param openOptions Filter that should be applied to the selection
//     * @return Task that resolves with the selected item's ID.
//     */
//    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
//        mOpenItemTaskSource = new TaskCompletionSource<>();
//        getDriveClient()
//                .newOpenFileActivityIntentSender(openOptions)
//                .continueWith((Continuation<IntentSender, Void>) task -> {
//                    startIntentSenderForResult(
//                            task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
//                    return null;
//                });
//        return mOpenItemTaskSource.getTask();
//    }
//
//    /**
//     * Shows a toast message.
//     */
//    public void showMessage(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//    }
//
//    /**
//     * Called after the user has signed in and the Drive client has been initialized.
//     */
//    protected abstract void onDriveClientReady();
//
//    protected DriveClient getDriveClient() {
//        return mDriveClient;
//    }
//
//    public DriveResourceClient getDriveResourceClient() {
//        return mDriveResourceClient;
//    }


}
