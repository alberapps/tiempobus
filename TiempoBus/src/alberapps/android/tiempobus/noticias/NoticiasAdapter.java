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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.java.noticias.Noticias;

/**
 * Datos lista de noticias
 */
public class NoticiasAdapter extends ArrayAdapter<Noticias> {


    public NoticiasAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) {
            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.noticias_item, null);

        }

        if (this.getCount() > 0) {
            Noticias noticia = getItem(position);
            if (noticia != null) {

                TextView fecha = (TextView) v.findViewById(R.id.fecha);
                TextView noticiaText = (TextView) v.findViewById(R.id.noticia);

                fecha.setText(noticia.getFecha().substring(0, 10));
                noticiaText.setText(noticia.getNoticia().trim());

            }
        }

        return v;
    }

    /**
     * carga en el adaptador
     *
     * @param noticias
     */
    public void addAll(List<Noticias> noticias) {
        if (noticias == null) {
            return;
        }

        for (int i = 0; i < noticias.size(); i++) {
            add(noticias.get(i));
        }
    }

}
