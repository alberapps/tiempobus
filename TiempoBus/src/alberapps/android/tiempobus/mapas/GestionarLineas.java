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

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.tasks.LoadDatosRecorridosAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDatosRecorridosAsyncTask.LoadDatosRecorridosAsyncTaskResponder;
import alberapps.java.tam.UtilidadesTAM;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.PlaceMark;
import alberapps.java.tram.UtilidadesTRAM;

/**
 * Gestion carga de lineas, etc en el mapa
 */
public class GestionarLineas {

    private MapasActivity context;

    private SharedPreferences preferencias;

    Integer colorTram = null;

    public GestionarLineas(MapasActivity contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }

    /**
     * kml de carga
     */
    public void loadDatosMapaV3() {

        //String url = UtilidadesTAM.getKMLParadasV3(context.lineaSeleccionada);

        //String urlRecorrido = UtilidadesTAM.getKMLRecorridoV3(context.lineaSeleccionada);

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            context.taskDatosMapaV3 = new LoadDatosRecorridosAsyncTask(loadDatosRecorridosAsyncTaskResponderIda).execute(context.lineaSeleccionadaNum, "1", context);
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
    LoadDatosRecorridosAsyncTaskResponder loadDatosRecorridosAsyncTaskResponderIda = new LoadDatosRecorridosAsyncTaskResponder() {
        public void datosRecorridosLoaded(DatosMapa[] datos) {

            if (datos != null && datos[0] != null) {
                context.datosMapaCargadosIda = datos[0];
                context.datosMapaCargadosVuelta = datos[1];

                //Si no hay datos, cargar desde offline
                if (context.datosMapaCargadosIda != null && context.datosMapaCargadosIda.getRecorrido() == null || context.datosMapaCargadosIda.getRecorrido().equals("")) {
                    context.mapasOffline.loadDatosRecorridoOffline();
                } else {
                    cargarMapa(null);
                }

                context.gestionVehiculos.loadDatosVehiculos();

                context.dialog.dismiss();

            } else {

                Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_tiempos), Toast.LENGTH_LONG).show();
                //context.finish();

                context.dialog.dismiss();

                context.selectorLinea.mostrarModalSelectorLinea();

            }

        }
    };


    public void cargarMapaTramTodas() {

    }


    public static BitmapDescriptor markerTram() {

        BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_tram1);

        return marker;

    }

    public static BitmapDescriptor markerTramPosicion() {

        BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_tram_black1);

        return marker;

    }

    public static BitmapDescriptor markerBusAzul() {

        BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_blue1);

        return marker;

    }

    public static BitmapDescriptor markerBusVerde() {

        BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_green1);

        return marker;

    }

    public static BitmapDescriptor markerBusMedio() {

        BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_green2);

        return marker;

    }

    public static BitmapDescriptor markerBusPosicion() {

        BitmapDescriptor marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_bus_black1);

        return marker;

    }


    /**
     * Cargar el mapa con las paradas de la linea
     */
    public void cargarMapa(String linea) {

        // Cargar datos cabecera
        String cabdatos = context.lineaSeleccionadaDesc;


        context.datosLinea.setText(cabdatos);

        if (context.modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
            context.drawableIda = markerTram();

            if (linea != null && linea.equals("L1")) {
                colorTram = context.getResources().getColor(R.color.tram_l1);
            } else if (linea != null && linea.equals("L2")) {
                colorTram = context.getResources().getColor(R.color.tram_l2);
            } else if (linea != null && linea.equals("L3")) {
                colorTram = context.getResources().getColor(R.color.tram_l3);
            } else if (linea != null && linea.equals("L4")) {
                colorTram = context.getResources().getColor(R.color.tram_l4);
            } else if (linea != null && linea.equals("L9")) {
                colorTram = context.getResources().getColor(R.color.tram_l9);
            }

        } else {
            context.drawableIda = markerBusAzul();
            colorTram = null;
        }

        context.drawableVuelta = markerBusVerde();
        context.drawableMedio = markerBusMedio();
        context.markersIda = new ArrayList<>();
        context.markersVuelta = new ArrayList<>();
        context.markersMedio = new ArrayList<>();

        // -0.510017579,38.386057662,0
        // 38.386057662,-0.510017579

        final List<LatLng> listaPuntos = new ArrayList<>();

        /**
         * 38.344820, -0.483320‎ +38° 20' 41.35", -0° 28' 59.95"
         * 38.34482,-0.48332
         *
         * long: -0,510018 lati: 38,386058 PRUEBAS‎
         *
         */

        // Carga de puntos del mapa

        LatLng point = null;

        // Recorrido ida
        /*if (context.datosMapaCargadosIda != null && context.datosMapaCargadosIda.getRecorrido() != null && !context.datosMapaCargadosIda.getRecorrido().equals("")) {


            // if
            // (DatosPantallaPrincipal.esLineaTram(context.lineaSeleccionadaNum))
            // {
            // colorRecorrido = "#ed7408";
            // }

            // Recorrido

            if (colorTram != null) {
                drawPath(context.datosMapaCargadosIda, colorTram);
            } else {
                drawPath(context.datosMapaCargadosIda, ContextCompat.getColor(context, R.color.bus_blue));
            }
        }*/

        MarkerOptions posicionSelecionada = null;

        // Datos IDA
        if (context.datosMapaCargadosIda != null && !context.datosMapaCargadosIda.getPlacemarks().isEmpty()) {

            // Recorrido
            if (colorTram != null) {
                drawPath(context.datosMapaCargadosIda, colorTram);
            } else {
                drawPath(context.datosMapaCargadosIda, ContextCompat.getColor(context, R.color.bus_blue));
            }

            for (int i = 0; i < context.datosMapaCargadosIda.getPlacemarks().size(); i++) {

                String[] coordenadas = context.datosMapaCargadosIda.getPlacemarks().get(i).getCoordinates().split(",");

                double lat = Double.parseDouble(coordenadas[1]); // 38.386058;
                double lng = Double.parseDouble(coordenadas[0]); // -0.510018;
                // int glat = (int) (lat * 1E6);
                // int glng = (int) (lng * 1E6);

                // 19240000,-99120000
                // 38337176
                // -491890

                // point = new GeoPoint(glat, glng);
                // GeoPoint point = new GeoPoint(19240000,-99120000);

                point = new LatLng(lat, lng);

                String descripcionAlert = context.getResources().getText(R.string.share_2) + " ";

                if (context.datosMapaCargadosIda.getPlacemarks().get(i).getSentido() != null && !context.datosMapaCargadosIda.getPlacemarks().get(i).getSentido().trim().equals("")) {
                    descripcionAlert += context.datosMapaCargadosIda.getPlacemarks().get(i).getSentido().trim();
                } else {
                    descripcionAlert += "Ida";
                }

                descripcionAlert += "\n" + context.getResources().getText(R.string.lineas) + " ";

                if (context.datosMapaCargadosIda.getPlacemarks().get(i).getLineas() != null) {
                    descripcionAlert += context.datosMapaCargadosIda.getPlacemarks().get(i).getLineas().trim();
                }

                if (context.datosMapaCargadosIda.getPlacemarks().get(i).getObservaciones() != null
                        && !context.datosMapaCargadosIda.getPlacemarks().get(i).getObservaciones().trim().equals("")) {
                    descripcionAlert += "\n" + context.getResources().getText(R.string.observaciones) + " ";
                    descripcionAlert += context.datosMapaCargadosIda.getPlacemarks().get(i).getObservaciones().trim();
                }

                context.markersIda.add(new MarkerOptions().position(point)
                        .title("[" + context.datosMapaCargadosIda.getPlacemarks().get(i).getCodigoParada().trim() + "] " + context.datosMapaCargadosIda.getPlacemarks().get(i).getTitle().trim()).snippet(descripcionAlert)
                        .icon(context.drawableIda));

                listaPuntos.add(point);

                if (context.paradaSeleccionadaEntrada != null && context.paradaSeleccionadaEntrada.equals(context.datosMapaCargadosIda.getPlacemarks().get(i).getCodigoParada().trim())) {

                    posicionSelecionada = context.markersIda.get(context.markersIda.size() - 1);

                }

            }

            if (context.markersIda != null && context.markersIda.size() > 0) {

                cargarMarkers(context.markersIda, posicionSelecionada);

            } else {
                avisoPosibleError();
            }

        }

        boolean coincide = false;

        // Recorrido vuelta
        if (context.datosMapaCargadosVuelta != null && context.datosMapaCargadosVuelta.getRecorrido() != null && !context.datosMapaCargadosVuelta.getRecorrido().equals("")) {

            // Recorrido
            drawPath(context.datosMapaCargadosVuelta, ContextCompat.getColor(context, R.color.tram_l2));

        }

        // Datos VUELTA
        if (context.datosMapaCargadosVuelta != null && !context.datosMapaCargadosVuelta.getPlacemarks().isEmpty()) {

            for (int i = 0; i < context.datosMapaCargadosVuelta.getPlacemarks().size(); i++) {

                String[] coordenadas = context.datosMapaCargadosVuelta.getPlacemarks().get(i).getCoordinates().split(",");

                double lat = Double.parseDouble(coordenadas[1]); // 38.386058;
                double lng = Double.parseDouble(coordenadas[0]); // -0.510018;
                // int glat = (int) (lat * 1E6);
                // int glng = (int) (lng * 1E6);

                // 19240000,-99120000

                // point = new GeoPoint(glat, glng);
                // GeoPoint point = new GeoPoint(19240000,-99120000);

                point = new LatLng(lat, lng);

                String direc = "";

                coincide = false;

                if (context.datosMapaCargadosIda.getPlacemarks().contains(context.datosMapaCargadosVuelta.getPlacemarks().get(i))) {

                    String ida = context.datosMapaCargadosIda.getCurrentPlacemark().getSentido().trim();

                    if (ida.equals("")) {
                        ida = "Ida";
                    }

                    direc = ida + " " + context.getResources().getText(R.string.tiempo_m_3) + " " + context.datosMapaCargadosVuelta.getPlacemarks().get(i).getSentido();

                    coincide = true;

                } else {
                    direc = context.datosMapaCargadosVuelta.getCurrentPlacemark().getSentido().trim();

                    coincide = false;
                }

                if (direc == null || (direc != null && direc.trim().equals(""))) {
                    direc = "Vuelta";
                }

                String descripcionAlert = context.getResources().getText(R.string.share_2) + " ";

                if (direc != null) {
                    descripcionAlert += direc;
                }

                descripcionAlert += "\n" + context.getResources().getText(R.string.lineas) + " ";

                if (context.datosMapaCargadosVuelta.getPlacemarks().get(i).getLineas() != null) {
                    descripcionAlert += context.datosMapaCargadosVuelta.getPlacemarks().get(i).getLineas().trim();
                }

                if (context.datosMapaCargadosVuelta.getPlacemarks().get(i).getObservaciones() != null
                        && !context.datosMapaCargadosVuelta.getPlacemarks().get(i).getObservaciones().trim().equals("")) {
                    descripcionAlert += "\n" + context.getResources().getText(R.string.observaciones) + " ";
                    descripcionAlert += context.datosMapaCargadosVuelta.getPlacemarks().get(i).getObservaciones().trim();
                }

                if (coincide) {

                    context.markersMedio.add(new MarkerOptions().position(point)
                            .title("[" + context.datosMapaCargadosVuelta.getPlacemarks().get(i).getCodigoParada().trim() + "] " + context.datosMapaCargadosVuelta.getPlacemarks().get(i).getTitle().trim())
                            .snippet(descripcionAlert).icon(context.drawableMedio));

                } else {

                    context.markersVuelta.add(new MarkerOptions().position(point)
                            .title("[" + context.datosMapaCargadosVuelta.getPlacemarks().get(i).getCodigoParada().trim() + "] " + context.datosMapaCargadosVuelta.getPlacemarks().get(i).getTitle().trim())
                            .snippet(descripcionAlert).icon(context.drawableVuelta));

                }

                listaPuntos.add(point);

                // Si hay seleccion pero no estaba en la ida
                if (posicionSelecionada == null && context.paradaSeleccionadaEntrada != null && context.paradaSeleccionadaEntrada.equals(context.datosMapaCargadosVuelta.getPlacemarks().get(i).getCodigoParada().trim())) {

                    posicionSelecionada = context.markersVuelta.get(context.markersVuelta.size() - 1);

                }

            }

            if (context.markersMedio != null && context.markersMedio.size() > 0 && context.datosMapaCargadosIda != null && !context.datosMapaCargadosIda.getPlacemarks().isEmpty()) {

                cargarMarkers(context.markersMedio, posicionSelecionada);

            }

            if (context.markersVuelta.size() > 0) {

                cargarMarkers(context.markersVuelta, posicionSelecionada);

            } else {
                //avisoPosibleError();
            }

        }

        if (listaPuntos != null && !listaPuntos.isEmpty()) {

            // Pan to see all markers in view.
            // Cannot zoom to bounds until the map has a size.
            final View mapView = context.getSupportFragmentManager().findFragmentById(R.id.map).getView();
            if (mapView.getViewTreeObserver().isAlive()) {
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    // We use the new method when supported
                    @SuppressLint("NewApi")
                    // We check which build version we are using.
                    public void onGlobalLayout() {

                        Builder ltb = new Builder();

                        for (int i = 0; i < listaPuntos.size(); i++) {
                            ltb.include(listaPuntos.get(i));
                        }

                        LatLngBounds bounds = ltb.build();

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        context.mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                    }
                });
            }

        }

        // Limpiar para modo normal
        posicionSelecionada = null;
        context.paradaSeleccionadaEntrada = null;

    }

    public void avisoPosibleError() {

        Toast.makeText(context, context.getString(R.string.mapa_posible_error), Toast.LENGTH_LONG).show();

    }

    /**
     * Inicializar el mapa
     */
    public void inicializarMapa() {

        // Control de modo de red
        context.modoRed = context.getIntent().getIntExtra("MODO_RED", 0);

        if (context.getIntent().getExtras() == null || (context.getIntent().getExtras() != null && !context.getIntent().getExtras().containsKey("MODO_RED"))) {

            context.modoRed = preferencias.getInt("infolinea_modo", 0);

        }

        context.primeraCarga = true;

        context.datosLinea = (TextView) context.findViewById(R.id.datos_linea);

        // Si viene de la seleccion de la lista
        if (context.getIntent().getExtras() != null && context.getIntent().getExtras().containsKey("LINEA_MAPA")) {

            int lineaPos = -1;

            // tram
            if (DatosPantallaPrincipal.esLineaTram(context.getIntent().getExtras().getString("LINEA_MAPA"))) {
                lineaPos = UtilidadesTRAM.getIdLinea(context.getIntent().getExtras().getString("LINEA_MAPA"));
            } else {
                lineaPos = UtilidadesTAM.getIdLinea(context.getIntent().getExtras().getString("LINEA_MAPA"));
            }

            Log.d("mapas", "linea: " + lineaPos + "l: " + context.getIntent().getExtras().getString("LINEA_MAPA"));

            if (lineaPos > -1) {

                if (DatosPantallaPrincipal.esLineaTram(context.getIntent().getExtras().getString("LINEA_MAPA"))) {

                    // context.lineaSeleccionada =

                    context.lineaSeleccionadaDesc = UtilidadesTRAM.DESC_LINEA[lineaPos];

                    context.lineaSeleccionadaNum = UtilidadesTRAM.LINEAS_NUM[lineaPos];

                } else {

                    context.lineaSeleccionadaDesc = UtilidadesTAM.LINEAS_DESCRIPCION[lineaPos];

                    context.lineaSeleccionadaNum = UtilidadesTAM.LINEAS_NUM[lineaPos];

                }

                // Control parada seleccionada al entrar
                context.paradaSeleccionadaEntrada = context.getIntent().getExtras().getString("LINEA_MAPA_PARADA");

                context.dialog = ProgressDialog.show(context, "", context.getString(R.string.dialogo_espera), true);

                if (DatosPantallaPrincipal.esLineaTram(context.lineaSeleccionadaNum)) {
                    context.modoRed = InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE;
                    context.mapasOffline.loadDatosMapaTRAMOffline(null);
                } else {
                    context.modoRed = InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE;
                    context.mapasOffline.loadDatosMapaOffline();
                }

                context.gestionVehiculos.loadDatosVehiculos();

            } else {

                Toast.makeText(context, context.getResources().getText(R.string.aviso_error_datos), Toast.LENGTH_LONG).show();

            }

        } else if (context.getIntent().getExtras() != null && context.getIntent().getExtras().containsKey("LINEA_MAPA_FICHA")) {

            String lineaPos = context.getIntent().getExtras().getString("LINEA_MAPA_FICHA");

            context.lineaSeleccionada = context.getIntent().getExtras().getString("LINEA_MAPA_FICHA_KML");
            context.lineaSeleccionadaDesc = context.getIntent().getExtras().getString("LINEA_MAPA_FICHA_DESC");

            context.lineaSeleccionadaNum = lineaPos;

            // Control parada seleccionada al entrar
            context.paradaSeleccionadaEntrada = context.getIntent().getExtras().getString("LINEA_MAPA_PARADA");

            context.dialog = ProgressDialog.show(context, "", context.getString(R.string.dialogo_espera), true);

            if (DatosPantallaPrincipal.esLineaTram(context.lineaSeleccionadaNum)) {
                context.modoRed = InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE;
                context.mapasOffline.loadDatosMapaTRAMOffline(null);
            } else {

                if (context.getIntent().getExtras().containsKey("LINEA_MAPA_FICHA_ONLINE")) {
                    context.modoRed = InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE;
                    loadDatosMapaV3();

                } else {
                    context.modoRed = InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE;
                    context.mapasOffline.loadDatosMapaOffline();
                }
            }

            context.gestionVehiculos.loadDatosVehiculos();

        } else {

            context.selectorLinea.cargarDatosLineasModal();

        }


        // Control de boton vehiculos
        /*final android.support.v7.widget.SwitchCompat botonVehiculos = (android.support.v7.widget.SwitchCompat) context.findViewById(R.id.mapasVehiculosButton);

        boolean vehiculosPref = preferencias.getBoolean("mapas_vehiculos", true);

        if (vehiculosPref) {
            botonVehiculos.setChecked(true);
        }


        botonVehiculos.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (botonVehiculos.isChecked()) {

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("mapas_vehiculos", true);
                    editor.apply();

                    context.gestionVehiculos.loadDatosVehiculos();
                } else {

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("mapas_vehiculos", false);
                    editor.apply();

                    if (context.markersVehiculos != null) {

                        if (context.timer != null) {
                            context.timer.cancel();
                        }

                        if (context.gestionVehiculos.markersVehiculos != null && !context.gestionVehiculos.markersVehiculos.isEmpty()) {
                            context.gestionarLineas.quitarMarkers(context.gestionVehiculos.markersVehiculos);
                        }

                    }
                }

            }
        });
        */


        //Botones ida y vuelta
        final androidx.appcompat.widget.SwitchCompat botonIda = (androidx.appcompat.widget.SwitchCompat) context.findViewById(R.id.mapaIdaButton);


        if (context.flagIda) {
            botonIda.setChecked(true);
        }

        botonIda.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                //if (context.modoRed != InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
                context.gestionarLineas.cargarOcultarIda();
                //}


            }
        });

        final androidx.appcompat.widget.SwitchCompat botonVuelta = (androidx.appcompat.widget.SwitchCompat) context.findViewById(R.id.mapaVueltaButton);

        if (context.modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
            botonVuelta.setEnabled(false);
            context.flagVuelta = false;
        }


        if (context.flagVuelta) {
            botonVuelta.setChecked(true);
        }

        botonVuelta.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (context.modoRed != InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
                    context.gestionarLineas.cargarOcultarVuelta();
                }


            }
        });


    }

    /**
     * Cargar marcadores sin guardar
     *
     * @param markers
     */
    public void cargarMarkers(List<MarkerOptions> markers, MarkerOptions posicionSeleccionada) {

        Log.d("mapas", "selecciondada: " + posicionSeleccionada);

        if (markers != null) {
            for (int i = 0; i < markers.size(); i++) {

                Marker marker = context.mMap.addMarker(markers.get(i));

                // Mostrar informacion de la seleccionada
                if ((posicionSeleccionada != null) && markers.get(i).equals(posicionSeleccionada)) {

                    marker.showInfoWindow();

                }

            }
        }

    }

    /**
     * Cargar marcadores y guardarlos
     *
     * @param markers
     * @return
     */
    public List<Marker> cargarMarkersCtr(List<MarkerOptions> markers) {

        List<Marker> listaMarker = new ArrayList<>();

        if (markers != null) {
            for (int i = 0; i < markers.size(); i++) {
                listaMarker.add(context.mMap.addMarker(markers.get(i)));
            }
        }

        return listaMarker;

    }

    /**
     * Eliminar marcadores
     *
     * @param markers
     */
    public void quitarMarkers(List<Marker> markers) {

        if (markers != null) {
            for (int i = 0; i < markers.size(); i++) {

                markers.get(i).remove();

            }

            markers.clear();
        }

    }

    /**
     * Recorrido
     *
     * @param navSet
     * @param color
     */
    public void drawPath(DatosMapa navSet, int color) {

        if (context.mMap == null) {
            return;
        }

        // color correction for dining, make it darker
        if (color == Color.parseColor("#add331"))
            color = Color.parseColor("#6C8715");

        String path = navSet.getRecorrido();

        if (path != null && path.trim().length() > 0) {
            String[] pairs = path.trim().split(" ");

            String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude
            // lngLat[1]=latitude
            // lngLat[2]=height

            if (lngLat.length < 3)
                lngLat = pairs[1].split(","); // if first pair is not
            // transferred completely, take
            // seconds pair //TODO

            try {

                LatLng startGP = new LatLng(Double.parseDouble(lngLat[1]), Double.parseDouble(lngLat[0]));

                LatLng gp1;
                LatLng gp2 = startGP;

                for (int i = 1; i < pairs.length; i++) // the last one would be
                // crash
                {
                    lngLat = pairs[i].split(",");

                    gp1 = gp2;

                    if (gp1 != null && gp2 != null && lngLat.length >= 2) {
                        gp2 = new LatLng(Double.parseDouble(lngLat[1]), Double.parseDouble(lngLat[0]));

                        context.mMap.addPolyline(new PolylineOptions().add(gp1, gp2).width(5).color(color));

                    }

                }

                context.mMap.addPolyline(new PolylineOptions().add(gp2, gp2).width(5).color(color));

            } catch (Exception e) {

                e.printStackTrace();

            }
        }

    }

    /**
     * Mostrar y ocultar el recorrido de ida
     */
    public void cargarOcultarIda() {

        if (context.datosMapaCargadosIda != null) {

            if (!context.lineaSeleccionadaNum.equals("-1")) {

                if (!context.datosMapaCargadosIda.getPlacemarks().isEmpty()) {
                    context.datosMapaCargadosIdaAux = new DatosMapa();
                    context.datosMapaCargadosIdaAux.setPlacemarks(context.datosMapaCargadosIda.getPlacemarks());
                    context.datosMapaCargadosIdaAux.setRecorrido(context.datosMapaCargadosIda.getRecorrido());
                    context.datosMapaCargadosIda.setPlacemarks(new ArrayList<PlaceMark>());
                    context.datosMapaCargadosIda.setRecorrido("");

                    context.flagIda = false;

                } else if (context.datosMapaCargadosIdaAux != null) {
                    context.datosMapaCargadosIda.setPlacemarks(context.datosMapaCargadosIdaAux.getPlacemarks());
                    context.datosMapaCargadosIda.setRecorrido(context.datosMapaCargadosIdaAux.getRecorrido());

                    context.flagIda = true;

                }

                // Limpiar lista anterior para nuevas busquedas
                if (context.mMap != null) {
                    context.mMap.clear();
                }


                cargarMapa(null);


            } else {

                /*if (context.flagIda) {
                    context.datosMapaCargadosIda.setPlacemarks(new ArrayList<PlaceMark>());
                    context.flagIda = false;

                } else {

                    context.flagIda = true;

                }

                // Limpiar lista anterior para nuevas busquedas
                if (context.mMap != null) {
                    context.mMap.clear();
                }

                if (context.flagIda && context.lineaSeleccionadaNum.equals("-1")) {
                    context.mapasOffline.loadDatosMapaTRAMOffline("L1");
                    context.mapasOffline.loadDatosMapaTRAMOffline("L2");
                    context.mapasOffline.loadDatosMapaTRAMOffline("L4");
                    context.mapasOffline.loadDatosMapaTRAMOffline("L9");
                    context.mapasOffline.loadDatosMapaTRAMOffline("L3");
                } else if (!context.flagIda && context.lineaSeleccionadaNum.equals("-1")) {
                    cargarMapa("L1");
                    cargarMapa("L2");
                    cargarMapa("L4");
                    cargarMapa("L9");
                    cargarMapa("L3");
                } else {
                    cargarMapa(null);
                }*/

            }

            context.gestionVehiculos.loadDatosVehiculos();
        }
    }

    /**
     * Mostrar y ocultar el recorrido de vuelta
     */
    public void cargarOcultarVuelta() {

        if (context.datosMapaCargadosVuelta != null) {

            if (!context.datosMapaCargadosVuelta.getPlacemarks().isEmpty()) {
                context.datosMapaCargadosVueltaAux = new DatosMapa();
                context.datosMapaCargadosVueltaAux.setPlacemarks(context.datosMapaCargadosVuelta.getPlacemarks());
                context.datosMapaCargadosVueltaAux.setRecorrido(context.datosMapaCargadosVuelta.getRecorrido());
                context.datosMapaCargadosVuelta.setPlacemarks(new ArrayList<PlaceMark>());
                context.datosMapaCargadosVuelta.setRecorrido("");

                context.flagVuelta = false;

            } else if (context.datosMapaCargadosVueltaAux != null) {
                context.datosMapaCargadosVuelta.setPlacemarks(context.datosMapaCargadosVueltaAux.getPlacemarks());
                context.datosMapaCargadosVuelta.setRecorrido(context.datosMapaCargadosVueltaAux.getRecorrido());

                context.flagVuelta = true;

            }

            // Limpiar lista anterior para nuevas busquedas
            if (context.mMap != null) {
                context.mMap.clear();
            }

            cargarMapa(null);

            context.gestionVehiculos.loadDatosVehiculos();

        }
    }

}
