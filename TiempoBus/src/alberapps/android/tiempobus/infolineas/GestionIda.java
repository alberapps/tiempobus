/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.tasks.LoadDatosInfoLineasAsyncTask;
import alberapps.java.tam.UtilidadesTAM;
import alberapps.java.tram.UtilidadesTRAM;

/**
 * Gestion de datos del tram
 */
public class GestionIda {

    /**
     * Cotexto principal
     */
    private InfoLineasTabsPager context;

    private SharedPreferences preferencias;

    public GestionIda(InfoLineasTabsPager contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }


    /**
     * Seleccion parada de ida
     *
     * @param posicion
     */
    public void seleccionarParadaIda(int posicion) {


        int codigo = -1;

        try {
            codigo = Integer.parseInt(context.datosIda.getPlacemarks().get(posicion).getCodigoParada());

        } catch (Exception e) {

        }

        if (codigo != -1 && (context.datosIda.getPlacemarks().get(posicion).getCodigoParada().length() == 4 || DatosPantallaPrincipal.esTram(context.datosIda.getPlacemarks().get(posicion).getCodigoParada()))) {

            context.cargarTiempos(codigo);

        } else {

            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_codigo), Toast.LENGTH_SHORT).show();

        }


    }


    public void cargarListado() {

        context.infoLineaParadasAdapter = new InfoLineaParadasAdapter(context, R.layout.infolineas_item);

        context.infoLineaParadasAdapter.addAll(context.datosIda.getPlacemarks());

        ListView idaView = (ListView) context.findViewById(R.id.infolinea_lista_ida);

        TextView vacio = (TextView) context.findViewById(R.id.infolinea_lista_ida_vacio);
        idaView.setEmptyView(vacio);

        idaView.setOnItemClickListener(idaClickedHandler);

        idaView.setAdapter(context.infoLineaParadasAdapter);

    }


    /**
     * Paradas ida
     *
     * @param fIda
     */
    public void loadDatosMapaIda(FragmentIda fIda) {

        // String url = "http://www.subus.es/Lineas/kml/ALC34ParadasIda.xml";

        String url = UtilidadesTAM.getKMLParadasIda(context.getLinea().getIdlinea());

        DatosInfoLinea datos = new DatosInfoLinea();
        datos.setUrl(url);
        // datos.setfIda(fIda);

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            context.taskInfoLineaIda = new LoadDatosInfoLineasAsyncTask(loadDatosInfoLineasAsyncTaskResponderIda).execute(datos);
        } else {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
            if (context.dialog != null && context.dialog.isShowing()) {
                context.dialog.dismiss();
            }
        }

    }


    LoadDatosInfoLineasAsyncTask.LoadDatosInfoLineasAsyncTaskResponder loadDatosInfoLineasAsyncTaskResponderIda = new LoadDatosInfoLineasAsyncTask.LoadDatosInfoLineasAsyncTaskResponder() {
        public void datosInfoLineasLoaded(DatosInfoLinea datos) {

            if (datos != null && datos.getResult() != null) {

                context.datosIda = datos.getResult();

                cargarHeaderIda(true, false, false);

                cargarListadoIda();

                context.cambiarTab();

                if (context.datosIda == null || context.datosVuelta == null || context.datosIda.equals(context.datosVuelta)) {

                    Toast.makeText(context, context.getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

                }

            } else {
                Toast toast = Toast.makeText(context, context.getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT);
                toast.show();
                context.dialog.dismiss();

            }

            context.dialog.dismiss();

        }
    };

    public void cargarListadoIda() {

        context.infoLineaParadasAdapter = new InfoLineaParadasAdapter(context, R.layout.infolineas_item);

        context.infoLineaParadasAdapter.addAll(context.datosIda.getPlacemarks());

        ListView idaView = (ListView) context.findViewById(R.id.infolinea_lista_ida);
        idaView.setOnItemClickListener(idaClickedHandler);

        idaView.setAdapter(context.infoLineaParadasAdapter);

    }

    /**
     * Listener encargado de gestionar las pulsaciones sobre los items
     */
    private AdapterView.OnItemClickListener idaClickedHandler = new AdapterView.OnItemClickListener() {

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

            context.gestionIda.seleccionarParadaIda(position - 1);

        }
    };


    /**
     * Cargar cabecera listado
     */
    public void cargarHeaderIda(boolean esBus, boolean esTram, boolean esBusOffline) {

        ListView idaView = (ListView) context.findViewById(R.id.infolinea_lista_ida);

        View vheader = null;

        if (idaView != null && idaView.getHeaderViewsCount() == 0) {

            LayoutInflater li2 = LayoutInflater.from(context);

            vheader = li2.inflate(R.layout.infolineas_paradas_header, null);

            idaView.addHeaderView(vheader);

        }

        if(context.linea != null) {
            //Linea num
            TextView textoNumLinea = (TextView) idaView.findViewById(R.id.num_linea);
            textoNumLinea.setText(context.linea.getNumLinea());

            //Formato colores
            DatosPantallaPrincipal.formatoLinea(context, textoNumLinea, context.linea.getNumLinea());

            //Linea
            TextView textoLinea = (TextView) idaView.findViewById(R.id.datos_desc_linea);

            textoLinea.setText(context.linea.getLinea().substring(context.linea.getNumLinea().length()).trim());


        }

        //Sentido linea
        TextView texto = (TextView) idaView.findViewById(R.id.datos_desc_linea_sentido);


        if (context.datosHorarios != null) {

            texto.setText(context.datosHorarios.getTituloSalidaIda());

        } else if (context.datosIda != null) {

            context.gestionHorariosIda.limpiarHorariosIda();

            if (esBus && context.datosIda != null && context.datosIda.getCurrentPlacemark() != null && context.datosIda.getCurrentPlacemark().getSentido() != null) {
                texto.setText(">> " + context.datosIda.getCurrentPlacemark().getSentido());
            } else if (esTram && context.datosIda != null && context.datosIda.getCurrentPlacemark() != null && context.datosIda.getCurrentPlacemark().getSentido() != null) {

                int posicion = UtilidadesTRAM.getIdLinea(context.getLinea().getNumLinea());

                String desc = UtilidadesTRAM.DESC_LINEA[UtilidadesTRAM.TIPO[posicion]];

                texto.setText(desc);

            } else if (esBusOffline && context.datosIda != null && context.datosIda.getCurrentPlacemark() != null && context.datosIda.getCurrentPlacemark().getSentido() != null) {
                texto.setText(">> " + context.datosIda.getCurrentPlacemark().getSentido());
            }else {
                texto.setText("-");
            }


            context.gestionIda.cargarListado();
        } else {
            idaView = (ListView) context.findViewById(R.id.infolinea_lista_ida);

            TextView vacio = (TextView) context.findViewById(R.id.infolinea_lista_ida_vacio);
            idaView.setEmptyView(vacio);
        }


        texto.setLinksClickable(true);
        texto.setAutoLinkMask(Linkify.WEB_URLS);


    }


    /**
     * Cargar cabecera listado
     */
    public void cargarHeaderIdaOfflineTram() {

        cargarHeaderIda(false, true, false);


    }

    /**
     * Cargar cabecera listado
     */
    public void cargarHeaderIdaOfflineBus() {

        cargarHeaderIda(false, false, true);


    }






}
