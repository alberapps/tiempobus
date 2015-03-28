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
package alberapps.java.weather.yahooweather;

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
import alberapps.java.util.Conectividad;
import alberapps.java.weather.WeatherData;
import alberapps.java.weather.WeatherQuery;

/**
 * Procesar informacion del tiempo de yahoo weather
 */
public class ProcesarYahooWeather {

    private final static String LOG_TAG = ProcesarYahooWeather.class.getSimpleName();


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


            /*
            select * from weather.forecast where woeid in (select woeid from geo.placefinder where text='38.349491,-0.50056' and gflags='LR' and locale='ES') and u = 'c'

            https://query.yahooapis.com/v1/public/yql?q=

            &format=json&diagnostics=true&callback=

            https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.placefinder%20where%20text%3D'38.349491%2C-0.50056'%20and%20gflags%3D'LR'%20and%20locale%3D'ES')%20and%20u%20%3D%20'c'&format=json&callback=

            */

            String query = "select * from weather.forecast where woeid in (select woeid from geo.placefinder where text='38.349491,-0.50056' and gflags='LR' and locale='ES') and u = 'c'";

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").authority("query.yahooapis.com").appendPath("v1").appendPath("public")
                    .appendPath("yql")
                    .appendQueryParameter("q", query)
                    .appendQueryParameter("format", "json")
                    ;


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


        List<WeatherData> listaWeather = new ArrayList<WeatherData>();
        WeatherData data = null;


        try {


            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONObject query = forecastJson.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");





            //String date = forecastJson.getString("pubDate");
            String idCiudad = "";//forecastJson.getString("id");












            //Channel
            JSONObject channel = results.getJSONObject("channel");

            //Location
            JSONObject location = channel.getJSONObject("location");
            String ciudad = location.getString("city");

            //Atmosphere
            JSONObject atmosphere = channel.getJSONObject("atmosphere");
            String humidity = atmosphere.getString("humidity");

            //Astronomy
            JSONObject astronomy = channel.getJSONObject("astronomy");
            String sunset = astronomy.getString("sunset");
            String sunrise = astronomy.getString("sunrise");


            //Item
            JSONObject item = channel.getJSONObject("item");

            //Condition
            JSONObject condition = item.getJSONObject("condition");
            String temp = condition.getString("temp");
            String description = condition.getString("text");
            String icon = condition.getString("code");


            //Forecast
            JSONArray forecast = item.getJSONArray("forecast");
            //Max
            JSONObject weather = forecast.getJSONObject(0);
            String tempMin = weather.getString("low");
            String tempMax = weather.getString("high");


            //Fin Item


            //Fin Channel



            //String pressure = main.getString("pressure");










            data = new WeatherData();


            String numberFormat = "";
            try {
                Double tempD = Double.parseDouble(temp);
                NumberFormat formatear = new DecimalFormat("#0.##");
                numberFormat = formatear.format(tempD);
            } catch (Exception e) {

            }

            data.setContitionTemp(numberFormat);

            numberFormat = "";
            try {
                Double tempD = Double.parseDouble(tempMin);
                NumberFormat formatear = new DecimalFormat("#0.##");
                numberFormat = formatear.format(tempD);
            } catch (Exception e) {

            }
            data.setLow(numberFormat);

            numberFormat = "";
            try {
                Double tempD = Double.parseDouble(tempMax);
                NumberFormat formatear = new DecimalFormat("#0.##");
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
            //builderLink.scheme("http").authority("openweathermap.org").appendPath("city").appendPath(idCiudad);
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
