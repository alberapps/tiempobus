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

import java.util.ArrayList;
import java.util.List;

/**
 * Datos transbordos
 */
public class DatoTransbordo {


    private String datoPaso;

    private String trenesDestino;

    private TablaHoras tablaHoras;

    private boolean errorServicio;

    private boolean sinDatos;

    public boolean isSinDatos() {
        return sinDatos;
    }

    public void setSinDatos(boolean sinDatos) {
        this.sinDatos = sinDatos;
    }

    public boolean isErrorServicio() {
        return errorServicio;
    }

    public void setErrorServicio(boolean errorServicio) {
        this.errorServicio = errorServicio;
    }

    public TablaHoras getTablaHoras() {
        return tablaHoras;
    }

    public void setTablaHoras(TablaHoras tablaHoras) {
        this.tablaHoras = tablaHoras;
    }

    public String getDatoPaso() {
        return datoPaso;
    }

    public void setDatoPaso(String datoPaso) {
        this.datoPaso = datoPaso;
    }

    public String getTrenesDestino() {
        return trenesDestino;
    }

    public void setTrenesDestino(String trenesDestino) {
        this.trenesDestino = trenesDestino;
    }


    public List<HorarioItem> getHorariosItem() {

        List<HorarioItem> horarioList = null;

        if (isSinDatos()) {
            horarioList = new ArrayList<HorarioItem>();
            HorarioItem horario = new HorarioItem();
            horario.setSinDatos(true);
            horarioList.add(horario);

        } else if (isErrorServicio()) {
            horarioList = new ArrayList<HorarioItem>();
            HorarioItem horario = new HorarioItem();
            horario.setErrorServicio(true);
            horarioList.add(horario);
        } else if (getTablaHoras() != null && getTablaHoras().getDatosHoras() != null) {

            horarioList = new ArrayList<HorarioItem>();

            HorarioItem horarioItem = null;

            List<String> keys = new ArrayList<String>();
            keys.addAll(getTablaHoras().getDatosHoras().keySet());

            List<String> listaHoras = null;

            for (int i = 0; i < keys.size(); i++) {

                horarioItem = new HorarioItem();

                horarioItem.setDatoInfo(datoPaso);

                //Grupo
                horarioItem.setGrupoHora(keys.get(i));

                //Horas
                listaHoras = getTablaHoras().getDatosHoras().get(keys.get(i));

                StringBuffer sb = new StringBuffer("");

                for (int j = 0; j < listaHoras.size(); j++) {

                    if (listaHoras.get(j) != null && !listaHoras.get(j).equals("") && !listaHoras.get(j).equals("---")) {

                        if (j > 0) {
                            sb.append(" ");
                        }

                        sb.append(listaHoras.get(j));

                    }


                }

                horarioItem.setHoras(sb.toString());

                horarioList.add(horarioItem);


            }


        }


        return horarioList;

    }


}
