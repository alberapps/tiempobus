/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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

import java.text.SimpleDateFormat;
import java.util.Date;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.tasks.ActualizarBDAsyncTask;
import alberapps.android.tiempobus.tasks.ActualizarBDAsyncTask.LoadActualizarBDAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadWeatherAsyncTask;
import alberapps.android.tiempobus.tasks.LoadWeatherAsyncTask.LoadWeatherAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadWikipediaAsyncTask;
import alberapps.android.tiempobus.tasks.LoadWikipediaAsyncTask.LoadWikipediaAsyncTaskResponder;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.util.Utilidades;
import alberapps.java.weather.EstadoCielo;
import alberapps.java.weather.WeatherQuery;
import alberapps.java.wikipedia.WikiQuery;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Gestion de la tarjeta de informacion
 * 
 * 
 */
public class GestionarTarjetaInfo {

	/**
	 * Cotexto principal
	 */
	private MainActivity context;

	private SharedPreferences preferencias;

	public GestionarTarjetaInfo(MainActivity contexto, SharedPreferences preferencia) {

		context = contexto;

		preferencias = preferencia;

	}

	private String paradaWiki = null;
	private String datosWiki = null;

	/**
	 * Cargar la informacion de la wikipedia para la parada
	 */
	public void cargarInfoWikipedia(String lat, String lon, final View v) {

		// Verificar si ya disponemos de los datos
		if (paradaWiki != null && datosWiki != null && paradaWiki.equals(Integer.toString(context.paradaActual))) {

			try {
				TextView textoWiki = (TextView) v.findViewById(R.id.datos_wiki);

				textoWiki.setText(Html.fromHtml(datosWiki));
				textoWiki.setMovementMethod(LinkMovementMethod.getInstance());

			} catch (Exception e) {
				e.printStackTrace();
			}

			return;

		} else {
			paradaWiki = Integer.toString(context.paradaActual);
			datosWiki = null;
		}

		LoadWikipediaAsyncTaskResponder loadWikipediaAsyncTaskResponder = new LoadWikipediaAsyncTaskResponder() {
			public void WikipediaLoaded(WikiQuery wiki) {

				if (wiki != null) {

					StringBuffer sb = new StringBuffer();

					// Preparar titulos
					for (int i = 0; i < wiki.getListaDatos().size(); i++) {

						if (sb.length() > 0) {
							sb.append(", ");
						}

						sb.append("<a href=\"http://");
						sb.append(UtilidadesUI.getIdiomaWiki());
						sb.append(".wikipedia.org/?curid=");
						sb.append(wiki.getListaDatos().get(i).getPageId());
						sb.append("\">");

						sb.append(wiki.getListaDatos().get(i).getTitle());
						sb.append("</a>");

					}

					// Cargar titulos en textView
					if (sb.length() > 0) {

						try {
							TextView textoWiki = (TextView) v.findViewById(R.id.datos_wiki);

							textoWiki.setText(Html.fromHtml(sb.toString()));
							textoWiki.setMovementMethod(LinkMovementMethod.getInstance());

							// Datos para siguiente pasada
							datosWiki = sb.toString();

						} catch (Exception e) {

						}

					}

				} else {

				}
			}

		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadWikipediaAsyncTask(loadWikipediaAsyncTaskResponder).execute(lat, lon);
		} else {
			Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
		}

	}

	private WeatherQuery datosWeather = null;

