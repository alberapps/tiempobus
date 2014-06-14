/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
 *  based on code by The Android Open Source Project
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
package alberapps.android.tiempobus.database;

import java.util.Locale;

import alberapps.android.tiempobus.util.UtilidadesUI;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Provides access to the dictionary database.
 */
public class BuscadorLineasProvider extends ContentProvider {
	String TAG = "BuscadorLineasProvider";

	public static String AUTHORITY = "alberapps.android.tiempobus.buscador.BuscadorLineasProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/datosLineas");
	public static final Uri PARADAS_PROXIMAS_URI = Uri.parse("content://" + AUTHORITY + "/get_paradas_proximas");
	public static final Uri PARADAS_LINEA_URI = Uri.parse("content://" + AUTHORITY + "/get_paradas_linea");
	public static final Uri PARADAS_LINEA_RECORRIDO_URI = Uri.parse("content://" + AUTHORITY + "/get_paradas_linea_recorrido");
	public static final Uri DATOS_PARADA_URI = Uri.parse("content://" + AUTHORITY + "/get_datos_parada");

	// MIME types used for searching words or looking up a single definition
	public static final String WORDS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.alberapps.android.tiempobus.buscador";
	public static final String DEFINITION_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.alberapps.android.tiempobus.buscador";

	private DatosLineasDB mDictionary;

	// UriMatcher stuff
	private static final int SEARCH_WORDS = 0;
	private static final int GET_WORD = 1;
	private static final int SEARCH_SUGGEST = 2;
	private static final int REFRESH_SHORTCUT = 3;
	public static final int GET_PARADAS_PROXIMAS = 4;
	public static final int GET_PARADAS_LINEA = 5;
	public static final int GET_DATOS_PARADA = 6;
	public static final int GET_PARADAS_LINEA_RECORRIDO = 7;

	private static final UriMatcher sURIMatcher = buildUriMatcher();

	/**
	 * Builds up a UriMatcher for search suggestion and shortcut refresh
	 * queries.
	 */
	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		// to get definitions...
		matcher.addURI(AUTHORITY, "datosLineas", SEARCH_WORDS);
		matcher.addURI(AUTHORITY, "datosLineas/#", GET_WORD);
		// to get suggestions...
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

		/*
		 * The following are unused in this implementation, but if we include
		 * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our
		 * suggestions table, we could expect to receive refresh queries when a
		 * shortcutted suggestion is displayed in Quick Search Box, in which
		 * case, the following Uris would be provided and we would return a
		 * cursor with a single item representing the refreshed suggestion data.
		 */
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);

		matcher.addURI(AUTHORITY, "get_paradas_proximas", GET_PARADAS_PROXIMAS);

		matcher.addURI(AUTHORITY, "get_paradas_linea", GET_PARADAS_LINEA);

		matcher.addURI(AUTHORITY, "get_paradas_linea_recorrido", GET_PARADAS_LINEA_RECORRIDO);

		matcher.addURI(AUTHORITY, "get_datos_parada", GET_DATOS_PARADA);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		mDictionary = new DatosLineasDB(getContext());
		return true;
	}

	/**
	 * Handles all the dictionary searches and suggestion queries from the
	 * Search Manager. When requesting a specific word, the uri alone is
	 * required. When searching all of the dictionary for matches, the
	 * selectionArgs argument must carry the search query as the first element.
	 * All other arguments are ignored.
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		// Use the UriMatcher to see what kind of query we have and format the
		// db query accordingly
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return getSuggestions(selectionArgs[0]);
		case SEARCH_WORDS:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return search(selectionArgs[0]);
		case GET_WORD:
			return getWord(uri);
		case REFRESH_SHORTCUT:
			return refreshShortcut(uri);

		case GET_PARADAS_PROXIMAS:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return getParadasProximas(uri, selectionArgs[0], selectionArgs[1], selectionArgs[2]);

		case GET_PARADAS_LINEA:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return getParadasLinea(uri, selectionArgs[0]);

		case GET_PARADAS_LINEA_RECORRIDO:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return getParadasLineaRecorrido(uri, selectionArgs[0]);

		case GET_DATOS_PARADA:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return getDatosParada(uri, selectionArgs[0]);

		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	private Cursor getSuggestions(String query) {
		query = query.toLowerCase(UtilidadesUI.getLocaleInt());
		String[] columns = new String[] { BaseColumns._ID, DatosLineasDB.KEY_WORD, DatosLineasDB.KEY_DEFINITION,
		/*
		 * SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, (only if you want to
		 * refresh shortcuts)
		 */
		SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };

		return mDictionary.getWordMatches(query, columns);
	}

	private Cursor search(String query) {
		query = query.toLowerCase(UtilidadesUI.getLocaleInt());
		String[] columns = new String[] { BaseColumns._ID, DatosLineasDB.KEY_WORD, DatosLineasDB.KEY_DEFINITION };

		return mDictionary.getWordMatches(query, columns);
	}

	private Cursor getWord(Uri uri) {
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] { DatosLineasDB.KEY_WORD, DatosLineasDB.KEY_DEFINITION, DatosLineasDB.COLUMN_CONEXION, DatosLineasDB.COLUMN_COORDENADAS, DatosLineasDB.COLUMN_DESTINO,
				DatosLineasDB.COLUMN_DIRECCION, DatosLineasDB.COLUMN_LATITUD, DatosLineasDB.COLUMN_LINEA_DESC, DatosLineasDB.COLUMN_LINEA_NUM, DatosLineasDB.COLUMN_LONGITUD, DatosLineasDB.COLUMN_PARADA,
				DatosLineasDB.COLUMN_OBSERVACIONES };

		return mDictionary.getWord(rowId, columns);
	}

	private Cursor getParadasProximas(Uri uri, String latitudActual, String longitudActual, String distancia) {

		String[] columns = new String[] { DatosLineasDB.COLUMN_CONEXION, DatosLineasDB.COLUMN_COORDENADAS, DatosLineasDB.COLUMN_DESTINO, DatosLineasDB.COLUMN_DIRECCION, DatosLineasDB.COLUMN_LATITUD,
				DatosLineasDB.COLUMN_LINEA_DESC, DatosLineasDB.COLUMN_LINEA_NUM, DatosLineasDB.COLUMN_LONGITUD, DatosLineasDB.COLUMN_PARADA, DatosLineasDB.COLUMN_OBSERVACIONES, DatosLineasDB.COLUMN_RED_LINEAS };

		return mDictionary.getPuntosProximos(latitudActual, longitudActual, distancia, columns);
	}

	private Cursor getParadasLinea(Uri uri, String linea) {

		String[] columns = new String[] { DatosLineasDB.COLUMN_CONEXION, DatosLineasDB.COLUMN_COORDENADAS, DatosLineasDB.COLUMN_DESTINO, DatosLineasDB.COLUMN_DIRECCION, DatosLineasDB.COLUMN_LATITUD,
				DatosLineasDB.COLUMN_LINEA_DESC, DatosLineasDB.COLUMN_LINEA_NUM, DatosLineasDB.COLUMN_LONGITUD, DatosLineasDB.COLUMN_PARADA, DatosLineasDB.COLUMN_OBSERVACIONES };

		return mDictionary.getParadasLinea(linea, columns);
	}

	private Cursor getParadasLineaRecorrido(Uri uri, String linea) {

		String[] columns = new String[] { DatosLineasDB.COLUMN_LINEA_NUM, DatosLineasDB.COLUMN_DESTINO, DatosLineasDB.COLUMN_COORDENADAS };

		return mDictionary.getParadasLineaRecorrido(linea, columns);
	}

	private Cursor getDatosParada(Uri uri, String parada) {

		String[] columns = new String[] { DatosLineasDB.COLUMN_CONEXION, DatosLineasDB.COLUMN_COORDENADAS, DatosLineasDB.COLUMN_DESTINO, DatosLineasDB.COLUMN_DIRECCION, DatosLineasDB.COLUMN_LATITUD,
				DatosLineasDB.COLUMN_LINEA_DESC, DatosLineasDB.COLUMN_LINEA_NUM, DatosLineasDB.COLUMN_LONGITUD, DatosLineasDB.COLUMN_PARADA, DatosLineasDB.COLUMN_OBSERVACIONES };

		return mDictionary.getDatosParada(parada, columns);
	}

	private Cursor refreshShortcut(Uri uri) {
		/*
		 * This won't be called with the current implementation, but if we
		 * include {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column
		 * in our suggestions table, we could expect to receive refresh queries
		 * when a shortcutted suggestion is displayed in Quick Search Box. In
		 * which case, this method will query the table for the specific word,
		 * using the given item Uri and provide all the columns originally
		 * provided with the suggestion query.
		 */
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] { BaseColumns._ID, DatosLineasDB.KEY_WORD, DatosLineasDB.KEY_DEFINITION, SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };

		return mDictionary.getWord(rowId, columns);
	}

	/**
	 * This method is required in order to query the supported types. It's also
	 * useful in our own query() method to determine the type of Uri received.
	 */
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_WORDS:
			return WORDS_MIME_TYPE;
		case GET_WORD:
			return DEFINITION_MIME_TYPE;
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
		case REFRESH_SHORTCUT:
			return SearchManager.SHORTCUT_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	// Other required implementations...

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		Log.d("DATOS", "Recarga manual de la base de datos");

		mDictionary.recargaManual();

		return 0;

		// throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		Log.d("DATOS", "Actualizacion remota de la base de datos");

		mDictionary.actualizarDescarga();

		return 0;

		// throw new UnsupportedOperationException();
	}

}
