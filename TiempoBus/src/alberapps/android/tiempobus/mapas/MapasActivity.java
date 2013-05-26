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

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.actionbar.ActionBarMapaActivity;
import alberapps.android.tiempobus.data.BusAdapter;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask.LoadDatosLineasAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadDatosMapaAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosMapaAsyncTask.LoadDatosMapaAsyncTaskResponder;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.UtilidadesTAM;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.DatosRuta;
import alberapps.java.tam.mapas.PlaceMark;
import alberapps.java.util.Utilidades;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * Actividad para mapas
 * 
 * 
 */
public class MapasActivity extends ActionBarMapaActivity {

	protected static final int SUB_ACTIVITY_REQUEST_LINEAS = 1000;
	public static final int SUB_ACTIVITY_RESULT_OK = 1002;

	private static final String DISTACIA_CERCANA = "-0.001";
	private static final String DISTACIA_MEDIA = "-0.002";
	private static final String DISTACIA_LEJOS = "-0.004";

	private String distancia = DISTACIA_CERCANA;

	LinearLayout linearLayout;
	MapView mapView;

	List<Overlay> mapOverlays;
	Drawable drawableIda;
	Drawable drawableVuelta;
	Drawable drawableMedio;
	MapasItemizedOverlay itemizedOverlayIda;
	MapasItemizedOverlay itemizedOverlayVuelta;
	MapasItemizedOverlay itemizedOverlayMedio;

	DatosMapa datosMapaCargadosIda;
	DatosMapa datosMapaCargadosVuelta;

	DatosMapa datosMapaCargadosIdaAux;
	DatosMapa datosMapaCargadosVueltaAux;

	String paradaSeleccionada;

	String lineaSeleccionada;
	String lineaSeleccionadaDesc;
	String lineaSeleccionadaNum;

	boolean primeraCarga = true;

	// boolean flagOffline = false;

	private MyLocationOverlay mMyLocationOverlay;

	private ProgressDialog dialog;

	SharedPreferences preferencias = null;

