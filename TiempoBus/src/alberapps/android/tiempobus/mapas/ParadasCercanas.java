/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
package alberapps.android.tiempobus.mapas;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Toast;

/**
 * Gestion de paradas cercanas
 * 
 */
public class ParadasCercanas {

	private MapasActivity context;

	private SharedPreferences preferencias;

	public static final String DISTACIA_CERCANA = "-0.001";
	public static final String DISTACIA_MEDIA = "-0.002";
	public static final String DISTACIA_LEJOS = "-0.004";

	public ParadasCercanas(MapasActivity contexto, SharedPreferences preferencia) {

		context = contexto;

		preferencias = preferencia;

	}

	/**
	 * Recuperar las paradas cercanas
	 * 
	 * @param latitud
	 * @param longitud
	 */
	public void cargarParadasCercanas(int latitud, int longitud) {

		if (context.mapOverlays != null) {
			context.mapOverlays.clear();

			context.mapOverlays.add(context.mMyLocationOverlay);
		} else {
			context.mapOverlays = context.mapView.getOverlays();
		}

		context.datosMapaCargadosIda = null;
		context.datosMapaCargadosVuelta = null;
		context.lineaSeleccionada = null;
		context.lineaSeleccionadaDesc = null;
		context.lineaSeleccionadaNum = null;

		// String query =
		// Integer.toString(BuscadorLineasProvider.GET_PARADAS_PROXIMAS);

		// WHERE (LATITUD> (-509837) AND LATITUD < (-469839) AND LONGITUD >
		// (38326241) AND LONGITUD < (38366239))
		// LATITUD> (-709838) AND LATITUD < (-269838) AND LONGITUD > (38126242)
		// AND LONGITUD < (38566242)
		// lat 38342115 ----- long -494467
		// LATITUD> (38126242) AND LATITUD < (38566242) AND LONGITUD > (-709838)
		// AND LONGITUD < (-269838)

		// latitud, longitud
		// String parametros[] = {"38.346242", "-0.489838","-0.001"};
		// //LONG: -0,489838 LATI:38,346242

		String parametros[] = { Integer.toString(latitud), Integer.toString(longitud), context.distancia };

		String selection = Integer.toString(BuscadorLineasProvider.GET_PARADAS_PROXIMAS);

		Cursor cursor = context.managedQuery(BuscadorLineasProvider.PARADAS_PROXIMAS_URI, null, selection, parametros, null);

		if (cursor != null) {
			List<Parada> listaParadas = new ArrayList<Parada>();

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

				Parada par = new Parada();

				par.setConexion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
				par.setCoordenadas(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
				par.setDireccion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
				par.setLatitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
				par.setLongitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
				par.setParada(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_PARADA)).trim());

				par.setRed(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_RED_LINEAS)));

				if (!listaParadas.contains(par)) {
					listaParadas.add(par);
				}
			}

			for (int i = 0; i < listaParadas.size(); i++) {

				if (listaParadas.get(i).getRed().equals(DatosLineasDB.RED_TRAM)) {
					context.drawableIda = context.getResources().getDrawable(R.drawable.tramway);
				} else {
					context.drawableIda = context.getResources().getDrawable(R.drawable.busstop_blue);
				}

				context.itemizedOverlayIda = new MapasItemizedOverlay(context.drawableIda, context);

				GeoPoint point = null;

				point = new GeoPoint(listaParadas.get(i).getLatitud(), listaParadas.get(i).getLongitud());

				String descripcionAlert = context.getString(R.string.lineas) + " ";

				if (listaParadas.get(i).getConexion() != null) {
					descripcionAlert += listaParadas.get(i).getConexion().trim();
				}

				OverlayItem overlayitem = new OverlayItem(point, "[" + listaParadas.get(i).getParada().trim() + "] " + listaParadas.get(i).getDireccion().trim(), descripcionAlert);

				context.itemizedOverlayIda.addOverlay(overlayitem);

				context.mapOverlays.add(context.itemizedOverlayIda);

			}

		} else {

			Toast.makeText(context.getApplicationContext(), context.getString(R.string.gps_no_paradas), Toast.LENGTH_SHORT).show();

		}

	}

	/**
	 * Seleccion de proximidad de paradas
	 */
	public void seleccionarProximidad() {

		final CharSequence[] items = { context.getString(R.string.proximidad_1), context.getString(R.string.proximidad_2), context.getString(R.string.proximidad_3) };

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.proximidad);

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				if (item == 0) {

					context.distancia = DISTACIA_CERCANA;
					context.miLocalizacion(true);

				} else if (item == 1) {

					context.distancia = DISTACIA_MEDIA;
					context.miLocalizacion(true);

				} else if (item == 2) {

					context.distancia = DISTACIA_LEJOS;
					context.miLocalizacion(true);

				}

			}
		});

		AlertDialog alert = builder.create();

		alert.show();

	}

}
