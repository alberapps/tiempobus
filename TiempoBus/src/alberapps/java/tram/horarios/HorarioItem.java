package alberapps.java.tram.horarios;

import java.util.List;

/**
 * Created by albert on 06/03/15.
 */
public class HorarioItem {


    private String grupoHora;

    private String horas;

    private String datoInfo;

    private String linea;

    private List<String> infoRecorrido;

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

}
