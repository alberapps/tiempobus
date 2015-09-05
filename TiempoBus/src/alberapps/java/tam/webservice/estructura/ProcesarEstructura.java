/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2015 Alberto Montiel
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
package alberapps.java.tam.webservice.estructura;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.mapas.UtilidadesGeo;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.PlaceMark;
import alberapps.java.tam.webservice.estructura.nodosmap.EstructuraGetNodosMapSublineaParser;
import alberapps.java.tam.webservice.estructura.nodosmap.GetNodosMapSublineaResult;
import alberapps.java.tam.webservice.estructura.nodosmap.InfoNodoMap;
import alberapps.java.tam.webservice.estructura.polylinea.EstructuraGetPolylineaSublineaParser;
import alberapps.java.tam.webservice.estructura.polylinea.GetPolylineaSublineaResult;
import alberapps.java.tam.webservice.estructura.rutas.EstructuraGetRutasSublineaParser;
import alberapps.java.tam.webservice.estructura.rutas.GetRutaSublineaResult;

public class ProcesarEstructura {

    public static final String LOG_NAME = "ProcesarEstructura";

    /**
     * Datos lineas
     *
     * @return
     */
    public static DatosMapa[] getDatosLineas(String linea, String sublinea, boolean cache) {

        DatosMapa[] datosMapa = {null, null};

        boolean parche11 = false;

        String lineab = linea;

        //PARCHE 11
        if (linea.equals("11")) {
            parche11 = true;
        }

        if (linea.equals("11H")) {
            lineab = "11";
            parche11 = false;
        }

        try {


            EstructuraGetRutasSublineaParser parser = new EstructuraGetRutasSublineaParser();

            GetRutaSublineaResult datos = parser.consultarServicio(lineab, sublinea, cache);


            if (datos != null && datos.getInfoRutaList() != null && !datos.getInfoRutaList().isEmpty()) {

                datosMapa[0] = new DatosMapa();

                PlaceMark pm = null;

                List<PlaceMark> listaIda = new ArrayList<PlaceMark>();

                for (int j = 0; j < datos.getInfoRutaList().get(0).getInfoSeccion().size(); j++) {

                    if (parche11) {
                        if (!datos.getInfoRutaList().get(0).getInfoSeccion().get(j).getSeccion().equals("1")) {
                            continue;
                        }
                    }


                    for (int i = 0; i < datos.getInfoRutaList().get(0).getInfoSeccion().get(j).getNodos().size(); i++) {

                        pm = new PlaceMark();

                        if (datos.getInfoRutaList().get(0).getInfoSeccion().get(j).getNodos().get(i).getTipo().equals("3") || datos.getInfoRutaList().get(0).getInfoSeccion().get(j).getNodos().get(i).getTipo().equals("1")) {

                            pm.setCodigoParada(datos.getInfoRutaList().get(0).getInfoSeccion().get(j).getNodos().get(i).getNodo());
                            pm.setTitle(datos.getInfoRutaList().get(0).getInfoSeccion().get(j).getNodos().get(i).getNombre());
                            pm.setLineas("");
                            pm.setObservaciones("");
                            pm.setSentido(datos.getInfoRutaList().get(0).getNombre());


                            listaIda.add(pm);

                        }

                    }

                }

                datosMapa[0].setPlacemarks(listaIda);

                datosMapa[0].setCurrentPlacemark(listaIda.get(0));

            } else {

                datosMapa[0] = null;

            }

            if (datos != null && datos.getInfoRutaList() != null && !datos.getInfoRutaList().isEmpty() && datos.getInfoRutaList().size() == 2) {

                datosMapa[1] = new DatosMapa();

                PlaceMark pm = null;

                List<PlaceMark> listaIda = new ArrayList<PlaceMark>();

                for (int j = 0; j < datos.getInfoRutaList().get(1).getInfoSeccion().size(); j++) {

                    if (parche11) {
                        if (datos.getInfoRutaList().get(1).getInfoSeccion().get(j).getSeccion().equals("4") || datos.getInfoRutaList().get(1).getInfoSeccion().get(j).getSeccion().equals("5")) {

                        } else {
                            continue;
                        }
                    }

                    for (int i = 0; i < datos.getInfoRutaList().get(1).getInfoSeccion().get(j).getNodos().size(); i++) {

                        if (datos.getInfoRutaList().get(1).getInfoSeccion().get(j).getNodos().get(i).getTipo().equals("3") || datos.getInfoRutaList().get(1).getInfoSeccion().get(j).getNodos().get(i).getTipo().equals("1")) {

                            pm = new PlaceMark();

                            pm.setCodigoParada(datos.getInfoRutaList().get(1).getInfoSeccion().get(j).getNodos().get(i).getNodo());
                            pm.setTitle(datos.getInfoRutaList().get(1).getInfoSeccion().get(j).getNodos().get(i).getNombre());
                            pm.setLineas("");
                            pm.setObservaciones("");
                            pm.setSentido(datos.getInfoRutaList().get(1).getNombre());

                            listaIda.add(pm);

                        }

                    }
                }

                datosMapa[1].setPlacemarks(listaIda);

                datosMapa[1].setCurrentPlacemark(listaIda.get(0));

            } else {

                datosMapa[1] = null;

            }


        } catch (Exception e) {

            e.printStackTrace();

            datosMapa = null;
        }

        return datosMapa;
    }


