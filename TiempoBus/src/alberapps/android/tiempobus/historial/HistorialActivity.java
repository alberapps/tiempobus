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
package alberapps.android.tiempobus.historial;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.actionbar.ActionBarActivity;
import alberapps.android.tiempobus.database.historial.HistorialDB;
import alberapps.android.tiempobus.util.UtilidadesUI;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * Historial
 * 
 */
@SuppressLint("NewApi")
public class HistorialActivity extends ActionBarActivity {

	public static final String[] PROJECTION = new String[] { HistorialDB.Historial._ID, // 0
			HistorialDB.Historial.PARADA, // 1
			HistorialDB.Historial.TITULO, // 2
			HistorialDB.Historial.DESCRIPCION, // 3
			HistorialDB.Historial.FECHA, // 4
	};

	private static final int MENU_BORRAR = 2;

	private ListView favoritosView;

	SimpleCursorAdapter adapter;

	SharedPreferences preferencias = null;

	String orden = "";

	// private ProgressDialog dialog;

	/**
	 * On Create
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			if(actionBar != null){
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
		}

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		setContentView(R.layout.favoritos);
		// setTitle(R.string.tit_favoritos);

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
		Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null, null, orden);

		/*
		 * Mapeamos las querys SQL a los campos de las vistas
		 */
		String[] camposDb = new String[] { HistorialDB.Historial.PARADA, HistorialDB.Historial.TITULO, HistorialDB.Historial.DESCRIPCION };
		int[] camposView = new int[] { R.id.poste, R.id.titulo, R.id.descripcion };

		adapter = new SimpleCursorAdapter(this, R.layout.favoritos_item, cursor, camposDb, camposView);

		setListAdapter(adapter);

		/*
		 * Preparamos las acciones a realizar cuando pulsen un favorito
		 */

		favoritosView = (ListView) findViewById(android.R.id.list);
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
			Cursor c = (Cursor) l.getItemAtPosition(position);
			int poste = c.getInt(c.getColumnIndex(HistorialDB.Historial.PARADA));

			Intent intent = new Intent();
			Bundle b = new Bundle();
			b.putInt("POSTE", poste);
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

		switch (item.getItemId()) {
		case MENU_BORRAR:
			Uri miUri = ContentUris.withAppendedId(HistorialDB.Historial.CONTENT_URI, info.id);

			getContentResolver().delete(miUri, null, null);

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
		case R.id.menu_hist_borrar:

			getContentResolver().delete(HistorialDB.Historial.CONTENT_URI, null, null);

			Toast.makeText(this, getResources().getText(R.string.hist_info_borrar), Toast.LENGTH_SHORT).show();

			break;

		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {

		super.onStart();
/*
		if (preferencias.getBoolean("analytics_on", true)) {
			EasyTracker.getInstance(this).activityStart(this);
		}
*/
	}

	@Override
	protected void onStop() {

		super.onStop();
/*
		if (preferencias.getBoolean("analytics_on", true)) {
			EasyTracker.getInstance(this).activityStop(this);
		}
*/
	}

}
