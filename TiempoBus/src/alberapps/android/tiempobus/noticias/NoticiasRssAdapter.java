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

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.java.noticias.rss.NoticiaRss;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Lista de noticias de RSS
 * 
 */
public class NoticiasRssAdapter extends ArrayAdapter<NoticiaRss> {
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 */
	public NoticiasRssAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	/**
	 * Genera la vista de cada uno de los items del listado
	 */
	public View getView(int position, View v, ViewGroup parent) {
		if (v == null) {
			Context ctx = this.getContext().getApplicationContext();
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			v = vi.inflate(R.layout.noticias_rss_item, null);

		}

		if (this.getCount() > 0) {
			NoticiaRss noticia = getItem(position);
			if (noticia != null) {

				TextView titulo = (TextView) v.findViewById(R.id.titulo_rss);
				TextView descripcion = (TextView) v.findViewById(R.id.descripcion_rss);

				titulo.setText(noticia.getTitulo());
				descripcion.setText(noticia.getDescripcion());

			}
		}

		return v;
	}

	/**
	 * noticias al adapter
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
