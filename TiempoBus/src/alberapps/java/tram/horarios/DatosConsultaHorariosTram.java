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

import java.util.Date;

/**
 * Datos de la consulta
 */
public class DatosConsultaHorariosTram {

    private int estacionOrigenSeleccion;

    private int codEstacionOrigen;

    private int estacionDestinoSeleccion;

    private int codEstacionDestino;


    private String dia;

    private Date diaDate;

    private String horaDesde;

    private String horaHasta;

    public int getEstacionOrigenSeleccion() {
        return estacionOrigenSeleccion;
    }

    public void setEstacionOrigenSeleccion(int estacionOrigenSeleccion) {
        this.estacionOrigenSeleccion = estacionOrigenSeleccion;
    }

    public int getCodEstacionOrigen() {
        return codEstacionOrigen;
    }

    public void setCodEstacionOrigen(int codEstacionOrigen) {
        this.codEstacionOrigen = codEstacionOrigen;
    }

    public int getEstacionDestinoSeleccion() {
        return estacionDestinoSeleccion;
    }

    public void setEstacionDestinoSeleccion(int estacionDestinoSeleccion) {
        this.estacionDestinoSeleccion = estacionDestinoSeleccion;
    }

    public int getCodEstacionDestino() {
        return codEstacionDestino;
    }

    public void setCodEstacionDestino(int codEstacionDestino) {
        this.codEstacionDestino = codEstacionDestino;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public Date getDiaDate() {
        return diaDate;
    }

    public void setDiaDate(Date diaDate) {
        this.diaDate = diaDate;
    }

    public String getHoraDesde() {
        return horaDesde;
    }

    public void setHoraDesde(String horaDesde) {
        this.horaDesde = horaDesde;
    }

    public String getHoraHasta() {
        return horaHasta;
    }

    public void setHoraHasta(String horaHasta) {
        this.horaHasta = horaHasta;
    }
}
