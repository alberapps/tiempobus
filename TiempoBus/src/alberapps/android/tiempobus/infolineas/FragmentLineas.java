/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
package alberapps.android.tiempobus.infolineas;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;
import alberapps.android.tiempobus.tasks.LoadDatosInfoLineasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosInfoLineasAsyncTask.LoadDatosInfoLineasAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask.LoadDatosLineasAsyncTaskResponder;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.UtilidadesTAM;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.PlaceMark;
import alberapps.java.tam.webservice.estructura.GetLineasResult;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.util.Utilidades;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragmento de lineas
 * 
 * 
 */
public class FragmentLineas extends Fragment {

	GetLineasResult lineasDummy = null;

	private ListView lineasView;

	ArrayList<BusLinea> lineasBus;

	BusLinea linea = null;

	InfoLineasTabsPager actividad;

	int mCurCheckPosition = 0;

	ProgressDialog dialog = null;

	InfoLineaAdapter infoLineaAdapter;

	InfoLineaParadasAdapter infoLineaParadasAdapter;

	SharedPreferences preferencias;

	/**
	 * On Create
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actividad = (InfoLineasTabsPager) getActivity();

		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {

		if (actividad.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {

			TextView descrip = (TextView) getActivity().findViewById(R.id.tituloAviso);

			descrip.setText(R.string.aviso_offline);

		} else if (actividad.modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {

			TextView descrip = (TextView) getActivity().findViewById(R.id.tituloAviso);

			descrip.setText(R.string.aviso_buscador_offline_tram);

		}

		setupFondoAplicacion();

		// Combo de seleccion de datos
		final Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner_datos);

		ArrayAdapter<CharSequence> adapter = null;

		if (UtilidadesTRAM.ACTIVADO_TRAM) {
			adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_datos, android.R.layout.simple_spinner_item);
		} else {
			adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_datos_b, android.R.layout.simple_spinner_item);
		}

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapter);

		// Seleccion inicial
		int infolineaModo = preferencias.getInt("infolinea_modo", 0);
		spinner.setSelection(infolineaModo);

		// Seleccion
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				// Solo en caso de haber cambiado
				if (preferencias.getInt("infolinea_modo", 0) != arg2) {

					// Guarda la nueva seleciccion
					SharedPreferences.Editor editor = preferencias.edit();
					editor.putInt("infolinea_modo", arg2);
					editor.commit();

					// cambiar el modo de la actividad
					if (arg2 == 0) {

						Intent intent2 = getActivity().getIntent();
						intent2.putExtra("MODO_RED", InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE);
						getActivity().finish();
						startActivity(intent2);

					} else if (arg2 == 1) {

						Intent intent2 = getActivity().getIntent();
						intent2.putExtra("MODO_RED", InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE);
						getActivity().finish();
						startActivity(intent2);

					} else if (arg2 == 2) {

						Intent intent2 = getActivity().getIntent();
						intent2.putExtra("MODO_RED", InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE);
						getActivity().finish();
						startActivity(intent2);

					}

				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		// Consultar si es necesario, si ya lo tiene carga la lista
		if (lineasBus != null) {
			cargarListado();
		} else {

			ListView lineasVi = (ListView) getActivity().findViewById(R.id.infolinea_lista_lineas);
			TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_lineas_empty);
			lineasVi.setEmptyView(vacio);

			cargarLineas();
		}

		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.infolinea_lineas, container, false);
	}

	private void cargarLineas() {

		dialog = ProgressDialog.show(actividad, "", getString(R.string.dialogo_espera), true);

		// Carga local de lineas
		String datosOffline = null;
		if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {

			Resources resources = getResources();
			InputStream inputStream = resources.openRawResource(R.raw.lineasoffline);

			datosOffline = Utilidades.obtenerStringDeStream(inputStream);

		} else if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {

			Resources resources = getResources();
			InputStream inputStream = resources.openRawResource(R.raw.lineasoffline_tram);

			datosOffline = Utilidades.obtenerStringDeStream(inputStream);

		}

		ConnectivityManager connMgr = (ConnectivityManager) actividad.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			actividad.taskBuses = new LoadDatosLineasAsyncTask(loadBusesAsyncTaskResponder).execute(datosOffline);
		} else {
			Toast.makeText(actividad.getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
			dialog.dismiss();
		}

	}

	/**
	 * Sera llamado cuando la tarea de cargar buses termine
	 */
	LoadDatosLineasAsyncTaskResponder loadBusesAsyncTaskResponder = new LoadDatosLineasAsyncTaskResponder() {
		public void busesLoaded(ArrayList<BusLinea> buses) {
			if (buses != null) {
				lineasBus = buses;

				cargarListado();

				dialog.dismiss();

			} else {

				dialog.dismiss();

				Toast toast = Toast.makeText(actividad, getResources().getText(R.string.error_tiempos), Toast.LENGTH_SHORT);
				toast.show();

			}

		}
	};

