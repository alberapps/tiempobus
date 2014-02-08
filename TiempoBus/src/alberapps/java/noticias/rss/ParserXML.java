package alberapps.java.noticias.rss;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import alberapps.java.util.Conectividad;

public class ParserXML {

	public static String TITULO = "T";

	public static String LINK = "L";

	public static LinkedList<HashMap<String, String>> parsea(String urlEntrada) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		LinkedList<HashMap<String, String>> entries = new LinkedList<HashMap<String, String>>();
		HashMap<String, String> entry;
		InputStream st = null;
		try {

			st = Conectividad.conexionGetIsoStream(urlEntrada);

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(st);
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("item");
			for (int i = 0; i < items.getLength(); i++) {
				entry = new HashMap<String, String>();
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				for (int j = 0; j < properties.getLength(); j++) {
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("title")) {
						entry.put(TITULO, property.getFirstChild().getNodeValue());
					} else if (name.equalsIgnoreCase("link")) {
						entry.put(LINK, property.getFirstChild().getNodeValue());
					}

					// description
				}
				entries.add(entry);
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

		return entries;

	}

}
