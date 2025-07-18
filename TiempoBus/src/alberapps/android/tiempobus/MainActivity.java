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
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import alberapps.android.tiempobus.alarma.GestionarAlarmas;
import alberapps.android.tiempobus.barcode.IntentIntegrator;
import alberapps.android.tiempobus.barcode.IntentResult;
import alberapps.android.tiempobus.barcode.UtilidadesBarcode;
import alberapps.android.tiempobus.barcodereader.BarcodeMainActivity;
import alberapps.android.tiempobus.data.FavoritosProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.historial.HistorialProvider;
import alberapps.android.tiempobus.databinding.PantallaPrincipalBinding;
import alberapps.android.tiempobus.favoritos.FavoritoNuevoActivity;
import alberapps.android.tiempobus.favoritos.FavoritosActivity;
import alberapps.android.tiempobus.favoritos.googledriverest.FavoritoGoogleDriveRestActivity;
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
import alberapps.android.tiempobus.util.Notificaciones;
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
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, FragmentSecundarioTablet.OnHeadlineSelectedListener,
        SwipeRefreshLayout.OnRefreshListener, PrincipalHorarioTramFragment.OnFragmentInteractionListener, OnMapsSdkInitializedCallback {

    public static final int SUB_ACTIVITY_REQUEST_PARADA = 1000;
    public static final int SUB_ACTIVITY_REQUEST_ADDFAV = 1001;
    public static final int SUB_ACTIVITY_RESULT_OK = 1002;
    public static final int SUB_ACTIVITY_RESULT_CANCEL = 1003;
    protected static final int SUB_ACTIVITY_REQUEST_PREFERENCIAS = 1004;
    protected static final int SUB_ACTIVITY_REQUEST_NOTICIAS = 1005;

    protected static final int VOICE_CHECK_CODE = 3000;

    protected static final int SUB_ACTIVITY_REQUEST_BARCODE = 3001;

    public static final int CARGAR_IMAGEN = 2000;

    protected static final int MSG_CLOSE_CARGANDO = 200;
    protected static final int MSG_ERROR_TIEMPOS = 201;
    public static final int MSG_FRECUENCIAS_ACTUALIZADAS = 202;
    public static final int MSG_RECARGA = 203;
    public static final long DELAY_RECARGA = 750;

    public ArrayList<BusLlegada> buses = new ArrayList<>();
    private TiemposAdapter tiemposAdapter;
    private TextView guiHora;
    //private TextView datosParada;

    public int paradaActual = 4450;
    public final ParadaActualHandler handler = new ParadaActualHandler(this);

    public TiemposUpdater tiemposUpdater = new TiemposUpdater();
    AlarmManager alarmManager;
    private ImageButton botonCargaTiempos;

    public String latitudInfo = null;
    public String longitudInfo = null;

    public BusLlegada busSeleccionado = null;

    public SharedPreferences preferencias = null;

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
    public NavigationView mDrawerView;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    AsyncTask<Object, Void, DatosRespuesta> loadTiemposTask = null;
    public AsyncTask<Object, Void, List<Noticias>> nuevasNoticiasTask;
    public AsyncTask<Object, Void, AvisosTram> nuevasNoticasTramTask;
    public AsyncTask<Object, Void, AvisosTram> nuevasNoticasAlberAppsTramTask;

    public View avisoPie = null;

    public View avisoTarjetaInfo = null;

    SwipeRefreshLayout swipeRefresh = null;

    public static final int REQUEST_CODE_STORAGE = 4;

    private BottomNavigationView bottomNavigation = null;


    public FirebaseAnalytics mFirebaseAnalytics;
    public FirebaseCrashlytics mFirebaseCrash;

    private PantallaPrincipalBinding pantallaPrincipalBinding;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseCrash = FirebaseCrashlytics.getInstance();

        //setContentView(R.layout.pantalla_principal);
        pantallaPrincipalBinding = PantallaPrincipalBinding.inflate(getLayoutInflater());
        setContentView(pantallaPrincipalBinding.getRoot());


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        Conectividad.activarCache(this, preferencias);

        setSupportActionBar(pantallaPrincipalBinding.toolbarid);

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
            editor.apply();
        }

        setupView();


        mTts = new TextToSpeech(this, this // TextToSpeech.OnInitListener
        );

        // Avisos
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Gestionar alarmas
        gestionarAlarmas = new GestionarAlarmas(this, preferencias, alarmManager);

        controlesIniciales();

        Notificaciones.initChannels(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Notificaciones.requestNotificationPermission(this);
        }

        MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, this);

    }

    @Override
    protected void onStart() {

        super.onStart();

        //Drive
        MenuItem driveMenu = mDrawerView.getMenu().getItem(0).getSubMenu().getItem(0);

        if (preferencias.contains("drive_cuenta")) {
            driveMenu.setTitle(preferencias.getString("drive_cuenta", getString(R.string.archivo_drive_signin)));
        } else {
            driveMenu.setTitle(getString(R.string.archivo_drive_signin));
        }

    }


    /**
     * Cargas iniciales y control de analytics
     */
    private void controlesIniciales() {

        // Verificar si hay parada por defecto
        if (preferencias.contains("parada_inicio")) {
            paradaActual = preferencias.getInt("parada_inicio", paradaActual);
        }

        Log.d("PRINCIPAL", "inicia: " + buses.size());

        handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

        // Poner en campo de poste
        AppCompatEditText txtPoste = findViewById(R.id.campo_poste);
        txtPoste.setText(Integer.toString(paradaActual));


        datosPantallaPrincipal.controlMostrarAnalytics();

        if (preferencias.getBoolean("analytics_on", false)) {

            //Nuevo para firebase
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
            mFirebaseCrash.setCrashlyticsCollectionEnabled(true);
            //

            Log.d("PRINCIPAL", "Analytics activo");

        } else {

            //Nuevo para firebase
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(false);
            mFirebaseCrash.setCrashlyticsCollectionEnabled(false);

            Log.d("PRINCIPAL", "Analytics inactivo");

        }


    }

    /**
     * Drawer Layout
     *
     * @param savedInstanceState
     */
    @SuppressLint("NewApi")
    private void iniciarDrawer(Bundle savedInstanceState) {
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mDrawerView = findViewById(R.id.left_drawer);

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

        View mDrawerHeader = mDrawerView.getHeaderView(0).findViewById(R.id.drawer_header);

        AppCompatTextView imgCabecera = mDrawerHeader.findViewById(R.id.imgAlberapps);

        if (imgCabecera != null) {
            imgCabecera.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    UtilidadesUI.openWebPage(MainActivity.this, "https://blog.alberapps.com");

                }
            });
        }


        //Drive
        MenuItem driveMenu = mDrawerView.getMenu().getItem(0).getSubMenu().getItem(0);

        if (preferencias.contains("drive_cuenta")) {
            driveMenu.setTitle(preferencias.getString("drive_cuenta", getString(R.string.archivo_drive_signin)));
        }

        datosPantallaPrincipal.opcionesNotificacion(mDrawerHeader);


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

        Bundle bundle = new Bundle();

        switch (item.getItemId()) {

            case R.id.navigation_item_mapa:

                if (datosPantallaPrincipal.servicesConnected()) {
                    detenerTodasTareas();
                    startActivityForResult(new Intent(MainActivity.this, MapasActivity.class), SUB_ACTIVITY_REQUEST_PARADA);
                }

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M01");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Mapa");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                break;

            case R.id.navigation_item_noticias:

                item.setChecked(false);

                detenerTodasTareas();
                //startActivity(new Intent(MainActivity.this, NoticiasTabsPager.class));
                startActivityForResult(new Intent(MainActivity.this, NoticiasTabsPager.class), SUB_ACTIVITY_REQUEST_PARADA);

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M02");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Noticias");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                break;

            case R.id.navigation_item_favoritos:
                detenerTodasTareas();
                startActivityForResult(new Intent(MainActivity.this, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_PARADA);

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M03");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Favoritos");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;

            case R.id.navigation_item_guardar:
                detenerTodasTareas();
                nuevoFavorito();

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M04");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Nuevo Favorito");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;

            case R.id.navigation_item_historial:
                detenerTodasTareas();
                startActivityForResult(new Intent(MainActivity.this, HistorialActivity.class), SUB_ACTIVITY_REQUEST_PARADA);

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M05");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Historial");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;

            case R.id.navigation_item_horarios_tram:

                detenerTodasTareas();

                Intent i = new Intent(this, InfoLineasTabsPager.class);
                i.putExtra("HORARIOS", "TRAM");

                this.startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M09");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Horario TRAM");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;

            case R.id.navigation_item_horarios_bus:

                detenerTodasTareas();

                Intent j = new Intent(this, InfoLineasTabsPager.class);
                j.putExtra("MODO_RED", InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE);

                this.startActivityForResult(j, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);


                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M10");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Horario BUS");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;

            case R.id.navigation_item_preferencias:

                detenerTodasTareas();
                startActivityForResult(new Intent(MainActivity.this, PreferencesFromXml.class), SUB_ACTIVITY_REQUEST_PREFERENCIAS);

                //startActivityForResult(new Intent(MainActivity.this, Settings2Activity.class), SUB_ACTIVITY_REQUEST_PREFERENCIAS);

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M06");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Preferencias");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;

            case R.id.navigation_item_fondo:

                gestionarFondo.configurarTema();

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M07");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Configuar tema");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;


            /*case R.id.navigation_item_exportar:
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);

                // configurar
                integrator.setTitleByID(R.string.barcode_titulo);
                integrator.setMessageByID(R.string.barcode_mensaje);
                integrator.setButtonYesByID(R.string.barcode_si);
                integrator.setButtonNoByID(R.string.barcode_no);

                String paradaCodificada = UtilidadesBarcode.codificarCodigoParada(Integer.toString(paradaActual));

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M08");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Exportar QR");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;*/
            case R.id.navigation_item_rutas:


                detenerTodasTareas();
                //startActivity(new Intent(MainActivity.this, RutasActivity.class));
                startActivityForResult(new Intent(MainActivity.this, RutasActivity.class), SUB_ACTIVITY_REQUEST_PARADA);

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M08");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Rutas");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;

            case R.id.navigation_item_drive:
                detenerTodasTareas();
                startActivity(new Intent(MainActivity.this, FavoritoGoogleDriveRestActivity.class));

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M09");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Favoritos - Drive");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;

            case R.id.navigation_item_link_blog:

                UtilidadesUI.openWebPage(this, "https://blog.alberapps.com");

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M10");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Blog");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                break;
            /*case R.id.navigation_item_link_tw:

                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M10");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - TW");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                UtilidadesUI.openWebPage(this, "https://twitter.com/alberapps");

                break;*/
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


    @Override
    protected void onStop() {

        Conectividad.flushCache();

        handler.removeMessages(MSG_RECARGA);

        //Cerrar BD
        FavoritosProvider.DatabaseHelper.getInstance(this).close();
        DatosLineasDB.DatosLineasOpenHelper.getInstance(this).close();
        HistorialProvider.DatabaseHelper.getInstance(this).close();
        //

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

        //Cerrar BD
        FavoritosProvider.DatabaseHelper.getInstance(this).close();
        DatosLineasDB.DatosLineasOpenHelper.getInstance(this).close();
        HistorialProvider.DatabaseHelper.getInstance(this).close();
        //

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

        if (nuevasNoticasAlberAppsTramTask != null && nuevasNoticasAlberAppsTramTask.getStatus() == Status.RUNNING) {

            nuevasNoticasAlberAppsTramTask.cancel(true);

            Log.d("tiempos", "Cancelada task nuevas noticias alberapps");

        }


    }

    @Override
    public void finish() {

        // Guardar ultima parada seleccionada
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("parada_inicio", paradaActual);
        editor.apply();

        editor.remove("parada_tram");
        editor.apply();

        handler.removeMessages(MSG_RECARGA);

        super.finish();

    }


    @Override
    protected void onPause() {

        // Guardar ultima parada seleccionada
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("parada_inicio", paradaActual);
        editor.apply();

        editor.remove("parada_tram");
        editor.apply();

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

        // Una vez cargado, recargamos datos
        handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

        datosPantallaPrincipal.controlMostrarNovedades();

        mDrawerToggle.syncState();

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
                startActivityForResult(new Intent(MainActivity.this, InfoLineasTabsPager.class), SUB_ACTIVITY_REQUEST_PARADA);

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

        //Status bar color init
        UtilidadesUI.initStatusBar(this);

        /**
         * Configuramos la lista de resultados
         */
        tiemposAdapter = new TiemposAdapter(this, R.layout.tiempos_item);

        // registerForContextMenu(getListView());

        // Pie para la lista de resultados
        datosPantallaPrincipal.cargarHeader();


        guiHora = findViewById(R.id.ultima_act);

        //datosParada = (TextView) findViewById(R.id.datos_parada);

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


        tiemposView.setOnScrollListener(new ScrollListenerAux());


        // Asignamos el adapter a la lista
        tiemposView.setAdapter(tiemposAdapter);
        tiemposAdapter.notifyDataSetChanged();


        /**
         * Definimos el comportamiento de los botones
         */

        // boton poste
        botonCargaTiempos = findViewById(R.id.boton_subposte);
        botonCargaTiempos.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                accionCargar();

            }
        });


        // //Barcode

        ImageButton botonBarcode = findViewById(R.id.boton_barcode);
        botonBarcode.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                iniciarLectorCodigoQr();


            }
        });

        // //Alertas

        ImageButton botonAlerta = findViewById(R.id.boton_alertas);
        botonAlerta.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                gestionarAlarmas.mostrarModalAlertas(paradaActual);

            }
        });

        // //Info

        ImageButton botonInfo = findViewById(R.id.boton_info);
        botonInfo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                // datosPantallaPrincipal.cargarModalInfo(paradaActual);

                boolean resultado = gestionarVoz.reconocerVoz();

                if (!resultado) {
                    Toast.makeText(getApplicationContext(), getString(R.string.reconocimiento_voz_no), Toast.LENGTH_SHORT).show();
                }

            }
        });

        // //Fav destacados

        ImageView botonFavDesc = findViewById(R.id.favorito_dest);
        botonFavDesc.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                datosPantallaPrincipal.abrirFavDestacados();

            }
        });

        // //info covid

        /*TextView infoCovid = findViewById(R.id.info_covid);
        infoCovid.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                UtilidadesUI.openWebPage(MainActivity.this, "http://coronavirus.san.gva.es");

            }
        });*/


        // Swipe para recargar
        swipeRefresh = findViewById(R.id.swipeRefreshPrincipal);
        swipeRefresh.setColorSchemeResources(R.color.mi_material_blue_principal, R.color.tram_l2, R.color.mi_material_blue_principal, R.color.tram_l2);
        swipeRefresh.setOnRefreshListener(this);


        //Control desde teclado
        AppCompatEditText txtPoste = findViewById(R.id.campo_poste);
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


        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                selectItem(item);

                return true;
            }
        });


    }

    public void iniciarLectorCodigoQr() {

        final CharSequence[] items = {getString(R.string.barcode_opcion_1), getString(R.string.barcode_opcion_2), getString(R.string.menu_export_qr) + " " + getString(R.string.barcode_opcion_3_info)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.barcode_opcion_titulo);

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }

        });

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {

                    Intent i = new Intent(MainActivity.this, BarcodeMainActivity.class);
                    startActivityForResult(i, SUB_ACTIVITY_REQUEST_BARCODE);

                } else if (item == 1) {

                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);

                    // configurar
                    integrator.setTitleByID(R.string.barcode_titulo);
                    integrator.setMessageByID(R.string.barcode_mensaje);
                    integrator.setButtonYesByID(R.string.barcode_si);
                    integrator.setButtonNoByID(R.string.barcode_no);

                    integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);


                } else if (item == 2) {

                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);

                    // configurar
                    integrator.setTitleByID(R.string.barcode_titulo);
                    integrator.setMessageByID(R.string.barcode_mensaje);
                    integrator.setButtonYesByID(R.string.barcode_si);
                    integrator.setButtonNoByID(R.string.barcode_no);

                    String paradaCodificada = UtilidadesBarcode.codificarCodigoParada(Integer.toString(paradaActual));

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "M08");
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Menu - Exportar QR");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }


            }
        });

        AlertDialog alert = builder.create();

        alert.show();

    }

    /**
     * Cargar al pulsar boton o otra accion
     */
    private void accionCargar() {

        AppCompatEditText txtPoste = findViewById(R.id.campo_poste);

        try {
            int tmpPoste = Integer.parseInt(txtPoste.getText().toString());
            if (tmpPoste > 0 && tmpPoste < 9999) {
                paradaActual = tmpPoste;

                SharedPreferences.Editor editor = preferencias.edit();
                editor.putInt("parada_inicio", paradaActual);
                editor.apply();

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

        AppCompatEditText txtPoste = findViewById(R.id.campo_poste);

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

                Bundle bundle = new Bundle();

                switch (item) {
                    case 0:

                        if (busSeleccionado != null) {

                            try {

                                // Texto para receiver
                                String textoReceiver = gestionarAlarmas.prepararReceiver(busSeleccionado, paradaActual);

                                // Activar alarma y mostrar modal
                                gestionarAlarmas.mostrarModalTiemposAlerta(busSeleccionado, paradaActual, textoReceiver);
                                busSeleccionado = null;

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();
                        }

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "C01");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Tarjeta - Menu Alarma");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        break;

                    case 1:
                        if (busSeleccionado != null) {
                            datosPantallaPrincipal.shareBus(busSeleccionado, paradaActual);
                            busSeleccionado = null;
                        }

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "C02");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Tarjeta - Menu Compartir");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        break;

                    case 2:

                        if (datosPantallaPrincipal.servicesConnected()) {

                            if (busSeleccionado != null && busSeleccionado.getLinea() != null && !busSeleccionado.getLinea().equals("")) {
                                Intent i = new Intent(MainActivity.this, MapasActivity.class);
                                i.putExtra("LINEA_MAPA", busSeleccionado.getLinea());
                                i.putExtra("LINEA_MAPA_PARADA", Integer.toString(paradaActual));
                                startActivityForResult(i, SUB_ACTIVITY_REQUEST_PARADA);
                            }

                            busSeleccionado = null;

                        }

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "C03");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Tarjeta - Menu Mapa");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        break;

                    case 3:
                        datosPantallaPrincipal.cantarLinea(busSeleccionado);
                        busSeleccionado = null;

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "C04");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Tarjeta - Menu Leer");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        break;

                    case 4:
                        gestionarWidget.enviarAWidget(busSeleccionado, paradaActual);
                        busSeleccionado = null;

                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "C05");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Tarjeta - Menu Widget");
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
                    editor.apply();

                    startService(intent);

                } else {

                    Intent intent = new Intent(TiemposForegroundService.ACTION_FOREGROUND);
                    intent.setClass(MainActivity.this, TiemposForegroundService.class);

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("activarServicio", false);
                    editor.apply();

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
                case SUB_ACTIVITY_REQUEST_PARADA:

                    if (data.getExtras() != null) {
                        Bundle b = data.getExtras();

                        if (b.containsKey("MODO_RED_INFO")) {
                            Intent i = new Intent(this, InfoLineasTabsPager.class);
                            i.putExtra("MODO_RED", b.getInt("MODO_RED_INFO"));
                            startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);
                            break;

                        } else if (b.containsKey("MODO_RED_MAPA")) {
                            Intent i = new Intent(this, MapasActivity.class);
                            i.putExtra("MODO_RED", b.getInt("MODO_RED_MAPA"));
                            startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);
                            break;

                        } else if (b.containsKey("POSTE")) {

                            if (b.getInt("POSTE") == 0) {

                                // //Horarios tram
                                detenerTodasTareas();

                                Intent i = new Intent(this, InfoLineasTabsPager.class);
                                i.putExtra("HORARIOS", "TRAM");
                                i.putExtra("HORARIOSDATA", b.getString("HORARIOS"));

                                startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                                break;


                            }

                            paradaActual = b.getInt("POSTE");
                        }
                    }

                    // Poner en campo de poste
                    AppCompatEditText txtPoste = findViewById(R.id.campo_poste);
                    txtPoste.setText(Integer.toString(paradaActual));

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putInt("parada_inicio", paradaActual);
                    editor.apply();

                    handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);
                    break;
                case SUB_ACTIVITY_REQUEST_ADDFAV:
                    startActivityForResult(new Intent(MainActivity.this, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_PARADA);
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

                    String parada = UtilidadesBarcode.parsearCodigoParada(result.getContents());

                    if (parada != null) {

                        Toast.makeText(this, "QR Code: " + parada, Toast.LENGTH_SHORT).show();

                        paradaActual = Integer.parseInt(parada);

                        // Poner en campo de poste
                        AppCompatEditText txtPoste = findViewById(R.id.campo_poste);
                        txtPoste.setText(Integer.toString(paradaActual));

                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putInt("parada_inicio", paradaActual);
                        editor.apply();

                        handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

                    } else {
                        Toast.makeText(this, getString(R.string.barcode_error), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(this, getString(R.string.barcode_error), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == SUB_ACTIVITY_REQUEST_BARCODE) {

            //Nuevo barcode reader

            if (resultCode == Activity.RESULT_OK) {

                String result = data.getStringExtra("RESULTADO_BARCODE");


                if (result != null) {


                    String parada = UtilidadesBarcode.parsearCodigoParada(result);

                    if (parada != null) {

                        Toast.makeText(this, "QR Code: " + parada, Toast.LENGTH_SHORT).show();

                        paradaActual = Integer.parseInt(parada);

                        // Poner en campo de poste
                        AppCompatEditText txtPoste = findViewById(R.id.campo_poste);
                        txtPoste.setText(Integer.toString(paradaActual));

                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putInt("parada_inicio", paradaActual);
                        editor.apply();

                        handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

                    } else {
                        Toast.makeText(this, getString(R.string.barcode_error), Toast.LENGTH_SHORT).show();
                    }


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


        /*if (requestCode == GestionarTarjetaInfo.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();


                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("geo:" + place.getLatLng().latitude + "," + place.getLatLng().longitude + "?z=20&q=" + Uri.encode(place.getName().toString() + ", " + place.getAddress()));

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);


            }
        }*/


    }

    /**
     * Lanza la subactividad de anadir favorito. Le pasa el poste y la
     * descripcion
     */
    private void nuevoFavorito() {

        String cabdatos = datosPantallaPrincipal.cargarDescripcion(Integer.toString(paradaActual));

        if (cabdatos.equals("")) {

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

        } else {

            Toast.makeText(getApplicationContext(), getString(R.string.fav_existente), Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Muestro la barra de progreso?
     *
     * @param show
     */
    public void showProgressBar(Boolean show) {
        if (show) {

            swipeRefresh.setRefreshing(true);

            FloatingActionButton imgCancelar = findViewById(R.id.boton_circular_cancelar);
            imgCancelar.setVisibility(View.VISIBLE);

            //animar
            //ViewCompat.setAlpha(imgCancelar, 0.0f);
            //ViewCompat.animate(imgCancelar).alpha(1.0f);


            imgCancelar.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    detenerTareaTiempos();
                    errorTiempos();
                    showProgressBar(false);
                }
            });


        } else {

            // Finalizar refresh
            if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
            }

            final FloatingActionButton imgCancelar = findViewById(R.id.boton_circular_cancelar);

            //animar

            /*ViewCompat.animate(imgCancelar).alpha(0.0f).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {

                }

                @Override
                public void onAnimationEnd(View view) {
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            });*/

            if(imgCancelar != null) {
                imgCancelar.setVisibility(View.INVISIBLE);
            }

            /*imgCancelar.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    imgCancelar.setVisibility(View.INVISIBLE);
                }
            });*/


        }
    }


    //Error al recuperar los tiempos. Mostrar y recuperar ultimos datos si hay
    public void errorTiempos() {

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

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {
        switch (renderer) {
            case LATEST:
                Log.d("TiempoBus Maps","Maps latest");
                break;
            case LEGACY:
                Log.d("TiempoBus Maps","Maps legacy");
                break;
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

                            errorTiempos();

                        }

                        if (datosRespuesta != null && datosRespuesta.getError() != null && datosRespuesta.getError().equals(TiempoBusException.ERROR_005_SERVICIO)) {

                            Toast.makeText(getApplicationContext(), getString(R.string.error_status_2), Toast.LENGTH_LONG).show();

                        }

                    }
                };

                Boolean cacheTiempos = preferencias.getBoolean("conectividad_cache_tiempos", false);

                String paradaDestinoTram = preferencias.getString("parada_destino_tram", "");

                // Control de disponibilidad de conexion
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {


                    loadTiemposTask = new LoadTiemposAsyncTask(loadTiemposAsyncTaskResponder).execute(paradaActual, getApplicationContext(), cacheTiempos, paradaDestinoTram);
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
                        //Toast.makeText(laActividad, laActividad.getString(R.string.error_tiempos), Toast.LENGTH_SHORT).show();

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

                        ImageView imgFavorito = laActividad.findViewById(R.id.indicador_favorito);


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


                            FloatingActionButton imgCircularFavorito = laActividad.findViewById(R.id.boton_circular_fav);

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
                                    activ.startActivityForResult(new Intent(activ, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_PARADA);
                                }
                            });


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                                FloatingActionButton imgCircularFavorito = laActividad.findViewById(R.id.boton_circular_fav);


                                imgCircularFavorito.setImageDrawable(ResourcesCompat.getDrawable(laActividad.getResources(), R.drawable.ic_bookmark_white_24dp, null));


                                // Para acceder al listado de favoritos
                                imgCircularFavorito.setOnClickListener(new OnClickListener() {
                                    public void onClick(View v) {
                                        activ.detenerTodasTareas();
                                        activ.startActivityForResult(new Intent(activ, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_PARADA);
                                    }
                                });
                            } else {

                                ImageButton imgCircularFavorito = laActividad.findViewById(R.id.boton_circular_fav);


                                imgCircularFavorito.setImageDrawable(ResourcesCompat.getDrawable(laActividad.getResources(), R.drawable.ic_bookmark_white_24dp, null));


                                // Para acceder al listado de favoritos
                                imgCircularFavorito.setOnClickListener(new OnClickListener() {
                                    public void onClick(View v) {
                                        activ.detenerTodasTareas();
                                        activ.startActivityForResult(new Intent(activ, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_PARADA);
                                    }
                                });

                            }


                        }


                        if (cabdatos.equals("")) {
                            cabdatos = laActividad.getString(R.string.share_0b) + " " + laActividad.paradaActual;
                        }

                        //ImageButton botonHorarios = (ImageButton) laActividad.findViewById(R.id.aviso_header_horario);

                        if (DatosPantallaPrincipal.esTram(laActividad.paradaActual) || DatosPantallaPrincipal.esTramRt(laActividad.paradaActual)) {
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

                        //laActividad.datosParada.setText(cabdatos);

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
                        boolean verificaNoticiasTram = false;//laActividad.preferencias.getBoolean("aviso_noticias_tram", true);
                        boolean verificaNoticiasAlberApps = false; //laActividad.preferencias.getBoolean("aviso_noticias_alberapps", true);

                        if (verificaNoticias) {
                            laActividad.datosPantallaPrincipal.verificarNuevasNoticias();
                        }

                        if (UtilidadesTRAM.ACTIVADO_TRAM && verificaNoticiasTram) {
                            laActividad.datosPantallaPrincipal.verificarNuevasNoticiasTram();
                        }

                        if (verificaNoticiasAlberApps) {
                            laActividad.datosPantallaPrincipal.verificarNuevosAvisosAlberApps();
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
                    editor.apply();

                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(actividad);
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "TR01");
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Tiempos - Tram");
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

                    Log.d("PRINCIPAL", "Enviado tram a analytics");

                }

            } catch (Exception e) {

            }

        }

    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // success!
                gestionarFondo.seleccionarFondoPermisos();
            } else {
                // Permission was denied or request was cancelled

                Toast.makeText(getApplicationContext(), getString(R.string.error_fichero), Toast.LENGTH_SHORT).show();

            }

        } else if(requestCode == Notificaciones.RC_HANDLE_NOTIFICATION_PERM) {

            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recargarTiempos();
            } else {
                Notificaciones.requestNotificationPermissionToast(this);
                recargarTiempos();
            }

        }
    }


    @Override
    public void onFragmentInteraction(Integer destino, String textoDestino) {


        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("parada_destino_tram", destino + ";" + textoDestino);
        editor.apply();

        handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);


    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }


    public class ScrollListenerAux implements AbsListView.OnScrollListener {


        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

            if (SCROLL_STATE_TOUCH_SCROLL == i) {
                View cFocus = getCurrentFocus();
                if (cFocus != null) {
                    cFocus.clearFocus();
                }
            }

        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

        }
    }


}
