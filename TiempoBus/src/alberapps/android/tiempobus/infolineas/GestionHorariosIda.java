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

import androidx.appcompat.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.tasks.LoadHorariosInfoLineasAsyncTask;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.horarios.DatosHorarios;
import alberapps.java.horarios.ProcesarHorarios;
import alberapps.java.tam.BusLinea;

/**
 * Gestion de datos del tram
 */
public class GestionHorariosIda {

    /**
     * Cotexto principal
     */
    private InfoLineasTabsPager context;

    private SharedPreferences preferencias;

    public GestionHorariosIda(InfoLineasTabsPager contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }


    /**
     * Carga los horarios
     */
    private void loadHorarios(BusLinea datosLinea) {

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            context.taskHorarios = new LoadHorariosInfoLineasAsyncTask(loadHorariosInfoLineasAsyncTaskResponder).execute(datosLinea);
        } else {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
            if (context.dialog != null && context.dialog.isShowing()) {
                context.dialog.dismiss();
            }
        }

    }


    /**
     * Se llama cuando las paradas hayan sido cargadas
     */
    LoadHorariosInfoLineasAsyncTask.LoadHorariosInfoLineasAsyncTaskResponder loadHorariosInfoLineasAsyncTaskResponder = new LoadHorariosInfoLineasAsyncTask.LoadHorariosInfoLineasAsyncTaskResponder() {
        public void datosHorariosInfoLineasLoaded(DatosHorarios datos) {

            if (datos != null) {

                context.datosHorarios = datos;

                cargarListadoHorarioIda();

                context.cambiarTab();

            } else {

                //datosHorarios = null;

                context.datosHorarios = new DatosHorarios();

                cargarListadoHorarioIda();

                Toast toast = Toast.makeText(context.getApplicationContext(), context.getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT);
                toast.show();
                context.dialog.dismiss();

                modalErrorHorario();

            }

            context.dialog.dismiss();

        }
    };


    /**
     * En caso de no poder cargar los horarios
     */
    private void modalErrorHorario() {

        if (context.linkHorario != null) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);

            dialog.setTitle(context.getString(R.string.infolinea_horarios));

            dialog.setMessage(context.getString(R.string.error_horarios_modal));
            dialog.setIcon(R.drawable.ic_tiempobus_5);

            dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {

                    dialog.dismiss();

                    UtilidadesUI.openWebPage(context, context.linkHorario);

                    context.linkHorario = null;

                }

            });

            dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {

                    dialog.dismiss();

                    context.linkHorario = null;

                }

            });

            dialog.show();

        }


    }


    /**
     * Carga lista con los horarios de ida
     */
    public void cargarListadoHorarioIda() {

        context.gestionIda.cargarHeaderIda(true, false, false);

        InfoLineaHorariosAdapter infoLineaHorariosAdapter = new InfoLineaHorariosAdapter(context, R.layout.infolineas_horarios_item);

        infoLineaHorariosAdapter.addAll(context.datosHorarios.getHorariosIda());

        ListView idaView = (ListView) context.findViewById(R.id.infolinea_lista_ida);
        idaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        if (idaView.getFooterViewsCount() == 0) {

            LayoutInflater li = LayoutInflater.from(context);

            context.vistaPieHorarioIda = li.inflate(R.layout.infolineas_horarios_item, null);

            TextView descHorario = (TextView) context.vistaPieHorarioIda.findViewById(R.id.desc_horario);

            descHorario.setText(context.getString(R.string.observaciones));

            idaView.addFooterView(context.vistaPieHorarioIda);

            // Pie aviso
            LayoutInflater li2 = LayoutInflater.from(context);
            context.vistaPieAvisoIda = li2.inflate(R.layout.infolineas_horarios_item, null);
            TextView descHorario2 = (TextView) context.vistaPieAvisoIda.findViewById(R.id.desc_horario);

            descHorario2.setText(context.getString(R.string.aviso_noticia));

            idaView.addFooterView(context.vistaPieAvisoIda);

        }

        TextView datosHorario = (TextView) context.vistaPieHorarioIda.findViewById(R.id.datos_horario);

        StringBuffer comentarios = new StringBuffer("");

        if (context.datosHorarios.getComentariosIda() != null && !context.datosHorarios.getComentariosIda().toString().equals("")) {
            comentarios.append(context.datosHorarios.getComentariosIda());
            comentarios.append("\n");
        }

        if (context.datosHorarios.getValidezHorarios() != null) {
            comentarios.append(context.datosHorarios.getValidezHorarios());
        }

        datosHorario.setText(comentarios.toString());

        // Aviso
        TextView datosHorario2 = (TextView) context.vistaPieAvisoIda.findViewById(R.id.datos_horario);
        datosHorario2.setAutoLinkMask(Linkify.ALL);
        datosHorario2.setLinksClickable(true);
        if (context.datosHorarios.getHorariosIda() != null && !context.datosHorarios.getHorariosIda().isEmpty()) {
            datosHorario2.setText(context.datosHorarios.getHorariosIda().get(0).getLinkHorario());
        }

        idaView.setAdapter(infoLineaHorariosAdapter);

        infoLineaHorariosAdapter.notifyDataSetChanged();

    }


    /**
     * Eliminar datos horarios
     */
    public void limpiarHorariosIda() {

        Log.d("INFOLINEAS", "limpiar horarios ida");

        context.datosHorarios = null;

        ListView idaView = (ListView) context.findViewById(R.id.infolinea_lista_ida);

        if (idaView != null && idaView.getFooterViewsCount() > 0) {
            idaView.removeFooterView(context.vistaPieHorarioIda);
            idaView.removeFooterView(context.vistaPieAvisoIda);
            context.vistaPieHorarioIda = null;
        }

    }


    public void cargarHorarios(BusLinea linea, int index) {

        // We can display everything in-place with fragments, so update
        // the list to highlight the selected item and show the data.
        ListView lineasView = (ListView) context.findViewById(R.id.infolinea_lista_lineas);

        lineasView.setItemChecked(index, true);

        context.lineasMapas = null;
        context.sentidoIda = null;
        context.sentidoVuelta = null;
        context.datosHorarios = null;
        context.linkHorario = ProcesarHorarios.LINEA_URL + linea.getIdlinea();

        if(context.dialog == null) {
            context.dialog = ProgressDialog.show(context, "", context.getString(R.string.dialogo_espera), true);
        }else{
            context.dialog.show();
        }

        loadHorarios(linea);


    }

}
