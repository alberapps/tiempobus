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
package alberapps.android.tiempobus.mapas;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.content.ContentResolverCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.PlaceMark;

/**
 * Gestion de paradas offline
 */
public class MapasOffline {

    private MapasActivity context;

    private SharedPreferences preferencias;

    public MapasOffline(MapasActivity contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }

    /**
     * Cargar datos en modo offline
     */
    public void loadDatosMapaOffline() {

        DatosMapa datosIda = new DatosMapa();
        DatosMapa datosVuelta = new DatosMapa();

        String parametros[] = {context.lineaSeleccionadaNum};

        Cursor cursorParadas = null;

        try {

            //cursorParadas = context.managedQuery(BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null);
            cursorParadas = ContentResolverCompat.query(context.getContentResolver(), BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null, null);

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

                context.datosMapaCargadosIda = datosIda;

                context.datosMapaCargadosVuelta = datosVuelta;

                // Recorrido

                Cursor cursorRecorrido = null;

                try {
                    //cursorRecorrido = context.managedQuery(BuscadorLineasProvider.PARADAS_LINEA_RECORRIDO_URI, null, null, parametros, null);
                    cursorRecorrido = ContentResolverCompat.query(context.getContentResolver(), BuscadorLineasProvider.PARADAS_LINEA_RECORRIDO_URI, null, null, parametros, null, null);
                } catch (Exception e) {
                    cursorRecorrido = null;
                    e.printStackTrace();
                }

                if (cursorRecorrido != null) {
                    cursorRecorrido.moveToFirst();

                    context.datosMapaCargadosIda.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));

                    if(cursorRecorrido.getCount() > 1) {
                        cursorRecorrido.moveToNext();
                        context.datosMapaCargadosVuelta.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
                    }

                    // Cargar datos en el mapa
                    context.gestionarLineas.cargarMapa(null);

                    cursorRecorrido.close();

                } else {
                    Toast.makeText(context, context.getString(R.string.error_datos_offline), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(context, context.getString(R.string.error_datos_offline), Toast.LENGTH_SHORT).show();
            }

            cursorParadas.close();

        } else {
            Toast.makeText(context, context.getString(R.string.error_datos_offline), Toast.LENGTH_SHORT).show();
        }

        context.dialog.dismiss();

    }

    /**
     * Cargar datos en modo offline
     */
    public void loadDatosRecorridoOffline() {

        String parametros[] = {context.lineaSeleccionadaNum};

        Cursor cursorRecorrido = null;

        try {
            cursorRecorrido = ContentResolverCompat.query(context.getContentResolver(), BuscadorLineasProvider.PARADAS_LINEA_RECORRIDO_URI, null, null, parametros, null, null);
        } catch (Exception e) {
            cursorRecorrido = null;
            e.printStackTrace();
        }

        if (cursorRecorrido != null) {
            cursorRecorrido.moveToFirst();

            context.datosMapaCargadosIda.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));

            if(cursorRecorrido.getCount() > 1) {
                cursorRecorrido.moveToNext();
                context.datosMapaCargadosVuelta.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
            }

            // Cargar datos en el mapa
            context.gestionarLineas.cargarMapa(null);

            cursorRecorrido.close();

        } else {
            Toast.makeText(context, context.getString(R.string.error_datos_offline), Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Cargar datos en modo offline
     */
    public void loadDatosMapaTRAMOffline(String linea) {

        DatosMapa datosIda = new DatosMapa();

        String lineaSel = null;

        if (linea == null) {
            lineaSel = context.lineaSeleccionadaNum;
        } else {
            lineaSel = linea;
        }

        String parametros[] = {lineaSel};

        Cursor cursorParadas = null;

        try {
            //cursorParadas = context.managedQuery(BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null);
            cursorParadas = ContentResolverCompat.query(context.getContentResolver(), BuscadorLineasProvider.PARADAS_LINEA_URI, null, null, parametros, null, null);
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

            context.datosMapaCargadosIda = datosIda;


            // Recorrido

            Cursor cursorRecorrido = null;

            try {
                //cursorRecorrido = context.managedQuery(BuscadorLineasProvider.PARADAS_LINEA_RECORRIDO_URI, null, null, parametros, null);
                cursorRecorrido = ContentResolverCompat.query(context.getContentResolver(), BuscadorLineasProvider.PARADAS_LINEA_RECORRIDO_URI, null, null, parametros, null, null);
            } catch (Exception e) {
                cursorRecorrido = null;
                e.printStackTrace();
            }

            if (cursorRecorrido != null) {
                cursorRecorrido.moveToFirst();

                context.datosMapaCargadosIda.setRecorrido(cursorRecorrido.getString(cursorRecorrido.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));

                cursorRecorrido.close();

                // Cargar datos en el mapa
                context.gestionarLineas.cargarMapa(lineaSel);


            } else {
                Toast.makeText(context, context.getString(R.string.error_datos_offline), Toast.LENGTH_SHORT).show();
            }

            cursorParadas.close();

        } else {
            Toast toast = Toast.makeText(context, context.getString(R.string.error_datos_offline), Toast.LENGTH_SHORT);
            toast.show();
        }

        context.dialog.dismiss();

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

}
