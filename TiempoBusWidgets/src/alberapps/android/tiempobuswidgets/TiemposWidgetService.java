/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
			destino = mCursor.getString(destinoColIndex);
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
