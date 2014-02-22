/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.helper.StringUtil;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.data.TiempoBusDb;
import alberapps.android.tiempobus.favoritos.FavoritosActivity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.EditText;

/**
 * Funciones de reconocimiento de voz
 * 
 * 
 */
public class GestionarVoz {

	/**
	 * Cotexto principal
	 */
	private MainActivity context;

	private SharedPreferences preferencias;

	public static final int VOICE_REQUEST_CODE = 4000;

	public GestionarVoz(MainActivity contexto, SharedPreferences preferencia) {

		context = contexto;

		preferencias = preferencia;

	}

	/**
	 * Intent de reconocimiento de voz
	 */
	public boolean reconocerVoz() {

		try {

			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Número de parada");
			intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
			context.startActivityForResult(intent, VOICE_REQUEST_CODE);

		} catch (ActivityNotFoundException e) {

			return false;

		}

		return true;

	}

	public boolean reconocimientoDisponible() {

		PackageManager manager = context.getPackageManager();
		List<ResolveInfo> actividades = manager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

		if (actividades.size() == 0) {

			Log.i("VOZ", "Reconocimiento no disponible");

			return false;

		}

		return true;

	}

	/**
	 * Modal que muestra los posibles resultados del reconocimiento de voz
	 * 
	 */
	public boolean seleccionarPosibleOpcion(ArrayList<String> resultados) {

		// Procesa los resultados
		final List<DatosVoz> datosVoz = procesarResultados(resultados);

		if (datosVoz == null || datosVoz.isEmpty()) {
			return false;
		}

		CharSequence[] items = datosAcaracter(datosVoz);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.reconocimiento_voz_titulo);

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				
				if(datosVoz.get(item).isPosibleParada()){
					
					context.paradaActual = Integer.parseInt(datosVoz.get(item).getResultado());

					// Poner en campo de poste
					EditText txtPoste = (EditText) context.findViewById(R.id.campo_poste);
					txtPoste.setText(Integer.toString(context.paradaActual));

					SharedPreferences.Editor editor = preferencias.edit();
					editor.putInt("parada_inicio", context.paradaActual);
					editor.commit();

					context.handler.sendEmptyMessageDelayed(MainActivity.MSG_RECARGA, MainActivity.DELAY_RECARGA);
					
				}else if(datosVoz.get(item).getFavoritoParada() != null){
					
					context.paradaActual = Integer.parseInt(datosVoz.get(item).getFavoritoParada());

					// Poner en campo de poste
					EditText txtPoste = (EditText) context.findViewById(R.id.campo_poste);
					txtPoste.setText(Integer.toString(context.paradaActual));

					SharedPreferences.Editor editor = preferencias.edit();
					editor.putInt("parada_inicio", context.paradaActual);
					editor.commit();

					context.handler.sendEmptyMessageDelayed(MainActivity.MSG_RECARGA, MainActivity.DELAY_RECARGA);
					
					
				}
				
				
			}
		});

		AlertDialog alert = builder.create();

		alert.show();

		return true;

	}

	/**
	 * Para obtener posibles paradas y o favoritos
	 * 
	 * @param resultados
	 * @return lista
	 */
	private List<DatosVoz> procesarResultados(ArrayList<String> resultados) {

		HashMap<String, String> datosFav = cargarDatosFavoritos();

		List<DatosVoz> datosVoz = new ArrayList<DatosVoz>();

		DatosVoz dato = null;

		for (int i = 0; i < resultados.size(); i++) {

			dato = null;

			// Quitar espacios en blanco
			String resultadoParada = resultados.get(i).trim().replaceAll(" ", "");
			String resultadoFavorito = resultados.get(i).trim();

			DatosVoz buscar = new DatosVoz();
			buscar.setResultado(resultadoParada);

			if (datosVoz.contains(buscar)) {
				continue;
			}

			if (!resultadoParada.equals("") && resultadoParada.length() <= 4 && StringUtil.isNumeric(resultadoParada)) {

				// Posible parada o tambien posible favorito

				dato = new DatosVoz();
				dato.setResultado(resultadoParada);
				dato.setPosibleParada(true);
				dato.setPosibleFavorito(false);
				dato.setDescripcion("Parada: " + resultadoParada);

				datosVoz.add(dato);

				// Posible favorito?

				String parada = existeFavorito(resultadoParada, datosFav);

				if (parada != null) {

					dato = new DatosVoz();
					dato.setResultado(resultadoParada);
					dato.setPosibleParada(false);
					dato.setPosibleFavorito(true);
					dato.setDescripcion("Favorito: " + resultadoParada + " (" + parada + ")");
					dato.setFavoritoParada(parada);

					datosVoz.add(dato);

				}

			} else if (!resultadoFavorito.equals("")) {

				// Posible favorito?

				String parada = existeFavorito(resultadoFavorito, datosFav);

				if (parada != null) {

					dato = new DatosVoz();

					dato.setResultado(resultadoFavorito);
					dato.setPosibleParada(false);
					dato.setPosibleFavorito(true);
					dato.setDescripcion("Favorito: " + resultadoFavorito + " (" + parada + ")");
					dato.setFavoritoParada(parada);

					datosVoz.add(dato);

				}

			}

		}

		return datosVoz;

	}

	/**
	 * charsecquence para el listado
	 * 
	 * @param datosVoz
	 * @return datos
	 */
	private CharSequence[] datosAcaracter(List<DatosVoz> datosVoz) {

		CharSequence[] items = new CharSequence[datosVoz.size()];

		for (int i = 0; i < datosVoz.size(); i++) {

			items[i] = datosVoz.get(i).getDescripcion();

		}

		return items;
	}

	/**
	 * Es un posible vavorito? Si lo es devuelve la parada que le corresponderia
	 * 
	 * @param dato
	 * @param datosFav
	 * @return parada
	 */
	private String existeFavorito(String dato, HashMap<String, String> datosFav) {

		if (datosFav.containsKey(dato)) {

			return datosFav.get(dato);

		} else {
			return null;
		}

	}

	/**
	 * Carga los favoritos en un hashmap
	 * 
	 * @return hashmap
	 */
	private HashMap<String, String> cargarDatosFavoritos() {

		try {
			HashMap<String, String> datosFav = new HashMap<String, String>();

			Cursor cursor = context.managedQuery(TiempoBusDb.Favoritos.CONTENT_URI, FavoritosActivity.PROJECTION, null, null, TiempoBusDb.Favoritos.DEFAULT_SORT_ORDER);

			if (cursor != null) {

				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					datosFav.put(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.TITULO)), cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.POSTE)));
				}

			}

			return datosFav;

		} catch (Exception e) {
			return null;
		}

	}

}
