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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Noticias implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4240900250983171841L;

	private String fecha;
	private String noticia;

	private List<String> links;

	private List<String> descLink;

	private String contenidoHtml;

	private String fechaCabecera;

	private String tituloCabecera;

	private String lineaCabecera;

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

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getNoticia() {
		return noticia;
	}

	public void setNoticia(String noticia) {
		this.noticia = noticia;
	}

	public Date getFechaDate() {

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

		Date fechaDate = null;

		if (fecha != null) {
			try {
				fechaDate = df.parse(fecha);

				return fechaDate;

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return null;

	}

}
