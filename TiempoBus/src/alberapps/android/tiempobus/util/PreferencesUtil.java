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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import alberapps.java.util.Utilidades;

/**
 * Utilidades para gestionar preferencias
 */
public class PreferencesUtil {

    /**
     * Recuperar aviso
     *
     * @param context
     * @return
     */
    public static String getAlertaInfo(Context context) {

        SharedPreferences preferenciasAlertas = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferenciasAlertas = context.getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);

        } else {

            preferenciasAlertas = context.getSharedPreferences("prefalertas", 0);

        }


        String aviso = preferenciasAlertas.getString("alerta", "");


        return aviso;

    }

    /**
     * Eliminar pref de alerta
     *
     * @param context
     */
    public static void clearAlertaInfo(Context context) {

        SharedPreferences preferenciasAlertas = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferenciasAlertas = context.getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);

        } else {

            preferenciasAlertas = context.getSharedPreferences("prefalertas", 0);

        }

        //Quitar info de la alarma
        SharedPreferences.Editor editor = preferenciasAlertas.edit();
        editor.putString("alerta", "");
        editor.commit();

    }

    /**
     * Crear pref de alerta
     *
     * @param context
     * @param info
     */
    public static void putAlertaInfo(Context context, String info) {

        SharedPreferences preferenciasAlertas = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferenciasAlertas = context.getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);

        } else {

            preferenciasAlertas = context.getSharedPreferences("prefalertas", 0);

        }

        //Quitar info de la alarma
        SharedPreferences.Editor editor = preferenciasAlertas.edit();
        editor.putString("alerta", info);
        editor.commit();

    }

    /**
     * Recuperar pref de actualizaciones
     *
     * @param context
     * @return
     */
    public static String getUpdateInfo(Context context) {

        SharedPreferences preferenciasAlertas = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferenciasAlertas = context.getSharedPreferences("prefupdate", Context.MODE_MULTI_PROCESS);

        } else {

            preferenciasAlertas = context.getSharedPreferences("prefupdate", 0);

        }


        String aviso = preferenciasAlertas.getString("update", "");

        if (aviso.equals("true")) {
            return "";
        }


        return aviso;

    }

    /**
     * Recuperar pref de actualizaciones
     *
     * @param context
     * @return
     */
    public static String getUpdateIgnorarInfo(Context context) {

        SharedPreferences preferenciasAlertas = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferenciasAlertas = context.getSharedPreferences("prefupdate", Context.MODE_MULTI_PROCESS);

        } else {

            preferenciasAlertas = context.getSharedPreferences("prefupdate", 0);

        }


        String aviso = preferenciasAlertas.getString("ignorar", "");


        return aviso;

    }

    /**
     * Eliminar pref de actualizaciones
     *
     * @param context
     */
    public static void clearUpdateInfo(Context context) {

        SharedPreferences preferenciasAlertas = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferenciasAlertas = context.getSharedPreferences("prefupdate", Context.MODE_MULTI_PROCESS);

        } else {

            preferenciasAlertas = context.getSharedPreferences("prefupdate", 0);

        }

        //Quitar info de la alarma
        SharedPreferences.Editor editor = preferenciasAlertas.edit();
        editor.putString("update", "");
        editor.putString("ignorar", "");
        editor.commit();

    }

    /**
     * Actualizar pref de actulizaciones
     *
     * @param context
     * @param info
     * @param ignorar
     */
    public static void putUpdateInfo(Context context, String info, String ignorar) {

        SharedPreferences preferenciasAlertas = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferenciasAlertas = context.getSharedPreferences("prefupdate", Context.MODE_MULTI_PROCESS);

        } else {

            preferenciasAlertas = context.getSharedPreferences("prefupdate", 0);

        }

        //Quitar info de la alarma
        SharedPreferences.Editor editor = preferenciasAlertas.edit();

        if (info.equals("true")) {
            info = Utilidades.getFechaControl();
        }

        editor.putString("update", info);


        editor.putString("ignorar", ignorar);
        editor.commit();

    }


    /**
     * Control cache datos
     */

    /**
     * Crear pref de alerta
     *
     * @param context
     * @param campo
     */
    public static void putCache(Context context, String campo, String valor) {

        SharedPreferences preferenciasCache = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferenciasCache = context.getSharedPreferences("prefcache", Context.MODE_MULTI_PROCESS);

        } else {

            preferenciasCache = context.getSharedPreferences("prefcache", 0);

        }

        //Guardar valor
        SharedPreferences.Editor editor = preferenciasCache.edit();
        editor.putString(campo, valor);
        editor.commit();

    }


    /**
     * Recuperar pref de actualizaciones
     *
     * @param context
     * @return
     */
    public static String getCache(Context context, String campo) {

        SharedPreferences preferenciasCache = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferenciasCache = context.getSharedPreferences("prefcache", Context.MODE_MULTI_PROCESS);

        } else {

            preferenciasCache = context.getSharedPreferences("prefcache", 0);

        }


        String valor = preferenciasCache.getString(campo, "");




        return valor;

    }




}
