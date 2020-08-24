/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * based on code by ZgzBus Copyright (C) 2010 Francho Joven
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.favoritos;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContentResolverCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.data.Favorito;
import alberapps.android.tiempobus.data.FavoritosProvider;
import alberapps.android.tiempobus.data.TiempoBusDb;
import alberapps.android.tiempobus.favoritos.googledriverest.FavoritoGoogleDriveRestActivity;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.tasks.BackupAsyncTask;
import alberapps.android.tiempobus.tasks.BackupAsyncTask.BackupAsyncTaskResponder;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.data.backup.DatosDriveBackup;

/**
 * Muestra los favoritos guardados
 */
public class FavoritosActivity extends AppCompatActivity {

    public static final int SUB_ACTIVITY_REQUEST_DRIVE = 1100;

    public static final String[] PROJECTION = new String[]{TiempoBusDb.Favoritos._ID, // 0
            TiempoBusDb.Favoritos.POSTE, // 1
            TiempoBusDb.Favoritos.TITULO, // 2
            TiempoBusDb.Favoritos.DESCRIPCION, // 3
    };

    private static final int MENU_BORRAR = 2;

    private static final int MENU_MODIFICAR = 3;

    private ListView favoritosView;

    FavoritosAdapter adapter;

    SharedPreferences preferencias = null;

    String orden = "";

    private ProgressDialog dialog;

    private static final int REQUEST_CODE_STORAGE = 3;

    /**
     * On Create
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

        }

        //Status bar color init
        UtilidadesUI.initStatusBar(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.favoritos);

        // Fondo
        setupFondoAplicacion();

        // Orden de favoritos
        orden = preferencias.getString("orden_favoritos", TiempoBusDb.Favoritos.DEFAULT_SORT_ORDER);
        //consultarDatos(orden);

    }



    /**
     * Consulda de datos desde la base de datos en el orden indicado
     *
     * @param orden
     */
    private void consultarDatos(String orden) {

		/*
         * Si no ha sido cargado con anterioridad, cargamos nuestro
		 * "content provider"
		 */
        Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(TiempoBusDb.Favoritos.CONTENT_URI);
        }

		/*
         * Query "managed": la actividad se encargará de cerrar y volver a
		 * cargar el cursor cuando sea necesario
		 */
        //Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, orden);

        //Sustituir managedquery deprecated
        Cursor cursor = ContentResolverCompat.query(getContentResolver(), getIntent().getData(), PROJECTION, null, null, orden, null);

        List<Favorito> listaFavoritos = new ArrayList<>();

