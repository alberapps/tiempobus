/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2014 Alberto Montiel
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
package alberapps.android.tiempobus.historial;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.data.Favorito;
import androidx.core.content.res.ResourcesCompat;

/**
 * Adaptador Historial
 */
public class HistorialAdapter extends ArrayAdapter<Favorito> {

    private Context contexto;

    /**
     * Constructor
     *
     * @param context
     * @param textViewResourceId
     */
    public HistorialAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        this.contexto = context;

    }

    /**
     * Genera la vista de cada uno de los items
     */
    @Override
    public View getView(int position, View v, ViewGroup parent) {

        final Favorito favorito = getItem(position);

        // Si no tenemos la vista de la fila creada componemos una
        if (v == null) {
            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.historial_item, null);

            v.setTag(new ViewHolder(v));
        }

        // Accedemos a la vista cacheada y la rellenamos
        ViewHolder tag = (ViewHolder) v.getTag();

        if (favorito != null) {

            tag.numParada.setText(favorito.getNumParada().trim());

            TextView texto = (TextView) v.findViewById(R.id.numParadaFav);

            texto.setTextColor(contexto.getResources().getColor(R.color.mi_material_blue_principal));

            tag.titulo.setText(favorito.getTitulo().trim());
            tag.descripcion.setText(favorito.getDescripcion().trim());


        }


        return v;
    }

    /*
     * Clase contendora de los elementos de la vista de fila para agilizar su
     * acceso
     */
    private class ViewHolder {
        TextView numParada;
        TextView titulo;
        TextView descripcion;

        public ViewHolder(View v) {
            numParada = (TextView) v.findViewById(R.id.numParadaFav);
            titulo = (TextView) v.findViewById(R.id.titulo);
            descripcion = (TextView) v.findViewById(R.id.descripcion);

            Typeface ubuntu = ResourcesCompat.getFont(contexto, R.font.ubuntu);
            numParada.setTypeface(ubuntu, Typeface.BOLD);
            titulo.setTypeface(ubuntu, Typeface.BOLD);
            descripcion.setTypeface(ubuntu);
        }

    }

    /**
     * favoritos al adapter
     *
     * @param favorito
     */
    public void addAll(List<Favorito> favorito) {
        if (favorito == null) {
            return;
        }

        for (int i = 0; i < favorito.size(); i++) {
            add(favorito.get(i));
        }
    }

}
