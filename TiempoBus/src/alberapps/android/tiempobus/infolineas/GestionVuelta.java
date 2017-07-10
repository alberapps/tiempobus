/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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

import android.content.SharedPreferences;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;

/**
 * Gestion de datos del tram
 */
public class GestionVuelta {

    /**
     * Cotexto principal
     */
    private InfoLineasTabsPager context;

    private SharedPreferences preferencias;

    public GestionVuelta(InfoLineasTabsPager contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }


    /**
     * Seleccion parada de vuelta
     *
     * @param posicion
     */
    public void seleccionarParadaVuelta(int posicion) {

        int codigo = -1;

        try {
            codigo = Integer.parseInt(context.datosVuelta.getPlacemarks().get(posicion).getCodigoParada());

        } catch (Exception e) {

        }

        if (codigo != -1 && (context.datosVuelta.getPlacemarks().get(posicion).getCodigoParada().length() == 4 || DatosPantallaPrincipal.esTram(context.datosVuelta.getPlacemarks().get(posicion).getCodigoParada()))) {

            context.cargarTiempos(codigo);

        } else {

            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_codigo), Toast.LENGTH_SHORT).show();

        }


    }


    /**
     * Recarga de datos
     */
    public void recargaInformacion() {


        if (context.datosHorarios != null) {

            cargarHeaderVuelta();

            context.gestionHorariosVuelta.cargarListadoHorarioVuelta();

        } else if (context.datosVuelta != null) {

            context.gestionHorariosVuelta.limpiarHorariosVuelta();

            cargarListado();
        } else {

            ListView idaView = (ListView) context.findViewById(R.id.infolinea_lista_vuelta);

            TextView vacio = (TextView) context.findViewById(R.id.infolinea_vuelta_empty);
            idaView.setEmptyView(vacio);
        }

    }


    /**
     * Carga el listado de pantalla
     */
    public void cargarListado() {

        cargarHeaderVuelta();

        context.infoLineaParadasAdapter = new InfoLineaParadasAdapter(context, R.layout.infolineas_item);

        context.infoLineaParadasAdapter.addAll(context.datosVuelta.getPlacemarks());

        ListView idaView = (ListView) context.findViewById(R.id.infolinea_lista_vuelta);

        TextView vacio = (TextView) context.findViewById(R.id.infolinea_vuelta_empty);
        idaView.setEmptyView(vacio);

        idaView.setOnItemClickListener(lineasClickedHandler);

        idaView.setAdapter(context.infoLineaParadasAdapter);

    }

    /**
     * Listener encargado de gestionar las pulsaciones sobre los items
     */
    private AdapterView.OnItemClickListener lineasClickedHandler = new AdapterView.OnItemClickListener() {

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

            if(position == 0){
                return;
            }

            context.gestionVuelta.seleccionarParadaVuelta(position - 1);

        }
    };


    /**
     * Cargar cabecera listado
     */
    public void cargarHeaderVuelta() {

        ListView vueltaView = (ListView) context.findViewById(R.id.infolinea_lista_vuelta);

        View vheader = null;

        if (vueltaView != null && vueltaView.getHeaderViewsCount() == 0) {

            LayoutInflater li2 = LayoutInflater.from(context);

            vheader = li2.inflate(R.layout.infolineas_paradas_header, null);

            vueltaView.addHeaderView(vheader);

        }

        if(context.linea != null) {
            //Linea num
            TextView textoNumLinea = (TextView) vueltaView.findViewById(R.id.num_linea);
            textoNumLinea.setText(context.linea.getNumLinea());

            //Formato colores
            DatosPantallaPrincipal.formatoLinea(context, textoNumLinea, context.linea.getNumLinea(), true);

            //Linea
            TextView textoLinea = (TextView) vueltaView.findViewById(R.id.datos_desc_linea);

            textoLinea.setText(context.linea.getLinea().substring(context.linea.getNumLinea().length()).trim());


        }

        //Sentido linea
        TextView texto = (TextView) vueltaView.findViewById(R.id.datos_desc_linea_sentido);


        if (context.datosHorarios != null) {

            texto.setText(context.datosHorarios.getTituloSalidaVuelta());

        } else if (context.datosVuelta != null) {


            if (context.datosVuelta != null && context.datosVuelta.getCurrentPlacemark() != null && context.datosVuelta.getCurrentPlacemark().getSentido() != null) {
                texto.setText(context.datosVuelta.getCurrentPlacemark().getSentido());
            } else {
                texto.setText("-");
            }


        }

        texto.setLinksClickable(true);
        texto.setAutoLinkMask(Linkify.WEB_URLS);


    }


}
