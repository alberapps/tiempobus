/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
 *  based on code by The Android Open Source Project
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
package alberapps.android.tiempobus.buscador;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.actionbar.ActionBarBuscadorActivity;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.util.UtilidadesUI;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * The main activity for the dictionary. Displays search results triggered by
 * the search dialog and handles actions from search suggestions.
 */
public class BuscadorLineas extends ActionBarBuscadorActivity {

	private TextView mTextView;
	private ListView mListView;

	SharedPreferences preferencias = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		setContentView(R.layout.resultados_busqueda_offline);

		mTextView = (TextView) findViewById(R.id.text);
		mListView = (ListView) findViewById(R.id.list);

		// Fondo
		setupFondoAplicacion();

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// Because this activity has set launchMode="singleTop", the system
		// calls this method
		// to deliver the intent if this activity is currently the foreground
		// activity when
		// invoked again (when the user executes a search from this activity, we
		// don't create
		// a new instance of this activity, so the system delivers the search
		// intent here)
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			// handles a click on a search suggestion; launches activity to show
			// word
			Intent wordIntent = new Intent(this, DatosParadaActivity.class);
			wordIntent.setData(intent.getData());
			startActivity(wordIntent);
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String query = intent.getStringExtra(SearchManager.QUERY);
			showResults(query);
		}
	}

	/**
	 * Searches the dictionary and displays results for the given query.
	 * 
	 * @param query
	 *            The search query
	 */
	private void showResults(String query) {

		Cursor cursor = managedQuery(BuscadorLineasProvider.CONTENT_URI, null, null, new String[] { query }, null);

		if (cursor == null) {
			// There are no results
			mTextView.setText(getString(R.string.no_results, new Object[] { query }));
		} else {
			// Display the number of results
			int count = cursor.getCount();
			String countString = getResources().getQuantityString(R.plurals.search_results, count, new Object[] { count, query });
			mTextView.setText(countString);

			// Specify the columns we want to display in the result
			String[] from = new String[] { DatosLineasDB.KEY_WORD, DatosLineasDB.KEY_DEFINITION };

			// Specify the corresponding layout elements where we want the
			// columns to go
			int[] to = new int[] { R.id.titulo, R.id.descripcion };

			// Create a simple cursor adapter for the definitions and apply them
			// to the ListView
			SimpleCursorAdapter words = new SimpleCursorAdapter(this, R.layout.resultado_offline_item, cursor, from, to);

			mListView.setAdapter(words);

			// Define the on-click listener for the list items
			mListView.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// Build the Intent used to open WordActivity with a
					// specific word Uri
					Intent wordIntent = new Intent(getApplicationContext(), DatosParadaActivity.class);
					Uri data = Uri.withAppendedPath(BuscadorLineasProvider.CONTENT_URI, String.valueOf(id));
					wordIntent.setData(data);
					startActivity(wordIntent);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			onSearchRequested();
			break;

		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		}

		return super.onOptionsItemSelected(item);

	}

	/**
	 * Seleccion del fondo de la galeria en el arranque
	 */
	private void setupFondoAplicacion() {

		String fondo_galeria = preferencias.getString("image_galeria", "");

		View contenedor_principal = findViewById(R.id.contenedor_resultado_offline);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

	}

}
