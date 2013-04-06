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
package alberapps.java.tam.noticias;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 * Procesar lista de noticias usando JSOUP
 *
 */
public class ProcesarNoticias {

	public static String URL_SUBUS_NOTICIAS = "http://www.subus.es/Especiales/Novedades/Novedades.asp";

	public static List<Noticias> getTamNews() throws Exception {

		List<Noticias> noticias = new ArrayList<Noticias>();

		
		Document doc = Jsoup.parse(new URL(URL_SUBUS_NOTICIAS).openStream(), "ISO-8859-1", URL_SUBUS_NOTICIAS);

		String title = doc.title();

		Elements tables = doc.select("table"); // a with href

		Element tabla = tables.get(4);

		Elements filas = tabla.select("tr");

		for (int i = 0; i < filas.size(); i++) {

			if (filas.get(i).select("td").size() == 2) {
				Noticias noticia = new Noticias();

				noticia.setFecha(filas.get(i).select("td").get(0).text());

				noticia.setNoticia(filas.get(i).select("td").get(1).text());

				noticia.setLinks(new ArrayList<String>());
				noticia.setDescLink(new ArrayList<String>());

				Elements links = filas.get(i).select("td").get(1).select("a[href]");

				for (Element link : links) {

					noticia.getLinks().add(link.attr("abs:href"));
					noticia.getDescLink().add(link.text());

				}

				noticias.add(noticia);

				
				
			}

		}
		if(!noticias.isEmpty()){
			noticias.remove(0);
		}
		
		return noticias;

	}

}
