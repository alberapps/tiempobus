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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.UtilidadesUI;

/**
 * Fragmento tw
 */
public class FragmentAlberappsTwitter extends Fragment {

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
    public void onViewStateRestored(Bundle savedInstanceState) {

        setupFondoAplicacion();

        if (actividad.avisosAlberappsRecuperados != null) {
            //Si hay resultados

            actividad.cargarListadoAlberappsTw();
        } else if (actividad.twAlberappsSinResultados) {

            //Si se ha consultado y no hay resultados

            actividad.cargarListadoAlberappsTw();

            ProgressBar lpb = (ProgressBar) actividad.findViewById(R.id.tiempos_progreso_alberapps_tw);
            lpb.clearAnimation();
            lpb.setVisibility(View.INVISIBLE);

            ListView listTwWiew = (ListView) actividad.findViewById(R.id.listaalberappstw);

            TextView vacio = (TextView) actividad.findViewById(R.id.vacio_alberapps_tw);
            listTwWiew.setEmptyView(vacio);


        } else {

            //Progreso inicial

            // Progreso lista
            ListView listTwWiew = (ListView) actividad.findViewById(R.id.listaalberappstw);
            TextView vacio = (TextView) actividad.findViewById(R.id.vacio_alberapps_tw);
            vacio.setVisibility(View.INVISIBLE);
            ProgressBar lpb = (ProgressBar) actividad.findViewById(R.id.tiempos_progreso_alberapps_tw);
            lpb.setIndeterminate(true);
            listTwWiew.setEmptyView(lpb);

        }

        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.noticias_alberappstw, container, false);
    }

    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = getActivity().findViewById(R.id.contenedor_alberapps_tw);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());

    }

}
