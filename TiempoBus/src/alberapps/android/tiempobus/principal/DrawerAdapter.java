/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 *
 *  based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.android.tiempobus.principal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import alberapps.android.tiempobus.R;

/**
 * Adaptador Tiempos
 */
public class DrawerAdapter<T> extends ArrayAdapter<T> {

    private Context contexto;

    private List<T> mObjectsTextos;

    private List<T> mObjectsIconos;

    /**
     * Constructor
     *
     * @param context
     * @param textViewResourceId
     */
    public DrawerAdapter(Context context, int textViewResourceId, T[] textos, T[] iconos) {
        super(context, textViewResourceId, textos);

        mObjectsTextos = Arrays.asList(textos);
        mObjectsIconos = Arrays.asList(iconos);

        this.contexto = context;

    }

    /**
     * Genera la vista de cada uno de los items
     */
    @Override
    public View getView(int position, View v, ViewGroup parent) {

        //final BusLlegada bus = getItem(position);


        if (v == null) {
            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.drawer_list_item, null);

        }


        if (this.getCount() > 0) {
            //Noticias noticia = getItem(position);

            //String item = getItem(position);

            String itemTexto = (String) mObjectsTextos.get(position);
            String itemIcono = (String) mObjectsIconos.get(position);

            if (itemTexto != null) {

                TextView texto = (TextView) v.findViewById(R.id.textoItem);
                texto.setText(itemTexto);

                ImageView image = (ImageView) v.findViewById(R.id.textoIcon);

                int id = contexto.getResources().getIdentifier(itemIcono, "drawable", contexto.getPackageName());

                image.setImageResource(id);

            }
        }


        return v;
    }

    /**
     * carga en el adaptador
     *
     * @param datos
     */
    /*public void addAll(List<String> datos) {
        if (datos == null) {
            return;
        }

        for (int i = 0; i < datos.size(); i++) {
            add(datos.get(i));
        }
    }*/

}
