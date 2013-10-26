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
import android.widget.ListView;
import android.widget.TextView;

/**
 * Muestra las noticias recuperadas
 * 
 * 
 */
public class FragmentIda extends Fragment {

	// private ProgressDialog dialog;

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
	public void onViewStateRestored(Bundle savedInstanceState) {

		setupFondoAplicacion();

		TextView titIda = (TextView) actividad.findViewById(R.id.tituloIda);

		if (actividad.datosHorarios != null) {

			titIda.setText(actividad.datosHorarios.getTituloSalidaIda());

		} else if (actividad.datosIda != null) {

			actividad.limpiarHorariosIda();

			if (actividad.datosIda != null && actividad.datosIda.getCurrentPlacemark() != null && actividad.datosIda.getCurrentPlacemark().getSentido() != null) {
				titIda.setText(">> " + actividad.datosIda.getCurrentPlacemark().getSentido());
			} else {
				titIda.setText("-");
			}

			cargarListado();
		} else {
			ListView idaView = (ListView) getActivity().findViewById(R.id.infolinea_lista_ida);

			TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_lista_ida_vacio);
			idaView.setEmptyView(vacio);
		}

		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.infolinea_ida, container, false);
	}

	public void cargarListado() {

		infoLineaParadasAdapter = new InfoLineaParadasAdapter(getActivity(), R.layout.infolineas_item);

		infoLineaParadasAdapter.addAll(actividad.datosIda.getPlacemarks());

		ListView idaView = (ListView) getActivity().findViewById(R.id.infolinea_lista_ida);

		TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_lista_ida_vacio);
		idaView.setEmptyView(vacio);

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

		View contenedor_principal = getActivity().findViewById(R.id.contenedor_infolinea_ida);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());

	}

}
