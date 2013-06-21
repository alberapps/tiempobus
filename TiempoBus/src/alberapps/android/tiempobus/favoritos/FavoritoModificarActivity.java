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
package alberapps.android.tiempobus.favoritos;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.actionbar.ActionBarBuscadorActivity;
import alberapps.android.tiempobus.data.TiempoBusDb;
import alberapps.android.tiempobus.util.UtilidadesUI;
import android.app.ActionBar;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Guarda un nuevo favorito
 * 
 * 
 */
public class FavoritoModificarActivity extends ActionBarBuscadorActivity {
	private EditText guiDescripcion;
	private EditText guiTitulo;

	private String poste;

	private long id_uri;

	SharedPreferences preferencias = null;

	/**
	 * OnCreate....
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Configuramos la vista

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		setContentView(R.layout.favorito_nuevo);

		setupView();
	}

	/**
	 * Si no hay poste cerramos la actividad
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if ((poste == null) || poste.equals("")) {
			Toast.makeText(FavoritoModificarActivity.this, R.string.no_poste, Toast.LENGTH_SHORT).show();

			finish();
		}
	}

	/**
	 * Configura la vista
	 */
	private void setupView() {
		guiTitulo = (EditText) findViewById(R.id.titulo);
		guiDescripcion = (EditText) findViewById(R.id.descripcion);

		// Fondo
		setupFondoAplicacion();

		// Comprobamos si nos estan pasando como par�metro el poste y la
		// descripcion

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			poste = "" + extras.getInt("POSTE");
			guiDescripcion.setText("" + extras.getString("DESCRIPCION"));
			guiTitulo.setText("" + extras.getString("TITULO"));

			id_uri = extras.getLong("ID_URI");

		}

		setTitle(String.format(getString(R.string.tit_modificar), poste));

		/*
		 * Asignamos el comprotamiento de los botones
		 */
		Button guiGo = (Button) findViewById(R.id.boton_go);
		guiGo.setOnClickListener(guiGoOnClickListener);

	}

	/**
	 * Escuchar el boton de guardar
	 */
	OnClickListener guiGoOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			ContentValues values = new ContentValues();

			values.put(TiempoBusDb.Favoritos.TITULO, guiTitulo.getText().toString());
			values.put(TiempoBusDb.Favoritos.DESCRIPCION, guiDescripcion.getText().toString());
			values.put(TiempoBusDb.Favoritos.POSTE, Integer.valueOf(poste));

			Uri miUriM = ContentUris.withAppendedId(TiempoBusDb.Favoritos.CONTENT_URI, id_uri);

			getContentResolver().update(miUriM, values, null, null);

			Intent intent = new Intent();
			setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent);

			modificadoOK();

			finish();
		}
	};

	/**
	 * Escuchar el boton de cancelar
	 */
	OnClickListener guiCancelListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent();
			setResult(MainActivity.SUB_ACTIVITY_RESULT_CANCEL, intent);
			finish();
		}
	};

	private void modificadoOK() {
		Toast.makeText(this, getResources().getText(R.string.info_modificar), Toast.LENGTH_SHORT).show();
	}

	/**
	 * Seleccion del fondo de la galeria en el arranque
	 */
	private void setupFondoAplicacion() {

		String fondo_galeria = preferencias.getString("image_galeria", "");

		View contenedor_principal = findViewById(R.id.contenedor_nuevo);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

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
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		}

		return super.onOptionsItemSelected(item);

	}

}
