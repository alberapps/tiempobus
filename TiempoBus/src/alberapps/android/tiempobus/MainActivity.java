/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.android.tiempobus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import alberapps.android.tiempobus.ApplicationTiempoBus.TrackerName;
import alberapps.android.tiempobus.alarma.GestionarAlarmas;
import alberapps.android.tiempobus.barcode.IntentIntegrator;
import alberapps.android.tiempobus.barcode.IntentResult;
import alberapps.android.tiempobus.barcode.Utilidades;
import alberapps.android.tiempobus.favoritos.FavoritoNuevoActivity;
import alberapps.android.tiempobus.favoritos.FavoritosActivity;
import alberapps.android.tiempobus.historial.HistorialActivity;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.mapas.MapasActivity;
import alberapps.android.tiempobus.noticias.NoticiasTabsPager;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.principal.FragmentSecundarioTablet;
import alberapps.android.tiempobus.principal.GestionarFondo;
import alberapps.android.tiempobus.principal.GestionarTarjetaInfo;
import alberapps.android.tiempobus.principal.GestionarVoz;
import alberapps.android.tiempobus.principal.GestionarWidget;
import alberapps.android.tiempobus.principal.TiemposAdapter;
import alberapps.android.tiempobus.principal.horariotram.PrincipalHorarioTramFragment;
import alberapps.android.tiempobus.rutas.RutasActivity;
import alberapps.android.tiempobus.service.TiemposForegroundService;
import alberapps.android.tiempobus.tasks.LoadTiemposAsyncTask;
import alberapps.android.tiempobus.tasks.LoadTiemposAsyncTask.LoadTiemposAsyncTaskResponder;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.exception.TiempoBusException;
import alberapps.java.noticias.Noticias;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.DatosRespuesta;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.tram.avisos.AvisosTram;
import alberapps.java.util.Conectividad;

