/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
package alberapps.android.tiempobuswidgets;

import java.util.List;

import alberapps.java.datos.Datos;
import alberapps.java.datos.GestionarDatos;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * 
 * Actividad para eliminar registros
 * 
 */
public class EliminarDatoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.eliminar);

		// Get the intent that started this activity
		Intent intent = getIntent();

		final int datoEliminar = intent.getIntExtra("DATO", -1);

		// boton parada
		Button botonAceptar = (Button) findViewById(R.id.aceptar_eliminar);
		botonAceptar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {

				SharedPreferences preferencias = getSharedPreferences("datoswidget", Context.MODE_MULTI_PROCESS);

				if (datoEliminar >= 0) {

					List<Datos> lineasParada = GestionarDatos.listaDatos(preferencias.getString("lineas_parada", ""));

					lineasParada.remove(datoEliminar);

					String nuevo = GestionarDatos.getStringDeLista(lineasParada);

					SharedPreferences.Editor editor = preferencias.edit();
					editor.putString("lineas_parada", nuevo);
					editor.commit();

					Log.d("tag", " eliminado: " + datoEliminar);

					final String mostrar = getString(R.string.eliminar_ok);
					Toast.makeText(getApplicationContext(), mostrar, Toast.LENGTH_SHORT).show();

					actualizarWidget();

				}

				finish();
			}

		});

		Button botonCancelar = (Button) findViewById(R.id.cancelar_eliminar);
		botonCancelar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {

				finish();
			}

		});

	}

	/**
	 * Actualizar el contenido del widget
	 */
	private void actualizarWidget() {

		AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());

		// int[] awid= awm.getAppWidgetIds(new
		// ComponentName(this,TiemposWidgetProvider.class));

		// if(awid.length > 0){

		Intent updateIntent = new Intent(getApplicationContext(), TiemposWidgetProvider.class);
		updateIntent.setAction(TiemposWidgetProvider.REFRESH_ACTION);

		getApplicationContext().sendBroadcast(updateIntent);

		// new TiemposWidgetProvider().actualizar(context,
		// intent).onUpdate(this, awm, awid);
		// }

	}

}
