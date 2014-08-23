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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.List;
import java.util.Locale;

import alberapps.android.tiempobus.R;

/**
 * Utilidades de uso en la interfaz
 */
public class UtilidadesUI {

    public static String WIDGET_PACKAGE = "alberapps.android.tiempobuswidgets";
    public static String WIDGET_ACTIVITY = "alberapps.android.tiempobuswidgets.ComunicacionActivity";

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
                contenedorPrincipal.setBackgroundResource(R.color.fondo_g);
            }

        } else {

            contenedorPrincipal.setBackgroundResource(R.color.fondo_g);

        }

    }

    /**
     * Verifica la instalacion del widget
     *
     * @param context
     * @return boolean
     */
    public static boolean verificarWidgetInstalado(Context context) {

        PackageManager manager = context.getPackageManager();

        List<ApplicationInfo> packages = manager.getInstalledApplications(0);

        for (int i = 0; i < packages.size(); i++) {
            if (packages.get(i).packageName.equals(WIDGET_PACKAGE)) {
                return true;
            }
        }

        return false;

    }

    /**
     * Verifica si se trata de una tablet y en horizontal y api >= v14
     *
     * @param contexto
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean pantallaTabletHorizontal(Context contexto) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            Configuration config = contexto.getResources().getConfiguration();

            if (config.smallestScreenWidthDp >= 600 && config.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                return true;

            } else {

                return false;
            }
        } else {
            return false;
        }

    }

	/*
     * public static void enviarEventoAnalytics(Context context, String evento)
	 * {
	 * 
	 * // May return null if a EasyTracker has not yet been initialized with a
	 * // property ID. EasyTracker easyTracker =
	 * EasyTracker.getInstance(context);
	 * 
	 * // MapBuilder.createEvent().build() returns a Map of event fields and //
	 * values // that are set and sent with the hit.
	 * easyTracker.send(MapBuilder.createEvent("Eventos", // Event category //
	 * (required) "funcionalidad", // Event action (required) evento, // Event
	 * label null) // Event value .build());
	 * 
	 * }
	 */

    /**
     * Idioma para la wikipedia
     *
     * @return idioma
     */
    public static String getIdiomaWiki() {

        String idiomaWiki = null;

        try {
            String locale = Locale.getDefault().getDisplayLanguage();

            if (locale.substring(0, 2).equals("es")) {
                idiomaWiki = "es";
            } else if (locale.substring(0, 2).equals("ca")) {
                idiomaWiki = "ca";
            } else if (locale.substring(0, 2).equals("en")) {
                idiomaWiki = "en";
            } else {
                idiomaWiki = "es";
            }

        } catch (Exception e) {
            idiomaWiki = "es";
        }

        return idiomaWiki;

    }

    /**
     * Idioma para openweathermap
     *
     * @return idioma
     */
    public static String getIdiomaOWM() {

        String idioma = null;

        try {
            String locale = Locale.getDefault().getDisplayLanguage();

            if (locale.substring(0, 2).equals("es")) {
                idioma = "es";
            } else if (locale.substring(0, 2).equals("ca")) {
                idioma = "ca";
            } else if (locale.substring(0, 2).equals("en")) {
                idioma = "en";
            } else if (locale.substring(0, 2).equals("fr")) {
                idioma = "fr";
            } else if (locale.substring(0, 2).equals("it")) {
                idioma = "it";
            } else if (locale.substring(0, 2).equals("de")) {
                idioma = "de";
            } else if (locale.substring(0, 2).equals("ru")) {
                idioma = "ru";
            } else {
                idioma = "es";
            }

        } catch (Exception e) {
            idioma = "es";
        }

        return idioma;

    }

    /**
     * Locale adecuado
     *
     * @return
     */
    public static Locale getLocaleInt() {

        Locale loc = null;

        try {
            loc = new Locale("spa", "ES");
        } catch (Exception e) {

        }

        if (loc == null) {
            loc = Locale.US;
        }

        return loc;

    }

    /**
     * Locale adecuado intentando primero el por defecto
     *
     * @return
     */
    public static Locale getLocaleUsuario() {

        Locale loc = null;

        // Por defecto
        try {
            loc = Locale.getDefault();

        } catch (Exception e) {

        }

        // Intanta el ES
        if (loc == null) {
            try {
                loc = new Locale("spa", "ES");
            } catch (Exception ex) {

            }
        }

        // Como ultimo intento
        if (loc == null) {
            loc = Locale.US;
        }

        return loc;

    }

}