	/**
	 * Cargar la informacion de la wikipedia para la parada
	 */
	public void cargarInfoWeather(final View v) {

		final ImageView iv = (ImageView) v.findViewById(R.id.imageWeather);

		// Verificar si ya disponemos de los datos
		if (datosWeather != null) {

			try {

				mostrarElTiempo(datosWeather, iv, v);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return;

		} else {
			// datosWeather =;
			// datosWiki = null;
		}

		LoadWeatherAsyncTaskResponder loadWeatherAsyncTaskResponder = new LoadWeatherAsyncTaskResponder() {
			public void WeatherLoaded(WeatherQuery weather) {

				iv.setVisibility(ImageView.INVISIBLE);

				if (weather != null) {

					mostrarElTiempo(weather, iv, v);

				} else {

					iv.setVisibility(ImageView.INVISIBLE);

				}
			}

		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new LoadWeatherAsyncTask(loadWeatherAsyncTaskResponder).execute();
		} else {
			Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Mostrar el tiempo
	 * 
	 * @param weather
	 * @param iv
	 * @param v
	 */
	private void mostrarElTiempo(WeatherQuery weather, ImageView iv, View v) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < weather.getListaDatos().size(); i++) {

			for (int j = 0; j < weather.getListaDatos().get(i).getEstadoCielo().size(); j++) {

				if (weather.getListaDatos().get(i).getEstadoCielo().get(j).getPeriodo().equals(getPeriodoWheather())) {

					imgTiempo(weather.getListaDatos().get(i).getEstadoCielo().get(j), iv);

					sb.append("(");

					sb.append(weather.getListaDatos().get(i).getEstadoCielo().get(j).getDescripcion());

					sb.append(") ");

				}

			}

			sb.append(" min/máx: ");
			sb.append(weather.getListaDatos().get(i).getTempMinima());
			sb.append("/");
			sb.append(weather.getListaDatos().get(i).getTempMaxima());

		}

		// Cargar titulos en textView
		if (sb.length() > 0) {

			try {
				TextView textoWeather = (TextView) v.findViewById(R.id.textoWeather);

				textoWeather.setText(sb.toString());
				// textoWiki.setMovementMethod(LinkMovementMethod.getInstance());

				// Datos para siguiente pasada
				datosWeather = weather;

			} catch (Exception e) {

			}

		}

	}

	/**
	 * Periodo para tiempo
	 * 
	 * @return periodo
	 */
	private String getPeriodoWheather() {

		SimpleDateFormat ft = new SimpleDateFormat("HH");

		int horaT = Integer.parseInt(ft.format(new Date()));

		if (horaT > 0 && horaT < 6) {
			return "00-06";
		} else if (horaT >= 6 && horaT < 12) {
			return "06-12";
		} else if (horaT >= 12 && horaT < 18) {
			return "12-18";
		} else if (horaT >= 18) {
			return "18-24";
		} else {
			return null;
		}

	}

	/**
	 * Cargar imagen estado del tiempo
	 * 
	 * @param data
	 * @param iv
	 */
	private void imgTiempo(EstadoCielo data, ImageView iv) {

		/*
		 * 11: sol
		 * 
		 * 
		 * 12: nube-sol 13: nube-sol+ 14: nube-sol++ 17: niebla
		 * 
		 * 
		 * 15: nubes
		 * 
		 * 
		 * 43: lluvia suave 44: lluvia suave+ 45: lluvia suave++ 46: lluvia
		 * suave+++ 23: intervalos nubosos lluvia 25: muy nuboso lluvia 26:
		 * nuboso lluvia+
		 * 
		 * 71: nieve 72: nieve+ 73: nieve++ 33: intervalos nubosos nieve 34:
		 * intervalos nubosos nieve+ 35: nuboso con nieve 36: nuboso con nieve+
		 * 
		 * 52: tormenta 53: tormenta+ 54: tormenta++ 62: nuboso tormenta 63:
		 * nuboso tormenta+ 64: nuboso tormenta++
		 */

		if (data.getValor().substring(0, 2).equals("11")) {
			iv.setImageResource(R.drawable.weather_sun_blue_48);
		} else if (data.getValor().substring(0, 2).equals("15")) {
			iv.setImageResource(R.drawable.weather_clouds_blue_48);
		} else if (data.getValor().substring(0, 1).equals("1")) {
			iv.setImageResource(R.drawable.weather_cloudy_blue_48);
		} else if (data.getValor().substring(0, 1).equals("4") || data.getValor().substring(0, 1).equals("2")) {
			iv.setImageResource(R.drawable.weather_rain_blue_48);
		} else if (data.getValor().substring(0, 1).equals("7") || data.getValor().substring(0, 1).equals("3")) {
			iv.setImageResource(R.drawable.weather_snow_blue_48);
		} else if (data.getValor().substring(0, 1).equals("5") || data.getValor().substring(0, 1).equals("6")) {
			iv.setImageResource(R.drawable.weather_thunder_blue_48);
		} else {
			iv.setVisibility(ImageView.INVISIBLE);
			return;
		}

		iv.setVisibility(ImageView.VISIBLE);

	}

	/**
	 * Control de estado de actualizaciones
	 * 
	 * @param tw
	 */
	public void controlActualizarDB(final TextView tw) {

		LoadActualizarBDAsyncTaskResponder loadActualizarBDAsyncTaskResponder = new LoadActualizarBDAsyncTaskResponder() {
			public void ActualizarBDLoaded(final String respuesta) {

				boolean mostrarAviso = false;

				if (respuesta != null && !respuesta.equals("false")) {

					Log.d("ACTUALIZA", "respuesta: " + respuesta);

					// 01012014

					if (respuesta.length() == 8) {

						// Si es la primera actualizacion
						if (PreferencesUtil.getUpdateInfo(context).equals("")) {

							// Si es mas actual que la instalada
							if (Utilidades.isFechaControl(respuesta, DatosLineasDB.DATABASE_VERSION_FECHA)) {
								Log.d("ACTUALIZA", "aviso comparado copia local");
								mostrarAviso = true;
							} else {

								Log.d("ACTUALIZA", "aviso comparado copia local - no actualizar");
								mostrarAviso = false;

							}

						} else if (!PreferencesUtil.getUpdateInfo(context).equals("")) {

							String control = PreferencesUtil.getUpdateInfo(context);
							String ignorar = PreferencesUtil.getUpdateIgnorarInfo(context);

							// Si opcion de ignorar
							if (!ignorar.equals("")) {

								if (control.equals(ignorar)) {
									mostrarAviso = false;

									Log.d("ACTUALIZA", "ingnorar");

								} else {

									// Si la actualizacion es posterior a la
									// actual
									if (Utilidades.isFechaControl(respuesta, control)) {

										mostrarAviso = true;

										Log.d("ACTUALIZA", "mostrar");

									}

								}

							}

						}

					}

				} else {

					mostrarAviso = false;

				}

				if (mostrarAviso) {

					final CharSequence textoAnterior = tw.getText();

					tw.setText(tw.getText() + "\n" + context.getString(R.string.actualizacion_aviso));

					tw.setOnClickListener(new TextView.OnClickListener() {
						public void onClick(View arg0) {

							modalActualizar(respuesta, textoAnterior, tw);

						}
					});

				}

			}

		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			new ActualizarBDAsyncTask(loadActualizarBDAsyncTaskResponder).execute(true);
		} else {

		}

	}

	/**
	 * Modal de confirmacion de actualizar
	 * 
	 * @param respuesta
	 * @param textoAnterior
	 * @param tw
	 */
	private void modalActualizar(final String respuesta, final CharSequence textoAnterior, final TextView tw) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);

