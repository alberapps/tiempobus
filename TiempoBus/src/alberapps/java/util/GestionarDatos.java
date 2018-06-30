/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p>
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.java.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestion interna de los datos
 */
public class GestionarDatos {

    /**
     * Recupera la lista a partir del string de datos
     *
     * @param lista
     * @return
     */
    public static List<Datos> listaDatos(String lista) {

        if (lista.length() == 0) {
            return null;
        }

        String[] listaS = lista.split(";");

        List<Datos> datosNuevo = new ArrayList<>();
        Datos dato = null;

        for (int i = 0; i < listaS.length; i++) {

            String[] datos = listaS[i].split(",");

            if (datos.length < 2) {
                break;
            }

            dato = new Datos();
            dato.setLinea(datos[0]);
            dato.setDestino(datos[1]);

            /*
            if(datos.length > 2) {
                dato.setDestino(datos[2]);
            }
            */

            datosNuevo.add(dato);

        }

        return datosNuevo;

    }

    /**
     * Recupera el String de datos a partir de la lista interna
     *
     * @param lista
     * @return
     */
    public static String getStringDeLista(List<Datos> lista) {

        StringBuffer datos = new StringBuffer();

        for (int i = 0; i < lista.size(); i++) {

            if (datos.length() > 0) {
                datos.append(";");
            }

            datos.append(lista.get(i).getLinea());

            datos.append(",");

            datos.append(lista.get(i).getDestino());

        }

        return datos.toString();

    }

    public static List<Datos> listaDatos2(String lista) {

        if (lista.length() == 0) {
            return null;
        }

        String[] listaS = lista.split(";");

        List<Datos> datosNuevo = new ArrayList<>();
        Datos dato = null;

        for (int i = 0; i < listaS.length; i++) {

            String[] datos = listaS[i].split(",");

            if (datos.length < 2) {
                break;
            }

            dato = new Datos();
            if(!datos[0].equals("")) {
                dato.setLinea(datos[0]);
            }
            if(!datos[1].equals("")) {
                dato.setDestino(datos[1]);
            }
            if(!datos[2].equals("")) {
                dato.setParada(datos[2]);
            }

            datosNuevo.add(dato);

        }

        return datosNuevo;

    }

    public static String getStringDeLista2(List<Datos> lista) {

        StringBuilder datos = new StringBuilder(lista.size() * 10);

        for (int i = 0; i < lista.size(); i++) {

            if (datos.length() > 0) {
                datos.append(";");
            }

            if (lista.get(i).getLinea() != null) {
                datos.append(lista.get(i).getLinea());
            } else {
                datos.append("");
            }

            datos.append(",");

            if (lista.get(i).getDestino() != null) {

                datos.append(lista.get(i).getDestino());

            } else {
                datos.append("");
            }

            datos.append(",");

            if (lista.get(i).getParada() != null) {

                datos.append(lista.get(i).getParada());

            } else {
                datos.append("");
            }

        }

        return datos.toString();

    }

}
