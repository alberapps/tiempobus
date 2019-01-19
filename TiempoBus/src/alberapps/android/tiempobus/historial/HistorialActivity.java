/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
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
package alberapps.android.tiempobus.historial;

import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.app.NavUtils;
import androidx.core.content.ContentResolverCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.data.Favorito;
import alberapps.android.tiempobus.database.historial.HistorialDB;
import alberapps.android.tiempobus.util.UtilidadesUI;

/**
 * Historial
 */

public class HistorialActivity extends AppCompatActivity {

    public static final String[] PROJECTION = new String[]{HistorialDB.Historial._ID, // 0
            HistorialDB.Historial.PARADA, // 1
            HistorialDB.Historial.TITULO, // 2
            HistorialDB.Historial.DESCRIPCION, // 3
            HistorialDB.Historial.FECHA, // 4
            HistorialDB.Historial.HORARIO_SELECCIONADO, // 5
    };

    private static final int MENU_BORRAR = 2;

    private ListView favoritosView;

    HistorialAdapter adapter;

    SharedPreferences preferencias = null;

    String orden = "";

    // private ProgressDialog dialog;

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


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.favoritos);


        // Fondo
        setupFondoAplicacion();

        // Orden de historico
        orden = HistorialDB.Historial.DEFAULT_SORT_ORDER;
        consultarDatos(orden);

    }

    /**
     * Consulta de datos de historial
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
            intent.setData(HistorialDB.Historial.CONTENT_URI);
        }

		/*
         * Query "managed": la actividad se encargará de cerrar y volver a
		 * cargar el cursor cuando sea necesario
		 */
        //Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, orden);
        Cursor cursor = ContentResolverCompat.query(getContentResolver(), getIntent().getData(), PROJECTION, null, null, orden, null);

		/*
         * Mapeamos las querys SQL a los campos de las vistas
		 */
        //String[] camposDb = new String[]{HistorialDB.Historial.PARADA, HistorialDB.Historial.TITULO, HistorialDB.Historial.DESCRIPCION};
        //int[] camposView = new int[]{R.id.numParadaFav, R.id.titulo, R.id.descripcion};

        //adapter = new SimpleCursorAdapter(this, R.layout.historial_item, cursor, camposDb, camposView);
        List<Favorito> listaHistorial = new ArrayList<>();
        Favorito historial = null;

        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                historial = new Favorito();

                historial.setId(cursor.getString(cursor.getColumnIndex(HistorialDB.Historial._ID)));
                historial.setNumParada(cursor.getString(cursor.getColumnIndex(HistorialDB.Historial.PARADA)));
                historial.setTitulo(cursor.getString(cursor.getColumnIndex(HistorialDB.Historial.TITULO)));
                historial.setDescripcion(cursor.getString(cursor.getColumnIndex(HistorialDB.Historial.DESCRIPCION)));
                listaHistorial.add(historial);

            }

            cursor.close();

        }

        // Nuevo adapter para favoritos
        adapter = new HistorialAdapter(this, R.layout.historial_item);
        adapter.addAll(listaHistorial);

		/*
         * Preparamos las acciones a realizar cuando pulsen un favorito
		 */

        favoritosView = (ListView) findViewById(android.R.id.list);

        favoritosView.setAdapter(adapter);


        favoritosView.setOnItemClickListener(favoritoClickedHandler);
        registerForContextMenu(favoritosView);

    }

    /**
     * Si no hay favoritos cerramos la actividad
     */
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

            //Cursor c = (Cursor) l.getItemAtPosition(position);
            //int poste = c.getInt(c.getColumnIndex(HistorialDB.Historial.PARADA));

            Intent intent = new Intent();
            Bundle b = new Bundle();
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

    }

    /**
     * Gestionamos la pulsacion de un menu contextual
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        Favorito historial = adapter.getItem((int) info.id);
        long id = Long.parseLong(historial.getId());


        switch (item.getItemId()) {

            case MENU_BORRAR:
                Uri miUri = ContentUris.withAppendedId(HistorialDB.Historial.CONTENT_URI, id);

                getContentResolver().delete(miUri, null, null);

                consultarDatos(orden);

                Toast.makeText(this, getResources().getText(R.string.hist_info_borrar), Toast.LENGTH_SHORT).show();

                return true;

            default:
                // return super.onContextItemSelected(item);
        }
        return false;
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
        menuInflater.inflate(R.menu.historial, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.menu_hist_borrar:

                getContentResolver().delete(HistorialDB.Historial.CONTENT_URI, null, null);

                consultarDatos(orden);

                Toast.makeText(this, getResources().getText(R.string.hist_info_borrar), Toast.LENGTH_SHORT).show();

                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {

        super.onStart();

        if (preferencias.getBoolean("analytics_on", true)) {
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();

        if (preferencias.getBoolean("analytics_on", true)) {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }
    }

}
