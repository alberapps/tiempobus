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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import alberapps.java.util.Conectividad;

/**
 * Procesa los datos recuperados de las lineas
 */
public class ProcesarDatosLineasService {

    public static final String URL_SUBUS_LINEAS = "http://www.subus.es/Lineas/MapaMapsAgregadorZoom.asp";

    public static void getLineasInfo() throws Exception {

        // List<Noticias> noticias = new ArrayList<Noticias>();

        InputStream st = Conectividad.conexionGetIsoStream(URL_SUBUS_LINEAS);

        Document doc = Jsoup.parse(st, "ISO-8859-1", URL_SUBUS_LINEAS);

        String title = doc.title();

        Elements tables = doc.select("table"); // a with href

        Element tablaGlobal = tables.get(8);

        DatosLinea datosLinea = new DatosLinea();

        List<DatosLinea> lineas = new ArrayList<DatosLinea>();

        String tituloCabecera = "";
        String lineaDesc = "";
        String lineaNum = "";

        // Grupo
        // Element tablaAlicanteUrbano =
        // tables.get(8).select("table").get(1).select("table").get(0);

        // Titulo cabecera grupo
        tituloCabecera = tables.get(8).select("table").get(1).select("table").get(0).select("td.cabeza").text();

        for (int i = 0; i < tables.get(8).select("table").get(1).select("table").get(0).select("td.enlace").select("a").size(); i++) {

            // Descripcion linea
            lineaDesc = tables.get(8).select("table").get(1).select("table").get(0).select("td.enlace").select("a").get(i).attr("title");

            // Numero linea
            lineaNum = tables.get(8).select("table").get(1).select("table").get(0).select("td.enlace").select("a").get(i).text();

            datosLinea = new DatosLinea();
            datosLinea.setTituloCabecera(tituloCabecera);
            datosLinea.setLineaDescripcion(lineaDesc);
            datosLinea.setLineaNum(lineaNum);

            lineas.add(datosLinea);
        }

        // urbano : tables.get(8).select("table").get(1).select("table").get(0)

        // titulo urbano:
        // tables.get(8).select("table").get(1).select("table").get(0).select("td.cabeza")

        // elementos:
        // tables.get(8).select("table").get(1).select("table").get(0).select("td.enlace")

        // links valor:
        // tables.get(8).select("table").get(1).select("table").get(0).select("td.enlace").select("a")

        // textos lineas:
        // tables.get(8).select("table").get(1).select("table").get(0).select("td.enlace").select("a")

        // titulo
        // tables.get(8).select("table").get(1).select("table").get(0).select("td.enlace").select("a").get(0).attr("title")

        // javascript link :
        // tables.get(8).select("table").get(1).select("table").get(0).select("td.enlace").select("input").get(0).attr("onclick")

        // tables.get(8).select("table").get(1).select("table").get(1) elemento

		/*
         * Elements filas = tabla.select("tr");
		 * 
		 * for (int i = 0; i < filas.size(); i++) {
		 * 
		 * if (filas.get(i).select("td").size() == 2) { Noticias noticia = new
		 * Noticias();
		 * 
		 * noticia.setFecha(filas.get(i).select("td").get(0).text());
		 * 
		 * noticia.setNoticia(filas.get(i).select("td").get(1).text());
		 * 
		 * noticia.setLinks(new ArrayList<String>()); noticia.setDescLink(new
		 * ArrayList<String>());
		 * 
		 * Elements links = filas.get(i).select("td").get(1).select("a[href]");
		 * 
		 * for (Element link : links) {
		 * 
		 * noticia.getLinks().add(link.attr("abs:href"));
		 * noticia.getDescLink().add(link.text());
		 * 
		 * }
		 * 
		 * noticias.add(noticia);
		 * 
		 * 
		 * 
		 * }
		 * 
		 * } if(!noticias.isEmpty()){ noticias.remove(0); }
		 * 
		 * return noticias;
		 */
    }

}