        if(cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Favorito favorito = new Favorito();

                favorito.setId(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos._ID)));
                favorito.setNumParada(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.POSTE)));
                favorito.setTitulo(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.TITULO)));
                favorito.setDescripcion(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.DESCRIPCION)));

                listaFavoritos.add(favorito);

            }


            cursor.close();


        }

        // Nuevo adapter para favoritos
        adapter = new FavoritosAdapter(this, R.layout.favoritos_item);
        adapter.addAll(listaFavoritos);



		/*
         * Preparamos las acciones a realizar cuando pulsen un favorito
		 */

        favoritosView = (ListView) findViewById(android.R.id.list);

        favoritosView.setAdapter(adapter);


        favoritosView.setOnItemClickListener(favoritoClickedHandler);
        registerForContextMenu(favoritosView);


        TextView vacio = (TextView) findViewById(android.R.id.empty);
        favoritosView.setEmptyView(vacio);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    /**
     * Listener encargado de gestionar las pulsaciones sobre los items
     */
    private OnItemClickListener favoritoClickedHandler = new OnItemClickListener() {

        /**
         * @param l
         *            The ListView where the click happened
         * @param v
         *            The view that was clicked within the ListView
         * @param position
         *            The position of the view in the list
         * @param id
         *            The row id of the item that was clicked
         */
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            Favorito fav = (Favorito) l.getItemAtPosition(position);

            Intent intent = new Intent();
            Bundle b = new Bundle();

            //Horarios
            if (fav.getNumParada().equals("0")) {
                String[] desc = fav.getDescripcion().trim().split("::");
                b.putString("HORARIOS", desc[1]);
            }

            b.putInt("POSTE", Integer.parseInt(fav.getNumParada()));
            intent.putExtras(b);
            setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent);
            finish();

        }
    };

    /**
     * Menu contextual
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        menu.setHeaderTitle(R.string.menu_contextual);

        menu.add(0, MENU_BORRAR, 0, getResources().getText(R.string.menu_borrar));

        menu.add(0, MENU_MODIFICAR, 1, getResources().getText(R.string.menu_modificar));

    }

    /**
     * Gestionamos la pulsacion de un menu contextual
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        Favorito favorito = adapter.getItem((int) info.id);

        long id = Long.parseLong(favorito.getId());

        switch (item.getItemId()) {
            case MENU_BORRAR:

                launchBorrarFavorito(id);

                return true;

            case MENU_MODIFICAR:

                launchModificarFavorito(id);

                return true;

            default:
                // return super.onContextItemSelected(item);
        }
        return false;
    }

    /**
     * Borrado
     *
     * @param idBorrado
     */
    public void launchBorrarFavorito(final long idBorrado) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //dialog.setTitle(getString(R.string.menu_borrar));

        dialog.setMessage(getString(R.string.favoritos_pregunta));
        // dialog.setIcon(R.drawable.ic_tiempobus_3);

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                Uri miUri = ContentUris.withAppendedId(TiempoBusDb.Favoritos.CONTENT_URI, idBorrado);

                int resultado = getContentResolver().delete(miUri, null, null);

                //Datos para copia de seguridad
                Date fechaDB = DatosDriveBackup.datosArchivoDB();
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putLong("drive_local_db", fechaDB.getTime());
                editor.apply();
                ////

                adapter.notifyDataSetInvalidated();

                consultarDatos(orden);

                if (resultado > 0) {
                    Toast.makeText(FavoritosActivity.this, getString(R.string.info_borrar), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FavoritosActivity.this, getString(R.string.error_no_definido), Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();

            }

        });

        dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }

        });

        dialog.show();

    }

    /**
     * Lanza la subactividad para modificar el favorito seleccionado
     */
    public void launchModificarFavorito(long id) {

        Uri miUriM = ContentUris.withAppendedId(TiempoBusDb.Favoritos.CONTENT_URI, id);

        Cursor mCursor = getContentResolver().query(miUriM, PROJECTION, null, null, TiempoBusDb.Favoritos.DEFAULT_SORT_ORDER);

        String posteN = "";
        String titulo = "";
        String descripcion = "";

        if (mCursor != null) {

            while (mCursor.moveToNext()) {

                posteN = mCursor.getString(mCursor.getColumnIndex(TiempoBusDb.Favoritos.POSTE));
                titulo = mCursor.getString(mCursor.getColumnIndex(TiempoBusDb.Favoritos.TITULO));
                descripcion = mCursor.getString(mCursor.getColumnIndex(TiempoBusDb.Favoritos.DESCRIPCION));

            }

            mCursor.close();

        }

        int poste = Integer.parseInt(posteN);

        Intent i = new Intent(FavoritosActivity.this, FavoritoModificarActivity.class);

        Bundle extras = new Bundle();
        extras.putInt("POSTE", poste); // Pasamos el poste actual

        extras.putString("TITULO", titulo);

        extras.putString("DESCRIPCION", descripcion);

        // Uri de modificacion
        extras.putLong("ID_URI", id);

        i.putExtras(extras);
        startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_ADDFAV);
    }

    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = findViewById(R.id.contenedor_favoritos);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.favoritos, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences.Editor editor = preferencias.edit();

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.menu_ordenar_parada:

                if (orden.equals(TiempoBusDb.Favoritos.NUM_A_SORT_ORDER)) {

                    orden = TiempoBusDb.Favoritos.NUM_D_SORT_ORDER;

                    consultarDatos(orden);

                    // Guardar
                    editor.putString("orden_favoritos", TiempoBusDb.Favoritos.NUM_D_SORT_ORDER);
                    editor.apply();

                } else {

                    orden = TiempoBusDb.Favoritos.NUM_A_SORT_ORDER;

                    consultarDatos(orden);

                    // Guardar
                    editor.putString("orden_favoritos", TiempoBusDb.Favoritos.NUM_A_SORT_ORDER);
                    editor.apply();

                }

                break;
            case R.id.menu_ordenar_nombre:

                if (orden.equals(TiempoBusDb.Favoritos.NAME_A_SORT_ORDER)) {

                    orden = TiempoBusDb.Favoritos.NAME_D_SORT_ORDER;

                    consultarDatos(orden);

                    // Guarda
                    editor.putString("orden_favoritos", TiempoBusDb.Favoritos.NAME_D_SORT_ORDER);
                    editor.apply();
                } else {

                    orden = TiempoBusDb.Favoritos.NAME_A_SORT_ORDER;

                    consultarDatos(orden);

                    // Guarda
                    editor.putString("orden_favoritos", TiempoBusDb.Favoritos.NAME_A_SORT_ORDER);
                    editor.apply();

                }

                break;

            case R.id.menu_exportar:

                exportarDB();

                break;

            case R.id.menu_importar:

                importarDB();

                break;

            case R.id.menu_importar_drive:

                if (DatosPantallaPrincipal.servicesConnectedActivity(this)) {
                    importarDriveDB();
                }
                break;

