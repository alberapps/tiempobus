/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2019 Alberto Montiel
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
package alberapps.android.tiempobus.favoritos.googledriverest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.data.FavoritosProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.historial.HistorialProvider;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.data.backup.DatosDriveBackup;
import alberapps.java.util.Utilidades;


/**
 * Exportar e importar desde drive con API rest
 */
@SuppressLint("NewApi")
public class FavoritoGoogleDriveRestActivity extends AppCompatActivity {

    private static final String TAG = "GoogleDriveRestActivity";

    public static final String MODO_EXPORTAR = "exportar";
    public static final String MODO_IMPORTAR = "importar";

    private String modo = MODO_EXPORTAR;

    private ProgressBar mProgressBar;

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;

    private DriveServiceHelper mDriveServiceHelper;

    private SharedPreferences preferencias = null;

    private GoogleSignInClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.favoritos_drive);

        //Status bar color init
        UtilidadesUI.initStatusBar(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

        }


        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);

        init();

    }


    private void init() {

        Button importar = findViewById(R.id.drive_importar);
        importar.setEnabled(false);


        importar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modo = MODO_IMPORTAR;
                queryArchivosApp();
            }
        });

        Button exportar = findViewById(R.id.drive_exportar);
        exportar.setEnabled(false);

        exportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modo = MODO_EXPORTAR;
                nuevoArchivoDrive(false);
            }
        });


        Button guardar = findViewById(R.id.drive_backup1);
        guardar.setEnabled(false);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modo = MODO_EXPORTAR;
                nuevoArchivoDrive(true);
            }
        });

        Button cargar = findViewById(R.id.drive_backup2);
        cargar.setEnabled(false);
        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modo = MODO_IMPORTAR;
                cargarArchivoBackup();
            }
        });


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        // Fondo
        setupFondoAplicacion();

        Button cuenta = findViewById(R.id.drive_cuenta);
        cuenta.setText(getString(R.string.archivo_drive_signin));

        cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!preferencias.contains("drive_cuenta")) {
                    requestSignIn();
                } else {
                    requestSignOut();
                }
            }
        });

        if (preferencias.contains("drive_cuenta")) {
            requestSignIn();
        }
    }

    private void reset() {

        Button importar = findViewById(R.id.drive_importar);
        importar.setEnabled(false);

        Button exportar = findViewById(R.id.drive_exportar);
        exportar.setEnabled(false);

        Button guardar = findViewById(R.id.drive_backup1);
        guardar.setEnabled(false);

        Button cargar = findViewById(R.id.drive_backup2);
        cargar.setEnabled(false);

        Button cuenta = findViewById(R.id.drive_cuenta);
        cuenta.setText(getString(R.string.archivo_drive_signin));


        /*SwitchCompat switchDrive1 = findViewById(R.id.switchDrive1);
        switchDrive1.setEnabled(false);
        switchDrive1.setChecked(false);

        SwitchCompat switchDrive2 = findViewById(R.id.switchDrive2);
        switchDrive2.setEnabled(false);
        switchDrive2.setChecked(false);
        */


        TextView userEmail = findViewById(R.id.drive_user_email);
        userEmail.setText(getString(R.string.archivo_drive_usuario_def));

    }

    private void initSwitch() {

        if (preferencias.contains("drive_cuenta")) {

            /*SwitchCompat switchDrive1 = findViewById(R.id.switchDrive1);
            switchDrive1.setEnabled(true);
            switchDrive1.setChecked(preferencias.getBoolean("drive_sync1", false));

            SwitchCompat switchDrive2 = findViewById(R.id.switchDrive2);
            switchDrive2.setEnabled(true);
            switchDrive2.setChecked(preferencias.getBoolean("drive_sync2", false));


            switchDrive1.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    boolean checked = switchDrive1.isChecked();

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("drive_sync1", checked);
                    editor.apply();

                    if (switchDrive1.isChecked()) {
                        modo = MODO_EXPORTAR;
                        nuevoArchivoDrive(true);
                    }

                }
            });

            switchDrive2.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                }
            });


             */

        }

    }

    @Override
    protected void onResume() {

        super.onResume();
    }


    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");

        mProgressBar.setIndeterminate(true);

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        this.startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        //this.overridePendingTransition(0,0);
    }

    private void requestSignOut() {
        Log.d(TAG, "Requesting sign-out");

        AlertDialog.Builder builder = new AlertDialog.Builder(FavoritoGoogleDriveRestActivity.this);
        builder.setMessage(getString(R.string.archivo_drive_backup_aviso_signout_pregunta))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.archivo_drive_signout_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        client.signOut().addOnCompleteListener(FavoritoGoogleDriveRestActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                SharedPreferences.Editor editor = preferencias.edit();
                                editor.remove("drive_cuenta");
                                editor.apply();

                                showMessage(getString(R.string.archivo_drive_backup_aviso_signout));
                                reset();
                            }
                        });

                    }
                }).setNeutralButton(getString(R.string.archivo_drive_disconnect_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                client.revokeAccess().addOnCompleteListener(FavoritoGoogleDriveRestActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.remove("drive_cuenta");
                        editor.apply();

                        showMessage(getString(R.string.archivo_drive_backup_aviso_disconnect));
                        reset();
                    }
                });

            }


        }).setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.create().show();


    }


    protected void onDriveClientReady(DriveUserInfo user) {

        mProgressBar.setIndeterminate(false);

        Button importar = findViewById(R.id.drive_importar);
        Button exportar = findViewById(R.id.drive_exportar);
        importar.setEnabled(true);
        exportar.setEnabled(true);
        Button guardar = findViewById(R.id.drive_backup1);
        guardar.setEnabled(true);
        Button cargar = findViewById(R.id.drive_backup2);
        cargar.setEnabled(true);


        TextView userEmail = findViewById(R.id.drive_user_email);
        userEmail.setText(user.getEmail());

        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("drive_cuenta", user.getEmail());
        editor.apply();

        Button cuenta = findViewById(R.id.drive_cuenta);
        cuenta.setText(getString(R.string.archivo_drive_signout));

        initSwitch();

        cargarDatosBackup();

    }

    private void cargarDatosBackup(){

        //Cerrar BD
        FavoritosProvider.DatabaseHelper.getInstance(this).close();
        DatosLineasDB.DatosLineasOpenHelper.getInstance(this).close();
        HistorialProvider.DatabaseHelper.getInstance(this).close();
        //

        final TextView fileInfo = findViewById(R.id.drive_file_info);
        fileInfo.setText(R.string.archivo_drive_info_no_data);

        final TextView fileSync = findViewById(R.id.drive_file_sync);

        final TextView fileDbFile = findViewById(R.id.drive_file_dbfile);


        //Datos para copia de seguridad
        Long datos_local_db = preferencias.getLong("drive_local_db", 0);


        if(!datos_local_db.equals(0)){
            Date fechaDB = new Date(datos_local_db);
            fileSync.setText(Utilidades.getFechaHoraES(fechaDB));
        } else {
            fileSync.setText(R.string.archivo_drive_info_no_data);
        }

        Date fechaLocalFile = DatosDriveBackup.datosArchivoDB();
        fileDbFile.setText(Utilidades.getFechaHoraES(fechaLocalFile));
        //

        mDriveServiceHelper.queryBackupFile()
                .addOnSuccessListener(fileList -> {

                    if (fileList.getFiles().size() > 0 && fileList.getFiles().get(0).getModifiedTime() != null) {

                        fileInfo.setText(Utilidades.getFechaHoraES(new Date(fileList.getFiles().get(0).getModifiedTime().getValue())));

                    } else {

                        fileInfo.setText(R.string.archivo_drive_info_no_data_2);

                    }

                })
                .addOnFailureListener(exception -> {

                    fileInfo.setText(R.string.archivo_drive_info_no_data_3);

                        }
                );


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {


            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    handleSignInResult(data);
                } else {
                    showMessage(getString(R.string.archivo_drive_error_signin));
                    terminar(null);
                }
                mProgressBar.setIndeterminate(false);
                break;

            /*case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        //openFileFromFilePicker(uri);
                    }
                }
                break;*/


            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }


    }


    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    DriveUserInfo user = new DriveUserInfo();
                    user.setEmail(googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("TiempoBus y Tram")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);

                    onDriveClientReady(user);
                })
                .addOnFailureListener(exception -> {
                    Log.e(TAG, "Unable to sign in.", exception);
                    showMessage(getString(R.string.archivo_drive_error_signin));
                    terminar(null);
                });
    }


    /**
     * Creates a new file via the Drive REST API.
     */
    private void nuevoArchivoDrive(boolean isBackup) {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Creating a file.");


            mDriveServiceHelper.queryTiempoBusFolder()
                    .addOnSuccessListener(fileList -> {

                        if (fileList.getFiles().size() > 0) {
                            crearArchivo(fileList.getFiles().get(0).getId(), isBackup);
                        } else {

                            mDriveServiceHelper.createTiempoBusFolder()
                                    .addOnSuccessListener(fileId -> {
                                        mProgressBar.setProgress(30);
                                        crearArchivo(fileId, isBackup);
                                    })
                                    .addOnFailureListener(exception -> {
                                                Log.e(TAG, "Couldn't create file.", exception);
                                                showMessage(getString(R.string.file_create_error));
                                                terminar(null);
                                            }
                                    );

                        }

                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }
    }

    private void crearArchivo(String idFolder, boolean isBackup) {

        if (isBackup) {
            guardarArchivoBackup(idFolder);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(FavoritoGoogleDriveRestActivity.this);
            builder.setMessage(getString(R.string.archivo_drive_backup_aviso_3))
                    .setCancelable(false)
                    .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            mDriveServiceHelper.createFileDb(idFolder)
                                    .addOnSuccessListener(fileId -> {
                                        showMessage(getString(R.string.file_created));
                                        mProgressBar.setProgress(50);
                                        guardarDatos(fileId, isBackup);
                                    })
                                    .addOnFailureListener(exception -> {
                                                Log.e(TAG, "Couldn't create file.", exception);
                                                showMessage(getString(R.string.file_create_error));
                                                terminar(null);
                                            }
                                    );
                        }
                    }).setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    mProgressBar.setProgress(0);
                }
            });

            builder.create().show();


        }

    }

    private void guardarArchivoBackup(final String idFolder) {

        mDriveServiceHelper.queryBackupFile()
                .addOnSuccessListener(fileList -> {
                    mProgressBar.setProgress(50);

                    if (fileList.getFiles().size() > 0) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(FavoritoGoogleDriveRestActivity.this);
                        builder.setMessage(getString(R.string.archivo_drive_backup_aviso_1))
                                .setCancelable(false)
                                .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        guardarDatos(fileList.getFiles().get(0).getId(), true);
                                    }
                                }).setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mProgressBar.setProgress(0);
                            }
                        });

                        builder.create().show();

                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(FavoritoGoogleDriveRestActivity.this);
                        builder.setMessage(getString(R.string.archivo_drive_backup_aviso_2))
                                .setCancelable(false)
                                .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        mDriveServiceHelper.createFileDb(idFolder)
                                                .addOnSuccessListener(fileId -> {
                                                    showMessage(getString(R.string.file_created));
                                                    mProgressBar.setProgress(50);
                                                    guardarDatos(fileId, true);
                                                })
                                                .addOnFailureListener(exception -> {
                                                            Log.e(TAG, "Couldn't create file.", exception);
                                                            showMessage(getString(R.string.file_create_error));
                                                            terminar(null);
                                                        }
                                                );
                                    }
                                }).setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mProgressBar.setProgress(0);
                            }
                        });

                        builder.create().show();


                    }

                })
                .addOnFailureListener(exception -> {
                            Log.e(TAG, "Couldn't create file.", exception);
                            showMessage(getString(R.string.file_create_error));
                            terminar(null);
                        }
                );


    }

    private void cargarArchivoBackup() {

        mDriveServiceHelper.queryBackupFile()
                .addOnSuccessListener(fileList -> {

                    if (fileList.getFiles().size() > 0) {

                        cargarDatos(fileList.getFiles().get(0).getId());
                        mProgressBar.setProgress(50);

                    } else {
                        mProgressBar.setProgress(0);
                        showMessage(getString(R.string.file_create_error));
                        terminar(null);
                    }

                })
                .addOnFailureListener(exception -> {
                            Log.e(TAG, "Couldn't create file.", exception);
                            showMessage(getString(R.string.file_create_error));
                            terminar(null);
                        }
                );


    }

    /**
     * Saves the currently opened file created via {@link #guardarDatos(String, boolean)} if one exists.
     */
    private void guardarDatos(String mOpenFileId, boolean isBackup) {

        if (mDriveServiceHelper != null && mOpenFileId != null) {
            Log.d(TAG, "Saving " + mOpenFileId);

            mProgressBar.setProgress(70);

            String fileName = null;

            if (isBackup) {
                fileName = "tiempobus_data_backup.db";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm", Locale.US);
                String fecha = sdf.format(new Date());
                fileName = "tiempobus_" + fecha + ".db";
            }

            byte[] fileContent = DatosDriveBackup.exportarDriveRest();

            mDriveServiceHelper.saveFileDb(mOpenFileId, fileName, fileContent)
                    .addOnSuccessListener(newFile -> {
                        showMessage(getString(R.string.ok_exportar_drive));
                        mProgressBar.setProgress(100);

                        String resultado = getString(R.string.archivo_drive_nuevo) + " " + newFile + " " + getString(R.string.archivo_drive_nuevo_2);

                        AlertDialog.Builder builder = new AlertDialog.Builder(FavoritoGoogleDriveRestActivity.this);
                        builder.setMessage(resultado).setCancelable(false).setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                        builder.create().show();

                        cargarDatosBackup();

                        terminar(null);
                    })
                    .addOnFailureListener(exception -> {
                        Log.e(TAG, "Unable to save file via REST.", exception);
                        showMessage(getString(R.string.error_exportar_drive));
                        terminar(null);
                    });
        }
    }


    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private void query() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles()
                    .addOnSuccessListener(fileList -> {
                        //StringBuilder builder = new StringBuilder();
                        String idFolder = null;
                        for (File file : fileList.getFiles()) {
                            //builder.append(file.getName()).append("\n");

                            if (file.getName().equals("TiempoBus_data")) {
                                idFolder = file.getDriveId();
                            }

                        }

                        if (idFolder == null) {


                        }


                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }
    }


    private void queryArchivosApp() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Querying for files.");

            mDriveServiceHelper.queryFiles()
                    .addOnSuccessListener(fileList -> {

                        mProgressBar.setProgress(30);

                        //StringBuilder builder = new StringBuilder();
                        List<DriveFileInfo> driveList = new ArrayList<>();

                        String idFolder = null;
                        for (File file : fileList.getFiles()) {

                            if (!file.getMimeType().equals("application/vnd.google-apps.folder")) {
                                driveList.add(new DriveFileInfo(file.getName(), file.getId(), false));
                            }

                        }

                        abrirImportarListado(driveList);

                    })
                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
        }
    }


    public void abrirImportarListado(final List<DriveFileInfo> driveList) {

        mProgressBar.setProgress(40);


        if (driveList.size() > 0) {

            List<CharSequence> itemsList = new ArrayList<>();

            for (int i = 0; i < driveList.size(); i++) {
                if (!driveList.get(i).isFolder()) {
                    itemsList.add(driveList.get(i).getName());
                }
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.importar_drive);

            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    mProgressBar.setProgress(0);
                }

            });

            builder.setItems(itemsList.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog2, int item) {

                    mostrarOpciones(item, driveList);

                }
            });

            AlertDialog alert = builder.create();

            alert.show();

        } else {

            Toast.makeText(this, getString(R.string.error_importar_drive), Toast.LENGTH_SHORT).show();

        }


    }

    private void mostrarOpciones(final int item, final List<DriveFileInfo> driveList) {
        AlertDialog.Builder builderOptions = new AlertDialog.Builder(FavoritoGoogleDriveRestActivity.this);

        builderOptions.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog3, int id) {
                dialog3.dismiss();
                abrirImportarListado(driveList);
            }

        });

        CharSequence[] values = new CharSequence[]{getString(R.string.importar_drive), getString(R.string.menu_borrar)};


        builderOptions.setItems(values, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog3, int item2) {

                if (item2 == 0) {
                    cargarDatos(driveList.get(item).getId());
                    dialog3.dismiss();
                    mProgressBar.setProgress(50);
                } else if (item2 == 1) {

                    borrarArchivo(driveList.get(item).getId());
                    //abrirImportarListado(driveList);

                }

            }
        });

        builderOptions.create().show();
    }


    private void cargarDatos(String fileId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(FavoritoGoogleDriveRestActivity.this);
        builder.setMessage(getString(R.string.favoritos_pregunta)).setCancelable(false).setPositiveButton(getString(R.string.barcode_si), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (mDriveServiceHelper != null) {
                    Log.d(TAG, "Reading file " + fileId);

                    mDriveServiceHelper.readFile(fileId)
                            .addOnSuccessListener(nameAndContent -> {
                                String name = nameAndContent.first;
                                InputStream content = nameAndContent.second;

                                mProgressBar.setProgress(60);
                                sobreescribirDataBase(content);

                            })
                            .addOnFailureListener(exception -> {
                                        Log.e(TAG, "Couldn't read file.", exception);
                                        showMessage(getString(R.string.error_importar_drive));
                                        terminar(null);
                                    }
                            );
                }

            }
        }).setNegativeButton(getString(R.string.barcode_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();

            }
        });

        builder.create().show();


    }


    private void sobreescribirDataBase(final InputStream data) {

        DatosDriveBackup.recuperarDatosTask(data)
                .addOnSuccessListener(resultado -> {

                    mProgressBar.setProgress(70);
                    // Read contents
                    try {

                        //sqlDb.rawQuery("select name from sqlite_master where type=?",new String[]{"table"}).getCount();

                        // Limpiar archivos temporales y backup
                        DatosDriveBackup.borrarArchivosBackup();

                        if (resultado) {
                            showMessage(getString(R.string.ok_importar_drive));
                        } else {
                            // Error al recuperar datos
                            showMessage(getString(R.string.error_importar_drive));
                        }

                        mProgressBar.setProgress(100);

                        //Cerrar para recargar datos
                        FavoritosProvider.DatabaseHelper dbHelper = FavoritosProvider.DatabaseHelper.getInstance(this);
                        dbHelper.close();
                        //

                        data.close();

                    } catch (Exception e) {
                        Log.e(TAG, "Unable to read contents", e);
                        try {
                            data.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        showMessage(getString(R.string.error_importar_drive));
                        terminar(null);
                    }

                })
                .addOnFailureListener(exception -> {
                            Log.e(TAG, "Couldn't read file.", exception);
                            showMessage(getString(R.string.error_importar_drive));
                            terminar(null);
                        }
                );


    }

    private void borrarArchivo(String fileId) {


        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Deleting file " + fileId);

            mDriveServiceHelper.deleteFile(fileId)
                    .addOnSuccessListener(Void -> {
                        showMessage(getString(R.string.archivo_borrado));
                        queryArchivosApp();

                    })
                    .addOnFailureListener(exception -> {
                                Log.e(TAG, "Couldn't read file.", exception);
                                showMessage(getString(R.string.error_importar_drive));
                                terminar(null);
                            }
                    );
        }


    }


    private void terminar(Integer resultado) {

        if (resultado != null) {
            setResult(resultado);
        }

    }


    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Shows a toast message.
     */
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sin_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = findViewById(R.id.contenedor_nuevo);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

    }


}
