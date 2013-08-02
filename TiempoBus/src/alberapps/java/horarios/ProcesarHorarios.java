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
import android.text.Html;
import android.util.Log;

/**
 * 
 * Procesar el detalle de la noticia
 * 
 */
public class ProcesarHorarios {

	public static String URL_SUBUS = "http://www.subus.es";
	
	public static String HORARIO_URL = "http://www.subus.es/Lineas/Horario.asp?codigo=189";
	
	public static String LINEA_URL = "http://www.subus.es/Lineas/Linea.asp?linea=";//ALC24";
	
	public static DatosHorarios getDetalleHorario(BusLinea datosLinea) throws Exception {

		InputStream st = null;

		DatosHorarios datosHorario = null;
		
		String url = HORARIO_URL;

		try {

			datosHorario = getNumeroHorario(datosLinea);
			
			for(int i = 0;i< datosHorario.getHorariosIda().size();i++){
			
			st = Utilidades.recuperarStreamConexionSimple(URL_SUBUS + datosHorario.getHorariosIda().get(i).getLinkHorario());

			Document doc = Jsoup.parse(st, "ISO-8859-1", url);

			

			String title = doc.title();

			Elements tables = doc.select("table"); 

			
			
			//Log.d("HORARIOS", "table3: " + tables.get(3));
			
			//Log.d("HORARIOS", "table4: " + tables.get(4));
			
			//Log.d("HORARIOS", "table5: " + tables.get(5));
			
			//Titulo ida
			Element table5 = tables.get(5);
			String safe5 = Jsoup.clean(table5.html(), Whitelist.basic());

			// Problema caracteres
			String limpiar5 = safe5.replace("", "-").replace("", "&euro;").replace("&nbsp;", "").trim();
			
			datosHorario.setTituloSalidaIda(Html.fromHtml(limpiar5).toString());
			
			
			
			//Horas ida
			Element tabla2 = tables.get(6);
			String safe2 = Jsoup.clean(tabla2.html(), Whitelist.basic());

			// Problema caracteres
			String limpiar2 = safe2.replace("", "-").replace("", "&euro;").replace("&nbsp;", "").trim();
			
			//Log.d("HORARIOS", "html2: " + limpiar2);
			
			
			//Limpiar 2
			limpiar2 = limpiar2.replaceAll("\n", "");
			limpiar2 = limpiar2.replaceAll("\t", "");
			
			//Sacar horas
			String[] listaHorarios2 = limpiar2.split(" ");
			
			
			//Mapear horas
			for(int j = 0;j< listaHorarios2.length;j++){
				if(!listaHorarios2[j].trim().equals("")){
					
					datosHorario.getHorariosIda().get(i).getHorarios().add(listaHorarios2[j].trim());
					
					Log.d("HORARIOS", "i: " + i + " " + datosHorario.getHorariosIda().get(i).getTituloHorario() +  "-> horas: " + listaHorarios2[j].trim());
					
				}
			}
			
			
			//Log.d("HORARIOS", "table7: " + tables.get(7));
			
			
			//Titlulo vuelta
			Element table7 = tables.get(7);
			String safe7 = Jsoup.clean(table7.html(), Whitelist.basic());

			// Problema caracteres
			String limpiar7 = safe7.replace("", "-").replace("", "&euro;").replace("&nbsp;", "").trim();
			
			
			datosHorario.setTituloSalidaVuelta(Html.fromHtml(limpiar7).toString());
			
			
			
			//Horas vuelta
			Element table3 = tables.get(8);			
			String safe3 = Jsoup.clean(table3.html(), Whitelist.basic());

			// Problema caracteres
			String limpiar3 = safe3.replace("", "-").replace("", "&euro;").replace("&nbsp;", "").trim();
			
			Log.d("HORARIOS", "html3: " + limpiar3);
			
			//Limpiar 2
			limpiar3 = limpiar3.replaceAll("\n", "");
			limpiar3 = limpiar3.replaceAll("\t", "");
			
			//Sacar horas
			String[] listaHorarios3 = limpiar3.split(" ");
			
			
			//Mapear horas
			for(int j = 0;j< listaHorarios3.length;j++){
				if(!listaHorarios3[j].trim().equals("")){
					
					datosHorario.getHorariosVuelta().get(i).getHorarios().add(listaHorarios3[j].trim());
					
					Log.d("HORARIOS", datosHorario.getHorariosVuelta().get(i).getTituloHorario() +  "-> horas: " + listaHorarios3[j].trim());
					
				}
			}
			
			
			
			
			//noticias.setContenidoHtml(limpiar);

			// Cabecera
			//Element filaCabecera2 = filas.get(5);
			//Elements contCabecera2 = filaCabecera2.select("td");
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

	
	private static DatosHorarios getNumeroHorario(BusLinea datosLinea) throws Exception{
		
		DatosHorarios datos = new DatosHorarios();
		
		InputStream st = null;
		
		String url = LINEA_URL + datosLinea.getIdlinea();
		
		Log.d("HORARIOS", "id linea: " + datosLinea.getIdlinea());

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
				
				horario = new Horario();
				horario.setLinkHorario(elementos.get(i).attr("href"));
				horario.setTituloHorario(elementos.get(i).text());				
				datos.getHorariosVuelta().add(horario);
				
				
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
