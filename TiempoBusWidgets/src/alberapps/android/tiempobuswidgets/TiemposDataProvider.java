/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 *
 *  based on the Copyright (C) 2011 The Android Open Source Project
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
package alberapps.android.tiempobuswidgets;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

/**
 * A dummy class that we are going to use internally to store weather data.
 * Generally, this data will be stored in an external and persistent location
 * (ie. File, Database, SharedPreferences) so that the data can persist if the
 * process is ever killed. For simplicity, in this sample the data will only be
 * stored in memory.
 */
class TiemposDataPoint {
	String linea;
	String tiempo;
	String destino;
	String parada;

	TiemposDataPoint(String lineaP, String tiempoP, String destinoP, String paradaP) {
		linea = lineaP;
		tiempo = tiempoP;
		destino = destinoP;
		parada = paradaP;
	}
}

/**
 * The AppWidgetProvider for our sample weather widget.
 */
public class TiemposDataProvider extends ContentProvider {
	public static final Uri CONTENT_URI = Uri.parse("content://alberapps.android.tiempobuswidgets.provider");

	public static class Columns {
		public static final String ID = "_id";
		public static final String LINEA = "linea";
		public static final String TIEMPO = "tiempo";
		public static final String DESTINO = "destino";
		public static final String PARADA = "parada";
	}

	/**
	 * Generally, this data will be stored in an external and persistent
	 * location (ie. File, Database, SharedPreferences) so that the data can
	 * persist if the process is ever killed. For simplicity, in this sample the
	 * data will only be stored in memory.
	 */
	private static final ArrayList<TiemposDataPoint> sData = new ArrayList<TiemposDataPoint>();

	@Override
	public boolean onCreate() {
		// We are going to initialize the data provider with some default values
		// sData.add(new TiemposDataPoint("", "",""));

		return true;
	}

	@Override
	public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		assert (uri.getPathSegments().isEmpty());

		// In this sample, we only query without any parameters, so we can just
		// return a cursor to
		// all the weather data.
		final MatrixCursor c = new MatrixCursor(new String[] { Columns.ID, Columns.LINEA, Columns.TIEMPO, Columns.DESTINO, Columns.PARADA });
		for (int i = 0; i < sData.size(); ++i) {
			final TiemposDataPoint data = sData.get(i);
			c.addRow(new Object[] { new Integer(i), data.linea, data.tiempo, data.destino, data.parada });
		}
		return c;
	}

	@Override
	public String getType(Uri uri) {
		return "vnd.android.cursor.dir/vnd.tiempobuswidgets.tiempos";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		final TiemposDataPoint data = new TiemposDataPoint(values.getAsString(Columns.LINEA), values.getAsString(Columns.TIEMPO), values.getAsString(Columns.DESTINO), values.getAsString(Columns.PARADA));

		sData.add(data);

		Uri noteUri = ContentUris.withAppendedId(uri, sData.size());
		getContext().getContentResolver().notifyChange(noteUri, null);
		return noteUri;

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		sData.clear();

		getContext().getContentResolver().notifyChange(uri, null);

		return 1;
	}

	@Override
	public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		assert (uri.getPathSegments().size() == 1);

		// In this sample, we only update the content provider individually for
		// each row with new
		// temperature values.
		final int index = Integer.parseInt(uri.getPathSegments().get(0));
		final MatrixCursor c = new MatrixCursor(new String[] { Columns.ID, Columns.LINEA, Columns.TIEMPO, Columns.DESTINO, Columns.PARADA });
		assert (0 <= index && index < sData.size());
		final TiemposDataPoint data = sData.get(index);
		data.tiempo = values.getAsString(Columns.TIEMPO);
		data.linea = values.getAsString(Columns.LINEA);
		data.destino = values.getAsString(Columns.DESTINO);
		data.parada = values.getAsString(Columns.PARADA);

		// Notify any listeners that the data backing the content provider has
		// changed, and return
		// the number of rows affected.
		getContext().getContentResolver().notifyChange(uri, null);
		return 1;
	}

}
