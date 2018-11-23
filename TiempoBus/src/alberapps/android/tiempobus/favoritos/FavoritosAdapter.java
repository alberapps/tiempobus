/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2014 Alberto Montiel
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
package alberapps.android.tiempobus.favoritos;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.data.Favorito;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.java.util.Datos;
import androidx.core.content.res.ResourcesCompat;

/**
 * Adaptador Favoritos
 */
public class FavoritosAdapter extends ArrayAdapter<Favorito> {

    private Context contexto;

    private List<Datos> listaDestacados;


    /**
     * Constructor
     *
     * @param context
     * @param textViewResourceId
     */
    public FavoritosAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        this.contexto = context;

        listaDestacados = PreferencesUtil.recuperarLista(context, PreferencesUtil.LISTA_PARADAS_DESTACADAS);

    }

    /**
     * Genera la vista de cada uno de los items
     */
    @Override
    public View getView(int position, View v, ViewGroup parent) {

        final Favorito favorito = getItem(position);

        String datosHorario = null;

        // Si no tenemos la vista de la fila creada componemos una
        if (v == null) {
            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.favoritos_item, null);

            v.setTag(new ViewHolder(v));
        }

        // Accedemos a la vista cacheada y la rellenamos
        ViewHolder tag = (ViewHolder) v.getTag();

        if (favorito != null) {

            ImageView compartir = (ImageView) v.findViewById(R.id.compartir_img);

            if (favorito.getNumParada().equals("0")) {

                tag.numParada.setText("HT");
                TextView texto = (TextView) v.findViewById(R.id.numParadaFav);
                texto.setTextColor(contexto.getResources().getColor(R.color.tram_l3));
                tag.titulo.setText(favorito.getTitulo().trim());
                String[] desc = favorito.getDescripcion().trim().split("::");
                datosHorario = desc[1];
                tag.descripcion.setText(desc[0]);

                compartir.setVisibility(View.INVISIBLE);

            } else {

                tag.numParada.setText(favorito.getNumParada().trim());
                TextView texto = (TextView) v.findViewById(R.id.numParadaFav);
                texto.setTextColor(contexto.getResources().getColor(R.color.mi_material_blue_principal));
                tag.titulo.setText(favorito.getTitulo().trim());
                tag.descripcion.setText(favorito.getDescripcion().trim());

                compartir.setVisibility(View.VISIBLE);

            }


        }

        // Botones
        ImageView favoritoEditar = (ImageView) v.findViewById(R.id.favorito_editar);

        favoritoEditar.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                FavoritosActivity actividad = (FavoritosActivity) contexto;

                if (favorito != null) {
                    actividad.launchModificarFavorito(Integer.parseInt(favorito.getId()));
                }

            }

        });

        ImageView compartir = (ImageView) v.findViewById(R.id.compartir_img);

        compartir.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                FavoritosActivity actividad = (FavoritosActivity) contexto;

                actividad.shareFavorito(favorito);

            }

        });

        ImageView favoritoBorrar = (ImageView) v.findViewById(R.id.favorito_borrar);

        favoritoBorrar.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                FavoritosActivity actividad = (FavoritosActivity) contexto;

                if (favorito != null) {
                    actividad.launchBorrarFavorito(Integer.parseInt(favorito.getId()));
                    PreferencesUtil.eliminarParada(contexto, PreferencesUtil.LISTA_PARADAS_DESTACADAS, favorito.getNumParada());
                }

            }

        });


        AppCompatImageView favoritoDestacar = v.findViewById(R.id.favorito_destacar);

        Datos dato = new Datos();
        dato.setParada(favorito.getNumParada());

        if (listaDestacados.contains(dato)) {
            favoritoDestacar.setImageResource(R.drawable.ic_favorite_24dp);
        } else {
            favoritoDestacar.setImageResource(R.drawable.ic_favorite_border_24dp);
        }

        favoritoDestacar.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                AppCompatImageView favoritoDestacar2 = view.findViewById(R.id.favorito_destacar);

                Datos dato = new Datos();
                dato.setParada(favorito.getNumParada());

                if (!listaDestacados.contains(dato)) {
                    PreferencesUtil.guardarParada(contexto, PreferencesUtil.LISTA_PARADAS_DESTACADAS, favorito.getNumParada());
                    favoritoDestacar2.setImageResource(R.drawable.ic_favorite_24dp);
                } else {
                    PreferencesUtil.eliminarParada(contexto, PreferencesUtil.LISTA_PARADAS_DESTACADAS, favorito.getNumParada());
                    favoritoDestacar2.setImageResource(R.drawable.ic_favorite_border_24dp);
                }

                listaDestacados = PreferencesUtil.recuperarLista(contexto, PreferencesUtil.LISTA_PARADAS_DESTACADAS);

                Toast.makeText(contexto.getApplicationContext(), contexto.getString(R.string.favorito_destacado_aviso), Toast.LENGTH_LONG).show();

            }

        });


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

            try {
                Typeface ubuntu = ResourcesCompat.getFont(contexto, R.font.ubuntu);
                numParada.setTypeface(ubuntu, Typeface.BOLD);
                titulo.setTypeface(ubuntu, Typeface.BOLD);
                descripcion.setTypeface(ubuntu);
            }catch (Exception e){
                e.printStackTrace();
            }

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

        if (listaDestacados.size() > 0) {

            Datos dato = null;
            List<Favorito> favoritoDesc = new ArrayList<>();
            List<Favorito> favoritoNo = new ArrayList<>();

            for (int i = 0; i < favorito.size(); i++) {
                dato = new Datos();
                dato.setParada(favorito.get(i).getNumParada());
                if (listaDestacados.contains(dato)) {
                    favoritoDesc.add(favorito.get(i));
                } else {
                    favoritoNo.add(favorito.get(i));
                }
            }

            favorito.clear();
            favorito.addAll(favoritoDesc);
            favorito.addAll(favoritoNo);

        }


        for (int i = 0; i < favorito.size(); i++) {
            add(favorito.get(i));
        }
    }


}
