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
package alberapps.android.tiempobus.infolineas;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.tam.mapas.PlaceMark;
import alberapps.java.tram.UtilidadesTRAM;
import androidx.core.content.res.ResourcesCompat;

/**
 * Adaptador listado de paradas
 */
public class InfoLineaParadasAdapter extends ArrayAdapter<PlaceMark> {

    private Context contexto;

    /**
     * Constructor
     *
     * @param context
     * @param textViewResourceId
     */
    public InfoLineaParadasAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        this.contexto = context;

    }

    /**
     * Genera la vista de cada uno de los items
     */
    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        // Si no tenemos la vista de la fila creada componemos una
        if (v == null) {
            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(R.layout.infolineas_paradas_item, null);

        }

        TextView numParada;
        TextView descParada;
        TextView datos;

        numParada = (TextView) v.findViewById(R.id.num_parada);
        descParada = (TextView) v.findViewById(R.id.desc_parada);
        datos = (TextView) v.findViewById(R.id.datos_parada);

        Typeface ubuntu = ResourcesCompat.getFont(contexto, R.font.ubuntu);

        numParada.setTypeface(ubuntu, Typeface.BOLD);
        if(descParada != null) {
            descParada.setTypeface(ubuntu, Typeface.BOLD);
        }

        if (datos != null) {
            datos.setTypeface(ubuntu);
            datos.setText("");
        }

        final PlaceMark bus = getItem(position);

        if (bus != null) {
            numParada.setText(bus.getCodigoParada());
            descParada.setText(bus.getTitle());
            //datos.setText("T: ".concat(bus.getLineas()));

            if (datos != null) {
                if (bus.getObservaciones() != null && !bus.getObservaciones().trim().equals("")) {


                    datos.setText(datos.getText() + "\ni: " + bus.getObservaciones());


                } else {
                    LinearLayout bloqueDatos = (LinearLayout) v.findViewById(R.id.bloque_datos);
                    bloqueDatos.removeView(datos);
                }
            }

            mostrarLineasParada(contexto, v, bus.getLineas());

        }


        TextView cargarText = (TextView) v.findViewById(R.id.infoparada_cargar);

        cargarText.setTypeface(ubuntu);

        if (!UtilidadesTRAM.ACTIVADO_L9 && ((InfoLineasTabsPager) contexto).getLinea().getNumLinea().equals("L9")) {
            cargarText.setVisibility(View.INVISIBLE);
        } else {

            // Link cargar
            cargarText.setOnClickListener(new OnClickListener() {

                public void onClick(View view) {

                    int codigo = -1;

                    try {
                        codigo = Integer.parseInt(bus.getCodigoParada());

                    } catch (Exception e) {

                    }

                    if (codigo != -1 && (bus.getCodigoParada().length() == 4 || DatosPantallaPrincipal.esTram(bus.getCodigoParada()))) {

                        ((InfoLineasTabsPager) contexto).cargarTiempos(codigo);

                    } else {

                        Toast.makeText(contexto.getApplicationContext(), contexto.getString(R.string.error_codigo), Toast.LENGTH_SHORT).show();

                    }

                }

            });

        }

        TextView informacionText = (TextView) v.findViewById(R.id.infoparada_info);
        informacionText.setTypeface(ubuntu);

        // Link informacion
        informacionText.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                ((InfoLineasTabsPager) contexto).irInformacion(bus);

            }

        });


        return v;
    }

    /**
     * Anade todas las lineas al adapter
     *
     * @param parada
     */
    public void addAll(List<PlaceMark> parada) {
        if (parada == null) {
            return;
        }

        for (int i = 0; i < parada.size(); i++) {
            add(parada.get(i));
        }
    }

    /**
     * Cargar lineas con conexion
     *
     * @param contexto
     * @param v
     * @param conexiones
     */
    public static void mostrarLineasParada(Context contexto, View v, String conexiones) {

        //Lineas con parada
        LinearLayout lineasParada = (LinearLayout) v.findViewById(R.id.lineas_parada);
        LinearLayout lineasParada2 = (LinearLayout) v.findViewById(R.id.lineas_parada_2);

        lineasParada.removeAllViews();
        lineasParada2.removeAllViews();


        String[] conexionesList = conexiones.split(",");

        int posicionMax = conexionesList.length;

        if (conexionesList.length > 5) {
            posicionMax = 5;
        }

        for (int i = 0; i < posicionMax; i++) {
            lineasParada.addView(incluirTexto(contexto, conexionesList[i]));
        }

        if (posicionMax != conexionesList.length) {

            for (int i = posicionMax; i < conexionesList.length; i++) {
                lineasParada2.addView(incluirTexto(contexto, conexionesList[i]));
            }

        }


    }


    /**
     * Construir textview de la linea
     *
     * @param contexto
     * @param conexion
     * @return
     */
    public static FrameLayout incluirTexto(Context contexto, String conexion) {

        FrameLayout fl = new FrameLayout(contexto);
        fl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        fl.setPadding(2, 5, 5, 2);

        AppCompatTextView texto = new AppCompatTextView(contexto);
        texto.setText(conexion.trim());
        texto.setTextAppearance(contexto, R.style.TextAppearance_AppCompat_Small);
        texto.setTextColor(ContextCompat.getColor(contexto, R.color.abc_primary_text_disable_only_material_dark));

        int size50 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, contexto.getResources().getDisplayMetrics());

        texto.setLayoutParams(new ViewGroup.LayoutParams(size50, size50));
        texto.setGravity(Gravity.CENTER);
        //texto.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        Typeface ubuntu = ResourcesCompat.getFont(contexto, R.font.ubuntu);
        texto.setTypeface(ubuntu, Typeface.BOLD);

        DatosPantallaPrincipal.formatoLinea(contexto, texto, conexion, false);

        //Size
        if (conexion.trim().length() > 2) {
            texto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        } else {
            texto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }


        fl.addView(texto);

        return fl;


    }

}
