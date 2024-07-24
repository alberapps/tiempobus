/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.java.noticias;

import android.content.Context;
import android.os.Build;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

/**
 * Procesar el detalle de la noticia
 */
public class ProcesarDetalleNoticia {

    public static Noticias getDetalleNoticia(String url, String userAgent, Context context) throws Exception {

        return getDetalleNoticiaNuevo(url, userAgent, context);

    }


    /**
     * Noticias de tipo Aviso y Modificacion
     *
     * @param url
     * @return noticias
     * @throws Exception
     */
    public static Noticias getDetalleNoticiaNuevo(String url, String userAgentDefault, Context context) throws Exception {

        InputStream st = null;

        Noticias noticias = null;


        try {

            //String conexion = null;

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                conexion = Conectividad.conexionGetUtf8StringUserAgent(url, true, userAgentDefault);
            } else {
                conexion = Conectividad.conexionGetUtf8StringUserAgent(url, true, userAgentDefault, context);
            }


            Document doc = Jsoup.parse(Utilidades.stringToStream(conexion), "UTF-8", url);*/

            Document doc = Jsoup.connect(url).timeout(10000)
                    .header("Cache-Control", "no-cache")
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("Accept-Encoding", "gzip, deflate, br, zstd")
                    .header("Connection", "keep-alive")
                    .userAgent(userAgentDefault).get();

            noticias = new Noticias();

            //Seccion de noticias
            Element seccionNoticia = doc.select("div.notificaciones-izda").first();

            //Fecha
            Elements fecha = seccionNoticia.select("div.notif-date");

            String fechaTexto = fecha.first().text();

            String fechaTextoSalida = null;

            if (fechaTexto.length() > 8) {

                //del 09/03/15 al 09/06/15

                fechaTextoSalida = fechaTexto;

            } else {

                fechaTextoSalida = Utilidades.getFechaStringSinHora(Utilidades.getFechaDateCorta(fechaTexto));

            }


            noticias.setFechaCabecera(fechaTextoSalida);

            //Titulo
            Elements titulo = seccionNoticia.select("div.notif-title");
            noticias.setTituloCabecera(titulo.first().text());

            //Lineas
            Elements lineas = seccionNoticia.select("div.notif-lineas");
            noticias.setLineaCabecera(lineas.first().text());

            //Contenido
            Elements contenido = seccionNoticia.select("div.notif-content");

            // Limpiar resultado
            String safe = Jsoup.clean(contenido.first().html(), "https://alicante.vectalia.es/notificacion/",
                    Safelist.basicWithImages().addTags("table", "td", "tr", "th", "thead", "tfoot", "tbody", "span", "div")
                            .addAttributes("td", "rowspan", "align", "colspan", "src")
                            .addAttributes("span", "style")
                            .addAttributes("div", "style"));

            String limpiar = safe.replaceAll("<img", "<img style=\"max-width: 100%; height:auto\"")
                    .replaceAll("<p>&nbsp;</p>", "").replaceAll("<p><strong>&nbsp;</strong></p>", "");

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


}
