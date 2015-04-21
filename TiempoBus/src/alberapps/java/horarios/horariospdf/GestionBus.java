/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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
package alberapps.java.horarios.horariospdf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.java.tam.BusLinea;
import alberapps.java.tram.UtilidadesTRAM;

/**
 * Gestion de datos del tram
 */
public class GestionBus {

    /**
     * Cotexto principal
     */
    private InfoLineasTabsPager context;

    private SharedPreferences preferencias;

    public GestionBus(InfoLineasTabsPager contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }



    /**
     * Seleccion de pdf
     *
     * @param bus
     */
    public void seleccionarPdf(BusLinea bus) {

        if (bus.getNumLinea().equals("L1") || bus.getNumLinea().equals("L3")) {

            //seleccionHorarioTramL1L3();

        } else if (bus.getNumLinea().equals("L2")) {
            abrirPdfGDocs(2);
        } else if (bus.getNumLinea().equals("L4")) {
            abrirPdfGDocs(3);
        } else if (bus.getNumLinea().equals("L9")) {
            abrirPdfGDocs(4);
        }

    }

    /**
     * Abrir pdf
     *
     * @param idPdf
     */
    public void abrirPdfGDocs(int idPdf) {

        String pdf = UtilidadesTRAM.PDF_URL[idPdf];

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(UtilidadesTRAM.URL_DOCS + pdf));
        context.startActivity(i);

    }



}
