/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2015 Alberto Montiel
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
package alberapps.java.tam.webservice.estructura.nodosmap;

import java.io.Serializable;

public class InfoNodoMap implements Serializable {


    private String nodo;

    private String tipo;

    private String nombre;

    private String label;

    private String posx;

    private String posy;


    public InfoNodoMap() {
    }

    public InfoNodoMap(String nodo, String tipo, String nombre, String label, String posx, String posy) {
        this.nodo = nodo;
        this.tipo = tipo;
        this.nombre = nombre;
        this.label = label;
        this.posx = posx;
        this.posy = posy;
    }

    public String getNodo() {
        return nodo;
    }

    public void setNodo(String nodo) {
        this.nodo = nodo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPosx() {
        return posx;
    }

    public void setPosx(String posx) {
        this.posx = posx;
    }

    public String getPosy() {
        return posy;
    }

    public void setPosy(String posy) {
        this.posy = posy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfoNodoMap that = (InfoNodoMap) o;

        if (nodo != null ? !nodo.equals(that.nodo) : that.nodo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return nodo != null ? nodo.hashCode() : 0;
    }
}