	int modoRed = InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mapas);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		// Control de modo de red
		modoRed = this.getIntent().getIntExtra("MODO_RED", 0);

		if (this.getIntent().getExtras() == null || (this.getIntent().getExtras() != null && !this.getIntent().getExtras().containsKey("MODO_RED"))) {

			modoRed = preferencias.getInt("infolinea_modo", 0);

		}

		primeraCarga = true;

		mapView = (MapView) findViewById(R.id.mapview);

		mapView.setBuiltInZoomControls(true);

		mMyLocationOverlay = new MyLocationOverlay(this, mapView);

		MapController mapController = mapView.getController();

		GeoPoint point = null;

		point = new GeoPoint(38337176, -491892);

		mapController.animateTo(point);
		mapController.setZoom(10);

		// Si viene de la seleccion de la lista
		if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("LINEA_MAPA")) {

			int lineaPos = UtilidadesTAM.getIdLinea(this.getIntent().getExtras().getString("LINEA_MAPA"));

			Log.d("mapas", "linea: " + lineaPos + "l: " + this.getIntent().getExtras().getString("LINEA_MAPA"));

			if (lineaPos > -1) {

				lineaSeleccionada = UtilidadesTAM.LINEAS_CODIGO_KML[lineaPos];
				lineaSeleccionadaDesc = UtilidadesTAM.LINEAS_DESCRIPCION[lineaPos];

				lineaSeleccionadaNum = UtilidadesTAM.LINEAS_NUM[lineaPos];

				dialog = ProgressDialog.show(MapasActivity.this, "", getString(R.string.dialogo_espera), true);

				// loadDatosMapa();

				loadDatosMapaOffline();

			} else {
				// launchBuses();
				Toast.makeText(this, getResources().getText(R.string.aviso_error_datos) + " www.subus.es", Toast.LENGTH_LONG).show();

			}

		} else if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("LINEA_MAPA_FICHA")) {

			String lineaPos = this.getIntent().getExtras().getString("LINEA_MAPA_FICHA");

			// lineaSeleccionada = UtilidadesTAM.LINEAS_CODIGO_KML[lineaPos];

			// int pos = UtilidadesTAM.getIdLinea(lineaPos);

			// lineaSeleccionada = UtilidadesTAM.LINEAS_CODIGO_KML[pos];
			// lineaSeleccionadaDesc = UtilidadesTAM.LINEAS_DESCRIPCION[pos];

			lineaSeleccionada = this.getIntent().getExtras().getString("LINEA_MAPA_FICHA_KML");
			lineaSeleccionadaDesc = this.getIntent().getExtras().getString("LINEA_MAPA_FICHA_DESC");

			lineaSeleccionadaNum = lineaPos;

			dialog = ProgressDialog.show(MapasActivity.this, "", getString(R.string.dialogo_espera), true);

			if (this.getIntent().getExtras().containsKey("LINEA_MAPA_FICHA_ONLINE")) {
				loadDatosMapa();
			} else {
				loadDatosMapaOffline();
			}

		}

		else {
			// Si se entra desde el menu
			// launchBuses();
			// miLocalizacion();

		}

		// Combo de seleccion de datos
		final Spinner spinner = (Spinner) findViewById(R.id.spinner_datos);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_datos, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		// Seleccion inicial
		int infolineaModo = preferencias.getInt("infolinea_modo", 0);
		spinner.setSelection(infolineaModo);

		// Seleccion
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				// Solo en caso de haber cambiado
				if (preferencias.getInt("infolinea_modo", 0) != arg2) {

					// Guarda la nueva seleciccion
					SharedPreferences.Editor editor = preferencias.edit();
					editor.putInt("infolinea_modo", arg2);
					editor.commit();

					// cambiar el modo de la actividad
					if (arg2 == 0) {

						Intent intent2 = getIntent();
						intent2.putExtra("MODO_RED", InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE);
						finish();
						startActivity(intent2);

					} else if (arg2 == 1) {

						Intent intent2 = getIntent();
						intent2.putExtra("MODO_RED", InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE);
						finish();
						startActivity(intent2);

					} else if (arg2 == 2) {

						Intent intent2 = getIntent();
						intent2.putExtra("MODO_RED", InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE);
						finish();
						startActivity(intent2);

					}

				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

	}

	private void enableLocationSettings() {
		Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
	}

	/**
	 * Control de posicion
	 * 
	 * @param cercanas
	 */
	private void miLocalizacion(final boolean cercanas) {

		if (!mMyLocationOverlay.isMyLocationEnabled()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.gps_on)).setCancelable(false).setPositiveButton(getString(R.string.barcode_si), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					enableLocationSettings();

				}
			}).setNegativeButton(getString(R.string.barcode_no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();

				}
			});
			AlertDialog alert = builder.create();

			alert.show();

		} else {

			if (cercanas) {
				setTitle(getString(R.string.cercanas));
			}

			if (primeraCarga) {

				Toast.makeText(this, getString(R.string.gps_recuperando), Toast.LENGTH_SHORT).show();

				mMyLocationOverlay.runOnFirstFix(new Runnable() {
					public void run() {

						mapView.getController().animateTo(mMyLocationOverlay.getMyLocation());

						if (cercanas) {
							cargarParadasCercanas(mMyLocationOverlay.getMyLocation().getLatitudeE6(), mMyLocationOverlay.getMyLocation().getLongitudeE6());
						}

						primeraCarga = false;

					}
				});

				mapView.getOverlays().add(mMyLocationOverlay);

				mapView.getController().setZoom(18);
				mapView.setClickable(true);
				mapView.setEnabled(true);

			} else {
				miLocalizacionRecarga(cercanas);
			}

		}

	}

	/**
	 * Recarga de la posicion
	 * 
	 * @param cercanas
	 */
	private void miLocalizacionRecarga(boolean cercanas) {

		Toast.makeText(this, getString(R.string.gps_recuperando), Toast.LENGTH_SHORT).show();

		if (cercanas) {
			cargarParadasCercanas(mMyLocationOverlay.getMyLocation().getLatitudeE6(), mMyLocationOverlay.getMyLocation().getLongitudeE6());

		}

		mapView.getController().animateTo(mMyLocationOverlay.getMyLocation());

		mapView.getController().setZoom(18);
		mapView.setClickable(true);
		mapView.setEnabled(true);

	}

	/**
	 * Recuperar las paradas cercanas
	 * 
	 * @param latitud
	 * @param longitud
	 */
	private void cargarParadasCercanas(int latitud, int longitud) {

		if (mapOverlays != null) {
			mapOverlays.clear();

			mapOverlays.add(mMyLocationOverlay);
		}

		datosMapaCargadosIda = null;
		datosMapaCargadosVuelta = null;
		lineaSeleccionada = null;
		lineaSeleccionadaDesc = null;
		lineaSeleccionadaNum = null;

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

		String parametros[] = { Integer.toString(latitud), Integer.toString(longitud), distancia };

		String selection = Integer.toString(BuscadorLineasProvider.GET_PARADAS_PROXIMAS);

		Cursor cursor = managedQuery(BuscadorLineasProvider.PARADAS_PROXIMAS_URI, null, selection, parametros, null);

		if (cursor != null) {
			List<Parada> listaParadas = new ArrayList<Parada>();

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

				Parada par = new Parada();

				// par.setLineaNum(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
				// par.setLineaDesc(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
				par.setConexion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
				par.setCoordenadas(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
				// par.setDestino(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
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

				mapOverlays = mapView.getOverlays();

				if (listaParadas.get(i).getRed().equals(DatosLineasDB.RED_TRAM)) {
					drawableIda = this.getResources().getDrawable(R.drawable.tramway);
				} else {
					drawableIda = this.getResources().getDrawable(R.drawable.busstop_blue);
				}

				itemizedOverlayIda = new MapasItemizedOverlay(drawableIda, this);

				GeoPoint point = null;

				point = new GeoPoint(listaParadas.get(i).getLatitud(), listaParadas.get(i).getLongitud());

				String descripcionAlert = getResources().getText(R.string.lineas) + " ";

				if (listaParadas.get(i).getConexion() != null) {
					descripcionAlert += listaParadas.get(i).getConexion().trim();
				}

				OverlayItem overlayitem = new OverlayItem(point, "[" + listaParadas.get(i).getParada().trim() + "] " + listaParadas.get(i).getDireccion().trim(), descripcionAlert);

				itemizedOverlayIda.addOverlay(overlayitem);

				mapOverlays.add(itemizedOverlayIda);

			}

		} else {

			Toast.makeText(getApplicationContext(), getString(R.string.gps_no_paradas), Toast.LENGTH_SHORT).show();

		}

	}

	/**
	 * Cargar datos en modo offline
	 */
	private void loadDatosMapaOffline() {

		DatosMapa datosIda = new DatosMapa();
		DatosMapa datosVuelta = new DatosMapa();

		String parametros[] = { lineaSeleccionadaNum };

		Cursor cursorParadas = managedQuery(BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null);

		if (cursorParadas != null) {
			List<Parada> listaParadasIda = new ArrayList<Parada>();

			List<Parada> listaParadasVuelta = new ArrayList<Parada>();

			String destinoIda = "";
			String destinoVuelta = "";

			for (cursorParadas.moveToFirst(); !cursorParadas.isAfterLast(); cursorParadas.moveToNext()) {

				Parada par = new Parada();

				par.setLineaNum(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
				par.setLineaDesc(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
				par.setConexion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
				par.setCoordenadas(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
				par.setDestino(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
				par.setDireccion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
				par.setLatitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
				par.setLongitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
				par.setParada(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));
				par.setObservaciones(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_OBSERVACIONES)));

				if (destinoIda.equals("")) {
					destinoIda = par.getDestino();
				} else if (destinoVuelta.equals("") && !destinoIda.equals(par.getDestino())) {
					destinoVuelta = par.getDestino();
				}

				if (par.getDestino().equals(destinoIda)) {

					listaParadasIda.add(par);

				} else if (par.getDestino().equals(destinoVuelta)) {

					listaParadasVuelta.add(par);

				}

			}

			if (listaParadasIda != null && !listaParadasIda.isEmpty() && listaParadasVuelta != null && !listaParadasVuelta.isEmpty()) {
				datosIda = mapearDatosModelo(listaParadasIda);

				datosVuelta = mapearDatosModelo(listaParadasVuelta);

				datosMapaCargadosIda = datosIda;

				datosMapaCargadosVuelta = datosVuelta;

				// Recorrido

				Cursor cursorRecorrido = managedQuery(BuscadorLineasProvider.PARADAS_LINEA_RECORRIDO_URI, null, null, parametros, null);
				if (cursorRecorrido != null) {
					cursorRecorrido.moveToFirst();

					datosMapaCargadosIda.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));

					cursorRecorrido.moveToNext();

					datosMapaCargadosVuelta.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));

				}

				// Cargar datos en el mapa
				cargarMapa();

			} else {
				Toast toast = Toast.makeText(this, getResources().getText(R.string.error_datos_offline), Toast.LENGTH_SHORT);
				toast.show();
			}

		} else {
			Toast toast = Toast.makeText(this, getResources().getText(R.string.aviso_error_datos), Toast.LENGTH_SHORT);
			toast.show();
		}

		dialog.dismiss();

	}

	/**
	 * Cargar datos en modo offline
	 */
	private void loadDatosMapaTRAMOffline() {

		DatosMapa datosIda = new DatosMapa();

		String parametros[] = { lineaSeleccionadaNum };

		Cursor cursorParadas = managedQuery(BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null);

		if (cursorParadas != null) {
			List<Parada> listaParadasIda = new ArrayList<Parada>();

			for (cursorParadas.moveToFirst(); !cursorParadas.isAfterLast(); cursorParadas.moveToNext()) {

				Parada par = new Parada();

				par.setLineaNum(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
				par.setLineaDesc(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
				par.setConexion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
				par.setCoordenadas(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
				par.setDestino(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
				par.setDireccion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
				par.setLatitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
				par.setLongitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
				par.setParada(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));
				par.setObservaciones(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_OBSERVACIONES)));

				listaParadasIda.add(par);

			}

			datosIda = mapearDatosModelo(listaParadasIda);

			datosMapaCargadosIda = datosIda;

			// Cargar datos en el mapa
			cargarMapa();

		} else {
			Toast toast = Toast.makeText(this, getResources().getText(R.string.aviso_error_datos), Toast.LENGTH_SHORT);
			toast.show();
		}

		dialog.dismiss();

	}

	/**
	 * Cargar datos en modo online
	 * 
	 * @param listaParadas
	 * @return
	 */
	private DatosMapa mapearDatosModelo(List<Parada> listaParadas) {

		DatosMapa datos = new DatosMapa();

		datos.setPlacemarks(new ArrayList<PlaceMark>());

		for (int i = 0; i < listaParadas.size(); i++) {

			PlaceMark placeMark = new PlaceMark();

			placeMark.setAddress(listaParadas.get(i).getDireccion());
			placeMark.setCodigoParada(listaParadas.get(i).getParada());
			placeMark.setCoordinates(listaParadas.get(i).getCoordenadas());
			placeMark.setDescription(listaParadas.get(i).getLineaDesc());
			placeMark.setLineas(listaParadas.get(i).getConexion());
			placeMark.setObservaciones(listaParadas.get(i).getObservaciones());
			placeMark.setSentido(listaParadas.get(i).getDestino());
			placeMark.setTitle(listaParadas.get(i).getDireccion());

			datos.getPlacemarks().add(placeMark);
		}

		datos.setCurrentPlacemark(datos.getPlacemarks().get(0));

		return datos;
	}

	/**
	 * kml de carga
	 */
	private void loadDatosMapa() {

		String url = UtilidadesTAM.getKMLParadasIda(lineaSeleccionada);

		String urlRecorrido = UtilidadesTAM.getKMLRecorridoIda(lineaSeleccionada);

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadDatosMapaAsyncTask(loadDatosMapaAsyncTaskResponderIda).execute(url, urlRecorrido);
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

	/**
	 * Se llama cuando las paradas hayan sido cargadas
	 */
	LoadDatosMapaAsyncTaskResponder loadDatosMapaAsyncTaskResponderIda = new LoadDatosMapaAsyncTaskResponder() {
		public void datosMapaLoaded(DatosMapa datos) {

			if (datos != null) {
				datosMapaCargadosIda = datos;

				loadDatosMapaVuelta();

			} else {

				Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.aviso_error_datos) + " www.subus.es", Toast.LENGTH_SHORT);
				toast.show();
				finish();

				dialog.dismiss();

			}

		}
	};

	private void loadDatosMapaVuelta() {

		String url = UtilidadesTAM.getKMLParadasVuelta(lineaSeleccionada);

		String urlVuelta = UtilidadesTAM.getKMLRecorridoVuelta(lineaSeleccionada);

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadDatosMapaAsyncTask(loadDatosMapaAsyncTaskResponderVuelta).execute(url, urlVuelta);
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

	/**
	 * Se llama cuando las paradas hayan sido cargadas
	 */
	LoadDatosMapaAsyncTaskResponder loadDatosMapaAsyncTaskResponderVuelta = new LoadDatosMapaAsyncTaskResponder() {
		public void datosMapaLoaded(DatosMapa datos) {

			if (datos != null) {
				try {
					datosMapaCargadosVuelta = datos;
					cargarMapa();

				} catch (Exception e) {

					Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.aviso_error_datos) + " www.subus.es", Toast.LENGTH_SHORT);
					toast.show();

				}

				dialog.dismiss();

			} else {

				Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.aviso_error_datos) + " www.subus.es", Toast.LENGTH_SHORT);
				toast.show();

				dialog.dismiss();

				finish();

			}

		}
	};

	/**
	 * Cargar el mapa con las paradas de la linea
	 * 
	 */
	private void cargarMapa() {

		// Cargar datos cabecera
		String cabdatos = lineaSeleccionadaDesc;
		setTitle(cabdatos);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();

		if (modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
			drawableIda = this.getResources().getDrawable(R.drawable.tramway);
		} else {
			drawableIda = this.getResources().getDrawable(R.drawable.busstop_blue);
		}

		drawableVuelta = this.getResources().getDrawable(R.drawable.busstop_green);
		drawableMedio = this.getResources().getDrawable(R.drawable.busstop_medio);
		itemizedOverlayIda = new MapasItemizedOverlay(drawableIda, this);
		itemizedOverlayVuelta = new MapasItemizedOverlay(drawableVuelta, this);
		itemizedOverlayMedio = new MapasItemizedOverlay(drawableMedio, this);

		// -0.510017579,38.386057662,0
		// 38.386057662,-0.510017579

		/**
		 * 38.344820, -0.483320‎ +38° 20' 41.35", -0° 28' 59.95"
		 * 38.34482,-0.48332
		 * 
		 * long: -0,510018  lati: 38,386058 PRUEBAS‎
		 * 
		 */

		// Carga de puntos del mapa

		GeoPoint point = null;

		// Datos IDA
		if (datosMapaCargadosIda != null && !datosMapaCargadosIda.getPlacemarks().isEmpty()) {

			// Recorrido
			drawPath(datosMapaCargadosIda, Color.parseColor("#157087"), mapView);

			for (int i = 0; i < datosMapaCargadosIda.getPlacemarks().size(); i++) {

				String[] coordenadas = datosMapaCargadosIda.getPlacemarks().get(i).getCoordinates().split(",");

				double lat = Double.parseDouble(coordenadas[1]); // 38.386058;
				double lng = Double.parseDouble(coordenadas[0]); // -0.510018;
				int glat = (int) (lat * 1E6);
				int glng = (int) (lng * 1E6);

				// 19240000,-99120000

				point = new GeoPoint(glat, glng);
				// GeoPoint point = new GeoPoint(19240000,-99120000);

				String descripcionAlert = getResources().getText(R.string.share_2) + " ";

				if (datosMapaCargadosIda.getPlacemarks().get(i).getSentido() != null && !datosMapaCargadosIda.getPlacemarks().get(i).getSentido().trim().equals("")) {
					descripcionAlert += datosMapaCargadosIda.getPlacemarks().get(i).getSentido().trim();
				} else {
					descripcionAlert += "Ida";
				}

				descripcionAlert += "\n" + getResources().getText(R.string.lineas) + " ";

				if (datosMapaCargadosIda.getPlacemarks().get(i).getLineas() != null) {
					descripcionAlert += datosMapaCargadosIda.getPlacemarks().get(i).getLineas().trim();
				}

				descripcionAlert += "\n" + getResources().getText(R.string.observaciones) + " ";

				if (datosMapaCargadosIda.getPlacemarks().get(i).getObservaciones() != null) {
					descripcionAlert += datosMapaCargadosIda.getPlacemarks().get(i).getObservaciones().trim();
				}

				OverlayItem overlayitem = new OverlayItem(point, "[" + datosMapaCargadosIda.getPlacemarks().get(i).getCodigoParada().trim() + "] " + datosMapaCargadosIda.getPlacemarks().get(i).getTitle().trim(),
						descripcionAlert);

				itemizedOverlayIda.addOverlay(overlayitem);

			}

			if (itemizedOverlayIda != null && itemizedOverlayIda.size() > 0) {
				mapOverlays.add(itemizedOverlayIda);
			} else {
				avisoPosibleError();
			}

		}

		boolean coincide = false;

		// Datos VUELTA
		if (datosMapaCargadosVuelta != null && !datosMapaCargadosVuelta.getPlacemarks().isEmpty()) {

			// Recorrido
			drawPath(datosMapaCargadosVuelta, Color.parseColor("#6C8715"), mapView);

			for (int i = 0; i < datosMapaCargadosVuelta.getPlacemarks().size(); i++) {

				String[] coordenadas = datosMapaCargadosVuelta.getPlacemarks().get(i).getCoordinates().split(",");

				double lat = Double.parseDouble(coordenadas[1]); // 38.386058;
				double lng = Double.parseDouble(coordenadas[0]); // -0.510018;
				int glat = (int) (lat * 1E6);
				int glng = (int) (lng * 1E6);

				// 19240000,-99120000

				point = new GeoPoint(glat, glng);
				// GeoPoint point = new GeoPoint(19240000,-99120000);

				String direc = "";

				coincide = false;

				if (datosMapaCargadosIda.getPlacemarks().contains(datosMapaCargadosVuelta.getPlacemarks().get(i))) {

					String ida = datosMapaCargadosIda.getCurrentPlacemark().getSentido().trim();

					if (ida.equals("")) {
						ida = "Ida";
					}

					direc = ida + " " + getResources().getText(R.string.tiempo_m_3) + " " + datosMapaCargadosVuelta.getPlacemarks().get(i).getSentido();

					coincide = true;

				} else {
					direc = datosMapaCargadosVuelta.getCurrentPlacemark().getSentido().trim();

					coincide = false;
				}

				if (direc == null || (direc != null && direc.trim().equals(""))) {
					direc = "Vuelta";
				}

				String descripcionAlert = getResources().getText(R.string.share_2) + " ";

				if (direc != null) {
					descripcionAlert += direc;
				}

				descripcionAlert += "\n" + getResources().getText(R.string.lineas) + " ";

				if (datosMapaCargadosVuelta.getPlacemarks().get(i).getLineas() != null) {
					descripcionAlert += datosMapaCargadosVuelta.getPlacemarks().get(i).getLineas().trim();
				}

				descripcionAlert += "\n" + getResources().getText(R.string.observaciones) + " ";

				if (datosMapaCargadosVuelta.getPlacemarks().get(i).getObservaciones() != null) {
					descripcionAlert += datosMapaCargadosVuelta.getPlacemarks().get(i).getObservaciones().trim();
				}

				OverlayItem overlayitem = new OverlayItem(point, "[" + datosMapaCargadosVuelta.getPlacemarks().get(i).getCodigoParada().trim() + "] " + datosMapaCargadosVuelta.getPlacemarks().get(i).getTitle().trim(),
						descripcionAlert);

				if (coincide) {
					itemizedOverlayMedio.addOverlay(overlayitem);
				} else {
					itemizedOverlayVuelta.addOverlay(overlayitem);
				}

			}

			if (itemizedOverlayMedio != null && itemizedOverlayMedio.size() > 0 && datosMapaCargadosIda != null && !datosMapaCargadosIda.getPlacemarks().isEmpty()) {
				mapOverlays.add(itemizedOverlayMedio);
			}

			if (itemizedOverlayVuelta.size() > 0) {
				mapOverlays.add(itemizedOverlayVuelta);
			} else {
				avisoPosibleError();
			}

		}

		if (!primeraCarga) {
			mapView.getOverlays().add(mMyLocationOverlay);
			mapView.getController().animateTo(mMyLocationOverlay.getMyLocation());

		} else {

			MapController mapController = mapView.getController();

			mapController.animateTo(point);
			mapController.setZoom(15);
		}

	}

	private void avisoPosibleError() {

		Toast.makeText(this, getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

	}

	/**
	 * Ir a la parada seleccionada y cerra mapa
	 * 
	 */
	public void irParadaSeleccionada() {

		int codigo;

		if (paradaSeleccionada != null) {
			codigo = Integer.parseInt(paradaSeleccionada.substring(1, 5));

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
		super.finish();

	}

	public String getParadaSeleccionada() {
		return paradaSeleccionada;
	}

	public void setParadaSeleccionada(String paradaSeleccionada) {
		this.paradaSeleccionada = paradaSeleccionada;
	}

	private void launchBuses() {

		// flagOffline = false;

		cargarDatosLineas();

	}

	private void launchBusesOffline() {

		// flagOffline = true;

		cargarDatosLineas();

	}

	ArrayList<BusLinea> lineasBus;
	BusAdapter lineasAdapter;

	Dialog dialogoLineas = null;

	private void cargarDatosLineas() {

		lineasAdapter = new BusAdapter(this, android.R.layout.simple_list_item_1);

		loadBuses();

	}

	private void abrirDialogoLineas() {

		dialogoLineas = new Dialog(this);

		dialogoLineas.setTitle(R.string.tit_buses);

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View vista = li.inflate(R.layout.buses, null, false);

		dialogoLineas.setContentView(vista);

		ListView lista = (ListView) dialogoLineas.findViewById(android.R.id.list);

		lista.setAdapter(lineasAdapter);

		lista.setOnItemClickListener(lineasClickedHandler);

		dialogoLineas.show();

	}

	/**
	 * Listener encargado de gestionar las pulsaciones sobre los items
	 */
	private OnItemClickListener lineasClickedHandler = new OnItemClickListener() {

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

			dialogoLineas.dismiss();

			lineaSeleccionada(position);

		}
	};

	private void lineaSeleccionada(int posicion) {

		// Limpiar lista anterior para nuevas busquedas
		if (mapView != null && mapView.getOverlays() != null)
			mapView.getOverlays().clear();

		lineaSeleccionada = lineasBus.get(posicion).getIdlinea();
		lineaSeleccionadaDesc = lineasBus.get(posicion).getLinea();

		lineaSeleccionadaNum = lineasBus.get(posicion).getNumLinea();

		dialog = ProgressDialog.show(MapasActivity.this, "", getString(R.string.dialogo_espera), true);

		if (modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE) {
			loadDatosMapa();
		} else if (modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {
			loadDatosMapaOffline();
		} else if (modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
			loadDatosMapaTRAMOffline();
		}

	}

	/**
	 * Carga las lineas de bus
	 */
	private void loadBuses() {

		dialog = ProgressDialog.show(MapasActivity.this, "", getString(R.string.dialogo_espera), true);

		// Carga local de lineas
		String datosOffline = null;
		if (modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {

			Resources resources = getResources();
			InputStream inputStream = resources.openRawResource(R.raw.lineasoffline);

			datosOffline = Utilidades.obtenerStringDeStream(inputStream);

		} else if (modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {

			Resources resources = getResources();
			InputStream inputStream = resources.openRawResource(R.raw.lineasoffline_tram);

			datosOffline = Utilidades.obtenerStringDeStream(inputStream);

		}

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadDatosLineasAsyncTask(loadBusesAsyncTaskResponder).execute(datosOffline);
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

	LoadDatosLineasAsyncTaskResponder loadBusesAsyncTaskResponder = new LoadDatosLineasAsyncTaskResponder() {
		public void busesLoaded(ArrayList<BusLinea> buses) {
			if (buses != null) {
				lineasBus = buses;
				lineasAdapter.clear();
				lineasAdapter.addAll(lineasBus);
				lineasAdapter.notifyDataSetChanged();

				dialog.dismiss();

				abrirDialogoLineas();

			} else {

				dialog.dismiss();

			}

		}
	};

	/**
	 * Mostrar y ocultar el recorrido de ida
	 */
	private void cargarOcultarIda() {

		if (datosMapaCargadosVuelta != null && datosMapaCargadosVuelta.getPlacemarks().isEmpty()) {

			Toast.makeText(this, getString(R.string.mapa_aviso_vacio), Toast.LENGTH_SHORT).show();

		} else if (datosMapaCargadosIda != null) {

			if (!datosMapaCargadosIda.getPlacemarks().isEmpty()) {
				datosMapaCargadosIdaAux = new DatosMapa();
				datosMapaCargadosIdaAux.setPlacemarks(datosMapaCargadosIda.getPlacemarks());
				datosMapaCargadosIda.setPlacemarks(new ArrayList<PlaceMark>());
			} else if (datosMapaCargadosIdaAux != null) {
				datosMapaCargadosIda.setPlacemarks(datosMapaCargadosIdaAux.getPlacemarks());
			}

			// Limpiar lista anterior para nuevas busquedas
			if (mapView != null && mapView.getOverlays() != null)
				mapView.getOverlays().clear();

			cargarMapa();
		}
	}

	/**
	 * Mostrar y ocultar el recorrido de vuelta
	 */
	private void cargarOcultarVuelta() {

		if (datosMapaCargadosIda != null && datosMapaCargadosIda.getPlacemarks().isEmpty()) {

			Toast.makeText(this, getString(R.string.mapa_aviso_vacio), Toast.LENGTH_SHORT).show();

		} else if (datosMapaCargadosVuelta != null) {

			if (!datosMapaCargadosVuelta.getPlacemarks().isEmpty()) {
				datosMapaCargadosVueltaAux = new DatosMapa();
				datosMapaCargadosVueltaAux.setPlacemarks(datosMapaCargadosVuelta.getPlacemarks());
				datosMapaCargadosVuelta.setPlacemarks(new ArrayList<PlaceMark>());
			} else if (datosMapaCargadosVueltaAux != null) {
				datosMapaCargadosVuelta.setPlacemarks(datosMapaCargadosVueltaAux.getPlacemarks());
			}

			// Limpiar lista anterior para nuevas busquedas
			if (mapView != null && mapView.getOverlays() != null)
				mapView.getOverlays().clear();

			cargarMapa();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.mapa, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*
		 * case R.id.menu_search: onSearchRequested(); break;
		 */
		case R.id.menu_satelite:

			if (mapView.isSatellite()) {
				mapView.setSatellite(false);
			} else {
				mapView.setSatellite(true);
			}

			break;
		case R.id.menu_ida:
			if (modoRed != InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
				cargarOcultarIda();
			}
			break;
		case R.id.menu_vuelta:
			if (modoRed != InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
				cargarOcultarVuelta();
			}
			break;
		case R.id.menu_cercanas:

			seleccionarProximidad();

			break;
		case R.id.menu_posicion:
			miLocalizacion(false);
			break;
		case R.id.menu_search_online:
			launchBuses();
			break;
		/*
		 * case R.id.menu_search_offline: launchBusesOffline(); break;
		 */
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {

		Intent intent = new Intent();
		setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent);
		super.finish();

	}

	@Override
	protected void onResume() {
		super.onResume();
		mMyLocationOverlay.enableMyLocation();
	}

	@Override
	protected void onStop() {
		mMyLocationOverlay.disableMyLocation();
		super.onStop();
	}

	private void seleccionarProximidad() {

		final CharSequence[] items = { getResources().getString(R.string.proximidad_1), getResources().getString(R.string.proximidad_2), getResources().getString(R.string.proximidad_3) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.proximidad);

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				if (item == 0) {

					distancia = DISTACIA_CERCANA;
					miLocalizacion(true);

				} else if (item == 1) {

					distancia = DISTACIA_MEDIA;
					miLocalizacion(true);

				} else if (item == 2) {

					distancia = DISTACIA_LEJOS;
					miLocalizacion(true);

				}

			}
		});

		AlertDialog alert = builder.create();

		alert.show();

	}

	public void drawPath(DatosMapa navSet, int color, MapView mMapView01) {

		// color correction for dining, make it darker
		if (color == Color.parseColor("#add331"))
			color = Color.parseColor("#6C8715");

		/*
		 * Collection overlaysToAddAgain = new ArrayList(); for (Iterator iter =
		 * mMapView01.getOverlays().iterator(); iter.hasNext();) { Object o =
		 * iter.next();
		 * 
		 * if (!DatosRuta.class.getName().equals(o.getClass().getName())) { //
		 * mMapView01.getOverlays().remove(o); overlaysToAddAgain.add(o); } }
		 * mMapView01.getOverlays().clear();
		 * mMapView01.getOverlays().addAll(overlaysToAddAgain);
		 */
		String path = navSet.getRecorrido();

		if (path != null && path.trim().length() > 0) {
			String[] pairs = path.trim().split(" ");

			String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude
													// lngLat[1]=latitude
													// lngLat[2]=height

			if (lngLat.length < 3)
				lngLat = pairs[1].split(","); // if first pair is not
												// transferred completely, take
												// seconds pair //TODO

			try {
				GeoPoint startGP = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));
				mMapView01.getOverlays().add(new DatosRuta(startGP, startGP, 1));
				GeoPoint gp1;
				GeoPoint gp2 = startGP;

				for (int i = 1; i < pairs.length; i++) // the last one would be
														// crash
				{
					lngLat = pairs[i].split(",");

					gp1 = gp2;

					// if (lngLat.length >= 2 && gp1.getLatitudeE6() > 0 &&
					// gp1.getLongitudeE6() > 0
					// && gp2.getLatitudeE6() > 0 && gp2.getLongitudeE6() > 0) {
					if (lngLat.length >= 2) {
						// for GeoPoint, first:latitude, second:longitude
						gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));

						// if (gp2.getLatitudeE6() != 22200000) {
						mMapView01.getOverlays().add(new DatosRuta(gp1, gp2, 2, color));

						// }
					}

				}
				// DatosRutas.add(new DatosRuta(gp2,gp2, 3));
				mMapView01.getOverlays().add(new DatosRuta(gp2, gp2, 3));
			} catch (NumberFormatException e) {

			}
		}

	}

}
