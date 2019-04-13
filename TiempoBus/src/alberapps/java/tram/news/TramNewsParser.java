/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2019 Alberto Montiel
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
package alberapps.java.tram.news;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.Noticias;
import alberapps.java.noticias.rss.NoticiaRss;
import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;


public class TramNewsParser {


    public static List<NoticiaRss> getTramNews(Boolean usarCache, String userAgentDefault, Context context) throws Exception {

        List<NoticiaRss> newsTram = new ArrayList<>();

        //https://www.tramalicante.es/page.php?page=199&idioma=_es
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("www.tramalicante.es");
        builder.appendPath("page.php");
        builder.appendQueryParameter("page", "199");
        builder.appendQueryParameter("idioma", UtilidadesUI.getIdiomaRssTram());

        String basePath = "https://www.tramalicante.es";

        String conexion = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            conexion = Conectividad.conexionGetUtf8StringUserAgent(builder.toString(), usarCache, userAgentDefault);
        } else {
            conexion = Conectividad.conexionGetUtf8StringUserAgent(builder.toString(), usarCache, userAgentDefault, context);
        }


        Document doc = Jsoup.parse(Utilidades.stringToStream(conexion), "UTF-8", builder.toString());


        Element newsElement = doc.getElementById("news");

        Elements newsList = newsElement.getElementsByClass("new");

        NoticiaRss newsItem = null;


        for (int i = 0; i < newsList.size(); i++) {

            newsItem = new NoticiaRss();

            try {

                newsItem.setDate(newsList.get(i).getElementsByClass("date").text());
                newsItem.setTitulo(newsList.get(i).getElementsByTag("h2").get(0).text());
                newsItem.setDescripcion(newsList.get(i).getElementsByTag("h3").get(0).text());
                newsItem.setLink(basePath + newsList.get(i).getElementsByTag("h2").get(0).getElementsByTag("a").get(0).attr("href"));
                newsItem.setDateItem(Utilidades.getDateRss(newsItem.getDate()));

            } catch (Exception e) {
                e.printStackTrace();
                newsItem.setDate("");
                newsItem.setTitulo("Info error");
                newsItem.setDescripcion("");
                newsItem.setLink("");
            }

            newsTram.add(newsItem);

        }

        //Collections.sort(noticias);

        return newsTram;

    }

}
