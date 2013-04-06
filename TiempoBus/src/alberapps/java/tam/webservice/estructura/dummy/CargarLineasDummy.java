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
package alberapps.java.tam.webservice.estructura.dummy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alberapps.java.tam.webservice.estructura.GetLineasResult;

public class CargarLineasDummy {

	private InputStream file;

	public CargarLineasDummy(InputStream fi) {

		this.file = fi;

	}

	public GetLineasResult cargarListaLineas() throws IOException {

		GetLineasResult lineasResult = new GetLineasResult();

		BufferedReader brin = new BufferedReader(new InputStreamReader(this.file));

		// Lectura del fichero
		String linea;

		while ((linea = brin.readLine()) != null){
			
			String[] registros = linea.split(";");
			
			String lineaBus = registros[0].trim();
			
			
			//Datos				
			lineasResult.getHashDatosParadas().put(lineaBus, new HashMap<String, List<String>>());
			
			
			String destino = null;
			String parada = null;
			
			for(int i=1;i < registros.length;i++){
				
				//Si no es parada, es destino
				if(registros[i].trim().charAt(0) != '['){
					
					destino = registros[i].trim();
					
					lineasResult.getHashDatosParadas().get(lineaBus).put(destino, new ArrayList<String>());
					
				}else{
					
					parada = registros[i].trim();
					
					lineasResult.getHashDatosParadas().get(lineaBus).get(destino).add(parada.trim());
					
					
				}
				
				
				
				
				
			}
			
			
			
			
		}
			
	
		
		return lineasResult;

	}

}
