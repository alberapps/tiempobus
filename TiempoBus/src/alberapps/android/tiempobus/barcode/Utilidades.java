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
package alberapps.android.tiempobus.barcode;

public class Utilidades {

    /**
     * FORMATO: SMSTO:215034:TAM 2932
     *
     * @param qrCodeRecuperado
     * @return codigo
     */
    public static String parsearCodigoParada(String qrCodeRecuperado) {

        String codigo = null;

        if (qrCodeRecuperado != null && !qrCodeRecuperado.equals("")) {

            if (qrCodeRecuperado.contains("SMSTO:215034:TAM")) {

                String[] datos = qrCodeRecuperado.split(":");

                if (datos != null && datos.length == 3) {

                    String datos2 = datos[2];

                    codigo = datos2.substring(4);

                }
            } else if (qrCodeRecuperado.toLowerCase().contains("/alicante/consulta.aspx?p=") || qrCodeRecuperado.contains("TiempoBus y Tram p=")) {

                //Nuevos qr de paradas

                String[] datos = qrCodeRecuperado.split("p=");

                if (datos != null && datos.length == 2) {

                    codigo = datos[1];

                }

            }

        }

        return codigo;

    }

    /**
     * FORMATO: SMSTO:215034:TAM 2932
     * <p>
     * Codificar el codigo para exportar
     *
     * @param codigo
     * @return codigo
     */
    public static String codificarCodigoParada(String codigo) {

        StringBuffer qrCodeRecuperado = null;

        if (codigo != null && !codigo.equals("")) {

            qrCodeRecuperado = new StringBuffer("");
            qrCodeRecuperado.append("TiempoBus y Tram p=");
            qrCodeRecuperado.append(codigo);

        }

        return qrCodeRecuperado.toString();

    }

}
