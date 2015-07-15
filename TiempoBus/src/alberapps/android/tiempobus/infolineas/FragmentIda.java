/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
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
package alberapps.android.tiempobus.infolineas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.UtilidadesUI;

/**
 * Muestra las noticias recuperadas
 */
public class FragmentIda extends Fragment {

    InfoLineasTabsPager actividad;

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

        actividad.gestionIda.cargarHeaderIda(true, false, false);

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.infolinea_ida, container, false);
    }


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
