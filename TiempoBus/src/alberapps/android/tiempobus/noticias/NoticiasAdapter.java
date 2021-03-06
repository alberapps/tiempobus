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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.java.noticias.Noticias;
import alberapps.java.util.Utilidades;

/**
 * Datos lista de noticias
 */
public class NoticiasAdapter extends ArrayAdapter<Noticias> {

    private Context contexto;


    public NoticiasAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        this.contexto = context;

    }

    public View getView(int position, View v, ViewGroup parent) {

        Noticias noticia = getItem(position);

        Typeface ubuntu = null;

        try {
            ubuntu = ResourcesCompat.getFont(contexto, R.font.ubuntu);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!noticia.isSinDatos() && !noticia.isErrorServicio()) {

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.noticias_item, null);
            }

            if (this.getCount() > 0) {

                if (noticia != null) {

                    TextView fecha = (TextView) v.findViewById(R.id.fecha);
                    TextView noticiaText = (TextView) v.findViewById(R.id.noticia);
                    TextView noticiaLineasText = (TextView) v.findViewById(R.id.noticia_lineas);

                    if (ubuntu != null) {
                        fecha.setTypeface(ubuntu, Typeface.BOLD);
                        noticiaText.setTypeface(ubuntu);
                        noticiaLineasText.setTypeface(ubuntu);
                    }

                    if (noticia.getFechaDoble() != null) {
                        fecha.setText(noticia.getFechaDoble());
                    } else if (noticia.getFecha() != null) {
                        fecha.setText(Utilidades.getFechaStringSinHora(noticia.getFecha()));
                    } else {
                        fecha.setText(contexto.getString(R.string.sin_fecha));
                    }
                    noticiaText.setText(noticia.getNoticia().trim());

                    String noticiaLineas = "";

                    if (noticia.getNoticiaLineas().length() > 400) {
                        noticiaLineas = noticia.getNoticiaLineas().substring(0, 400) + "...";
                    } else {
                        noticiaLineas = noticia.getNoticiaLineas();
                    }

                    noticiaLineasText.setText(noticiaLineas.trim());

                }
            }

        } else {

            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.tiempos_item_sin_datos, null);

            TextView text = (TextView) v.findViewById(R.id.txt_sin_datos);
            if (ubuntu != null) {
                text.setTypeface(ubuntu, Typeface.BOLD);
            }


            if (noticia != null && noticia.isErrorServicio()) {
                text.setText(contexto.getString(R.string.error_tiempos));
            } else {
                text.setText(contexto.getString(R.string.main_no_items) + "\n" + contexto.getString(R.string.error_status));
            }

            TextView textAviso = (TextView) v.findViewById(R.id.txt_sin_datos_aviso);
            if (ubuntu != null) {
                textAviso.setTypeface(ubuntu, Typeface.BOLD);
            }

            String aviso = "";

            aviso = contexto.getString(R.string.tlf_subus);

            ImageView imagenAviso = (ImageView) v.findViewById(R.id.imageAviso);
            imagenAviso.setImageResource(R.drawable.ic_warning_black_48dp);


            textAviso.setText(aviso);


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