	/**
	 * Cargar el listado de lineas
	 */
	private void cargarListado() {

		infoLineaAdapter = new InfoLineaAdapter(getActivity(), R.layout.infolineas_item);

		infoLineaAdapter.addAll(lineasBus);

		// Controlar pulsacion
		lineasView = (ListView) getActivity().findViewById(R.id.infolinea_lista_lineas);
		lineasView.setOnItemClickListener(lineasClickedHandler);

		TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_lineas_empty);
		lineasView.setEmptyView(vacio);

		lineasView.setAdapter(infoLineaAdapter);

	}

	/**
	 * Listener encargado de gestionar las pulsaciones sobre los items
	 */
	private OnItemClickListener lineasClickedHandler = new OnItemClickListener() {

		/**
		 * @param l
		 *            The ListView where the click happened
		 * @param v
		 *            The view that was clicked within the ListView
		 * @param position
		 *            The position of the view in the list
		 * @param id
		 *            The row id of the item that was clicked
		 */
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {

			linea = lineasBus.get(position);

			actividad.setLinea(linea);

			actividad.setTitle(linea.getLinea());

			// Quitar de horarios
			actividad.limpiarHorariosIda();

			cargarParadas(position);

		}
	};

	void cargarParadas(int index) {
		mCurCheckPosition = index;

		// We can display everything in-place with fragments, so update
		// the list to highlight the selected item and show the data.
		lineasView = (ListView) getActivity().findViewById(R.id.infolinea_lista_lineas);

		lineasView.setItemChecked(index, true);

		actividad.lineasMapas = null;
		actividad.sentidoIda = null;
		actividad.sentidoVuelta = null;

		dialog = ProgressDialog.show(actividad, "", getString(R.string.dialogo_espera), true);

		// Control para el nuevo modo offline
		if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE) {

			loadDatosMapa();
		} else if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {

			loadDatosMapaOffline();
		} else if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {

			loadDatosMapaTRAMOffline();
		}

	}

	/**
	 * Carga las paradas de MAPAS
	 */
	private void loadDatosMapa() {

		// String url = "http://www.subus.es/Lineas/kml/ALC34ParadasVuelta.xml";

		String url = "";

		if (!UtilidadesTAM.ACTIVAR_MAPS_V3) {
			url = UtilidadesTAM.getKMLParadasVuelta(actividad.getLinea().getIdlinea());
		} else {
			url = UtilidadesTAM.getKMLParadasV3(actividad.getLinea().getIdlinea());
		}

		DatosInfoLinea datos = new DatosInfoLinea();
		datos.setUrl(url);

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) actividad.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			actividad.taskDatosLinea = new LoadDatosInfoLineasAsyncTask(loadDatosInfoLineasAsyncTaskResponder).execute(datos);
		} else {
			Toast.makeText(actividad.getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

	/**
	 * Carga las paradas de MAPAS OFFLINE
	 */
	private void loadDatosMapaOffline() {

		List<DatosInfoLinea> datosRecorridos = cargarDatosMapaBD(actividad.getLinea().getNumLinea());

		if (datosRecorridos != null) {

			actividad.datosVuelta = datosRecorridos.get(1).getResult();

			actividad.datosIda = datosRecorridos.get(0).getResult();

			TextView titIda = (TextView) actividad.findViewById(R.id.tituloIda);

			if (actividad.datosIda != null && actividad.datosIda.getCurrentPlacemark() != null && actividad.datosIda.getCurrentPlacemark().getSentido() != null) {
				titIda.setText(">> " + actividad.datosIda.getCurrentPlacemark().getSentido());
			} else {
				titIda.setText("-");
			}

			cargarListadoIda();

			actividad.cambiarTab();

			if (actividad.datosIda == null || actividad.datosVuelta == null || actividad.datosIda.equals(actividad.datosVuelta)) {

				Toast.makeText(actividad, actividad.getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

			}

		} else {

			Toast toast = Toast.makeText(getActivity(), getResources().getText(R.string.error_datos_offline), Toast.LENGTH_SHORT);
			toast.show();

		}

		dialog.dismiss();

	}

	/**
	 * Carga las paradas de MAPAS OFFLINE
	 */
	private void loadDatosMapaTRAMOffline() {

		List<DatosInfoLinea> datosRecorridos = cargarDatosMapaTRAMBD(actividad.getLinea().getNumLinea());

		if (datosRecorridos != null) {

			actividad.datosIda = datosRecorridos.get(0).getResult();

			actividad.datosIda.setPlacemarks(UtilidadesTRAM.posicionesRecorrido(actividad.getLinea().getNumLinea(), datosRecorridos.get(0).getResult().getPlacemarks()));

			actividad.datosIda.ordenarPlacemark();

			actividad.datosVuelta = new DatosMapa();
			actividad.datosVuelta.setPlacemarks(actividad.datosIda.getPlacemarksInversa());

			TextView titIda = (TextView) actividad.findViewById(R.id.tituloIda);

			if (actividad.datosIda != null && actividad.datosIda.getCurrentPlacemark() != null && actividad.datosIda.getCurrentPlacemark().getSentido() != null) {
				// titIda.setText(">> " +
				// actividad.datosIda.getCurrentPlacemark().getSentido());

				int posicion = UtilidadesTRAM.getIdLinea(actividad.getLinea().getNumLinea());

				String desc = UtilidadesTRAM.DESC_LINEA[UtilidadesTRAM.TIPO[posicion]];

				titIda.setText(desc);

			} else {
				titIda.setText("-");
			}

			cargarListadoIda();

			actividad.cambiarTab();

			if (actividad.datosIda == null || actividad.datosVuelta == null || actividad.datosIda.equals(actividad.datosVuelta)) {

				Toast.makeText(actividad, actividad.getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

			}

		} else {

			Toast toast = Toast.makeText(getActivity(), getResources().getText(R.string.error_datos_offline), Toast.LENGTH_SHORT);
			toast.show();

		}

		dialog.dismiss();

	}

	/**
	 * Paradas ida
	 * 
	 * @param fIda
	 */
	private void loadDatosMapaIda(FragmentIda fIda) {

		// String url = "http://www.subus.es/Lineas/kml/ALC34ParadasIda.xml";

		String url = UtilidadesTAM.getKMLParadasIda(actividad.getLinea().getIdlinea());

		DatosInfoLinea datos = new DatosInfoLinea();
		datos.setUrl(url);
		// datos.setfIda(fIda);

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) actividad.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			actividad.taskInfoLineaIda = new LoadDatosInfoLineasAsyncTask(loadDatosInfoLineasAsyncTaskResponderIda).execute(datos);
		} else {
			Toast.makeText(actividad.getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

	/**
	 * Se llama cuando las paradas hayan sido cargadas
	 */
	LoadDatosInfoLineasAsyncTaskResponder loadDatosInfoLineasAsyncTaskResponder = new LoadDatosInfoLineasAsyncTaskResponder() {
		public void datosInfoLineasLoaded(DatosInfoLinea datos) {

			if (!UtilidadesTAM.ACTIVAR_MAPS_V3) {

				if (datos != null && datos.getResult() != null) {

					actividad.datosVuelta = datos.getResult();

					loadDatosMapaIda(datos.getfIda());

				} else {
					Toast toast = Toast.makeText(actividad, actividad.getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT);
					toast.show();
					dialog.dismiss();

				}

			} else {

				if (datos != null && datos.getResultIda() != null && datos.getResultVuelta() != null) {

					actividad.datosVuelta = datos.getResultVuelta();
					actividad.datosIda = datos.getResultIda();

					TextView titIda = (TextView) actividad.findViewById(R.id.tituloIda);

					if (actividad.datosIda != null && actividad.datosIda.getCurrentPlacemark() != null && actividad.datosIda.getCurrentPlacemark().getSentido() != null) {
						titIda.setText(">> " + actividad.datosIda.getCurrentPlacemark().getSentido());
					} else {
						titIda.setText("-");
					}

					cargarListadoIda();

					actividad.cambiarTab();

					if (actividad.datosIda == null || actividad.datosVuelta == null || actividad.datosIda.equals(actividad.datosVuelta)) {

						Toast.makeText(actividad, actividad.getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

					}

				} else {
					Toast toast = Toast.makeText(actividad, actividad.getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT);
					toast.show();
					dialog.dismiss();

				}

				dialog.dismiss();

			}

		}
	};

	LoadDatosInfoLineasAsyncTaskResponder loadDatosInfoLineasAsyncTaskResponderIda = new LoadDatosInfoLineasAsyncTaskResponder() {
		public void datosInfoLineasLoaded(DatosInfoLinea datos) {

			if (datos != null && datos.getResult() != null) {

				actividad.datosIda = datos.getResult();

				// datos.getfIda().cargarListado();

				TextView titIda = (TextView) actividad.findViewById(R.id.tituloIda);

				if (actividad.datosIda != null && actividad.datosIda.getCurrentPlacemark() != null && actividad.datosIda.getCurrentPlacemark().getSentido() != null) {
					titIda.setText(">> " + actividad.datosIda.getCurrentPlacemark().getSentido());
				} else {
					titIda.setText("-");
				}

				cargarListadoIda();

				actividad.cambiarTab();

				if (actividad.datosIda == null || actividad.datosVuelta == null || actividad.datosIda.equals(actividad.datosVuelta)) {

					Toast.makeText(actividad, actividad.getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

				}

			} else {
				Toast toast = Toast.makeText(actividad, actividad.getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT);
				toast.show();
				dialog.dismiss();

			}

			dialog.dismiss();

		}
	};

	public void cargarListadoIda() {

		infoLineaParadasAdapter = new InfoLineaParadasAdapter(getActivity(), R.layout.infolineas_item);

		infoLineaParadasAdapter.addAll(actividad.datosIda.getPlacemarks());

		ListView idaView = (ListView) getActivity().findViewById(R.id.infolinea_lista_ida);
		idaView.setOnItemClickListener(idaClickedHandler);

		idaView.setAdapter(infoLineaParadasAdapter);

	}

	/**
	 * Listener encargado de gestionar las pulsaciones sobre los items
	 */
	private OnItemClickListener idaClickedHandler = new OnItemClickListener() {

		/**
		 * @param l
		 *            The ListView where the click happened
		 * @param v
		 *            The view that was clicked within the ListView
		 * @param position
		 *            The position of the view in the list
		 * @param id
		 *            The row id of the item that was clicked
		 */
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {

			actividad.seleccionarParadaIda(position);

		}
	};

	/**
	 * Seleccion del fondo de la galeria en el arranque
	 */
	private void setupFondoAplicacion() {

		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
		SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

		String fondo_galeria = preferencias.getString("image_galeria", "");

		View contenedor_principal = getActivity().findViewById(R.id.contenedor_infolinea_lineas);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());

	}

	/**
	 * Cargar datos en modo offline
	 */
	private List<DatosInfoLinea> cargarDatosMapaBD(String lineaSeleccionadaNum) {

		List<DatosInfoLinea> datosInfoLinea = null;

		DatosMapa datosIda = new DatosMapa();
		DatosMapa datosVuelta = new DatosMapa();

		String parametros[] = { lineaSeleccionadaNum };

		Cursor cursorParadas = null;

		try {
			cursorParadas = getActivity().managedQuery(BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null);
		} catch (Exception e) {

			cursorParadas = null;

			e.printStackTrace();

		}

		if (cursorParadas != null) {
			List<Parada> listaParadasIda = new ArrayList<Parada>();

			List<Parada> listaParadasVuelta = new ArrayList<Parada>();

			String destinoIda = "";
			String destinoVuelta = "";

			for (cursorParadas.moveToFirst(); !cursorParadas.isAfterLast(); cursorParadas.moveToNext()) {

				Parada par = new Parada();

				par.setLineaNum(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
				par.setLineaDesc(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
				par.setConexion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
				par.setCoordenadas(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
				par.setDestino(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
				par.setDireccion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
				par.setLatitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
				par.setLongitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
				par.setParada(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));
				par.setObservaciones(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_OBSERVACIONES)));

				if (destinoIda.equals("")) {
					destinoIda = par.getDestino();
				} else if (destinoVuelta.equals("") && !destinoIda.equals(par.getDestino())) {
					destinoVuelta = par.getDestino();
				}

				if (par.getDestino().equals(destinoIda)) {

					listaParadasIda.add(par);

				} else if (par.getDestino().equals(destinoVuelta)) {

					listaParadasVuelta.add(par);

				}

			}

			if (listaParadasIda != null && !listaParadasIda.isEmpty() && listaParadasVuelta != null && !listaParadasVuelta.isEmpty()) {
				datosIda = mapearDatosModelo(listaParadasIda);

				datosVuelta = mapearDatosModelo(listaParadasVuelta);

				// Recorrido

				Cursor cursorRecorrido = getActivity().managedQuery(BuscadorLineasProvider.PARADAS_LINEA_RECORRIDO_URI, null, null, parametros, null);
				if (cursorRecorrido != null) {
					cursorRecorrido.moveToFirst();

					datosIda.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));

					cursorRecorrido.moveToNext();

					datosVuelta.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));

				}

				// Datos a la estructura esperada
				datosInfoLinea = new ArrayList<DatosInfoLinea>();
				DatosInfoLinea datoIda = new DatosInfoLinea();
				datoIda.setResult(datosIda);
				DatosInfoLinea datoVuelta = new DatosInfoLinea();
				datoVuelta.setResult(datosVuelta);
				datosInfoLinea.add(datoIda);
				datosInfoLinea.add(datoVuelta);

			} else {
				Toast toast = Toast.makeText(getActivity(), getString(R.string.error_datos_offline), Toast.LENGTH_SHORT);
				toast.show();
			}

		} else {
			Toast toast = Toast.makeText(getActivity(), getString(R.string.error_datos_offline), Toast.LENGTH_SHORT);
			toast.show();
		}

		return datosInfoLinea;

	}

	/**
	 * Cargar datos en modo offline TRAM
	 */
	private List<DatosInfoLinea> cargarDatosMapaTRAMBD(String lineaSeleccionadaNum) {

		List<DatosInfoLinea> datosInfoLinea = null;

		DatosMapa datosIda = new DatosMapa();

		String parametros[] = { lineaSeleccionadaNum };

		Cursor cursorParadas = null;

		try {
			cursorParadas = getActivity().managedQuery(BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null);
		} catch (Exception e) {

			cursorParadas = null;

			e.printStackTrace();

		}

		if (cursorParadas != null) {
			List<Parada> listaParadasIda = new ArrayList<Parada>();

			for (cursorParadas.moveToFirst(); !cursorParadas.isAfterLast(); cursorParadas.moveToNext()) {

				Parada par = new Parada();

				par.setLineaNum(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
				par.setLineaDesc(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
				par.setConexion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
				par.setCoordenadas(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
				par.setDestino(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
				par.setDireccion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
				par.setLatitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
				par.setLongitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
				par.setParada(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));
				par.setObservaciones(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_OBSERVACIONES)));

				listaParadasIda.add(par);

			}

			datosIda = mapearDatosModelo(listaParadasIda);

			// Datos a la estructura esperada
			datosInfoLinea = new ArrayList<DatosInfoLinea>();
			DatosInfoLinea datoIda = new DatosInfoLinea();
			datoIda.setResult(datosIda);

			DatosInfoLinea datoVuelta = new DatosInfoLinea();

			datosInfoLinea.add(datoIda);
			datosInfoLinea.add(datoVuelta);

		} else {
			Toast toast = Toast.makeText(getActivity(), getString(R.string.error_datos_offline), Toast.LENGTH_SHORT);
			toast.show();
		}

		return datosInfoLinea;

	}

	/**
	 * Cargar datos en modo online
	 * 
	 * @param listaParadas
	 * @return
	 */
	private DatosMapa mapearDatosModelo(List<Parada> listaParadas) {

		DatosMapa datos = new DatosMapa();

		datos.setPlacemarks(new ArrayList<PlaceMark>());

		for (int i = 0; i < listaParadas.size(); i++) {

			PlaceMark placeMark = new PlaceMark();

			placeMark.setAddress(listaParadas.get(i).getDireccion());
			placeMark.setCodigoParada(listaParadas.get(i).getParada());
			placeMark.setCoordinates(listaParadas.get(i).getCoordenadas());
			placeMark.setDescription(listaParadas.get(i).getLineaDesc());
			placeMark.setLineas(listaParadas.get(i).getConexion());
			placeMark.setObservaciones(listaParadas.get(i).getObservaciones());
			placeMark.setSentido(listaParadas.get(i).getDestino());
			placeMark.setTitle(listaParadas.get(i).getDireccion());

			datos.getPlacemarks().add(placeMark);
		}

		datos.setCurrentPlacemark(datos.getPlacemarks().get(0));

		return datos;
	}

}