//            case R.id.menu_exportar_drive:
//
//                if (DatosPantallaPrincipal.servicesConnectedActivity(this)) {
//
//                    Intent intent2 = new Intent(FavoritosActivity.this, FavoritoGoogleDriveActivity.class);
//
//                    Bundle b2 = new Bundle();
//                    b2.putString("MODO", FavoritoGoogleDriveActivity.MODO_EXPORTAR);
//                    intent2.putExtras(b2);
//
//                    startActivityForResult(intent2, SUB_ACTIVITY_REQUEST_DRIVE);
//
//
//                    /*Intent intent2 = new Intent(FavoritosActivity.this, FavoritoGoogleDriveRestActivity.class);
//
//                    Bundle b2 = new Bundle();
//                    b2.putString("MODO", FavoritoGoogleDriveRestActivity.MODO_EXPORTAR);
//                    intent2.putExtras(b2);
//
//                    startActivityForResult(intent2, SUB_ACTIVITY_REQUEST_DRIVE);*/
//
//                }
//
//                break;


        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Exportar la base de datos
     */
    private void exportarDB() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE);
        } else {

            exportarDBConPermisos();

        }

    }

    private void exportarDBConPermisos() {

        dialog = ProgressDialog.show(FavoritosActivity.this, "", getString(R.string.dialog_procesando), true);

        BackupAsyncTaskResponder backupAsyncTaskResponder = new BackupAsyncTaskResponder() {
            public void backupLoaded(Boolean resultado) {

                if (resultado != null && resultado) {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(FavoritosActivity.this, getString(R.string.ok_exportar), Toast.LENGTH_LONG).show();

                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(FavoritosActivity.this, getString(R.string.error_exportar), Toast.LENGTH_SHORT).show();

                }
            }
        };

        new BackupAsyncTask(backupAsyncTaskResponder).execute("exportar", this);

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // success!
                exportarDBConPermisos();
            } else {
                // Permission was denied or request was cancelled

                Toast.makeText(FavoritosActivity.this, getString(R.string.error_exportar), Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * Importar la base de datos
     */
    private void importarDB() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.favoritos_pregunta)).setCancelable(false).setPositiveButton(getString(R.string.barcode_si), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                procederImportacionBD();

            }
        }).setNegativeButton(getString(R.string.barcode_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });
        AlertDialog alert = builder.create();

        alert.show();

    }

    /**
     * Importar la base de datos
     */
    private void importarDriveDB() {

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.favoritos_pregunta)).setCancelable(false).setPositiveButton(getString(R.string.barcode_si), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //Intent intent1 = new Intent(FavoritosActivity.this, FavoritoGoogleDriveActivity.class);

                Intent intent1 = new Intent(FavoritosActivity.this, FavoritoGoogleDriveRestActivity.class);

                Bundle b = new Bundle();
                b.putString("MODO", FavoritoGoogleDriveActivity.MODO_IMPORTAR);
                intent1.putExtras(b);

                startActivityForResult(intent1, SUB_ACTIVITY_REQUEST_DRIVE);

            }
        }).setNegativeButton(getString(R.string.barcode_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();

            }
        });
        AlertDialog alert = builder.create();

        alert.show();*/

        Intent intent1 = new Intent(FavoritosActivity.this, FavoritoGoogleDriveRestActivity.class);
        startActivityForResult(intent1, SUB_ACTIVITY_REQUEST_DRIVE);

    }

    /**
     * Recuperar
     */
    private void procederImportacionBD() {

        dialog = ProgressDialog.show(FavoritosActivity.this, "", getString(R.string.dialog_procesando), true);

        BackupAsyncTaskResponder backupAsyncTaskResponder = new BackupAsyncTaskResponder() {
            public void backupLoaded(Boolean resultado) {

                if (resultado != null && resultado) {

                    //Cerrar para recargar datos
                    FavoritosProvider.DatabaseHelper dbHelper = FavoritosProvider.DatabaseHelper.getInstance(FavoritosActivity.this);
                    dbHelper.close();
                    //

                    consultarDatos(orden);
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(FavoritosActivity.this, getString(R.string.ok_importar), Toast.LENGTH_SHORT).show();

                } else {
                    // Error al recuperar datos

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    Toast.makeText(FavoritosActivity.this, getString(R.string.error_importar), Toast.LENGTH_LONG).show();

                }
            }
        };

        new BackupAsyncTask(backupAsyncTaskResponder).execute("importar");

    }

    @Override
    protected void onStart() {

        super.onStart();

        consultarDatos(orden);

    }

    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SUB_ACTIVITY_REQUEST_DRIVE:
                if (resultCode == RESULT_OK) {

                    consultarDatos(orden);

                }

                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);

                consultarDatos(orden);

                break;
        }
    }

    /**
     * Compartir informacion del bus
     */
    public void shareFavorito(Favorito favorito) {

        // String devuelto

        String mensaje = getString(R.string.share_0) + " " + getString(R.string.share_0b) + " " + favorito.getNumParada() + " '" + favorito.getTitulo() + "' " + ": " + favorito.getDescripcion();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.menu_share)));

    }

}
