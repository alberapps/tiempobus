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

import java.util.List;

/**
 * Item de horarios
 */
public class HorarioItem {


    private String grupoHora;

    private String horas;

    private String datoInfo;

    private String linea;

    private List<String> infoRecorrido;

    private int numPasos;

    public List<String> getInfoRecorrido() {
        return infoRecorrido;
    }

    public void setInfoRecorrido(List<String> infoRecorrido) {
        this.infoRecorrido = infoRecorrido;
    }

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public String getDatoInfo() {
        return datoInfo;
    }

    public void setDatoInfo(String datoInfo) {
        this.datoInfo = datoInfo;
    }

    public String getGrupoHora() {
        return grupoHora;
    }

    public void setGrupoHora(String grupoHora) {
        this.grupoHora = grupoHora;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

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

    public int getNumPasos() {
        return numPasos;
    }

    public void setNumPasos(int numPasos) {
        this.numPasos = numPasos;
    }
}
