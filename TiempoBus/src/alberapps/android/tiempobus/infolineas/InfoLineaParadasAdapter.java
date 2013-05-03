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
import alberapps.java.tam.mapas.PlaceMark;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adaptador Tiempos
 */
public class InfoLineaParadasAdapter extends ArrayAdapter<PlaceMark> {

	private Context contexto;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 */
	public InfoLineaParadasAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);

		this.contexto = context;

	}

	/**
	 * Genera la vista de cada uno de los items
	 */
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// Si no tenemos la vista de la fila creada componemos una
		if (v == null) {
			Context ctx = this.getContext().getApplicationContext();
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			v = vi.inflate(R.layout.infolineas_paradas_item, null);

		}

		TextView numParada;
		TextView descParada;
		TextView datos;

		numParada = (TextView) v.findViewById(R.id.num_parada);
		descParada = (TextView) v.findViewById(R.id.desc_parada);
		datos = (TextView) v.findViewById(R.id.datos_parada);
		
		PlaceMark bus = getItem(position);

		if (bus != null) {
			numParada.setText(bus.getCodigoParada());
			descParada.setText(bus.getTitle());
			datos.setText("T: ".concat(bus.getLineas()));

			if(bus.getObservaciones() != null && !bus.getObservaciones().trim().equals("")){
				
				datos.setText(datos.getText() + "\ni: " + bus.getObservaciones());
				
			}
			
		}

		return v;
	}

	/**
	 * Anade todas las lineas al adapter
	 * 
	 * @param noticias
	 */
	public void addAll(List<PlaceMark> parada) {
		if (parada == null) {
			return;
		}

		for (int i = 0; i < parada.size(); i++) {
			add(parada.get(i));
		}
	}

}
