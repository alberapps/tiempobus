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
package alberapps.java.tam.mapas;

import java.io.Serializable;

public class PlaceMark implements Serializable, Comparable<PlaceMark> {

    /**
     *
     */
    private static final long serialVersionUID = 5874866588234797674L;

    private String title;
    private String description;
    private String coordinates;
    private String address;

    private String codigoParada;
    private String sentido;
    private String lineas;

    private String observaciones;

    private Integer orden = 0;

    public String getObservaciones() {

        if (observaciones != null) {
            return observaciones;
        } else {
            return " ";
        }

    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCodigoParada() {
        return codigoParada;
    }

    public void setCodigoParada(String codigoParada) {
        this.codigoParada = codigoParada;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public String getLineas() {
        return lineas;
    }

    public void setLineas(String lineas) {
        this.lineas = lineas;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((codigoParada == null) ? 0 : codigoParada.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PlaceMark other = (PlaceMark) obj;
        if (codigoParada == null) {
            if (other.codigoParada != null)
                return false;
        } else if (!codigoParada.equals(other.codigoParada))
            return false;
        return true;
    }

    public int compareTo(PlaceMark another) {

        //Integer c1 = Integer.parseInt(this.getCodigoParada());
        //Integer c2 = Integer.parseInt(another.getCodigoParada());

        //return c1.compareTo(c2);

        return orden.compareTo(another.orden);

    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

}
