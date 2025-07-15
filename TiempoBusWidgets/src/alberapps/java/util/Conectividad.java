/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2014 Alberto Montiel
 * <p/>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.java.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Acceso a la red. Conexiones para distintas versiones de Android
 */
public class Conectividad {

    public static final String USER_AGENT = "TiempoBus/4.0 (http://alberapps.blogspot.com; alberapps@gmail.com)";

    /**
     * Conexion con get y codificacion UTF-8
     * <p/>
     * Sin cache
     *
     * @param url
     * @return
     */
    public static String conexionGetUtf8(String url) throws Exception {

        return conexionGetIso(url, false, null, true, 0);

    }

    /**
     * Conexion con get y codificacion ISO
     *
     * @param urlGet
     * @param usarCache
     * @return string
     */
    public static String conexionGetIso(String urlGet, boolean usarCache, String userAgent, boolean utf8, int retry) throws Exception {


        // Abrir Conexion
        HttpURLConnection urlConnection = null;

        String datos = null;

        InputStream in = null;

        try {

            // Crear url
            URL url = new URL(urlGet);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setReadTimeout(Comunes.READ_TIMEOUT);
            urlConnection.setConnectTimeout(Comunes.CONNECT_TIMEOUT);

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            if (userAgent == null) {
                urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            } else {
                urlConnection.setRequestProperty("User-Agent", userAgent);
            }

            if (!usarCache) {
                urlConnection.addRequestProperty("Cache-Control", "no-cache");
                Log.d("CONEXION", "Sin cache");
            } else {
                Log.d("CONEXION", "Con cache");
            }

            in = new BufferedInputStream(urlConnection.getInputStream());

            if (utf8) {
                datos = Utilidades.obtenerStringDeStreamUTF8(in);
            } else {
                datos = Utilidades.obtenerStringDeStream(in);
            }

            if ((datos.contains("<error><code>051</code>") || datos.contains("<GetPasoParadaResult><status>0</status></GetPasoParadaResult></GetPasoParadaResponse>")) && retry < 3) {
                Log.d("CONEXION", "Reintento: " + retry);
                TimeUnit.SECONDS.sleep(2);
                return conexionGetIso(urlGet, usarCache, userAgent, utf8, retry + 1);
            }

        } catch (IOException e) {

            e.printStackTrace();

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }

            throw new Exception("Error al acceder al servicio");

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }

        }

        //Log.d("CONEXION", datos);

        return datos;

    }

}
