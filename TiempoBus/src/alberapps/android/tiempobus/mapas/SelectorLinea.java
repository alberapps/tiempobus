/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2013 Alberto Montiel
 * <p/>
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
package alberapps.android.tiempobus.mapas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask.LoadDatosLineasAsyncTaskResponder;
import alberapps.java.tam.BusLinea;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.util.Utilidades;

/**
 * Modal seleccion de linea
 */
public class SelectorLinea {

    private MapasActivity context;

    private SharedPreferences preferencias;

    private LinkedList<SpinnerItem> listaSpinner = new LinkedList<>();

    List<BusLinea> listaSinFiltroGrupo;
    List<BusLinea> listaConFiltroGrupo = new ArrayList<>();

    ArrayAdapter<SpinnerItem> adapter = null;

    public SelectorLinea(MapasActivity contexto, SharedPreferences preferencia) {

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


        //Spinner modo
        seleccionModo(vista);


        final Spinner spinner = (Spinner) vista.findViewById(R.id.spinner_linea);


        adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listaSpinner);

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

    public void seleccionModo(View vista) {


        //Seleccion de grupos de lineas bus
        final Spinner spinnerGrupos = (Spinner) vista.findViewById(R.id.spinner_grupo_lineas);

        ArrayAdapter<CharSequence> adaptergrupos = ArrayAdapter.createFromResource(context, R.array.grupos_lineas_bus, android.R.layout.simple_spinner_item);

        spinnerGrupos.setAdapter(adaptergrupos);


        /*if (actividad.filtroGrupo != null) {
            spinnerGrupos.setSelection(Integer.parseInt(actividad.filtroGrupo));
        }*/


        if (context.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {
            spinnerGrupos.setEnabled(true);
        } else if (context.modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
            spinnerGrupos.setEnabled(false);
        } else {
            spinnerGrupos.setEnabled(true);
        }

        if (spinnerGrupos.isEnabled()) {
            int seleccionIncial = preferencias.getInt("infolinea_bus_filtro1", 0);
            spinnerGrupos.setSelection(seleccionIncial);
        }

        // Seleccion de grupo
        spinnerGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                /*if (context.infoLineaAdapter != null) {
                    context.filtroGrupo = Integer.toString(arg2);
                    context.infoLineaAdapter.filtrarPorGrupo(Integer.toString(arg2));
                }*/

                filtrarPorGrupo(Integer.toString(arg2));

                if (context.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE
                        || context.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE) {
                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putInt("infolinea_bus_filtro1", arg2);
                    editor.commit();
                }

            }


            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });


        // Combo de seleccion de datos
        final Spinner spinner = (Spinner) vista.findViewById(R.id.spinner_datos);

        ArrayAdapter<CharSequence> adapter = null;

        if (UtilidadesTRAM.ACTIVADO_TRAM) {
            adapter = ArrayAdapter.createFromResource(context, R.array.spinner_datos, android.R.layout.simple_spinner_item);
        } else {
            adapter = ArrayAdapter.createFromResource(context, R.array.spinner_datos_b, android.R.layout.simple_spinner_item);
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        // Seleccion inicial
        int infolineaModo = preferencias.getInt("infolinea_modo", 0);
        spinner.setSelection(infolineaModo);

        // Seleccion
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                // Solo en caso de haber cambiado
                if (preferencias.getInt("infolinea_modo", 0) != arg2) {

                    // Guarda la nueva seleciccion
                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putInt("infolinea_modo", arg2);
                    editor.commit();

                    // cambiar el modo de la actividad
                    if (arg2 == 0) {

                        Intent intent2 = new Intent();
                        intent2.putExtra("MODO_RED_MAPA", InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE);

                        context.setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent2);
                        context.finish();


                    } else if (arg2 == 1) {

                        Intent intent2 = new Intent();
                        intent2.putExtra("MODO_RED_MAPA", InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE);

                        context.setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent2);
                        context.finish();


                    } else if (arg2 == 2) {


                        Intent intent2 = new Intent();
                        intent2.putExtra("MODO_RED_MAPA", InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE);

                        context.setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent2);
                        context.finish();

                    }

                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });


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

                if (context.modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
                    listaSpinner.add(new SpinnerItem(-1, context.getString(R.string.noticias_todas)));
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

        Log.d("SELECTOR", "linea: " + context.lineaSeleccionadaNum);

        context.dialog = ProgressDialog.show(context, "", context.getString(R.string.dialogo_espera), true);

        if (context.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE) {

            context.lineaSeleccionada = listaConFiltroGrupo.get(posicion).getIdlinea();
            context.lineaSeleccionadaDesc = listaConFiltroGrupo.get(posicion).getLinea();

            context.lineaSeleccionadaNum = listaConFiltroGrupo.get(posicion).getNumLinea();

            context.gestionarLineas.loadDatosMapaV3();

        } else if (context.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {

            context.lineaSeleccionada = listaConFiltroGrupo.get(posicion).getIdlinea();
            context.lineaSeleccionadaDesc = listaConFiltroGrupo.get(posicion).getLinea();

            context.lineaSeleccionadaNum = listaConFiltroGrupo.get(posicion).getNumLinea();

            context.mapasOffline.loadDatosMapaOffline();
            context.gestionVehiculos.loadDatosVehiculos();

        } else if (context.modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {


            if (posicion == -1) {

                context.lineaSeleccionada = "-1";
                context.lineaSeleccionadaDesc = "TRAM";

                context.lineaSeleccionadaNum = "-1";

                context.mapasOffline.loadDatosMapaTRAMOffline("L1");
                context.mapasOffline.loadDatosMapaTRAMOffline("L2");
                context.mapasOffline.loadDatosMapaTRAMOffline("L4");
                context.mapasOffline.loadDatosMapaTRAMOffline("L9");
                context.mapasOffline.loadDatosMapaTRAMOffline("L3");

            } else {

                context.lineaSeleccionada = listaConFiltroGrupo.get(posicion).getIdlinea();
                context.lineaSeleccionadaDesc = listaConFiltroGrupo.get(posicion).getLinea();

                context.lineaSeleccionadaNum = listaConFiltroGrupo.get(posicion).getNumLinea();

                context.mapasOffline.loadDatosMapaTRAMOffline(null);

                context.gestionVehiculos.loadDatosVehiculos();

            }


        }

    }


    public void filtrarPorGrupo(String grupo) {

        listaSinFiltroGrupo = context.lineasBus;

        if (listaSinFiltroGrupo == null || listaSinFiltroGrupo.isEmpty()) {
            return;
        }

        listaConFiltroGrupo.clear();

        if (grupo == null || grupo.equals("0")) {

            listaConFiltroGrupo.addAll(listaSinFiltroGrupo);

        } else {

            for (int i = 0; i < listaSinFiltroGrupo.size(); i++) {

                if (listaSinFiltroGrupo.get(i).getIdGrupo().equals(grupo)) {
                    listaConFiltroGrupo.add(listaSinFiltroGrupo.get(i));
                }

            }

        }

        listaSpinner.clear();

        for (int i = 0; i < listaConFiltroGrupo.size(); i++) {
            listaSpinner.add(new SpinnerItem(i, listaConFiltroGrupo.get(i).getLinea()));
        }

        Log.d("SELECTOR_LINEA", "por grupo: " + grupo + " lista: " + listaSpinner.size());

        adapter.addAll(listaSpinner);
        adapter.notifyDataSetChanged();


    }

}
