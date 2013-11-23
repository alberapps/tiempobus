/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
 *  based on code by ZgzBus Copyright (C) 2010 Francho Joven
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

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.java.tam.BusLinea;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adaptador Tiempos
 */
public class InfoLineaAdapter extends ArrayAdapter<BusLinea> {

	private InfoLineasTabsPager contexto;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 */
	public InfoLineaAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);

		this.contexto = ((InfoLineasTabsPager) context);

	}

	/**
	 * Genera la vista de cada uno de los items
	 */
	@Override
	public View getView(final int position, View v, ViewGroup parent) {
		// Si no tenemos la vista de la fila creada componemos una
		if (v == null) {
			Context ctx = this.getContext().getApplicationContext();
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			v = vi.inflate(R.layout.infolineas_item, null);

		}

		TextView busLinea;
		TextView descLinea;
		TextView datosLinea;

		busLinea = (TextView) v.findViewById(R.id.bus_linea);
		descLinea = (TextView) v.findViewById(R.id.desc_linea);
		datosLinea = (TextView) v.findViewById(R.id.datos_linea);

		final BusLinea bus = getItem(position);

		if (bus != null) {
			busLinea.setText(bus.getNumLinea());
			descLinea.setText(bus.getLinea().substring(bus.getNumLinea().length()).trim());

			datosLinea.setText(bus.getGrupo());

		}

		TextView informacionText = (TextView) v.findViewById(R.id.infoparada_horarios);

		if (((InfoLineasTabsPager) contexto).modoRed != InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
			// Carga de horarios bus
			// Link informacion
			informacionText.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {

					contexto.setLinea(bus);

					contexto.setTitle(bus.getLinea());

					contexto.cargarHorarios(bus, position);

				}

			});

		} else {
			
			informacionText.setText(R.string.infolinea_horarios_pdf);
			
			// Carga de horarios tram
			// Link informacion
			informacionText.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {

					contexto.gestionTram.seleccionarPdf(bus);

				}

			});

		}

		return v;
	}

	/**
	 * Anade todas las lineas al adapter
	 * 
	 * @param noticias
	 */
	public void addAll(List<BusLinea> busLinea) {
		if (busLinea == null) {
			return;
		}

		for (int i = 0; i < busLinea.size(); i++) {
			add(busLinea.get(i));
		}
	}

}
