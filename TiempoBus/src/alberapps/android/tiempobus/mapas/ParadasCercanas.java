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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContentResolverCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;

/**
 * Gestion de paradas cercanas
 */
public class ParadasCercanas {

    private MapasActivity context;

    private SharedPreferences preferencias;

    public static final String DISTACIA_CERCANA = "-0.001";
    public static final String DISTACIA_MEDIA = "-0.002";
    public static final String DISTACIA_LEJOS = "-0.004";

    public ParadasCercanas(MapasActivity contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }

    /**
     * Recuperar las paradas cercanas
     *
     * @param latitud
     * @param longitud
     */
    public List<LatLng> cargarParadasCercanas(int latitud, int longitud) {

        if (context.mMap != null) {
            context.mMap.clear();

            // context.mapOverlays.add(context.mMyLocationOverlay);
        } else {
            // context.mapOverlays = context.mapView.getOverlays();
        }

        context.datosMapaCargadosIda = null;
        context.datosMapaCargadosVuelta = null;
        context.lineaSeleccionada = null;
        context.lineaSeleccionadaDesc = null;
        context.lineaSeleccionadaNum = null;

        final List<LatLng> listaPuntos = new ArrayList<>();

        // String query =
        // Integer.toString(BuscadorLineasProvider.GET_PARADAS_PROXIMAS);

        // WHERE (LATITUD> (-509837) AND LATITUD < (-469839) AND LONGITUD >
        // (38326241) AND LONGITUD < (38366239))
        // LATITUD> (-709838) AND LATITUD < (-269838) AND LONGITUD > (38126242)
        // AND LONGITUD < (38566242)
        // lat 38342115 ----- long -494467
        // LATITUD> (38126242) AND LATITUD < (38566242) AND LONGITUD > (-709838)
        // AND LONGITUD < (-269838)

        // latitud, longitud
        // String parametros[] = {"38.346242", "-0.489838","-0.001"};
        // //LONG: -0,489838 LATI:38,346242

        String parametros[] = {Integer.toString(latitud), Integer.toString(longitud), context.distancia};

        String selection = Integer.toString(BuscadorLineasProvider.GET_PARADAS_PROXIMAS);

        Cursor cursor = null;

        try {
            //cursor = context.managedQuery(BuscadorLineasProvider.PARADAS_PROXIMAS_URI, null, selection, parametros, null);
            cursor = ContentResolverCompat.query(context.getContentResolver(), BuscadorLineasProvider.PARADAS_PROXIMAS_URI, null, selection, parametros, null, null);

        } catch (Exception e) {

            cursor = null;

            e.printStackTrace();
        }

        if (cursor != null) {
            List<Parada> listaParadas = new ArrayList<>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Parada par = new Parada();

                par.setConexion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
                par.setCoordenadas(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
                par.setDireccion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
                par.setLatitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
                par.setLongitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
                par.setParada(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_PARADA)).trim());

                par.setRed(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_RED_LINEAS)));

                if (!listaParadas.contains(par)) {
                    listaParadas.add(par);
                }
            }

            for (int i = 0; i < listaParadas.size(); i++) {

                if (listaParadas.get(i).getRed().equals(DatosLineasDB.RED_TRAM)) {
                    context.drawableIda = GestionarLineas.markerTram();
                } else {
                    context.drawableIda = GestionarLineas.markerBusAzul();
                }

                context.markersIda = new ArrayList<>();

                LatLng point = null;

                point = new LatLng((listaParadas.get(i).getLatitud() / 1E6), (listaParadas.get(i).getLongitud()) / 1E6);

                String descripcionAlert = context.getString(R.string.lineas) + " ";

                if (listaParadas.get(i).getConexion() != null) {
                    descripcionAlert += listaParadas.get(i).getConexion().trim();
                }

                context.markersIda.add(new MarkerOptions().position(point).title("[" + listaParadas.get(i).getParada().trim() + "] " + listaParadas.get(i).getDireccion().trim()).snippet(descripcionAlert)
                        .icon(context.drawableIda));

                context.gestionarLineas.cargarMarkers(context.markersIda, null);

                listaPuntos.add(point);

            }

            cursor.close();

        } else {

            Toast.makeText(context.getApplicationContext(), context.getString(R.string.gps_no_paradas), Toast.LENGTH_SHORT).show();

        }

        return listaPuntos;

    }

    /**
     * Seleccion de proximidad de paradas
     */
    public void seleccionarProximidad() {

        final CharSequence[] items = {context.getString(R.string.proximidad_1), context.getString(R.string.proximidad_2), context.getString(R.string.proximidad_3)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.proximidad);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {

                    context.distancia = DISTACIA_CERCANA;
                    miLocalizacion(true);

                } else if (item == 1) {

                    context.distancia = DISTACIA_MEDIA;
                    miLocalizacion(true);

                } else if (item == 2) {

                    context.distancia = DISTACIA_LEJOS;
                    miLocalizacion(true);

                }

            }
        });

        AlertDialog alert = builder.create();

        alert.show();

    }

    /**
     * Control de posicion
     *
     * @param cercanas
     */
    public void miLocalizacion(final boolean cercanas) {

        try {

            if (cercanas) {
                // setTitle(getString(R.string.cercanas));
                context.datosLinea.setText(context.getString(R.string.cercanas));

            }

            if (context.conectadoLocation) {
                // String msg = "Location = " +
                // context.mLocationClient.getLastLocation();
                // Toast.makeText(context.getApplicationContext(), msg,
                // Toast.LENGTH_SHORT).show();
            } else {

                /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.gps_on)).setCancelable(false).setPositiveButton(context.getString(R.string.barcode_si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //context.enableLocationSettings();

                    }
                }).setNegativeButton(context.getString(R.string.barcode_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
                AlertDialog alert = builder.create();

                alert.show();*/

                return;

            }

            // if (context.primeraCarga) {

            Toast.makeText(context, context.getString(R.string.gps_recuperando), Toast.LENGTH_SHORT).show();

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // Request missing location permission.
                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MapasActivity.REQUEST_CODE_LOCATION);

                return;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(context.mGoogleApiClient);

            if (location == null) {
                Toast.makeText(context, context.getString(R.string.error_gps), Toast.LENGTH_SHORT).show();
                return;
            }

            double latitud = location.getLatitude();
            double longitud = location.getLongitude();

            LatLng lt = new LatLng(latitud, longitud);

            context.mMap.moveCamera(CameraUpdateFactory.newLatLng(lt));

            context.mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

            if (cercanas) {

                int glat = (int) (latitud * 1E6);
                int glng = (int) (longitud * 1E6);

                final List<LatLng> listaPuntos = cargarParadasCercanas(glat, glng);

                listaPuntos.add(lt);

                context.mMap.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)));

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

            }

        } catch (Exception e) {

            Toast.makeText(context, context.getString(R.string.error_gps), Toast.LENGTH_SHORT).show();

            e.printStackTrace();

        }

    }

}
