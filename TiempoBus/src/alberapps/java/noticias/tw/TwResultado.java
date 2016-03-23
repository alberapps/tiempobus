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
package alberapps.java.noticias.tw;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Resultados de tw
 */
public class TwResultado implements Comparable<TwResultado> {

    private String usuario;

    private String nombreCompleto;

    private String fecha;

    private Date fechaDate;

    private String url;

    private String id;

    private long respuestaId;

    private boolean retweet;

    private String error;

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    private String mensajeError;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    private String mensaje;

    private String imagen;

    private Bitmap imagenBitmap;

    public Bitmap getImagenBitmap() {
        return imagenBitmap;
    }

    public void setImagenBitmap(Bitmap imagenBitmap) {
        this.imagenBitmap = imagenBitmap;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int compareTo(TwResultado arg0) {

        if (arg0.getFechaDate() != null) {

            return arg0.getFechaDate().compareTo(getFechaDate());

        } else {
            return -1;
        }

    }

    public Date getFechaDate() {
        return fechaDate;
    }

    public void setFechaDate(Date fechaDate) {
        this.fechaDate = fechaDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getRespuestaId() {
        return respuestaId;
    }

    public void setRespuestaId(long respuestaId) {
        this.respuestaId = respuestaId;
    }

    public boolean isRetweet() {
        return retweet;
    }

    public void setRetweet(boolean retweet) {
        this.retweet = retweet;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TwResultado that = (TwResultado) o;

        return !(usuario != null ? !usuario.equals(that.usuario) : that.usuario != null);

    }

    @Override
    public int hashCode() {
        return usuario != null ? usuario.hashCode() : 0;
    }
}
