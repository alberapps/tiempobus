/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p>
 * based on code by The Android Open Source Project
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.database;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.java.actualizador.DescargarActualizaBD;
import alberapps.java.tram.UtilidadesTRAM;

/**
 * Base de datos de lineas y recorridos
 */
public class DatosLineasDB {
    private static final String TAG = "DatosLineasDB";

    // The columns we'll include in the dictionary table
    public static final String KEY_WORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_DEFINITION = SearchManager.SUGGEST_COLUMN_TEXT_2;

    public static final String COLUMN_LINEA_NUM = "LINEA_NUM";
    public static final String COLUMN_LINEA_DESC = "LINEA_DESC";
    public static final String COLUMN_DESTINO = "DESTINO";
    public static final String COLUMN_PARADA = "PARADA";
    public static final String COLUMN_COORDENADAS = "COORDENADAS";
    public static final String COLUMN_DIRECCION = "DIRECCION";
    public static final String COLUMN_CONEXION = "CONEXION";
    public static final String COLUMN_OBSERVACIONES = "OBSERVACIONES";

    public static final String COLUMN_LATITUD = "LATITUD";
    public static final String COLUMN_LONGITUD = "LONGITUD";

    public static final String COLUMN_RED_LINEAS = "RED_LINEAS";

    private static final String DATABASE_NAME = "tiempobuslineas";
    private static final String FTS_VIRTUAL_TABLE = "FTSlineas";
    private static final String FTS_VIRTUAL_TABLE_RECORRIDO = "FTSlineasRecorrido";
    private static final int DATABASE_VERSION = 78;

    public static final String DATABASE_VERSION_FECHA = "18072017";

    private final DatosLineasOpenHelper mDatabaseOpenHelper;
    private static final HashMap<String, String> mColumnMap = buildColumnMap();

    private static final HashMap<String, String> mColumnMapRecorrido = buildColumnMapRecorrido();

    static Context contexto;

    public static final String RED_TAM = "TAM";
    public static final String RED_TRAM = "TRAM";

    /**
     * Constructor
     *
     * @param context The Context within which to work, used to create the DB
     */
    public DatosLineasDB(Context context) {
        contexto = context;
        mDatabaseOpenHelper = new DatosLineasOpenHelper(context);
    }

