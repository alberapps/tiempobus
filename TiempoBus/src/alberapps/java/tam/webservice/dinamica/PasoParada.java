/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
package alberapps.java.tam.webservice.dinamica;


import java.io.Serializable;



public class PasoParada implements Serializable {

		
       
    /**
	 * 
	 */
	private static final long serialVersionUID = 1736225819399570972L;
	
	

	private boolean cabecera = false;
    
    private InfoParada e1;
    
    private InfoParada e2;
    
    private String linea;
    
    private String parada;
    
    private String ruta;
    
    private String sublinea;

    
    public PasoParada(){
    	
    	e1 = new InfoParada();
    	e2 = new InfoParada();
    	
    }

    public PasoParada(InfoParada e1, InfoParada e2, String linea, String parada, String ruta, String sublinea) {
        this.e1 = e1;
        this.e2 = e2;
        this.linea = linea;
        this.parada = parada;
        this.ruta = ruta;
        this.sublinea = sublinea;
    }

    public boolean isCabecera() {
		return cabecera;
	}

	public void setCabecera(boolean cabecera) {
		this.cabecera = cabecera;
	}

	

	public String getLinea() {
		return linea;
	}

	public void setLinea(String linea) {
		this.linea = linea;
	}

	public String getParada() {
		return parada;
	}

	public void setParada(String parada) {
		this.parada = parada;
	}

	public String getRuta() {
		return ruta;
	}

	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	public String getSublinea() {
		return sublinea;
	}

	public void setSublinea(String sublinea) {
		this.sublinea = sublinea;
	}

	public InfoParada getE1() {
		return e1;
	}

	public void setE1(InfoParada e1) {
		this.e1 = e1;
	}

	public InfoParada getE2() {
		return e2;
	}

	public void setE2(InfoParada e2) {
		this.e2 = e2;
	}
	
	
       

}