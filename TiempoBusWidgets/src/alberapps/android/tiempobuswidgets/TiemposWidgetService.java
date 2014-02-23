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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * This is the service that provides the factory to be bound to the collection
 * service.
 */
public class TiemposWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
	}
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	private Context mContext;
	private Cursor mCursor;
	private int mAppWidgetId;

	public StackRemoteViewsFactory(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	public void onCreate() {
		// Since we reload the cursor in onDataSetChanged() which gets called
		// immediately after
		// onCreate(), we do nothing here.
	}

	public void onDestroy() {
		if (mCursor != null) {
			mCursor.close();
		}
	}

	public int getCount() {
		return mCursor.getCount();
	}

	public RemoteViews getViewAt(int position) {
		// Get the data for this position from the content provider
		String linea = "";
		String tiempo = "";
		String destino = "";
		String parada = "";
		if (mCursor.moveToPosition(position)) {
			final int lineaColIndex = mCursor.getColumnIndex(TiemposDataProvider.Columns.LINEA);
			final int tiempoColIndex = mCursor.getColumnIndex(TiemposDataProvider.Columns.TIEMPO);
			final int destinoColIndex = mCursor.getColumnIndex(TiemposDataProvider.Columns.DESTINO);
			final int paradaColIndex = mCursor.getColumnIndex(TiemposDataProvider.Columns.PARADA);

			linea = mCursor.getString(lineaColIndex);
			tiempo = controlAviso(mCursor.getString(tiempoColIndex));
			
			// min.
			tiempo = tiempo.replaceAll("min.", mContext.getString(R.string.literal_min));
			
			destino = mCursor.getString(destinoColIndex);
			
			if(destino != null && !destino.equals("")){
				destino = ">> "+destino;
			}
			
			parada = mCursor.getString(paradaColIndex);
		}

		// Return a proper item with the proper day and temperature
		// final String formatStr =
		// mContext.getResources().getString(R.string.item_format_string);
		// final int itemId = R.layout.widget_item;
		// RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
		// rv.setTextViewText(R.id.widget_item, String.format(formatStr, temp,
		// day));

		final int itemId = R.layout.tiempos_item;
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
		rv.setTextViewText(R.id.bus_linea, linea);
		rv.setTextViewText(R.id.bus_destino, destino);
		rv.setTextViewText(R.id.bus_proximo, tiempo);
		rv.setTextViewText(R.id.bus_parada, parada);

		// Set the click intent so that we can handle it and show a toast
		// message
		final Intent fillInIntent = new Intent();
		final Bundle extras = new Bundle();
		extras.putInt(TiemposWidgetProvider.DATO_ID, position);
		fillInIntent.putExtras(extras);
		rv.setOnClickFillInIntent(R.id.t_item, fillInIntent);

		return rv;
	}

	/**
	 * Modificación para traducir por idioma
	 * 
	 * @param proximo
	 * @return
	 */
	private String controlAviso(String proximo) {

		String traducido = "";

		if (proximo != null && !proximo.equals("")) {

			String[] procesa = proximo.split(";");

			String tiempo1 = "";
			String tiempo2 = "";

			if (procesa[0].equals("enlaparada")) {

				tiempo1 = mContext.getString(R.string.tiempo_m_1);

			} else if (procesa[0].equals("sinestimacion")) {

				tiempo1 = mContext.getString(R.string.tiempo_m_2);

			} else {

				tiempo1 = procesa[0];

			}

			if (procesa[1].equals("enlaparada")) {

				tiempo2 = mContext.getString(R.string.tiempo_m_1);

			} else if (procesa[1].equals("sinestimacion")) {

				tiempo2 = mContext.getString(R.string.tiempo_m_2);

			} else {

				tiempo2 = procesa[1];

			}

			traducido = tiempo1 + " " + mContext.getString(R.string.tiempo_m_3) + " " + tiempo2;

		} else {

			// Sin informacion para mostrar

			traducido = mContext.getString(R.string.empty_view_text);

		}

		return traducido;

	}

	public RemoteViews getLoadingView() {
		// We aren't going to return a default loading view in this sample
		return null;
	}

	public int getViewTypeCount() {
		// Technically, we have two types of views (the dark and light
		// background views)
		return 2;
	}

	public long getItemId(int position) {
		return position;
	}

	public boolean hasStableIds() {
		return true;
	}

	public void onDataSetChanged() {
		// Refresh the cursor
		if (mCursor != null) {
			mCursor.close();
		}
		mCursor = mContext.getContentResolver().query(TiemposDataProvider.CONTENT_URI, null, null, null, null);
	}
}
