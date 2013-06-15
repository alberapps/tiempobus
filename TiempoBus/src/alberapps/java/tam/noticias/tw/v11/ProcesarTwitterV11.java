package alberapps.java.tam.noticias.tw.v11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import android.util.Base64;

import com.google.gson.Gson;

public class ProcesarTwitterV11 {

	private static String RUTA_AUTH = "https://api.twitter.com/oauth2/token";

	private static String RUTA_LIMIT = "https://api.twitter.com/1.1/account/rate_limit_status.xml";

	/**
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @return
	 */
	private static String codificarClaves(String consumerKey, String consumerSecret) {

		try {

			String codificadoConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
			String codificadoConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");

			String clave = codificadoConsumerKey + ":" + codificadoConsumerSecret;

			// String clave = consumerKey + ":" + consumerSecret;

			// byte[] data = clave.getBytes("UTF-8");

			String codificadoBytes = Base64.encodeToString(clave.getBytes(), Base64.DEFAULT);

			codificadoBytes = codificadoBytes.replaceAll("\n", "");

			return codificadoBytes;

		} catch (UnsupportedEncodingException e) {

			return null;

		}

	}

	public static String recuperarToken() {

		Connection conexion = null;

		try {

			String credenciales = codificarClaves(Constantes.ck, Constantes.cs);

			conexion = Jsoup.connect(RUTA_AUTH).header("Host", "api.twitter.com").userAgent("TiempoBus").header("Authorization", "Basic " + credenciales)
					.header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8").header("Content-Length", "29").header("Accept-Encoding", "gzip").data("grant_type", "client_credentials").timeout(10000)
					.method(Method.POST);

			conexion.ignoreHttpErrors(true);
			conexion.ignoreContentType(true);

			conexion.execute();

			if (conexion.response().statusCode() == HttpURLConnection.HTTP_OK) {

				String body = conexion.response().body();

				return body;

			}

			// {"errors":[{"label":"authenticity_token_error","code":99,"message":"Unable to verify your credentials"}]}
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
	
	
	public static String recuperarTwV11(String myurl, String token) {

		Connection conexion = null;

		try {

			
			conexion = Jsoup.connect(myurl).header("Host", "api.twitter.com").userAgent("TiempoBus").header("Authorization", "Bearer " + token)
					.header("Accept-Encoding", "gzip").timeout(10000)
					.method(Method.GET);

			conexion.ignoreHttpErrors(true);
			conexion.ignoreContentType(true);

			conexion.execute();

			if (conexion.response().statusCode() == HttpURLConnection.HTTP_OK) {

				String body = conexion.response().body();

				return body;

			}

			// {"errors":[{"label":"authenticity_token_error","code":99,"message":"Unable to verify your credentials"}]}
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
	
	

	public static InputStream recuperarStreamAuthV11() {
		InputStream is = null;

		String credenciales = codificarClaves(Constantes.ck, Constantes.cs);

		try {
			URL url = new URL(RUTA_AUTH);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			// conn.setReadTimeout(10000 /* milliseconds */);
			// conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Host", "api.twitter.com");
			conn.setRequestProperty("User-Agent", "TiempoBus");
			conn.setRequestProperty("Authorization", "Basic " + credenciales);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			conn.setRequestProperty("Content-Length", "29");
			// conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setUseCaches(false);

			String data = URLEncoder.encode("grant_type", "UTF-8") + "=" + URLEncoder.encode("client_credentials", "UTF-8");

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

			writer.write(data);

			writer.flush();
			// writer.close();
			// os.close();

			// Starts the query
			// conn.connect();
			// int response = conn.getResponseCode();

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			StringBuilder sb = new StringBuilder();

			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}

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

	public static String recuperarAuth() {

		// InputStream source = ProcesarTwitterV11.recuperarStreamAuthV11();

		String source = recuperarToken();

		if (source != null) {

			try {

				Gson gson = new Gson();

				// Reader reader = new InputStreamReader(source);

				Token response = gson.fromJson(source, Token.class);

				// source.close();

				if (response != null) {

					return response.accessToken;

				}

			} catch (Exception e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;

	}

	public static InputStream recuperarStreamV11(String myurl) {
		InputStream is = null;

		String url2 = "";

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
			conn.setRequestProperty("Authorization", "Basic " + credenciales);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			conn.setRequestProperty("Content-Length", "29");
			conn.setUseCaches(false);

			OutputStream os = conn.getOutputStream();

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

			writer.write("grant_type=client_credentials");

			writer.flush();
			writer.close();
			// os.close();

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
