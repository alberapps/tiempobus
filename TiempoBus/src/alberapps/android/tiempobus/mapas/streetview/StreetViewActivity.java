/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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
package alberapps.android.tiempobus.mapas.streetview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import alberapps.android.tiempobus.R;

/**
 * Vista de streetview
 */
public class StreetViewActivity extends AppCompatActivity {

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


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

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

        //Cambio en mensaje
        if (datosMensaje != null && !datosMensaje.equals("")) {

            int index = datosMensaje.lastIndexOf("\n");

            //En caso de encontrarlo
            if(index > 0) {
                datosMensaje = datosMensaje.substring(0, index);
            }

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
                NavUtils.navigateUpFromSameTask(this);
                return true;
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
