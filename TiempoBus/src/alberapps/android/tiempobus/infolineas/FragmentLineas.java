/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
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
package alberapps.android.tiempobus.infolineas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContentResolverCompat;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;
import alberapps.android.tiempobus.tasks.LoadDatosInfoLineasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosLineasAsyncTask.LoadDatosLineasAsyncTaskResponder;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.PlaceMark;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.util.Utilidades;

/**
 * Fragmento de lineas
 */
public class FragmentLineas extends Fragment {

    BusLinea linea = null;

    InfoLineasTabsPager actividad;

    int mCurCheckPosition = 0;

    SharedPreferences preferencias;

    /**
     * On Create
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actividad = (InfoLineasTabsPager) getActivity();

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {


        if (actividad.lineasBus == null && savedInstanceState != null && savedInstanceState.getSerializable("LINEAS_INSTANCE") != null) {
            List lineasBusAux = (ArrayList) savedInstanceState.getSerializable("LINEAS_INSTANCE");

            actividad.lineasBus = new ArrayList<>();

            for (int i = 0; i < lineasBusAux.size(); i++) {
                actividad.lineasBus.add((BusLinea) lineasBusAux.get(i));
            }

        }

        if (actividad.dialog != null) {
            actividad.dialog.dismiss();
        }

        setupFondoAplicacion();

        // Consultar si es necesario, si ya lo tiene carga la lista
        if (actividad.lineasBus != null && actividad.lineasView != null) {
            recargarListado();

        } else if (actividad.lineasBus != null) {

            cargarListado();

        } else {

            ListView lineasVi = (ListView) getActivity().findViewById(R.id.infolinea_lista_lineas);
            TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_lineas_empty);
            lineasVi.setEmptyView(vacio);

            cargarLineas(false);

            cargarHeaderLineas();
        }


        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("LINEAS_INSTANCE", actividad.lineasBus);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.infolinea_lineas, container, false);
    }

    private void cargarLineas(boolean usarOffline) {

        if (actividad.dialog == null) {
            actividad.dialog = ProgressDialog.show(actividad, "", getString(R.string.dialogo_espera), true);
        } else {
            actividad.dialog.show();
        }

        // Carga local de lineas
        String datosOffline = null;
        if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE || usarOffline) {

            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(R.raw.lineasoffline);

            datosOffline = Utilidades.obtenerStringDeStream(inputStream);

        } else if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {

            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(R.raw.lineasoffline_tram);

            datosOffline = Utilidades.obtenerStringDeStream(inputStream);

        }

        ConnectivityManager connMgr = (ConnectivityManager) actividad.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            actividad.taskBuses = new LoadDatosLineasAsyncTask(loadBusesAsyncTaskResponder).execute(datosOffline);
        } else {
            Toast.makeText(actividad.getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
            actividad.dialog.dismiss();
        }

    }

    /**
     * Sera llamado cuando la tarea de cargar buses termine
     */
    LoadDatosLineasAsyncTaskResponder loadBusesAsyncTaskResponder = new LoadDatosLineasAsyncTaskResponder() {
        public void busesLoaded(ArrayList<BusLinea> buses) {
            if (buses != null) {
                actividad.lineasBus = buses;

                cargarListado();


            } else {

                Toast toast = Toast.makeText(actividad, getResources().getText(R.string.error_tiempos), Toast.LENGTH_SHORT);
                toast.show();

                //En caso de error en la web, cargar el listado offline
                cargarLineas(true);

            }

            actividad.dialog.dismiss();

        }
    };

