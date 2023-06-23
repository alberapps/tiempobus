/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2023 Alberto Montiel
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.rss.NoticiaRss;
import alberapps.java.util.Utilidades;


public class AlberappsNewsParser {


    public static List<NoticiaRss> getAlberappsNews(Boolean usarCache, String userAgentDefault, Context context) throws Exception {

        List<NoticiaRss> newsTram = new ArrayList<>();
        //https://blog.alberapps.com/feeds/posts/default?alt=rss
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("blog.alberapps.com");
        builder.appendPath("feeds");
        builder.appendPath("posts");
        builder.appendPath("default");
        builder.appendQueryParameter("alt","rss");

        ParserXML parserXML = new ParserXML();

        Noticias noticias = parserXML.parserNoticias(builder.toString(), usarCache, userAgentDefault);

        NoticiaRss newsItem = null;


        for (int i = 0; i < noticias.getNoticiasList().size(); i++) {

            newsItem = new NoticiaRss();

            try {

                //newsItem.setDate(newsList.get(i).getElementsByClass("date").text());
                newsItem.setTitulo(noticias.getNoticiasList().get(i).getTitle());

                Document doc = Jsoup.parse(Utilidades.stringToStream(noticias.getNoticiasList().get(i).getDescription()), "UTF-8", builder.toString());
                if(doc.text().contains("...")) {
                    newsItem.setDescripcion(doc.text().split("\\.\\.\\.")[0] + "...");
                } else {
                    newsItem.setDescripcion(doc.text());
                }

                newsItem.setLink(noticias.getNoticiasList().get(i).getLink());
                newsItem.setDateItem(Utilidades.getDateRss(noticias.getNoticiasList().get(i).getPubDate()));

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
