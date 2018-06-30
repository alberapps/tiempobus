/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p>
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import alberapps.java.util.Datos;
import alberapps.java.util.GestionarDatos;
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
        editor.apply();

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
        editor.apply();

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
        editor.apply();

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
        editor.apply();

    }


    /**
     * Control cache datos
     */

    /**
     * Crear
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
        editor.apply();

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

    public static String LISTA_PARADAS_DESTACADAS = "lista_fav_destacados";


    public static void guardarParada(Context context, String lista, String parada) {

        SharedPreferences preferencias = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferencias = context.getSharedPreferences("preflistas", Context.MODE_MULTI_PROCESS);

        } else {

            preferencias = context.getSharedPreferences("preflistas", 0);

        }

        String prefDatos = preferencias.getString(lista, "");

        List<Datos> listaDatos = GestionarDatos.listaDatos2(prefDatos);

        if (listaDatos == null) {
            listaDatos = new ArrayList<>();
        }

        //Nuevo dato
        Datos dato = new Datos();
        dato.setParada(parada);

        listaDatos.add(dato);

        String nuevaLista = GestionarDatos.getStringDeLista2(listaDatos);

        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(lista, nuevaLista);
        editor.apply();

    }


    public static void eliminarParada(Context context, String lista, String parada) {

        SharedPreferences preferencias = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferencias = context.getSharedPreferences("preflistas", Context.MODE_MULTI_PROCESS);

        } else {

            preferencias = context.getSharedPreferences("preflistas", 0);

        }

        String prefDatos = preferencias.getString(lista, "");


        List<Datos> listaDatos = GestionarDatos.listaDatos2(prefDatos);

        if (listaDatos == null) {
            listaDatos = new ArrayList<>();
        }


        //Objeto a eliminar
        Datos dato = new Datos();
        dato.setParada(parada);

        listaDatos.remove(dato);


        String nuevaLista = GestionarDatos.getStringDeLista2(listaDatos);

        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(lista, nuevaLista);
        editor.apply();

    }

    public static List<Datos> recuperarLista(Context context, String lista) {

        SharedPreferences preferencias = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            preferencias = context.getSharedPreferences("preflistas", Context.MODE_MULTI_PROCESS);

        } else {

            preferencias = context.getSharedPreferences("preflistas", 0);

        }

        String prefDatos = preferencias.getString(lista, "");


        List<Datos> listaDatos = GestionarDatos.listaDatos2(prefDatos);

        if (listaDatos == null) {
            listaDatos = new ArrayList<>();
        }


        return listaDatos;

    }

}
