/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2018 Alberto Montiel
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

public class GruposDias {

    private String grupoDias;

    private String descIda;

    private String descVuelta;

    private List<String> listaIda;

    private List<String> listaVuelta;


    public GruposDias() {

        listaIda = new ArrayList<>();
        listaVuelta = new ArrayList<>();

    }

    public String getGrupoDias() {
        return grupoDias;
    }

    public void setGrupoDias(String grupoDias) {
        this.grupoDias = grupoDias;
    }

    public String getDescIda() {
        return descIda;
    }

    public void setDescIda(String descIda) {
        this.descIda = descIda;
    }

    public String getDescVuelta() {
        return descVuelta;
    }

    public void setDescVuelta(String descVuelta) {
        this.descVuelta = descVuelta;
    }

    public List<String> getListaIda() {
        return listaIda;
    }

    public void setListaIda(List<String> listaIda) {
        this.listaIda = listaIda;
    }

    public List<String> getListaVuelta() {
        return listaVuelta;
    }

    public void setListaVuelta(List<String> listaVuelta) {
        this.listaVuelta = listaVuelta;
    }
}
