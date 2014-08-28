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

public class Horario {

    private String linkHorario = "";

    private String tituloHorario = "";

    private List<String> horarios = new ArrayList<String>();

    public String getLinkHorario() {
        return linkHorario;
    }

    public void setLinkHorario(String linkHorario) {
        this.linkHorario = linkHorario;
    }

    public String getTituloHorario() {
        return tituloHorario;
    }

    public void setTituloHorario(String tituloHorario) {
        this.tituloHorario = tituloHorario;
    }

    public List<String> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<String> horarios) {
        this.horarios = horarios;
    }


}
