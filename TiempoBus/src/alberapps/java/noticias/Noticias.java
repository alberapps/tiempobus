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
package alberapps.java.noticias;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Noticias implements Serializable, Comparable<Noticias> {

    /**
     *
     */
    private static final long serialVersionUID = 4240900250983171841L;

    private Date fecha;
    private String noticia;

    private String noticiaLineas;

    private List<String> links;

    private List<String> descLink;

    private String contenidoHtml;

    private String fechaCabecera;

    private String tituloCabecera;

    private String lineaCabecera;

    public String getFechaDoble() {
        return fechaDoble;
    }

    public void setFechaDoble(String fechaDoble) {
        this.fechaDoble = fechaDoble;
    }

    private String fechaDoble;

    private boolean sinDatos;

    private boolean errorServicio;

    public boolean isSinDatos() {
        return sinDatos;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getNoticiaLineas() {
        return noticiaLineas;
    }

    public void setNoticiaLineas(String noticiaLineas) {
        this.noticiaLineas = noticiaLineas;
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

    public String getLineaCabecera() {
        return lineaCabecera;
    }

    public void setLineaCabecera(String lineaCabecera) {
        this.lineaCabecera = lineaCabecera;
    }

    public String getFechaCabecera() {
        return fechaCabecera;
    }

    public void setFechaCabecera(String fechaCabecera) {
        this.fechaCabecera = fechaCabecera;
    }

    public String getTituloCabecera() {
        return tituloCabecera;
    }

    public void setTituloCabecera(String tituloCabecera) {
        this.tituloCabecera = tituloCabecera;
    }

    public String getContenidoHtml() {
        return contenidoHtml;
    }

    public void setContenidoHtml(String contenidoHtml) {
        this.contenidoHtml = contenidoHtml;
    }

    public List<String> getLinks() {
        return links;
    }

    public void setLinks(List<String> links) {
        this.links = links;
    }

    public List<String> getDescLink() {
        return descLink;
    }

    public void setDescLink(List<String> descLink) {
        this.descLink = descLink;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getNoticia() {
        return noticia;
    }

    public void setNoticia(String noticia) {
        this.noticia = noticia;
    }


    @Override
    public int compareTo(Noticias another) {

        if(getFecha() != null && another.getFecha()!= null) {

            Date fecha1 = getFecha();
            Date fecha2 = another.getFecha();

            return fecha2.compareTo(fecha1);
        }else{
            return 1;
        }
    }



}
