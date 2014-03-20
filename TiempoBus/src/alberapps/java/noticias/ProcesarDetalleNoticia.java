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
package alberapps.java.noticias;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import alberapps.java.util.Conectividad;
import android.util.Log;

/**
 * 
 * Procesar el detalle de la noticia
 * 
 */
public class ProcesarDetalleNoticia {

	public static Noticias getDetalleNoticia(String url) throws Exception {

		InputStream st = null;

		Noticias noticias = null;

		try {

			st = Conectividad.conexionGetIsoStream(url);

			Document doc = Jsoup.parse(st, "ISO-8859-1", url);

			noticias = new Noticias();

			String title = doc.title();

			Elements tables = doc.select("table"); // a with href

			Element tabla = tables.get(4);

			Elements filas = tabla.select("tr");

			Element filaDetalle = filas.get(7);

			Elements cont1 = filaDetalle.select("td");

			Element cont2 = cont1.get(1);

			String safe = Jsoup.clean(cont2.html(), Whitelist.basic());

			// Problema caracteres
			String limpiar = safe.replace("", "-").replace("", "&euro;");

			Log.d("NOTICIAS", "html: " + limpiar);

			noticias.setContenidoHtml(limpiar);

			// Cabecera
			Element filaCabecera2 = filas.get(5);
			Elements contCabecera2 = filaCabecera2.select("td");
			noticias.setFechaCabecera(contCabecera2.get(0).text().trim());
			noticias.setTituloCabecera(contCabecera2.get(1).text().trim());

			// Cabecera linea
			noticias.setLineaCabecera(filas.get(1).text().trim());

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

		return noticias;
	}

}
