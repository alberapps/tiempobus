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
import alberapps.android.tiempobus.util.UtilidadesUI;
import android.content.SharedPreferences;
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

/**
 * Fragmento vuelta
 * 
 * 
 */
public class FragmentVuelta extends Fragment {

	private ListView lineasView;

	InfoLineasTabsPager actividad;

	InfoLineaParadasAdapter infoLineaParadasAdapter;

	/**
	 * On Create
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actividad = (InfoLineasTabsPager) getActivity();

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		setupFondoAplicacion();

		TextView titVuelta = (TextView) actividad.findViewById(R.id.tituloVuelta);

		if (actividad.datosHorarios != null) {

			titVuelta.setText(actividad.datosHorarios.getTituloSalidaVuelta());

			actividad.cargarListadoHorarioVuelta();

		} else if (actividad.datosVuelta != null) {

			actividad.limpiarHorariosVuelta();

			if (actividad.datosVuelta != null && actividad.datosVuelta.getCurrentPlacemark() != null && actividad.datosVuelta.getCurrentPlacemark().getSentido() != null) {
				titVuelta.setText(">> " + actividad.datosVuelta.getCurrentPlacemark().getSentido());
			} else {
				titVuelta.setText("-");
			}

			cargarListado();
		} else {

			ListView idaView = (ListView) getActivity().findViewById(R.id.infolinea_lista_vuelta);

			TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_vuelta_empty);
			idaView.setEmptyView(vacio);
		}

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.infolinea_vuelta, container, false);
	}

	public void cargarListado() {

		infoLineaParadasAdapter = new InfoLineaParadasAdapter(getActivity(), R.layout.infolineas_item);

		infoLineaParadasAdapter.addAll(actividad.datosVuelta.getPlacemarks());

		ListView idaView = (ListView) getActivity().findViewById(R.id.infolinea_lista_vuelta);

		TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_vuelta_empty);
		idaView.setEmptyView(vacio);

		idaView.setOnItemClickListener(lineasClickedHandler);

		idaView.setAdapter(infoLineaParadasAdapter);

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

			actividad.seleccionarParadaVuelta(position);

		}
	};

	/**
	 * Seleccion del fondo de la galeria en el arranque
	 */
	private void setupFondoAplicacion() {

		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
		SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

		String fondo_galeria = preferencias.getString("image_galeria", "");

		View contenedor_principal = getActivity().findViewById(R.id.contenedor_infolinea_vuelta);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());

	}

}