		dialog.setTitle(context.getString(R.string.actualizacion_titulo));

		dialog.setMessage(context.getString(R.string.actualizacion_desc));
		dialog.setIcon(R.drawable.ic_tiempobus_3);

		dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				// PreferencesUtil.putUpdateInfo(context, respuesta, "");

				tw.setText(textoAnterior);
				tw.setOnClickListener(new TextView.OnClickListener() {
					public void onClick(View arg0) {

					}
				});
				actualizarDB(respuesta);

				dialog.dismiss();

			}

		});

		dialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				PreferencesUtil.putUpdateInfo(context, respuesta, respuesta);
				tw.setText(textoAnterior);
				tw.setOnClickListener(new TextView.OnClickListener() {
					public void onClick(View arg0) {

					}
				});

				dialog.dismiss();

			}

		});

		dialog.show();

	}

	/**
	 * Actualizar la base de datos
	 * 
	 * @param respuesta
	 */
	public void actualizarDB(String respuesta) {

		final Builder mBuilder = Notificaciones.notificacionBaseDatos(context.getApplicationContext(), Notificaciones.NOTIFICACION_BD_INICIAL, null, null);

		LoadActualizarBDAsyncTaskResponder loadActualizarBDAsyncTaskResponder = new LoadActualizarBDAsyncTaskResponder() {
			public void ActualizarBDLoaded(String respuesta) {

				if (respuesta.equals("true")) {
					context.getContentResolver().update(BuscadorLineasProvider.CONTENT_URI, null, null, null);

					PreferencesUtil.putUpdateInfo(context, respuesta, "");

				} else {
					Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_descarga_actualizacion), Toast.LENGTH_SHORT).show();

					Notificaciones.notificacionBaseDatos(context.getApplicationContext(), Notificaciones.NOTIFICACION_BD_ERROR, mBuilder, null);
				}

			}

		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			new ActualizarBDAsyncTask(loadActualizarBDAsyncTaskResponder).execute();
		} else {
			Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();

			Notificaciones.notificacionBaseDatos(context.getApplicationContext(), Notificaciones.NOTIFICACION_BD_ERROR, mBuilder, null);

		}

	}

}
