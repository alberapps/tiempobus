/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 *
 *  based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.android.tiempobus.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import alberapps.android.tiempobus.data.TiempoBusDb.Favoritos;

/**
 * Proveedor de contenido, provee el acceso a la base de datos de favoritos
 */
public class FavoritosProvider extends ContentProvider {

    private static final String DATABASE_NAME = "zgzbus.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLA_FAVORITOS = "favoritos";

    private static final UriMatcher sUriMatcher;

    private static final int FAVORITOS = 1;
    private static final int FAVORITOS_ID = 2;
    private static final int FAVORITOS_POSTE = 3;

    private static HashMap<String, String> sFavoritosProjectionMap;

    private DatabaseHelper mOpenHelper;

    /**
     * On create...
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    /**
     * Ejecuta una consulta
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLA_FAVORITOS);

        switch (sUriMatcher.match(uri)) {
            case FAVORITOS:
                qb.setProjectionMap(sFavoritosProjectionMap);
                break;

            case FAVORITOS_ID:
                qb.setProjectionMap(sFavoritosProjectionMap);
                qb.appendWhere(Favoritos._ID + "=" + uri.getPathSegments().get(1));
                break;

            case FAVORITOS_POSTE:
                qb.setProjectionMap(sFavoritosProjectionMap);
                qb.appendWhere(Favoritos.POSTE + " LIKE " + uri.getPathSegments().get(2));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = TiempoBusDb.Favoritos.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        Log.d("**debug**", c.toString());
        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    /**
     * devuelve el tipo de consulta
     */
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case FAVORITOS:
                return Favoritos.CONTENT_TYPE;

            case FAVORITOS_ID:
                return Favoritos.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    /**
     * Hace un instert en la BBDD
     */
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != FAVORITOS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(TiempoBusDb.Favoritos.DESCRIPCION) == false) {
            values.put(TiempoBusDb.Favoritos.DESCRIPCION, Resources.getSystem().getString(android.R.string.untitled));
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId = db.insert(TABLA_FAVORITOS, Favoritos.TITULO, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(TiempoBusDb.Favoritos.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    /**
     * Borra un registro
     */
    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case FAVORITOS:
                count = db.delete(TABLA_FAVORITOS, where, whereArgs);
                break;

            case FAVORITOS_ID:
                String favoritosId = uri.getPathSegments().get(1);
                count = db.delete(TABLA_FAVORITOS, Favoritos._ID + "=" + favoritosId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * Actualiza un registro
     */
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case FAVORITOS:
                count = db.update(TABLA_FAVORITOS, values, where, whereArgs);
                break;

            case FAVORITOS_ID:
                String favoritoId = uri.getPathSegments().get(1);
                count = db.update(TABLA_FAVORITOS, values, Favoritos._ID + "=" + favoritoId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * Configuramos las urls disponibles
     */
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sUriMatcher.addURI(TiempoBusDb.AUTHORITY, "favoritos/poste/#", FAVORITOS_POSTE);
        sUriMatcher.addURI(TiempoBusDb.AUTHORITY, "favoritos/#", FAVORITOS_ID);
        sUriMatcher.addURI(TiempoBusDb.AUTHORITY, "favoritos", FAVORITOS);

        sFavoritosProjectionMap = new HashMap<String, String>();
        sFavoritosProjectionMap.put(Favoritos._ID, Favoritos._ID);
        sFavoritosProjectionMap.put(Favoritos.POSTE, Favoritos.POSTE);
        sFavoritosProjectionMap.put(Favoritos.TITULO, Favoritos.TITULO);
        sFavoritosProjectionMap.put(Favoritos.DESCRIPCION, Favoritos.DESCRIPCION);
    }

    /**
     * Clase de ayuda para abrir, crear y "upgradear" el fichero de base de
     * datos
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        /**
         * Configura la BBDD
         *
         * @param context
         */
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Si todavia no se ha creado la BBDD, la creamos y cargamos la
         * estructura
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            if (db.isReadOnly()) {
                db = getWritableDatabase();
            }
            db.execSQL("CREATE TABLE " + TABLA_FAVORITOS + " (" + Favoritos._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Favoritos.POSTE + " INTEGER," + Favoritos.TITULO + " TEXT," + Favoritos.DESCRIPCION + " TEXT" + ");");
        }

        /**
         * Si hay que actualizarla...
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (db.isReadOnly()) {
                db = getWritableDatabase();
            }
            Log.w("FavoritosProvider", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db = getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + TABLA_FAVORITOS);
            onCreate(db);
        }
    }

}