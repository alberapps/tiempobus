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
 * Horarios TRAM
 */
public class HorarioTram {

    private String estacionOrigen;

    private String estacionDestino;

    private String horas;

    private String dia;

    private String duracion;

    private String tipoBillete;

    private String transbordos;

    private List<String> lineasTransbordos;

    private List<DatoTransbordo> datosTransbordos;

    public List<String> getLineasTransbordos() {
        return lineasTransbordos;
    }

    public void setLineasTransbordos(List<String> lineasTransbordos) {
        this.lineasTransbordos = lineasTransbordos;
    }


    public List<DatoTransbordo> getDatosTransbordos() {
        return datosTransbordos;
    }

    public void setDatosTransbordos(List<DatoTransbordo> datosTransbordos) {
        this.datosTransbordos = datosTransbordos;
    }


    public String getEstacionOrigen() {
        return estacionOrigen;
    }

    public void setEstacionOrigen(String estacionOrigen) {
        this.estacionOrigen = estacionOrigen;
    }

    public String getEstacionDestino() {
        return estacionDestino;
    }

    public void setEstacionDestino(String estacionDestino) {
        this.estacionDestino = estacionDestino;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getTipoBillete() {
        return tipoBillete;
    }

    public void setTipoBillete(String tipoBillete) {
        this.tipoBillete = tipoBillete;
    }

    public String getTransbordos() {
        return transbordos;
    }

    public void setTransbordos(String transbordos) {
        this.transbordos = transbordos;
    }

    public List<HorarioItem> getHorariosItemCombinados() {

        return getHorariosItemCombinados(0);
    }

    public List<HorarioItem> getHorariosItemCombinados(Integer paso) {

        List<HorarioItem> listado = new ArrayList<>();

        if (datosTransbordos != null && !datosTransbordos.isEmpty() && datosTransbordos.size() > paso && !datosTransbordos.get(0).isErrorServicio() && !datosTransbordos.get(0).isSinDatos()) {

            HorarioItem info = new HorarioItem();
            info.setInfoRecorrido(new ArrayList<String>());

            String[] duracion1 = duracion.split(":");
            String[] tipoBillete1 = tipoBillete.split(":");

            if (duracion1.length > 1) {
                info.getInfoRecorrido().add(duracion1[1].trim());
            }
            if (tipoBillete1.length > 1) {
                info.getInfoRecorrido().add(tipoBillete1[1].trim());
            }


            if (lineasTransbordos != null && !lineasTransbordos.isEmpty()) {

                StringBuffer transbordos = new StringBuffer("");

                for (int i = 0; i < lineasTransbordos.size(); i++) {

                    if (i > 0) {
                        transbordos.append(" -> ");
                    }

                    transbordos.append(lineasTransbordos.get(i));

                }

                info.getInfoRecorrido().add(transbordos.toString());

                info.setNumPasos(datosTransbordos.size());

            }


            listado.add(info);

        }

        //for (int i = 0; i < datosTransbordos.size(); i++) {

        if (datosTransbordos != null && !datosTransbordos.isEmpty() && datosTransbordos.size() > paso) {

            List<HorarioItem> items = datosTransbordos.get(paso).getHorariosItem();



            for (int j = 0; j < items.size(); j++) {

                if (lineasTransbordos != null && !lineasTransbordos.isEmpty() && lineasTransbordos.size() > paso) {
                    items.get(j).setLinea(lineasTransbordos.get(paso));
                } else {
                    items.get(j).setLinea("");
                }
            }

            //lineasTransbordos.remove("L4L");


            //items.get(items.size() - 1).setLinea(lineasTransbordos.get(lineasTransbordos.size() - 1));

            /*for (int j = 0; j < items.size(); j++) {

                if(j == 0) {
                    items.get(0).setLinea(lineasTransbordos.get(0));
                }else if(j == items.size()-1) {
                    items.get(items.size() - 1).setLinea(lineasTransbordos.get(lineasTransbordos.size() - 1));
                }else {

                    if(lineasTransbordos.size() == datosTransbordos.size()) {

                        if (lineasTransbordos != null && !lineasTransbordos.isEmpty() && lineasTransbordos.size() > paso) {
                            items.get(j).setLinea(lineasTransbordos.get(paso));
                        } else {
                            items.get(j).setLinea("");
                        }

                    } else {
                        items.get(j).setLinea("");
                    }

                }



            }*/



            listado.addAll(items);

        }
        //}

        if(listado.isEmpty()){
            return null;
        }

        return listado;


    }


}
