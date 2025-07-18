/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
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

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.BusLinea;

/**
 * Adaptador de informacion de lineas
 */
public class InfoLineaAdapter extends ArrayAdapter<BusLinea> implements Filterable {

    private InfoLineasTabsPager contexto;

    private List<BusLinea> listaOriginal;

    private List<BusLinea> listaOriginalSinGrupo;

    private CharSequence filtro;

    private List<String> descripcionesGrupo;

    /**
     * Constructor
     *
     * @param context
     * @param textViewResourceId
     */
    public InfoLineaAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        this.contexto = ((InfoLineasTabsPager) context);

        String[] arrayGrupos = context.getResources().getStringArray(R.array.grupos_lineas_bus);

        descripcionesGrupo = new ArrayList<>(Arrays.asList(arrayGrupos));

    }

    /**
     * Genera la vista de cada uno de los items
     */
    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        // Si no tenemos la vista de la fila creada componemos una
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.infolineas_item, null);
        }

        //Control de vista correcta por el filtro
        if (v.findViewById(R.id.bus_linea) == null) {
            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.infolineas_item, null);
        }


        TextView busLinea;
        TextView descLinea;
        TextView datosLinea;

        busLinea = (TextView) v.findViewById(R.id.bus_linea);
        descLinea = (TextView) v.findViewById(R.id.desc_linea);
        datosLinea = (TextView) v.findViewById(R.id.datos_linea);

        Typeface ubuntu = null;

        try {
            ubuntu = ResourcesCompat.getFont(contexto, R.font.ubuntu);
            busLinea.setTypeface(ubuntu, Typeface.BOLD);
            descLinea.setTypeface(ubuntu, Typeface.BOLD);
            datosLinea.setTypeface(ubuntu);
        } catch (Exception e) {
            e.printStackTrace();
        }


        final BusLinea bus = getItem(position);

        if (bus != null && !bus.isErrorServicio() && !bus.isFiltroSinDatos()) {
            busLinea.setText(bus.getNumLinea());
            //descLinea.setText(bus.getLinea().substring(bus.getNumLinea().length()).trim());
            descLinea.setText(bus.getLinea());

            //Formato colores
            DatosPantallaPrincipal.formatoLinea(contexto, busLinea, bus.getNumLinea(), true);


            TextView informacionText = (TextView) v.findViewById(R.id.infoparada_horarios);
            TextView informacionText1 = (TextView) v.findViewById(R.id.infoparada_horarios_1);
            TextView infoparada = (TextView) v.findViewById(R.id.infoparada_paradas);

            if (ubuntu != null) {
                informacionText.setTypeface(ubuntu);
                informacionText1.setTypeface(ubuntu);
                infoparada.setTypeface(ubuntu);
            }

            if (((InfoLineasTabsPager) contexto).modoRed != InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {


                // Carga de horarios bus
                // Link informacion
                informacionText1.setOnClickListener(new OnClickListener() {

                    public void onClick(View view) {

                        contexto.setLinea(bus);

                        //contexto.setTitle(bus.getLinea());

                        contexto.gestionHorariosIda.cargarHorarios(bus, position);

                    }

                });


                int id = Integer.parseInt(bus.getIdGrupo());

                String descripcion = "";

                if (descripcionesGrupo != null && id < descripcionesGrupo.size()) {
                    descripcion = descripcionesGrupo.get(id);
                }

                datosLinea.setText(descripcion);


                //informacionText.setVisibility(View.INVISIBLE);

                //informacionText.setText(R.string.pdf_text);

                // Carga de horarios tram
                // Link informacion
                informacionText.setOnClickListener(new OnClickListener() {

                    public void onClick(View view) {

                        contexto.gestionTram.abrirPdfBus(bus.getNumLinea());

                    }

                });


            } else {

                informacionText.setVisibility(View.INVISIBLE);

                datosLinea.setText(bus.getGrupo());

                informacionText1.setText(R.string.infolinea_horarios_pdf);

                //Desactivado horarios
                //informacionText1.setVisibility(View.INVISIBLE);

                // Carga de horarios tram
                // Link informacion
                informacionText1.setOnClickListener(new OnClickListener() {

                    public void onClick(View view) {

                        contexto.gestionTram.seleccionarPdf(bus);

                    }

                });

            }
        } else if (bus != null && bus.isErrorServicio()) {

            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.tiempos_item_sin_datos, null);

            TextView text = (TextView) v.findViewById(R.id.txt_sin_datos);
            text.setText(contexto.getString(R.string.error_tiempos));
            TextView textAviso = (TextView) v.findViewById(R.id.txt_sin_datos_aviso);

            String aviso = "";

            /*InfoLineasTabsPager actividad = (InfoLineasTabsPager) contexto;

            if (actividad.datosPantallaPrincipal.esTram(Integer.toString(actividad.paradaActual))) {
                aviso = ctx.getString(R.string.tlf_tram);
            } else {
                aviso = ctx.getString(R.string.tlf_subus);
            }*/

            ImageView imagenAviso = (ImageView) v.findViewById(R.id.imageAviso);
            imagenAviso.setImageResource(R.drawable.ic_warning_black_48dp);


            textAviso.setText(aviso);


        } else if (bus != null && bus.isFiltroSinDatos()) {

            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.tiempos_item_sin_datos, null);

            TextView text = (TextView) v.findViewById(R.id.txt_sin_datos);

            text.setText(contexto.getString(R.string.main_no_items));

            TextView textAviso = (TextView) v.findViewById(R.id.txt_sin_datos_aviso);

            String aviso = "";

            //ImageView imagenAviso = (ImageView) v.findViewById(R.id.imageAviso);
            //imagenAviso.setImageResource(R.drawable.alerts_warning);

            textAviso.setText(aviso);

        }


        return v;
    }

    /**
     * Anade todas las lineas al adapter
     *
     * @param busLinea
     */
    public void addAll(List<BusLinea> busLinea) {
        if (busLinea == null) {
            return;
        }

        for (int i = 0; i < busLinea.size(); i++) {
            add(busLinea.get(i));
        }

        listaOriginal = new ArrayList<>(busLinea);
        listaOriginalSinGrupo = new ArrayList<>(busLinea);
    }

    /**
     * Aplicar filtro de grupo
     *
     * @param grupo
     */
    public void filtrarPorGrupo(String grupo) {

        if (listaOriginalSinGrupo == null || listaOriginalSinGrupo.isEmpty() || listaOriginalSinGrupo.get(0).isErrorServicio()) {
            return;
        }

        notifyDataSetChanged();
        clear();

        listaOriginal.clear();

        if (grupo == null || grupo.equals("0")) {

            addAll(listaOriginalSinGrupo);

        } else {

            for (int i = 0; i < listaOriginalSinGrupo.size(); i++) {

                if (listaOriginalSinGrupo.get(i).getIdGrupo().equals(grupo)) {
                    listaOriginal.add(listaOriginalSinGrupo.get(i));
                    add(listaOriginalSinGrupo.get(i));
                }

            }

        }
        if (listaOriginal.isEmpty()) {
            BusLinea b1 = new BusLinea();
            b1.setFiltroSinDatos(true);
            add(b1);
        }

        notifyDataSetInvalidated();

        final TextView textoBuscar = (TextView) contexto.findViewById(R.id.texto_buscar);
        textoBuscar.setText("");


    }


    /**
     * Para filtros del listado
     *
     * @return filtro
     */
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                List<BusLinea> filtrada = new ArrayList<>();

                if (constraint != null && !constraint.toString().equals("")) {

                    for (int i = 0; i < listaOriginal.size(); i++) {

                        if (listaOriginal.get(i).getLinea().toLowerCase(UtilidadesUI.getLocaleInt()).contains(constraint.toString().toLowerCase(UtilidadesUI.getLocaleInt())) || listaOriginal.get(i).getNumLinea().toLowerCase(UtilidadesUI.getLocaleInt()).contains(constraint.toString().toLowerCase(UtilidadesUI.getLocaleInt()))) {
                            filtrada.add(listaOriginal.get(i));
                        }

                    }

                    results.count = filtrada.size();
                    results.values = filtrada;

                } else {

                    results.count = listaOriginal.size();
                    results.values = listaOriginal;

                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                try {

                    List<BusLinea> lista = (List<BusLinea>) results.values;

                    notifyDataSetChanged();
                    clear();

                    for (int i = 0; i < lista.size(); i++) {
                        add(lista.get(i));
                    }

                    if (getCount() == 0) {
                        BusLinea b1 = new BusLinea();
                        b1.setFiltroSinDatos(true);
                        add(b1);
                    }

                    notifyDataSetInvalidated();

                    final TextView textoBuscar = (TextView) contexto.findViewById(R.id.texto_buscar);
                    if (textoBuscar != null) {
                        textoBuscar.requestFocus();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

        };

        return filter;
    }

    /**
     * Lista con el filtro
     *
     * @return
     */
    public List<BusLinea> getListaFiltrada() {

        List<BusLinea> lista = new ArrayList<>();

        for (int i = 0; i < getCount(); i++) {

            lista.add(getItem(i));

        }

        return lista;

    }


    public CharSequence getFiltro() {
        return filtro;
    }

    public void setFiltro(CharSequence filtro) {
        this.filtro = filtro;
    }

}
