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
package alberapps.android.tiempobus.principal;

import alberapps.android.tiempobus.R;
import alberapps.java.tam.BusLlegada;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adaptador Tiempos
 */
public class TiemposAdapter extends ArrayAdapter<BusLlegada> {

	private Context contexto;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 */
	public TiemposAdapter(Context context, int textViewResourceId) {
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

			v = vi.inflate(R.layout.tiempos_item, null);

			v.setTag(new ViewHolder(v));
		}

		// Accedemos a la vista cacheada y la rellenamos
		ViewHolder tag = (ViewHolder) v.getTag();

		BusLlegada bus = getItem(position);
		if (bus != null) {
			tag.busLinea.setText(bus.getLinea().trim());
			tag.busDestino.setText(bus.getDestino().trim());

			// tag.busProximo.setText(bus.getProximo());

			if (bus.getSegundoTram() != null) {

				tag.busProximo.setText(controlAviso(bus.getProximo()).trim() + "\n" + controlAviso(bus.getSegundoTram().getProximo()).trim());

			} else {

				tag.busProximo.setText(controlAviso(bus.getProximo()).trim());
			}

		}

		return v;
	}

	/**
	 * Modificación para traducir por idioma
	 * 
	 * @param proximo
	 * @return
	 */
	private String controlAviso(String proximo) {

		String traducido = "";

		String[] procesa = proximo.split(";");

		// TODO para el TRAM
		if (procesa[0].equals("TRAM")) {
			return procesa[1];
		}

		String tiempo1 = "";
		String tiempo2 = "";

		if (procesa[0].equals("enlaparada")) {

			tiempo1 = (String) contexto.getResources().getText(R.string.tiempo_m_1);

		} else if (procesa[0].equals("sinestimacion")) {

			tiempo1 = (String) contexto.getResources().getText(R.string.tiempo_m_2);

		} else {

			tiempo1 = procesa[0];

		}

		if (procesa[1].equals("enlaparada")) {

			tiempo2 = (String) contexto.getResources().getText(R.string.tiempo_m_1);

		} else if (procesa[1].equals("sinestimacion")) {

			tiempo2 = (String) contexto.getResources().getText(R.string.tiempo_m_2);

		} else {

			tiempo2 = procesa[1];

		}

		traducido = tiempo1 + " " + contexto.getResources().getText(R.string.tiempo_m_3) + " " + tiempo2;

		return traducido;

	}

	/*
	 * Clase contendora de los elementos de la vista de fila para agilizar su
	 * acceso
	 */
	private class ViewHolder {
		TextView busLinea;
		TextView busDestino;
		TextView busProximo;

		public ViewHolder(View v) {
			busLinea = (TextView) v.findViewById(R.id.bus_linea);
			busDestino = (TextView) v.findViewById(R.id.bus_destino);
			busProximo = (TextView) v.findViewById(R.id.bus_proximo);
		}

	}

}