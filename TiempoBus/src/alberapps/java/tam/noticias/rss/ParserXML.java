package alberapps.java.tam.noticias.rss;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParserXML {

	public static String TITULO = "T";

	public static String LINK = "L";

	public static LinkedList<HashMap<String, String>> parsea(String urlEntrada) {

		URL url = null;

		try {
			url = new URL(urlEntrada);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		LinkedList<HashMap<String, String>> entries = new LinkedList<HashMap<String, String>>();
		HashMap<String, String> entry;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(url.openConnection().getInputStream());
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
				}
				entries.add(entry);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return entries;

	}

}
