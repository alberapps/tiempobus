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
package alberapps.android.tiempobus.noticias;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.UtilidadesUI;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragmento vuelta
 * 
 * 
 */
public class FragmentNoticias extends Fragment {

	private static final String noticiasURL = "http://www.subus.es/Especiales/Novedades/Novedades.asp";

	
	private NoticiasTabsPager actividad;
	
	/**
	 * On Create
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		actividad = (NoticiasTabsPager) getActivity();
		
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		// TextView titVuelta = (TextView)
		// actividad.findViewById(R.id.tituloVuelta);

		setupFondoAplicacion();

		TextView accederNoticia = (TextView) getActivity().findViewById(R.id.accederNoticiaL);

		accederNoticia.setLinksClickable(true);
		accederNoticia.setAutoLinkMask(Linkify.WEB_URLS);

		accederNoticia.setText(noticiasURL);

		if(actividad.noticiasRecuperadas != null){
			actividad.cargarListado(actividad.noticiasRecuperadas, true);
		}
		
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.noticias, container, false);
	}

	/**
	 * Seleccion del fondo de la galeria en el arranque
	 */
	private void setupFondoAplicacion() {

		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
		SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

		String fondo_galeria = preferencias.getString("image_galeria", "");

		View contenedor_principal = getActivity().findViewById(R.id.contenedor_noticias);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());

	}

}
