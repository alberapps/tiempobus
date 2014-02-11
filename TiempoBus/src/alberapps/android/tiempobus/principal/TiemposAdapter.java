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

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.mapas.maps2.MapasMaps2Activity;
import alberapps.java.tam.BusLlegada;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

		final BusLlegada bus = getItem(position);

		if (!bus.isSinDatos()) {

			// Si no tenemos la vista de la fila creada componemos una
			// if (v == null) {
			Context ctx = this.getContext().getApplicationContext();
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			v = vi.inflate(R.layout.tiempos_item, null);

			v.setTag(new ViewHolder(v));
			// }

			// Accedemos a la vista cacheada y la rellenamos
			ViewHolder tag = (ViewHolder) v.getTag();

			// BusLlegada bus = getItem(position);
			if (bus != null) {
				tag.busLinea.setText(bus.getLinea().trim());
				tag.busDestino.setText(bus.getDestino().trim());

				// tag.busProximo.setText(bus.getProximo());

				if (bus.getSegundoTram() != null) {

					tag.busProximo.setText(controlAviso(bus.getProximo()).trim() + "\n" + controlAviso(bus.getSegundoTram().getProximo()).trim());

				} else if (bus.getSegundoBus() != null) {

					tag.busProximo.setText(controlAviso(bus.getProximo()).trim() + "\n" + controlAviso(bus.getSegundoBus().getProximo()).trim());

				}else {

					tag.busProximo.setText(controlAviso(bus.getProximo()).trim());
				}

			}

			// Botones
			ImageView alertaText = (ImageView) v.findViewById(R.id.tiempos_alerta_img);

			alertaText.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {

					MainActivity actividad = (MainActivity) contexto;

					// Texto para receiver
					String textoReceiver = actividad.gestionarAlarmas.prepararReceiver(bus, actividad.paradaActual);

					// Activar alarma y mostrar modal
					actividad.gestionarAlarmas.mostrarModalTiemposAlerta(bus, actividad.paradaActual, textoReceiver);

				}

			});

			ImageView compartir = (ImageView) v.findViewById(R.id.compartir_img);

			compartir.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {

					MainActivity actividad = (MainActivity) contexto;

					actividad.datosPantallaPrincipal.shareBus(bus, actividad.paradaActual);

				}

			});

			ImageView leer = (ImageView) v.findViewById(R.id.audio_img);

			leer.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {

					MainActivity actividad = (MainActivity) contexto;

					actividad.datosPantallaPrincipal.cantarLinea(bus);

				}

			});

			ImageView mapa = (ImageView) v.findViewById(R.id.mapa_tarjeta);

			mapa.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {

					MainActivity actividad = (MainActivity) contexto;

					if (actividad.datosPantallaPrincipal.servicesConnected()) {

						Intent i = new Intent(actividad, MapasMaps2Activity.class);
						i.putExtra("LINEA_MAPA", bus.getLinea());
						i.putExtra("LINEA_MAPA_PARADA", Integer.toString(actividad.paradaActual));
						actividad.startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_POSTE);

					}

				}

			});

		} else {

			Context ctx = this.getContext().getApplicationContext();
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.tiempos_item_sin_datos, null);
			// v.setTag(new ViewHolder(v));

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

		// min.
		String nuevoLiteral = traducido.replaceAll("min.", contexto.getString(R.string.literal_min));

		return nuevoLiteral;

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
