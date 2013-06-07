/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
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
package alberapps.android.tiempobus.principal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.data.TiempoBusDb;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;
import alberapps.android.tiempobus.favoritos.FavoritosActivity;
import alberapps.android.tiempobus.tasks.LoadNoticiasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadNoticiasAsyncTask.LoadNoticiasAsyncTaskResponder;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tam.noticias.Noticias;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Gestion de tiempos principal
 * 
 * 
 */
public class DatosPantallaPrincipal {

	/**
	 * Cotexto principal
	 */
	private MainActivity context;

	private SharedPreferences preferencias;

	public DatosPantallaPrincipal(MainActivity contexto, SharedPreferences preferencia) {

		context = contexto;

		preferencias = preferencia;

	}

	/**
	 * Modal con informacion de la parada
	 */
	public void cargarModalInfo(int parada) {

		try {

			String parametros[] = { Integer.toString(parada) };

			Cursor cursor = context.managedQuery(BuscadorLineasProvider.DATOS_PARADA_URI, null, null, parametros, null);

			if (cursor != null) {
				List<Parada> listaParadas = new ArrayList<Parada>();

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

					Parada par = new Parada();

					par.setLineaNum(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
					par.setLineaDesc(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
					par.setConexion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
					par.setCoordenadas(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
					par.setDestino(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
					par.setDireccion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
					par.setLatitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
					par.setLongitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
					par.setParada(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));

					par.setObservaciones(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_OBSERVACIONES)));

					listaParadas.add(par);
				}

				String descripcionAlert = context.getString(R.string.localizacion) + ": " + listaParadas.get(0).getDireccion() + "\n" + context.getString(R.string.lineas) + " ";

				if (listaParadas.get(0).getConexion() != null) {
					descripcionAlert += listaParadas.get(0).getConexion().trim();
				}

				descripcionAlert += "\n" + context.getString(R.string.observaciones);

				// Observaciones
				for (int i = 0; i < listaParadas.size(); i++) {

					if (listaParadas.get(i).getObservaciones() != null && !listaParadas.get(i).getObservaciones().trim().equals("")) {
						descripcionAlert += "\n[" + listaParadas.get(i).getLineaNum() + "] " + listaParadas.get(i).getObservaciones().trim() + "\n";
					}

				}

				AlertDialog.Builder dialog = new AlertDialog.Builder(context);

				dialog.setTitle(context.getString(R.string.share_0b) + " " + listaParadas.get(0).getParada());
				dialog.setMessage(descripcionAlert);
				dialog.setIcon(R.drawable.ic_info_modal);

				dialog.show();

			} else {

				Toast.makeText(context, context.getString(R.string.error_generico_1), Toast.LENGTH_SHORT).show();

			}

		} catch (Exception e) {
			Toast.makeText(context, context.getString(R.string.error_generico_1), Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * 
	 * @param paradaActual
	 * @return
	 */
	public String cargarDescripcionBD(int paradaActual) {

		try {

			String parametros[] = { Integer.toString(paradaActual) };

			Cursor cursor = context.managedQuery(BuscadorLineasProvider.DATOS_PARADA_URI, null, null, parametros, null);

			if (cursor != null) {
				List<Parada> listaParadas = new ArrayList<Parada>();

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

					Parada par = new Parada();

					par.setLineaNum(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
					par.setLineaDesc(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
					par.setConexion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
					par.setCoordenadas(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
					par.setDestino(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
					par.setDireccion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
					par.setLatitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
					par.setLongitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
					par.setParada(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));

					listaParadas.add(par);
				}

				return listaParadas.get(0).getDireccion();

			} else {
				return "";

			}

		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * Si la parada esta en favoritos mostramos su titulo
	 * 
	 * @param parada
	 * @return
	 */
	public String cargarDescripcion(String parada) {

		try {
			HashMap<String, String> datosFav = new HashMap<String, String>();

			Cursor cursor = context.managedQuery(TiempoBusDb.Favoritos.CONTENT_URI, FavoritosActivity.PROJECTION, null, null, TiempoBusDb.Favoritos.DEFAULT_SORT_ORDER);

			if (cursor != null) {

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					datosFav.put(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.POSTE)), cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.TITULO)));
				}

			}

			if (datosFav.containsKey(parada)) {

				return datosFav.get(parada);

			} else {
				return "";
			}

		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * Verifica si hay nuevas noticias y muestra un aviso
	 * 
	 */
	public void verificarNuevasNoticias() {

		/**
		 * Sera llamado cuando la tarea de cargar las noticias
		 */
		LoadNoticiasAsyncTaskResponder loadNoticiasAsyncTaskResponder = new LoadNoticiasAsyncTaskResponder() {
			public void noticiasLoaded(List<Noticias> noticias) {

				if (noticias != null && !noticias.isEmpty()) {

					String fecha_ultima = "";
					boolean lanzarAviso = false;

					// Ver si se guardo la fecha de la ultima noticia
					if (preferencias.contains("ultima_noticia")) {
						fecha_ultima = preferencias.getString("ultima_noticia", "");

						if (!fecha_ultima.equals(noticias.get(0).getFecha())) {

							lanzarAviso = true;

							SharedPreferences.Editor editor = preferencias.edit();
							editor.putString("ultima_noticia", noticias.get(0).getFecha());
							editor.commit();

						}

					} else {

						SharedPreferences.Editor editor = preferencias.edit();
						editor.putString("ultima_noticia", noticias.get(0).getFecha());
						editor.commit();

					}

					// Si se guardo la fecha y no coincide con la ultima, lanzar
					// aviso
					if (lanzarAviso) {

						Notificaciones.notificacionNoticias(context.getApplicationContext());

					}
				} else {

				}
			}
		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadNoticiasAsyncTask(loadNoticiasAsyncTaskResponder).execute();
		} else {
			Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
		}

	}

	public boolean esTram(int paradaActual) {

		if (Integer.toString(paradaActual).length() < 4) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Formatea la salida por idioma
	 * 
	 * @param proximo
	 * @return
	 */
	public String formatearShare(String proximo) {

		String traducido = "";

		String[] procesa = proximo.split(";");

		String tiempo1 = "";
		String tiempo2 = "";

		//Si es tram devuelve solo un dato
		if (procesa[0].equals("TRAM")) {
			return procesa[1];
		}

		if (procesa[0].equals("enlaparada")) {

			tiempo1 = (String) context.getResources().getText(R.string.tiempo_m_1);

		} else if (procesa[0].equals("sinestimacion")) {

			tiempo1 = (String) context.getResources().getText(R.string.tiempo_m_2);

		} else {

			tiempo1 = procesa[0];

		}

		if (procesa[1].equals("enlaparada")) {

			tiempo2 = (String) context.getResources().getText(R.string.tiempo_m_1);

		} else if (procesa[1].equals("sinestimacion")) {

			tiempo2 = (String) context.getResources().getText(R.string.tiempo_m_2);

		} else {

			tiempo2 = procesa[1];

		}

		traducido = tiempo1 + " " + context.getResources().getText(R.string.tiempo_m_3) + " " + tiempo2;

		return traducido;

	}

	/**
	 * Compartir informacion del bus
	 */
	public void shareBus(BusLlegada busSeleccionado, int paradaActual) {

		// String devuelto

		String mensaje = context.getResources().getText(R.string.share_0) + " " + context.getResources().getText(R.string.share_0b) + " " + paradaActual + " " + context.getResources().getText(R.string.share_1) + " "
				+ busSeleccionado.getLinea() + " " + context.getResources().getText(R.string.share_2) + " " + busSeleccionado.getDestino() + " " + context.getResources().getText(R.string.share_3) + " "
				+ formatearShare(busSeleccionado.getProximo());

		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
		sendIntent.setType("text/plain");
		context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.menu_share)));

	}

}
