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
package alberapps.android.tiempobus.data;

import java.util.ArrayList;

import alberapps.android.tiempobus.R;
import alberapps.java.tam.BusLinea;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Lista lineas
 * 
 */
public class BusAdapter extends ArrayAdapter<BusLinea> {
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 */
	public BusAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	/**
	 * Genera la vista de cada uno de los items del listado
	 */
	public View getView(int position, View v, ViewGroup parent) {		
		if (v == null) {
			Context ctx = this.getContext().getApplicationContext();
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			v = vi.inflate(R.layout.buses_item, null);
		}

		if (this.getCount() > 0) {
			BusLinea bus = getItem(position);
			if (bus != null) {
				TextView busLinea = (TextView) v.findViewById(R.id.buses_l);
				busLinea.setText(bus.getLinea());
			}
		}

		return v;
	}

	/**
	 * Anade todas las lineas al adapter
	 * 
	 * @param lineasBus
	 */
	public void addAll(ArrayList<BusLinea> lineasBus) {
		if(lineasBus == null) { return; }
		
		for (int i = 0; i < lineasBus.size(); i++) {
			add(lineasBus.get(i));
		}
	}

}

