/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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
package alberapps.android.tiempobus.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;
import java.util.List;

import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.java.weather.WeatherData;
import alberapps.java.weather.WeatherQuery;
import alberapps.java.weather.yahooweather.ProcesarYahooWeather;

/**
 * Tarea asincrona para recuperar informacion metereologica
 */
public class LoadWeatherAsyncTask extends AsyncTask<Object, Void, WeatherQuery> {

    /**
     *
     *
     */
    public interface LoadWeatherAsyncTaskResponder {
        public void WeatherLoaded(WeatherQuery Wikipedia);
    }

    private LoadWeatherAsyncTaskResponder responder;

    /**
     * @param responder
     */
    public LoadWeatherAsyncTask(LoadWeatherAsyncTaskResponder responder) {
        this.responder = responder;
    }

    /**
     *
     */
    @Override
    protected WeatherQuery doInBackground(Object... datos) {
        WeatherQuery weather = null;
        try {

            String lat = (String) datos[0];
            String lon = (String) datos[1];

            //Control cache
            Context context = (Context) datos[2];
            String paradaWeather = (String) datos[3];

            //Control de cache
            String fechaConsultaClima = PreferencesUtil.getCache(context, "cache_clima_fecha");

            //Si no hay valor almacenarlo y continuar
            if (fechaConsultaClima == null || fechaConsultaClima.equals("")) {
                String control = String.valueOf((new Date()).getTime());
                PreferencesUtil.putCache(context, "cache_clima_fecha", control);
                PreferencesUtil.putCache(context, "cache_clima_parada", paradaWeather);
                PreferencesUtil.putCache(context, "cache_clima_json", "");
            } else {

                //Si la parada ha cambiado, se descarta
                String paradaCache = PreferencesUtil.getCache(context, "cache_clima_parada");

                if (!paradaCache.equals(paradaWeather)) {

                    String control = String.valueOf((new Date()).getTime());
                    PreferencesUtil.putCache(context, "cache_clima_fecha", control);
                    PreferencesUtil.putCache(context, "cache_clima_parada", paradaWeather);
                    PreferencesUtil.putCache(context, "cache_clima_json", "");

                } else {

                    //Si coincide parada continuar para verificar hora

                    Date fecha = new Date(Long.parseLong(fechaConsultaClima));

                    Date ahora = new Date();

                    //Si la diferencia es menor a 15 minutos. No continuar
                    if (ahora.getTime() - fecha.getTime() < 15 * 60 * 1000) {

                        String jsonCache = PreferencesUtil.getCache(context, "cache_clima_json");

                        if (jsonCache != null && !jsonCache.equals("")) {
                            //List<WeatherData> weatherData = ProcesarOWMCurrect.parsea(jsonCache);

                            List<WeatherData> weatherData = ProcesarYahooWeather.parsea(jsonCache);


                            if (weatherData != null) {
                                weather = new WeatherQuery();
                                weather.setListaDatos(weatherData);

                                return weather;

                            }


                        }
                    } else {
                        String control = String.valueOf((new Date()).getTime());
                        PreferencesUtil.putCache(context, "cache_clima_fecha", control);
                        PreferencesUtil.putCache(context, "cache_clima_parada", paradaWeather);
                        PreferencesUtil.putCache(context, "cache_clima_json", "");
                    }


                }

            }


            //weather = ProcesarDatosWeatherService.getDatosClima();

            //weather = ProcesarYWRSS.getDatosClima();

            //weather = ProcesarOWMCurrect.getDatosClima(lat, lon);

            weather = ProcesarYahooWeather.getDatosClima(lat, lon);

            //Guardar ultima consulta json
            if (weather.getListaDatos() != null && !weather.getListaDatos().isEmpty() && weather.getListaDatos().get(0).getJson() != null) {
                PreferencesUtil.putCache(context, "cache_clima_json", weather.getListaDatos().get(0).getJson());
            }

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

        return weather;
    }

    /**
     *
     */
    @Override
    protected void onPostExecute(WeatherQuery result) {
        if (responder != null) {
            responder.WeatherLoaded(result);
        }

    }

}
