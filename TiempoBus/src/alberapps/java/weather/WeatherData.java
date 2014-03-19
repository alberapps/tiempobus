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
package alberapps.java.weather;

import java.util.List;

import android.graphics.Bitmap;

/**
 * 
 * Datos del tiempo
 * 
 *
 */
public class WeatherData {

	//Aemet
	private String enlace;
	private String dia;
	private List<EstadoCielo> estadoCielo;
	private String tempMaxima;
	private String tempMinima;

	// YW
	private String link;
	private String description;
	private String title;
	private String geolong;
	private String geolat;
	private String pubDate;
	private String contitionText;
	private String contitionCode;
	private String contitionTemp;
	private Bitmap imagen;

	public String getEnlace() {
		return enlace;
	}

	public void setEnlace(String enlace) {
		this.enlace = enlace;
	}

	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public String getTempMaxima() {
		return tempMaxima;
	}

	public void setTempMaxima(String tempMaxima) {
		this.tempMaxima = tempMaxima;
	}

	public String getTempMinima() {
		return tempMinima;
	}

	public void setTempMinima(String tempMinima) {
		this.tempMinima = tempMinima;
	}

	public List<EstadoCielo> getEstadoCielo() {
		return estadoCielo;
	}

	public void setEstadoCielo(List<EstadoCielo> estadoCielo) {
		this.estadoCielo = estadoCielo;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGeolong() {
		return geolong;
	}

	public void setGeolong(String geolong) {
		this.geolong = geolong;
	}

	public String getGeolat() {
		return geolat;
	}

	public void setGeolat(String geolat) {
		this.geolat = geolat;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getContitionText() {
		return contitionText;
	}

	public void setContitionText(String contitionText) {
		this.contitionText = contitionText;
	}

	public String getContitionCode() {
		return contitionCode;
	}

	public void setContitionCode(String contitionCode) {
		this.contitionCode = contitionCode;
	}

	public String getContitionTemp() {
		return contitionTemp;
	}

	public void setContitionTemp(String contitionTemp) {
		this.contitionTemp = contitionTemp;
	}

	public Bitmap getImagen() {
		return imagen;
	}

	public void setImagen(Bitmap imagen) {
		this.imagen = imagen;
	}

}
