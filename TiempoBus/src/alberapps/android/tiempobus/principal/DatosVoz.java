/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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

package alberapps.android.tiempobus.principal;

/**
 * Datos para la gestion del reconocimiento de voz
 */
public class DatosVoz {

    private String resultado;

    private String descripcion;

    private boolean posibleParada;

    private boolean posibleFavorito;

    private String favoritoParada;

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public boolean isPosibleParada() {
        return posibleParada;
    }

    public void setPosibleParada(boolean posibleParada) {
        this.posibleParada = posibleParada;
    }

    public boolean isPosibleFavorito() {
        return posibleFavorito;
    }

    public void setPosibleFavorito(boolean posibleFavorito) {
        this.posibleFavorito = posibleFavorito;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFavoritoParada() {
        return favoritoParada;
    }

    public void setFavoritoParada(String favoritoParada) {
        this.favoritoParada = favoritoParada;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resultado == null) ? 0 : resultado.hashCode());
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
        DatosVoz other = (DatosVoz) obj;
        if (resultado == null) {
            if (other.resultado != null)
                return false;
        } else if (!resultado.equals(other.resultado))
            return false;
        return true;
    }


}
