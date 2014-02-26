package alberapps.java.weather;

import java.util.List;

public class WeatherData {

	
	private String enlace;
	
	private String dia;
	
	private List<EstadoCielo> estadoCielo;
	
	private String tempMaxima;
	
	private String tempMinima;

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

	
	

}
