package alberapps.java.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Conectividad {

	public static String postContenido(String urlPost, String post) {

		// Abrir Conexion
		HttpURLConnection urlConnection = null;
		
		String datos = null;

		try {

			// Crear url
			URL url = new URL(urlPost);

			urlConnection = (HttpURLConnection) url.openConnection();

			urlConnection.setDoOutput(true);
			urlConnection.setChunkedStreamingMode(0);
			// urlConnection.setFixedLengthStreamingMode(int)

			// urlConnection.setReadTimeout(10000 /* milliseconds */);
			// urlConnection.setConnectTimeout(15000 /* milliseconds */);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoInput(true);
			
			urlConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

			OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
			writeIt(out, post);

			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			datos = readIt(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		return datos;

	}

	// Reads an InputStream and converts it to a String.
	public static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {

		int len = 1000000;

		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

	public static void writeIt(OutputStream out, String str) throws IOException {

		out.write(str.getBytes("UTF-8"));
		
		out.flush();

	}

	
	public static InputStream stringToStream(String str){
		
		
		InputStream stream = null;
		try {
			stream = new ByteArrayInputStream(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stream;
		
	}
	
}
