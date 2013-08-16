/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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
package alberapps.android.tiempobus.database.historial;

import java.util.HashMap;

import alberapps.android.tiempobus.data.TiempoBusDb.Favoritos;
import alberapps.android.tiempobus.database.historial.HistorialDB.Historial;
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

/**
 * Proveedor de la base datos de historial
 * 
 */
public class HistorialProvider extends ContentProvider {

	private static final String DATABASE_NAME = "historial.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLA_HISTORIAL = "historial";

	private static final UriMatcher sUriMatcher;

	private static final int HISTORIAL = 1;
	private static final int HISTORIAL_ID = 2;
	private static final int HISTORIAL_PARADA = 3;

	private static HashMap<String, String> sHistorialProjectionMap;

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
		qb.setTables(TABLA_HISTORIAL);

		switch (sUriMatcher.match(uri)) {
		case HISTORIAL:
			qb.setProjectionMap(sHistorialProjectionMap);
			break;

		case HISTORIAL_ID:
			qb.setProjectionMap(sHistorialProjectionMap);
			qb.appendWhere(Historial._ID + "=" + uri.getPathSegments().get(1));
			break;

		case HISTORIAL_PARADA:
			qb.setProjectionMap(sHistorialProjectionMap);			
			//qb.appendWhere(Historial.PARADA + "=" + selectionArgs[0]);
			selection = Historial.PARADA + "= ?";
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = HistorialDB.Historial.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		Log.d("**debug**", Integer.toString(c.getCount()));
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
		case HISTORIAL:
			return Favoritos.CONTENT_TYPE;

		case HISTORIAL_ID:
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
		if (sUriMatcher.match(uri) != HISTORIAL) {
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
		if (values.containsKey(HistorialDB.Historial.DESCRIPCION) == false) {
			values.put(HistorialDB.Historial.DESCRIPCION, Resources.getSystem().getString(android.R.string.untitled));
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		long rowId = db.insert(TABLA_HISTORIAL, Favoritos.TITULO, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(HistorialDB.Historial.CONTENT_URI, rowId);
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
		case HISTORIAL:
			count = db.delete(TABLA_HISTORIAL, where, whereArgs);
			break;

		case HISTORIAL_ID:
			String historialId = uri.getPathSegments().get(1);
			count = db.delete(TABLA_HISTORIAL, Favoritos._ID + "=" + historialId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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
		case HISTORIAL:
			count = db.update(TABLA_HISTORIAL, values, where, whereArgs);
			break;

		case HISTORIAL_ID:
			String historialId = uri.getPathSegments().get(1);
			count = db.update(TABLA_HISTORIAL, values, Favoritos._ID + "=" + historialId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
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

		sUriMatcher.addURI(HistorialDB.AUTHORITY, "historial/parada", HISTORIAL_PARADA);
		sUriMatcher.addURI(HistorialDB.AUTHORITY, "historial/#", HISTORIAL_ID);
		sUriMatcher.addURI(HistorialDB.AUTHORITY, "historial", HISTORIAL);

		sHistorialProjectionMap = new HashMap<String, String>();
		sHistorialProjectionMap.put(Historial._ID, Historial._ID);
		sHistorialProjectionMap.put(Historial.PARADA, Historial.PARADA);
		sHistorialProjectionMap.put(Historial.TITULO, Historial.TITULO);
		sHistorialProjectionMap.put(Historial.DESCRIPCION, Historial.DESCRIPCION);
		sHistorialProjectionMap.put(Historial.FECHA, Historial.FECHA);
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
		 * Crear la base de datos
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			if (db.isReadOnly()) {
				db = getWritableDatabase();
			}
			db.execSQL("CREATE TABLE " + TABLA_HISTORIAL + " (" + Historial._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + Historial.PARADA + " INTEGER," + Historial.TITULO + " TEXT," + Historial.DESCRIPCION + " TEXT,"
					+ Historial.FECHA + " DATETIME" + ");");
		}

		/**
		 * Si hay que actualizarla...
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (db.isReadOnly()) {
				db = getWritableDatabase();
			}
			Log.w("HistorialProvider", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db = getWritableDatabase();
			db.execSQL("DROP TABLE IF EXISTS " + TABLA_HISTORIAL);
			onCreate(db);
		}
	}

}