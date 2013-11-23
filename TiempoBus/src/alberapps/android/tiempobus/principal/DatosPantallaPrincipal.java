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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.appinfo.AppInfoActivity;
import alberapps.android.tiempobus.data.TiempoBusDb;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;
import alberapps.android.tiempobus.database.historial.HistorialDB;
import alberapps.android.tiempobus.favoritos.FavoritosActivity;
import alberapps.android.tiempobus.historial.HistorialActivity;
import alberapps.android.tiempobus.tasks.LoadNoticiasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadNoticiasAsyncTask.LoadNoticiasAsyncTaskResponder;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.Noticias;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.util.Utilidades;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

		FragmentSecundarioTablet detalleFrag = (FragmentSecundarioTablet) context.getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);

		if (detalleFrag != null && UtilidadesUI.pantallaTabletHorizontal(context)) {

			Log.d("Principal", "Actualizar fragmento secundario");

			detalleFrag.actualizarDatos();

		}

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
	 * Gestiona el historial
	 * 
	 * @param paradaActual
	 * 
	 */
	public void gestionarHistorial(int paradaActual) {

		try {

			// Consultar datos
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

				// Comprueba si ya existe la parada para sustituirla
				Integer id = cargarIdParadaHistorial(Integer.toString(paradaActual));

				// Almacenar historial
				ContentValues values = new ContentValues();

				Date fechaActual = new Date();

				values.put(HistorialDB.Historial.TITULO, Utilidades.getFechaString(fechaActual));

				StringBuffer descripcion = new StringBuffer("");

				if (listaParadas != null && !listaParadas.isEmpty() && listaParadas.get(0).getDireccion() != null) {
					descripcion.append(listaParadas.get(0).getDireccion());
					descripcion.append("\n");
					descripcion.append("T: ");
					descripcion.append(listaParadas.get(0).getConexion());
				}

				// Descripcion del favorito
				String favorito = cargarDescripcion(Integer.toString(paradaActual));

				if (favorito != null && !favorito.equals("")) {
					if (descripcion.length() > 1) {
						descripcion.append("\n");
					}
					descripcion.append("\"");
					descripcion.append(favorito);
					descripcion.append("\"");
				}

				values.put(HistorialDB.Historial.DESCRIPCION, descripcion.toString());

				values.put(HistorialDB.Historial.PARADA, paradaActual);
				values.put(HistorialDB.Historial.FECHA, Utilidades.getFechaSQL(fechaActual));

				if (id != null) {
					// La actualiza
					Uri miUriM = ContentUris.withAppendedId(HistorialDB.Historial.CONTENT_URI, id);

					context.getContentResolver().update(miUriM, values, null, null);

				} else {
					// Una nueva
					context.getContentResolver().insert(HistorialDB.Historial.CONTENT_URI, values);
				}

			} else {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Consultar si la parada ya esta en el historial
	 * 
	 * @param parada
	 * @return
	 */
	public Integer cargarIdParadaHistorial(String parada) {

		try {

			String parametros[] = { parada };

			Cursor cursor = context.managedQuery(HistorialDB.Historial.CONTENT_URI_ID_PARADA, HistorialActivity.PROJECTION, null, parametros, null);

			if (cursor != null) {

				cursor.moveToFirst();

				Log.d("HISTORIAL", "historial: " + cursor.getInt(cursor.getColumnIndex(HistorialDB.Historial._ID)));

				return cursor.getInt(cursor.getColumnIndex(HistorialDB.Historial._ID));

			} else {
				return null;
			}

		} catch (Exception e) {
			return null;
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

					int nuevas = 0;

					String fecha_ultima = "";
					boolean lanzarAviso = false;

					// Ver si se guardo la fecha de la ultima noticia
					if (preferencias.contains("ultima_noticia")) {
						fecha_ultima = preferencias.getString("ultima_noticia", "");

						if (fecha_ultima != null) {

							Date fechaUltima = Utilidades.getFechaDate(fecha_ultima);

							// Contar nuevas noticias

							for (int i = 0; i < noticias.size(); i++) {

								if (noticias.get(i).getFechaDate() != null) {
									if (noticias.get(i).getFechaDate().after(fechaUltima)) {
										nuevas++;
									}
								}

							}

						}

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

						// Extendido

						String[] extendido = new String[2];

						extendido[0] = noticias.get(0).getFecha() + ": " + noticias.get(0).getNoticia();

						if (noticias.size() > 1) {
							extendido[1] = noticias.get(1).getFecha() + ": " + noticias.get(1).getNoticia();
						} else {
							extendido[1] = "";
						}

						Notificaciones.notificacionNoticias(context.getApplicationContext(), extendido, nuevas);

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

		if (!UtilidadesTRAM.ACTIVADO_TRAM) {
			return false;
		}

		if (Integer.toString(paradaActual).length() < 4 || Integer.toString(paradaActual).charAt(0) == '1') {
			return true;
		} else {
			return false;
		}

	}

	public static boolean esTram(String paradaActual) {

		if (!UtilidadesTRAM.ACTIVADO_TRAM) {
			return false;
		}

		if (paradaActual.length() < 4 || paradaActual.charAt(0) == '1') {
			return true;
		} else {
			return false;
		}

	}

	public static boolean esLineaTram(String lineaActual) {

		if (!UtilidadesTRAM.ACTIVADO_TRAM) {
			return false;
		}

		if (UtilidadesTRAM.esLineaTram(lineaActual)) {
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

		// Si es tram devuelve solo un dato
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

	/**
	 * Frecuencia configurable
	 * 
	 * @return frecuencia
	 */
	public long frecuenciaRecarga() {

		String preFrec = preferencias.getString("tiempo_recarga", "60");

		long frecuencia = Long.parseLong(preFrec) * 1000;

		return frecuencia;

	}

	
	/**
	 * Cargar cabecera listado
	 */
	public void cargarHeader(){
		
		LayoutInflater li2 = LayoutInflater.from(context);
		
		View vheader = li2.inflate(R.layout.tiempos_aviso_header, null);
		
		context.tiemposView = (ListView) context.findViewById(R.id.lista_tiempos);

		context.tiemposView.addHeaderView(vheader);
		
	}
	
	
	/**
	 * Cargar pie listado
	 */
	public void cargarPie() {

		if(context.avisoPie != null && context.tiemposView != null){
			context.tiemposView.removeFooterView(context.avisoPie);
		}
		
		
		
		
		
		if (!esTram(context.paradaActual)) {
		
			
			LayoutInflater li = LayoutInflater.from(context);
			
			View v = li.inflate(R.layout.tiempos_aviso_3_bus, null);

			
			TextView infoapp = (TextView) v.findViewById(R.id.legal3);
			infoapp.setOnClickListener(new TextView.OnClickListener() {
				public void onClick(View arg0) {

					context.startActivity(new Intent(context, AppInfoActivity.class));

				}
			});
			
			context.tiemposView = (ListView) context.findViewById(R.id.lista_tiempos);

			context.tiemposView.addFooterView(v);
			
			context.avisoPie = v;

		} else {

			LayoutInflater li = LayoutInflater.from(context);
						
			
			View v = li.inflate(R.layout.tiempos_aviso_3, null);

			ImageView imgTram = (ImageView) v.findViewById(R.id.imgTram);
			imgTram.setOnClickListener(new TextView.OnClickListener() {
				public void onClick(View arg0) {

					Uri uri = Uri.parse("http://www.tramalicante.es");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);

				}
			});

			ImageView imgFgv = (ImageView) v.findViewById(R.id.imgFgv);
			imgFgv.setOnClickListener(new TextView.OnClickListener() {
				public void onClick(View arg0) {

					Uri uri = Uri.parse("http://www.fgv.es");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);

				}
			});

			TextView infoapp = (TextView) v.findViewById(R.id.legal3);
			infoapp.setOnClickListener(new TextView.OnClickListener() {
				public void onClick(View arg0) {

					context.startActivity(new Intent(context, AppInfoActivity.class));

				}
			});

			context.tiemposView = (ListView) context.findViewById(R.id.lista_tiempos);

			context.tiemposView.addFooterView(v);

			context.avisoPie = v;
			
		}

	}

}
