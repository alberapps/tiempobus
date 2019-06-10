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
package alberapps.android.tiempobus.rutas;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.tasks.LoadDirectionsAsyncTask;
import alberapps.android.tiempobus.tasks.LoadLocationAsyncTask;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.directions.Direction;
import alberapps.java.directions.Leg;
import alberapps.java.directions.Route;
import alberapps.java.directions.Step;
import alberapps.java.localizacion.Localizacion;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Actividad del calculador de rutas
 */
public class RutasActivity extends AppCompatActivity {


    private static int PLACE_AUTOCOMPLETE_REQUEST_ORIGEN_CODE = 1;
    private static int PLACE_AUTOCOMPLETE_REQUEST_DESTINO_CODE = 2;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public static final int REQUEST_CODE_LOCATION = 5;

    private ProgressDialog dialog;

    AsyncTask<Object, Void, Direction> taskDirection;

    SharedPreferences preferencias = null;

    protected StepAdapter stepAdapter;


    Direction directionDatos;

    protected RecyclerView mRecyclerView;
    protected RouteAdapter routeAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<Route> datosRutas;

    String origen = "";
    String destino = "";

    Integer posicionActual = null;

    private AppCompatActivity activity = this;

    private FusedLocationProviderClient mFusedLocationClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rutas_2);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();

                cargarDatosRutas();


            }
        });*/

        Button botonBuscar = (Button) findViewById(R.id.boton_buscar);

        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilidadesUI.ocultarTeclado(activity);
                cargarDatosRutas();
            }
        });


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        datosRutas = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(0);


        routeAdapter = new RouteAdapter(this, datosRutas);
        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(routeAdapter);


        // Fondo
        setupFondoAplicacion();


        //Datos anteriores
        String datosAnteriores = PreferencesUtil.getCache(this, "datos_form_rutas");

        if (datosAnteriores != null && !datosAnteriores.equals("")) {
            String[] datos = datosAnteriores.split(";;");

            origen = datos[0];
            destino = datos[1];

            TextView textoOrigen = (TextView) findViewById(R.id.ruta_origen);
            TextView textoDestino = (TextView) findViewById(R.id.ruta_destino);

            textoOrigen.setText(origen);
            textoDestino.setText(destino);

        }


        //Autocompletado con places
        ImageButton botonOrigen = (ImageButton) findViewById(R.id.boton_origen);
        botonOrigen.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                cargarAutocompletar(PLACE_AUTOCOMPLETE_REQUEST_ORIGEN_CODE);
            }
        });

        ImageButton botonDestino = (ImageButton) findViewById(R.id.boton_destino);
        botonDestino.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                cargarAutocompletar(PLACE_AUTOCOMPLETE_REQUEST_DESTINO_CODE);
            }
        });

        ImageButton botonPosicionActual = (ImageButton) findViewById(R.id.boton_posicion_actual);
        botonPosicionActual.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                obtenerPosicionActual();
            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }

    private void cargarAutocompletar(int sentido) {

        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, sentido);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();

        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e("RUTAS", message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_ORIGEN_CODE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("RUTAS", "Place Selected: " + place.getName());


                // Display attributions if required.
                /*CharSequence attributions = place.getAttributions();
                if (!TextUtils.isEmpty(attributions)) {
                    mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
                } else {
                    mPlaceAttribution.setText("");
                }*/

                origen = "";

                if (place.getName() != null && !place.getName().equals("")) {
                    origen = place.getName().toString();
                }

                if (place.getAddress() != null && !place.getAddress().equals("")) {

                    if (!origen.equals("")) {
                        origen = origen + ", ";
                    }

                    origen = origen + place.getAddress().toString();
                }


                TextView textoOrigen = (TextView) findViewById(R.id.ruta_origen);
                textoOrigen.setText(origen);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("RUTAS", "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }

        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_DESTINO_CODE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("RUTAS", "Place Selected: " + place.getName());


                destino = "";

                if (place.getName() != null && !place.getName().equals("")) {
                    destino = place.getName().toString();
                }

                if (place.getAddress() != null && !place.getAddress().equals("")) {

                    if (!destino.equals("")) {
                        destino = destino + ", ";
                    }

                    destino = destino + place.getAddress().toString();
                }


                TextView textoDestino = (TextView) findViewById(R.id.ruta_destino);
                textoDestino.setText(destino);


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("RUTAS", "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }

        }
    }

    /**
     * Cargar los datos de las rutas
     */
    private void cargarDatosRutas() {

        TextView textoOrigen = (TextView) findViewById(R.id.ruta_origen);
        TextView textoDestino = (TextView) findViewById(R.id.ruta_destino);

        origen = textoOrigen.getText().toString();
        destino = textoDestino.getText().toString();

        if (origen.trim().equals("") || destino.trim().equals("")) {

            Toast.makeText(getApplicationContext(), getString(R.string.error_ruta), Toast.LENGTH_LONG).show();

            return;

        }

        PreferencesUtil.putCache(this, "datos_form_rutas", origen.trim() + ";;" + destino.trim());


        mRecyclerView.setAdapter(routeAdapter);

        LoadDirectionsAsyncTask.LoadDirectionsAsyncTaskResponder loadDirectionsAsyncTaskResponder = new LoadDirectionsAsyncTask.LoadDirectionsAsyncTaskResponder() {
            @Override
            public void directionsLoaded(Direction direction) {

                if (direction != null && direction.getStatus().equals("OK")) {

                    directionDatos = direction;
                    cargarListadoRutas();

                } else {

                    Toast.makeText(getApplicationContext(), getString(R.string.error_ruta), Toast.LENGTH_LONG).show();

                }

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

            }

        };

        if (dialog == null) {
            dialog = ProgressDialog.show(this, "", getString(R.string.dialogo_espera), true);
        } else {
            dialog.show();
        }

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            taskDirection = new LoadDirectionsAsyncTask(loadDirectionsAsyncTaskResponder).execute(origen, destino, "", getApplicationContext());

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }


    /**
     * Cargar el listado de rutas
     */
    public void cargarListadoRutas() {

        routeAdapter.addAll(directionDatos.getRoutes());
        mRecyclerView.setAdapter(routeAdapter);

    }

    /**
     * Recargar datos al volver desde pasos
     */
    public void cargarListadoRutasVuelta() {

        mRecyclerView.setAdapter(routeAdapter);

    }


    /**
     * Cargar el listado de pasos de la ruta
     */
    public void cargarListadoPasosRuta(int position) {

        posicionActual = position - 1;

        List<Step> steps = new ArrayList<>(datosRutas.get(position - 1).getLegs().get(0).getSteps());

        if (stepAdapter == null) {
            stepAdapter = new StepAdapter(this, steps);
        } else {
            stepAdapter.addAll(steps);
        }


        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragMapa = fragmentManager.findFragmentById(R.id.mapRuta);
        if (fragMapa != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(fragMapa);
            ft.commit();
        }

        // Set CustomAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(stepAdapter);

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

    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = findViewById(R.id.contenedor_rutas);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

    }


    public String getPolyline() {

        return directionDatos.getRoutes().get(posicionActual).getPolyline();

    }

    public Leg getLegActual() {

        return directionDatos.getRoutes().get(posicionActual).getLegs().get(0);

    }


    private void obtenerPosicionActual() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);

            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            String lat = Double.toString(location.getLatitude());
                            String lon = Double.toString(location.getLongitude());

                            obtenerDireccionLocation(lat, lon);

                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_gps), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.error_gps), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                //Intent intent = getIntent();
                //finish();
                //startActivity(intent);

                obtenerPosicionActual();

            } else {
                // Permission was denied or request was cancelled
                //finish();
                Toast.makeText(getApplicationContext(), getString(R.string.error_gps), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void obtenerDireccionLocation(String lat, String lon) {

        LoadLocationAsyncTask.LoadLocationAsyncTaskResponder loadLocationAsyncTaskResponder = new LoadLocationAsyncTask.LoadLocationAsyncTaskResponder() {
            public void LocationLoaded(final Localizacion localizacion) {

                if (localizacion != null) {

                    StringBuilder sb = new StringBuilder(100);

                    sb.append(localizacion.getDireccion());
                    if (localizacion.getLocalidad() != null) {
                        sb.append(", ");
                        sb.append(localizacion.getLocalidad());
                    }

                    origen = sb.toString();

                    TextView textoOrigen = (TextView) findViewById(R.id.ruta_origen);
                    textoOrigen.setText(origen);


                } else {
                    origen = "";
                    Toast.makeText(getApplicationContext(), getString(R.string.error_ruta), Toast.LENGTH_LONG).show();
                }

            }

        };

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new LoadLocationAsyncTask(loadLocationAsyncTaskResponder).execute(lat, lon, this);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
        }


    }

}
