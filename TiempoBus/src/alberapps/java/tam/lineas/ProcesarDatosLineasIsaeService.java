/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.java.tam.lineas;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.DatosTam;
import alberapps.java.tam.UtilidadesTAM;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.util.Conectividad;
import alberapps.java.util.Utilidades;

/**
 * Procesa los datos recuperados de las lineas
 */
public class ProcesarDatosLineasIsaeService {

    public static List<DatosLinea> getLineasInfo(String offline, boolean enableHttps) {

        List<DatosLinea> lineas = null;

        InputStream st = null;

        try {

            Document doc = null;

            // Carga desde internet o desde fichero local
            if (offline == null) {
                //st = Conectividad.conexionGetIsoStream(DatosTam.URL_SERVIDOR_LINEAS);
                //doc = Jsoup.parse(st, "ISO-8859-1", DatosTam.URL_SERVIDOR_LINEAS);

                String url = DatosTam.URL_SERVIDOR_LINEAS;

                if(!enableHttps) {
                    url = url.replace("https", "http");
                }

                String conexion = Conectividad.conexionGetUtf8StringUserAgent(url, true, null);
                doc = Jsoup.parse(Utilidades.stringToStream(conexion), "UTF-8", url);


            } else {

                Log.d("lineas", "datos offline: " + offline);

                doc = Jsoup.parse(offline);
            }

            //Elements selectLineas = doc.select("select[name=LineasBox]");

            Elements option = doc.select("option");

            DatosLinea datosLinea = null;

            lineas = new ArrayList<>();

            for (int i = 0; i < option.size(); i++) {

                if(option.get(i).attr("value").equals("12-old") ||
                        option.get(i).attr("value").equals("03Nh")) {
                    continue;
                }

                datosLinea = new DatosLinea();

                datosLinea.setLineaNum(option.get(i).attr("value"));

                datosLinea.setLineaDescripcion(option.get(i).text());


                if (datosLinea.getLineaDescripcion().contains(":")) {
                    datosLinea.setLineaDescripcion(datosLinea.getLineaDescripcion().split(":")[1]);
                }
                datosLinea.setLineaDescripcion(datosLinea.getLineaDescripcion().trim());

                // KML
                int posicion = UtilidadesTAM.getIdLinea(datosLinea.getLineaNum());

                boolean esTram = false;

                if (posicion < 0) {

                    // Verificar si es TRAM
                    posicion = UtilidadesTRAM.getIdLinea(datosLinea.getLineaNum());

                    esTram = true;

                }

                if (!esTram && posicion >= 0 && posicion < UtilidadesTAM.LINEAS_NUM.length) {

                    datosLinea.setGrupoLinea(UtilidadesTAM.DESC_TIPO[UtilidadesTAM.TIPO[posicion]]);
                    datosLinea.setGrupoLineaId(Integer.toString(UtilidadesTAM.TIPO[posicion]));

                } else if (esTram && posicion >= 0) {

                    datosLinea.setGrupoLinea(UtilidadesTRAM.DESC_TIPO[UtilidadesTRAM.TIPO[posicion]]);
                    datosLinea.setGrupoLineaId(Integer.toString(UtilidadesTRAM.TIPO[posicion]));

                } else {
                    datosLinea.setLineaDescripcion(datosLinea.getLineaDescripcion().concat("\n[**Sin información]"));
                    datosLinea.setGrupoLineaId("1000");
                }


                lineas.add(datosLinea);

            }

        } catch (Exception e) {

            lineas = null;

            e.printStackTrace();

        } finally {

            try {
                if (st != null) {
                    st.close();
                }
            } catch (IOException eb) {

            }

        }

        return lineas;

    }

    /**
     * Mapea los datos recuperados a la anterior estructura
     *
     * @param offline
     * @return
     * @throws IOException
     */
    public static ArrayList<BusLinea> getLineasBus(String offline, boolean enableHttps) throws IOException {

        ArrayList<BusLinea> lineasBus = new ArrayList<>();

        List<DatosLinea> datosRecuperados = getLineasInfo(offline, enableHttps);

        if (datosRecuperados != null && !datosRecuperados.isEmpty()) {

            // Datos recuperados con exito
            for (int i = 0; i < datosRecuperados.size(); i++) {

                lineasBus.add(new BusLinea(datosRecuperados.get(i).getLineaCodigoKML(), datosRecuperados.get(i).getLineaDescripcion(), datosRecuperados.get(i).getLineaNum(), datosRecuperados.get(i).getGrupoLinea(), datosRecuperados.get(i).getGrupoLineaId()));


            }
        } else {
            return null;

        }

        return ordenarPorGrupo(lineasBus);
    }


    /**
     * Ordenar por id de grupo
     *
     * @param lineas
     * @return
     */
    private static ArrayList<BusLinea> ordenarPorGrupo(ArrayList<BusLinea> lineas) {

        if (lineas == null || lineas.isEmpty()) {
            return lineas;
        } else if (DatosPantallaPrincipal.esLineaTram(lineas.get(0).getNumLinea())) {
            return lineas;
        } else {

            Comparator<BusLinea> comparator = new Comparator<BusLinea>() {
                @Override
                public int compare(BusLinea lhs, BusLinea rhs) {
                    if (lhs.getIdGrupo() != null && rhs.getIdGrupo() != null) {
                        Integer c1 = Integer.parseInt(lhs.getIdGrupo());
                        Integer c2 = Integer.parseInt(rhs.getIdGrupo());
                        return c1.compareTo(c2);
                    } else {
                        return -1;
                    }
                }
            };

            Collections.sort(lineas, comparator);

        }

        return lineas;


    }

}
