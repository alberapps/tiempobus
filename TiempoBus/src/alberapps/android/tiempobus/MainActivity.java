/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
 *  based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.android.tiempobus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import alberapps.android.tiempobus.actionbar.ActionBarActivityFragments;
import alberapps.android.tiempobus.alarma.GestionarAlarmas;
import alberapps.android.tiempobus.barcode.IntentIntegrator;
import alberapps.android.tiempobus.barcode.IntentResult;
import alberapps.android.tiempobus.barcode.Utilidades;
import alberapps.android.tiempobus.favoritos.FavoritoNuevoActivity;
import alberapps.android.tiempobus.favoritos.FavoritosActivity;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.mapas.MapasActivity;
import alberapps.android.tiempobus.noticias.NoticiasTabsPager;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.principal.GestionarFondo;
import alberapps.android.tiempobus.principal.GestionarWidget;
import alberapps.android.tiempobus.principal.TiemposAdapter;
import alberapps.android.tiempobus.service.TiemposForegroundService;
import alberapps.android.tiempobus.tasks.LoadTiemposAsyncTask;
import alberapps.android.tiempobus.tasks.LoadTiemposAsyncTask.LoadTiemposAsyncTaskResponder;
import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.DatosRespuesta;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivityFragments implements TextToSpeech.OnInitListener {

	// Novedades
	// private int REV_ACTUAL = 27;
	// private String NOVEDADES = "";
	// Fin novedades

	protected static final int SUB_ACTIVITY_REQUEST_POSTE = 1000;
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
	protected static final int MSG_FRECUENCIAS_ACTUALIZADAS = 202;
	protected static final int MSG_RECARGA = 203;
	private static final long DELAY_RECARGA = 750;

	private ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();
	private TiemposAdapter posteAdapter;
	private TextView guiHora;
	private TextView datosParada;

	Calendar ahora = new GregorianCalendar();
	private int paradaActual = 4450;
	final ParadaActualHandler handler = new ParadaActualHandler();

	private TiemposUpdater posteUpdater = new TiemposUpdater();
	AlarmManager alarmManager;
	private ImageButton botonCargaTiempos;

	BusLlegada busSeleccionado = null;

	SharedPreferences preferencias = null;

	private TextToSpeech mTts;

	private boolean lecturaOK = true;
	private boolean lecturaAlternativa = false;

	DatosPantallaPrincipal datosPantallaPrincipal;

	GestionarFondo gestionarFondo;

	GestionarAlarmas gestionarAlarmas;

	GestionarWidget gestionarWidget;

	private ListView tiemposView;

	// drawer
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerTitles;

	AsyncTask<Object, Void, DatosRespuesta> loadTiemposTask = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pantalla_principal);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			iniciarDrawer(savedInstanceState);
		}

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		// Delegate gestion
		datosPantallaPrincipal = new DatosPantallaPrincipal(this, preferencias);
		gestionarFondo = new GestionarFondo(this, preferencias);
		gestionarWidget = new GestionarWidget(this, preferencias);

		cambiarLocale(false);

		setupView();

		showProgressBar(true);

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

		mTts = new TextToSpeech(this, this // TextToSpeech.OnInitListener
		);

		// Avisos
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		// Gestionar alarmas
		gestionarAlarmas = new GestionarAlarmas(this, preferencias, alarmManager);

		// NOTICIAS
		boolean verificaNoticias = preferencias.getBoolean("aviso_noticias", true);

		if (verificaNoticias) {
			datosPantallaPrincipal.verificarNuevasNoticias();
		}

	}

	/**
	 * Drawer Layout
	 * 
	 * @param savedInstanceState
	 */
	private void iniciarDrawer(Bundle savedInstanceState) {
		mTitle = mDrawerTitle = getTitle();
		mDrawerTitles = getResources().getStringArray(R.array.menu_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		if (savedInstanceState == null) {
			// selectItem(0);
		}

	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);

		}
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

			// If the nav drawer is open, hide action items related to the
			// content view
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
			menu.findItem(R.id.menu_search).setVisible(!drawerOpen);
			menu.findItem(R.id.menu_refresh).setVisible(!drawerOpen);
		}

		return super.onPrepareOptionsMenu(menu);

	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		// FragmentPrincipalDrawer fragment = new FragmentPrincipalDrawer();
		// Bundle args = new Bundle();
		// args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		// fragment.setArguments(args);

		// FragmentManager fragmentManager = getFragmentManager();
		// fragmentManager.beginTransaction().replace(R.id.content_frame,
		// fragment).commit();

		// update selected item and title, then close the drawer
		// mDrawerList.setItemChecked(position, true);
		// setTitle(mDrawerTitles[position]);

		switch (position) {

		case 0:

			detenerTareaTiempos();
			startActivityForResult(new Intent(MainActivity.this, MapasActivity.class), SUB_ACTIVITY_REQUEST_POSTE);

			break;

		case 1:

			detenerTareaTiempos();
			startActivity(new Intent(MainActivity.this, NoticiasTabsPager.class));
			break;

		case 2:
			detenerTareaTiempos();
			startActivityForResult(new Intent(MainActivity.this, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_POSTE);
			break;

		case 3:
			detenerTareaTiempos();
			nuevoFavorito();
			break;

		case 4:

			detenerTareaTiempos();
			startActivityForResult(new Intent(MainActivity.this, PreferencesFromXml.class), SUB_ACTIVITY_REQUEST_PREFERENCIAS);
			break;

		case 5:

			gestionarFondo.seleccionarFondo();

			break;

		}
		;

		mDrawerList.setItemChecked(position, false);
		mDrawerLayout.closeDrawer(mDrawerList);

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

		handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

		// Poner en campo de poste
		EditText txtPoste = (EditText) findViewById(R.id.campo_poste);
		txtPoste.setText(Integer.toString(paradaActual));

	}

	@Override
	protected void onStop() {

		handler.removeMessages(MSG_RECARGA);

		super.onStop();

	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}

		detenerTareaTiempos();

		super.onDestroy();
	}

	public void detenerTareaTiempos() {

		if (loadTiemposTask != null && loadTiemposTask.getStatus() == Status.RUNNING) {

			loadTiemposTask.cancel(true);

			Log.d("tiempos", "Cancelada task tiempos");

		}

	}

	@Override
	public void finish() {

		// Guardar ultima parada seleccionada
		SharedPreferences.Editor editor = preferencias.edit();
		editor.putInt("parada_inicio", paradaActual);
		editor.commit();

		handler.removeMessages(MSG_RECARGA);

		super.finish();

	}

	/**
	 * Despues de crear la actividad
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Una vez cargado todo... recargamos datos
		handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

		controlMostrarNovedades();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			mDrawerToggle.syncState();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main, menu);

		// Calling super after populating the menu is necessary here to ensure
		// that the
		// action bar helpers have a chance to handle this event.
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			// Pass any configuration change to the drawer toggls
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			// Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_refresh:

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

			break;

		case R.id.menu_search:

			detenerTareaTiempos();
			startActivityForResult(new Intent(MainActivity.this, InfoLineasTabsPager.class), SUB_ACTIVITY_REQUEST_POSTE);

			break;

		case R.id.menu_preferencias:

			detenerTareaTiempos();
			startActivityForResult(new Intent(MainActivity.this, PreferencesFromXml.class), SUB_ACTIVITY_REQUEST_PREFERENCIAS);
			break;

		case R.id.menu_guardar:
			detenerTareaTiempos();
			nuevoFavorito();
			break;

		case R.id.menu_favoritos:
			detenerTareaTiempos();
			startActivityForResult(new Intent(MainActivity.this, FavoritosActivity.class), SUB_ACTIVITY_REQUEST_POSTE);
			break;
		case R.id.menu_noticias:

			detenerTareaTiempos();
			startActivity(new Intent(MainActivity.this, NoticiasTabsPager.class));
			break;
		case R.id.menu_mapas:

			detenerTareaTiempos();
			startActivityForResult(new Intent(MainActivity.this, MapasActivity.class), SUB_ACTIVITY_REQUEST_POSTE);

			break;

		case R.id.menu_fondo:

			gestionarFondo.seleccionarFondo();

			break;

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Configura los elementos de la GUI
	 */
	private void setupView() {

		guiHora = (TextView) findViewById(R.id.ultima_act);

		datosParada = (TextView) findViewById(R.id.datos_parada);

		// Fondo
		gestionarFondo.setupFondoAplicacion();

		/**
		 * Configuramos la lista de resultados
		 */
		posteAdapter = new TiemposAdapter(this, R.layout.tiempos_item);

		// registerForContextMenu(getListView());

		// Pie para la lista de resultados
		LayoutInflater li = LayoutInflater.from(this);
		View v = li.inflate(R.layout.tiempos_aviso, null);

		tiemposView = (ListView) findViewById(R.id.lista_tiempos);

		tiemposView.addFooterView(v);

		// Progreso inicial
		TextView vacio = (TextView) findViewById(R.id.tiempos_vacio);
		vacio.setVisibility(View.INVISIBLE);
		ProgressBar lpb = (ProgressBar) findViewById(R.id.tiempos_progreso);
		lpb.setIndeterminate(true);
		tiemposView.setEmptyView(lpb);

		// Al pulsar sobre un item abriremos el dialogo de poner alarma
		tiemposView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> view, View arg1, int position, long arg3) {
				BusLlegada bus = (BusLlegada) view.getItemAtPosition(position);

				if (bus != null) {
					// setAlarm(bus);
					busSeleccionado = bus;
					// openContextMenu(getListView());

					opcionesLineaSeleccionada();

				}
			}
		});

		tiemposView.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View view) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		// Asignamos el adapter a la lista
		tiemposView.setAdapter(posteAdapter);
		posteAdapter.notifyDataSetChanged();

		// registerForContextMenu(guiTitulo);

		/**
		 * Definimos el comportamiento de los botones
		 */

		// boton poste
		botonCargaTiempos = (ImageButton) findViewById(R.id.boton_subposte);
		botonCargaTiempos.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {
				// A peticion de los usuarios volvemos a sacar el buscador aquí
				// launchPoste();
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

				datosPantallaPrincipal.cargarModalInfo(paradaActual);

			}
		});

	}

	/**
	 * Menu de seleccion de linea
	 * 
	 */
	private void opcionesLineaSeleccionada() {

		List<CharSequence> itemsL = new ArrayList<CharSequence>();
		itemsL.add(getString(R.string.menu_alarma));
		itemsL.add(getString(R.string.menu_share));
		itemsL.add(getString(R.string.menu_ver_en_mapa));
		itemsL.add(getString(R.string.menu_leer));

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !datosPantallaPrincipal.esTram(paradaActual)) {

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
					datosPantallaPrincipal.shareBus(busSeleccionado, paradaActual);
					busSeleccionado = null;
					break;

				case 2:

					if (busSeleccionado.getLinea() != null && !busSeleccionado.getLinea().equals("")) {
						Intent i = new Intent(MainActivity.this, MapasActivity.class);
						i.putExtra("LINEA_MAPA", busSeleccionado.getLinea());
						startActivityForResult(i, SUB_ACTIVITY_REQUEST_POSTE);
					}

					busSeleccionado = null;
					break;

				case 3:
					cantarLinea();
					busSeleccionado = null;
					break;

				case 4:
					gestionarWidget.enviarAWidget(busSeleccionado, paradaActual);
					busSeleccionado = null;
					break;
				}
				;

			}
		});

		AlertDialog alert = builder.create();

		alert.show();
	}

	/**
	 * Prepara la linea a leer
	 * 
	 */
	private void cantarLinea() {

		if (lecturaOK) {

			String lineaALeer = getString(R.string.leer_1) + " " + busSeleccionado.getLinea() + " " + getString(R.string.leer_2) + " " + busSeleccionado.getDestino() + " " + getString(R.string.leer_3) + " "
					+ busSeleccionado.getProximoMinutos().toString() + " " + getString(R.string.leer_4);

			textToSpeech(lineaALeer);

		} else if (lecturaAlternativa) {

			Toast.makeText(this, getString(R.string.leer_ko_2), Toast.LENGTH_SHORT).show();

			String lineaALeer = "El autobús de la línea " + busSeleccionado.getLinea() + " con destino " + busSeleccionado.getDestino() + " llegará en " + busSeleccionado.getProximoMinutos().toString() + " minutos";

			textToSpeech(lineaALeer);

		} else {

			Toast.makeText(this, getString(R.string.leer_ko), Toast.LENGTH_SHORT).show();

		}

	}

	/**
	 * Dialogo con las novedades de la version
	 * 
	 */
	private void controlMostrarNovedades() {
		// Mostrar novedades

		/*
		 * int revAviso = preferencias.getInt("revAviso", 0);
		 * 
		 * if (revAviso < REV_ACTUAL) {
		 * 
		 * AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		 * 
		 * dialog.setTitle(getString(R.string.novedades_titulo));
		 * 
		 * dialog.setMessage(NOVEDADES);
		 * dialog.setIcon(R.drawable.ic_tiempobus_3);
		 * 
		 * dialog.setPositiveButton(R.string.ok, new
		 * DialogInterface.OnClickListener() {
		 * 
		 * public void onClick(DialogInterface dialog, int id) {
		 * 
		 * dialog.dismiss();
		 * 
		 * }
		 * 
		 * });
		 * 
		 * dialog.show();
		 * 
		 * SharedPreferences.Editor editor = preferencias.edit();
		 * editor.putInt("revAviso", REV_ACTUAL); editor.commit();
		 * 
		 * }
		 */

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
					gestionarFondo.activarNuevoFondo(selectedImage);
				} catch (Exception e) {

					Toast.makeText(this, getString(R.string.error_fichero), Toast.LENGTH_SHORT).show();

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
		HashSet<String> h = new HashSet<String>();
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

			Toast.makeText(this, getResources().getText(R.string.aviso_recarga), Toast.LENGTH_SHORT).show();

			getActionBarHelper().setRefreshActionItemState(true);

		} else {

			getActionBarHelper().setRefreshActionItemState(false);

		}
	}

	/**
	 * Actualizar datos tiempos
	 */
	class TiemposUpdater implements Runnable {
		public void run() {
			try {

				showProgressBar(true);

				/**
				 * Sera llamado cuando la tarea de cargar tiempos termine
				 */
				LoadTiemposAsyncTaskResponder loadTiemposAsyncTaskResponder = new LoadTiemposAsyncTaskResponder() {
					public void tiemposLoaded(DatosRespuesta datosRespuesta) {

						ArrayList<BusLlegada> tiempos = null;

						if (datosRespuesta != null) {
							tiempos = datosRespuesta.getListaBusLlegada();
						}

						if (tiempos != null) {
							buses = tiempos;
							handler.sendEmptyMessage(MSG_FRECUENCIAS_ACTUALIZADAS);
							showProgressBar(false);
						} else {
							// Error al recuperar datos
							showProgressBar(false);
							handler.sendEmptyMessage(MSG_ERROR_TIEMPOS);
						}

						// Quitar barra progreso inicial
						ProgressBar lpb = (ProgressBar) findViewById(R.id.tiempos_progreso);
						lpb.clearAnimation();
						lpb.setVisibility(View.INVISIBLE);

						if (tiempos == null || tiempos.isEmpty()) {
							TextView vacio = (TextView) findViewById(R.id.tiempos_vacio);
							tiemposView.setEmptyView(vacio);
						}

						if (datosRespuesta != null && datosRespuesta.getError() != null && datosRespuesta.getError().equals(TiempoBusException.ERROR_STATUS_SERVICIO)) {

							Toast.makeText(getApplicationContext(), getString(R.string.error_status), Toast.LENGTH_SHORT).show();

						}

					}
				};

				// Control de disponibilidad de conexion
				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					loadTiemposTask = new LoadTiemposAsyncTask(loadTiemposAsyncTaskResponder).execute(paradaActual, getApplicationContext());
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
					showProgressBar(false);
				}

			} catch (Exception e) {
				handler.sendEmptyMessage(MSG_ERROR_TIEMPOS);
			}

		}
	}

	/**
	 * Manejar mensajes
	 */
	class ParadaActualHandler extends Handler {
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case MSG_ERROR_TIEMPOS:
				Toast toast = Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_tiempos), Toast.LENGTH_LONG);
				toast.show();
				showProgressBar(false);
				break;

			case MSG_CLOSE_CARGANDO:
				showProgressBar(false);
				break;

			case MSG_RECARGA:

				removeCallbacks(posteUpdater);
				removeMessages(MSG_RECARGA);
				post(posteUpdater);
				sendEmptyMessageDelayed(MSG_RECARGA, 60 * 1000);
				break;

			case MSG_FRECUENCIAS_ACTUALIZADAS:

				String cabdatos = "";
				String cabdatos2 = "";

				cabdatos2 = datosPantallaPrincipal.cargarDescripcionBD(paradaActual);

				cabdatos = datosPantallaPrincipal.cargarDescripcion(Integer.toString(paradaActual));

				if (cabdatos.equals("")) {

					cabdatos = cabdatos2;

				}

				if (cabdatos.equals("")) {
					cabdatos = getString(R.string.share_0b) + " " + paradaActual;
				}

				if (datosPantallaPrincipal.esTram(paradaActual)) {
					cabdatos = "TRAM " + cabdatos;
				}

				datosParada.setText(cabdatos);

				final Calendar c = Calendar.getInstance();

				SimpleDateFormat df = new SimpleDateFormat("HH:mm");

				String updated = df.format(c.getTime()).toString();

				guiHora.setText(updated);

				// Limpiamos la lista
				posteAdapter.clear();

				// La rellenamos con los nuevos datos
				if (buses != null && buses.size() > 0) {
					int n = buses.size();

					for (int i = 0; i < n; i++) {
						posteAdapter.add(buses.get(i));
					}
				}

				posteAdapter.notifyDataSetChanged();
				break;

			}

			// Si no hay atuomatico, se cancela el mensaje
			boolean auto = preferencias.getBoolean("checkbox_preference", true);

			if (!auto) {
				handler.removeMessages(MSG_RECARGA);
			}

		}
	}

	public void onInit(int status) {

		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will
			// indicate this.
			int result = mTts.setLanguage(Locale.getDefault());
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.

				lecturaOK = false;

				Locale loc = new Locale("spa", "ES");

				result = mTts.setLanguage(loc);

				if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
					lecturaAlternativa = false;
				} else {
					lecturaAlternativa = true;
				}

			} else {
				// Check the documentation for other possible result codes.
				// For example, the language may be available for the locale,
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

	}

	/**
	 * Texto a leer
	 * 
	 * @param texto
	 */
	private void textToSpeech(String texto) {

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

}
