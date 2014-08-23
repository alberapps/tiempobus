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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.tasks.ActualizarBDAsyncTask;
import alberapps.android.tiempobus.tasks.ActualizarBDAsyncTask.LoadActualizarBDAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadLocationAsyncTask;
import alberapps.android.tiempobus.tasks.LoadWeatherAsyncTask;
import alberapps.android.tiempobus.tasks.LoadWeatherAsyncTask.LoadWeatherAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadWikipediaAsyncTask;
import alberapps.android.tiempobus.tasks.LoadWikipediaAsyncTask.LoadWikipediaAsyncTaskResponder;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.localizacion.Localizacion;
import alberapps.java.util.Utilidades;
import alberapps.java.weather.EstadoCielo;
import alberapps.java.weather.WeatherQuery;
import alberapps.java.wikipedia.WikiQuery;

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
    private String datosLocalizacion = null;
    private String paradaLocaliza = null;

	AsyncTask<Object, Void, WeatherQuery> weatherTask = null;
	AsyncTask<Object, Void, String> actualizarTask = null;
	AsyncTask<Object, Void, String> actualizarNumTask = null;
	AsyncTask<Object, Void, WikiQuery> wikiTask = null;
    AsyncTask<Object, Void, Localizacion> localizacionTask = null;

	/**
	 * Tarjeta con informacion de la parada
	 */
	public void cargarTarjetaInfo() {

		View v = null;

		boolean tablet = false;

		if (context.avisoTarjetaInfo != null && context.tiemposView != null) {
			context.tiemposView.removeFooterView(context.avisoTarjetaInfo);
		}

		// Si es una tablet en horizontal
		FragmentSecundarioTablet detalleFrag = (FragmentSecundarioTablet) context.getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);

		if (detalleFrag != null && UtilidadesUI.pantallaTabletHorizontal(context)) {

			v = detalleFrag.getView().findViewById(R.id.contenedor_secundario);

			tablet = true;

		} else {

			tablet = false;

			LayoutInflater li = LayoutInflater.from(context);

			v = li.inflate(R.layout.tiempos_tarjeta_info_2, null);

		}

		String parametros[] = { Integer.toString(context.paradaActual) };

		try {

			Cursor cursor = context.managedQuery(BuscadorLineasProvider.DATOS_PARADA_URI, null, null, parametros, null);

			if (cursor == null) {

				return;

			} else {

				StringBuffer observaciones = new StringBuffer();

				// Observaciones
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					int observacionesIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_OBSERVACIONES);
					int numLineaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LINEA_NUM);

					String observa = cursor.getString(observacionesIndex);
					String linea = cursor.getString(numLineaIndex);

					if (observa != null && !observa.trim().equals("")) {

						if (observaciones.length() > 0) {
							observaciones.append(", ");
						}

						observaciones.append("(");
						observaciones.append(linea);
						observaciones.append(") ");
						observaciones.append(observa);
					}

				}

				// Primera posicion
				cursor.moveToFirst();

				int paradaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_PARADA);
				int lineaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LINEA_DESC);
				int direccionIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_DIRECCION);
				int conexionesIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_CONEXION);
				int destinoIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_DESTINO);

				int numLineaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LINEA_NUM);

				int observacionesIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_OBSERVACIONES);

				int latIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LATITUD);
				int lonIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LONGITUD);

				//if (!tablet) {

					TextView parada = (TextView) v.findViewById(R.id.parada);
					TextView localizacion = (TextView) v.findViewById(R.id.localizacion);

					TextView datosParada = (TextView) v.findViewById(R.id.datos_parada);

					parada.setText(cursor.getString(paradaIndex));

					localizacion.setText(cursor.getString(direccionIndex));

					datosParada.setText("T: ".concat(cursor.getString(conexionesIndex)));

					String observa = observaciones.toString();

					if (observa != null && !observa.trim().equals("")) {

						datosParada.setText(datosParada.getText() + "\ni: " + observa);

					}

				//}

				String lat = cursor.getString(latIndex);
				String lon = cursor.getString(lonIndex);


                final View vista = v;


                LoadLocationAsyncTask.LoadLocationAsyncTaskResponder loadLocationAsyncTaskResponder = new LoadLocationAsyncTask.LoadLocationAsyncTaskResponder() {
                    public void LocationLoaded(Localizacion localizacion) {

                        if (localizacion != null) {

                            StringBuffer sb = new StringBuffer();


                            sb.append(localizacion.getDireccion());
                            sb.append(", ");
                            sb.append(localizacion.getLocalidad());


                            // Cargar titulos en textView
                            if (sb.length() > 0) {

                                try {
                                    TextView textoLocation = (TextView) vista.findViewById(R.id.datos_location);

                                    textoLocation.setText(sb.toString());

                                    // Datos para siguiente pasada
                                    datosLocalizacion = sb.toString();

                                } catch (Exception e) {

                                }

                            } else {

                                sb.append(context.getString(R.string.main_no_items));

                                TextView textoLocaliza = (TextView) vista.findViewById(R.id.datos_location);

                                textoLocaliza.setText(sb.toString());

                            }

                        } else {

                            TextView textoLocation = (TextView) vista.findViewById(R.id.datos_location);
                            textoLocation.setText("");

                        }
                    }

                };

                //Para usar datos anteriores si no ha cambiado
                if (paradaLocaliza != null && datosLocalizacion != null && paradaLocaliza.equals(Integer.toString(context.paradaActual))) {


                    TextView textoLocation = (TextView) v.findViewById(R.id.datos_location);

                    textoLocation.setText(datosLocalizacion);



                }else {
                    paradaLocaliza = Integer.toString(context.paradaActual);
                    datosLocalizacion = null;
                }

                //Cargar datos si no los tenemos
                if(datosLocalizacion == null){

                    // Control de disponibilidad de conexion
                    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {

                        TextView textoLocation = (TextView) v.findViewById(R.id.datos_location);
                        textoLocation.setText(context.getString(R.string.aviso_recarga));

                        localizacionTask = new LoadLocationAsyncTask(loadLocationAsyncTaskResponder).execute(lat, lon, context);
                    } else {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
                    }


                }








				// Cargar info wikipedia
				if (lat != null && !lat.equals("") && !lon.equals("")) {
					context.gestionarTarjetaInfo.cargarInfoWikipedia(lat, lon, v);

                    // Cargar informacion del tiempo
                    context.gestionarTarjetaInfo.cargarInfoWeather(lat, lon, v);

				}



			}

		} catch (Exception e) {

			return;

		}

		if (!tablet) {
			context.tiemposView = (ListView) context.findViewById(R.id.lista_tiempos);

			context.tiemposView.addFooterView(v);

			context.avisoTarjetaInfo = v;

		}

	}

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

					try {

						// Preparar titulos
						for (int i = 0; i < wiki.getListaDatos().size(); i++) {

							if(i == (wiki.getListaDatos().size() / 2)){
								sb.append("<br/>");
							}else if (sb.length() > 0) {
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

					} catch (Exception e) {

						sb.setLength(0);

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

					} else {

						sb.append(context.getString(R.string.main_no_items));

						TextView textoWiki = (TextView) v.findViewById(R.id.datos_wiki);

						textoWiki.setText(sb.toString());

					}

				} else {

				}
			}

		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			TextView textoWiki = (TextView) v.findViewById(R.id.datos_wiki);
			textoWiki.setText(context.getString(R.string.aviso_recarga));

			wikiTask = new LoadWikipediaAsyncTask(loadWikipediaAsyncTaskResponder).execute(lat, lon);
		} else {
			Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
		}

	}

	private WeatherQuery datosWeather = null;
    private String paradaWeather = null;

	/**
	 * Cargar la informacion del clima
	 */
	public void cargarInfoWeather(String lat, String lon, final View v) {

		final ImageView iv = (ImageView) v.findViewById(R.id.imageWeather);

		// Verificar si ya disponemos de los datos
        /*if (datosWeather != null) {

			try {

				mostrarElTiempoYW(datosWeather, iv, v);

			} catch (Exception e) {
				e.printStackTrace();
			}

			return;

		} else {
			// datosWeather =;
			// datosWiki = null;
		}*/

        if (paradaWeather != null && datosWeather != null && paradaWeather.equals(Integer.toString(context.paradaActual))) {

            try {

                mostrarElTiempoOwm(datosWeather, iv, v);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return;

        } else {
            paradaWeather = Integer.toString(context.paradaActual);
            datosWeather = null;
        }



		LoadWeatherAsyncTaskResponder loadWeatherAsyncTaskResponder = new LoadWeatherAsyncTaskResponder() {
			public void WeatherLoaded(WeatherQuery weather) {

				iv.setVisibility(ImageView.INVISIBLE);

				if (weather != null) {

					mostrarElTiempoOwm(weather, iv, v);

				} else {

					iv.setVisibility(ImageView.INVISIBLE);

				}
			}

		};

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			TextView textoWeather = (TextView) v.findViewById(R.id.textoWeather);
			textoWeather.setText(context.getString(R.string.aviso_recarga));

			weatherTask = new LoadWeatherAsyncTask(loadWeatherAsyncTaskResponder).execute(lat, lon);
		} else {
			Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * Mostrar el tiempo aemet
	 * 
	 * @param weather
	 * @param iv
	 * @param v
	 */
	private void mostrarElTiempo(WeatherQuery weather, ImageView iv, View v) {

		StringBuffer sb = new StringBuffer();

		try {

			for (int i = 0; i < weather.getListaDatos().size(); i++) {

				for (int j = 0; j < weather.getListaDatos().get(i).getEstadoCielo().size(); j++) {

					if (weather.getListaDatos().get(i).getEstadoCielo().get(j).getPeriodo().equals(getPeriodoWheather())) {

						imgTiempo(weather.getListaDatos().get(i).getEstadoCielo().get(j), iv);

						sb.append("(");

						// sb.append(weather.getListaDatos().get(i).getEstadoCielo().get(j).getDescripcion());

						sb.append(weather.getListaDatos().get(i).getEstadoCielo().get(j).getDescripcion());

						sb.append(") ");

					}

				}

				sb.append(" min/máx: ");
				sb.append(weather.getListaDatos().get(i).getTempMinima());
				sb.append("/");
				sb.append(weather.getListaDatos().get(i).getTempMaxima());

			}

		} catch (Exception e) {

			sb.setLength(0);

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

		} else {

			sb.append(context.getString(R.string.main_no_items));

			TextView textoWeather = (TextView) v.findViewById(R.id.textoWeather);

			textoWeather.setText(sb.toString());

		}

	}

	/**
	 * Informacion del tiempo de yahoo
	 * 
	 * @param weather
	 * @param iv
	 * @param v
	 */
	private void mostrarElTiempoOwm(WeatherQuery weather, ImageView iv, View v) {

		StringBuffer sb = new StringBuffer();

		StringBuffer temp = new StringBuffer();

		try {

			if (weather.getListaDatos() != null && !weather.getListaDatos().isEmpty()) {

				// Imagen
				if (weather.getListaDatos().get(0).getImagen() != null) {
					iv.setImageBitmap(weather.getListaDatos().get(0).getImagen());
					iv.setVisibility(ImageView.VISIBLE);
				} else {
					iv.setVisibility(ImageView.INVISIBLE);
				}

                sb.append(weather.getListaDatos().get(0).getHumidity());
				sb.append("%, ");
                sb.append(weather.getListaDatos().get(0).getLow());
				sb.append("º/");
				sb.append(weather.getListaDatos().get(0).getHigh());
				sb.append("º");


				temp.append(weather.getListaDatos().get(0).getContitionTemp());
				temp.append("º");

			}

		} catch (Exception e) {

			sb.setLength(0);
			temp.setLength(0);

		}

		// Cargar titulos en textView
		if (sb.length() > 0 && temp.length() > 0) {

			try {
				TextView textoWeather = (TextView) v.findViewById(R.id.textoWeather);

				textoWeather.setText(sb.toString());
				// textoWiki.setMovementMethod(LinkMovementMethod.getInstance());

				TextView textoTemperatura = (TextView) v.findViewById(R.id.TextTemperatura);

				textoTemperatura.setText(temp.toString());

                TextView textoLocalidad = (TextView) v.findViewById(R.id.textoWeatherTexto2);
                textoLocalidad.setText("\"" + weather.getListaDatos().get(0).getTitle()+"\"");

                TextView textoWeatherTexto = (TextView) v.findViewById(R.id.textoWeatherTexto);
                textoWeatherTexto.setText(weather.getListaDatos().get(0).getDescription());

				// Datos para siguiente pasada
				datosWeather = weather;

			} catch (Exception e) {

			}

		} else {

			sb.append(context.getString(R.string.main_no_items));

			TextView textoWeather = (TextView) v.findViewById(R.id.textoWeather);

			textoWeather.setText(sb.toString());

		}

	}

	/**
	 * Periodo para tiempo
	 * 
	 * @return periodo
	 */
	private String getPeriodoWheather() {

		SimpleDateFormat ft = new SimpleDateFormat("HH", Locale.US);

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
		/*
		 * if (data.getValor().substring(0, 2).equals("11")) {
		 * iv.setImageResource(R.drawable.weather_sun_blue_48); } else if
		 * (data.getValor().substring(0, 2).equals("15")) {
		 * iv.setImageResource(R.drawable.weather_clouds_blue_48); } else if
		 * (data.getValor().substring(0, 1).equals("1")) {
		 * iv.setImageResource(R.drawable.weather_cloudy_blue_48); } else if
		 * (data.getValor().substring(0, 1).equals("4") ||
		 * data.getValor().substring(0, 1).equals("2")) {
		 * iv.setImageResource(R.drawable.weather_rain_blue_48); } else if
		 * (data.getValor().substring(0, 1).equals("7") ||
		 * data.getValor().substring(0, 1).equals("3")) {
		 * iv.setImageResource(R.drawable.weather_snow_blue_48); } else if
		 * (data.getValor().substring(0, 1).equals("5") ||
		 * data.getValor().substring(0, 1).equals("6")) {
		 * iv.setImageResource(R.drawable.weather_thunder_blue_48); } else {
		 * iv.setVisibility(ImageView.INVISIBLE); return; }
		 */
		iv.setVisibility(ImageView.VISIBLE);

	}

	/*
	 * private void imgTiempoYW(String condionCode, ImageView iv){
	 * 
	 * 
	 * // http://developer.yahoo.com/weather/ switch (conditionCode) { case 19:
	 * // dust or sand case 20: // foggy case 21: // haze case 22: // smoky
	 * //niebla iv.setImageResource(R.drawable.weather_clouds_blue_48); break;
	 * case 23: // blustery case 24: // windy return
	 * R.drawable.ic_weather_windy; case 25: // cold case 26: // cloudy case 27:
	 * // mostly cloudy (night) case 28: // mostly cloudy (day) return
	 * R.drawable.ic_weather_cloudy; case 29: // partly cloudy (night) case 30:
	 * // partly cloudy (day) case 44: // partly cloudy return
	 * R.drawable.ic_weather_partly_cloudy; case 31: // clear (night) case 33:
	 * // fair (night) case 34: // fair (day) return
	 * R.drawable.ic_weather_clear; case 32: // sunny case 36: // hot return
	 * R.drawable.ic_weather_sunny; case 0: // tornado case 1: // tropical storm
	 * case 2: // hurricane case 3: // severe thunderstorms case 4: //
	 * thunderstorms case 5: // mixed rain and snow case 6: // mixed rain and
	 * sleet case 7: // mixed snow and sleet case 8: // freezing drizzle case 9:
	 * // drizzle case 10: // freezing rain case 11: // showers case 12: //
	 * showers case 17: // hail case 18: // sleet case 35: // mixed rain and
	 * hail case 37: // isolated thunderstorms case 38: // scattered
	 * thunderstorms case 39: // scattered thunderstorms case 40: // scattered
	 * showers case 45: // thundershowers case 47: // isolated thundershowers
	 * return R.drawable.ic_weather_raining; case 13: // snow flurries case 14:
	 * // light snow showers case 15: // blowing snow case 16: // snow case 41:
	 * // heavy snow case 42: // scattered snow showers case 43: // heavy snow
	 * case 46: // snow showers return R.drawable.ic_weather_snow;
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

	/**
	 * Control de estado de actualizaciones
	 * 
	 * @param tw
	 */
	public void controlActualizarDB(final TextView tw) {

		// Verificar si hay que consultar la version
		boolean verificar = preferencias.getBoolean("control_verificar_actualiza", true);
		if (!verificar) {
			return;
		}

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

			actualizarNumTask = new ActualizarBDAsyncTask(loadActualizarBDAsyncTaskResponder).execute(true);
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

			actualizarTask = new ActualizarBDAsyncTask(loadActualizarBDAsyncTaskResponder).execute();
		} else {
			Toast.makeText(context.getApplicationContext(), context.getString(R.string.error_red), Toast.LENGTH_LONG).show();

			Notificaciones.notificacionBaseDatos(context.getApplicationContext(), Notificaciones.NOTIFICACION_BD_ERROR, mBuilder, null);

		}

	}

	/**
	 * Detener tareas
	 */
	public void detenerTareas() {

		if (weatherTask != null && weatherTask.getStatus() == Status.RUNNING) {

			weatherTask.cancel(true);

		}

		if (actualizarTask != null && actualizarTask.getStatus() == Status.RUNNING) {

			actualizarTask.cancel(true);

		}

		if (actualizarNumTask != null && actualizarNumTask.getStatus() == Status.RUNNING) {

			actualizarNumTask.cancel(true);

		}

		if (wikiTask != null && wikiTask.getStatus() == Status.RUNNING) {

			wikiTask.cancel(true);

		}

	}

}
