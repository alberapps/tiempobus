/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
package alberapps.java.horarios;

import java.util.ArrayList;
import java.util.List;

public class DatosHorarios {

    private String tituloSalidaIda;

    private String tituloSalidaVuelta;

    private String validezHorarios;

    private StringBuffer comentariosIda;

    private StringBuffer comentariosVuelta;

    private List<Horario> horariosIda = new ArrayList<Horario>();

    private List<Horario> horariosVuelta = new ArrayList<Horario>();

    public List<Horario> getHorariosIda() {
        return horariosIda;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horariosIda = horarios;
    }

    public List<Horario> getHorariosVuelta() {
        return horariosVuelta;
    }

    public void setHorariosVuelta(List<Horario> horariosVuelta) {
        this.horariosVuelta = horariosVuelta;
    }

    public String getTituloSalidaIda() {
        return tituloSalidaIda;
    }

    public void setTituloSalidaIda(String tituloSalidaIda) {
        this.tituloSalidaIda = tituloSalidaIda;
    }

    public String getTituloSalidaVuelta() {
        return tituloSalidaVuelta;
    }

    public void setTituloSalidaVuelta(String tituloSalidaVuelta) {
        this.tituloSalidaVuelta = tituloSalidaVuelta;
    }

    public String getValidezHorarios() {
        return validezHorarios;
    }

    public void setValidezHorarios(String validezHorarios) {
        this.validezHorarios = validezHorarios;
    }

    public StringBuffer getComentariosIda() {
        return comentariosIda;
    }

    public void setComentariosIda(StringBuffer comentariosIda) {
        this.comentariosIda = comentariosIda;
    }

    public StringBuffer getComentariosVuelta() {
        return comentariosVuelta;
    }

    public void setComentariosVuelta(StringBuffer comentariosVuelta) {
        this.comentariosVuelta = comentariosVuelta;
    }


}
