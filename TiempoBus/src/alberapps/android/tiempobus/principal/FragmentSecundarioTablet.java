/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2014 Alberto Montiel
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
package alberapps.android.tiempobus.principal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.UtilidadesUI;

/**
 * Fragmento secundario tablet
 */
public class FragmentSecundarioTablet extends Fragment {

    MainActivity actividad;

    SharedPreferences preferencias;

    /**
     * On Create
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actividad = (MainActivity) getActivity();

        PreferenceManager.setDefaultValues(actividad, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(actividad);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        //Activacion o no de tarjetas
        boolean activadaTarjetaWiki = preferencias.getBoolean("tarjeta_wiki_on", true);
        boolean activadaTarjetaClima = preferencias.getBoolean("tarjeta_clima_on", true);


        if (!activadaTarjetaWiki) {

            if (UtilidadesUI.pantallaTabletHorizontal(actividad)) {

                LinearLayout tarjetasTablet = (LinearLayout) view.findViewById(R.id.contenedor_secundario);

                CardView tarjetaWiki = (CardView) tarjetasTablet.findViewById(R.id.tarjetaWiki);

                if (tarjetaWiki != null) {

                    tarjetasTablet.removeView(tarjetaWiki);
                }

            }

        }

        if (!activadaTarjetaClima) {

            if (UtilidadesUI.pantallaTabletHorizontal(actividad)) {

                LinearLayout tarjetasTablet = (LinearLayout) view.findViewById(R.id.contenedor_secundario);

                CardView tarjetaClima = (CardView) tarjetasTablet.findViewById(R.id.tarjetaClima);

                if (tarjetaClima != null) {

                    tarjetasTablet.removeView(tarjetaClima);
                }

            }

        }


    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        setupFondoAplicacion();

        actualizarDatos();

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_secun_detalle, container, false);
    }

    OnHeadlineSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int position);
    }

    /**
     * Actualizar datos ficha tablet
     */
    public void actualizarDatos() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    public void setupFondoAplicacion() {

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = getActivity().findViewById(R.id.contenedor_secundario);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());

    }

}
