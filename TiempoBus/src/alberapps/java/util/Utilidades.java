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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import alberapps.android.tiempobus.util.UtilidadesUI;
import android.util.Log;

/**
 * 
 * Clase de utilidades
 * 
 */
public class Utilidades {

	/**
	 * 
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
	 * 
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

		DateFormat df = new SimpleDateFormat("EEE dd MMM yyyy HH:mm", UtilidadesUI.getLocaleUsuario());

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

	public static String getFechaControl() {

		Date fecha = new Date();
		
		DateFormat df = new SimpleDateFormat("ddMMyyyy", Locale.US);

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
	public static boolean isFechaControl(String fechaControl, String fechaPreferencias) {

		Date fechaDateControl = null;
		Date fechaDatePref = null;
		

		DateFormat df = new SimpleDateFormat("ddMMyyyy", Locale.US);

		
		
		if (fechaControl != null && !fechaControl.equals("") && fechaPreferencias != null && !fechaPreferencias.equals("")) {
			try {
				fechaDateControl = df.parse(fechaControl);
				fechaDatePref = df.parse(fechaPreferencias);

				if(fechaDateControl.after(fechaDatePref)){
					return true;
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		
		return false;
		

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

	/**
	 * Escribir outputstream
	 * 
	 * @param out
	 * @param str
	 * @throws IOException
	 */
	public static void writeIt(OutputStream out, String str) throws IOException {

		out.write(str.getBytes("UTF-8"));

		out.flush();

	}

	/**
	 * De string a stream
	 * 
	 * @param str
	 * @return
	 */
	public static InputStream stringToStream(String str) {

		InputStream stream = null;
		try {
			stream = new ByteArrayInputStream(str.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		return stream;

	}

	/**
	 * De string a stream
	 * 
	 * @param str
	 * @return
	 */
	public static InputStream stringToStreamIso(String str) {

		InputStream stream = null;
		try {
			stream = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		return stream;

	}
	
	
	/**
	 * Verificar si se recibe un archivo comprimido en zip
	 * 
	 * @param is
	 * @return boolean
	 */
	public static boolean isZipFile(InputStream is){
		
		boolean esZip = false;
		
		DataInputStream entrada = new DataInputStream(is);
		
		try {
			int verificar = entrada.readInt();
			
			//if(verificar == 0x504b0304){
			if(verificar == 1347093252){
				esZip = true;
			}
			
		} catch (IOException e) {			
			e.printStackTrace();
		}finally{
			
			try {
				entrada.close();
				
				is.reset();
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
			
			
			
		}
		
		
		return esZip;
	}
	
	

}
