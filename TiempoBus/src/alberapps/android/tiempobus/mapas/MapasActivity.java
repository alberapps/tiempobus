/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
 * <p/>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.mapas;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.mapas.DatosMapa;

/**
 * Mapas con info de paradas y posicion
 */
public class MapasActivity extends AppCompatActivity
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        OnMarkerClickListener,
        OnInfoWindowClickListener,
        OnMarkerDragListener,
        ResultCallback<LocationSettingsResult> {

    public GoogleApiClient mGoogleApiClient;

    public String lineaSeleccionada;
    public String lineaSeleccionadaDesc;
    public String lineaSeleccionadaNum;

    public String paradaSeleccionadaEntrada;

    public AsyncTask<String, Void, DatosMapa> taskDatosMapa = null;
    public AsyncTask<String, Void, DatosMapa> taskDatosMapaVuelta = null;
    public AsyncTask<String, Void, ArrayList<BusLinea>> taskBuses = null;
    public AsyncTask<Object, Void, DatosMapa> taskVehiculosMapa = null;
    public AsyncTask<Object, Void, DatosMapa[]> taskDatosMapaV3 = null;

    public ProgressDialog dialog;

    public DatosMapa datosMapaCargadosIda;
    public DatosMapa datosMapaCargadosVuelta;

    public DatosMapa datosMapaCargadosIdaAux;
    public DatosMapa datosMapaCargadosVueltaAux;

    public TextView datosLinea;

    public int modoRed = InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE;

    public BitmapDescriptor drawableIda;
    public BitmapDescriptor drawableVuelta;
    public BitmapDescriptor drawableMedio;
    public BitmapDescriptor drawableVehiculo;

    public List<MarkerOptions> markersIda;
    public List<MarkerOptions> markersVuelta;
    public List<MarkerOptions> markersMedio;
    public List<MarkerOptions> markersVehiculos;

    GestionarLineas gestionarLineas;
    ParadasCercanas paradasCercanas;
    GestionVehiculos gestionVehiculos;
    GestionMapa gestionMapa;

    SharedPreferences preferencias = null;

    boolean primeraCarga = true;

    public MapasOffline mapasOffline;
    public SelectorLinea selectorLinea;

    public Timer timer = null;

    ArrayList<BusLinea> lineasBus;

    public GoogleMap mMap;

    private TextView mTopText;

    public String distancia = ParadasCercanas.DISTACIA_CERCANA;

    String paradaSeleccionada;

    public boolean flagIda = true;
    public boolean flagVuelta = true;

    public boolean conectadoLocation = false;


    protected LocationSettingsRequest mLocationSettingsRequest;
    protected LocationRequest mLocationRequest;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private static final int REQUEST_CODE_LOCATION = 2;


    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapas_maps2);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

        }


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        gestionarLineas = new GestionarLineas(this, preferencias);
        mapasOffline = new MapasOffline(this, preferencias);
        paradasCercanas = new ParadasCercanas(this, preferencias);
        selectorLinea = new SelectorLinea(this, preferencias);
        gestionVehiculos = new GestionVehiculos(this, preferencias);
        gestionMapa = new GestionMapa(this, preferencias);


        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        setUpMap();
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);

        buildLocationSettingsRequest();

        gestionarLineas.inicializarMapa();

    }


    private void setUpMap() {
        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Setting an info window adapter allows us to change the both the
        // contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events. See the bottom of this class for
        // their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.386058, -0.51001810), 15));

    }

    /**
     * Button to get current Location. This demonstrates how to get the current Location as required
     * without needing to register a LocationListener.
     */
    public void showMyLocation(View view) {
        if (mGoogleApiClient.isConnected()) {
            String msg = "Location = "
                    + LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        //mMessageView.setText("Location = " + location);
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.d("mapas", "location conectado");

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);  // LocationListener

    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        conectadoLocation = false;
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {

        conectadoLocation = false;

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }


    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends AppCompatDialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MapasActivity) getActivity()).onDialogDismissed();
        }
    }


    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).

        //String msg = "Location = "
        //      + LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        return false;
    }


    /**
     * Demonstrates customizing the info window and/or its contents.
     */
    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        // private final RadioGroup mOptions;

        // These a both viewgroups containing an ImageView with id "badge" and
        // two TextViews with id
        // "title" and "snippet".
        // private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            // mWindow =
            // getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
            // mOptions = (RadioGroup)
            // findViewById(R.id.custom_info_window_options);
        }

        public View getInfoWindow(Marker marker) {
            // if (mOptions.getCheckedRadioButtonId() !=
            // R.id.custom_info_window) {
            // This means that getInfoContents will be called.
            // return null;
            // }
            // render(marker, mWindow);
            // return mWindow;

            return null;
        }

        public View getInfoContents(Marker marker) {
            // if (mOptions.getCheckedRadioButtonId() !=
            // R.id.custom_info_contents) {
            // This means that the default info contents will be used.
            // return null;
            // }
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge;
            // Use the equals() method on a Marker to check for equals. Do not
            // use ==.

            int codigo;

            if (marker.getSnippet() != null && !marker.getSnippet().equals("")) {

                try {
                    codigo = Integer.parseInt(marker.getTitle().substring(1, 5));
                } catch (Exception e) {

                    // Por si es tram
                    int c1 = marker.getTitle().indexOf("[");
                    int c2 = marker.getTitle().indexOf("]");

                    codigo = Integer.parseInt(marker.getTitle().substring(c1 + 1, c2));

                }

                if (DatosPantallaPrincipal.esTram(Integer.toString(codigo))) {
                    //badge = R.drawable.tramway_2;
                    badge = R.mipmap.ic_tram1;
                } else {
                    //badge = R.drawable.bus;
                    badge = R.mipmap.ic_bus_blue1;
                }

            } else {
                //badge = R.drawable.bus;
                badge = R.mipmap.ic_bus_blue1;
            }

            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the
                // text.
                SpannableString titleText = new SpannableString(title);
                // titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0,
                // titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                // snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA),
                // 0, 10, 0);
                // snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12,
                // snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }


    //
    // Marker related listeners.
    //

    public boolean onMarkerClick(final Marker marker) {

        return false;
    }

    public void onInfoWindowClick(Marker marker) {

        if (marker.getSnippet() != null && !marker.getSnippet().trim().equals("")) {
            gestionMapa.seleccionInfoParada(marker);
        }

    }

    public void onMarkerDragStart(Marker marker) {

    }

    public void onMarkerDragEnd(Marker marker) {

    }

    public void onMarkerDrag(Marker marker) {

    }


    /**
     * Control de tareas
     */
    public void detenerTareas() {

        if (taskDatosMapa != null && taskDatosMapa.getStatus() == AsyncTask.Status.RUNNING) {

            taskDatosMapa.cancel(true);

            Log.d("MAPAS", "Cancelada task datos mapa");

        }

        if (taskDatosMapaVuelta != null && taskDatosMapaVuelta.getStatus() == AsyncTask.Status.RUNNING) {

            taskDatosMapaVuelta.cancel(true);

            Log.d("MAPAS", "Cancelada task datos mapa vuelta");

        }

        if (taskBuses != null && taskBuses.getStatus() == AsyncTask.Status.RUNNING) {

            taskBuses.cancel(true);

            Log.d("MAPAS", "Cancelada task taskBuses");

        }

        if (taskVehiculosMapa != null && taskVehiculosMapa.getStatus() == AsyncTask.Status.RUNNING) {

            taskVehiculosMapa.cancel(true);

            Log.d("MAPAS", "Cancelada task vehiculos");

        }

        if (timer != null) {

            timer.cancel();

        }
    }


    public String getParadaSeleccionada() {
        return paradaSeleccionada;
    }

    public void setParadaSeleccionada(String paradaSeleccionada) {
        this.paradaSeleccionada = paradaSeleccionada;
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

            case R.id.menu_satelite:

                if (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else {

                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                }

                break;

            case R.id.menu_search:

                selectorLinea.cargarDatosLineasModal();

                break;

            case R.id.menu_cercanas_img:

                checkLocationSettings();

                //detenerTareas();

                //paradasCercanas.seleccionarProximidad();

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
    protected void onDestroy() {

        detenerTareas();

        super.onDestroy();
    }


    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        mLocationRequest = REQUEST;

        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public void checkLocationSettings() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        } else {
            // Location permission has been granted, continue as usual.

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest);

            result.setResultCallback(this);
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // success!
                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, mLocationSettingsRequest);
                result.setResultCallback(this);
            } else {
                // Permission was denied or request was cancelled
                conectadoLocation = false;
            }
        }
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {

        final Status status = locationSettingsResult.getStatus();

        switch (status.getStatusCode()) {

            case LocationSettingsStatusCodes.SUCCESS:
                conectadoLocation = true;

                detenerTareas();
                paradasCercanas.seleccionarProximidad();


                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(MapasActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    conectadoLocation = false;
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                conectadoLocation = false;
                break;

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        conectadoLocation = true;

                        detenerTareas();
                        paradasCercanas.seleccionarProximidad();

                        break;
                    case Activity.RESULT_CANCELED:
                        conectadoLocation = false;

                        Toast.makeText(this, getString(R.string.error_gps), Toast.LENGTH_SHORT).show();

                        break;


                }
                break;
            case REQUEST_RESOLVE_ERROR:
                mResolvingError = false;
                if (resultCode == RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connect
                    if (!mGoogleApiClient.isConnecting() &&
                            !mGoogleApiClient.isConnected()) {
                        mGoogleApiClient.connect();
                    }
                }
                break;

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }
}
