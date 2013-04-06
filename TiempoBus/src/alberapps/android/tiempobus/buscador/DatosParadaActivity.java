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
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.mapas.MapasActivity;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.UtilidadesTAM;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Displays a word and its definition.
 */
public class DatosParadaActivity extends ActionBarBuscadorActivity {

	String paradaSel = "";
	String lineaSel = "";

	SharedPreferences preferencias = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datos_parada);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		// Fondo
		setupFondoAplicacion();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		Uri uri = getIntent().getData();
		Cursor cursor = managedQuery(uri, null, null, null, null);

		if (cursor == null) {
			finish();
		} else {
			cursor.moveToFirst();

			TextView parada = (TextView) findViewById(R.id.parada);
			TextView linea = (TextView) findViewById(R.id.linea);
			TextView destino = (TextView) findViewById(R.id.destino);
			TextView localizacion = (TextView) findViewById(R.id.localizacion);
			TextView conexiones = (TextView) findViewById(R.id.conexiones);

			int paradaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_PARADA);
			int lineaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LINEA_DESC);
			int direccionIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_DIRECCION);
			int conexionesIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_CONEXION);
			int destinoIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_DESTINO);

			int numLineaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LINEA_NUM);

			parada.setText(cursor.getString(paradaIndex));

			paradaSel = cursor.getString(paradaIndex);
			lineaSel = cursor.getString(numLineaIndex);

			linea.setText(cursor.getString(lineaIndex));
			destino.setText(cursor.getString(destinoIndex));
			localizacion.setText(cursor.getString(direccionIndex));
			conexiones.setText(cursor.getString(conexionesIndex));
		}

		// boton parada
		Button botonPoste = (Button) findViewById(R.id.buttonT);
		botonPoste.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {

				int codigo = -1;

				try {
					codigo = Integer.parseInt(paradaSel);

				} catch (Exception e) {

				}

				if (codigo != -1 && paradaSel.length() == 4) {

					cargarTiempos(codigo);

				} else {

					Toast.makeText(getApplicationContext(), getString(R.string.error_codigo), Toast.LENGTH_SHORT).show();

				}

			}
		});

		// boton mapa
		Button botonMapa = (Button) findViewById(R.id.buttonM);
		botonMapa.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {

				launchMapasSeleccion(lineaSel);

			}
		});

	}

	private void cargarTiempos(int codigo) {

		Intent intent = new Intent(this, MainActivity.class);
		Bundle b = new Bundle();
		b.putInt("poste", codigo);
		intent.putExtras(b);

		SharedPreferences.Editor editor = preferencias.edit();
		editor.putInt("parada_inicio", codigo);
		editor.commit();

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);

	}

	private void launchMapasSeleccion(String linea) {

		if (linea != null && !linea.equals("")) {
			Intent i = new Intent(this, MapasActivity.class);
			i.putExtra("LINEA_MAPA_FICHA", linea);

			int pos = UtilidadesTAM.getIdLinea(linea);

			i.putExtra("LINEA_MAPA_FICHA_KML", UtilidadesTAM.LINEAS_CODIGO_KML[pos]);
			i.putExtra("LINEA_MAPA_FICHA_DESC", UtilidadesTAM.LINEAS_DESCRIPCION[pos]);

			startActivity(i);
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

		View contenedor_principal = findViewById(R.id.datos_contenedor);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

	}

}
