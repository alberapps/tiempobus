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
package alberapps.android.tiempobus.mapas;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.util.UtilidadesUI;

/**
 * Adaptador de informacion de lineas
 */
public class LineasArrayAdapter extends ArrayAdapter<SpinnerItem> implements Filterable {

    private MapasActivity contexto;

    List<SpinnerItem> listaOriginal;

    CharSequence filtro;


    /**
     * Constructor
     *
     * @param context
     * @param textViewResourceId
     */
    public LineasArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        this.contexto = ((MapasActivity) context);

    }



    /**
     * Genera la vista de cada uno de los items
     */
    /*@Override
    public View getView(final int position, View v, ViewGroup parent) {
        // Si no tenemos la vista de la fila creada componemos una
        if (v == null) {
            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(android.R.layout.simple_spinner_item, null);

        }

        //Control de vista correcta por el filtro
        /*if (v.findViewById(R.id.bus_linea) == null) {
            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.infolineas_item, null);
        }*/


       /* return v;
    }*/

    /**
     * Anade todas las lineas al adapter
     *
     * @param datos
     */
    public void addAll(List<SpinnerItem> datos) {
        if (datos == null) {
            return;
        }

        clear();

        for (int i = 0; i < datos.size(); i++) {
            add(datos.get(i));
        }

        //addAll(datos);

        listaOriginal = new ArrayList<>(datos);

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

                List<SpinnerItem> filtrada = new ArrayList<>();

                if (constraint != null && !constraint.toString().equals("")) {

                    for (int i = 0; i < listaOriginal.size(); i++) {

                        if (listaOriginal.get(i).descripcion.toLowerCase(UtilidadesUI.getLocaleInt()).contains(constraint.toString().toLowerCase(UtilidadesUI.getLocaleInt()))) {
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

                List<SpinnerItem> lista = (List<SpinnerItem>) results.values;

                notifyDataSetChanged();
                clear();

                for (int i = 0; i < lista.size(); i++) {
                    add(lista.get(i));
                }

                /*if (getCount() == 0) {
                    SpinnerItem b1 = new SpinnerItem();
                    b1.setFiltroSinDatos(true);
                    add(b1);
                }*/

                notifyDataSetInvalidated();

                notifyDataSetChanged();

                //final TextView textoBuscar = (TextView) contexto.findViewById(R.id.texto_buscar);
                //textoBuscar.requestFocus();

            }

        };

        return filter;
    }

    /**
     * Lista con el filtro
     *
     * @return
     */
    public List<SpinnerItem> getListaFiltrada() {

        List<SpinnerItem> lista = new ArrayList<>();

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
