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

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.infolineas.horariosTram.HorariosTramAdapter;
import alberapps.android.tiempobus.infolineas.sliding.SlidingTabsBasicFragment;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.horarios.DatosHorarios;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.PlaceMark;
import alberapps.java.tam.webservice.estructura.rutas.GetLineasResult;
import alberapps.java.tram.horarios.DatosConsultaHorariosTram;
import alberapps.java.tram.horarios.HorarioTram;

/**
 * Informacion de lineas con tabs
 */

public class InfoLineasTabsPager extends ActionBarActivity {

    public ViewPager mViewPager;


    BusLinea linea = null;

    String sentidoIda = "";
    String sentidoVuelta = "";

    GetLineasResult lineasMapas = null;

    DatosMapa datosIda = null;
    DatosMapa datosVuelta = null;

    DatosHorarios datosHorarios = null;

    String linkHorario;

    public ProgressDialog dialog = null;

    View vistaPieHorarioIda = null;
    View vistaPieHorarioVuelta = null;
    View vistaPieAvisoIda = null;
    View vistaPieAvisoVuelta = null;

    // Red a usar 0(subus online) 1(subus local) 2(tram)
    public static int MODO_RED_SUBUS_ONLINE = 0;
    public static int MODO_RED_SUBUS_OFFLINE = 1;
    public static int MODO_RED_TRAM_OFFLINE = 2;

    public int modoRed = MODO_RED_SUBUS_ONLINE;

    SharedPreferences preferencias = null;

    AsyncTask<Object, Void, DatosHorarios> taskHorarios = null;
    AsyncTask<String, Void, ArrayList<BusLinea>> taskBuses = null;
    AsyncTask<DatosInfoLinea, Void, DatosInfoLinea> taskDatosLinea = null;
    AsyncTask<DatosInfoLinea, Void, DatosInfoLinea> taskInfoLineaIda = null;

    public AsyncTask<Object, Void, HorarioTram> taskHorariosTram = null;


    InfoLineaParadasAdapter infoLineaParadasAdapter;


    public GestionTram gestionTram;
    public GestionIda gestionIda;
    public GestionVuelta gestionVuelta;
    public GestionHorariosIda gestionHorariosIda;
    public GestionHorariosVuelta gestionHorariosVuelta;

    public ArrayList<BusLinea> lineasBus;
    public InfoLineaAdapter infoLineaAdapter;
    public ListView lineasView;
    public ListView horariosTramView;
    public HorarioTram datosHorariosTram;
    public HorariosTramAdapter horariosTramAdapter;


    public DatosConsultaHorariosTram consultaHorarioTram;


    public BusLinea getLinea() {
        return linea;
    }

    public void setLinea(BusLinea linea) {
        this.linea = linea;
    }

    public String modoHorario = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        gestionTram = new GestionTram(this, preferencias);
        gestionIda = new GestionIda(this, preferencias);
        gestionVuelta = new GestionVuelta(this, preferencias);
        gestionHorariosIda = new GestionHorariosIda(this, preferencias);
        gestionHorariosVuelta = new GestionHorariosVuelta(this, preferencias);

        // Control de modo de red
        modoRed = this.getIntent().getIntExtra("MODO_RED", 0);

        if (this.getIntent().getExtras() == null || (this.getIntent().getExtras() != null && !this.getIntent().getExtras().containsKey("MODO_RED"))) {

            modoRed = preferencias.getInt("infolinea_modo", 0);

        }

        modoHorario = null;

        //Al entrar a horarios directamente
        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("HORARIOS")) {

            if((this.getIntent().getExtras().getString("HORARIOS")).equals("TRAM")) {

                modoRed = MODO_RED_TRAM_OFFLINE;
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putInt("infolinea_modo", MODO_RED_TRAM_OFFLINE);
                editor.commit();

                modoHorario = "TRAM";
                this.getIntent().removeExtra("HORARIOS");
            }

        }


        setContentView(R.layout.infolinea_contenedor);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);
        }


        if (!UtilidadesUI.pantallaTabletHorizontal(this)) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            transaction.replace(R.id.tabs_content_fragment, fragment);
            transaction.commit();


        } else {
            if (modoRed == MODO_RED_TRAM_OFFLINE) {

                FragmentManager fragmentManager = this.getSupportFragmentManager();

                Fragment fragVuelta = fragmentManager.findFragmentById(R.id.infolinea_3_fragment);

                FragmentTransaction ft = fragmentManager.beginTransaction();

                ft.hide(fragVuelta);

                ft.commit();

                Log.d("infolinea", "eliminar panel vuelta");

            }else{

                FragmentManager fragmentManager = this.getSupportFragmentManager();

                Fragment fragVuelta = fragmentManager.findFragmentById(R.id.infolinea_4_fragment);

                FragmentTransaction ft = fragmentManager.beginTransaction();

                ft.hide(fragVuelta);

                ft.commit();

                Log.d("infolinea", "eliminar panel horarios tram");


            }


        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        finish();
        startActivity(getIntent());

    }

    @Override
    protected void onDestroy() {

        detenerTareas();

        super.onDestroy();
    }

    /**
     * Detener todas las tareas
     */
    public void detenerTareas() {

        if (taskHorarios != null && taskHorarios.getStatus() == Status.RUNNING) {

            taskHorarios.cancel(true);

            Log.d("INFOLINEA", "Cancelada task horarios");

        }

        if (taskBuses != null && taskBuses.getStatus() == Status.RUNNING) {

            taskBuses.cancel(true);

            Log.d("INFOLINEA", "Cancelada task buses");

        }

        if (taskDatosLinea != null && taskDatosLinea.getStatus() == Status.RUNNING) {

            taskDatosLinea.cancel(true);

            Log.d("INFOLINEA", "Cancelada task datos linea");

        }

        if (taskInfoLineaIda != null && taskInfoLineaIda.getStatus() == Status.RUNNING) {

            taskInfoLineaIda.cancel(true);

            Log.d("INFOLINEA", "Cancelada task linea ida");

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*if (!UtilidadesUI.pantallaTabletHorizontal(this) && mTabHost != null) {
            outState.putString("tab", mTabHost.getCurrentTabTag());
        }*/
    }

    public void cambiarTab() {

        if (!UtilidadesUI.pantallaTabletHorizontal(this)) {
            mViewPager.setCurrentItem(1);
        } else {

            // Lanzar carga de vuelta
            FragmentVuelta vueltaFrag = (FragmentVuelta) getSupportFragmentManager().findFragmentById(R.id.infolinea_3_fragment);

            if (vueltaFrag != null) {

                gestionVuelta.recargaInformacion();

            }

        }


    }


    public void cargarTiempos(int codigo) {

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

    public void irInformacion(PlaceMark datosParada) {

        Intent i = new Intent(this, InfoLineasDatosParadaActivity.class);
        i.putExtra("DATOS_PARADA", datosParada);
        i.putExtra("DATOS_LINEA", linea);
        startActivity(i);

    }


    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = findViewById(R.id.contenedor_tabs);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.infolineas, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {



		/*case R.id.menu_search:

			onSearchRequested();

			break;
*/
        }

        return super.onOptionsItemSelected(item);

    }

    public int getModoRed() {
        return modoRed;
    }

    public void setModoRed(int modoRed) {
        this.modoRed = modoRed;
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
