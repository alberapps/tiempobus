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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import alberapps.java.tam.webservice.vehiculos.InfoVehiculo;

public class DatosMapa implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 623708931441052658L;

    private List<PlaceMark> placemarks = new ArrayList<PlaceMark>();
    private PlaceMark currentPlacemark;
    private PlaceMark routePlacemark;

    private List<PlaceMark> placemarksInversa = new ArrayList<PlaceMark>();

    List<InfoVehiculo> vehiculosList = new ArrayList<InfoVehiculo>();

    // Recorrido
    private String recorrido;

    public String getRecorrido() {
        return recorrido;
    }

    public void setRecorrido(String recorrido) {
        this.recorrido = recorrido;
    }

    public String toString() {
        String s = "";
        for (Iterator<PlaceMark> iter = placemarks.iterator(); iter.hasNext(); ) {
            PlaceMark p = (PlaceMark) iter.next();
            s += p.getTitle() + "\n" + p.getDescription() + "\n\n";
        }
        return s;
    }

    public void addCurrentPlacemark() {
        placemarks.add(currentPlacemark);
    }

    public List<PlaceMark> getPlacemarks() {
        return placemarks;
    }

    public void setPlacemarks(List<PlaceMark> placemarks) {
        this.placemarks = placemarks;
    }

    public PlaceMark getCurrentPlacemark() {
        return currentPlacemark;
    }

    public void setCurrentPlacemark(PlaceMark currentPlacemark) {
        this.currentPlacemark = currentPlacemark;
    }

    public PlaceMark getRoutePlacemark() {
        return routePlacemark;
    }

    public void setRoutePlacemark(PlaceMark routePlacemark) {
        this.routePlacemark = routePlacemark;
    }

    /**
     * Lista reordenada
     *
     * @return lista
     */
    public List<PlaceMark> getPlacemarksInversa() {

        // Reordenar
        placemarksInversa = new ArrayList<PlaceMark>(placemarks);
        Collections.reverse(placemarksInversa);

        return placemarksInversa;
    }

    public void setPlacemarksInversa(List<PlaceMark> placemarksInversa) {
        this.placemarksInversa = placemarksInversa;
    }

    public void ordenarPlacemark() {

        Collections.sort(placemarks);

    }

    public List<InfoVehiculo> getVehiculosList() {
        return vehiculosList;
    }

    public void setVehiculosList(List<InfoVehiculo> vehiculosList) {
        this.vehiculosList = vehiculosList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((placemarks == null) ? 0 : placemarks.hashCode());
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
        DatosMapa other = (DatosMapa) obj;
        if (placemarks == null) {
            if (other.placemarks != null)
                return false;
        } else if (!placemarks.equals(other.placemarks))
            return false;
        return true;
    }

}
