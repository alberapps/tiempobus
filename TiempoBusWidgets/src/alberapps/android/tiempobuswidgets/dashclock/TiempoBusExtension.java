/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
 * 
 *  based on code by Dashclock example extension Copyright (C) 2013 Google Inc (Roman Nurik) 
 *  (DashClock license: http://www.apache.org/licenses/LICENSE-2.0)
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
package alberapps.android.tiempobuswidgets.dashclock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import alberapps.android.tiempobuswidgets.R;
import alberapps.android.tiempobuswidgets.tasks.LoadTiemposLineaParadaAsyncTask;
import alberapps.android.tiempobuswidgets.tasks.LoadTiemposLineaParadaAsyncTask.LoadTiemposLineaParadaAsyncTaskResponder;
import alberapps.java.datos.Datos;
import alberapps.java.datos.GestionarDatos;
import alberapps.java.tam.BusLlegada;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class TiempoBusExtension extends DashClockExtension {
	private static final String TAG = "TiempoBusExtension";

	public static final String PREF_NAME = "pref_name";

	List<BusLlegada> listaTiempos = null;

	@Override
	protected void onUpdateData(int reason) {
		// Get preference value.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		String name = sp.getString(PREF_NAME, getString(R.string.pref_name_default));

		// Iniciar actualizacion de los datos
		actualizar();

		// Para que actualice al desbloquear la pantalla
		setUpdateWhenScreenOn(true);
	}

	/**
	 * Pulicar datos
	 */
	private void publicar() {

		// Intent para iniciar tiempobus al hacer click
		Intent intentTiempoBus = intentTiempoBus();

		ExtensionData extData = null;
		
		if(listaTiempos != null && !listaTiempos.isEmpty()){
		
			extData = new ExtensionData().visible(true).icon(R.drawable.tiempobus_dashclock).status(getStatus()).expandedTitle(getTitle()).expandedBody(getBody())
				.contentDescription("Información de tiempos de paso.");

		}else{
			
			extData = new ExtensionData().visible(true).icon(R.drawable.tiempobus_dashclock).status("No info").expandedTitle(getTitle()).expandedBody("Sin datos")
					.contentDescription("Información de tiempos de paso.");

			
		}
		
		if (intentTiempoBus != null) {

			extData.clickIntent(intentTiempoBus);

		}

		// Publish the extension data update.
		publishUpdate(extData);

	}

	/**
	 * Formatear tiempos para mostrarlos
	 * 
	 * @return tiempos
	 */
	private String getBody() {

		StringBuffer body = new StringBuffer();

		if (listaTiempos != null && !listaTiempos.isEmpty()) {

			for (int i = 0; i < listaTiempos.size(); i++) {

				body.append(listaTiempos.get(i).getParada());
				body.append("-");
				body.append(listaTiempos.get(i).getLinea());
				body.append(": ");
				body.append(listaTiempos.get(i).getProximoMinutos());
				body.append(" min - ");
				body.append(listaTiempos.get(i).getSiguienteMinutos());
				body.append(" min");

				body.append("\n");

			}

		}

		if (body.length() == 0) {
			body.append("Sin información");
		}

		return body.toString();

	}

	/**
	 * Recuperar el status
	 * 
	 * Por ahora es el tiempo del primer bus disponible
	 * 
	 * @return tiempo
	 */
	private String getStatus() {

		String status = "";

		if (listaTiempos != null && !listaTiempos.isEmpty()) {

			status = listaTiempos.get(0).getProximoMinutos() + " min";

		}

		return status;

	}

	/**
	 * Contruir el title. Informacion de la hora
	 * 
	 * @return
	 */
	private String getTitle() {

		String title = "";

		final Calendar c = Calendar.getInstance();

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		String updated = df.format(c.getTime()).toString();

		title = "Tiempos " + updated;

		return title;

	}

	/**
	 * Actualizar el contenido
	 * 
	 * @param context
	 * @param intent
	 */
	public void actualizar() {

		final SharedPreferences preferencias = getSharedPreferences("datoswidget", Context.MODE_MULTI_PROCESS);

		listaTiempos = new ArrayList<BusLlegada>();

		LoadTiemposLineaParadaAsyncTaskResponder loadTiemposLineaParadaAsyncTaskResponder = new LoadTiemposLineaParadaAsyncTaskResponder() {
			public void tiemposLoaded(List<BusLlegada> tiempos) {

				boolean sinParadas = false;

				if (preferencias.getString("lineas_parada", "").equals("")) {
					sinParadas = true;
				}

				if (tiempos != null || sinParadas) {

					listaTiempos = tiempos;

				} else {

					if (!sinParadas) {

						// Toast.makeText(context,
						// context.getString(R.string.error_tiempos),
						// Toast.LENGTH_LONG).show();

					}

				}

				if (sinParadas) {

				}

				publicar();

			}

		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			List<Datos> lineasParada = GestionarDatos.listaDatos(preferencias.getString("lineas_parada", ""));

			new LoadTiemposLineaParadaAsyncTask(loadTiemposLineaParadaAsyncTaskResponder).execute(lineasParada);

		} else {

		}

	}

	/**
	 * Crear intent para iniciar tiempobus
	 * 
	 * @return intent
	 */
	public Intent intentTiempoBus() {

		PackageManager manager = getPackageManager();

		List<ApplicationInfo> packages = manager.getInstalledApplications(0);

		for (int i = 0; i < packages.size(); i++) {
			if (packages.get(i).packageName.equals("alberapps.android.tiempobus")) {

				Intent intent = manager.getLaunchIntentForPackage("alberapps.android.tiempobus");

				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				return intent;

			}
		}

		return null;

	}

}
