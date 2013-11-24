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
package alberapps.android.tiempobus.infolineas;

import alberapps.android.tiempobus.R;
import alberapps.java.tam.BusLinea;
import alberapps.java.tram.UtilidadesTRAM;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class GestionTram {

	/**
	 * Cotexto principal
	 */
	private InfoLineasTabsPager context;

	private SharedPreferences preferencias;

	public GestionTram(InfoLineasTabsPager contexto, SharedPreferences preferencia) {

		context = contexto;

		preferencias = preferencia;

	}

	/**
	 * Selector de horarios
	 */
	public void seleccionHorarioTramL1L3() {

		final CharSequence[] items = { "L1 L3 a Campello y Benidorm", "L1 L3 a Luceros(Alicante)" };

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.infolinea_horarios);

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {

					abrirPdfGDocs(0);

				} else if (item == 1) {

					abrirPdfGDocs(1);

				}

			}

		});

		AlertDialog alert = builder.create();

		alert.show();

	}

	/**
	 * Seleccion de pdf
	 * 
	 * @param bus
	 */
	public void seleccionarPdf(BusLinea bus) {

		if (bus.getNumLinea().equals("L1") || bus.getNumLinea().equals("L3")) {

			seleccionHorarioTramL1L3();

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

	/**
	 * Abrir pdf
	 * 
	 * @param idPdf
	 */
	public void abrirPdf(int idPdf) {

		try {
			
			Log.d("PDF", "pdf ID: " + idPdf); 
			
			String pdf = UtilidadesTRAM.PDF_URL[idPdf];
			
			Log.d("PDF", "pdf: " + pdf); 
			
			Intent i = new Intent(Intent.ACTION_VIEW);

			i.setDataAndType(Uri.parse(pdf), "application/pdf");
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			context.startActivity(i);
		} catch (Exception e) {

			e.printStackTrace();
			
			Toast.makeText(context, context.getString(R.string.error_pdf), Toast.LENGTH_SHORT).show();

		}

	}

}
