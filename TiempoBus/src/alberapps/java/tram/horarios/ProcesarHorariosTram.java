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
package alberapps.java.tram.horarios;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;
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
 * Procesar horarios tram
 */
public class ProcesarHorariosTram {

    public static HorarioTram getHorarios(DatosConsultaHorariosTram datosConsulta, Context context) throws Exception {

        HorarioTram horarioTram = new HorarioTram();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").encodedAuthority("api.alberapps.com:8080").appendPath("tempsbusbackend")
                .appendPath("2.0")
                .appendPath("getTramSchedule")
                .appendQueryParameter("origen", Integer.toString(datosConsulta.getCodEstacionOrigen()))
                .appendQueryParameter("destino", Integer.toString(datosConsulta.getCodEstacionDestino()))
                .appendQueryParameter("dia", Utilidades.getFechaEN2(datosConsulta.getDiaDate()))
                .appendQueryParameter("horaDesde", datosConsulta.getHoraDesde())
                .appendQueryParameter("horaHasta", datosConsulta.getHoraHasta());


        Uri urlHorarios = builder.build();

        String str = Conectividad.conexionGetUtf8String(urlHorarios.toString(), true);

        JSONObject infoJson = new JSONObject(str);

        horarioTram.setEstacionOrigen(infoJson.getString("estacionOrigen"));
        horarioTram.setEstacionDestino(infoJson.getString("estacionDestino"));
        horarioTram.setHoras(infoJson.getString("horas"));
        horarioTram.setDia(infoJson.getString("dia"));
        horarioTram.setDuracion(infoJson.getString("duracion"));
        horarioTram.setTipoBillete(infoJson.getString("tipoBillete"));

        horarioTram.setTransbordos(infoJson.getString("transbordos"));


        if (horarioTram.getLineasTransbordos() == null) {
            horarioTram.setLineasTransbordos(new ArrayList<String>());
        }

        //Transbordos

        JSONArray transbordosJson = infoJson.getJSONArray("datosTransbordos");

        DatoTransbordo datoTransbordo = null;

        List<DatoTransbordo> datosTransbordo = new ArrayList<>();

        for (int i = 0; i < transbordosJson.length(); i++) {

            JSONObject transbordoJson = transbordosJson.getJSONObject(i);

            datoTransbordo = new DatoTransbordo();
            datoTransbordo.setDatoPaso(transbordoJson.getString("datoPaso"));
            datoTransbordo.setTrenesDestino(transbordoJson.getString("trenesDestino"));

            //////
            horarioTram.getLineasTransbordos().add(datoTransbordo.getTrenesDestino());


            JSONObject tablaHorasJson = transbordoJson.getJSONObject("tablaHoras");
            JSONObject datosHorasJson = tablaHorasJson.getJSONObject("datosHoras");

            datoTransbordo.setTablaHoras(new TablaHoras());
            datoTransbordo.getTablaHoras().setDatosHoras(new LinkedHashMap<String, List<String>>());

            for (int j = 0; j < datosHorasJson.names().length(); j++) {

                JSONArray valuesJson = datosHorasJson.getJSONArray(datosHorasJson.names().get(j).toString());

                List<String> data = new ArrayList<>();
                for (int n = 0; n < valuesJson.length(); n++) {

                    data.add(valuesJson.getString(n));

                }

                datoTransbordo.getTablaHoras().getDatosHoras().put(datosHorasJson.names().get(j).toString(), data);


            }

            datosTransbordo.add(datoTransbordo);

        }

        horarioTram.setDatosTransbordos(datosTransbordo);

        return horarioTram;

    }


}
