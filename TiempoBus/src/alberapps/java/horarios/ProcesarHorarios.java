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
package alberapps.java.horarios;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import alberapps.java.tam.BusLinea;
import alberapps.java.util.Utilidades;
import android.util.Log;

/**
 * 
 * Procesar el detalle de la noticia
 * 
 */
public class ProcesarHorarios {

	public static String URL_SUBUS = "http://www.subus.es";
	
	public static String HORARIO_URL = "http://www.subus.es/Lineas/Horario.asp?codigo=189";
	
	public static String LINEA_URL = "http://www.subus.es/Lineas/Linea.asp?linea=ALC24";
	
	public static DatosHorarios getDetalleHorario(BusLinea datosLinea) throws Exception {

		InputStream st = null;

		DatosHorarios datosHorario = null;
		
		String url = HORARIO_URL;

		try {

			datosHorario = getNumeroHorario();
			
			for(int i = 0;i< datosHorario.getHorariosIda().size();i++){
			
			st = Utilidades.recuperarStreamConexionSimple(URL_SUBUS + datosHorario.getHorariosIda().get(i).getLinkHorario());

			Document doc = Jsoup.parse(st, "ISO-8859-1", url);

			//noticias = new Noticias();

			String title = doc.title();

			Elements tables = doc.select("table"); // a with href

			//Horarios lunes a viernes
			Element tabla = tables.get(3);

			Elements filas = tabla.select("tr");

			Element filaDetalle = filas.get(7);

			Elements cont1 = filaDetalle.select("td");

			Element cont2 = cont1.get(1);

			String safe = Jsoup.clean(cont2.html(), Whitelist.basic());

			// Problema caracteres
			String limpiar = safe.replace("", "-").replace("", "&euro;").replace("&nbsp;", "").trim();

			Log.d("HORARIOS", "html: " + limpiar);

			limpiar = limpiar.replaceAll("\n", "");
			limpiar = limpiar.replaceAll("\t", "");
			
			String[] listaHorarios = limpiar.split(" ");
			
			
			
			for(int j = 0;j< listaHorarios.length;j++){
				if(!listaHorarios[i].trim().equals("")){
					datosHorario.getHorariosIda().get(i).getHorarios().add(listaHorarios[j].trim());
					
					Log.d("HORARIOS", "html sin saltos: " + listaHorarios[j].trim());
				}
			}
			
			
			
			//Horarios sabados
			Element tabla2 = tables.get(6);

			//Elements filas2 = tabla2.select("tr");

			//Element filaDetalle2 = filas2.get(7);

			//Elements cont12 = filaDetalle2.select("td");

			//Element cont22 = cont12.get(1);

			String safe2 = Jsoup.clean(tabla2.html(), Whitelist.basic());

			// Problema caracteres
			String limpiar2 = safe2.replace("", "-").replace("", "&euro;").replace("&nbsp;", "").trim();

			Log.d("HORARIOS", "html2: " + limpiar2);
			
			
			
			
			
			
			
			
			
			
			//noticias.setContenidoHtml(limpiar);

			// Cabecera
			Element filaCabecera2 = filas.get(5);
			Elements contCabecera2 = filaCabecera2.select("td");
			//noticias.setFechaCabecera(contCabecera2.get(0).text().trim());
			//noticias.setTituloCabecera(contCabecera2.get(1).text().trim());

			// Cabecera linea
			//noticias.setLineaCabecera(filas.get(1).text().trim());

			
			}
			
		} catch (Exception e) {
			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

			throw e;

		} finally {

			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

		}

		return datosHorario;
	}

	
	private static DatosHorarios getNumeroHorario() throws Exception{
		
		DatosHorarios datos = new DatosHorarios();
		
		InputStream st = null;
		
		String url = LINEA_URL;

		try {

			st = Utilidades.recuperarStreamConexionSimple(url);

			Document doc = Jsoup.parse(st, "ISO-8859-1", url);
			
			Elements elementos = doc.select("a[href^=/Lineas/Horario.asp?codigo=]");
			
			Horario horario = null;
			
			for(int i = 0; i< elementos.size();i++){
			
				horario = new Horario();
				
				horario.setLinkHorario(elementos.get(i).attr("href"));
				
			
				horario.setTituloHorario(elementos.get(i).text());
			
				
			
				datos.getHorariosIda().add(horario);
				
				
			}
			
		} catch (Exception e) {
			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

			throw e;

		} finally {

			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

		}
		
		return datos;
		
	}
	
}
