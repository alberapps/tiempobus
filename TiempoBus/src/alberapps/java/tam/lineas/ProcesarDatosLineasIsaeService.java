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
package alberapps.java.tam.lineas;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import alberapps.java.tam.BusLinea;
import alberapps.java.tam.UtilidadesTAM;
import android.util.Log;

/**
 * 
 * Procesa los datos recuperados de las lineas
 * 
 */
public class ProcesarDatosLineasIsaeService {

	public static final String URL_SUBUS_LINEAS = "http://isaealicante.subus.es/movil/estima.aspx";

	public static List<DatosLinea> getLineasInfo(String offline) {

		List<DatosLinea> lineas = null;

		try {

			Document doc = null;

			//Carga desde internet o desde fichero local
			if (offline == null) {
				doc = Jsoup.parse(new URL(URL_SUBUS_LINEAS).openStream(), "ISO-8859-1", URL_SUBUS_LINEAS);
			} else {

				Log.d("lineas", "datos offline: " + offline);

				doc = Jsoup.parse(offline);
			}
		
			Elements selectLineas = doc.select("select[name=LineasBox]"); 

			Elements option = selectLineas.get(0).select("option");

			DatosLinea datosLinea = null;

			lineas = new ArrayList<DatosLinea>();

			for (int i = 0; i < option.size(); i++) {

				datosLinea = new DatosLinea();

				datosLinea.setLineaDescripcion(option.get(i).text());

				datosLinea.setLineaNum(option.get(i).attr("value"));

				// KML
				int posicion = UtilidadesTAM.getIdLinea(datosLinea.getLineaNum());

				if (posicion >= 0 && posicion < UtilidadesTAM.LINEAS_CODIGO_KML.length) {
					datosLinea.setLineaCodigoKML(UtilidadesTAM.LINEAS_CODIGO_KML[posicion]);

					datosLinea.setGrupoLinea(UtilidadesTAM.DESC_TIPO[UtilidadesTAM.TIPO[posicion]]);

				} else {
					datosLinea.setLineaDescripcion(datosLinea.getLineaDescripcion().concat("\n[**ERROR]"));
				}

				lineas.add(datosLinea);

				// 11H
				if (datosLinea.getLineaNum().equals("11")) {

					DatosLinea datosLineaH = new DatosLinea();
					datosLineaH.setLineaNum("11H");
					int posicionH = UtilidadesTAM.getIdLinea(datosLineaH.getLineaNum());
					datosLineaH.setLineaCodigoKML(UtilidadesTAM.LINEAS_CODIGO_KML[posicionH]);
					datosLineaH.setLineaDescripcion(UtilidadesTAM.LINEAS_DESCRIPCION[posicionH]);

					datosLineaH.setGrupoLinea(UtilidadesTAM.DESC_TIPO[UtilidadesTAM.TIPO[posicionH]]);

					lineas.add(datosLineaH);
				}

			}

		} catch (Exception e) {

			lineas = null;

		}

		return lineas;

	}

	/**
	 * Mapea los datos recuperados a la anterior estructura
	 * 
	 * @param datos
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<BusLinea> getLineasBus(String offline) throws IOException {

		ArrayList<BusLinea> lineasBus = new ArrayList<BusLinea>();

		List<DatosLinea> datosRecuperados = getLineasInfo(offline);

		if (datosRecuperados != null && !datosRecuperados.isEmpty()) {

			// Datos recuperados con exito
			for (int i = 0; i < datosRecuperados.size(); i++) {

				lineasBus.add(new BusLinea(datosRecuperados.get(i).getLineaCodigoKML(), datosRecuperados.get(i).getLineaDescripcion(), datosRecuperados.get(i).getLineaNum(), datosRecuperados.get(i).getGrupoLinea()));

			}
		} else {
			return null;

		}

		return lineasBus;
	}

}
