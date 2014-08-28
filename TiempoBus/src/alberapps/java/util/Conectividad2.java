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
package alberapps.java.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.Comunes;

/**
 * Utilidadades de acceso a la red
 */
public class Conectividad2 {

    /**
     * Conexion sencilla
     *
     * @param urlEntrada
     * @return stream
     */
    public static InputStream recuperarStreamConexionSimple(String urlEntrada) {

        InputStream is = null;

        try {

            URL url = new URL(urlEntrada);

            URLConnection con = url.openConnection();

            // timeout
            con.setReadTimeout(Comunes.TIMEOUT_HTTP_READ);
            con.setConnectTimeout(Comunes.TIMEOUT_HTTP_CONNECT);

            is = con.getInputStream();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return is;

    }

    public static InputStream recuperarStreamConexionSimpleDepuracion(Context contexto, String urlEntrada) throws IOException {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        int readTimeout = Integer.parseInt(preferencias.getString("read_timeout", "60")) * 1000;

        int conexionTimeout = Integer.parseInt(preferencias.getString("conexion_timeout", "50")) * 1000;

        InputStream is = null;

        try {

            URL url = new URL(urlEntrada);

            URLConnection con = url.openConnection();

            // timeout
            if (readTimeout > 0) {
                con.setReadTimeout(readTimeout);
            }
            if (conexionTimeout > 0) {
                con.setConnectTimeout(conexionTimeout);
            }

            is = con.getInputStream();

        } catch (MalformedURLException e) {

            e.printStackTrace();

            throw e;

        } catch (IOException e) {

            e.printStackTrace();

            throw e;
        }

        return is;

    }

    public static InputStream recuperarStreamConexionSimpleDepuracionTipo2(Context contexto, String urlEntrada) throws Exception {

        PreferenceManager.setDefaultValues(contexto, R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

        int readTimeout = Integer.parseInt(preferencias.getString("read_timeout_2", "60")) * 1000;

        int conexionTimeout = Integer.parseInt(preferencias.getString("conexion_timeout_2", "50")) * 1000;

        InputStream is = null;

        try {

            HttpGet request = new HttpGet(urlEntrada);

            int timeout = 60000;

            HttpParams httpParam = new BasicHttpParams();

            // Timeout para establecer conexion
            if (conexionTimeout > 0) {
                timeout = conexionTimeout;

                HttpConnectionParams.setConnectionTimeout(httpParam, timeout);

            }

            // Timeout para recibir datos
            int timeoutSocket = 60000;
            if (readTimeout > 0) {
                timeoutSocket = readTimeout;

                HttpConnectionParams.setSoTimeout(httpParam, timeoutSocket);

            }

            DefaultHttpClient client = new DefaultHttpClient(httpParam);

            HttpResponse response = client.execute(request);

            final int statusCode = response.getStatusLine().getStatusCode();

			/*
             * if (statusCode != HttpStatus.SC_OK) {
			 * 
			 * return null; }
			 */

            HttpEntity responseEntity = response.getEntity();
            is = responseEntity.getContent();

        } catch (MalformedURLException e) {

            e.printStackTrace();

            throw e;

        } catch (IOException e) {

            e.printStackTrace();

            throw e;
        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }

        return is;

    }

	/*
	 * public static InputStream recuperarStream(String url) {
	 * 
	 * // Seleccion del metodo de acceso en funcion de la version del sistema if
	 * (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) { return
	 * recuperarStreamNuevo(url); } else { return recuperarStreamBasic(url); }
	 * 
	 * }
	 */

    /**
     *
     * Otros sistemas de conexion
     *
     */

    /**
     * Recuperar con basic
     *
     * @param url
     * @return
     */
    public static InputStream recuperarStreamBasic(String url) {

        HttpGet request = new HttpGet(url);

        try {

            // Timeout para establecer conexion
            int timeout = 3000;
            HttpParams httpParam = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParam, timeout);

            // Timeout para recibir datos
            int timeoutSocket = Comunes.TIMEOUT_HTTP_READ;
            HttpConnectionParams.setSoTimeout(httpParam, timeoutSocket);

            DefaultHttpClient client = new DefaultHttpClient(httpParam);

            HttpResponse response = client.execute(request);

            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {

                return null;
            }

            HttpEntity responseEntity = response.getEntity();
            return responseEntity.getContent();

        } catch (IOException e) {
            request.abort();

        }

        return null;

    }

    /**
     * Recuperar en utf8
     *
     * @param url
     * @return
     */
    public static String recuperarStringUTF8(String url) {

        HttpGet request = new HttpGet(url);

        try {

            // Timeout para establecer conexion
            int timeout = Comunes.TIMEOUT_HTTP_CONNECT;
            HttpParams httpParam = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParam, timeout);

            // Timeout para recibir datos
            int timeoutSocket = Comunes.TIMEOUT_HTTP_READ;
            HttpConnectionParams.setSoTimeout(httpParam, timeoutSocket);

            DefaultHttpClient client = new DefaultHttpClient(httpParam);

            HttpResponse response = client.execute(request);

            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {

                return null;
            }

            HttpEntity responseEntity = response.getEntity();

            return EntityUtils.toString(responseEntity, HTTP.UTF_8);

        } catch (IOException e) {
            request.abort();

        }

        return null;

    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private static InputStream recuperarStreamNuevo(String myurl) {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        // int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(Comunes.TIMEOUT_HTTP_CONNECT);
            conn.setConnectTimeout(Comunes.TIMEOUT_HTTP_READ);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            // int response = conn.getResponseCode();

            is = conn.getInputStream();

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {

            try {
                is.close();
                is = null;
            } catch (Exception ex) {

            }

        } finally {
			/*
			 * if (is != null) { try { is.close(); } catch (IOException e) {
			 * 
			 * } }
			 */
        }

        return is;

    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private static String recuperarStringUTF8Nuevos(String myurl) {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(Comunes.TIMEOUT_HTTP_CONNECT);
            conn.setConnectTimeout(Comunes.TIMEOUT_HTTP_READ);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            // int response = conn.getResponseCode();

            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (Exception e) {

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }

        return null;

    }

    // Reads an InputStream and converts it to a String.
    public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

	/*
	 * ConnectivityManager connMgr = (ConnectivityManager)
	 * getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo networkInfo =
	 * connMgr.getActiveNetworkInfo(); if (networkInfo != null &&
	 * networkInfo.isConnected()) { // fetch data } else { // display error }
	 * ...
	 */

    /**
     * @param inputStream
     * @return
     */
    public static String obtenerStringDeStream(InputStream inputStream) {

        String datos = "";

        Scanner s = new Scanner(inputStream, "ISO-8859-1").useDelimiter("\\A");
        datos = s.hasNext() ? s.next() : "";

        return datos;
    }

    /**
     * @param inputStream
     * @return
     */
    public static String obtenerStringDeStreamUTF8(InputStream inputStream) {

        String datos = "";

        Scanner s = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        datos = s.hasNext() ? s.next() : "";

        return datos;
    }


    /**
     * Date desde string
     *
     * @param fecha
     * @return
     */
    public static Date getFechaDate(String fecha) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        Date fechaDate = null;

        if (fecha != null) {
            try {
                fechaDate = df.parse(fecha);

                return fechaDate;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    /**
     * String desde date
     *
     * @param fecha
     * @return
     */
    public static String getFechaString(Date fecha) {

        DateFormat df = new SimpleDateFormat("EEE dd MMM yyyy HH:mm", Locale.US);

        String fechaString = null;

        if (fecha != null) {

            fechaString = df.format(fecha);

            return fechaString;

        }

        return null;

    }

    /**
     * String desde date
     *
     * @param fecha
     * @return
     */
    public static String getFechaSQL(Date fecha) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        String fechaString = null;

        if (fecha != null) {

            fechaString = df.format(fecha);

            return fechaString;

        }

        return null;

    }

    /**
     * Aleatorio
     *
     * @return int
     */
    public static boolean ipRandom() {

        int min = 0;
        int max = 1;

        Random rand = new Random();

        int random = rand.nextInt((max - min) + 1) + min;

        Log.d("RANDOM", "RANDOM: " + random);

        if (random == 0) {
            return true;
        } else {
            return false;
        }

    }

}
