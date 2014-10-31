/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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
package alberapps.android.tiempobus.infolineas;

import android.content.SharedPreferences;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TextView;

import alberapps.android.tiempobus.R;
import alberapps.java.horarios.ProcesarHorarios;

/**
 * Gestion de datos del tram
 */
public class GestionHorariosVuelta {

    /**
     * Cotexto principal
     */
    private InfoLineasTabsPager context;

    private SharedPreferences preferencias;

    public GestionHorariosVuelta(InfoLineasTabsPager contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }


    /**
     * Carga lista con los horarios de vuelta
     */
    public void cargarListadoHorarioVuelta() {

        InfoLineaHorariosAdapter infoLineaHorariosAdapter = new InfoLineaHorariosAdapter(context, R.layout.infolineas_horarios_item);

        infoLineaHorariosAdapter.addAll(context.datosHorarios.getHorariosVuelta());

        ListView vueltaView = (ListView) context.findViewById(R.id.infolinea_lista_vuelta);
        // idaView.setOnItemClickListener(idaClickedHandler);

        if (vueltaView.getFooterViewsCount() == 0) {

            LayoutInflater li = LayoutInflater.from(context);
            context.vistaPieHorarioVuelta = li.inflate(R.layout.infolineas_horarios_item, null);

            TextView descHorario = (TextView) context.vistaPieHorarioVuelta.findViewById(R.id.desc_horario);

            descHorario.setText(context.getString(R.string.observaciones));

            vueltaView.addFooterView(context.vistaPieHorarioVuelta);

            // Pie aviso
            LayoutInflater li2 = LayoutInflater.from(context);
            context.vistaPieAvisoVuelta = li2.inflate(R.layout.infolineas_horarios_item, null);
            TextView descHorario2 = (TextView) context.vistaPieAvisoVuelta.findViewById(R.id.desc_horario);

            descHorario2.setText(context.getString(R.string.aviso_noticia));

            vueltaView.addFooterView(context.vistaPieAvisoVuelta);

        }

        TextView datosHorario = (TextView) context.vistaPieHorarioVuelta.findViewById(R.id.datos_horario);

        StringBuffer comentarios = new StringBuffer("");

        if (context.datosHorarios.getComentariosVuelta() != null && !context.datosHorarios.getComentariosVuelta().equals("")) {
            comentarios.append(context.datosHorarios.getComentariosVuelta());
            comentarios.append("\n");
        }

        if (context.datosHorarios.getValidezHorarios() != null) {
            comentarios.append(context.datosHorarios.getValidezHorarios());
        }

        datosHorario.setText(comentarios);

        // Aviso
        TextView datosHorario2 = (TextView) context.vistaPieAvisoVuelta.findViewById(R.id.datos_horario);
        datosHorario2.setAutoLinkMask(Linkify.ALL);
        datosHorario2.setLinksClickable(true);
        if (context.datosHorarios.getHorariosIda() != null && !context.datosHorarios.getHorariosIda().isEmpty()) {
            datosHorario2.setText(ProcesarHorarios.URL_SUBUS + context.datosHorarios.getHorariosIda().get(0).getLinkHorario());
        }

        vueltaView.setAdapter(infoLineaHorariosAdapter);

        infoLineaHorariosAdapter.notifyDataSetChanged();

    }


    /**
     * Eliminar datos horarios
     */
    public void limpiarHorariosVuelta() {

        context.datosHorarios = null;

        ListView vueltaView = (ListView) context.findViewById(R.id.infolinea_lista_vuelta);

        if (vueltaView.getFooterViewsCount() > 0) {
            vueltaView.removeFooterView(context.vistaPieHorarioVuelta);
            vueltaView.removeFooterView(context.vistaPieAvisoVuelta);
            context.vistaPieHorarioVuelta = null;
        }

    }


}