    /**
     * Cargar el listado de lineas
     */
    private void cargarListado() {

        if (actividad.lineasBus != null) {

            actividad.infoLineaAdapter = new InfoLineaAdapter(getActivity(), R.layout.infolineas_item);

            actividad.infoLineaAdapter.addAll(actividad.lineasBus);

            // Controlar pulsacion
            actividad.lineasView = (ListView) getActivity().findViewById(R.id.infolinea_lista_lineas);

            if (actividad.lineasView != null) {
                actividad.lineasView.setOnItemClickListener(lineasClickedHandler);

                actividad.lineasView.setOnScrollListener(new ScrollListenerAux());


                TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_lineas_empty);
                actividad.lineasView.setEmptyView(vacio);

                cargarHeaderLineas();

                actividad.lineasView.setAdapter(actividad.infoLineaAdapter);

            }

        }


    }

    /**
     * Cargar cabecera listado
     */
    public void cargarHeaderLineas() {

        if (actividad.lineasView != null && actividad.lineasView.getHeaderViewsCount() == 0) {

            LayoutInflater li2 = LayoutInflater.from(actividad);

            View vheader = li2.inflate(R.layout.infolinea_lineas_header, null);

            TextView texto = (TextView) vheader.findViewById(R.id.txt_noticias_header);

            /*
            TextView pdfRecorrido = (TextView) vheader.findViewById(R.id.lineas_informacion);
            pdfRecorrido.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    Intent i = new Intent(getActivity(), ImageGridActivity.class);
                    getActivity().startActivity(i);

                }
            });
            */


            /////

            //Seleccion de grupos de lineas bus
            final Spinner spinnerGrupos = (Spinner) vheader.findViewById(R.id.spinner_grupo_lineas);


            ArrayAdapter<CharSequence> adaptergrupos = ArrayAdapter.createFromResource(getActivity(), R.array.grupos_lineas_bus, R.layout.spinner_item_horario);
            adaptergrupos.setDropDownViewResource(R.layout.spinner_item_horario_lista);
            spinnerGrupos.setAdapter(adaptergrupos);


            if (actividad.filtroGrupo != null) {
                spinnerGrupos.setSelection(Integer.parseInt(actividad.filtroGrupo));
            }

            if (actividad.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {

                texto.setText(R.string.aviso_offline);

                spinnerGrupos.setEnabled(true);

            } else if (actividad.modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {

                texto.setText(R.string.aviso_buscador_offline_tram);

                spinnerGrupos.setEnabled(false);

            } else {

                texto.setText(R.string.aviso_buscador_online);

                spinnerGrupos.setEnabled(true);

            }

            texto.setLinksClickable(true);
            texto.setAutoLinkMask(Linkify.WEB_URLS);

            if (spinnerGrupos.isEnabled()) {

                int seleccionIncial = preferencias.getInt("infolinea_bus_filtro1", 0);

                spinnerGrupos.setSelection(seleccionIncial);

            }


            // Combo de seleccion de datos
            final Spinner spinner = (Spinner) vheader.findViewById(R.id.spinner_datos_tarjeta);


            ArrayAdapter<CharSequence> adapter = null;

            if (UtilidadesTRAM.ACTIVADO_TRAM) {
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_datos, R.layout.spinner_item_horario);
            } else {
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_datos_b, R.layout.spinner_item_horario);
            }

            adapter.setDropDownViewResource(R.layout.spinner_item_horario_lista);

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
                        editor.apply();

                        // cambiar el modo de la actividad
                        if (arg2 == 0) {

                            //Intent intent2 = getActivity().getIntent();
                            Intent intent2 = new Intent();
                            intent2.putExtra("MODO_RED_INFO", InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE);

                            getActivity().setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent2);
                            getActivity().finish();
                            //startActivity(intent2);
                            //startActivityForResult(intent2, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                        } else if (arg2 == 1) {

                            //Intent intent2 = getActivity().getIntent();
                            Intent intent2 = new Intent();
                            intent2.putExtra("MODO_RED_INFO", InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE);

                            getActivity().setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent2);
                            getActivity().finish();
                            //startActivity(intent2);
                            //startActivityForResult(intent2, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                        } else if (arg2 == 2) {

                            //Intent intent2 = getActivity().getIntent();
                            Intent intent2 = new Intent();
                            intent2.putExtra("MODO_RED_INFO", InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE);

                            getActivity().setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent2);
                            getActivity().finish();
                            //startActivity(intent2);
                            //startActivityForResult(intent2, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                        }

                    }

                }


                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }

            });

            // Seleccion de grupo
            spinnerGrupos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                    if (actividad.infoLineaAdapter != null) {
                        actividad.filtroGrupo = Integer.toString(arg2);
                        actividad.infoLineaAdapter.filtrarPorGrupo(Integer.toString(arg2));
                    }

                    if (actividad.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE
                            || actividad.modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE) {
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putInt("infolinea_bus_filtro1", arg2);
                        editor.apply();
                    }

                }


                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }

            });


            // Filtrar resultados
            final TextView textoBuscar = (TextView) vheader.findViewById(R.id.texto_buscar);

            if (actividad.infoLineaAdapter.getFiltro() != null && !actividad.infoLineaAdapter.getFiltro().equals("")) {
                textoBuscar.setText(actividad.infoLineaAdapter.getFiltro());
            }

            textoBuscar.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (actividad.infoLineaAdapter != null && actividad.infoLineaAdapter.getFilter() != null && actividad.lineasBus != null && !actividad.lineasBus.isEmpty() && !actividad.lineasBus.get(0).isErrorServicio()) {

                        actividad.infoLineaAdapter.getFilter().filter(s);

                        actividad.infoLineaAdapter.setFiltro(s);


                    }

                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void afterTextChanged(Editable s) {


                }
            });


            actividad.lineasView = (ListView) actividad.findViewById(R.id.infolinea_lista_lineas);

            actividad.lineasView.addHeaderView(vheader);


        }

    }

    /**
     * Recarga al restaurar la vista
     */
    private void recargarListado() {

        if (actividad.infoLineaAdapter != null) {

            // Controlar pulsacion
            actividad.lineasView = (ListView) getActivity().findViewById(R.id.infolinea_lista_lineas);

            if (actividad.lineasView != null) {

                actividad.lineasView.setOnItemClickListener(lineasClickedHandler);

                TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_lineas_empty);
                actividad.lineasView.setEmptyView(vacio);

                cargarHeaderLineas();

                actividad.lineasView.setAdapter(actividad.infoLineaAdapter);

            }

        }

    }

    /**
     * Listener encargado de gestionar las pulsaciones sobre los items
     */
    private OnItemClickListener lineasClickedHandler = new OnItemClickListener() {

        /**
         * @param l
         *            The ListView where the click happened
         * @param v
         *            The view that was clicked within the ListView
         * @param position
         *            The position of the view in the list
         * @param id
         *            The row id of the item that was clicked
         */
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {

            linea = actividad.infoLineaAdapter.getListaFiltrada().get(position - 1);

            if(linea != null && !linea.isErrorServicio() && !linea.isFiltroSinDatos()) {
                actividad.setLinea(linea);

                // Quitar de horarios
                actividad.gestionHorariosIda.limpiarHorariosIda();

                cargarParadas(position - 1);
            }

        }
    };

    /**
     * Cargar paradas de la linea seleccionada
     *
     * @param index
     */
    void cargarParadas(int index) {
        mCurCheckPosition = index;

        // We can display everything in-place with fragments, so update
        // the list to highlight the selected item and show the data.
        actividad.lineasView = (ListView) getActivity().findViewById(R.id.infolinea_lista_lineas);

        actividad.lineasView.setItemChecked(index, true);

        actividad.lineasMapas = null;
        actividad.sentidoIda = null;
        actividad.sentidoVuelta = null;

        if (actividad.dialog == null) {
            actividad.dialog = ProgressDialog.show(actividad, "", getString(R.string.dialogo_espera), true);
        } else {
            actividad.dialog.show();
        }

        // Control para el nuevo modo offline
        if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE) {

            loadDatosLineaServicio();
        } else if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {

            loadDatosLineaOffline();
        } else if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {

            loadDatosMapaTRAMOffline();
        }

    }

    /**
     * Carga las paradas de la linea
     */
    private void loadDatosLineaServicio() {

        DatosInfoLinea datos = new DatosInfoLinea();
        datos.setLinea(actividad.getLinea().getNumLinea());
        datos.setSublinea("1");
        datos.setContext(actividad);

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) actividad.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            actividad.taskDatosLinea = new LoadDatosInfoLineasAsyncTask(loadDatosInfoLineasAsyncTaskResponder).execute(datos);
        } else {
            Toast.makeText(actividad.getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
            if (actividad.dialog != null && actividad.dialog.isShowing()) {
                actividad.dialog.dismiss();
            }
        }

    }


    /**
     * Se llama cuando las paradas hayan sido cargadas
     */
    LoadDatosInfoLineasAsyncTask.LoadDatosInfoLineasAsyncTaskResponder loadDatosInfoLineasAsyncTaskResponder = new LoadDatosInfoLineasAsyncTask.LoadDatosInfoLineasAsyncTaskResponder() {
        public void datosInfoLineasLoaded(DatosInfoLinea datos) {

            try {

                if (datos != null && datos.getResultIda() != null && datos.getResultVuelta() != null) {

                    actividad.datosVuelta = datos.getResultVuelta();
                    actividad.datosIda = datos.getResultIda();


                    actividad.gestionIda.cargarHeaderIda(true, false, false);

                    actividad.gestionIda.cargarListadoIda();

                    actividad.cambiarTab();

                    /*if (actividad.datosIda == null || actividad.datosVuelta == null || actividad.datosIda.equals(actividad.datosVuelta)) {

                        Toast.makeText(actividad, actividad.getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

                    }*/

                } else {
                    Toast.makeText(actividad, actividad.getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT).show();
                }

                if (actividad.dialog != null && actividad.dialog.isShowing()) {
                    actividad.dialog.dismiss();
                }


            } catch (Exception e) {

                e.printStackTrace();

                Toast.makeText(actividad, actividad.getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT).show();

                if (actividad.dialog != null && actividad.dialog.isShowing()) {
                    actividad.dialog.dismiss();
                }


            }
        }
    };


    /**
     * Carga las paradas de la base de datos OFFLINE
     */
    private void loadDatosLineaOffline() {

        List<DatosInfoLinea> datosRecorridos = cargarDatosMapaBD(actividad.getLinea().getNumLinea());

        if (datosRecorridos != null) {

            actividad.datosVuelta = datosRecorridos.get(1).getResult();

            actividad.datosIda = datosRecorridos.get(0).getResult();

            actividad.gestionIda.cargarHeaderIdaOfflineBus();

            actividad.gestionIda.cargarListadoIda();

            actividad.cambiarTab();

            /*if (actividad.datosIda == null || actividad.datosVuelta == null || actividad.datosIda.equals(actividad.datosVuelta)) {

                Toast.makeText(actividad, actividad.getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

            }*/

        } else {

            Toast toast = Toast.makeText(getActivity(), getResources().getText(R.string.error_datos_offline), Toast.LENGTH_SHORT);
            toast.show();

        }

        actividad.dialog.dismiss();

    }

    /**
     * Carga las paradas de TRAM desde la base de datos OFFLINE
     */
    private void loadDatosMapaTRAMOffline() {

        List<DatosInfoLinea> datosRecorridos = cargarDatosMapaTRAMBD(actividad.getLinea().getNumLinea());

        if (datosRecorridos != null) {

            actividad.datosIda = datosRecorridos.get(0).getResult();

            actividad.datosIda.setPlacemarks(UtilidadesTRAM.posicionesRecorrido(actividad.getLinea().getNumLinea(), datosRecorridos.get(0).getResult().getPlacemarks()));

            actividad.datosIda.ordenarPlacemark();

            actividad.datosVuelta = new DatosMapa();
            actividad.datosVuelta.setPlacemarks(actividad.datosIda.getPlacemarksInversa());

            actividad.gestionIda.cargarHeaderIdaOfflineTram();

            actividad.gestionIda.cargarListadoIda();

            actividad.cambiarTab();

            /*if (actividad.datosIda == null || actividad.datosVuelta == null || actividad.datosIda.equals(actividad.datosVuelta)) {

                Toast.makeText(actividad, actividad.getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

            }*/

        } else {

            Toast toast = Toast.makeText(getActivity(), getResources().getText(R.string.error_datos_offline), Toast.LENGTH_SHORT);
            toast.show();

        }

        actividad.dialog.dismiss();

    }


    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = getActivity().findViewById(R.id.contenedor_infolinea_lineas);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());

    }

    /**
     * Cargar datos en modo offline
     */
    private List<DatosInfoLinea> cargarDatosMapaBD(String lineaSeleccionadaNum) {

        List<DatosInfoLinea> datosInfoLinea = null;

        DatosMapa datosIda = new DatosMapa();
        DatosMapa datosVuelta = new DatosMapa();

        String parametros[] = {lineaSeleccionadaNum};

        Cursor cursorParadas = null;

        try {
            //cursorParadas = getActivity().managedQuery(BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null);
            cursorParadas = ContentResolverCompat.query(getActivity().getContentResolver(), BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null, null);
        } catch (Exception e) {

            cursorParadas = null;

            e.printStackTrace();

        }

        if (cursorParadas != null) {
            List<Parada> listaParadasIda = new ArrayList<>();

            List<Parada> listaParadasVuelta = new ArrayList<>();

            String destinoIda = "";
            String destinoVuelta = "";

            for (cursorParadas.moveToFirst(); !cursorParadas.isAfterLast(); cursorParadas.moveToNext()) {

                Parada par = new Parada();

                par.setLineaNum(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
                par.setLineaDesc(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
                par.setConexion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
                par.setCoordenadas(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
                par.setDestino(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
                par.setDireccion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
                par.setLatitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
                par.setLongitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
                par.setParada(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));
                par.setObservaciones(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_OBSERVACIONES)));

                if (destinoIda.equals("")) {
                    destinoIda = par.getDestino();
                } else if (destinoVuelta.equals("") && !destinoIda.equals(par.getDestino())) {
                    destinoVuelta = par.getDestino();
                }

                if (par.getDestino().equals(destinoIda)) {

                    listaParadasIda.add(par);

                } else if (par.getDestino().equals(destinoVuelta)) {

                    listaParadasVuelta.add(par);

                }

            }

            if (listaParadasIda != null && !listaParadasIda.isEmpty() && listaParadasVuelta != null && !listaParadasVuelta.isEmpty()) {
                datosIda = mapearDatosModelo(listaParadasIda);

                datosVuelta = mapearDatosModelo(listaParadasVuelta);

                // Recorrido

                //Cursor cursorRecorrido = getActivity().managedQuery(BuscadorLineasProvider.PARADAS_LINEA_RECORRIDO_URI, null, null, parametros, null);
                Cursor cursorRecorrido = ContentResolverCompat.query(getActivity().getContentResolver(), BuscadorLineasProvider.PARADAS_LINEA_RECORRIDO_URI, null, null, parametros, null, null);
                if (cursorRecorrido != null) {
                    cursorRecorrido.moveToFirst();

                    datosIda.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));

                    cursorRecorrido.close();

                }

                // Datos a la estructura esperada
                datosInfoLinea = new ArrayList<>();
                DatosInfoLinea datoIda = new DatosInfoLinea();
                datoIda.setResult(datosIda);
                DatosInfoLinea datoVuelta = new DatosInfoLinea();
                datoVuelta.setResult(datosVuelta);
                datosInfoLinea.add(datoIda);
                datosInfoLinea.add(datoVuelta);

            } else {
                Toast toast = Toast.makeText(getActivity(), getString(R.string.error_datos_offline), Toast.LENGTH_SHORT);
                toast.show();
            }

            if (cursorParadas != null) {
                cursorParadas.close();
            }

        } else {
            Toast toast = Toast.makeText(getActivity(), getString(R.string.error_datos_offline), Toast.LENGTH_SHORT);
            toast.show();
        }

        return datosInfoLinea;

    }

    /**
     * Cargar datos en modo offline TRAM
     */
    private List<DatosInfoLinea> cargarDatosMapaTRAMBD(String lineaSeleccionadaNum) {

        List<DatosInfoLinea> datosInfoLinea = null;

        DatosMapa datosIda = new DatosMapa();

        String parametros[] = {lineaSeleccionadaNum};

        Cursor cursorParadas = null;

        try {
            //cursorParadas = getActivity().managedQuery(BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null);
            cursorParadas = ContentResolverCompat.query(getActivity().getContentResolver(), BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null, null);
        } catch (Exception e) {

            cursorParadas = null;

            e.printStackTrace();

        }

        if (cursorParadas != null) {
            List<Parada> listaParadasIda = new ArrayList<>();

            for (cursorParadas.moveToFirst(); !cursorParadas.isAfterLast(); cursorParadas.moveToNext()) {

                Parada par = new Parada();

                par.setLineaNum(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
                par.setLineaDesc(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
                par.setConexion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
                par.setCoordenadas(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
                par.setDestino(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
                par.setDireccion(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
                par.setLatitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
                par.setLongitud(cursorParadas.getInt(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
                par.setParada(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));
                par.setObservaciones(cursorParadas.getString(cursorParadas.getColumnIndex(DatosLineasDB.COLUMN_OBSERVACIONES)));

                listaParadasIda.add(par);

            }

            datosIda = mapearDatosModelo(listaParadasIda);

            // Datos a la estructura esperada
            datosInfoLinea = new ArrayList<>();
            DatosInfoLinea datoIda = new DatosInfoLinea();
            datoIda.setResult(datosIda);

            DatosInfoLinea datoVuelta = new DatosInfoLinea();

            datosInfoLinea.add(datoIda);
            datosInfoLinea.add(datoVuelta);

            cursorParadas.close();

        } else {
            Toast toast = Toast.makeText(getActivity(), getString(R.string.error_datos_offline), Toast.LENGTH_SHORT);
            toast.show();
        }

        return datosInfoLinea;

    }

    /**
     * Cargar datos en modo online
     *
     * @param listaParadas
     * @return
     */
    private DatosMapa mapearDatosModelo(List<Parada> listaParadas) {

        DatosMapa datos = new DatosMapa();

        datos.setPlacemarks(new ArrayList<PlaceMark>());

        for (int i = 0; i < listaParadas.size(); i++) {

            PlaceMark placeMark = new PlaceMark();

            placeMark.setAddress(listaParadas.get(i).getDireccion());
            placeMark.setCodigoParada(listaParadas.get(i).getParada());
            placeMark.setCoordinates(listaParadas.get(i).getCoordenadas());
            placeMark.setDescription(listaParadas.get(i).getLineaDesc());
            placeMark.setLineas(listaParadas.get(i).getConexion());
            placeMark.setObservaciones(listaParadas.get(i).getObservaciones());
            placeMark.setSentido(listaParadas.get(i).getDestino());
            placeMark.setTitle(listaParadas.get(i).getDireccion());

            datos.getPlacemarks().add(placeMark);
        }

        datos.setCurrentPlacemark(datos.getPlacemarks().get(0));

        return datos;
    }

    public class ScrollListenerAux implements AbsListView.OnScrollListener {


        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

            if(SCROLL_STATE_TOUCH_SCROLL == i) {
                View cFocus = actividad.getCurrentFocus();
                if(cFocus != null) {
                    cFocus.clearFocus();
                }
            }

        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

        }
    }

}
