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
package alberapps.java.tram.avisos;

import android.net.Uri;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.util.Conectividad;

/**
 * Procesar avisos de la web del tram
 */
public class ProcesarAvisosTram {

    public static String URL_TRAM_AVISOS = "http://www.tramalicante.es/page.php?page=144";

    public static List<Aviso> getAvisosTram() throws Exception {

        List<Aviso> avisos = new ArrayList<>();

        String idioma = UtilidadesUI.getIdiomaRssTram();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("www.tramalicante.es").appendPath("page.php")
                .appendQueryParameter("page", "144")
                .appendQueryParameter("idioma", idioma);

        Uri urlNoticias = builder.build();


        Document doc = Jsoup.parse(Conectividad.conexionGetIsoStream(urlNoticias.toString()), "UTF-8", urlNoticias.toString());


        //Seccion de noticias
        Elements seccionIncidencias = doc.select("div.incidencias");

        //Listado de noticias
        Elements lineasList = seccionIncidencias.select("div.linea");

        for (int i = 0; i < lineasList.size(); i++) {

            if (lineasList.get(i).select("div.alert").size() > 0) {

                //String safe = Jsoup.clean(lineasList.get(i).html(), URL_TRAM_AVISOS, Whitelist.basicWithImages());
                String safe = Jsoup.clean(lineasList.get(i).html(), urlNoticias.toString(), Safelist.basicWithImages().addTags("h3", "h4", "table", "td", "tr", "th", "thead", "tfoot", "tbody").addAttributes("td", "rowspan", "align", "colspan", "src"));


                Aviso aviso = new Aviso();
                aviso.setDescripcion(safe);
                if (i == 0) {
                    aviso.setTitulo("L1");
                } else if (i == 1) {
                    aviso.setTitulo("L2");
                } else if (i == 2) {
                    aviso.setTitulo("L3");
                } else if (i == 3) {
                    aviso.setTitulo("L4");
                } else if (i == 4) {
                    aviso.setTitulo("L5");
                } else if (i == 5) {
                    aviso.setTitulo("L9");
                }
                avisos.add(aviso);


            }

        }

        return avisos;

    }

}
