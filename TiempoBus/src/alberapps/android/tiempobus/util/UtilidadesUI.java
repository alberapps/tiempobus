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
package alberapps.android.tiempobus.util;

import alberapps.android.tiempobus.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;

public class UtilidadesUI {

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public static Bitmap decodeBitmapFromFile(String res, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(res, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(res, options);
	}
	
	
	
	public static void setupFondoAplicacion(String fondoGaleria, View contenedorPrincipal, Activity actividad) {

		if (!fondoGaleria.equals("")) {

			Drawable dr = null;

			// Bitmap bitmapCargado = BitmapFactory.decodeFile(fondo_galeria);

			DisplayMetrics displaymetrics = new DisplayMetrics();
			actividad.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int height = displaymetrics.heightPixels;
			int width = displaymetrics.widthPixels;

			Bitmap bitmapCargado = UtilidadesUI.decodeBitmapFromFile(fondoGaleria, width, height);

			// Bitmap bitmapRecortado = recortarBitmap(bitmap);
			if (bitmapCargado != null)
				dr = new BitmapDrawable(bitmapCargado);

			if (dr != null) {
				contenedorPrincipal.setBackgroundDrawable(dr);
			} else {
				contenedorPrincipal.setBackgroundResource(android.R.color.darker_gray);
			}

		} else {

			contenedorPrincipal.setBackgroundResource(android.R.color.darker_gray);

		}

	}
	
	
	
	
	

}
