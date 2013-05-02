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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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

import android.os.Build;

/**
 * 
 * Utilidadades de acceso a la red
 * 
 */
public class Utilidades {

	public static InputStream recuperarStream(String url) {

		// Seleccion del metodo de acceso en funcion de la version del sistema
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return recuperarStreamNuevo(url);
		} else {
			return recuperarStreamBasic(url);
		}

	}

	/**
	 * Recuperar con basic
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
			int timeoutSocket = 15000;
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
			int timeout = 10000;
			HttpParams httpParam = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParam, timeout);

			// Timeout para recibir datos
			int timeoutSocket = 15000;
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
		//int len = 500;

		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
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
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			//int response = conn.getResponseCode();

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
	 * 
	 * @param inputStream
	 * @return
	 */
	public static String obtenerStringDeStream(InputStream inputStream){
		
		String datos = "";
		
		Scanner s = new Scanner(inputStream, "ISO-8859-1").useDelimiter("\\A");
		datos = s.hasNext() ? s.next() : "";
		
		return datos;
	}
	
}