/**
 * Actividad principal de la aplicacion
 */
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, FragmentSecundarioTablet.OnHeadlineSelectedListener, SwipeRefreshLayout.OnRefreshListener, PrincipalHorarioTramFragment.OnFragmentInteractionListener {

    public static final int SUB_ACTIVITY_REQUEST_POSTE = 1000;
    public static final int SUB_ACTIVITY_REQUEST_ADDFAV = 1001;
    public static final int SUB_ACTIVITY_RESULT_OK = 1002;
    public static final int SUB_ACTIVITY_RESULT_CANCEL = 1003;
    protected static final int SUB_ACTIVITY_REQUEST_PREFERENCIAS = 1004;
    protected static final int SUB_ACTIVITY_REQUEST_NOTICIAS = 1005;

    protected static final int VOICE_CHECK_CODE = 3000;

    public static final int CARGAR_IMAGEN = 2000;

    protected static final int DIALOG_CARGANDO = 100;

    protected static final int MSG_CLOSE_CARGANDO = 200;
    protected static final int MSG_ERROR_TIEMPOS = 201;
    public static final int MSG_FRECUENCIAS_ACTUALIZADAS = 202;
    public static final int MSG_RECARGA = 203;
    public static final long DELAY_RECARGA = 750;

    public ArrayList<BusLlegada> buses = new ArrayList<>();
    private TiemposAdapter tiemposAdapter;
    private TextView guiHora;
    private TextView datosParada;

    Calendar ahora = new GregorianCalendar();
    public int paradaActual = 4450;
    public final ParadaActualHandler handler = new ParadaActualHandler(this);

    public TiemposUpdater tiemposUpdater = new TiemposUpdater();
    AlarmManager alarmManager;
    private ImageButton botonCargaTiempos;

    public String latitudInfo = null;
    public String longitudInfo = null;

    public BusLlegada busSeleccionado = null;

    SharedPreferences preferencias = null;

    private TextToSpeech mTts;

    public boolean lecturaOK = true;
    public boolean lecturaAlternativa = false;

    public DatosPantallaPrincipal datosPantallaPrincipal;

    public GestionarFondo gestionarFondo;

    public GestionarAlarmas gestionarAlarmas;

    GestionarWidget gestionarWidget;

    GestionarVoz gestionarVoz;

    public GestionarTarjetaInfo gestionarTarjetaInfo;

    public ListView tiemposView;

    // drawer
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerView;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mDrawerTitles;
    private String[] mDrawerIcons;

    AsyncTask<Object, Void, DatosRespuesta> loadTiemposTask = null;
    public AsyncTask<Object, Void, List<Noticias>> nuevasNoticiasTask;
    public AsyncTask<Object, Void, AvisosTram> nuevasNoticasTramTask;

    public View avisoPie = null;

    public View avisoTarjetaInfo = null;

    SwipeRefreshLayout swipeRefresh = null;

    MenuItem refresh = null;

    public static final int REQUEST_CODE_STORAGE = 4;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pantalla_principal);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        Conectividad.activarCache(this, preferencias);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarid);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        // Delegate gestion
        datosPantallaPrincipal = new DatosPantallaPrincipal(this, preferencias);
        gestionarFondo = new GestionarFondo(this, preferencias);
        gestionarWidget = new GestionarWidget(this, preferencias);
        gestionarVoz = new GestionarVoz(this, preferencias);
        gestionarTarjetaInfo = new GestionarTarjetaInfo(this, preferencias);

        cambiarLocale(false);

        iniciarDrawer(savedInstanceState);


        // Verificar si hay parada por defecto
        if (preferencias.contains("parada_inicio")) {
            paradaActual = preferencias.getInt("parada_inicio", paradaActual);
        }

        if (savedInstanceState != null) {
            paradaActual = savedInstanceState.getInt("poste");

            SharedPreferences.Editor editor = preferencias.edit();
            editor.putInt("parada_inicio", paradaActual);
            editor.commit();
        }

        setupView();


        mTts = new TextToSpeech(this, this // TextToSpeech.OnInitListener
        );

        // Avisos
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Gestionar alarmas
        gestionarAlarmas = new GestionarAlarmas(this, preferencias, alarmManager);


        //PrecargasV3.precargarDatosLineas(this);
        //PrecargasV3.precargarDatosLineasRecorrido(this);


    }


    /**
     * Drawer Layout
     *
     * @param savedInstanceState
     */
    @SuppressLint("NewApi")
    private void iniciarDrawer(Bundle savedInstanceState) {
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerView = (NavigationView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                selectItem(menuItem);

                return false;
            }
        });

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */

                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            // selectItem(0);
        }

        ImageView imgCabecera = (ImageView) mDrawerLayout.findViewById(R.id.imgAlberapps);

        if (imgCabecera != null) {
            imgCabecera.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    UtilidadesUI.openWebPage(MainActivity.this, "http://alberapps.blogspot.com.es");

                }
            });
        }

    }


    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

        // If the nav drawer is open, hide action items related to the
        // content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerView);
        menu.findItem(R.id.menu_search).setVisible(!drawerOpen);
        //menu.findItem(R.id.menu_refresh).setVisible(!drawerOpen);
        //}

        return super.onPrepareOptionsMenu(menu);

    }

    /**
     * Seleccion del menu lateral
     *
     * @param item
     */
    private void selectItem(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.navigation_item_mapa:

                if (datosPantallaPrincipal.servicesConnected()) {
                    detenerTodasTareas();
                    startActivityForResult(new Intent(MainActivity.this, MapasActivity.class), SUB_ACTIVITY_REQUEST_POSTE);
                }

                break;

            case R.id.navigation_item_noticias:

                item.setChecked(false);

                detenerTodasTareas();
                startActivity(new Intent(MainActivity.this, NoticiasTabsPager.class));
                break;

            case R.id.navigation_item_favoritos:
                detenerTodasTareas();
                startActivityForResult(new Intent(MainActivity.this, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_POSTE);
                break;

            case R.id.navigation_item_guardar:
                detenerTodasTareas();
                nuevoFavorito();
                break;

            case R.id.navigation_item_historial:
                detenerTodasTareas();
                startActivityForResult(new Intent(MainActivity.this, HistorialActivity.class), SUB_ACTIVITY_REQUEST_POSTE);
                break;

            case R.id.navigation_item_preferencias:

                detenerTodasTareas();
                startActivityForResult(new Intent(MainActivity.this, PreferencesFromXml.class), SUB_ACTIVITY_REQUEST_PREFERENCIAS);
                break;

            case R.id.navigation_item_fondo:

                gestionarFondo.seleccionarFondo();

                break;


            case R.id.navigation_item_exportar:
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);

                // configurar
                integrator.setTitleByID(R.string.barcode_titulo);
                integrator.setMessageByID(R.string.barcode_mensaje);
                integrator.setButtonYesByID(R.string.barcode_si);
                integrator.setButtonNoByID(R.string.barcode_no);

                String paradaCodificada = Utilidades.codificarCodigoParada(Integer.toString(paradaActual));

                integrator.shareText(paradaCodificada);

                break;
            case R.id.navigation_item_rutas:


                detenerTodasTareas();
                startActivity(new Intent(MainActivity.this, RutasActivity.class));


                break;
        }


        item.setChecked(false);

        //mDrawerList.setItemChecked(position, false);
        mDrawerLayout.closeDrawer(mDrawerView);

    }

    /**
     * Cambio de idioma
     *
     * @param cambio
     */
    private void cambiarLocale(boolean cambio) {

        String nuevoLocale = preferencias.getString("idioma_seleccionado", "no");

        // Solo aplica si no esta seleccionado el del sistema
        if (!nuevoLocale.equals("no")) {

            Locale locale = new Locale(nuevoLocale);

            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;

            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        }

        // Reiniciar actividad
        if (cambio) {

            // Restablecer el locale del sistema
            if (nuevoLocale.equals("no")) {

                String localeDefault = Resources.getSystem().getConfiguration().locale.getLanguage();

                Locale locale = new Locale(localeDefault);

                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;

                getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            }

            // Reiniciar la actividad para mostrar el nuevo locale
            Intent intent = getIntent();
            finish();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

    }

	/*
     * @Override public View onCreateView(String name, Context context,
	 * AttributeSet attrs) { // TODO Auto-generated method stub
	 * 
	 * final String path = "principal";//getString(R.string.pr);
	 * 
	 * Tracker t = ((ApplicationTiempoBus) this.getApplication()).getTracker(
	 * TrackerName.GLOBAL_TRACKER); t.setScreenName(path); t.send(new
	 * HitBuilders.AppViewBuilder().build());
	 * 
	 * 
	 * return super.onCreateView(name, context, attrs); }
	 */

    private boolean controlInicialAnalytics = false;

    /**
     * Una vez este creada la actividad obtenemos el servicio para fijar las
     * alarmas
     */

    @Override
    protected void onStart() {
        super.onStart();

        // Verificar si hay parada por defecto
        if (preferencias.contains("parada_inicio")) {
            paradaActual = preferencias.getInt("parada_inicio", paradaActual);
        }

        Log.d("PRINCIPAL", "inicia: " + buses.size());

        handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

        // Poner en campo de poste
        EditText txtPoste = (EditText) findViewById(R.id.campo_poste);
        txtPoste.setText(Integer.toString(paradaActual));


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {

            datosPantallaPrincipal.controlMostrarAnalytics();

            if (preferencias.getBoolean("analytics_on", false)) {
                controlInicialAnalytics = true;

                // Activar
                GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(false);

                // Inicia tracker
                Tracker t = ((ApplicationTiempoBus) this.getApplication()).getTracker(TrackerName.APP_TRACKER);

                // Envia inicio actividad
                GoogleAnalytics.getInstance(this).reportActivityStart(this);

                Log.d("PRINCIPAL", "Analytics activo");

            } else {
                GoogleAnalytics.getInstance(getApplicationContext()).setAppOptOut(true);

                Log.d("PRINCIPAL", "Analytics inactivo");

            }

        } else {

            //Ya no funciona en Froyo

            SharedPreferences.Editor editor = preferencias.edit();
            editor.putBoolean("analytics_on", false);
            editor.commit();
        }

    }

    @Override
    protected void onStop() {

        Conectividad.flushCache();

        handler.removeMessages(MSG_RECARGA);

        if (preferencias.getBoolean("analytics_on", true) || controlInicialAnalytics) {

            GoogleAnalytics.getInstance(this).reportActivityStop(this);

        }

        super.onStop();

    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (mTts != null) {

            try {

                mTts.stop();
                mTts.shutdown();

            } catch (Exception e) {

                Toast.makeText(this, getString(R.string.error_voz), Toast.LENGTH_SHORT).show();

                e.printStackTrace();

            }

        }

        detenerTodasTareas();

        super.onDestroy();
    }

    /**
     * Detener tareas asincronas
     */
    public void detenerTodasTareas() {

        handler.removeMessages(MSG_RECARGA);

        detenerTareaTiempos();

        gestionarTarjetaInfo.detenerTareas();

    }

    /**
     * Detener tarea carga tiempos
     */
    public void detenerTareaTiempos() {

        if (loadTiemposTask != null && loadTiemposTask.getStatus() == Status.RUNNING) {

            loadTiemposTask.cancel(true);

            Log.d("tiempos", "Cancelada task tiempos");

        }

        if (nuevasNoticiasTask != null && nuevasNoticiasTask.getStatus() == Status.RUNNING) {

            nuevasNoticiasTask.cancel(true);

            Log.d("tiempos", "Cancelada task nuevas noticias");

        }

        if (nuevasNoticasTramTask != null && nuevasNoticasTramTask.getStatus() == Status.RUNNING) {

            nuevasNoticasTramTask.cancel(true);

            Log.d("tiempos", "Cancelada task nuevas noticias tram");

        }

    }

    @Override
    public void finish() {

        // Guardar ultima parada seleccionada
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("parada_inicio", paradaActual);
        editor.commit();

        editor.remove("parada_tram");
        editor.commit();

        handler.removeMessages(MSG_RECARGA);

        super.finish();

    }


    @Override
    protected void onPause() {

        // Guardar ultima parada seleccionada
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("parada_inicio", paradaActual);
        editor.commit();

        editor.remove("parada_tram");
        editor.commit();

        handler.removeMessages(MSG_RECARGA);

        detenerTodasTareas();

        super.onPause();
    }

    /**
     * Despues de crear la actividad
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Una vez cargado todo... recargamos datos
        handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

        datosPantallaPrincipal.controlMostrarNovedades();

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        mDrawerToggle.syncState();
        //}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        //refresh = menu.findItem(R.id.menu_refresh);

        // Calling super after populating the menu is necessary here to ensure
        // that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
        //}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        switch (item.getItemId()) {

            case R.id.menu_search:

                detenerTodasTareas();
                startActivityForResult(new Intent(MainActivity.this, InfoLineasTabsPager.class), SUB_ACTIVITY_REQUEST_POSTE);

                break;


        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Configura los elementos de la GUI
     */
    private void setupView() {


        // Fondo
        gestionarFondo.setupFondoAplicacion();

        /**
         * Configuramos la lista de resultados
         */
        tiemposAdapter = new TiemposAdapter(this, R.layout.tiempos_item);

        // registerForContextMenu(getListView());

        // Pie para la lista de resultados
        datosPantallaPrincipal.cargarHeader();


        guiHora = (TextView) findViewById(R.id.ultima_act);

        datosParada = (TextView) findViewById(R.id.datos_parada);

        // Progreso inicial
        /*
         * TextView vacio = (TextView) findViewById(R.id.tiempos_vacio);
		 * vacio.setVisibility(View.INVISIBLE); ProgressBar lpb = (ProgressBar)
		 * findViewById(R.id.tiempos_progreso); lpb.setIndeterminate(true);
		 * tiemposView.setEmptyView(lpb);
		 */
        // Control de sin datos
        BusLlegada sinDatos = new BusLlegada();
        sinDatos.setSinDatos(true);
        sinDatos.setConsultaInicial(true);
        tiemposAdapter.add(sinDatos);

        datosPantallaPrincipal.cargarPie();

        // Al pulsar sobre un item abriremos el dialogo de poner alarma
        tiemposView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
                BusLlegada bus = (BusLlegada) view.getItemAtPosition(position);

                if (bus != null && !bus.isSinDatos()) {
                    // setAlarm(bus);
                    busSeleccionado = bus;
                    // openContextMenu(getListView());

                    if (!busSeleccionado.isErrorServicio() && !busSeleccionado.isSinDatos()) {
                        opcionesLineaSeleccionada();
                    }

                }
            }
        });

        /*tiemposView.setOnLongClickListener(new OnLongClickListener() {

            public boolean onLongClick(View view) {
                // TODO Auto-generated method stub
                return false;
            }
        });*/

        // Asignamos el adapter a la lista
        tiemposView.setAdapter(tiemposAdapter);
        tiemposAdapter.notifyDataSetChanged();

        // registerForContextMenu(guiTitulo);

        /**
         * Definimos el comportamiento de los botones
         */

        // boton poste
        botonCargaTiempos = (ImageButton) findViewById(R.id.boton_subposte);
        botonCargaTiempos.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                accionCargar();

            }
        });


        // //Barcode

        Button botonBarcode = (Button) findViewById(R.id.boton_barcode);
        botonBarcode.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);

                // configurar
                integrator.setTitleByID(R.string.barcode_titulo);
                integrator.setMessageByID(R.string.barcode_mensaje);
                integrator.setButtonYesByID(R.string.barcode_si);
                integrator.setButtonNoByID(R.string.barcode_no);

                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);

            }
        });

        // //Alertas

        ImageButton botonAlerta = (ImageButton) findViewById(R.id.boton_alertas);
        botonAlerta.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                gestionarAlarmas.mostrarModalAlertas(paradaActual);

            }
        });

        // //Info

        ImageButton botonInfo = (ImageButton) findViewById(R.id.boton_info);
        botonInfo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                // datosPantallaPrincipal.cargarModalInfo(paradaActual);

                boolean resultado = gestionarVoz.reconocerVoz();

                if (!resultado) {
                    Toast.makeText(getApplicationContext(), getString(R.string.reconocimiento_voz_no), Toast.LENGTH_SHORT).show();
                }

            }
        });


        // Swipe para recargar
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshPrincipal);
        swipeRefresh.setColorSchemeResources(R.color.mi_material_blue_principal, R.color.tram_l2, R.color.mi_material_blue_principal, R.color.tram_l2);
        swipeRefresh.setOnRefreshListener(this);


        //Control desde teclado
        EditText txtPoste = (EditText) findViewById(R.id.campo_poste);
        txtPoste.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    accionCargar();
                    return false;
                }

                return false;
            }
        });


    }

    /**
     * Cargar al pulsar boton o otra accion
     */
    private void accionCargar() {

        EditText txtPoste = (EditText) findViewById(R.id.campo_poste);

        try {
            int tmpPoste = Integer.parseInt(txtPoste.getText().toString());
            if (tmpPoste > 0 && tmpPoste < 9999) {
                paradaActual = tmpPoste;

                SharedPreferences.Editor editor = preferencias.edit();
                editor.putInt("parada_inicio", paradaActual);
                editor.commit();

                handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

            }
        } catch (NumberFormatException e) {
            // Si no ha metido un numero correcto no hacemos nada
        }

    }

    /**
     * Pulsar recarga de tiempos
     */
    private void recargarTiempos() {

        EditText txtPoste = (EditText) findViewById(R.id.campo_poste);

        try {
            int tmpPoste = Integer.parseInt(txtPoste.getText().toString());
            if (tmpPoste > 0 && tmpPoste < 9999) {
                paradaActual = tmpPoste;
                handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

            }
        } catch (NumberFormatException e) {
            // No hay numero. Recargar con el ultimo usado
            handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);
        }

    }

    /**
     * Menu de seleccion de linea
     */
    private void opcionesLineaSeleccionada() {

        List<CharSequence> itemsL = new ArrayList<>();
        itemsL.add(getString(R.string.menu_alarma));
        itemsL.add(getString(R.string.menu_share));
        itemsL.add(getString(R.string.menu_ver_en_mapa));
        itemsL.add(getString(R.string.menu_leer));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            itemsL.add(getString(R.string.menu_widget));

        }

        final CharSequence[] items = itemsL.toArray(new CharSequence[itemsL.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.menu_contextual);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:

                        // Texto para receiver
                        String textoReceiver = gestionarAlarmas.prepararReceiver(busSeleccionado, paradaActual);

                        // Activar alarma y mostrar modal
                        gestionarAlarmas.mostrarModalTiemposAlerta(busSeleccionado, paradaActual, textoReceiver);
                        busSeleccionado = null;
                        break;

                    case 1:
                        if (busSeleccionado != null) {
                            datosPantallaPrincipal.shareBus(busSeleccionado, paradaActual);
                            busSeleccionado = null;
                        }
                        break;

                    case 2:

                        if (datosPantallaPrincipal.servicesConnected()) {

                            if (busSeleccionado != null && busSeleccionado.getLinea() != null && !busSeleccionado.getLinea().equals("")) {
                                Intent i = new Intent(MainActivity.this, MapasActivity.class);
                                i.putExtra("LINEA_MAPA", busSeleccionado.getLinea());
                                i.putExtra("LINEA_MAPA_PARADA", Integer.toString(paradaActual));
                                startActivityForResult(i, SUB_ACTIVITY_REQUEST_POSTE);
                            }

                            busSeleccionado = null;

                        }

                        break;

                    case 3:
                        datosPantallaPrincipal.cantarLinea(busSeleccionado);
                        busSeleccionado = null;
                        break;

                    case 4:
                        gestionarWidget.enviarAWidget(busSeleccionado, paradaActual);
                        busSeleccionado = null;
                        break;
                }


            }
        });

        AlertDialog alert = builder.create();

        alert.show();
    }

    public void alertaServiceClick(View view) {

        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch (view.getId()) {
            case R.id.checkBoxAlerta:
                if (checked) {

                    Intent intent = new Intent(TiemposForegroundService.ACTION_FOREGROUND);
                    intent.setClass(MainActivity.this, TiemposForegroundService.class);
                    intent.putExtra("PARADA", paradaActual);

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("activarServicio", true);
                    editor.commit();

                    startService(intent);

                } else {

                    Intent intent = new Intent(TiemposForegroundService.ACTION_FOREGROUND);
                    intent.setClass(MainActivity.this, TiemposForegroundService.class);

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("activarServicio", false);
                    editor.commit();

                    stopService(intent);

                }

                break;

        }

    }

    /**
     * Guardamos el poste en el estado para cuando el proceso se mate y reinicie
     *
     * @param savedInstanceState
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("poste", paradaActual);

    }

    /**
     * Recuperamos el estado previo si ha sido guardado
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            paradaActual = savedInstanceState.getInt("poste");

            gestionarTarjetaInfo.reconstruirFragment();
        }
    }

    /**
     * Encargada de capturar los datos que nos devuelven las subactividades
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == SUB_ACTIVITY_RESULT_OK) {
            switch (requestCode) {
                case SUB_ACTIVITY_REQUEST_POSTE:

                    if (data.getExtras() != null) {
                        Bundle b = data.getExtras();
                        if (b.containsKey("POSTE")) {

                            if (b.getInt("POSTE") == 0) {

                                // //Horarios tram
                                detenerTodasTareas();

                                Intent i = new Intent(this, InfoLineasTabsPager.class);
                                i.putExtra("HORARIOS", "TRAM");
                                i.putExtra("HORARIOSDATA", b.getString("HORARIOS"));

                                startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_POSTE);

                                break;


                            }

                            paradaActual = b.getInt("POSTE");
                        }
                    }

                    // Poner en campo de poste
                    EditText txtPoste = (EditText) findViewById(R.id.campo_poste);
                    txtPoste.setText(Integer.toString(paradaActual));

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putInt("parada_inicio", paradaActual);
                    editor.commit();

                    handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);
                    break;
                case SUB_ACTIVITY_REQUEST_ADDFAV:
                    startActivityForResult(new Intent(MainActivity.this, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_POSTE);
                    break;

                case SUB_ACTIVITY_REQUEST_PREFERENCIAS:
                    // Si no hay atuomatico, se cancela el mensaje
                    boolean auto = preferencias.getBoolean("checkbox_preference", true);

                    if (!auto) {
                        handler.removeMessages(MSG_RECARGA);
                    } else {
                        handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);
                    }

                    // Para ver si se ha cambiado
                    gestionarFondo.setupFondoAplicacion();

                    // Por si hay cambio de locale
                    cambiarLocale(true);

                    break;

                case SUB_ACTIVITY_REQUEST_NOTICIAS:

                    break;

            }

        } else {
            handler.sendEmptyMessage(MSG_RECARGA);
        }

        // Barcode
        if (requestCode == IntentIntegrator.REQUEST_CODE) {

            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null) {
                String contents = result.getContents();
                if (contents != null) {

                    String parada = Utilidades.parsearCodigoParada(result.getContents());

                    if (parada != null) {

                        Toast.makeText(this, "QR Code: " + parada, Toast.LENGTH_SHORT).show();

                        paradaActual = Integer.parseInt(parada);

                        // Poner en campo de poste
                        EditText txtPoste = (EditText) findViewById(R.id.campo_poste);
                        txtPoste.setText(Integer.toString(paradaActual));

                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putInt("parada_inicio", paradaActual);
                        editor.commit();

                        handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

                    } else {
                        Toast.makeText(this, getString(R.string.barcode_error), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(this, getString(R.string.barcode_error), Toast.LENGTH_SHORT).show();
                }
            }

        }

        // Voz
        if (requestCode == VOICE_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }

        // Galeria de imagenes
        if (requestCode == CARGAR_IMAGEN)
            if (resultCode == Activity.RESULT_OK) {

                try {

                    Uri selectedImage = data.getData();

                    // Cargamos imagen seleccionada

                    if (selectedImage != null) {
                        //Nueva galeria Fotos
                        gestionarFondo.activarNuevoFondo19(selectedImage);
                    } else {
                        //Galeria de Android
                        gestionarFondo.activarNuevoFondo(selectedImage);
                    }


                } catch (Exception e) {

                    Toast.makeText(this, getString(R.string.error_fichero), Toast.LENGTH_SHORT).show();

                }

            }

        if (requestCode == GestionarVoz.VOICE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                ArrayList<String> resultados = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                for (int i = 0; i < resultados.size(); i++) {
                    Log.d("VOZ", "datos: " + resultados.get(i));
                }

                boolean datosEncontrados = gestionarVoz.seleccionarPosibleOpcion(resultados);

                if (!datosEncontrados) {
                    Toast.makeText(this, getString(R.string.reconocimiento_voz_sin_datos), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, getString(R.string.reconocimiento_voz_error), Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * Lanza la subactividad de anadir favorito. Le pasa el poste y la
     * descripcion
     */
    private void nuevoFavorito() {
        Intent i = new Intent(MainActivity.this, FavoritoNuevoActivity.class);

        Bundle extras = new Bundle();
        extras.putInt("POSTE", paradaActual); // Pasamos el poste actual
        // Preparamos una descripcion automatica para el favorito
        HashSet<String> h = new HashSet<>();
        for (BusLlegada bus : buses) {
            h.add(bus.getLinea() + " a " + bus.getDestino());
        }
        extras.putString("DESCRIPCION", h.toString());

        i.putExtras(extras);
        startActivityForResult(i, SUB_ACTIVITY_REQUEST_ADDFAV);
    }

    /**
     * Muestro la barra de progreso?
     *
     * @param show
     */
    public void showProgressBar(Boolean show) {
        if (show) {

            swipeRefresh.setRefreshing(true);


        } else {

            // Finalizar refresh
            if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }


        }
    }


    /**
     * Actualizar datos tiempos
     */
    class TiemposUpdater implements Runnable {
        public void run() {
            try {

                showProgressBar(true);

                detenerTareaTiempos();

                /**
                 * Sera llamado cuando la tarea de cargar tiempos termine
                 */
                LoadTiemposAsyncTaskResponder loadTiemposAsyncTaskResponder = new LoadTiemposAsyncTaskResponder() {
                    public void tiemposLoaded(DatosRespuesta datosRespuesta) {

                        ArrayList<BusLlegada> tiempos = null;

                        if (datosRespuesta != null) {
                            tiempos = datosRespuesta.getListaBusLlegada();

                            //Reordenar en funcion de fijado
                            tiempos = datosPantallaPrincipal.ordenarTiemposPorTarjetaFija(tiempos);

                        }

                        if (tiempos != null) {
                            buses = tiempos;
                            handler.sendEmptyMessage(MSG_FRECUENCIAS_ACTUALIZADAS);
                            showProgressBar(false);
                        } else {
                            // Error al recuperar datos
                            showProgressBar(false);

                            buses = new ArrayList<>();
                            BusLlegada b1 = new BusLlegada();
                            b1.setErrorServicio(true);
                            buses.add(b1);

                            if (tiemposAdapter != null && tiemposAdapter.getBuses(Integer.toString(paradaActual)) != null) {
                                int n = tiemposAdapter.getBuses(Integer.toString(paradaActual)).size();

                                for (int i = 0; i < n; i++) {

                                    if (!tiemposAdapter.getBuses(Integer.toString(paradaActual)).get(i).isErrorServicio()) {
                                        buses.add(tiemposAdapter.getBuses(Integer.toString(paradaActual)).get(i));
                                    }
                                }

                            }

                            handler.sendEmptyMessage(MSG_ERROR_TIEMPOS);
                        }

                        if (datosRespuesta != null && datosRespuesta.getError() != null && datosRespuesta.getError().equals(TiempoBusException.ERROR_STATUS_SERVICIO)) {

                            //Toast.makeText(getApplicationContext(), getString(R.string.error_status), Toast.LENGTH_SHORT).show();

                        }

                    }
                };

                Boolean cacheTiempos = preferencias.getBoolean("conectividad_cache_tiempos", false);

                // Control de disponibilidad de conexion
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    loadTiemposTask = new LoadTiemposAsyncTask(loadTiemposAsyncTaskResponder).execute(paradaActual, getApplicationContext(), cacheTiempos);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
                    showProgressBar(false);
                }

            } catch (Exception e) {

                buses = new ArrayList<>();
                BusLlegada b1 = new BusLlegada();
                b1.setErrorServicio(true);
                buses.add(b1);

                if (tiemposAdapter != null && tiemposAdapter.getBuses(Integer.toString(paradaActual)) != null) {
                    int n = tiemposAdapter.getBuses(Integer.toString(paradaActual)).size();

                    for (int i = 0; i < n; i++) {

                        if (!tiemposAdapter.getBuses(Integer.toString(paradaActual)).get(i).isErrorServicio()) {
                            buses.add(tiemposAdapter.getBuses(Integer.toString(paradaActual)).get(i));
                        }
                    }

                }


                handler.sendEmptyMessage(MSG_ERROR_TIEMPOS);
            }

        }
    }

    /**
     * Manejar mensajes
     */
    public static class ParadaActualHandler extends Handler {

        private final WeakReference<MainActivity> mActividad;

        ParadaActualHandler(MainActivity actividad) {

            mActividad = new WeakReference<>(actividad);

        }

        @Override
        public void handleMessage(Message msg) {

            MainActivity laActividad = mActividad.get();

            switch (msg.what) {

                case MSG_ERROR_TIEMPOS:

                    if (laActividad != null) {
                        Toast.makeText(laActividad, laActividad.getString(R.string.error_tiempos), Toast.LENGTH_SHORT).show();

                        laActividad.showProgressBar(false);
                    }

                    sendEmptyMessage(MSG_FRECUENCIAS_ACTUALIZADAS);

                    break;

                case MSG_CLOSE_CARGANDO:
                    laActividad.showProgressBar(false);
                    break;

                case MSG_RECARGA:

                    if (laActividad.tiemposUpdater != null) {
                        removeCallbacks(laActividad.tiemposUpdater);
                    }
                    removeMessages(MSG_RECARGA);
                    post(laActividad.tiemposUpdater);
                    sendEmptyMessageDelayed(MSG_RECARGA, laActividad.datosPantallaPrincipal.frecuenciaRecarga());
                    break;

                case MSG_FRECUENCIAS_ACTUALIZADAS:

                    try {


                        String cabdatos = "";
                        String cabdatos2 = "";

                        cabdatos2 = laActividad.datosPantallaPrincipal.cargarDescripcionBD(laActividad.paradaActual);

                        cabdatos = laActividad.datosPantallaPrincipal.cargarDescripcion(Integer.toString(laActividad.paradaActual));

                        ImageView imgFavorito = (ImageView) laActividad.findViewById(R.id.indicador_favorito);


                        final MainActivity activ = mActividad.get();

                        if (cabdatos.equals("")) {
                            // Si no hay favorito, descripcion de la base de datos
                            cabdatos = cabdatos2;

                            // Si no es favorito
                            imgFavorito.setImageDrawable(ResourcesCompat.getDrawable(laActividad.getResources(), R.drawable.ic_bookmark_outline_grey600_18dp, null));

                            // Para acceder a guardar favorito
                            imgFavorito.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    Intent i = new Intent(activ, FavoritoNuevoActivity.class);

                                    Bundle extras = new Bundle();
                                    extras.putInt("POSTE", activ.paradaActual);
                                    // Preparamos una descripcion automatica para el
                                    // favorito
                                    HashSet<String> h = new HashSet<>();
                                    for (BusLlegada bus : activ.buses) {
                                        h.add(bus.getLinea() + " a " + bus.getDestino());
                                    }
                                    extras.putString("DESCRIPCION", h.toString());

                                    i.putExtras(extras);
                                    activ.startActivityForResult(i, SUB_ACTIVITY_REQUEST_ADDFAV);
                                }
                            });


                            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            FloatingActionButton imgCircularFavorito = (FloatingActionButton) laActividad.findViewById(R.id.boton_circular_fav);

                            imgCircularFavorito.setImageDrawable(ResourcesCompat.getDrawable(laActividad.getResources(), R.drawable.ic_bookmark_outline_white_24dp, null));

                            // Para acceder a guardar favorito
                            imgCircularFavorito.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    Intent i = new Intent(activ, FavoritoNuevoActivity.class);

                                    Bundle extras = new Bundle();
                                    extras.putInt("POSTE", activ.paradaActual);
                                    // Preparamos una descripcion automatica para el
                                    // favorito
                                    HashSet<String> h = new HashSet<>();
                                    for (BusLlegada bus : activ.buses) {
                                        h.add(bus.getLinea() + " a " + bus.getDestino());
                                    }
                                    extras.putString("DESCRIPCION", h.toString());

                                    i.putExtras(extras);
                                    activ.startActivityForResult(i, SUB_ACTIVITY_REQUEST_ADDFAV);
                                }
                            });


                        } else {

                            // Si hay favorito cambiar indicador favorito
                            imgFavorito.setImageDrawable(ResourcesCompat.getDrawable(laActividad.getResources(), R.drawable.ic_bookmark_grey600_18dp, null));

                            // Para acceder al listado de favoritos
                            imgFavorito.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    activ.detenerTodasTareas();
                                    activ.startActivityForResult(new Intent(activ, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_POSTE);
                                }
                            });


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                                FloatingActionButton imgCircularFavorito = (FloatingActionButton) laActividad.findViewById(R.id.boton_circular_fav);


                                imgCircularFavorito.setImageDrawable(ResourcesCompat.getDrawable(laActividad.getResources(), R.drawable.ic_bookmark_white_24dp, null));


                                // Para acceder al listado de favoritos
                                imgCircularFavorito.setOnClickListener(new OnClickListener() {
                                    public void onClick(View v) {
                                        activ.detenerTodasTareas();
                                        activ.startActivityForResult(new Intent(activ, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_POSTE);
                                    }
                                });
                            } else {

                                ImageButton imgCircularFavorito = (ImageButton) laActividad.findViewById(R.id.boton_circular_fav);


                                imgCircularFavorito.setImageDrawable(ResourcesCompat.getDrawable(laActividad.getResources(), R.drawable.ic_bookmark_white_24dp, null));


                                // Para acceder al listado de favoritos
                                imgCircularFavorito.setOnClickListener(new OnClickListener() {
                                    public void onClick(View v) {
                                        activ.detenerTodasTareas();
                                        activ.startActivityForResult(new Intent(activ, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_POSTE);
                                    }
                                });

                            }


                        }


                        if (cabdatos.equals("")) {
                            cabdatos = laActividad.getString(R.string.share_0b) + " " + laActividad.paradaActual;
                        }

                        //ImageButton botonHorarios = (ImageButton) laActividad.findViewById(R.id.aviso_header_horario);

                        if (laActividad.datosPantallaPrincipal.esTram(laActividad.paradaActual)) {
                            cabdatos = "TRAM " + cabdatos;

                            // Estadisticas tram
                            analyticsTram(laActividad);

                            //botonHorarios.setVisibility(View.VISIBLE);

                        } else {
                            //botonHorarios.setVisibility(View.INVISIBLE);
                        }

                        // Historial
                        laActividad.datosPantallaPrincipal.gestionarHistorial(laActividad.paradaActual, null);
                        laActividad.datosPantallaPrincipal.actualizarAnteriorHistorial();

                        laActividad.datosParada.setText(cabdatos);

                        final Calendar c = Calendar.getInstance();

                        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.US);

                        String updated = df.format(c.getTime());

                        laActividad.guiHora.setText(updated);

                        // Limpiamos la lista
                        laActividad.tiemposAdapter.clear();

                        // La rellenamos con los nuevos datos
                        if (laActividad.buses != null && laActividad.buses.size() > 0) {

                            int n = laActividad.buses.size();

                            for (int i = 0; i < n; i++) {
                                laActividad.tiemposAdapter.add(laActividad.buses.get(i));
                            }

                            laActividad.tiemposAdapter.setBuses(laActividad.buses, laActividad.paradaActual);

                        } else {

                            // Control de sin datos
                            BusLlegada sinDatos = new BusLlegada();
                            sinDatos.setSinDatos(true);
                            laActividad.tiemposAdapter.add(sinDatos);

                        }

                        // Pie para la lista de resultados
                        laActividad.gestionarTarjetaInfo.cargarTarjetaInfo();
                        laActividad.datosPantallaPrincipal.cargarPie();

                        //Cargar fragment horarios en funcion de si es tram o bus
                        laActividad.gestionarTarjetaInfo.controlFragmentHorarios();

                        laActividad.tiemposAdapter.notifyDataSetChanged();


                        // NOTICIAS
                        boolean verificaNoticias = laActividad.preferencias.getBoolean("aviso_noticias", true);
                        boolean verificaNoticiasTram = laActividad.preferencias.getBoolean("aviso_noticias_tram", true);

                        if (verificaNoticias) {
                            laActividad.datosPantallaPrincipal.verificarNuevasNoticias();
                        }

                        if (UtilidadesTRAM.ACTIVADO_TRAM && verificaNoticiasTram) {
                            laActividad.datosPantallaPrincipal.verificarNuevasNoticiasTram();
                        }


                        break;


                    } catch (Exception e) {
                        e.printStackTrace();
                        if (laActividad != null) {
                            Toast.makeText(laActividad, laActividad.getString(R.string.error_tiempos), Toast.LENGTH_SHORT).show();
                        }

                    }

            }

            // Si no hay atuomatico, se cancela el mensaje
            boolean auto = laActividad.preferencias.getBoolean("checkbox_preference", true);

            if (!auto) {
                laActividad.handler.removeMessages(MSG_RECARGA);
            }

        }
    }

    /**
     * init
     */
    public void onInit(int status) {

        try {

            // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
            if (status == TextToSpeech.SUCCESS) {
                // Set preferred language to US english.
                // Note that a language may not be available, and the result
                // will
                // indicate this.
                int result = mTts.setLanguage(Locale.getDefault());
                // Try this someday for some interesting results.
                // int result mTts.setLanguage(Locale.FRANCE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Lanuage data is missing or the language is not supported.

                    lecturaOK = false;

                    try {

                        Locale loc = new Locale("spa", "ES");

                        result = mTts.setLanguage(loc);

                        lecturaAlternativa = !(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED);

                    } catch (Exception e) {
                        lecturaAlternativa = false;
                        e.printStackTrace();
                    }

                } else {
                    // Check the documentation for other possible result codes.
                    // For example, the language may be available for the
                    // locale,
                    // but not for the specified country and variant.

                    // The TTS engine has been successfully initialized.
                    // Allow the user to press the button for the app to speak
                    // again.
                    // mAgainButton.setEnabled(true);
                    // Greet the user.
                    // textToSpeech();

                    lecturaOK = true;
                    lecturaAlternativa = false;

                }
            } else {
                // Initialization failed.
                lecturaOK = false;
                lecturaAlternativa = false;
            }

        } catch (Exception e) {

            Toast.makeText(this, getString(R.string.error_voz), Toast.LENGTH_SHORT).show();

            // Initialization failed.
            lecturaOK = false;
            lecturaAlternativa = false;

            e.printStackTrace();

        }

    }

    /**
     * Texto a leer
     *
     * @param texto
     */
    public void textToSpeech(String texto) {

        if (texto != null && !texto.equals("")) {
            mTts.speak(texto, TextToSpeech.QUEUE_FLUSH, // Drop all pending
                    // entries in the
                    // playback queue.
                    null);
        }
    }

    public DatosPantallaPrincipal getDatosPantallaPrincipal() {
        return datosPantallaPrincipal;
    }

    public void onArticleSelected(int position) {
        // TODO Auto-generated method stub

    }

    /**
     * Para la recarga con swipe vertical
     */
    public void onRefresh() {
        // TODO Auto-generated method stub

        recargarTiempos();

        // Finalizar
        // swipeRefresh.setRefreshing(false);

    }

    public Tracker getTracker() {

        // Get tracker.
        Tracker t = ((ApplicationTiempoBus) this.getApplication()).getTracker(TrackerName.APP_TRACKER);

        return t;

    }

    /**
     * Estadistica uso de tram
     *
     * @param actividad
     */
    public static void analyticsTram(MainActivity actividad) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {

            try {

                int parada = actividad.preferencias.getInt("parada_tram", 0);

                if (parada != actividad.paradaActual) {

                    SharedPreferences.Editor editor = actividad.preferencias.edit();
                    editor.putInt("parada_tram", actividad.paradaActual);
                    editor.commit();

                    // Get tracker.
                    Tracker t = ((ApplicationTiempoBus) actividad.getApplication()).getTracker(TrackerName.APP_TRACKER);

                    // Build and send an Event.
                    t.send(new HitBuilders.EventBuilder().setCategory("EVENTOS").setAction("TRAM").setLabel("TIEMPO_TRAM").build());

                    Log.d("PRINCIPAL", "Enviado tram a analytics");

                }

            } catch (Exception e) {

            }

        }

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // success!
                gestionarFondo.seleccionarFondoPermisos();
            } else {
                // Permission was denied or request was cancelled

                Toast.makeText(getApplicationContext(), getString(R.string.error_fichero), Toast.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
