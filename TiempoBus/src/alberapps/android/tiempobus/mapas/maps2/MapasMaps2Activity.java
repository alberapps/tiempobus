/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.actionbar.ActionBarActivityFragments;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.mapas.DatosMapa;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapasMaps2Activity extends ActionBarActivityFragments implements OnMarkerClickListener, OnInfoWindowClickListener, OnMarkerDragListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener {

	public String lineaSeleccionada;
	public String lineaSeleccionadaDesc;
	public String lineaSeleccionadaNum;

	public AsyncTask<String, Void, DatosMapa> taskDatosMapa = null;
	public AsyncTask<String, Void, DatosMapa> taskDatosMapaVuelta = null;
	public AsyncTask<String, Void, ArrayList<BusLinea>> taskBuses = null;
	public AsyncTask<String, Void, DatosMapa> taskVehiculosMapa = null;
	public AsyncTask<String, Void, DatosMapa[]> taskDatosMapaV3 = null;

	public ProgressDialog dialog;

	public DatosMapa datosMapaCargadosIda;
	public DatosMapa datosMapaCargadosVuelta;

	public DatosMapa datosMapaCargadosIdaAux;
	public DatosMapa datosMapaCargadosVueltaAux;

	public TextView datosLinea;

	public int modoRed = InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE;

	public int drawableIda;
	public int drawableVuelta;
	public int drawableMedio;
	public int drawableVehiculo;

	public List<MarkerOptions> markersIda;
	public List<MarkerOptions> markersVuelta;
	public List<MarkerOptions> markersMedio;
	public List<MarkerOptions> markersVehiculos;

	GestionarLineas gestionarLineas;
	ParadasCercanas paradasCercanas;
	GestionVehiculos gestionVehiculos;

	SharedPreferences preferencias = null;

	boolean primeraCarga = true;

	public MapasOffline mapasOffline;

	public SelectorLinea selectorLinea;

	public Timer timer = null;

	ArrayList<BusLinea> lineasBus;

	public GoogleMap mMap;

	private TextView mTopText;

	public LocationClient mLocationClient;

	public String distancia = ParadasCercanas.DISTACIA_CERCANA;

	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000) // 5
																								// seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapas_maps2);

		setUpMapIfNeeded();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		gestionarLineas = new GestionarLineas(this, preferencias);
		mapasOffline = new MapasOffline(this, preferencias);
		paradasCercanas = new ParadasCercanas(this, preferencias);
		selectorLinea = new SelectorLinea(this, preferencias);
		gestionVehiculos = new GestionVehiculos(this, preferencias);

		gestionarLineas.inicializarMapa();

	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	private void setUpMap() {
		// Hide the zoom controls as the button panel will cover it.
		mMap.getUiSettings().setZoomControlsEnabled(true);

		// Set listeners for marker events. See the bottom of this class for
		// their behavior.
		mMap.setOnMarkerClickListener(this);

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.386058, -0.51001810), 10));

	}

	private boolean checkReady() {
		if (mMap == null) {
			// Toast.makeText(this, R.string.map_not_ready,
			// Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/** Called when the Clear button is clicked. */
	public void onClearMap(View view) {
		if (!checkReady()) {
			return;
		}
		mMap.clear();
	}

	/** Called when the Reset button is clicked. */
	public void onResetMap(View view) {
		if (!checkReady()) {
			return;
		}
		// Clear the map because we don't want duplicates of the markers.
		mMap.clear();
		// addMarkersToMap();
	}

	//
	// Marker related listeners.
	//

	public boolean onMarkerClick(final Marker marker) {
		/*
		 * if (marker.equals(mPerth)) { // This causes the marker at Perth to
		 * bounce into position when it is clicked. final Handler handler = new
		 * Handler(); final long start = SystemClock.uptimeMillis(); final long
		 * duration = 1500;
		 * 
		 * final Interpolator interpolator = new BounceInterpolator();
		 * 
		 * handler.post(new Runnable() {
		 * 
		 * public void run() { long elapsed = SystemClock.uptimeMillis() -
		 * start; float t = Math.max(1 - interpolator .getInterpolation((float)
		 * elapsed / duration), 0); marker.setAnchor(0.5f, 1.0f + 2 * t);
		 * 
		 * if (t > 0.0) { // Post again 16ms later. handler.postDelayed(this,
		 * 16); } } }); } else if (marker.equals(mAdelaide)) { // This causes
		 * the marker at Adelaide to change color.
		 * marker.setIcon(BitmapDescriptorFactory.defaultMarker(new
		 * Random().nextFloat() * 360)); } // We return false to indicate that
		 * we have not consumed the event and that we wish // for the default
		 * behavior to occur (which is for the camera to move such that the //
		 * marker is centered and for the marker's info window to open, if it
		 * has one).
		 */
		return false;
	}

	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(getBaseContext(), "Click Info Window", Toast.LENGTH_SHORT).show();
	}

	public void onMarkerDragStart(Marker marker) {
		mTopText.setText("onMarkerDragStart");
	}

	public void onMarkerDragEnd(Marker marker) {
		mTopText.setText("onMarkerDragEnd");
	}

	public void onMarkerDrag(Marker marker) {
		mTopText.setText("onMarkerDrag.  Current Position: " + marker.getPosition());
	}

	/**
	 * Control de tareas
	 */
	public void detenerTareas() {

		if (taskDatosMapa != null && taskDatosMapa.getStatus() == Status.RUNNING) {

			taskDatosMapa.cancel(true);

			Log.d("MAPAS", "Cancelada task datos mapa");

		}

		if (taskDatosMapaVuelta != null && taskDatosMapaVuelta.getStatus() == Status.RUNNING) {

			taskDatosMapaVuelta.cancel(true);

			Log.d("MAPAS", "Cancelada task datos mapa vuelta");

		}

		if (taskBuses != null && taskBuses.getStatus() == Status.RUNNING) {

			taskBuses.cancel(true);

			Log.d("MAPAS", "Cancelada task taskBuses");

		}

		if (taskVehiculosMapa != null && taskVehiculosMapa.getStatus() == Status.RUNNING) {

			taskVehiculosMapa.cancel(true);

			Log.d("MAPAS", "Cancelada task vehiculos");

		}

		if (timer != null) {

			timer.cancel();

		}
	}

	@Override
	protected void onDestroy() {

		detenerTareas();

		super.onDestroy();
	}

	@Override
	public void finish() {

		Intent intent = new Intent();
		setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent);
		super.finish();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();

		if (modoRed != InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
			menuInflater.inflate(R.menu.mapa, menu);
		} else {
			menuInflater.inflate(R.menu.mapa_tram, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*
		 * case R.id.menu_search: onSearchRequested(); break;
		 */
		case R.id.menu_satelite:

			if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			} else {

				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

			}

			break;
		case R.id.menu_ida:
			if (modoRed != InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
				gestionarLineas.cargarOcultarIda();
			}

			break;
		case R.id.menu_vuelta:
			if (modoRed != InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
				gestionarLineas.cargarOcultarVuelta();
			}
			break;
		case R.id.menu_cercanas:

			paradasCercanas.seleccionarProximidad();

			break;
		case R.id.menu_posicion:
			// miLocalizacion(false);
			break;
		case R.id.menu_search_online:

			selectorLinea.cargarDatosLineasModal();

			break;

		case R.id.menu_search:

			selectorLinea.cargarDatosLineasModal();

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

	/**
	 * Button to get current Location. This demonstrates how to get the current
	 * Location as required without needing to register a LocationListener.
	 */
	public void showMyLocation(View view) {
		if (mLocationClient != null && mLocationClient.isConnected()) {
			String msg = "Location = " + mLocationClient.getLastLocation();
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Implementation of {@link LocationListener}.
	 */

	public void onLocationChanged(Location location) {
		// mMessageView.setText("Location = " + location);
	}

	/**
	 * Callback called when connected to GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */

	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */

	public void onDisconnected() {
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */

	public void onConnectionFailed(ConnectionResult result) {
		// Do nothing
	}

	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default
		// behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}

}