    /**
     * Builds a map for all columns that may be requested, which will be given
     * to the SQLiteQueryBuilder. This is a good way to define aliases for
     * column names, but must include all columns, even if the value is the key.
     * This allows the ContentProvider to request columns w/o the need to know
     * real column names and create the alias itself.
     */
    private static HashMap<String, String> buildColumnMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_WORD, KEY_WORD);
        map.put(KEY_DEFINITION, KEY_DEFINITION);
        map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
        map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
        map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " + SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);

        map.put(COLUMN_LATITUD, COLUMN_LATITUD);
        map.put(COLUMN_LONGITUD, COLUMN_LONGITUD);

        map.put(COLUMN_CONEXION, COLUMN_CONEXION);
        map.put(COLUMN_COORDENADAS, COLUMN_COORDENADAS);
        map.put(COLUMN_DESTINO, COLUMN_DESTINO);
        map.put(COLUMN_DIRECCION, COLUMN_DIRECCION);
        map.put(COLUMN_LINEA_DESC, COLUMN_LINEA_DESC);
        map.put(COLUMN_LINEA_NUM, COLUMN_LINEA_NUM);
        map.put(COLUMN_LONGITUD, COLUMN_LONGITUD);
        map.put(COLUMN_PARADA, COLUMN_PARADA);
        map.put(COLUMN_OBSERVACIONES, COLUMN_OBSERVACIONES);
        map.put(COLUMN_RED_LINEAS, COLUMN_RED_LINEAS);

        return map;
    }

    private static HashMap<String, String> buildColumnMapRecorrido() {
        HashMap<String, String> map = new HashMap<>();

        map.put(COLUMN_COORDENADAS, COLUMN_COORDENADAS);
        map.put(COLUMN_DESTINO, COLUMN_DESTINO);

        map.put(COLUMN_LINEA_NUM, COLUMN_LINEA_NUM);
        map.put(COLUMN_RED_LINEAS, COLUMN_RED_LINEAS);

        return map;
    }

    /**
     * Returns a Cursor positioned at the word specified by rowId
     *
     * @param rowId   id of word to retrieve
     * @param columns The columns to include, if null then all are included
     * @return Cursor positioned to matching word, or null if not found.
     */
    public Cursor getWord(String rowId, String[] columns) {
        String selection = "rowid = ?";
        String[] selectionArgs = new String[]{rowId};

        return query(null, selection, selectionArgs, columns);

		/*
         * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE rowid = <rowId>
		 */
    }

    /**
     * Returns a Cursor over all words that match the given query
     *
     * @param query   The string to search for
     * @param columns The columns to include, if null then all are included
     * @return Cursor over all words that match, or null if none found.
     */
    public Cursor getWordMatches(String query, String[] columns) {
        String selection = KEY_WORD + " MATCH ?";
        String[] selectionArgs = new String[]{query + "*"};

        return query(null, selection, selectionArgs, columns);

		/*
         * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE <KEY_WORD> MATCH 'query*' which is an FTS3 search for the query
		 * text (plus a wildcard) inside the word column.
		 * 
		 * - "rowid" is the unique id for all rows but we need this value for
		 * the "_id" column in order for the Adapters to work, so the columns
		 * need to make "_id" an alias for "rowid" - "rowid" also needs to be
		 * used by the SUGGEST_COLUMN_INTENT_DATA alias in order for suggestions
		 * to carry the proper intent data. These aliases are defined in the
		 * DictionaryProvider when queries are made. - This can be revised to
		 * also search the definition text with FTS3 by changing the selection
		 * clause to use FTS_VIRTUAL_TABLE instead of KEY_WORD (to search across
		 * the entire table, but sorting the relevance could be difficult.
		 */
    }

    public Cursor getPuntosProximos(String latitudActual, String longitudActual, String distancia, String[] columns) {

        // double lat = Double.parseDouble(latitudActual); // 38.386058;
        // double lng = Double.parseDouble(longitudActual); // -0.510018;
        double dst = Double.parseDouble(distancia); // -0.510018;
        // int glat = (int) (lat * 1E6);
        // int glng = (int) (lng * 1E6);

        int glat = Integer.parseInt(latitudActual);
        int glng = Integer.parseInt(longitudActual);

        int gdst = (int) (dst * 1E6);

        String selection = COLUMN_LATITUD + "> (" + (glat + gdst) + ") AND " + COLUMN_LATITUD + " < (" + (glat - gdst) + ") AND " + COLUMN_LONGITUD + " > (" + (glng + gdst) + ") AND " + COLUMN_LONGITUD + " < ("
                + (glng - gdst) + ")";

        String[] selectionArgs = new String[]{};

        return query(null, selection, selectionArgs, columns);

		/*
         * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE rowid = <rowId>
		 */
    }

    public Cursor getParadasLinea(String linea, String[] columns) {

        String selection = COLUMN_LINEA_NUM + " = ? ";

        String[] selectionArgs = new String[]{linea};

        return query(null, selection, selectionArgs, columns);

		/*
         * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE rowid = <rowId>
		 */
    }

    public Cursor getParadasLineaRecorrido(String linea, String[] columns) {

        String selection = COLUMN_LINEA_NUM + " = ? ";

        String[] selectionArgs = new String[]{linea};

        return query(FTS_VIRTUAL_TABLE_RECORRIDO, selection, selectionArgs, columns);

    }

    public Cursor getDatosParada(String parada, String[] columns) {

        String selection = COLUMN_PARADA + " = ? ";

        String[] selectionArgs = new String[]{parada};

        return query(null, selection, selectionArgs, columns);

		/*
		 * This builds a query that looks like: SELECT <columns> FROM <table>
		 * WHERE rowid = <rowId>
		 */
    }

    /**
     * Performs a database query.
     *
     * @param selection     The selection clause
     * @param selectionArgs Selection arguments for "?" components in the selection
     * @param columns       The columns to return
     * @return A Cursor over all rows matching the query
     */
    private Cursor query(String table, String selection, String[] selectionArgs, String[] columns) {
		/*
		 * The SQLiteBuilder provides a map for all possible columns requested
		 * to actual columns in the database, creating a simple column alias
		 * mechanism by which the ContentProvider does not need to know the real
		 * column names
		 */
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        if (table != null && table.equals(FTS_VIRTUAL_TABLE_RECORRIDO)) {

            builder.setTables(FTS_VIRTUAL_TABLE_RECORRIDO);
            builder.setProjectionMap(mColumnMapRecorrido);

        } else {

            builder.setTables(FTS_VIRTUAL_TABLE);
            builder.setProjectionMap(mColumnMap);
        }

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(), columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    /**
     * Recarga de la base datos de la version
     */
    public void recargaManual() {

        mDatabaseOpenHelper.reCargarBaseDatos(null);

    }

    /**
     * Actualizacion a partir de los archivos descargados
     */
    public void actualizarDescarga() {

        mDatabaseOpenHelper.reCargarBaseDatos(DescargarActualizaBD.BD_DESCARGA);

    }

    /**
     * This creates/opens the database.
     */
    private static class DatosLineasOpenHelper extends SQLiteOpenHelper {

        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        /*
         * Note that FTS3 does not support column constraints and thus, you
         * cannot declare a primary key. However, "rowid" is automatically used
         * as a unique identifier, so when making requests, we will use "_id" as
         * an alias for "rowid"
         */
        private static final String FTS_TABLE_CREATE = "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " USING fts3 (" + KEY_WORD + ", " + KEY_DEFINITION + ", " + COLUMN_LINEA_NUM + ", " + COLUMN_LINEA_DESC + ", "
                + COLUMN_DESTINO + ", " + COLUMN_PARADA + ", " + COLUMN_COORDENADAS + ", " + COLUMN_DIRECCION + ", " + COLUMN_CONEXION + ", " + COLUMN_LATITUD + ", " + COLUMN_LONGITUD + ", " + COLUMN_OBSERVACIONES
                + ", " + COLUMN_RED_LINEAS + ");";

        private static final String FTS_TABLE_CREATE_RECORRIDO = "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE_RECORRIDO + " USING fts3 (" + COLUMN_LINEA_NUM + ", " + COLUMN_DESTINO + ", " + COLUMN_COORDENADAS + ", "
                + COLUMN_RED_LINEAS + ", " + ");";

        DatosLineasOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;

            cargarBaseDatos();

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            mDatabase = db;
            super.onOpen(db);
        }

        /**
         * Carga inicial de la base de datos
         */
        private void cargarBaseDatos() {

            new Thread(new Runnable() {
                public void run() {

                    final Builder mBuilder = Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INICIAL, null, null);

                    // mDatabase.beginTransaction();

                    try {

                        mDatabase.execSQL(FTS_TABLE_CREATE);
                        mDatabase.execSQL(FTS_TABLE_CREATE_RECORRIDO);

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INCREMENTA, mBuilder, 20);

                        cargarLineas(null);

                        if (UtilidadesTRAM.ACTIVADO_TRAM) {
                            // TRAM
                            cargarLineasTRAM();
                        }

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INCREMENTA, mBuilder, 50);

                        cargarRecorridos(null);

                        if (UtilidadesTRAM.ACTIVADO_TRAM) {

                            Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INCREMENTA, mBuilder, 70);

                            // TRAM
                            cargarRecorridosTram(null);

                        }

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INCREMENTA, mBuilder, 100);

                        // mDatabase.setTransactionSuccessful();

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_FINAL, mBuilder, null);

                    } catch (Exception e) {

                        Log.d(TAG, "No se pueden precargar los datos");

                        e.printStackTrace();

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_ERROR, mBuilder, null);

                    } finally {

                        // mDatabase.endTransaction();

                    }

                }
            }).start();
        }

        /**
         * Recarga de la base de datos
         */
        private void reCargarBaseDatos(final String origen) {

            new Thread(new Runnable() {
                public void run() {

                    final Builder mBuilder = Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INICIAL, null, null);

                    // mDatabase.beginTransaction();

                    try {

                        mDatabase.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
                        mDatabase.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE_RECORRIDO);

                        mDatabase.execSQL(FTS_TABLE_CREATE);
                        mDatabase.execSQL(FTS_TABLE_CREATE_RECORRIDO);

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INCREMENTA, mBuilder, 20);

                        cargarLineas(origen);

                        if (UtilidadesTRAM.ACTIVADO_TRAM) {

                            Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INCREMENTA, mBuilder, 30);

                            // TRAM
                            cargarLineasTRAM();

                        }

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INCREMENTA, mBuilder, 50);

                        cargarRecorridos(origen);

                        if (UtilidadesTRAM.ACTIVADO_TRAM) {

                            Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INCREMENTA, mBuilder, 70);

                            // TRAM
                            cargarRecorridosTram(origen);

                        }

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_INCREMENTA, mBuilder, 100);

                        // mDatabase.setTransactionSuccessful();

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_FINAL, mBuilder, null);

                    } catch (Exception e) {

                        Log.d(TAG, "No se pueden precargar los datos");

                        e.printStackTrace();

                        Notificaciones.notificacionBaseDatos(contexto, Notificaciones.NOTIFICACION_BD_ERROR, mBuilder, null);

                    } finally {

                        // mDatabase.endTransaction();

                    }

                }
            }).start();
        }

        /**
         * Carga de lineas desde archivo
         *
         * @param origen
         * @throws IOException
         */
        private void cargarLineas(String origen) throws IOException {
            Log.d(TAG, "Loading database LINEA...");
            Resources resources = mHelperContext.getResources();

            InputStream inputStream = null;

            if (origen != null && origen.equals(DescargarActualizaBD.BD_DESCARGA)) {

                inputStream = DescargarActualizaBD.inputStreamInfolineas();

            } else {
                inputStream = resources.openRawResource(R.raw.precargainfolineas);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {

                //Carga de datos
                List<Datos> datosList = new ArrayList<>();
                Datos dato = null;

                String line;
                while ((line = reader.readLine()) != null) {

                    // Para TAM
                    line = line.concat(";;TAM");

                    String[] strings = TextUtils.split(line, ";;");
                    // if (strings.length < 2) continue;

                    dato = new Datos();
                    dato.setWord(strings[0].trim() + " > " + strings[2].trim() + " - " + strings[3].trim() + " " + strings[5].trim());
                    dato.setDefinition(strings[1].trim() + " > " + strings[2].trim() + " [" + strings[3].trim()
                            + "] - " + strings[5].trim());
                    dato.setDatos(strings);


                    dato.setParada(strings[3].trim());
                    dato.setLinea(strings[0].trim());

                    datosList.add(dato);
                }


                //Buscar transbordos
                for (int i = 0; i < datosList.size(); i++) {
                    for (int j = 0; j < datosList.size(); j++) {

                        if (datosList.get(i).getParada().equals(datosList.get(j).getParada())) {

                            if (datosList.get(i).getTransbordos() == null) {
                                datosList.get(i).setTransbordos(new ArrayList<String>());
                            }

                            if (!datosList.get(i).getTransbordos().contains(datosList.get(j).getLinea())) {
                                datosList.get(i).getTransbordos().add(datosList.get(j).getLinea());
                            }


                        }

                    }


                }


                String tb = null;

                //Guardar en base de datos
                for (int i = 0; i < datosList.size(); i++) {

                    tb = null;

                    for (int j = 0; j < datosList.get(i).getTransbordos().size(); j++) {

                        if (tb != null) {
                            tb = tb + ", ";
                        } else {
                            tb = "";
                        }

                        tb = tb + datosList.get(i).getTransbordos().get(j);

                    }


                    long id = addWord(datosList.get(i).getWord(), datosList.get(i).getDefinition(), datosList.get(i).getDatos(), tb);
                    if (id < 0) {
                        assert dato != null;
                        Log.e(TAG, "unable to add line: " + dato.getLinea());
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                inputStream.close();
                reader.close();

            }

            // Borrar archivos de la actualizacion
            if (origen != null && origen.equals(DescargarActualizaBD.BD_DESCARGA)) {

                DescargarActualizaBD.borrarArchivosLineas();

            }

            Log.d(TAG, "DONE loading database LINEA.");
        }

        /**
         * Carga de recorridos desde archivo
         *
         * @param origen
         * @throws IOException
         */
        private void cargarRecorridos(String origen) throws IOException {
            Log.d(TAG, "Loading database RECORRIDO...");
            Resources resources = mHelperContext.getResources();

            InputStream inputStreamR = null;
            BufferedReader readerR = null;
            InputStream inputStreamR2 = null;
            BufferedReader readerR2 = null;

            if (origen != null && origen.equals(DescargarActualizaBD.BD_DESCARGA)) {

                inputStreamR = DescargarActualizaBD.inputStreamInfolineasRecorrido1();
                readerR = new BufferedReader(new InputStreamReader(inputStreamR));

                inputStreamR2 = DescargarActualizaBD.inputStreamInfolineasRecorrido2();
                readerR2 = new BufferedReader(new InputStreamReader(inputStreamR2));

            } else {

                inputStreamR = resources.openRawResource(R.raw.precargainfolineasrecorrido);
                readerR = new BufferedReader(new InputStreamReader(inputStreamR));

                inputStreamR2 = resources.openRawResource(R.raw.precargainfolineasrecorrido2);
                readerR2 = new BufferedReader(new InputStreamReader(inputStreamR2));

            }

            try {
                String line;

                // Recorridos
                line = null;
                while ((line = readerR.readLine()) != null) {

                    // Para TAM
                    line = line.concat(";;TAM");

                    String[] strings = TextUtils.split(line, ";;");
                    // if (strings.length < 2) continue;

                    long id = addRecorrido(strings);
                    if (id < 0) {
                        Log.e(TAG, "unable to add line: " + strings[0].trim());
                    }
                }

                while ((line = readerR2.readLine()) != null) {

                    // Para TAM
                    line = line.concat(";;TAM");

                    String[] strings = TextUtils.split(line, ";;");
                    // if (strings.length < 2) continue;

                    long id = addRecorrido(strings);
                    if (id < 0) {
                        Log.e(TAG, "unable to add line: " + strings[0].trim());
                    }
                }

            } finally {

                inputStreamR.close();
                readerR.close();
                inputStreamR2.close();
                readerR2.close();
            }

            // Borrar archivos de la actualizacion
            if (origen != null && origen.equals(DescargarActualizaBD.BD_DESCARGA)) {

                DescargarActualizaBD.borrarArchivosRecorridos();

            }

            Log.d(TAG, "DONE loading database RECORRIDO.");
        }

        private void cargarLineasTRAM() throws IOException {
            Log.d(TAG, "Loading database LINEA TRAM...");
            Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.precargainfolineas_tram);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                while ((line = reader.readLine()) != null) {

                    // [nombre, identificador, latitud, longitud,1/0 si hay o no
                    // marquesinas de la emt cerca,lista de líneas de la
                    // estación] de las estaciones
                    // ["Albufereta",7,38.3667945862,-0.4425750077,1,3,4],

                    // 21;;21 ALICANTE-P.S.JUAN-EL CAMPELLO;;El
                    // Campello;;2964;;-0.494466158,38.342115635,0;;OSCAR ESPLA
                    // 33;;21, 22, 24;;

                    // 4L -> 41

                    line = line.replace("[", "");
                    line = line.replace("]", "");
                    line = line.replace("\"", "");

                    String[] strings1 = TextUtils.split(line, ",");

                    String numParada = strings1[1];
                    String latitud = strings1[2];
                    String longitud = strings1[3];
                    String direccion = strings1[0];

                    String[] strings = new String[9];

                    // Transbordos
                    StringBuffer transbordo = new StringBuffer();
                    for (int i = 5; i < strings1.length - 1; i++) {

                        if (transbordo.length() > 0) {
                            transbordo.append(",");
                        }

                        if (strings1[i].equals("1")) {
                            transbordo.append("L1");
                        } else if (strings1[i].equals("3")) {
                            transbordo.append("L3");
                        } else if (strings1[i].equals("4")) {
                            transbordo.append("L4");
                        } else if (strings1[i].equals("9")) {
                            transbordo.append("L9");
                        } else if (strings1[i].equals("41")) {
                            transbordo.append("4L");
                        } else if (strings1[i].equals("2")) {
                            transbordo.append("L2");
                        }

                    }

                    // Monta la linea para BD
                    for (int i = 5; i < strings1.length - 1; i++) {

                        if (strings1[i].equals("1")) {
                            strings[0] = "L1";
                            strings[1] = "L1";
                        } else if (strings1[i].equals("3")) {
                            strings[0] = "L3";
                            strings[1] = "L3";
                        } else if (strings1[i].equals("4")) {
                            strings[0] = "L4";
                            strings[1] = "L4";
                        } else if (strings1[i].equals("9")) {
                            strings[0] = "L9";
                            strings[1] = "L9";
                        } else if (strings1[i].equals("41")) {
                            strings[0] = "4L";
                            strings[1] = "4L";
                        } else if (strings1[i].equals("2")) {
                            strings[0] = "L2";
                            strings[1] = "L2";
                        }

                        int posicion = UtilidadesTRAM.getIdLinea(strings[0]);

                        Log.d("DB", "linea = " + strings[0] + " - " + strings1[i]);
                        Log.d("DB", "poscion = " + Integer.toString(posicion));
                        Log.d("DB", "poscion 2 = " + UtilidadesTRAM.TIPO[posicion]);
                        Log.d("DB", "poscion 3 = " + UtilidadesTRAM.DESC_LINEA[UtilidadesTRAM.TIPO[posicion]]);

                        strings[2] = UtilidadesTRAM.DESC_LINEA[UtilidadesTRAM.TIPO[posicion]];

                        // strings[2] = "IDA";

                        strings[3] = numParada;

                        strings[4] = longitud + "," + latitud + ",0";

                        strings[5] = direccion;

                        strings[6] = transbordo.toString();

                        strings[7] = "";


                        strings[8] = "TRAM";

                        // ida
                        long id = addWord(strings[0].trim() + " > " + strings[2].trim() + " - " + strings[3].trim() + " " + strings[5].trim(), strings[1].trim() + " > " + strings[2].trim() + " [" + strings[3].trim()
                                + "] - " + strings[5].trim(), strings, null);
                        if (id < 0) {
                            Log.e(TAG, "unable to add line: " + strings[0].trim());
                        }


                    }

                }

            } finally {
                inputStream.close();
                reader.close();

            }
            Log.d(TAG, "DONE loading database LINEA TRAM.");
        }

        /**
         * Carga de recorridos desde archivo
         *
         * @param origen
         * @throws IOException
         */
        private void cargarRecorridosTram(String origen) throws IOException {
            Log.d(TAG, "Loading database RECORRIDO TRAM...");
            Resources resources = mHelperContext.getResources();

            InputStream inputStreamR = null;
            BufferedReader readerR = null;


            inputStreamR = resources.openRawResource(R.raw.preinforecorridotram);
            readerR = new BufferedReader(new InputStreamReader(inputStreamR));


            try {
                String line;

                StringBuffer componerLinea = new StringBuffer();

                // Recorridos
                line = null;
                while ((line = readerR.readLine()) != null) {

                    if (line.contains("LINEA:")) {

                        String lin = line.substring(line.lastIndexOf(":") + 1);

                        if (componerLinea.length() > 0) {
                            componerLinea.append(";;TRAM\n");
                        }
                        componerLinea.append(lin);
                        componerLinea.append(";;IDA;;");
                    } else if (line.startsWith(",[") || line.startsWith("]]")) {
                        continue;
                    } else {

                        String[] punto = TextUtils.split(line, ",");

                        componerLinea.append(punto[2]);
                        componerLinea.append(",");
                        componerLinea.append(punto[1]);
                        componerLinea.append(",0 ");

                    }
                }

                if (componerLinea.length() > 0) {
                    componerLinea.append(";;TRAM\n");
                }

                // Almacenar datos ya formateados

                String[] lineas = TextUtils.split(componerLinea.toString(), "\n");

                for (int i = 0; i < lineas.length; i++) {

                    if (lineas[i].trim().equals("")) {
                        continue;
                    }

                    String[] strings = TextUtils.split(lineas[i], ";;");

                    long id = addRecorrido(strings);

                    if (id < 0) {
                        Log.e(TAG, "unable to add line: " + strings[0].trim());
                    }

                }


            } finally {

                inputStreamR.close();
                readerR.close();
                // inputStreamR2.close();
                // readerR2.close();
            }

            // Borrar archivos de la actualizacion
			/*
			 * if (origen != null &&
			 * origen.equals(DescargarActualizaBD.BD_DESCARGA)) {
			 * 
			 * DescargarActualizaBD.borrarArchivosRecorridos();
			 * 
			 * }
			 */

            Log.d(TAG, "DONE loading database RECORRIDO TRAM.");
        }

        /**
         * Add a word to the dictionary.
         *
         * @return rowId or -1 if failed
         */
        public long addWord(String word, String definition, String[] datos, String transbordos) {
            ContentValues initialValues = new ContentValues();

            initialValues.put(KEY_DEFINITION, definition);

            // Datos
            initialValues.put(COLUMN_LINEA_NUM, datos[0].trim());
            initialValues.put(COLUMN_LINEA_DESC, datos[1].trim());
            initialValues.put(COLUMN_DESTINO, datos[2].trim());
            initialValues.put(COLUMN_PARADA, datos[3].trim());
            initialValues.put(COLUMN_COORDENADAS, datos[4].trim());
            initialValues.put(COLUMN_DIRECCION, datos[5].trim());

            if (transbordos != null && !transbordos.equals("")) {
                initialValues.put(COLUMN_CONEXION, transbordos);
            } else {
                initialValues.put(COLUMN_CONEXION, datos[6].trim());
            }

            initialValues.put(COLUMN_OBSERVACIONES, datos[7].trim());

            initialValues.put(COLUMN_RED_LINEAS, datos[8].trim());

            String[] coordenadas = datos[4].trim().split(",");

            double lat = Double.parseDouble(coordenadas[1]); // 38.386058;
            double lng = Double.parseDouble(coordenadas[0]); // -0.510018;
            int glat = (int) (lat * 1E6);
            int glng = (int) (lng * 1E6);

            // Carga las coordenadas y las transforma

            initialValues.put(COLUMN_LONGITUD, glng);
            initialValues.put(COLUMN_LATITUD, glat);

            initialValues.put(KEY_WORD, word);

            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
        }

        public long addRecorrido(String[] datos) {
            ContentValues initialValues = new ContentValues();

            // Datos
            initialValues.put(COLUMN_LINEA_NUM, datos[0].trim());
            initialValues.put(COLUMN_DESTINO, datos[1].trim());
            initialValues.put(COLUMN_COORDENADAS, datos[2].trim());

            initialValues.put(COLUMN_RED_LINEAS, datos[3].trim());

            return mDatabase.insert(FTS_VIRTUAL_TABLE_RECORRIDO, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

            mDatabase = db;

            reCargarBaseDatos(null);

        }

    }

}
