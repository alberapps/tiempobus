/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p>
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.noticias;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.rss.NoticiaRss;
import alberapps.java.util.Utilidades;

/**
 * Lista de noticias de RSS
 */
public class NoticiasRssAdapter extends ArrayAdapter<NoticiaRss> {


    public NoticiasRssAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }


    public View getView(int position, View v, ViewGroup parent) {

        Context ctx = this.getContext();

        Typeface ubuntu = null;

        try {
            ubuntu = ResourcesCompat.getFont(ctx, R.font.ubuntu);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (v == null) {

            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.noticias_rss_item, parent, false);

        }

        if (this.getCount() > 0) {
            NoticiaRss noticia = getItem(position);
            if (noticia != null) {

                TextView titulo = (TextView) v.findViewById(R.id.titulo_rss);
                TextView descripcion = (TextView) v.findViewById(R.id.descripcion_rss);
                TextView link = (TextView) v.findViewById(R.id.link_rss);

                if (ubuntu != null) {
                    titulo.setTypeface(ubuntu, Typeface.BOLD);
                    descripcion.setTypeface(ubuntu);
                }

                String text = Utilidades.getFechaString2(noticia.getDateItem()) + "\n" + noticia.getTitulo();

                titulo.setText(text);
                descripcion.setText(noticia.getDescripcion());
                link.setText(noticia.getLink());

                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UtilidadesUI.openWebPage(getContext(), noticia.getLink());
                    }
                });

            }
        }

        return v;
    }

    /**
     * todas
     *
     * @param noticias
     */
    public void addAll(List<NoticiaRss> noticias) {
        if (noticias == null) {
            return;
        }

        for (int i = 0; i < noticias.size(); i++) {
            add(noticias.get(i));
        }
    }


}
