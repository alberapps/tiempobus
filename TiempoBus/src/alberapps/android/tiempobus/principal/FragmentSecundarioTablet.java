/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
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
package alberapps.android.tiempobus.principal;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.util.UtilidadesUI;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragmento principal
 * 
 * 
 */
public class FragmentSecundarioTablet extends Fragment {

	MainActivity actividad;

	/**
	 * On Create
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		actividad = (MainActivity) getActivity();

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		

		super.onViewCreated(view, savedInstanceState);

	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {

		setupFondoAplicacion();
		
		actualizarDatos();

		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_secun_detalle, container, false);
	}

	OnHeadlineSelectedListener mCallback;

	// Container Activity must implement this interface
	public interface OnHeadlineSelectedListener {
		public void onArticleSelected(int position);
	}

	public void actualizarDatos() {

		String parametros[] = { Integer.toString(actividad.paradaActual) };

		Cursor cursor = actividad.managedQuery(BuscadorLineasProvider.DATOS_PARADA_URI, null, null, parametros, null);

		// Uri data = Uri.withAppendedPath(BuscadorLineasProvider.CONTENT_URI,
		// String.valueOf(id));

		// Uri uri = getIntent().getData();
		// Cursor cursor = actividad.managedQuery(uri, null, null, null, null);

		if (cursor == null) {

		} else {
			cursor.moveToFirst();

			TextView parada = (TextView) actividad.findViewById(R.id.parada);
			TextView linea = (TextView) actividad.findViewById(R.id.linea);
			TextView destino = (TextView) actividad.findViewById(R.id.destino);
			TextView localizacion = (TextView) actividad.findViewById(R.id.localizacion);
			TextView conexiones = (TextView) actividad.findViewById(R.id.conexiones);

			TextView observaciones = (TextView) actividad.findViewById(R.id.observaciones);

			int paradaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_PARADA);
			int lineaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LINEA_DESC);
			int direccionIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_DIRECCION);
			int conexionesIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_CONEXION);
			int destinoIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_DESTINO);

			int numLineaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LINEA_NUM);

			int observacionesIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_OBSERVACIONES);

			parada.setText(cursor.getString(paradaIndex));

			// paradaSel = cursor.getString(paradaIndex);
			// lineaSel = cursor.getString(numLineaIndex);

			linea.setText(cursor.getString(lineaIndex));
			destino.setText(cursor.getString(destinoIndex));
			localizacion.setText(cursor.getString(direccionIndex));
			conexiones.setText(cursor.getString(conexionesIndex));

			observaciones.setText(cursor.getString(observacionesIndex));
		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (OnHeadlineSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}

	/**
	 * Seleccion del fondo de la galeria en el arranque
	 */
	private void setupFondoAplicacion() {

		PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
		SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

		String fondo_galeria = preferencias.getString("image_galeria", "");

		View contenedor_principal = getActivity().findViewById(R.id.contenedor_secundario);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());

	}

}
