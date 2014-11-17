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
package alberapps.android.tiempobus.infolineas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.tam.mapas.PlaceMark;
import alberapps.java.tram.UtilidadesTRAM;

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

        final PlaceMark bus = getItem(position);

        if (bus != null) {
            numParada.setText(bus.getCodigoParada());
            descParada.setText(bus.getTitle());
            datos.setText("T: ".concat(bus.getLineas()));

            if (bus.getObservaciones() != null && !bus.getObservaciones().trim().equals("")) {

                datos.setText(datos.getText() + "\ni: " + bus.getObservaciones());

            }

        }

        TextView informacionText = (TextView) v.findViewById(R.id.infoparada_info);

        // Link informacion
        informacionText.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                ((InfoLineasTabsPager) contexto).irInformacion(bus);

            }

        });

        TextView cargarText = (TextView) v.findViewById(R.id.infoparada_cargar);

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


        // Link informacion
        informacionText.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                Intent i = new Intent(contexto, InfoLineasDatosParadaActivity.class);
                i.putExtra("DATOS_PARADA", bus);
                i.putExtra("DATOS_LINEA", ((InfoLineasTabsPager) contexto).linea);
                contexto.startActivity(i);

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

}
