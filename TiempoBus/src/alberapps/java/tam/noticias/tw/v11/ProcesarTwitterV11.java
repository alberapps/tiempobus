package alberapps.java.tam.noticias.tw.v11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Base64;

public class ProcesarTwitterV11 {

	
	
	
	/**
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @return
	 */
	private static String codificarClaves(String consumerKey, String consumerSecret){
				
		
		try {
			
			String codificadoConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
			String codificadoConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");
			
			
			String clave = codificadoConsumerKey + "."+ codificadoConsumerSecret;
			
			String codificadoBytes = Base64.encodeToString(clave.getBytes(), Base64.DEFAULT);
			
			return codificadoBytes;
			
			
		} catch (UnsupportedEncodingException e) {
			
			return null;
			
		}
		
		
		
		
	}
	
	
	
	
	public static InputStream recuperarStreamV11(String myurl) {
		InputStream is = null;
		
		String url2 = "https://api.twitter.com/1.1/account/rate_limit_status.xml";
		
		 
		String credenciales = codificarClaves(Constantes.ck, Constantes.cs);

		try {
			URL url = new URL(url2);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Host", "api.twitter.com");
			conn.setRequestProperty("User-Agent", "TiempoBus");
			conn.setRequestProperty("Authorization", "Basic "+credenciales);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			conn.setRequestProperty("Content-Length", "29");
			conn.setUseCaches(false);
			
			OutputStream os = conn.getOutputStream();
						
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
			
			writer.write("grant_type=client_credentials");
			
			writer.flush();
			writer.close();
			//os.close();
			
			
			// Starts the query
			conn.connect();
			// int response = conn.getResponseCode();

			
			
			
			if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {

				return null;
			}
			
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
	
	
	
	/**
	 * stream
	 * 
	 * @param url
	 * @return
	 */
	public static InputStream recuperarStream(String url) {

		
		
		
		
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
	
	
}
