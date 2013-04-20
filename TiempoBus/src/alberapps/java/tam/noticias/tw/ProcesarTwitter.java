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
package alberapps.java.tam.noticias.tw;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;

public class ProcesarTwitter {

	public static final String tw_alicante = "http://search.twitter.com/search.json?q=from:Alicante_City";
	public static final String tw_alicante_ruta = "http://twitter.com/Alicante_City";

	public static final String tw_alberapps = "http://search.twitter.com/search.json?q=from:alberapps";
	public static final String tw_alberapps_ruta = "http://twitter.com/alberapps";

	public static final String tw_campello = "http://search.twitter.com/search.json?q=from:campelloturismo";
	public static final String tw_campello_ruta = "http://twitter.com/campelloturismo";
	
	public static final String tw_sanvi = "http://search.twitter.com/search.json?q=from:aytoraspeig";
	public static final String tw_sanvi_ruta = "http://twitter.com/aytoraspeig";
		
	public static final String tw_santjoan = "http://search.twitter.com/search.json?q=from:sant_joan";
	public static final String tw_santjoan_ruta = "http://twitter.com/sant_joan";
	
	
	public static final String TW_STATUS ="/status/";
	
	
	/**
	 * listas que se quieran
	 * 
	 * @return listado
	 */
	public static List<TwResultado> procesar() {

		List<TwResultado> lista;

		lista = procesarTw(tw_alberapps,tw_alberapps_ruta);

		lista.addAll(procesarTw(tw_alicante,tw_alicante_ruta));
		
		lista.addAll(procesarTw(tw_campello, tw_campello_ruta));
		
		lista.addAll(procesarTw(tw_sanvi, tw_sanvi_ruta));
		
		lista.addAll(procesarTw(tw_santjoan, tw_santjoan_ruta));

		if (lista != null && !lista.isEmpty()) {

			// Ordenar por fecha
			Collections.sort(lista);

		}

		return lista;

	}

	/**
	 * Procesar
	 * 
	 * @param url
	 * @return listado
	 */
	private static List<TwResultado> procesarTw(String url, String ruta) {

		List<TwResultado> listaDatos = new ArrayList<TwResultado>();

		InputStream source = recuperarStream(url);

		Gson gson = new Gson();

		Reader reader = new InputStreamReader(source);

		Respuesta response = gson.fromJson(reader, Respuesta.class);

		List<Resultado> results = response.results;

		for (int i = 0; i < results.size(); i++) {

			TwResultado dato = new TwResultado();

			dato.setUsuario("@" + results.get(i).fromUser);

			dato.setMensaje(results.get(i).text);

			dato.setImagen(results.get(i).profileImageUrl);

			// Imagen de perfil
			dato.setImagenBitmap(recuperaImagen(results.get(i).profileImageUrl));

			dato.setNombreCompleto(results.get(i).fromUserName);

			// Fri, 14 Dec 2012 10:48:19 +0000

			String fecha = formatearFechaTw(results.get(i).createdAt);

			if (fecha != null) {

				dato.setFecha(fecha);
				dato.setFechaDate(parsearFechaTw(results.get(i).createdAt));

			} else {

				dato.setFecha(results.get(i).createdAt.substring(5, 16));
			}

			dato.setUrl(ruta);

			dato.setId(results.get(i).idStr);
			
			listaDatos.add(dato);
		}

		return listaDatos;
	}

	/**
	 * stream
	 * 
	 * @param url
	 * @return
	 */
	private static InputStream recuperarStream(String url) {

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
	 * Recuperar la imagen
	 * 
	 * @param urlParam
	 * @return imagen
	 */
	private static Bitmap recuperaImagen(String urlParam) {

		try {
			URL url = new URL(urlParam);

			return BitmapFactory.decodeStream(url.openConnection().getInputStream());

		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * Formatear la fecha devuelta por tw
	 * 
	 * @param fecha
	 * @return string
	 */
	private static String formatearFechaTw(String fecha) {

		Date fechaTemp = parsearFechaTw(fecha);

		if (fechaTemp != null) {

			final String nuevaFechaP = "EEE dd MMM yyyy HH:mm";

			SimpleDateFormat sfNueva = new SimpleDateFormat(nuevaFechaP, Locale.getDefault());

			return sfNueva.format(fechaTemp);

		} else {

			return null;
		}

	}

	/**
	 * Pasar a Date
	 * 
	 * @param fecha
	 * @return date
	 */
	private static Date parsearFechaTw(String fecha) {

		Date fechaTemp = null;

		// Fri, 14 Dec 2012 10:48:19 +0000

		final String twFecha = "EEE, dd MMM yyyy HH:mm:ss ZZZZZ";

		SimpleDateFormat sf = new SimpleDateFormat(twFecha, Locale.ENGLISH);

		sf.setLenient(true);

		try {

			fechaTemp = sf.parse(fecha);

		} catch (ParseException e) {

			fechaTemp = null;

		}
		return fechaTemp;

	}

}
