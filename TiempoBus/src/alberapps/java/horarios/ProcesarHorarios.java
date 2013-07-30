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
package alberapps.java.horarios;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import alberapps.java.util.Utilidades;
import android.util.Log;

/**
 * 
 * Procesar el detalle de la noticia
 * 
 */
public class ProcesarHorarios {

	public static String HORARIO_URL = "http://www.subus.es/Lineas/Horario.asp?codigo=189";
	
	public static void getDetalleHorario() throws Exception {

		InputStream st = null;

		String url = HORARIO_URL;

		try {

			st = Utilidades.recuperarStreamConexionSimple(url);

			Document doc = Jsoup.parse(st, "ISO-8859-1", url);

			//noticias = new Noticias();

			String title = doc.title();

			Elements tables = doc.select("table"); // a with href

			//Horarios lunes a viernes
			Element tabla = tables.get(3);

			Elements filas = tabla.select("tr");

			Element filaDetalle = filas.get(7);

			Elements cont1 = filaDetalle.select("td");

			Element cont2 = cont1.get(1);

			String safe = Jsoup.clean(cont2.html(), Whitelist.basic());

			// Problema caracteres
			String limpiar = safe.replace("", "-").replace("", "&euro;").replace("&nbsp;", "").trim();

			Log.d("HORARIOS", "html: " + limpiar);

			
			//Horarios sabados
			Element tabla2 = tables.get(6);

			//Elements filas2 = tabla2.select("tr");

			//Element filaDetalle2 = filas2.get(7);

			//Elements cont12 = filaDetalle2.select("td");

			//Element cont22 = cont12.get(1);

			String safe2 = Jsoup.clean(tabla2.html(), Whitelist.basic());

			// Problema caracteres
			String limpiar2 = safe2.replace("", "-").replace("", "&euro;").replace("&nbsp;", "").trim();

			Log.d("HORARIOS", "html2: " + limpiar2);
			
			
			
			
			
			
			
			
			
			
			//noticias.setContenidoHtml(limpiar);

			// Cabecera
			Element filaCabecera2 = filas.get(5);
			Elements contCabecera2 = filaCabecera2.select("td");
			//noticias.setFechaCabecera(contCabecera2.get(0).text().trim());
			//noticias.setTituloCabecera(contCabecera2.get(1).text().trim());

			// Cabecera linea
			//noticias.setLineaCabecera(filas.get(1).text().trim());

		} catch (Exception e) {
			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

			throw e;

		} finally {

			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

		}

		
	}

}
