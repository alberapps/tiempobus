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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import alberapps.android.tiempobus.actionbar.ActionBarActivity;
import alberapps.android.tiempobus.barcode.IntentIntegrator;
import alberapps.android.tiempobus.barcode.IntentResult;
import alberapps.android.tiempobus.barcode.Utilidades;
import alberapps.android.tiempobus.data.PosteAdapter;
import alberapps.android.tiempobus.data.TiempoBusDb;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;
import alberapps.android.tiempobus.favoritos.FavoritoNuevoActivity;
import alberapps.android.tiempobus.favoritos.FavoritosActivity;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.mapas.MapasActivity;
import alberapps.android.tiempobus.noticias.NoticiasTabsPager;
import alberapps.android.tiempobus.service.TiemposForegroundService;
import alberapps.android.tiempobus.tasks.LoadNoticiasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadNoticiasAsyncTask.LoadNoticiasAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadTiemposAsyncTask;
import alberapps.android.tiempobus.tasks.LoadTiemposAsyncTask.LoadTiemposAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadTiemposLineaParadaAsyncTask;
import alberapps.android.tiempobus.tasks.LoadTiemposLineaParadaAsyncTask.LoadTiemposLineaParadaAsyncTaskResponder;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.noticias.Noticias;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {

	// Novedades
	private int REV_ACTUAL = 27;
	private String NOVEDADES = "(2.4.3)\n**Acceso al nuevo TiempoBus Widgets(Android 3.0 y sup) \n1. Información extra de líneas y paradas.\n2. Carga dínamica de lista de líneas desde subus.es.\n3. Mejoras en el cliente de Twitter y nuevas fuentes.\n4. Actualización de la Base de Datos.\n5. Resolución de bugs.";
	// Fin novedades

	protected static final int SUB_ACTIVITY_REQUEST_POSTE = 1000;
	public static final int SUB_ACTIVITY_REQUEST_ADDFAV = 1001;
	public static final int SUB_ACTIVITY_RESULT_OK = 1002;
	public static final int SUB_ACTIVITY_RESULT_CANCEL = 1003;
	protected static final int SUB_ACTIVITY_REQUEST_PREFERENCIAS = 1004;
	protected static final int SUB_ACTIVITY_REQUEST_NOTICIAS = 1005;

	protected static final int VOICE_CHECK_CODE = 3000;

	protected static final int CARGAR_IMAGEN = 2000;

	protected static final int DIALOG_CARGANDO = 100;

	protected static final int MSG_CLOSE_CARGANDO = 200;
	protected static final int MSG_ERROR_TIEMPOS = 201;
	protected static final int MSG_FRECUENCIAS_ACTUALIZADAS = 202;
	protected static final int MSG_RECARGA = 203;
	private static final long DELAY_RECARGA = 750;

	private ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();
	private PosteAdapter posteAdapter;
	private TextView guiHora;
	private TextView datosParada;

	Calendar ahora = new GregorianCalendar();
	private int poste = 4450;
	final PosteHandler handler = new PosteHandler();

	private TiemposUpdater posteUpdater = new TiemposUpdater();
	AlarmManager alarmManager;
	private ImageButton botonCargaTiempos;

	BusLlegada busSeleccionado = null;

	SharedPreferences preferencias = null;

	private TextToSpeech mTts;

	private boolean lecturaOK = true;
	private boolean lecturaAlternativa = false;

	PendingIntent alarmReceiver = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		cambiarLocale(false);

		setContentView(R.layout.pantalla_principal);

		showProgressBar(true);

		setupView();
 
		// Verificar si hay parada por defecto
		if (preferencias.contains("parada_inicio")) {
			poste = preferencias.getInt("parada_inicio", poste);
		}

		if (savedInstanceState != null) {
			poste = savedInstanceState.getInt("poste");

			SharedPreferences.Editor editor = preferencias.edit();
			editor.putInt("parada_inicio", poste);
			editor.commit();
		}

		mTts = new TextToSpeech(this, this // TextToSpeech.OnInitListener
		);

		// Avisos
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		// NOTICIAS
		boolean verificaNoticias = preferencias.getBoolean("aviso_noticias", true);

		if (verificaNoticias) {
			verificarNuevasNoticias();
		}

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
			poste = preferencias.getInt("parada_inicio", poste);
		}

		handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

		// Poner en campo de poste
		EditText txtPoste = (EditText) findViewById(R.id.campo_poste);
		txtPoste.setText(Integer.toString(poste));

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

		super.onDestroy();
	}

	@Override
	public void finish() {

		// Guardar ultima parada seleccionada
		SharedPreferences.Editor editor = preferencias.edit();
		editor.putInt("parada_inicio", poste);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_refresh:

			EditText txtPoste = (EditText) findViewById(R.id.campo_poste);

			try {
				int tmpPoste = Integer.parseInt(txtPoste.getText().toString());
				if (tmpPoste > 0 && tmpPoste < 9999) {
					poste = tmpPoste;
					handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);

				}
			} catch (NumberFormatException e) {
				// No hay numero. Recargar con el ultimo usado
				handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);
			}

			break;

		case R.id.menu_search:

			launchSearchOnline();

			break;

		case R.id.menu_preferencias:
			showPreferencias();
			break;

		case R.id.menu_guardar:
			launchNuevoFavorito();
			break;

		case R.id.menu_favoritos:
			launchFavoritos();
			break;
		case R.id.menu_noticias:
			launchNoticias();
			break;
		case R.id.menu_mapas:
			launchMapas();
			break;

		case R.id.menu_fondo:

			seleccionarFondo();

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
		setupFondoAplicacion();

		/**
		 * Configuramos la lista de resultados
		 */
		posteAdapter = new PosteAdapter(this, R.layout.tiempos_item);

		// registerForContextMenu(getListView());

		// Pie para la lista de resultados
		LayoutInflater li = LayoutInflater.from(this);
		View v = li.inflate(R.layout.tiempos_aviso, null);
		getListView().addFooterView(v);

		// Al pulsar sobre un item abriremos el dialogo de poner alarma
		getListView().setOnItemClickListener(new OnItemClickListener() {
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

		getListView().setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View view) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		// Asignamos el adapter a la lista
		setListAdapter(posteAdapter);
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
						poste = tmpPoste;

						SharedPreferences.Editor editor = preferencias.edit();
						editor.putInt("parada_inicio", poste);
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

				mostrarModalAlertas();

			}
		});

		// //Info

		ImageButton botonInfo = (ImageButton) findViewById(R.id.boton_info);
		botonInfo.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {

				cargarModalInfo();

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
					setAlarm(busSeleccionado);
					busSeleccionado = null;
					break;

				case 1:
					shareBus();
					busSeleccionado = null;
					break;

				case 2:
					launchMapasSeleccion(busSeleccionado.getLinea());
					busSeleccionado = null;
					break;

				case 3:
					cantarLinea();
					busSeleccionado = null;
					break;

				case 4:
					enviarAWidget();
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

		int revAviso = preferencias.getInt("revAviso", 0);

		if (revAviso < REV_ACTUAL) {

			AlertDialog.Builder dialog = new AlertDialog.Builder(this);

			dialog.setTitle(getString(R.string.novedades_titulo));

			dialog.setMessage(NOVEDADES);
			dialog.setIcon(R.drawable.ic_tiempobus);

			dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					dialog.dismiss();

				}

			});

			dialog.show();

			SharedPreferences.Editor editor = preferencias.edit();
			editor.putInt("revAviso", REV_ACTUAL);
			editor.commit();

		}

	}

	/**
	 * Modal con informacion de la alarma activa
	 * 
	 */
	private void mostrarModalAlertas() {

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle(getString(R.string.alarma_modal));

		// String aviso = preferenciasAlertas.getString("alerta", "");

		String aviso = PreferencesUtil.getAlertaInfo(this);

		if (aviso != null && !aviso.equals("")) {

			String[] datos = aviso.split(";");

			String alertaDialog = getString(R.string.alarma_establecida_linea) + ": " + datos[0] + "\n" + getString(R.string.alarma_establecida_parada) + ": " + datos[1] + "\n"
					+ getString(R.string.alarma_establecida_hora) + ": " + datos[2] + "\n" + getString(R.string.alarma_que_tiempo) + ": " + datos[3] + "\n" + "\n" + getString(R.string.alarma_auto_aviso);

			// dialog.setMessage(alertaDialog);
			dialog.setIcon(R.drawable.ic_alarm_modal);

			LayoutInflater li = getLayoutInflater();
			View vista = li.inflate(R.layout.alertas_info, null, false);

			TextView texto = (TextView) vista.findViewById(R.id.textAlerta);

			texto.setText(alertaDialog);

			dialog.setView(vista);

			CheckBox check = (CheckBox) vista.findViewById(R.id.checkBoxAlerta);
			boolean checkActivo = preferencias.getBoolean("activarServicio", false);
			check.setChecked(checkActivo);

			dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}

			});

			dialog.setNegativeButton(R.string.menu_cancelar_alarma, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					Intent intent = new Intent(TiemposForegroundService.ACTION_FOREGROUND);
					intent.setClass(MainActivity.this, TiemposForegroundService.class);

					stopService(intent);

					cancelarAlarmas(true);
				}

			});

			dialog.show();

		} else {

			Toast.makeText(this, getString(R.string.alarma_activa_no), Toast.LENGTH_SHORT).show();

		}

	}

	public void alertaServiceClick(View view) {

		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		// Check which checkbox was clicked
		switch (view.getId()) {
		case R.id.checkBoxAlerta:
			if (checked) {

				Toast.makeText(this, "activar", Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(TiemposForegroundService.ACTION_FOREGROUND);
				intent.setClass(MainActivity.this, TiemposForegroundService.class);
				intent.putExtra("PARADA", poste);

				SharedPreferences.Editor editor = preferencias.edit();
				editor.putBoolean("activarServicio", true);
				editor.commit();

				startService(intent);

			} else {
				Toast.makeText(this, "desactivar", Toast.LENGTH_SHORT).show();

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
		savedInstanceState.putInt("poste", poste);

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
			poste = savedInstanceState.getInt("poste");

		}
	}

	/**
	 * Enviar la parada al widget
	 */
	private void enviarAWidget() {

		if (UtilidadesUI.verificarWidgetInstalado(this)) {

			Intent intent = new Intent();

			intent.setComponent(new ComponentName(UtilidadesUI.WIDGET_PACKAGE, UtilidadesUI.WIDGET_ACTIVITY));

			// 24,2902;10,2902

			intent.putExtra("datos_linea", busSeleccionado.getLinea() + "," + poste);

			startActivity(intent);

		} else {

			AlertDialog.Builder downloadDialog = new AlertDialog.Builder(this);
			downloadDialog.setTitle(getString(R.string.menu_widget));
			downloadDialog.setMessage(getString(R.string.widget_instalar));
			downloadDialog.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialogInterface, int i) {
					Uri uri = Uri.parse("market://details?id=" + UtilidadesUI.WIDGET_PACKAGE);
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException anfe) {

						Toast.makeText(getApplicationContext(), getString(R.string.widget_market), Toast.LENGTH_SHORT).show();
					}
				}
			});
			downloadDialog.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialogInterface, int i) {
				}
			});

			downloadDialog.show();

		}

	}

	/**
	 * Compartir informacion del bus
	 */
	private void shareBus() {

		// String devuelto

		String mensaje = getResources().getText(R.string.share_0) + " " + getResources().getText(R.string.share_0b) + " " + poste + " " + getResources().getText(R.string.share_1) + " " + busSeleccionado.getLinea() + " "
				+ getResources().getText(R.string.share_2) + " " + busSeleccionado.getDestino() + " " + getResources().getText(R.string.share_3) + " " + formatearShare(busSeleccionado.getProximo());

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.menu_share)));

	}

	/**
	 * Formatea la salida por idioma
	 * 
	 * @param proximo
	 * @return
	 */
	private String formatearShare(String proximo) {

		String traducido = "";

		String[] procesa = proximo.split(";");

		String tiempo1 = "";
		String tiempo2 = "";

		if (procesa[0].equals("enlaparada")) {

			tiempo1 = (String) getResources().getText(R.string.tiempo_m_1);

		} else if (procesa[0].equals("sinestimacion")) {

			tiempo1 = (String) getResources().getText(R.string.tiempo_m_2);

		} else {

			tiempo1 = procesa[0];

		}

		if (procesa[1].equals("enlaparada")) {

			tiempo2 = (String) getResources().getText(R.string.tiempo_m_1);

		} else if (procesa[1].equals("sinestimacion")) {

			tiempo2 = (String) getResources().getText(R.string.tiempo_m_2);

		} else {

			tiempo2 = procesa[1];

		}

		traducido = tiempo1 + " " + getResources().getText(R.string.tiempo_m_3) + " " + tiempo2;

		return traducido;

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
						poste = b.getInt("POSTE");
					}
				}

				// Poner en campo de poste
				EditText txtPoste = (EditText) findViewById(R.id.campo_poste);
				txtPoste.setText(Integer.toString(poste));

				SharedPreferences.Editor editor = preferencias.edit();
				editor.putInt("parada_inicio", poste);
				editor.commit();

				handler.sendEmptyMessageDelayed(MSG_RECARGA, DELAY_RECARGA);
				break;
			case SUB_ACTIVITY_REQUEST_ADDFAV:
				launchFavoritos();
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
				setupFondoAplicacion();

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

						poste = Integer.parseInt(parada);

						// Poner en campo de poste
						EditText txtPoste = (EditText) findViewById(R.id.campo_poste);
						txtPoste.setText(Integer.toString(poste));

						SharedPreferences.Editor editor = preferencias.edit();
						editor.putInt("parada_inicio", poste);
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
				Uri selectedImage = data.getData();

				// Cargamos imagen seleccionada
				activarNuevoFondo(selectedImage);

			}

	}

	/**
	 * Abre modal de alarmas
	 * 
	 * @param bus
	 */
	private void setAlarm(BusLlegada bus) {

		mostrarModalTiemposAlerta(bus);

	}

	/**
	 * Nuevo selector de tiempos
	 * 
	 * @param bus
	 */
	private void mostrarModalTiemposAlerta(BusLlegada bus) {

		final BusLlegada theBus = bus;

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle(getString(R.string.tit_choose_alarm));

		LayoutInflater li = getLayoutInflater();
		View vista = li.inflate(R.layout.seleccionar_tiempo, null, false);

		final Spinner spinner = (Spinner) vista.findViewById(R.id.spinner_tiempos);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_minutos, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);

		dialog.setView(vista);

		dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				// Anular si existe una alarma anterior
				cancelarAlarmas(false);

				int seleccion = spinner.getSelectedItemPosition();

				calcularAlarma(theBus, 1, seleccion);

				Intent intent = new Intent(TiemposForegroundService.ACTION_FOREGROUND);
				intent.setClass(MainActivity.this, TiemposForegroundService.class);
				intent.putExtra("PARADA", poste);

				boolean checkActivo = preferencias.getBoolean("activarServicio", false);
				if (checkActivo) {
					startService(intent);
				}

				dialog.dismiss();

				mostrarModalAlertas();

			}

		});

		dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				dialog.dismiss();

			}

		});

		dialog.show();

	}

	/**
	 * Calcula y estable la alarma
	 * 
	 * @param theBus
	 * @param tiempo
	 * @param item
	 */
	private void calcularAlarma(BusLlegada theBus, int tiempo, int item) {

		Context context = getApplicationContext();

		Intent intent = new Intent(context, AlarmReceiver.class);

		long et;

		long mins = ((item + 1) * 5);

		// Que tiempo usar
		// Si el primer bus no cumple, se usa el segundo
		if (theBus.getProximoMinutos() < mins) {
			et = theBus.getSiguienteMinutos();
			tiempo = 2;
		} else {
			et = theBus.getProximoMinutos();
			tiempo = 1;
		}

		// Control de tiempo insuficiente o excesivo
		if (et < mins) {
			Toast.makeText(context, String.format(getString(R.string.err_bus_cerca), et), Toast.LENGTH_SHORT).show();
			return;
		} else if (et == 9999) {
			Toast.makeText(context, String.format(getString(R.string.err_bus_sin), et), Toast.LENGTH_SHORT).show();
			return;
		}

		String txt = String.format(getString(R.string.alarm_bus), "" + theBus.getLinea(), "" + poste);
		intent.putExtra("alarmTxt", txt);
		intent.putExtra("poste", poste);

		alarmReceiver = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

		Date actual = new Date();

		long milisegundos = (actual.getTime() + (et * 60000)) - (mins * 60000);

		alarmManager.set(AlarmManager.RTC_WAKEUP, milisegundos, alarmReceiver);

		SimpleDateFormat ft = new SimpleDateFormat("HH:mm");

		String horaT = ft.format(milisegundos);

		String alertaDialog = theBus.getLinea() + ";" + poste + ";" + horaT + ";" + tiempo + ";" + item + ";" + milisegundos;

		// SharedPreferences preferenciasAlertas =
		// context.getSharedPreferences("prefalertas",
		// Context.MODE_MULTI_PROCESS);

		// SharedPreferences.Editor editor = preferenciasAlertas.edit();
		// editor.putString("alerta", alertaDialog);
		// editor.commit();

		PreferencesUtil.putAlertaInfo(this, alertaDialog);

	}

	/**
	 * Cancelar alarmas establecidas
	 */
	private void cancelarAlarmas(boolean avisar) {

		// Cancelar posible notificacion
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(ns);
		mNotificationManager.cancel(AlarmReceiver.ALARM_ID);

		// Cancelar alarma si hay una definida
		if (alarmReceiver != null) {

			alarmManager.cancel(alarmReceiver);

			alarmReceiver.cancel();

			alarmReceiver = null;

			// SharedPreferences preferenciasAlertas =
			// getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);

			// SharedPreferences.Editor editor = preferenciasAlertas.edit();
			// editor.putString("alerta", "");
			// editor.commit();

			PreferencesUtil.clearAlertaInfo(this);

			if (avisar) {
				Toast.makeText(this, getString(R.string.alarma_cancelada), Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * Lanza la subactivididad de favoritos
	 */
	private void launchFavoritos() {
		Intent i = new Intent(MainActivity.this, FavoritosActivity.class);
		startActivityForResult(i, SUB_ACTIVITY_REQUEST_POSTE);
	}

	/**
	 * Lanza la subactivididad de preferencias
	 */
	private void showPreferencias() {
		Intent i = new Intent(MainActivity.this, PreferencesFromXml.class);

		startActivityForResult(i, SUB_ACTIVITY_REQUEST_PREFERENCIAS);

	}

	/**
	 * Lanza la subactivididad de noticias
	 */
	private void launchNoticias() {

		Intent i = new Intent(MainActivity.this, NoticiasTabsPager.class);
		startActivity(i);

	}

	/**
	 * Lanza la subactividad de anadir favorito. Le pasa el poste y la
	 * descripcion
	 */
	private void launchNuevoFavorito() {
		Intent i = new Intent(MainActivity.this, FavoritoNuevoActivity.class);

		Bundle extras = new Bundle();
		extras.putInt("POSTE", poste); // Pasamos el poste actual
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
	 * Lanza la subactivididad de mapas
	 */
	private void launchMapas() {
		Intent i = new Intent(MainActivity.this, MapasActivity.class);
		startActivityForResult(i, SUB_ACTIVITY_REQUEST_POSTE);
	}

	private void launchMapasSeleccion(String linea) {

		if (linea != null && !linea.equals("")) {
			Intent i = new Intent(MainActivity.this, MapasActivity.class);
			i.putExtra("LINEA_MAPA", linea);
			startActivityForResult(i, SUB_ACTIVITY_REQUEST_POSTE);
		}
	}

	private void launchSearchOnline() {
		Intent i = new Intent(MainActivity.this, InfoLineasTabsPager.class);
		startActivityForResult(i, SUB_ACTIVITY_REQUEST_POSTE);
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
	 * Clase encargada de coger los datos del poste
	 * 
	 * Es observable y puede ser lanzada en otro thread
	 * 
	 * Modificación para lanazarla de forma asincrona
	 * 
	 */
	class TiemposUpdater implements Runnable {
		public void run() {
			try {

				showProgressBar(true);

				/**
				 * Sera llamado cuando la tarea de cargar tiempos termine
				 */
				LoadTiemposAsyncTaskResponder loadTiemposAsyncTaskResponder = new LoadTiemposAsyncTaskResponder() {
					public void tiemposLoaded(ArrayList<BusLlegada> tiempos) {

						if (tiempos != null) {
							buses = tiempos;
							handler.sendEmptyMessage(MSG_FRECUENCIAS_ACTUALIZADAS);
							showProgressBar(false);
						} else {
							// Error al recuperar datos
							showProgressBar(false);
							handler.sendEmptyMessage(MSG_ERROR_TIEMPOS);
						}
					}
				};

				// Control de disponibilidad de conexion
				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					new LoadTiemposAsyncTask(loadTiemposAsyncTaskResponder).execute(poste);
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
	 * Handler para intercambiar mensajes entre los hilos
	 * 
	 * @author francho
	 * 
	 */
	class PosteHandler extends Handler {
		public void handleMessage(Message msg) {
			// int total = msg.getData().getInt("total");
			// progressDialog.setProgress(total);

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
				// showProgressBar(true);

				removeCallbacks(posteUpdater);
				removeMessages(MSG_RECARGA);
				post(posteUpdater);
				sendEmptyMessageDelayed(MSG_RECARGA, 60 * 1000);
				break;

			case MSG_FRECUENCIAS_ACTUALIZADAS:

				String cabdatos = "";
				String cabdatos2 = "";

				cabdatos2 = cargarDescripcionBD(Integer.toString(poste));

				cabdatos = cargarDescripcion(Integer.toString(poste));

				if (cabdatos.equals("")) {

					cabdatos = cabdatos2;

				}

				if (cabdatos.equals("")) {
					cabdatos = getString(R.string.share_0b) + " " + poste;
				}

				datosParada.setText(cabdatos);

				// setTitle(cabdatos);

				final Calendar c = Calendar.getInstance();

				SimpleDateFormat df = new SimpleDateFormat("HH:mm");
				// String updated =
				// String.format(getString(R.string.updated_at),
				// df.format(c.getTime()));

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

	/**
	 * Verifica si hay nuevas noticias y muestra un aviso
	 * 
	 */
	private void verificarNuevasNoticias() {

		/**
		 * Sera llamado cuando la tarea de cargar las noticias
		 */
		LoadNoticiasAsyncTaskResponder loadNoticiasAsyncTaskResponder = new LoadNoticiasAsyncTaskResponder() {
			public void noticiasLoaded(List<Noticias> noticias) {

				if (noticias != null && !noticias.isEmpty()) {

					String fecha_ultima = "";
					boolean lanzarAviso = false;

					// Ver si se guardo la fecha de la ultima noticia
					if (preferencias.contains("ultima_noticia")) {
						fecha_ultima = preferencias.getString("ultima_noticia", "");

						if (!fecha_ultima.equals(noticias.get(0).getFecha())) {

							lanzarAviso = true;

							SharedPreferences.Editor editor = preferencias.edit();
							editor.putString("ultima_noticia", noticias.get(0).getFecha());
							editor.commit();

						}

					} else {

						SharedPreferences.Editor editor = preferencias.edit();
						editor.putString("ultima_noticia", noticias.get(0).getFecha());
						editor.commit();

					}

					// Si se guardo la fecha y no coincide con la ultima, lanzar
					// aviso
					if (lanzarAviso) {

						Notificaciones.notificacionNoticias(getApplicationContext());

					}
				} else {

				}
			}
		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadNoticiasAsyncTask(loadNoticiasAsyncTaskResponder).execute();
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
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

	/**
	 * Seleccion del fondo de la galeria en el arranque
	 */
	private void setupFondoAplicacion() {

		String fondo_galeria = preferencias.getString("image_galeria", "");

		View contenedor_principal = findViewById(R.id.contenedor_principal);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

	}

	/**
	 * Decidir si galeria o fondo de color
	 * 
	 */
	private void seleccionarFondo() {

		final CharSequence[] items = { getResources().getString(R.string.seleccion_fondo_1), getResources().getString(R.string.seleccion_fondo_2), getResources().getString(R.string.seleccion_fondo_3) };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.preferencias_imagen);

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				if (item == 0) {

					Uri uri = getTempUri();

					if (uri != null) {

						Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
						intent.putExtra("crop", "true");

						intent.setType("image/*");
						intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
						intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

						startActivityForResult(intent, CARGAR_IMAGEN);

					} else {
						Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_fichero), Toast.LENGTH_SHORT).show();
					}

				} else if (item == 1) {
					View contenedor_principal = findViewById(R.id.contenedor_principal);
					contenedor_principal.setBackgroundResource(android.R.color.darker_gray);

					SharedPreferences.Editor editor = preferencias.edit();
					editor.putString("image_galeria", "");
					editor.commit();

					Toast.makeText(getApplicationContext(), getResources().getText(R.string.seleccion_ok), Toast.LENGTH_SHORT).show();
				}

			}
		});

		AlertDialog alert = builder.create();

		alert.show();

	}

	private Uri getTempUri() {

		File file = getTempFile();

		if (file != null) {

			return Uri.fromFile(getTempFile());
		} else {
			return null;
		}
	}

	private File getTempFile() {
		if (isSDCARDMounted()) {

			File directorio = new File(Environment.getExternalStorageDirectory() + "/Android/data/alberapps.android.tiempobus/");
			directorio.mkdirs();

			File f = new File(Environment.getExternalStorageDirectory() + "/Android/data/alberapps.android.tiempobus/", "fotoFondo.jpg");
			try {
				f.createNewFile();
			} catch (IOException e) {

				int i = 1;

			}
			return f;
		} else {
			return null;
		}
	}

	private boolean isSDCARDMounted() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

	/**
	 * cambiar el fondo de pantalla con la galeria
	 * 
	 * @param uri
	 */
	private void activarNuevoFondo(Uri uri) {

		// Uri de la nueva imagen
		File tempFile = getTempFile();

		if (tempFile != null) {

			String filePath = tempFile.getPath();

			// Guardar
			SharedPreferences.Editor editor = preferencias.edit();
			editor.putString("image_galeria", filePath);
			editor.commit();

			setupFondoAplicacion();

			Toast.makeText(this, getResources().getText(R.string.seleccion_ok), Toast.LENGTH_SHORT).show();

		}

	}

	/**
	 * Si la parada esta en favoritos mostramos su titulo
	 * 
	 * @param parada
	 * @return
	 */
	private String cargarDescripcion(String parada) {

		try {
			HashMap<String, String> datosFav = new HashMap<String, String>();

			Cursor cursor = managedQuery(TiempoBusDb.Favoritos.CONTENT_URI, FavoritosActivity.PROJECTION, null, null, TiempoBusDb.Favoritos.DEFAULT_SORT_ORDER);

			if (cursor != null) {

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					datosFav.put(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.POSTE)), cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.TITULO)));
				}

			}

			if (datosFav.containsKey(parada)) {

				return datosFav.get(parada);

			} else {
				return "";
			}

		} catch (Exception e) {
			return "";
		}

	}

	private String cargarDescripcionBD(String parada) {

		try {

			String parametros[] = { Integer.toString(poste) };

			Cursor cursor = managedQuery(BuscadorLineasProvider.DATOS_PARADA_URI, null, null, parametros, null);

			if (cursor != null) {
				List<Parada> listaParadas = new ArrayList<Parada>();

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

					Parada par = new Parada();

					par.setLineaNum(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
					par.setLineaDesc(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
					par.setConexion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
					par.setCoordenadas(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
					par.setDestino(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
					par.setDireccion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
					par.setLatitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
					par.setLongitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
					par.setParada(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));

					listaParadas.add(par);
				}

				return listaParadas.get(0).getDireccion();

			} else {
				return "";

			}

		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * Modal con informacion de la parada
	 */
	private void cargarModalInfo() {

		try {

			String parametros[] = { Integer.toString(poste) };

			Cursor cursor = managedQuery(BuscadorLineasProvider.DATOS_PARADA_URI, null, null, parametros, null);

			if (cursor != null) {
				List<Parada> listaParadas = new ArrayList<Parada>();

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

					Parada par = new Parada();

					par.setLineaNum(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
					par.setLineaDesc(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
					par.setConexion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
					par.setCoordenadas(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
					par.setDestino(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
					par.setDireccion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
					par.setLatitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
					par.setLongitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
					par.setParada(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));

					par.setObservaciones(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_OBSERVACIONES)));

					listaParadas.add(par);
				}

				String descripcionAlert = getResources().getText(R.string.localizacion) + ": " + listaParadas.get(0).getDireccion() + "\n" + getResources().getText(R.string.lineas) + " ";

				if (listaParadas.get(0).getConexion() != null) {
					descripcionAlert += listaParadas.get(0).getConexion().trim();
				}

				descripcionAlert += "\n" + getResources().getText(R.string.observaciones);

				// Observaciones
				for (int i = 0; i < listaParadas.size(); i++) {

					if (listaParadas.get(i).getObservaciones() != null && !listaParadas.get(i).getObservaciones().trim().equals("")) {
						descripcionAlert += "\n[" + listaParadas.get(i).getLineaNum() + "] " + listaParadas.get(i).getObservaciones().trim() + "\n";
					}

				}

				AlertDialog.Builder dialog = new AlertDialog.Builder(this);

				dialog.setTitle(getString(R.string.share_0b) + " " + listaParadas.get(0).getParada());
				dialog.setMessage(descripcionAlert);
				dialog.setIcon(R.drawable.ic_info_modal);

				dialog.show();

			} else {

				Toast.makeText(this, getResources().getText(R.string.error_generico_1), Toast.LENGTH_SHORT).show();

			}

		} catch (Exception e) {
			Toast.makeText(this, getResources().getText(R.string.error_generico_1), Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Recalcular alarma
	 */
	public void refrescarAlarma() {

		try {

			// showProgressBar(true);

			// SharedPreferences preferenciasAlertas =
			// getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);

			// String aviso = preferenciasAlertas.getString("alerta", "");

			String aviso = PreferencesUtil.getAlertaInfo(this);

			/**
			 * Sera llamado cuando la tarea de cargar tiempos termine
			 */
			LoadTiemposLineaParadaAsyncTaskResponder loadTiemposLineaParadaAsyncTaskResponder = new LoadTiemposLineaParadaAsyncTaskResponder() {
				public void tiemposLoaded(BusLlegada tiempos) {

					// SharedPreferences preferencias2 =
					// PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

					// SharedPreferences preferenciasAlertas =
					// getSharedPreferences("prefalertas",
					// Context.MODE_MULTI_PROCESS);

					String aviso = PreferencesUtil.getAlertaInfo(getApplicationContext());

					// String aviso = preferenciasAlertas.getString("alerta",
					// "");

					if (aviso != null && !aviso.equals("") && tiempos != null) {

						String[] datos = aviso.split(";");

						int tiempo = Integer.parseInt(datos[3]);

						int item = Integer.parseInt(datos[4]);

						long milisegundosAlarma = Long.parseLong(datos[5]);

						long mins = ((item + 1) * 5);
						Date actual = new Date();

						boolean cambioTiempo = false;

						// Posible cambio de orden
						// Si estamos en el segundo tiempo y el primero ya es
						// inferior a lo esperado
						if (tiempo == 2 && tiempos.getProximoMinutos() >= mins && tiempos.getSiguienteMinutos() > mins) {
							tiempo = 1;
							cambioTiempo = true;
						}

						Long milisegundosActuales = null;

						if (tiempo == 1) {
							milisegundosActuales = (actual.getTime() + (tiempos.getProximoMinutos() * 60000)) - (mins * 60000);
						} else if (tiempo == 2) {
							milisegundosActuales = (actual.getTime() + (tiempos.getSiguienteMinutos() * 60000)) - (mins * 60000);
						}

						// Verificar si hay que restablecer o se mantiene
						if (tiempo == 1 && (milisegundosAlarma > (milisegundosActuales + 60000) || milisegundosAlarma < (milisegundosActuales - 60000))) {

							calcularAlarma(tiempos, tiempo, item);

							Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_actualizada), Toast.LENGTH_SHORT).show();

						} else if (tiempo == 2 && (milisegundosAlarma > (milisegundosActuales + 60000) || milisegundosAlarma < (milisegundosActuales - 60000))) {

							calcularAlarma(tiempos, tiempo, item);

							Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_actualizada), Toast.LENGTH_SHORT).show();

						} else if (cambioTiempo) {

							calcularAlarma(tiempos, tiempo, item);
							Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_actualizada), Toast.LENGTH_SHORT).show();

						}

					} else {
						if (tiempos == null) {
							Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();
						}
					}

				}

			};

			if (aviso != null && !aviso.equals("")) {

				String[] datos = aviso.split(";");

				// Control de disponibilidad de conexion
				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					new LoadTiemposLineaParadaAsyncTask(loadTiemposLineaParadaAsyncTaskResponder).execute(datos[0], datos[1]);
				} else {
					Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
				}

			}

		} catch (Exception e) {

			Toast.makeText(getApplicationContext(), getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();

		}

	}

}
