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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import alberapps.android.tiempobuswidgets.tasks.LoadTiemposLineaParadaAsyncTask;
import alberapps.android.tiempobuswidgets.tasks.LoadTiemposLineaParadaAsyncTask.LoadTiemposLineaParadaAsyncTaskResponder;
import alberapps.java.datos.Datos;
import alberapps.java.datos.GestionarDatos;
import alberapps.java.tam.BusLlegada;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Our data observer just notifies an update for all weather widgets when it
 * detects a change.
 */
class WeatherDataProviderObserver extends ContentObserver {
	private AppWidgetManager mAppWidgetManager;
	private ComponentName mComponentName;

	WeatherDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
		super(h);
		mAppWidgetManager = mgr;
		mComponentName = cn;
	}

	@Override
	public void onChange(boolean selfChange) {
		// The data has changed, so notify the widget that the collection view
		// needs to be updated.
		// In response, the factory's onDataSetChanged() will be called which
		// will requery the
		// cursor for the new data.
		mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.weather_list);
	}
}

/**
 * The weather widget's AppWidgetProvider.
 */
public class TiemposWidgetProvider extends AppWidgetProvider {
	public static String CLICK_ACTION = "alberapps.android.tiempobuswidgets.CLICK";
	public static String REFRESH_ACTION = "alberapps.android.tiempobuswidgets.REFRESH";
	public static String DATO_ID = "alberapps.android.tiempobuswidgets.dato";

	private static HandlerThread sWorkerThread;
	private static Handler sWorkerQueue;
	private static WeatherDataProviderObserver sDataObserver;
	private static final int sMaxDegrees = 96;

	private boolean mIsLargeLayout = true;
	private int mHeaderWeatherState = 0;

	private List<BusLlegada> listaTiempos;

	SharedPreferences preferencias = null;

	public TiemposWidgetProvider() {
		// Start the worker thread
		sWorkerThread = new HandlerThread("WeatherWidgetProvider-worker");
		sWorkerThread.start();
		sWorkerQueue = new Handler(sWorkerThread.getLooper());
	}

	// XXX: clear the worker queue if we are destroyed?

	@Override
	public void onEnabled(Context context) {
		// Register for external updates to the data to trigger an update of the
		// widget. When using
		// content providers, the data is often updated via a background
		// service, or in response to
		// user interaction in the main app. To ensure that the widget always
		// reflects the current
		// state of the data, we must listen for changes and update ourselves
		// accordingly.
		final ContentResolver r = context.getContentResolver();
		if (sDataObserver == null) {
			final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
			final ComponentName cn = new ComponentName(context, TiemposWidgetProvider.class);
			sDataObserver = new WeatherDataProviderObserver(mgr, cn, sWorkerQueue);
			r.registerContentObserver(TiemposDataProvider.CONTENT_URI, true, sDataObserver);
		}

		onReceive(context, new Intent().setAction(REFRESH_ACTION));

	}

	/**
	 * Lanza la subactivididad de preferencias
	 */
	private void showPreferencias(Context ctx) {

		final Context context = ctx;

		Intent i = new Intent(context, PreferencesFromXml.class);

		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(i);

	}

