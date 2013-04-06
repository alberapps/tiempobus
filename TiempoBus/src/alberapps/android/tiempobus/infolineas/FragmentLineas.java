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

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.noticias.NoticiasAdapter;
import alberapps.android.tiempobus.tasks.LoadBusesAsyncTask;
import alberapps.android.tiempobus.tasks.LoadBusesAsyncTask.LoadBusesAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadDatosInfoLineasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosInfoLineasAsyncTask.LoadDatosInfoLineasAsyncTaskResponder;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.UtilidadesTAM;
import alberapps.java.tam.webservice.estructura.GetLineasResult;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

	/**
	 * On Create
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actividad = (InfoLineasTabsPager) getActivity();

		cargarLineas();

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		setupFondoAplicacion();

		if (lineasBus != null) {
			cargarListado();
		}

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.infolinea_lineas, container, false);
	}

	private void cargarLineas() {

		dialog = ProgressDialog.show(actividad, "", getString(R.string.dialogo_espera), true);

		ConnectivityManager connMgr = (ConnectivityManager) actividad.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadBusesAsyncTask(loadBusesAsyncTaskResponder).execute();
		} else {
			Toast.makeText(actividad.getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
			dialog.dismiss();
		}

	}

	/**
	 * Sera llamado cuando la tarea de cargar buses termine
	 */
	LoadBusesAsyncTaskResponder loadBusesAsyncTaskResponder = new LoadBusesAsyncTaskResponder() {
		public void busesLoaded(ArrayList<BusLinea> buses) {
			if (buses != null) {
				lineasBus = buses;

				cargarListado();

				dialog.dismiss();

			} else {

				dialog.dismiss();

				Toast toast = Toast.makeText(actividad, getResources().getText(R.string.error_gcode), Toast.LENGTH_SHORT);
				toast.show();

				// En caso de error carga la lista interna
				lineasBus = new ArrayList<BusLinea>();

				for (int i = 0; i < UtilidadesTAM.LINEAS_CODIGO_KML.length; i++) {

					lineasBus.add(new BusLinea(UtilidadesTAM.LINEAS_CODIGO_KML[i], UtilidadesTAM.LINEAS_DESCRIPCION[i], UtilidadesTAM.LINEAS_NUM[i]));

				}

				cargarListado();

			}

		}
	};

	private void cargarListado() {

		// List<String> lineasListString = new ArrayList<String>();

		// for (int i = 0; i < lineasBus.size(); i++) {

		// lineasListString.add(lineasBus.get(i).getLinea());

		// }

		infoLineaAdapter = new InfoLineaAdapter(getActivity(), R.layout.infolineas_item);

		infoLineaAdapter.addAll(lineasBus);

		// Controlar pulsacion
		lineasView = (ListView) getActivity().findViewById(R.id.infolinea_lista_lineas);
		lineasView.setOnItemClickListener(lineasClickedHandler);

		// lineasView.setAdapter(new ArrayAdapter<String>(getActivity(),
		// R.layout.infolineas_item, R.id.dato, lineasListString));

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

		loadDatosMapa();

	}

	/**
	 * Carga las paradas de MAPAS
	 */
	private void loadDatosMapa() {

		// String url = "http://www.subus.es/Lineas/kml/ALC34ParadasVuelta.xml";

		String url = UtilidadesTAM.getKMLParadasVuelta(actividad.getLinea().getIdlinea());

		// FragmentIda fIda = (FragmentIda)
		// getFragmentManager().findFragmentByTag("android:switcher:" + getId()
		// + ":1");

		DatosInfoLinea datos = new DatosInfoLinea();
		datos.setUrl(url);
		// datos.setfIda(fIda);

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) actividad.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadDatosInfoLineasAsyncTask(loadDatosInfoLineasAsyncTaskResponder).execute(datos);
		} else {
			Toast.makeText(actividad.getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

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
			new LoadDatosInfoLineasAsyncTask(loadDatosInfoLineasAsyncTaskResponderIda).execute(datos);
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

			if (datos != null && datos.getResult() != null) {

				actividad.datosVuelta = datos.getResult();

				loadDatosMapaIda(datos.getfIda());

			} else {
				Toast toast = Toast.makeText(actividad, actividad.getString(R.string.aviso_error_datos) + " www.subus.es", Toast.LENGTH_SHORT);
				toast.show();
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
				Toast toast = Toast.makeText(actividad, actividad.getString(R.string.aviso_error_datos) + " www.subus.es", Toast.LENGTH_SHORT);
				toast.show();
				dialog.dismiss();

			}

			dialog.dismiss();

		}
	};

	public void cargarListadoIda() {

		/*
		 * List<String> datosListString = new ArrayList<String>();
		 * 
		 * for (int i = 0; i < actividad.datosIda.getPlacemarks().size(); i++) {
		 * 
		 * datosListString.add(actividad.datosIda.getPlacemarks().get(i).
		 * getCodigoParada() + " " +
		 * actividad.datosIda.getPlacemarks().get(i).getTitle());
		 * 
		 * }
		 */

		infoLineaParadasAdapter = new InfoLineaParadasAdapter(getActivity(), R.layout.infolineas_item);

		infoLineaParadasAdapter.addAll(actividad.datosIda.getPlacemarks());

		ListView idaView = (ListView) getActivity().findViewById(R.id.infolinea_lista_ida);
		idaView.setOnItemClickListener(idaClickedHandler);

		// idaView.setAdapter(new ArrayAdapter<String>(getActivity(),
		// R.layout.infolineas_item, R.id.dato, datosListString));

		idaView.setAdapter(infoLineaParadasAdapter);

		// Controlar pulsacion

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

}
