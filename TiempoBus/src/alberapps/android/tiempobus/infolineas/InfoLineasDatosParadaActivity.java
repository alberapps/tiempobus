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
package alberapps.android.tiempobus.infolineas;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.actionbar.ActionBarBuscadorActivity;
import alberapps.android.tiempobus.mapas.MapasActivity;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.mapas.PlaceMark;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Informacion de la linea
 */
public class InfoLineasDatosParadaActivity extends ActionBarBuscadorActivity {

	String paradaSel = "";
	String lineaSel = "";

	BusLinea datosLineaSel = null;

	SharedPreferences preferencias = null;

	private static final int GRIS_BLOG = R.color.gris_blog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.datos_parada);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		try {

			PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
			preferencias = PreferenceManager.getDefaultSharedPreferences(this);

			// Fondo
			setupFondoAplicacion();

			PlaceMark datosParada = (PlaceMark) this.getIntent().getExtras().get("DATOS_PARADA");
			BusLinea datosLinea = (BusLinea) this.getIntent().getExtras().get("DATOS_LINEA");

			TextView parada = (TextView) findViewById(R.id.parada);
			TextView linea = (TextView) findViewById(R.id.linea);
			TextView destino = (TextView) findViewById(R.id.destino);
			TextView localizacion = (TextView) findViewById(R.id.localizacion);
			TextView conexiones = (TextView) findViewById(R.id.conexiones);

			parada.setText(datosParada.getCodigoParada());

			paradaSel = datosParada.getCodigoParada().trim();

			// int pos =
			// UtilidadesTAM.getNumLinea(datosLinea.getIdlinea().trim());

			// String lineaNum = UtilidadesTAM.LINEAS_NUM[pos];

			String lineaNum = datosLinea.getNumLinea();

			lineaSel = lineaNum;
			datosLineaSel = datosLinea;

			linea.setText(datosLinea.getLinea());
			destino.setText(datosParada.getSentido());
			localizacion.setText(datosParada.getTitle());
			conexiones.setText(datosParada.getLineas());

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

					launchMapasSeleccion(lineaSel, datosLineaSel);

				}
			});

		} catch (Exception e) {

			Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_tabs), Toast.LENGTH_SHORT);
			toast.show();
			finish();

		}

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

	private void launchMapasSeleccion(String linea, BusLinea datosLinea) {

		if (linea != null && !linea.equals("")) {
			Intent i = new Intent(this, MapasActivity.class);
			i.putExtra("LINEA_MAPA_FICHA", linea);
			i.putExtra("LINEA_MAPA_FICHA_ONLINE", "true");
			i.putExtra("LINEA_MAPA_FICHA_KML", datosLinea.getIdlinea());
			i.putExtra("LINEA_MAPA_FICHA_DESC", datosLinea.getLinea());
			startActivity(i);
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

	/**
	 * Seleccion del fondo de la galeria en el arranque
	 */
	private void setupFondoAplicacion() {

		String fondo_galeria = preferencias.getString("image_galeria", "");

		View contenedor_principal = findViewById(R.id.datos_contenedor);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

	}

}
