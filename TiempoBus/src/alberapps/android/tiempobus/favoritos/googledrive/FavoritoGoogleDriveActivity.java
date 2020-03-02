/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2018 Alberto Montiel
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.favoritos.googledrive;

import android.annotation.SuppressLint;


/**
 * Exportar e importar desde drive
 */
@SuppressLint("NewApi")
public class FavoritoGoogleDriveActivity extends BaseGoogleDriveActivity {

//    private static final String TAG = "FavoritoDriveActivity";
//
//    private static final int REQUEST_CODE_CREATE_FILE = 115;
//
//    public static final String MODO_EXPORTAR = "exportar";
//    public static final String MODO_IMPORTAR = "importar";
//
//    private String modo = MODO_EXPORTAR;
//
//    private ProgressDialog dialog;
//
//    private ProgressBar mProgressBar;
//
//    private ExecutorService mExecutorService;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            setTheme(android.R.style.Theme_Material_Light_Dialog);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            setTheme(android.R.style.Theme_Holo_Light_Dialog);
//        } else {
//            setTheme(android.R.style.Theme_Dialog);
//        }
//
//
//        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("MODO")) {
//
//            modo = this.getIntent().getExtras().getString("MODO");
//        }
//
//        if (modo.equals(MODO_IMPORTAR)) {
//
//            setContentView(R.layout.activity_progress);
//            mProgressBar = findViewById(R.id.progressBar);
//            mProgressBar.setMax(100);
//            mExecutorService = Executors.newSingleThreadExecutor();
//
//        } else {
//
//            setContentView(R.layout.favoritos_drive);
//
//        }
//
//    }
//
//    @Override
//    protected void onResume() {
//
//        activarProgreso();
//
//        super.onResume();
//    }
//
//    private void activarProgreso() {
//        // dialog = ProgressDialog.show(this, "",
//        // getString(R.string.dialog_procesando), true);
//    }
//
//
//    @Override
//    protected void onDriveClientReady() {
//
//        if (modo.equals(MODO_EXPORTAR)) {
//
//            nuevoArchivoDrive();
//
//        } else if (modo.equals(MODO_IMPORTAR)) {
//
//            cargarFicheroDrive();
//
//        }
//
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case REQUEST_CODE_CREATE_FILE:
//                if (resultCode == RESULT_OK) {
//
//                    DriveId driveId =
//                            data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
//                    showMessage(getString(R.string.file_created));
//
//                    if (driveId != null) {
//
//                        // activarProgreso();
//
//                        guardarDatos(driveId);
//
//                    } else {
//
//                        if (dialog != null && dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
//
//                        // Relanzar
//                        // relanzar();
//
//                    }
//
//                } else {
//                    Log.e(TAG, "Unable to create file");
//                    showMessage(getString(R.string.file_create_error));
//
//                    terminar(null);
//
//                }
//
//                break;
//
//
//            default:
//                super.onActivityResult(requestCode, resultCode, data);
//                break;
//        }
//    }
//
//    /**
//     * Crear archivo en drive
//     */
//    private void nuevoArchivoDrive() {
//
//        // [START create_file_with_intent]
//        Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
//        createContentsTask
//                .continueWithTask(task -> {
//                    DriveContents contents = task.getResult();
//
//
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm", Locale.US);
//
//                    String fecha = sdf.format(new Date());
//
//                    String nombre = "tiempobus_" + fecha + ".db";
//
//                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                            .setTitle(nombre)
//                            .setMimeType("application/octet-stream")
//                            .setStarred(false)
//                            .build();
//
//                    CreateFileActivityOptions createOptions =
//                            new CreateFileActivityOptions.Builder()
//                                    .setInitialDriveContents(contents)
//                                    .setInitialMetadata(changeSet)
//                                    .build();
//                    return getDriveClient().newCreateFileActivityIntentSender(createOptions);
//                })
//                .addOnSuccessListener(this,
//                        intentSender -> {
//                            try {
//                                startIntentSenderForResult(
//                                        intentSender, REQUEST_CODE_CREATE_FILE, null, 0, 0, 0);
//                            } catch (IntentSender.SendIntentException e) {
//                                Log.e(TAG, "Unable to create file", e);
//                                showMessage(getString(R.string.file_create_error));
//                                finish();
//                            }
//                        })
//                .addOnFailureListener(this, e -> {
//                    Log.e(TAG, "Unable to create file", e);
//                    showMessage(getString(R.string.file_create_error));
//                    finish();
//                });
//        // [END create_file_with_intent]
//    }
//
//
//    /**
//     * Guardar datos en el archivo
//     *
//     * @param id
//     */
//    private void guardarDatos(DriveId id) {
//
//        // activarProgreso();
//
//        try {
//
//            DriveFile file = id.asDriveFile();
//
//            AtomicReference<Boolean> resultado = new AtomicReference<>();
//
//            // [START open_for_write]
//            Task<DriveContents> openTask =
//                    getDriveResourceClient().openFile(file, DriveFile.MODE_WRITE_ONLY);
//            // [END open_for_write]
//            // [START rewrite_contents]
//            openTask.continueWithTask(task -> {
//                DriveContents driveContents = task.getResult();
//
//                resultado.set(DatosDriveBackup.exportar(driveContents));
//
//                // [START commit_content]
//                Task<Void> commitTask =
//                        getDriveResourceClient().commitContents(driveContents, null);
//                // [END commit_content]
//                return commitTask;
//            })
//                    .addOnSuccessListener(this,
//                            aVoid -> {
//                                if (resultado.get() != null && resultado.get()) {
//                                    showMessage(getString(R.string.ok_exportar_drive));
//                                } else {
//                                    showMessage(getString(R.string.error_exportar_drive));
//                                }
//
//                                terminar(null);
//
//                            })
//                    .addOnFailureListener(this, e -> {
//                        Log.e(TAG, "Unable to update contents", e);
//                        showMessage(getString(R.string.error_exportar_drive));
//                        //showMessage(e.getMessage());
//                        terminar(null);
//
//                    });
//            // [END rewrite_contents]
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            showMessage(getString(R.string.error_exportar_drive));
//            //showMessage(e.getMessage());
//
//        }
//
//    }
//
//
//    /**
//     * Cargar el fichero desde drive
//     */
//    private void cargarFicheroDrive() {
//
//        pickTextFile()
//                .addOnSuccessListener(this,
//                        driveId -> cargarDatos(driveId.asDriveFile()))
//                .addOnFailureListener(this, e -> {
//                    Log.e(TAG, "No file selected", e);
//                    showMessage(getString(R.string.file_not_selected));
//                    terminar(null);
//                });
//
//    }
//
//    /**
//     * Cargar los datos del archivo
//     *
//     * @param file
//     */
//    private void cargarDatos(DriveFile file) {
//
//        AtomicReference<Boolean> resultado = new AtomicReference<>();
//
//        // [START read_with_progress_listener]
//        OpenFileCallback openCallback = new OpenFileCallback() {
//            @Override
//            public void onProgress(long bytesDownloaded, long bytesExpected) {
//                // Update progress dialog with the latest progress.
//                int progress = (int) (bytesDownloaded * 100 / bytesExpected);
//                //Log.d(TAG, String.format("Loading progress: %d percent", progress));
//                mProgressBar.setProgress(progress);
//            }
//
//            @Override
//            public void onContents(@NonNull DriveContents driveContents) {
//                // onProgress may not be called for files that are already
//                // available on the device. Mark the progress as complete
//                // when contents available to ensure status is updated.
//                mProgressBar.setProgress(100);
//                // Read contents
//                // [START_EXCLUDE]
//                try {
//                    //try (BufferedReader reader = new BufferedReader(
//                    //      new InputStreamReader(driveContents.getInputStream()))) {
//
//                    resultado.set(DatosDriveBackup.recuperar(driveContents));
//
//                    getDriveResourceClient().discardContents(driveContents);
//
//                    // Limpiar archivos temporales y backup
//                    DatosDriveBackup.borrarArchivosBackup();
//
//                    //showMessage(getString(R.string.ok_importar_drive));
//
//
//                    if (resultado.get() != null && resultado.get()) {
//
//                        showMessage(getString(R.string.ok_importar_drive));
//
//                    } else {
//                        // Error al recuperar datos
//                        showMessage(getString(R.string.error_importar_drive));
//
//                    }
//
//                    terminar(RESULT_OK);
//
//                    //}
//                } catch (Exception e) {
//                    onError(e);
//                }
//                // [END_EXCLUDE]
//            }
//
//            @Override
//            public void onError(@NonNull Exception e) {
//                // Handle error
//                // [START_EXCLUDE]
//                Log.e(TAG, "Unable to read contents", e);
//                showMessage(getString(R.string.error_importar_drive));
//                //showMessage(e.getMessage());
//                terminar(null);
//                // [END_EXCLUDE]
//            }
//        };
//
//        getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY, openCallback);
//        // [END read_with_progress_listener]
//    }
//
//
//    private void terminar(Integer resultado) {
//
//        if (resultado != null) {
//            setResult(resultado);
//        }
//
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }
//
//        finish();
//    }
//
//
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mExecutorService != null) {
//            mExecutorService.shutdown();
//        }
//    }

}