	@Override
	public void onReceive(Context ctx, final Intent intent) {
		final String action = intent.getAction();

		final Context context = ctx;

		
		
		if (action.equals(REFRESH_ACTION)) {

			// BroadcastReceivers have a limited amount of time to do work, so
			// for this sample, we
			// are triggering an update of the data on another thread. In
			// practice, this update
			// can be triggered from a background service, or perhaps as a
			// result of user actions
			// inside the main application.

			actualizar(context, intent);

		} else if (action.equals(CLICK_ACTION)) {
			// Show a toast
			final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			final int dato = intent.getIntExtra(DATO_ID, -1);

			Log.d("tag", " eliminar: " + dato);

			
			Intent i = new Intent(context, EliminarDatoActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("DATO", dato);			
			context.startActivity(i);
			
			
			
			
			

					

						//actualizar(context, intent);

					
			
			
			

			// showPreferencias(context);

		}

		super.onReceive(ctx, intent);
	}

	
	
	public void actualizar(final Context context, Intent intent) {

		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(context);

		listaTiempos = new ArrayList<BusLlegada>();

		LoadTiemposLineaParadaAsyncTaskResponder loadTiemposLineaParadaAsyncTaskResponder = new LoadTiemposLineaParadaAsyncTaskResponder() {
			public void tiemposLoaded(List<BusLlegada> tiempos) {

				listaTiempos = tiempos;

				Log.d("tag", " " + tiempos.get(0).getProximo());

				sWorkerQueue.removeMessages(0);
				sWorkerQueue.post(new Runnable() {
					@Override
					public void run() {
						final ContentResolver r = context.getContentResolver();
						// final Cursor c =
						// r.query(TiemposDataProvider.CONTENT_URI, null, null,
						// null, null);
						// final int count = c.getCount();

						// We disable the data changed observer temporarily
						// since
						// each of the updates
						// will trigger an onChange() in our data observer.
						
						try{
							r.unregisterContentObserver(sDataObserver);
						}catch(Exception e){
							
						}
						
						
						r.delete(TiemposDataProvider.CONTENT_URI, null, null);

						for (int i = 0; i < listaTiempos.size(); ++i) {

							final Uri uri = ContentUris.withAppendedId(TiemposDataProvider.CONTENT_URI, i);

							final ContentValues values = new ContentValues();

							// Linea
							values.put(TiemposDataProvider.Columns.LINEA, listaTiempos.get(i).getLinea());

							// Tiempo
							values.put(TiemposDataProvider.Columns.TIEMPO, listaTiempos.get(i).getProximo());

							// Destino
							values.put(TiemposDataProvider.Columns.DESTINO, listaTiempos.get(i).getDestino());

							// Parada
							values.put(TiemposDataProvider.Columns.PARADA, listaTiempos.get(i).getParada());

							// r.update(uri, values,
							// TiemposDataProvider.Columns.LINEA, new
							// String[]{listaTiempos.get(i).getLinea()});

							r.insert(uri, values);
						}
						r.registerContentObserver(TiemposDataProvider.CONTENT_URI, true, sDataObserver);

						final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
						final ComponentName cn = new ComponentName(context, TiemposWidgetProvider.class);
						mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.weather_list);
					}
				});

			}

		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			List<Datos> lineasParada = GestionarDatos.listaDatos(preferencias.getString("lineas_parada", "24,2902;10,2902"));

			new LoadTiemposLineaParadaAsyncTask(loadTiemposLineaParadaAsyncTaskResponder).execute(lineasParada);

		} else {
			Toast.makeText(context.getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
		}

		final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

		// Cambiar hora actualizacion
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		final Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		String updated = df.format(c.getTime()).toString();
		rv.setTextViewText(R.id.hora_act, updated);

		final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		final ComponentName cn = new ComponentName(context, TiemposWidgetProvider.class);
		mgr.updateAppWidget(cn, rv);

		Toast.makeText(context, "Actualiza", Toast.LENGTH_SHORT).show();

	}

	private RemoteViews buildLayout(Context context, int appWidgetId, boolean largeLayout) {
		RemoteViews rv;
		if (largeLayout) {
			// Specify the service to provide data for the collection widget.
			// Note that we need to
			// embed the appWidgetId via the data otherwise it will be ignored.
			final Intent intent = new Intent(context, TiemposWidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			rv.setRemoteAdapter(appWidgetId, R.id.weather_list, intent);

			// Set the empty view to be displayed if the collection is empty. It
			// must be a sibling
			// view of the collection view.
			rv.setEmptyView(R.id.weather_list, R.id.empty_view);

			// Bind a click listener template for the contents of the weather
			// list. Note that we
			// need to update the intent's data if we set an extra, since the
			// extras will be
			// ignored otherwise.
			final Intent onClickIntent = new Intent(context, TiemposWidgetProvider.class);
			onClickIntent.setAction(TiemposWidgetProvider.CLICK_ACTION);
			onClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			onClickIntent.setData(Uri.parse(onClickIntent.toUri(Intent.URI_INTENT_SCHEME)));
			final PendingIntent onClickPendingIntent = PendingIntent.getBroadcast(context, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.weather_list, onClickPendingIntent);

			// Bind the click intent for the refresh button on the widget
			final Intent refreshIntent = new Intent(context, TiemposWidgetProvider.class);
			refreshIntent.setAction(TiemposWidgetProvider.REFRESH_ACTION);
			final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setOnClickPendingIntent(R.id.refresh, refreshPendingIntent);

			// Restore the minimal header
			rv.setTextViewText(R.id.city_name, context.getString(R.string.app_name));

		} else {
			rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout_small);

			// Update the header to reflect the weather for "today"
			Cursor c = context.getContentResolver().query(TiemposDataProvider.CONTENT_URI, null, null, null, null);
			if (c.moveToPosition(0)) {
				int tempColIndex = c.getColumnIndex(TiemposDataProvider.Columns.TIEMPO);
				int temp = c.getInt(tempColIndex);
				String formatStr = context.getResources().getString(R.string.header_format_string);
				String header = String.format(formatStr, temp, "reduce");
				rv.setTextViewText(R.id.city_name, header);
			}
			c.close();
		}
		return rv;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// Update each of the widgets with the remote adapter
		for (int i = 0; i < appWidgetIds.length; ++i) {
			RemoteViews layout = buildLayout(context, appWidgetIds[i], mIsLargeLayout);
			appWidgetManager.updateAppWidget(appWidgetIds[i], layout);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

		int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
		int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
		int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
		int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

		RemoteViews layout;
		if (minHeight < 200) {
			mIsLargeLayout = false;
		} else {
			mIsLargeLayout = true;
		}
		layout = buildLayout(context, appWidgetId, mIsLargeLayout);
		appWidgetManager.updateAppWidget(appWidgetId, layout);
	}
}