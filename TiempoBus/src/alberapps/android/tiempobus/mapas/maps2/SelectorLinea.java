/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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
package alberapps.android.tiempobus.mapas.maps2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask.LoadDatosLineasAsyncTaskResponder;
import alberapps.java.tam.BusLinea;
import alberapps.java.util.Utilidades;

/**
 * Modal seleccion de linea
 */
public class SelectorLinea {

    private MapasMaps2Activity context;

    private SharedPreferences preferencias;

    private LinkedList<SpinnerItem> listaSpinner = new LinkedList<SpinnerItem>();

    public SelectorLinea(MapasMaps2Activity contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }

    /**
     * Nuevo selector de tiempos
     */
    public void mostrarModalSelectorLinea() {

        AlertDialog.Builder dialogSeleccion = new AlertDialog.Builder(context);

        dialogSeleccion.setTitle(context.getString(R.string.tit_buses));

        LayoutInflater li = context.getLayoutInflater();
        View vista = li.inflate(R.layout.seleccionar_linea, null, false);

        final Spinner spinner = (Spinner) vista.findViewById(R.id.spinner_linea);


        final ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<SpinnerItem>(context, android.R.layout.simple_spinner_item, listaSpinner);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        // Busqueda
        final TextView textoBuscar = (TextView) vista.findViewById(R.id.texto_buscar);

        textoBuscar.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.getFilter().filter(s);

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        // Por defecto 5
        // spinner.setSelection(1);

        dialogSeleccion.setView(vista);

        dialogSeleccion.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                int seleccion = spinner.getSelectedItemPosition();

                Log.d("SELECTOR", "seleccion: " + seleccion);

                if (seleccion >= 0) {

                    Log.d("SELECTOR", "seleccion id: " + ((SpinnerItem) spinner.getSelectedItem()).getId());

                    int seleccionId = ((SpinnerItem) spinner.getSelectedItem()).getId();

                    lineaSeleccionada(seleccionId);

                }

                dialog.dismiss();

            }

        });

        dialogSeleccion.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }

        });

        dialogSeleccion.show();

    }

    public void cargarDatosLineasModal() {

        loadBuses();

    }

    /**
     * Carga las lineas de bus
     */
    private void loadBuses() {

        context.dialog = ProgressDialog.show(context, "", context.getString(R.string.dialogo_espera), true);

        // Limpiar
        if (listaSpinner != null && !listaSpinner.isEmpty()) {
            listaSpinner.clear();
        }

        // Carga local de lineas
        String datosOffline = null;
        if (context.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {

            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.lineasoffline);

            datosOffline = Utilidades.obtenerStringDeStream(inputStream);

        } else if (context.modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {

            Resources resources = context.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.lineasoffline_tram);

            datosOffline = Utilidades.obtenerStringDeStream(inputStream);

        }

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            context.taskBuses = new LoadDatosLineasAsyncTask(loadBusesAsyncTaskResponder).execute(datosOffline);
        } else {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
            if (context.dialog != null && context.dialog.isShowing()) {
                context.dialog.dismiss();
            }
        }

    }

    LoadDatosLineasAsyncTaskResponder loadBusesAsyncTaskResponder = new LoadDatosLineasAsyncTaskResponder() {
        public void busesLoaded(ArrayList<BusLinea> buses) {
            if (buses != null) {
                context.lineasBus = buses;

                for (int i = 0; i < buses.size(); i++) {

                    listaSpinner.add(new SpinnerItem(i, buses.get(i).getLinea()));

                }

                context.dialog.dismiss();

                mostrarModalSelectorLinea();

            } else {

                context.dialog.dismiss();

                Toast.makeText(context.getApplicationContext(), context.getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT).show();

            }

        }
    };

    /**
     * Control de linea seleccionada
     *
     * @param posicion
     */
    private void lineaSeleccionada(int posicion) {

        context.detenerTareas();

        // Limpiar lista anterior para nuevas busquedas
        if (context.mMap != null) {
            context.mMap.clear();
        }

        context.lineaSeleccionada = context.lineasBus.get(posicion).getIdlinea();
        context.lineaSeleccionadaDesc = context.lineasBus.get(posicion).getLinea();

        context.lineaSeleccionadaNum = context.lineasBus.get(posicion).getNumLinea();

        Log.d("SELECTOR", "linea: " + context.lineaSeleccionadaNum);

        context.dialog = ProgressDialog.show(context, "", context.getString(R.string.dialogo_espera), true);

        if (context.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE) {

            context.gestionarLineas.loadDatosMapaV3();

        } else if (context.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {
            context.mapasOffline.loadDatosMapaOffline();

            context.gestionVehiculos.loadDatosVehiculos();

        } else if (context.modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
            context.mapasOffline.loadDatosMapaTRAMOffline();

            context.gestionVehiculos.loadDatosVehiculos();

        }

    }

}