    /**
     * Datos nodos mapa
     *
     * @return
     */
    public static DatosMapa[] getDatosNodosMapa(String linea, String sublinea, boolean cache) {

        DatosMapa[] datosMapa = {null, null};


        try {

            EstructuraGetNodosMapSublineaParser parser = new EstructuraGetNodosMapSublineaParser();

            //PARCHE 11
            String lineab = linea;
            if (linea.equals("11H")) {
                lineab = "11";
            }

            GetNodosMapSublineaResult datos = parser.consultarServicio(lineab, sublinea, cache);


            if (datos != null && datos.getInfoNodoMapList() != null && !datos.getInfoNodoMapList().isEmpty()) {


                DatosMapa[] datosLineas = getDatosLineas(linea, sublinea, cache);

                String parada = null;

                //Ruta 1
                for (int i = 0; i < datosLineas[0].getPlacemarks().size(); i++) {

                    parada = datosLineas[0].getPlacemarks().get(i).getCodigoParada();

                    InfoNodoMap buscar = new InfoNodoMap();
                    buscar.setNodo(parada);
                    int indice = datos.getInfoNodoMapList().lastIndexOf(buscar);

                    if (indice >= 0) {

                        double x = Double.parseDouble(datos.getInfoNodoMapList().get(indice).getPosx());
                        double y = Double.parseDouble(datos.getInfoNodoMapList().get(indice).getPosy());

                        String coord = UtilidadesGeo.getCoordenadasCorreccion(UtilidadesGeo.getLatLongUTMBus(y, x));

                        datosLineas[0].getPlacemarks().get(i).setCoordinates(coord);

                    }

                }

                //Ruta 2
                for (int i = 0; i < datosLineas[1].getPlacemarks().size(); i++) {

                    parada = datosLineas[1].getPlacemarks().get(i).getCodigoParada();

                    InfoNodoMap buscar = new InfoNodoMap();
                    buscar.setNodo(parada);
                    int indice = datos.getInfoNodoMapList().lastIndexOf(buscar);

                    if (indice >= 0) {

                        double x = Double.parseDouble(datos.getInfoNodoMapList().get(indice).getPosx());
                        double y = Double.parseDouble(datos.getInfoNodoMapList().get(indice).getPosy());

                        String coord = UtilidadesGeo.getCoordenadasCorreccion(UtilidadesGeo.getLatLongUTMBus(y, x));

                        datosLineas[1].getPlacemarks().get(i).setCoordinates(coord);

                    }

                }


                datosMapa = datosLineas;

            }

        } catch (Exception e) {

            e.printStackTrace();

            datosMapa = null;
        }

        return datosMapa;
    }


    /**
     * Datos polylinea
     *
     * @return
     */
    public static String[] getDatosPolyLinea(String linea, String sublinea, boolean cache) {

        String[] recorridos = {null, null};


        try {


            EstructuraGetPolylineaSublineaParser parser = new EstructuraGetPolylineaSublineaParser();

            //PARCHE 11
            String lineab = linea;
            if (linea.equals("11H")) {
                lineab = "11";
            }

            GetPolylineaSublineaResult datos = parser.consultarServicio(lineab, sublinea, cache);


            if (datos != null && datos.getInfoCoordList() != null && !datos.getInfoCoordList().isEmpty()) {

                StringBuffer sb = new StringBuffer("");

                for (int i = 0; i < datos.getInfoCoordList().size(); i++) {

                    double x = Double.parseDouble(datos.getInfoCoordList().get(i).getX());
                    double y = Double.parseDouble(datos.getInfoCoordList().get(i).getY());

                    String coord = UtilidadesGeo.getCoordenadasCorreccion(UtilidadesGeo.getLatLongUTMBus(y, x));

                    if (sb.length() > 0) {
                        sb.append(" ");
                    }

                    sb.append(coord);

                }

                recorridos[0] = sb.toString();


            } else {

                recorridos[0] = null;

            }


        } catch (Exception e) {

            e.printStackTrace();

            recorridos = null;
        }

        return recorridos;
    }


    /**
     * Cargar datos de transbordos desde la base de datos
     *
     * @param paradas
     * @param context
     */
    public static void cargarDatosTransbordosBD(DatosMapa paradas, Context context) {

        //Carga de informacion de transbordos

        if (context != null) {

            for (int i = 0; i < paradas.getPlacemarks().size(); i++) {

                String parametros[] = {paradas.getPlacemarks().get(i).getCodigoParada()};

                try {

                    Cursor cursor = ((FragmentActivity) context).managedQuery(BuscadorLineasProvider.DATOS_PARADA_URI, null, null, parametros, null);


                    if (cursor != null) {

                        cursor.moveToFirst();

                        int conexionesIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_CONEXION);

                        paradas.getPlacemarks().get(i).setLineas(cursor.getString(conexionesIndex));

                    }

                } catch (Exception e) {

                }

            }


        }

    }


}
