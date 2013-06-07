package alberapps.java.rss;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import alberapps.java.tam.noticias.rss.NoticiaRss;
import android.text.Html;

public class ParserXML {

	
	public static List<NoticiaRss> parsea(String urlEntrada) {

		URL url = null;

		try {
			url = new URL(urlEntrada);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		List<NoticiaRss> listaNoticias = new ArrayList<NoticiaRss>();
		NoticiaRss noticia = null;

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(url.openConnection().getInputStream());
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
		}

		return listaNoticias;

	}

}
