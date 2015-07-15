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

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.tasks.LoadVehiculosMapaAsyncTask;
import alberapps.android.tiempobus.tasks.LoadVehiculosMapaAsyncTask.LoadVehiculosMapaAsyncTaskResponder;
import alberapps.java.tam.mapas.DatosMapa;

/**
 * Gestion de vehiculos
 */
public class GestionVehiculos {

    private MapasMaps2Activity context;

    private SharedPreferences preferencias;

    List<Marker> markersVehiculos;

    public GestionVehiculos(MapasMaps2Activity contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }

    /**
     * Cargar el mapa con las paradas de la linea
     */
    public void cargarVehiculosMapa() {

        if (markersVehiculos != null && !markersVehiculos.isEmpty()) {
            context.gestionarLineas.quitarMarkers(markersVehiculos);
        }

        if (((MapasMaps2Activity) context).modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
            context.drawableVehiculo = R.drawable.tramway_2;
        } else {
            context.drawableVehiculo = R.drawable.bus;
        }

        context.markersVehiculos = new ArrayList<MarkerOptions>();

        LatLng point = null;

        // Datos IDA
        if (context.datosMapaCargadosIda != null && !context.datosMapaCargadosIda.getVehiculosList().isEmpty()) {

            for (int i = 0; i < context.datosMapaCargadosIda.getVehiculosList().size(); i++) {

                double y = Double.parseDouble(context.datosMapaCargadosIda.getVehiculosList().get(i).getYcoord());
                double x = Double.parseDouble(context.datosMapaCargadosIda.getVehiculosList().get(i).getXcoord());

                String coord = UtilidadesGeo.getLatLongUTMBus(y, x);

                String[] coordenadas = coord.split(",");

                double lat = Double.parseDouble(coordenadas[1]); // 38.386058;
                double lng = Double.parseDouble(coordenadas[0]); // -0.510018;

                // Desvio en el calculo
                // lat = lat - 0.001517;
                // lng = lng - 0.001517;

				/*
                 * prueba no: 0,002185
				 * 
				 * Erronea: 38.337297,-0.492949 correcta: 38.337625,-0.492155
				 * desvio: +0,000328, +0,000794
				 * 
				 * Erronea: 38.338631,-0.490427 Correcta: 38.337114,-0.491668
				 * desvio: 0,001517, −0,001241
				 */

				/*
				 * Nueva prueba con tram
				 * 
				 * Erronea: 38.392287,-0.51708 Correcta: 38.392216,-0.517164
				 * 
				 * desvio: (0,000071) -> 0,001588
				 * 
				 * desvio2: (−0,000084)
				 * 
				 * Erronea 2: 38.393419,-0.515092
				 * 
				 * desvio: 0,001203 0,002072
				 */

                int glat = (int) (lat * 1E6);
                int glng = (int) (lng * 1E6);

                // Calibrado
                // https://github.com/Sloy/SeviBus
                // Rafa Vazquez (Sloy)
                glat = glat - 200 * 10;
                glng = glng - 130 * 10;
                //

                double latB = glat / 1E6;
                double longB = glng / 1E6;

                point = new LatLng(latB, longB);

                String descripcionAlert = "";

                context.markersVehiculos.add(new MarkerOptions().position(point).title(context.datosMapaCargadosIda.getVehiculosList().get(i).getVehiculo().trim()).snippet(descripcionAlert)
                        .icon(BitmapDescriptorFactory.fromResource(context.drawableVehiculo)));

				/*
				 * // 19240000,-99120000
				 * 
				 * //UTM a geograficas geotools jcoord
				 * 
				 * long x: 715923 lat y: 4253901 30N 715923 4253901 -> 38.40728
				 * -0.52710 geographiclib //lat:38337176 //long:-491890
				 */

            }

            if (context.markersVehiculos != null && context.markersVehiculos.size() > 0) {
                markersVehiculos = context.gestionarLineas.cargarMarkersCtr(context.markersVehiculos);
            } else {
                context.gestionarLineas.avisoPosibleError();
            }

        }

    }

    /**
     * Carga de vehiculos de la linea
     */
    public void loadDatosVehiculos() {

        Log.d("mapas", "Inicia carga de vehiculos");


        boolean vehiculosPref = preferencias.getBoolean("mapas_vehiculos", true);

        if (!vehiculosPref || context.lineaSeleccionadaNum == null || context.lineaSeleccionadaNum.equals("") || context.lineaSeleccionadaNum.equals("-1")) {
            return;
        }

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            if (context.timer != null) {
                context.timer.cancel();
            }

            final Handler handler = new Handler();
            context.timer = new Timer();
            TimerTask timerTask = new TimerTask() {

                @Override
                public void run() {
                    handler.post(new Runnable() {

                        public void run() {

                            Boolean cacheTiempos = preferencias.getBoolean("conectividad_cache_tiempos", false);

                            context.taskVehiculosMapa = new LoadVehiculosMapaAsyncTask(loadVehiculosMapaAsyncTaskResponder).execute(context.lineaSeleccionadaNum, cacheTiempos);

                        }
                    });

                }
            };

            context.timer.schedule(timerTask, 0, frecuenciaRecarga());

        } else {
            Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
            if (context.dialog != null && context.dialog.isShowing()) {
                context.dialog.dismiss();
            }
        }

    }

    /**
     * Carga de vehiculos de la linea
     */
    LoadVehiculosMapaAsyncTaskResponder loadVehiculosMapaAsyncTaskResponder = new LoadVehiculosMapaAsyncTaskResponder() {

        public void vehiculosMapaLoaded(DatosMapa datosMapa) {

            if (datosMapa != null && datosMapa.getVehiculosList() != null && context.datosMapaCargadosIda != null) {
                context.datosMapaCargadosIda.setVehiculosList(datosMapa.getVehiculosList());

                cargarVehiculosMapa();

                if (context.dialog != null && context.dialog.isShowing()) {
                    context.dialog.dismiss();
                }

                //Actualizar hora
                ToggleButton botonVehiculos = (ToggleButton) context.findViewById(R.id.mapasVehiculosButton);

                final Calendar c = Calendar.getInstance();

                SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.US);

                String updated = df.format(c.getTime()).toString();

                botonVehiculos.setText(context.getString(R.string.mapas_vehiculo_on) +  ": " + updated );


            } else {

                Toast.makeText(context.getApplicationContext(), context.getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT).show();
                // context.finish();

                if (context.dialog != null && context.dialog.isShowing()) {
                    context.dialog.dismiss();
                }

            }

        }
    };

    /**
     * Frecuencia configurable
     *
     * @return frecuencia
     */
    public long frecuenciaRecarga() {

        String preFrec = preferencias.getString("tiempo_recarga_vehiculos", "30");

        long frecuencia = Long.parseLong(preFrec) * 1000;

        return frecuencia;

    }

}
