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
package alberapps.java.rss;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import alberapps.java.noticias.rss.NoticiaRss;
import alberapps.java.util.Utilidades;
import android.text.Html;

/**
 * Parsear RSS
 * 
 * 
 */
public class ParserXML {

	public static List<NoticiaRss> parsea(String urlEntrada) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		List<NoticiaRss> listaNoticias = new ArrayList<NoticiaRss>();
		NoticiaRss noticia = null;

		InputStream st = null;

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			st = Utilidades.recuperarStreamConexionSimple(urlEntrada);
			Document dom = builder.parse(st);
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("item");
			for (int i = 0; i < items.getLength(); i++) {

				noticia = new NoticiaRss();

				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j = 0; j < properties.getLength(); j++) {
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("title")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						noticia.setTitulo(textoProc);

					} else if (name.equalsIgnoreCase("link")) {

						noticia.setLink(property.getFirstChild().getNodeValue());

					} else if (name.equalsIgnoreCase("description")) {

						String textoProc = (Html.fromHtml(property.getFirstChild().getNodeValue())).toString();

						noticia.setDescripcion(textoProc);

					}

				}
				listaNoticias.add(noticia);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {

			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

		}

		return listaNoticias;

	}

}
