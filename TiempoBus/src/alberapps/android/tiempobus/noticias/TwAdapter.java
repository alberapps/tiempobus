/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.java.noticias.tw.ProcesarTwitter;
import alberapps.java.noticias.tw.TwResultado;
import alberapps.java.noticias.tw.tw4j.ProcesarTwitter4j;

/**
 * Datos lista de tw
 */
public class TwAdapter extends ArrayAdapter<TwResultado> {

    public TwAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public HashMap<String, Bitmap> imagenesCache = new HashMap<>();


    public View getView(final int position, View v, ViewGroup parent) {

        ViewHolder holder = new ViewHolder();

        if (v == null) {
            Context ctx = getContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.avisostw_item, null);

            // holder.progress = (ProgressBar)
            // convertView.findViewById(R.id.progress_spinner);
            v.setTag(holder);

        } else {

            holder = (ViewHolder) v.getTag();

        }

        //Asignacion holder
        holder.usuario = (TextView) v.findViewById(R.id.usuarioNombre);
        holder.noticiaText = (TextView) v.findViewById(R.id.noticia);
        holder.usuarioId = (TextView) v.findViewById(R.id.usuarioId);
        holder.fecha = (TextView) v.findViewById(R.id.fecha);
        holder.twwebText = (TextView) v.findViewById(R.id.tw_web);
        holder.imagen = (ImageView) v.findViewById(R.id.imagenTw);
        holder.imagen.setTag(getItem(position).getImagen());
        holder.position = position;
        holder.imagen.setVisibility(View.INVISIBLE);
        holder.imagen.setImageBitmap(null);


        if (this.getCount() > 0) {
            final TwResultado tw = getItem(position);
            if (tw != null) {

                TextView usuario = holder.usuario;
                TextView noticiaText = holder.noticiaText;

                ImageView imagen = holder.imagen;

                // Link de la imagen de usuario
                imagen.setOnClickListener(new OnClickListener() {

                    public void onClick(View view) {

                        String url = tw.getUrl();

                        Intent i = new Intent(Intent.ACTION_VIEW);

                        i.setData(Uri.parse(url));
                        getContext().startActivity(i);

                    }

                });

                TextView usuarioId = holder.usuarioId;
                TextView fecha = holder.fecha;

                usuario.setText(tw.getNombreCompleto());
                noticiaText.setText(tw.getMensaje().trim());
                usuarioId.setText(tw.getUsuario());
                fecha.setText(tw.getFecha());

                if (tw.getImagen() != null) {
                    // imagen.setImageBitmap(tw.getImagenBitmap());

                    if (imagenesCache.containsKey((String) holder.imagen.getTag())) {
                        holder.imagen.setVisibility(View.VISIBLE);
                        holder.imagen.setImageBitmap(imagenesCache.get((String) holder.imagen.getTag()));
                    } else {

                        // Using an AsyncTask to load the slow images in a
                        // background thread
                        new AsyncTask<ViewHolder, Void, Bitmap>() {
                            private ViewHolder v;

                            @Override
                            protected Bitmap doInBackground(ViewHolder... params) {
                                v = params[0];

                                Bitmap imagenRecuperada = null;

                                try {
                                    imagenRecuperada = ProcesarTwitter4j.recuperaImagen((String) v.imagen.getTag());

                                    Log.d("Twitter2", "Imagen recuperada: " + v.imagen.getTag());

                                } catch (Exception e) {
                                    return null;
                                }

                                return imagenRecuperada;
                            }

                            @Override
                            protected void onPostExecute(Bitmap result) {
                                super.onPostExecute(result);
                                if (v.position == position) {
                                    // If this item hasn't been recycled
                                    // already,
                                    // hide the
                                    // progress and set and show the image
                                    // v.progress.setVisibility(View.GONE);
                                    v.imagen.setVisibility(View.VISIBLE);
                                    v.imagen.setImageBitmap(result);

                                    imagenesCache.put((String) v.imagen.getTag(), result);

                                    Log.d("Twitter2", "Carga imagen");

                                }
                            }
                        }.execute(holder);

                    }

                }

                // Link del nombre de usuario
                usuario.setOnClickListener(new OnClickListener() {

                    public void onClick(View view) {

                        String url = tw.getUrl();

                        Intent i = new Intent(Intent.ACTION_VIEW);

                        i.setData(Uri.parse(url));
                        getContext().startActivity(i);

                    }

                });

                // Link del nombre de usuario
                usuarioId.setOnClickListener(new OnClickListener() {

                    public void onClick(View view) {

                        String url = tw.getUrl();

                        Intent i = new Intent(Intent.ACTION_VIEW);

                        i.setData(Uri.parse(url));
                        getContext().startActivity(i);

                    }

                });

                TextView twwebText = holder.twwebText;

                // Link de acceso a twitter
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
     * Todas
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

    /**
     * No mostrar las dos primeras
     */
    public void quitarIniciales(){

        remove(getItem(1));
        remove(getItem(0));

    }

    private static class ViewHolder {
        public TextView usuario;
        public TextView noticiaText;
        public TextView usuarioId;
        public TextView fecha;
        public TextView twwebText;
        public ImageView imagen;
        public ProgressBar progress;
        public int position;
    }

}
