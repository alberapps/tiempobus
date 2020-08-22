/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
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
package alberapps.android.tiempobus.infolineas;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.java.horarios.Horario;

/**
 * Adaptador de listados de horarios
 */
public class InfoLineaHorariosAdapter extends ArrayAdapter<Horario> {

    private Context contexto;

    /**
     * Constructor
     *
     * @param context
     * @param textViewResourceId
     */
    public InfoLineaHorariosAdapter(Context context, int textViewResourceId) {
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
            LayoutInflater vi = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.infolineas_horarios_item, null);
        }


        TextView descHorario;
        TextView datosHorario;


        descHorario = (TextView) v.findViewById(R.id.desc_horario);
        datosHorario = (TextView) v.findViewById(R.id.datos_horario);

        Typeface ubuntu = null;

        try {
            ubuntu = ResourcesCompat.getFont(contexto, R.font.ubuntu);
            descHorario.setTypeface(ubuntu, Typeface.BOLD);
            datosHorario.setTypeface(ubuntu, Typeface.BOLD);
        } catch (Exception e) {
            e.printStackTrace();
        }


        final Horario horario = getItem(position);

        if (horario != null) {

            descHorario.setText(horario.getTituloHorario());

            StringBuilder sb = new StringBuilder(200);

            int horaAnterior = -1;

            for (int i = 0; i < horario.getHorarios().size(); i++) {

                try {

                    String[] num = horario.getHorarios().get(i).split(":");

                    int hora = Integer.parseInt(num[0]);

                    if (horaAnterior != hora) {
                        horaAnterior = hora;
                        if (i > 0) {
                            sb.append("<br/><br/>");
                        } else {
                            sb.append("<br/>");
                        }
                        sb.append("<font color='#4f5d72'><strong>" + hora + "h:</strong></font>&nbsp;");
                    } else {
                        if (i > 0) {
                            sb.append("&nbsp;");
                        }
                    }

                    sb.append(horario.getHorarios().get(i));


                } catch (Exception e) {
                    continue;
                }


            }

            datosHorario.setText(Html.fromHtml(sb.toString()));

        }


        return v;
    }

    /**
     * Anade todas las lineas al adapter
     *
     * @param horarios
     */
    public void addAll(List<Horario> horarios) {
        if (horarios == null) {
            return;
        }

        for (int i = 0; i < horarios.size(); i++) {
            add(horarios.get(i));
        }
    }

}
