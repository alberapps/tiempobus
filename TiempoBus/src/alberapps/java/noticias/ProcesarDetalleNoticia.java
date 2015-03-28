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

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

/**
 * Procesar el detalle de la noticia
 */
public class ProcesarDetalleNoticia {

    public static Noticias getDetalleNoticia(String url) throws Exception {

        // Distinguir distintos tipos de noticias
        /*if (url.contains("/Lineas/Horario.asp")) {
            // Tipo horario
            return getDetalleNoticiaHorarios(url);
        } else {
            // Para el resto
            return getDetalleNoticiaAvisoModificacion(url);
        }*/

        return getDetalleNoticiaNuevo(url);

    }


    /**
     * Noticias de tipo Aviso y Modificacion
     *
     * @param url
     * @return noticias
     * @throws Exception
     */
    public static Noticias getDetalleNoticiaNuevo(String url) throws Exception {

        InputStream st = null;

        Noticias noticias = null;


        try {

            Document doc = Jsoup.parse(Utilidades.stringToStream(Conectividad.conexionGetUtf8String(url, true)), "UTF-8", url);

            noticias = new Noticias();

            //Seccion de noticias
            Element seccionNoticia = doc.select("div.notificaciones-izda").first();

            //Fecha
            Elements fecha = seccionNoticia.select("div.notif-date");
            noticias.setFechaCabecera(Utilidades.getFechaStringSinHora(Utilidades.getFechaDateCorta(fecha.first().text())));

            //Titulo
            Elements titulo = seccionNoticia.select("div.notif-title");
            noticias.setTituloCabecera(titulo.first().text());

            //Lineas
            Elements lineas = seccionNoticia.select("div.notif-lineas");
            noticias.setLineaCabecera(lineas.first().text());

            //Contenido
            Elements contenido = seccionNoticia.select("div.notif-content");

            // Limpiar resultado
            String safe = Jsoup.clean(contenido.first().html(), "http://www.alicante.subus.es/notificacion/", Whitelist.basicWithImages().addTags("table", "td", "tr", "th", "thead", "tfoot", "tbody").addAttributes("td", "rowspan", "align", "colspan", "src"));

            String limpiar = safe;

            noticias.setContenidoHtml(limpiar);


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


    /**
     * Noticias de tipo Aviso y Modificacion
     *
     * @param url
     * @return noticias
     * @throws Exception
     */
    public static Noticias getDetalleNoticiaAvisoModificacion(String url) throws Exception {

        InputStream st = null;

        Noticias noticias = null;


        try {

            //st = Conectividad.conexionGetIsoStream(url);

            String datos = Conectividad.conexionGetIsoString(url);

            byte[] utf8 = datos.getBytes("UTF-8");

            //Log.d("PRUEBA UTF-8", "html: " + new String(utf8));


//            Document doc = Jsoup.parse(st, "ISO-8859-1", url);

            Document doc = Jsoup.parse(new String(utf8), url);

            noticias = new Noticias();

            // String title = doc.title();

            Elements tables = doc.select("table"); // a with href

            Element tabla = tables.get(4);

            Elements filas = tabla.select("tr");

            Element filaDetalle = filas.get(7);

            // Por si hay lineas extra
            for (int i = 7; i < filas.size(); i++) {
                if (filas.get(i).select("td").size() > 1) {
                    filaDetalle = filas.get(i);
                    break;
                }
            }

            Elements cont1 = filaDetalle.select("td");

            Element cont2 = cont1.get(1);

            // Limpiar resultado
            String safe = Jsoup.clean(cont2.html(), "http://www.subus.es/Lineas/", Whitelist.basicWithImages().addTags("table", "td", "tr", "th", "thead", "tfoot", "tbody").addAttributes("td", "rowspan", "align", "colspan", "src"));

            // Problema caracteres
            //String limpiar = safe.replace("", "-").replace("", "&euro;");
            String limpiar = safe;

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

    /**
     * Noticias del tipo horario
     *
     * @param url
     * @return noticias
     * @throws Exception
     */
    public static Noticias getDetalleNoticiaHorarios(String url) throws Exception {

        InputStream st = null;

        Noticias noticias = null;

        try {

            st = Conectividad.conexionGetIsoStream(url);

            Document doc = Jsoup.parse(st, "ISO-8859-1", url);

            noticias = new Noticias();

            // String title = doc.title();

            Elements tables = doc.select("table"); // a with href

            Element tabla = tables.get(3);

            Elements filas = tables.get(4).select("tr");

            // Element filaDetalle = filas.get(7);

            // Elements cont1 = filaDetalle.select("td");

            // Element cont2 = cont1.get(1);

            // Limpiar resultado
            String safe = Jsoup.clean(tabla.html(), Whitelist.basic().addTags("table", "td", "tr", "th", "thead", "tfoot", "tbody").addAttributes("td", "rowspan", "align", "colspan"));

            // Problema caracteres
            String limpiar = safe.replace("", "-").replace("", "&euro;");

            Log.d("NOTICIAS", "html: " + limpiar);

            noticias.setContenidoHtml(limpiar);

            // Cabecera
            // Element filaCabecera2 = filas.get(5);
            // Elements contCabecera2 = filaCabecera2.select("td");
            noticias.setFechaCabecera("");
            noticias.setTituloCabecera("");

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
