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
package alberapps.android.tiempobus.noticias;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.java.noticias.tw.ProcesarTwitter;
import alberapps.java.noticias.tw.TwResultado;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Datos lista de tw
 * 
 */
public class TwAdapter extends ArrayAdapter<TwResultado> {
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 */
	public TwAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	/**
	 * Genera la vista de cada uno de los items del listado
	 */
	public View getView(int position, View v, ViewGroup parent) {
		if (v == null) {
			Context ctx = getContext();
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			v = vi.inflate(R.layout.avisostw_item, null);

		}

		if (this.getCount() > 0) {
			final TwResultado tw = getItem(position);
			if (tw != null) {

				TextView usuario = (TextView) v.findViewById(R.id.usuarioNombre);
				TextView noticiaText = (TextView) v.findViewById(R.id.noticia);

				ImageView imagen = (ImageView) v.findViewById(R.id.imagenTw);

				//Link de la imagen de usuario
				imagen.setOnClickListener(new OnClickListener() {

					public void onClick(View view) {

						String url = tw.getUrl();

						Intent i = new Intent(Intent.ACTION_VIEW);

						i.setData(Uri.parse(url));
						getContext().startActivity(i);

					}

				});

				TextView usuarioId = (TextView) v.findViewById(R.id.usuarioId);
				TextView fecha = (TextView) v.findViewById(R.id.fecha);

				usuario.setText(tw.getNombreCompleto());
				noticiaText.setText(tw.getMensaje().trim());
				usuarioId.setText(tw.getUsuario());
				fecha.setText(tw.getFecha());

				if (tw.getImagenBitmap() != null) {
					imagen.setImageBitmap(tw.getImagenBitmap());
				}
				
				//Link del nombre de usuario
				usuario.setOnClickListener(new OnClickListener() {

					public void onClick(View view) {

						String url = tw.getUrl();

						Intent i = new Intent(Intent.ACTION_VIEW);

						i.setData(Uri.parse(url));
						getContext().startActivity(i);

					}

				});
				
				//Link del nombre de usuario
				usuarioId.setOnClickListener(new OnClickListener() {

					public void onClick(View view) {

						String url = tw.getUrl();

						Intent i = new Intent(Intent.ACTION_VIEW);

						i.setData(Uri.parse(url));
						getContext().startActivity(i);

					}

				});
				
				TextView twwebText = (TextView) v.findViewById(R.id.tw_web);
				
				//Link de acceso a twitter
				twwebText.setOnClickListener(new OnClickListener() {

					public void onClick(View view) {

						String url = tw.getUrl() + ProcesarTwitter.TW_STATUS + tw.getId();

						Intent i = new Intent(Intent.ACTION_VIEW);

						i.setData(Uri.parse(url));
						getContext().startActivity(i);

					}

				});

			}
		}

		return v;
	}

	/**
	 * Anade todas al adapter
	 * 
	 * @param tw
	 */
	public void addAll(List<TwResultado> tw) {
		if (tw == null) {
			return;
		}

		for (int i = 0; i < tw.size(); i++) {
			add(tw.get(i));
		}
	}

}
