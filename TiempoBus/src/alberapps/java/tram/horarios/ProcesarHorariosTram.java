/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
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
package alberapps.java.tram.horarios;

import android.content.Context;
import android.net.Uri;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

/**
 * Procesar horarios usando JSOUP
 */
public class ProcesarHorariosTram {

    //public static String URL_TRAM_HORARIOS = "http://www.tramalicante.es/horarios.mobi.php?origen=124&destino=34&fecha=07/03/2015&hini=19:00&hfin=20:59&calcular=1";

    public static HorarioTram getHorarios(DatosConsultaHorariosTram datosConsulta, Context context) throws Exception {

        HorarioTram horarioTram = new HorarioTram();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("www.tramalicante.es").appendPath("horarios.php")
                .appendQueryParameter("origen", Integer.toString(datosConsulta.getCodEstacionOrigen()))
                .appendQueryParameter("destino", Integer.toString(datosConsulta.getCodEstacionDestino()))
                .appendQueryParameter("fecha", Utilidades.getFechaES(datosConsulta.getDiaDate()))
                .appendQueryParameter("hini", datosConsulta.getHoraDesde())
                .appendQueryParameter("hfin", datosConsulta.getHoraHasta())
                .appendQueryParameter("calcular", "1")
        ;


        Uri urlHorarios = builder.build();


        //InputStream is = Utilidades.stringToStream(Conectividad.conexionGetUtf8String(urlHorarios.toString(), true));
        InputStream is = null;

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            is = Utilidades.stringToStream(Conectividad.conexionGetUtf8String(urlHorarios.toString(), true));
        //} else {
        //    is = Utilidades.stringToStream(Conectividad.conexionGetUtf8String(urlHorarios.toString(), true, context));
        //}


        Document doc = Jsoup.parse(is, "UTF-8", urlHorarios.toString());

        //String title = doc.title();

        Elements consulta = doc.getElementsByClass("consulta");

        //Datos trayecto
        Elements ul = consulta.select("ul");

        //Datos individuales
        Elements liList = ul.select("li");

        horarioTram.setEstacionOrigen(liList.get(0).text());
        horarioTram.setEstacionDestino(liList.get(1).text());
        horarioTram.setHoras(liList.get(2).text());
        horarioTram.setDia(liList.get(3).text());
        horarioTram.setDuracion(liList.get(4).text());
        horarioTram.setTipoBillete(liList.get(5).text());

        if (liList.size() > 6) {
            horarioTram.setTransbordos(liList.get(6).text());

            Elements imgList = liList.get(6).select("img");

            for (int i = 0; i < imgList.size(); i++) {

                if (horarioTram.getLineasTransbordos() == null) {
                    horarioTram.setLineasTransbordos(new ArrayList<String>());
                }

                String linea = imgList.get(i).attr("title");

                if (!horarioTram.getLineasTransbordos().contains("L" + linea)) {
                    horarioTram.getLineasTransbordos().add("L" + linea);
                }


            }

            //Parche paradas L5
            //if(UtilidadesTRAM.esParadaExclusivaL5(Integer.toString(datosConsulta.getCodEstacionDestino()))){

//                horarioTram.getLineasTransbordos().remove("L4");

  //          }



        }

        //

        //Transbordos

        Elements spanList = consulta.select("span.texto_transbordo");

        DatoTransbordo datoTransbordo = null;

        List<DatoTransbordo> datosTransbordo = new ArrayList<>();

        for (int i = 0; i < spanList.size(); i++) {

            datoTransbordo = new DatoTransbordo();
            datoTransbordo.setDatoPaso(spanList.get(i).text());
            datosTransbordo.add(datoTransbordo);

        }


        Elements h3List = consulta.select("h3");
        for (int j = 0; j < h3List.size(); j++) {

            datosTransbordo.get(j).setTrenesDestino(h3List.get(j).text());

        }

        horarioTram.setDatosTransbordos(datosTransbordo);


        //Horarios
        Elements tableList = consulta.select("table");

        Elements tr = null;
        Elements td = null;
        List<String> fila = null;
        String filaId = "";
        String filaIdAnterior = "";


        //Recorre las tablas de horarios
        for (int i = 0; i < tableList.size(); i++) {

            //Filas de horarios
            tr = tableList.get(i).select("tr");


            horarioTram.getDatosTransbordos().get(i).setTablaHoras(new TablaHoras());
            horarioTram.getDatosTransbordos().get(i).getTablaHoras().setDatosHoras(new LinkedHashMap<String, List<String>>());

            filaIdAnterior = "";

            //Horas de cada fila
            for (int j = 0; j < tr.size(); j++) {

                fila = new ArrayList<>();
                if (!filaId.equals("") && !filaId.equals("__")) {
                    filaIdAnterior = filaId;
                }
                filaId = "";

                td = tr.get(j).select("td");

                if (td.size() > 0) {
                    for (int k = 0; k < td.size(); k++) {

                        //La primera corresponde al grupo de horas
                        if (k == 0) {
                            filaId = td.get(k).text();
                        } else {
                            fila.add(td.get(k).text());
                        }

                    }

                    if (filaId.equals("__")) {
                        //Si la nueva fila sigue siendo de la misma hora
                        horarioTram.getDatosTransbordos().get(i).getTablaHoras().getDatosHoras().get(filaIdAnterior).addAll(fila);
                    } else {
                        //Guardar la fila con su grupo
                        horarioTram.getDatosTransbordos().get(i).getTablaHoras().getDatosHoras().put(filaId, fila);
                    }

                }

            }


        }



        //TODO parche
        /*horarioTram.getLineasTransbordos().remove("L41");

        if(horarioTram.getLineasTransbordos().size() > horarioTram.getDatosTransbordos().size()){

            List<String> aux = new ArrayList<>();

            for(int i = 0; i < horarioTram.getDatosTransbordos().size(); i++){

                /*if( i==0 ) {
                    aux.add(horarioTram.getLineasTransbordos().get(0));
                }else if(i == horarioTram.getDatosTransbordos().size() - 1){
                    aux.add(horarioTram.getLineasTransbordos().get(horarioTram.getLineasTransbordos().size() - 1));
                } else {
                    aux.add("Var");
                }*/

                //aux.add(UtilidadesTRAM.getLineaHorario(horarioTram.getDatosTransbordos().get(i).ge))


            /*}

            horarioTram.setLineasTransbordos(aux);

        }*/

        return horarioTram;

    }


}
