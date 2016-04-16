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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import alberapps.java.util.Conectividad;

/**
 * Procesar avisos de la web del tram
 */
public class ProcesarAvisosTram {

    public static String URL_TRAM_AVISOS = "http://www.tramalicante.es/page.php?page=144";

    public static List<Aviso> getAvisosTram() throws Exception {

        List<Aviso> avisos = new ArrayList<>();

        Document doc = Jsoup.parse(Conectividad.conexionGetIsoStream(URL_TRAM_AVISOS), "UTF-8", URL_TRAM_AVISOS);


        //Seccion de noticias
        Elements seccionIncidencias = doc.select("div.incidencias");

        //Listado de noticias
        Elements lineasList = seccionIncidencias.select("div.linea");

        for(int i = 0; i < lineasList.size();i++){

            if(lineasList.get(i).select("div.alert").size() > 0){

                //String safe = Jsoup.clean(lineasList.get(i).html(), URL_TRAM_AVISOS, Whitelist.basic());

                Aviso aviso = new Aviso();
                aviso.setDescripcion(lineasList.get(i).text());
                avisos.add(aviso);


            }

        }

        return avisos;

    }

}
