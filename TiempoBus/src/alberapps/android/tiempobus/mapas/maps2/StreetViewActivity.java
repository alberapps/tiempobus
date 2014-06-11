/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
 * 
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
package alberapps.android.tiempobus.mapas.maps2;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.actionbar.ActionBarActivityFragments;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Detalle de la noticia
 */
@SuppressLint("NewApi")
public class StreetViewActivity extends ActionBarActivityFragments {

	SharedPreferences preferencias = null;

	private LatLng coordenadas;
	private StreetViewPanorama mSvp;

	//private String descLinea;

	private String datosTitulo;

	private String datosMensaje;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapas_streetview);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			if(actionBar != null){
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
		}

		// Carga de datos extra
		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			double latitud = extras.getDouble("LATITUD");
			double longitud = extras.getDouble("LONGITUD");

			//descLinea = extras.getString("DATOS_LINEA");

			coordenadas = new LatLng(latitud, longitud);

			datosTitulo = extras.getString("DATOS_TITULO");
			datosMensaje = extras.getString("DATOS_MENSAJE");

		}

		// Si coordenadas cargadas
		if (coordenadas != null) {
			setUpStreetViewPanoramaIfNeeded(savedInstanceState);
		}

		// Datos pie
		if (datosTitulo != null && !datosTitulo.equals("")) {

			TextView textLinea = (TextView) findViewById(R.id.datos_linea);
			textLinea.setText(datosTitulo);

		}

		if (datosMensaje != null && !datosMensaje.equals("")) {

			datosMensaje = datosMensaje.substring(0, datosMensaje.lastIndexOf("\n"));

			TextView pieMensaje = (TextView) findViewById(R.id.pie_descrip);
			pieMensaje.setText(datosMensaje);
		}

	}

	/**
	 * StreetView
	 * 
	 * @param savedInstanceState
	 */
	private void setUpStreetViewPanoramaIfNeeded(Bundle savedInstanceState) {
		if (mSvp == null) {
			mSvp = ((SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama)).getStreetViewPanorama();
			if (mSvp != null) {
				if (savedInstanceState == null) {

					Log.d("STREETVIEW", "coordenadas 2: " + coordenadas.latitude + " - " + coordenadas.longitude);

					mSvp.setPosition(coordenadas);

				}
			}
		}
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

	@Override
	protected void onStart() {

		super.onStart();

		if (preferencias.getBoolean("analytics_on", true)) {
			GoogleAnalytics.getInstance(this).reportActivityStart(this);
		}

	}

	@Override
	protected void onStop() {

		if (preferencias.getBoolean("analytics_on", true)) {
			GoogleAnalytics.getInstance(this).reportActivityStop(this);
		}

		super.onStop();

	}

}