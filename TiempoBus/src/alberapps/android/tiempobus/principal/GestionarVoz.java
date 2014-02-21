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

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.util.Log;

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

	
	

	public void reconocerVoz() {

		try{
		
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Número de parada");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
		context.startActivityForResult(intent, VOICE_REQUEST_CODE);
		
		
		}catch(ActivityNotFoundException e){
			
			
			
		}
		

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
	 * Decidir si galeria o fondo de color
	 * 
	 */
	public void seleccionarPosibleOpcion(ArrayList<String> resultados) {

		CharSequence[] items = resultados.toArray(new CharSequence[resultados.size()]);
		
		//final CharSequence[] items = { context.getResources().getString(R.string.seleccion_fondo_1), context.getResources().getString(R.string.seleccion_fondo_2),
			//	context.getResources().getString(R.string.seleccion_fondo_3) };

		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.preferencias_imagen);

		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				if (item == 0) {

					

				} else if (item == 1) {
					
				}

				

			}
		});

		AlertDialog alert = builder.create();

		alert.show();

	}

	

}
