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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.tasks.LoadDirectionsAsyncTask;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.directions.Direction;
import alberapps.java.directions.Leg;
import alberapps.java.directions.Route;
import alberapps.java.directions.Step;

/**
 * Actividad del calculador de rutas
 */
public class RutasActivity extends AppCompatActivity {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rutas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();

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

        datosRutas = new ArrayList<Route>();

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

            taskDirection = new LoadDirectionsAsyncTask(loadDirectionsAsyncTaskResponder).execute(origen, destino, "");

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

        List<Step> steps = new ArrayList<Step>(datosRutas.get(position - 1).getLegs().get(0).getSteps());

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

        View contenedor_principal = findViewById(R.id.recyclerView);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

    }


    public String getPolyline() {

        return directionDatos.getRoutes().get(posicionActual).getPolyline();

    }

    public Leg getLegActual() {

        return directionDatos.getRoutes().get(posicionActual).getLegs().get(0);

    }


}
