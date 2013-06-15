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
package alberapps.java.tam.lineas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.protocol.HTTP;

import alberapps.java.tam.BusLinea;
import alberapps.java.util.Utilidades;

/**
 * 
 * Procesa los datos recuperados de las lineas
 * 
 */
public class ProcesarLineasService {

	public static final String URL_DATOS = "http://tiempobus.googlecode.com/svn/alberapps/trunk/InfoLineasBus/lineas/lineas.txt";

	
	/**
	 * Parsear datos lineas
	 * 
	 * @param url
	 * @return
	 */
	public static List<DatosLinea> getDatosLineas(String url) {

		List<DatosLinea> datosLineas = null;

		InputStream is = null;

		try {

			is = Utilidades.recuperarStreamConexionSimple(url);

			if (is != null) {

				BufferedReader input = new BufferedReader(new InputStreamReader(is, HTTP.UTF_8));

				String l = "";

				List<String> lineasFichero = new ArrayList<String>();

				while ((l = input.readLine()) != null) {

					lineasFichero.add(l);

				}

				datosLineas = procesa(lineasFichero);

			} else {
				datosLineas = null;
			}

		} catch (Exception e) {

			datosLineas = null;
			
		} finally {
			try {
				if(is != null){
					is.close();
				}
			} catch (IOException e) {

			}
		}

		return datosLineas;
	}

	/*
	 * public static List<DatosLinea> getDatosLineas(String url) {
	 * 
	 * List<DatosLinea> datosLineas = null; try { final URL aUrl = new URL(url);
	 * final URLConnection conn = aUrl.openConnection(); conn.setReadTimeout(15
	 * * 1000); // timeout for reading the google // maps data: 15 secs
	 * conn.connect();
	 * 
	 * String charset = "utf-8";
	 * 
	 * BufferedReader input = new BufferedReader(new
	 * InputStreamReader(conn.getInputStream(), charset));
	 * 
	 * String l = "";
	 * 
	 * List<String> lineasFichero = new ArrayList<String>();
	 * 
	 * while ((l = input.readLine()) != null) {
	 * 
	 * lineasFichero.add(l);
	 * 
	 * }
	 * 
	 * conn.getInputStream().close();
	 * 
	 * datosLineas = procesa(lineasFichero);
	 * 
	 * } catch (Exception e) {
	 * 
	 * datosLineas = null; } finally {
	 * 
	 * }
	 * 
	 * return datosLineas; }
	 */
	/**
	 * 
	 * @param lineasFichero
	 * @return
	 */
	private static List<DatosLinea> procesa(List<String> lineasFichero) {

		if (lineasFichero != null && !lineasFichero.isEmpty() && lineasFichero.size() >= 3) {

			List<DatosLinea> datos = new ArrayList<DatosLinea>();

			DatosLinea dat = null;

			String[] lineasDescrip = lineasFichero.get(0).split(";;");
			String[] lineasCodigoKML = lineasFichero.get(1).split(";;");
			String[] lineasNum = lineasFichero.get(2).split(";;");

			// Controles
			if ((lineasDescrip.length == lineasCodigoKML.length) && (lineasCodigoKML.length == lineasNum.length)) {

				for (int i = 0; i < lineasDescrip.length; i++) {

					dat = new DatosLinea();
					dat.setLineaDescripcion(lineasDescrip[i]);
					dat.setLineaCodigoKML(lineasCodigoKML[i]);
					dat.setLineaNum(lineasNum[i]);

					datos.add(dat);

				}

				return datos;

			} else {
				return null;
			}

		} else {
			return null;
		}

	}

	/**
	 * Mapea los datos recuperados a la anterior estructura
	 * 
	 * @param datos
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<BusLinea> getLineasBus() throws IOException {

		ArrayList<BusLinea> lineasBus = new ArrayList<BusLinea>();

		List<DatosLinea> datosRecuperados = getDatosLineas(URL_DATOS);

		if (datosRecuperados != null && !datosRecuperados.isEmpty()) {

			// Datos recuperados con exito
			for (int i = 0; i < datosRecuperados.size(); i++) {

				lineasBus.add(new BusLinea(datosRecuperados.get(i).getLineaCodigoKML(), datosRecuperados.get(i).getLineaDescripcion(), datosRecuperados.get(i).getLineaNum(),null));

			}
		} else {
			return null;

		}

		return lineasBus;
	}

}
