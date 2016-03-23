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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.tw.Constantes;
import alberapps.java.util.Conectividad;
import alberapps.java.weather.WeatherData;
import alberapps.java.weather.WeatherQuery;

/**
 * Procesar informacion del tiempo de openweathermap
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


        String lang = UtilidadesUI.getIdiomaOWM();

        String glat = Double.toString((Integer.parseInt(lat) / 1E6));
        String glon = Double.toString((Integer.parseInt(lon) / 1E6));

        try {

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").authority("api.openweathermap.org").appendPath("data").appendPath("2.5")
                    .appendPath("weather")
                            //.appendQueryParameter("lat", "38.347107")
                            //.appendQueryParameter("lon", "-0.4887623")
                    .appendQueryParameter("lat", glat)
                    .appendQueryParameter("lon", glon)

                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("lang", lang) //es
                    .appendQueryParameter("APPID", Constantes.OPENWEATHERMAPAPI);


            Uri urlWeather = builder.build();

            Log.d(LOG_TAG, "Built URI " + urlWeather.toString());


            String forecastJsonStr = Conectividad.conexionGetUtf8String(urlWeather.toString(), true);

            resultados.setListaDatos(parsea(forecastJsonStr));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return resultados;

    }

    /**
     * Parsea los datos json recibidos
     *
     * @param forecastJsonStr
     * @return
     */
    public static List<WeatherData> parsea(String forecastJsonStr) {


        List<WeatherData> listaWeather = new ArrayList<>();
        WeatherData data = null;


        try {


            JSONObject forecastJson = new JSONObject(forecastJsonStr);


            String ciudad = forecastJson.getString("name");
            String date = forecastJson.getString("dt");
            String idCiudad = forecastJson.getString("id");


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


            String numberFormat = "";
            try {
                Double tempD = Double.parseDouble(temp);
                NumberFormat formatear = new DecimalFormat("#0.00");
                numberFormat = formatear.format(tempD);
            } catch (Exception e) {

            }

            data.setContitionTemp(numberFormat);

            numberFormat = "";
            try {
                Double tempD = Double.parseDouble(tempMin);
                NumberFormat formatear = new DecimalFormat("#0.00");
                numberFormat = formatear.format(tempD);
            } catch (Exception e) {

            }
            data.setLow(numberFormat);

            numberFormat = "";
            try {
                Double tempD = Double.parseDouble(tempMax);
                NumberFormat formatear = new DecimalFormat("#0.00");
                numberFormat = formatear.format(tempD);
            } catch (Exception e) {

            }

            data.setHigh(numberFormat);


            data.setHumidity(humidity);

            data.setSunrise(sunrise);
            data.setSunset(sunset);

            // Imagen
            /*Uri.Builder builderImg = new Uri.Builder();
            builderImg.scheme("http").authority("openweathermap.org").appendPath("img").appendPath("w").appendPath(icon + ".png");

            Uri urlImg = builderImg.build();

            data.setImagen(recuperaImagen(urlImg.toString()));
            */


            data.setIcon(icon);


            data.setTitle(ciudad);
            data.setDescription(description);


            //Datos para cache
            data.setJson(forecastJsonStr);

            //Link
            Uri.Builder builderLink = new Uri.Builder();
            builderLink.scheme("http").authority("openweathermap.org").appendPath("city").appendPath(idCiudad);
            Uri urlLink = builderLink.build();
            data.setLink(builderLink.toString());


            listaWeather.add(data);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


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
