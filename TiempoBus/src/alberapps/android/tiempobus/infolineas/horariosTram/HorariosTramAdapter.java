/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
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
package alberapps.android.tiempobus.infolineas.horariosTram;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.tram.horarios.HorarioItem;
import androidx.core.content.res.ResourcesCompat;

/**
 * Adaptador de listados de horarios
 */
public class HorariosTramAdapter extends ArrayAdapter<HorarioItem> {

    private Context contexto;

    /**
     * Constructor
     *
     * @param context
     * @param textViewResourceId
     */
    public HorariosTramAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        this.contexto = context;

    }

    /**
     * Genera la vista de cada uno de los items
     */
    @Override
    public View getView(final int position, View v, ViewGroup parent) {

        final HorarioItem horas = getItem(position);

        Typeface ubuntu = ResourcesCompat.getFont(contexto, R.font.ubuntu);

        if (horas != null && !horas.isErrorServicio() && !horas.isSinDatos() && horas != null) {


            if (horas.getInfoRecorrido() != null && !horas.getInfoRecorrido().isEmpty()) {

                Context ctx = this.getContext().getApplicationContext();
                LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.infolinea_horarios_tram_info, null);

                TextView text = (TextView) v.findViewById(R.id.txt_datos);
                text.setTypeface(ubuntu);

                StringBuffer datos = new StringBuffer("");

                for (int i = 0; i < horas.getInfoRecorrido().size(); i++) {

                    if (i > 0) {
                        datos.append("\n");
                    }

                    if (i == 0) {
                        datos.append("- ");
                        datos.append(ctx.getString(R.string.duracion));
                        datos.append(": ");
                    } else if (i == 1) {
                        datos.append("- ");
                        datos.append(ctx.getString(R.string.tipo_billete));
                        datos.append(": ");
                    }else if (i == 2) {
                        datos.append("- ");
                        datos.append(ctx.getString(R.string.transbordos));
                        datos.append(": ");
                    }

                    datos.append(horas.getInfoRecorrido().get(i));

                }

                text.setText(datos.toString());


            } else {


                // Si no tenemos la vista de la fila creada componemos una
                if (v == null) {
                    Context ctx = this.getContext().getApplicationContext();
                    LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    v = vi.inflate(R.layout.infolineas_horarios_tram_item, null);

                }

                TextView datosGrupoHora;
                TextView datosHoras;
                TextView datosInfo;

                datosGrupoHora = (TextView) v.findViewById(R.id.datos_grupo_hora);
                datosHoras = (TextView) v.findViewById(R.id.datos_horas);
                datosInfo = (TextView) v.findViewById(R.id.datos_info);


                datosGrupoHora.setTypeface(ubuntu, Typeface.BOLD);
                datosHoras.setTypeface(ubuntu, Typeface.BOLD);
                datosInfo.setTypeface(ubuntu, Typeface.BOLD);

                if (datosGrupoHora == null) {
                    Context ctx = this.getContext().getApplicationContext();
                    LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.infolineas_horarios_tram_item, null);

                    datosGrupoHora = (TextView) v.findViewById(R.id.datos_grupo_hora);
                    datosHoras = (TextView) v.findViewById(R.id.datos_horas);
                    datosInfo = (TextView) v.findViewById(R.id.datos_info);

                }

                datosGrupoHora.setText(horas.getGrupoHora() + "h");
                datosHoras.setText(horas.getHoras());
                datosInfo.setText(horas.getDatoInfo());

                //Formato colores
                DatosPantallaPrincipal.formatoLinea(contexto, datosGrupoHora, horas.getLinea(), false);
            }


        } else if (horas != null && horas.isErrorServicio()) {

            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.tiempos_item_sin_datos, null);

            TextView text = (TextView) v.findViewById(R.id.txt_sin_datos);
            text.setTypeface(ubuntu, Typeface.BOLD);

            text.setText(ctx.getString(R.string.error_tiempos));

            TextView textAviso = (TextView) v.findViewById(R.id.txt_sin_datos_aviso);
            textAviso.setTypeface(ubuntu, Typeface.BOLD);

            String aviso = "";

            ImageView imagenAviso = (ImageView) v.findViewById(R.id.imageAviso);
            imagenAviso.setImageResource(R.drawable.ic_warning_black_48dp);

            textAviso.setText(aviso);

        } else if (horas != null && horas.isSinDatos()) {

            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.tiempos_item_sin_datos, null);

            TextView text = (TextView) v.findViewById(R.id.txt_sin_datos);
            text.setTypeface(ubuntu, Typeface.BOLD);

            text.setText(ctx.getString(R.string.main_no_items));

            TextView textAviso = (TextView) v.findViewById(R.id.txt_sin_datos_aviso);
            textAviso.setTypeface(ubuntu, Typeface.BOLD);

            String aviso = "";

            textAviso.setText(aviso);

        }

        return v;
    }

    /**
     * Anade todas las lineas al adapter
     *
     * @param horarios
     */
    public void addAll(List<HorarioItem> horarios) {
        if (horarios == null) {
            return;
        }

        //Control error al recuperar los datos
        if (horarios.size() == 1 && horarios.get(0).getHoras() == null) {
            horarios.get(0).setErrorServicio(true);
        }

        for (int i = 0; i < horarios.size(); i++) {
            add(horarios.get(i));
        }
    }

}
