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
package alberapps.java.weather.openweathermap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.tw.Constantes;
import alberapps.java.util.Conectividad;
import alberapps.java.weather.WeatherData;
import alberapps.java.weather.WeatherQuery;

/**
 * Procesar informacion del tiempo de yahoo
 * 
 * 
 */
public class ProcesarOWMCurrect {

    private final static String LOG_TAG = ProcesarOWMCurrect.class.getSimpleName();


	/**
	 * Datos del clima
	 * 
	 * @return clima
	 * @throws Exception
	 */
	public static WeatherQuery getDatosClima(String lat, String lon) throws Exception {

		WeatherQuery resultados = new WeatherQuery();


        String lang = UtilidadesUI.getIdiomaWiki();

        String glat = Double.toString((Integer.parseInt(lat) / 1E6));
        String glon = Double.toString((Integer.parseInt(lon) / 1E6));

		resultados.setListaDatos(parsea(glat, glon, lang));

		return resultados;

	}

	/**
	 * Parsear los datos RSS de yahoo
	 * 
	 * @param lon
	 * @return datos
	 */
	public static List<WeatherData> parsea(String lat, String lon, String lang) {


        String forecastJsonStr = null;


        List<WeatherData> listaWeather = new ArrayList<WeatherData>();
        WeatherData data = null;

        //InputStream st = null;


        try {


            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").authority("api.openweathermap.org").appendPath("data").appendPath("2.5")
                    .appendPath("weather")
                    //.appendQueryParameter("lat", "38.347107")
                    //.appendQueryParameter("lon", "-0.4887623")
                    .appendQueryParameter("lat", lat)
                    .appendQueryParameter("lon", lon)

                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("lang", lang) //es
                    .appendQueryParameter("APPID", Constantes.OPENWEATHERMAPAPI);


            Uri urlWeather = builder.build();

            Log.d(LOG_TAG, "Built URI " + urlWeather.toString());


            forecastJsonStr = Conectividad.conexionGetIsoString(urlWeather.toString());

            JSONObject forecastJson = new JSONObject(forecastJsonStr);




            String ciudad = forecastJson.getString("name");
            String date = forecastJson.getString("dt");


            //Main
            JSONObject main = forecastJson.getJSONObject("main");
            String temp = main.getString("temp");
            String humidity = main.getString("humidity");
            String pressure = main.getString("pressure");
            String tempMin = main.getString("temp_min");
            String tempMax = main.getString("temp_max");

            //Sys
            JSONObject sys = forecastJson.getJSONObject("sys");
            String sunset = sys.getString("sunset");
            String sunrise = sys.getString("sunrise");


            //Weather
            JSONArray weatherArray = forecastJson.getJSONArray("weather");


            JSONObject weather = weatherArray.getJSONObject(0);

            String m = weather.getString("main");
            String description = weather.getString("description");
            String icon = weather.getString("icon");



            data = new WeatherData();

            data.setContitionTemp(temp);
            data.setLow(tempMin);
            data.setHigh(tempMax);

            data.setHumidity(humidity);

            data.setSunrise(sunrise);
            data.setSunset(sunset);

            // Imagen
            Uri.Builder builderImg = new Uri.Builder();
            builderImg.scheme("http").authority("openweathermap.org").appendPath("img").appendPath("w").appendPath(icon + ".png");

            Uri urlImg = builderImg.build();

            data.setImagen(recuperaImagen(urlImg.toString()));


            data.setTitle(ciudad);
            data.setDescription(description);

            listaWeather.add(data);



/*
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
// For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

// Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

// The date/time is returned as a long. We need to convert that
// into something human-readable, since most people won't read "1400356800" as
// "this saturday".
                long dateTime = dayForecast.getLong(OWM_DATETIME);
                day = getReadableDateString(dateTime);

// description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

// Temperatures are in a child object called "temp". Try not to name variables
// "temp" when working with temperature. It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(activity, high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }
*/






        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {

            /*try {
                if (st != null) {
                    st.close();
                }
            } catch (IOException eb) {

            }*/

        }










/*
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		List<WeatherData> listaWeather = new ArrayList<WeatherData>();
		WeatherData data = null;

		InputStream st = null;

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			st = Conectividad.conexionGetIsoStream(urlEntrada);
			Document dom = builder.parse(st);
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("item");
			for (int i = 0; i < items.getLength(); i++) {

				data = new WeatherData();

				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j = 0; j < properties.getLength(); j++) {
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("title")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setTitle(textoProc);

					} else if (name.equalsIgnoreCase("link")) {

						data.setLink(property.getFirstChild().getNodeValue());

					} else if (name.equalsIgnoreCase("description")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setDescription(textoProc);

					} else if (name.equalsIgnoreCase("geo:lat")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setGeolat(textoProc);

					} else if (name.equalsIgnoreCase("geo:long")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setGeolong(textoProc);

					} else if (name.equalsIgnoreCase("pubDate")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						data.setPubDate(textoProc);

					} else if (name.equalsIgnoreCase("yweather:condition")) {

						for (int k = 0; k < property.getAttributes().getLength(); k++) {

							if (property.getAttributes().item(k).getNodeName().equals("text")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setContitionText(textoProc);

							} else if (property.getAttributes().item(k).getNodeName().equals("code")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setContitionCode(textoProc);

								// Imagen
								data.setImagen(recuperaImagen(URL_IMAGEN + textoProc + ".gif"));

							} else if (property.getAttributes().item(k).getNodeName().equals("temp")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setContitionTemp(textoProc);

							}

						}

					} else if (name.equalsIgnoreCase("yweather:forecast")) {

						for (int k = 0; k < property.getAttributes().getLength(); k++) {

							if (property.getAttributes().item(k).getNodeName().equals("low")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setLow(textoProc);

							} else if (property.getAttributes().item(k).getNodeName().equals("high")) {

								String textoProc = (Html.fromHtml(property.getAttributes().item(k).getNodeValue())).toString();

								data.setHigh(textoProc);

							}
						}

						break;

					}

				}
				listaWeather.add(data);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {

			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

		}
*/
		return listaWeather;

	}

	/**
	 * Recuperar la imagen
	 * 
	 * @param urlParam
	 * @return imagen
	 */
	private static Bitmap recuperaImagen(String urlParam) {

		InputStream st = null;

		Bitmap bm = null;

		try {

			st = Conectividad.conexionGetIsoStream(urlParam);

			bm = BitmapFactory.decodeStream(st);

		} catch (Exception e) {

			bm = null;

		} finally {

			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException e) {

			}

		}

		return bm;

	}

}
