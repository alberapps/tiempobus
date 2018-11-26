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
import android.net.Uri;
import android.os.Build;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

/**
 * Procesar lista de noticias usando JSOUP
 */
public class ProcesarNoticias {

    //public static String URL_SUBUS_NOTICIAS = "http://www.alicante.vectalia.es/alertas/";

    public static List<Noticias> getTamNews(Boolean usarCache, String userAgentDefault, Context context) throws Exception {

        List<Noticias> noticias = new ArrayList<>();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("alicante.vectalia.es");
        builder.appendPath(UtilidadesUI.getIdiomaWebSubus());
        builder.appendPath("alertas");

        String conexion = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            conexion = Conectividad.conexionGetUtf8StringUserAgent(builder.toString(), usarCache, userAgentDefault);
        } else {
            conexion = Conectividad.conexionGetUtf8StringUserAgent(builder.toString(), usarCache, userAgentDefault, context);
        }


        Document doc = Jsoup.parse(Utilidades.stringToStream(conexion), "UTF-8", builder.toString());


        //Seccion de noticias
        Elements seccionNoticias = doc.select("div.novedades_alertas");

        //Listado de noticias
        Elements noticiasList = seccionNoticias.select("div.txt_novedades_alertas");


        Noticias noticia = null;

        //Recorrer listado de noticias
        for (int i = 0; i < noticiasList.size(); i++) {

            String fecha = noticiasList.get(i).select("div.fecha_novedades").text();

            Elements seccionLink = noticiasList.get(i).select("a[href]");

            String h2 = seccionLink.get(0).select("h2").text();
            String p = seccionLink.get(0).select("p").text();

            String noticiaTexto = h2;

            String noticiaLineas = p;

            String link = seccionLink.get(0).attr("abs:href");

            noticia = new Noticias();

            String fechaDoble = null;

            if (fecha.length() > 8) {

                //del 09/03/15 al 09/06/15

                fechaDoble = fecha;

                String[] fechaString = fechaDoble.split(" ");

                fecha = fechaString[1];

            }


            noticia.setFecha(Utilidades.getFechaDateCorta(fecha));

            if (fechaDoble != null) {
                noticia.setFechaDoble(fechaDoble);
            }

            noticia.setNoticia(noticiaTexto);
            noticia.setLinks(new ArrayList<String>());
            noticia.setDescLink(new ArrayList<String>());
            noticia.getLinks().add(link);
            noticia.getDescLink().add(noticiaTexto);
            noticia.setNoticiaLineas(noticiaLineas);


            noticias.add(noticia);

        }


        Collections.sort(noticias);

        return noticias;

    }

}
