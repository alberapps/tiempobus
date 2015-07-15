/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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
package alberapps.java.horarios.horariospdf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

/**
 * Gestion de datos del tram
 */
public class PdfHorariosBus {

    /**
     * Cotexto principal
     */
    private InfoLineasTabsPager context;

    private SharedPreferences preferencias;

    public static String URL_SUBUS_PLANOS = "http://www.alicante.subus.es/planos/";



    /**
     * Abrir pdf
     *
     * @param url
     */
    public static void abrirPdfGDocs(String url, Context context) {

        String pdf = url;

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(UtilidadesTRAM.URL_DOCS + pdf));
        context.startActivity(i);

    }


    /**
     * Noticias de tipo Aviso y Modificacion
     *
     * @param linea
     * @return link
     * @throws Exception
     */
    public static String getUrlPdfLinea(String linea, String userAgentDefault, boolean horario) throws Exception {

        InputStream st = null;

        String link = null;

        try {


            Document doc = Jsoup.parse(Utilidades.stringToStream(Conectividad.conexionGetUtf8StringUserAgent(URL_SUBUS_PLANOS, true, userAgentDefault)), "UTF-8", URL_SUBUS_PLANOS);


            //Seccion de pdf
            Element seccionNoticia = doc.select("div.planos-content").first();

            //Link secciones
            Elements linkSecciones = seccionNoticia.select("a.descarga-aviso");

            //Elements seccionLink = linkSecciones.select("a[href]");






            for(int i = 0; i< linkSecciones.size();i++) {




                //if(linkSecciones.get(i).attr("abs:href").contains("L" + linea + ".pdf")
                  //      || linkSecciones.get(i).attr("abs:href").contains("L" + linea + "sh" +".pdf")){

                if(linea.equals("11H")){
                    linea = "11";
                }else if(linea.equals("C-6*")){
                    linea="C-6";
                }

                if(linkSecciones.get(i).attr("abs:title").contains("Línea " + linea)
                        || linkSecciones.get(i).attr("abs:title").contains("Línea " + linea.replace("-",""))
                        || linkSecciones.get(i).attr("abs:title").contains("Línea " + linea.replace("C-",""))
                        || (linea.equals("TURI") && linkSecciones.get(i).attr("abs:title").contains("TURIBUS"))){



                    if (!horario){
                        link = linkSecciones.get(i).attr("abs:href");
                        break;
                    }else if(horario){
                        link = linkSecciones.get(i).attr("abs:href");
                        break;
                    }else {
                        link = null;
                    }

                }


            }




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

        return link;
    }



}
