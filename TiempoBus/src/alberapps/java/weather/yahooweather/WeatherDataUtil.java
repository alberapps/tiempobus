/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2015 Alberto Montiel
 *
 *  based on DashClock weather extension. Copyright 2013 Google Inc. Apache License, Version 2.0
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

import alberapps.android.tiempobus.R;

/**
 * Utilidades weather
 */
public class WeatherDataUtil {


    public static int getConditionIconId(int conditionCode) {
        // http://developer.yahoo.com/weather/
        switch (conditionCode) {
            case 19: // dust or sand
            case 20: // foggy
            case 21: // haze
            case 22: // smoky
                return R.drawable.art_fog;
            case 23: // blustery
            case 24: // windy
                return R.drawable.art_clear;
            case 25: // cold
            case 26: // cloudy
            case 27: // mostly cloudy (night)
            case 28: // mostly cloudy (day)
                return R.drawable.art_clouds;
            case 29: // partly cloudy (night)
            case 30: // partly cloudy (day)
            case 44: // partly cloudy
                return R.drawable.art_light_clouds;
            case 31: // clear (night)
            case 33: // fair (night)
            case 34: // fair (day)
                return R.drawable.art_clear;
            case 32: // sunny
            case 36: // hot
                return R.drawable.art_clear;

            case 5: // mixed rain and snow
            case 6: // mixed rain and sleet
            case 7: // mixed snow and sleet
            case 8: // freezing drizzle
            case 9: // drizzle
            case 10: // freezing rain
            case 11: // showers
            case 12: // showers
            case 17: // hail
            case 18: // sleet
            case 35: // mixed rain and hail
            case 40: // scattered showers
                return R.drawable.art_rain;

            case 4: // thunderstorms
            case 0: // tornado
            case 1: // tropical storm
            case 2: // hurricane
            case 3: // severe thunderstorms
            case 37: // isolated thunderstorms
            case 38: // scattered thunderstorms
            case 39: // scattered thunderstorms
            case 45: // thundershowers
            case 47: // isolated thundershowers
                return R.drawable.art_storm;



            case 13: // snow flurries
            case 14: // light snow showers
            case 15: // blowing snow
            case 16: // snow
            case 41: // heavy snow
            case 42: // scattered snow showers
            case 43: // heavy snow
            case 46: // snow showers
                return R.drawable.art_snow;
        }

        return R.drawable.art_clear;
    }


}
