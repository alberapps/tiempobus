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
import alberapps.java.util.Conectividad;
import android.text.Html;
import android.util.Log;

/**
 * 
 * Procesar horarios
 * 
 */
public class ProcesarHorarios {

	public static String URL_SUBUS = "http://www.subus.es";

	public static String LINEA_URL = "http://www.subus.es/Lineas/Linea.asp?linea=";// ALC24";

	public static DatosHorarios getDetalleHorario(BusLinea datosLinea) throws Exception {

		InputStream st = null;

		DatosHorarios datosHorario = null;

		/*
		 * Especiales resueltas: 31, 30P, 4, 7, C6,27, C-53, 46B faltantes: 30,
		 * 191, 192,M, C2, 45, 46A, 46B Revisar 21N Los que empiezan a las 0:00
		 * Sin datos: 25N TURIBUS
		 */

		try {

			datosHorario = getNumeroHorario(datosLinea);

			for (int i = 0; i < datosHorario.getHorariosIda().size(); i++) {

				int posicionTabla = 6;

				String url = URL_SUBUS + datosHorario.getHorariosIda().get(i).getLinkHorario();

				st = Conectividad.conexionGetIsoStream(url);

				Document doc = Jsoup.parse(st, "ISO-8859-1", url);

				String title = doc.title();

				Elements tables = doc.select("table");

				// Titulo ida
				Element table5 = tables.get(posicionTabla);
				datosHorario.setTituloSalidaIda(procesarElementoTabla(table5));

				// Incrementar tabla
				posicionTabla++;

				// Horas ida
				Element tabla2 = tables.get(posicionTabla);

				// Sacar horas
				String[] listaHorarios2 = procesarElementoTabla(tabla2).split(" ");

				// Mapear horas
				for (int j = 0; j < listaHorarios2.length; j++) {
					if (!listaHorarios2[j].trim().equals("")) {

						datosHorario.getHorariosIda().get(i).getHorarios().add(listaHorarios2[j].trim());

						Log.d("HORARIOS", "i: " + i + " " + datosHorario.getHorariosIda().get(i).getTituloHorario() + "-> horas: " + listaHorarios2[j].trim());

					}
				}

				// Incrementar tabla
				posicionTabla++;

				// Posible comentario

				boolean continuar = false;

				while (!continuar) {

					Element comentarioPosible = tables.get(posicionTabla);
					String posibleComentario = procesarElementoTabla(comentarioPosible);

					if (!posibleComentario.trim().startsWith("SALIDAS DESDE")) {

						if (datosHorario.getComentariosIda() == null) {
							datosHorario.setComentariosIda(new StringBuffer(""));
						}

						datosHorario.getComentariosIda().append("[");
						datosHorario.getComentariosIda().append(datosHorario.getHorariosIda().get(i).getTituloHorario());
						datosHorario.getComentariosIda().append("]: ");

						datosHorario.getComentariosIda().append(posibleComentario);

						datosHorario.getComentariosIda().append("\n");

						// Incrementar tabla
						posicionTabla++;

					} else {
						continuar = true;
					}

				}

				/* VUELTA */

				// Titulo vuelta
				Element table7 = tables.get(posicionTabla);
				datosHorario.setTituloSalidaVuelta(procesarElementoTabla(table7));

				// Incrementar tabla
				posicionTabla++;

				// Horas vuelta
				Element table3 = tables.get(posicionTabla);

				// Sacar horas
				String[] listaHorarios3 = procesarElementoTabla(table3).split(" ");

				// Mapear horas
				for (int j = 0; j < listaHorarios3.length; j++) {
					if (!listaHorarios3[j].trim().equals("")) {

						datosHorario.getHorariosVuelta().get(i).getHorarios().add(listaHorarios3[j].trim());

						Log.d("HORARIOS", datosHorario.getHorariosVuelta().get(i).getTituloHorario() + "-> horas: " + listaHorarios3[j].trim());

					}
				}

				// Incrementar tabla
				posicionTabla++;

				boolean continuarVuelta = false;

				while (!continuarVuelta) {

					// Posible comentario
					Element comentarioPosible2 = tables.get(posicionTabla);
					String posibleComentario2 = procesarElementoTabla(comentarioPosible2);

					if (!posibleComentario2.trim().startsWith("Horarios v")) {

						if (datosHorario.getComentariosVuelta() == null) {
							datosHorario.setComentariosVuelta(new StringBuffer(""));
						}

						datosHorario.getComentariosVuelta().append("[");
						datosHorario.getComentariosVuelta().append(datosHorario.getHorariosVuelta().get(i).getTituloHorario());
						datosHorario.getComentariosVuelta().append("]: ");

						datosHorario.getComentariosVuelta().append(posibleComentario2);

						datosHorario.getComentariosVuelta().append("\n");

						// Incrementar tabla
						posicionTabla++;

					} else {
						continuarVuelta = true;
					}

				}

				// Horarios validos
				Element table9 = tables.get(posicionTabla);

				datosHorario.setValidezHorarios(procesarElementoTabla(table9));

			}

		} catch (Exception e) {
			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

			Log.d("HORARIOS", "Error en procesado de tiempos");

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

	/**
	 * Recuperar links a horarios
	 * 
	 * @param datosLinea
	 * @return links
	 * @throws Exception
	 */
	private static DatosHorarios getNumeroHorario(BusLinea datosLinea) throws Exception {

		DatosHorarios datos = new DatosHorarios();

		InputStream st = null;

		String url = LINEA_URL + datosLinea.getIdlinea();

		Log.d("HORARIOS", "id linea: " + datosLinea.getIdlinea());

		try {

			st = Conectividad.conexionGetIsoStream(url);

			Document doc = Jsoup.parse(st, "ISO-8859-1", url);

			Elements elementos = doc.select("a[href^=/Lineas/Horario.asp?codigo=]");

			Horario horario = null;

			for (int i = 0; i < elementos.size(); i++) {

				horario = new Horario();
				horario.setLinkHorario(elementos.get(i).attr("href"));
				horario.setTituloHorario(elementos.get(i).text());
				datos.getHorariosIda().add(horario);

				horario = new Horario();
				horario.setLinkHorario(elementos.get(i).attr("href"));
				horario.setTituloHorario(elementos.get(i).text());
				datos.getHorariosVuelta().add(horario);

			}

			if (datos.getHorariosIda() == null || datos.getHorariosIda().isEmpty()) {
				throw new Exception("Error paso 1");
			}

		} catch (Exception e) {
			try {
				if (st != null) {
					st.close();
				}
			} catch (IOException eb) {

			}

			Log.d("HORARIOS", "Error en procesado de tiempos paso 1");

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

	/**
	 * Recupera string de tabla
	 * 
	 * @param table7
	 * @return String
	 */
	private static String procesarElementoTabla(Element table) {

		String safe = Jsoup.clean(table.html(), Whitelist.basic());

		// Problema caracteres
		String limpiar = safe.replaceAll("", "-").replaceAll("", "&euro;").replaceAll("&nbsp;", "").trim();

		// Limpiar 2
		limpiar = limpiar.replaceAll("\n", "");
		limpiar = limpiar.replaceAll("\t", "");

		return Html.fromHtml(limpiar).toString();

	}

}
