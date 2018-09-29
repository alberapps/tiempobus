/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p>
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.java.horarios;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import alberapps.java.tam.BusLinea;
import alberapps.java.tam.DatosTam;
import alberapps.java.util.Conectividad;

/**
 * Procesar horarios
 */
public class ProcesarHorariosJson {


    public static DatosHorarios getDetalleHorario(BusLinea datosLinea) throws Exception {

        String str = null;

        DatosHorarios datosHorario = null;

        HorariosData horariosData = null;


        try {

            horariosData = new HorariosData();

            Uri.Builder builder = Uri.parse(DatosTam.URL_SERVIDOR_ESTRUCTURA_HORARIOS).buildUpon();
            builder.appendQueryParameter("linea", datosLinea.getNumLinea());
            builder.build();

            str = Conectividad.conexionGetUtf8String(builder.toString(), true);

            JSONObject forecastJson = new JSONObject(str);
            JSONObject horariosDataJson = forecastJson.getJSONObject("horariosData");
            JSONArray gruposDiasJson = horariosDataJson.getJSONArray("gruposDias");

            for (int i = 0; i < gruposDiasJson.length()-1; i++) {
                JSONObject grupoDiasJson = gruposDiasJson.getJSONObject(i);


                GruposDias gruposDias = new GruposDias();
                gruposDias.setGrupoDias(grupoDiasJson.getString("grupoDias"));
                gruposDias.setDescIda(grupoDiasJson.getString("descIda"));
                gruposDias.setDescVuelta(grupoDiasJson.getString("descVuelta"));

                JSONArray listaIdaJson = grupoDiasJson.getJSONArray("listaIda");

                for (int j = 0; j < listaIdaJson.length()-1; j++) {
                    JSONObject elem = listaIdaJson.getJSONObject(j);

                        gruposDias.getListaIda().add(elem.getString("hora"));

                }

                JSONArray listaVueltaJson = grupoDiasJson.getJSONArray("listaVuelta");

                for (int j = 0; j < listaVueltaJson.length()-1; j++) {
                    JSONObject elem = listaVueltaJson.getJSONObject(j);

                        gruposDias.getListaVuelta().add(elem.getString("hora"));

                }


                horariosData.getGruposDias().add(gruposDias);

            }

            if (horariosData.getGruposDias().size() > 0) {

                datosHorario = new DatosHorarios();

                datosHorario.setTituloSalidaIda(horariosData.getGruposDias().get(0).getDescIda());
                datosHorario.setTituloSalidaVuelta(horariosData.getGruposDias().get(0).getDescVuelta());

                List<Horario> horarios = new ArrayList<>();
                List<Horario> horariosVuelta = new ArrayList<>();

                for (int i = 0; i < horariosData.getGruposDias().size(); i++) {

                    Horario horario = new Horario();
                    horario.setTituloHorario(horariosData.getGruposDias().get(i).getGrupoDias());
                    horario.setLinkHorario("alicante.vectalia.es");
                    horario.setHorarios(new ArrayList<>());
                    horario.getHorarios().addAll(horariosData.getGruposDias().get(i).getListaIda());
                    horarios.add(horario);

                    Horario horarioVuelta = new Horario();
                    horarioVuelta.setTituloHorario(horariosData.getGruposDias().get(i).getGrupoDias());
                    horarioVuelta.setLinkHorario("alicante.vectalia.es");
                    horarioVuelta.setHorarios(new ArrayList<>());
                    horarioVuelta.getHorarios().addAll(horariosData.getGruposDias().get(i).getListaVuelta());
                    horariosVuelta.add(horarioVuelta);

                }

                datosHorario.setHorarios(horarios);
                datosHorario.setHorariosVuelta(horariosVuelta);

            }


        } catch (Exception e) {

            Log.d("HORARIOS", "Error en procesado de horarios");

            throw e;

        }

        return datosHorario;
    }


}
