package alberapps.java.tram.horarios;

import java.util.Date;

/**
 * Created by albert on 19/03/15.
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
