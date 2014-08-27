/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 *
 *  based on the Copyright (C) 2011 The Android Open Source Project
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
package alberapps.android.tiempobus;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.tasks.ActualizarBDAsyncTask;
import alberapps.android.tiempobus.tasks.ActualizarBDAsyncTask.LoadActualizarBDAsyncTaskResponder;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.android.tiempobus.util.PreferencesUtil;

/**
 * Pantalla de preferencias
 */
public class PreferencesFromXml extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void finish() {

        Intent intent = new Intent();
        setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent);

        super.finish();

    }

    @Override
    @Deprecated
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preference != null && preference.getKey() != null) {

            if (preference.getKey().equals("reiniciar_db")) {

                reiniciarDB();

            } else if (preference.getKey().equals("actualizar_db")) {

                actualizarDB();

            }

        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {

            super.onPreferenceTreeClick(preferenceScreen, preference);

            // Para resolver bug de versiones anteriores
            if (preference != null) {
                if (preference instanceof PreferenceScreen) {
                    if (((PreferenceScreen) preference).getDialog() != null) {

                        ((PreferenceScreen) preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());

                    }
                }
            }

            return false;

        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    /**
     * Reinicia la base de datos
     */
    public void reiniciarDB() {

        getContentResolver().delete(BuscadorLineasProvider.CONTENT_URI, null, null);

    }

    /**
     * Actualiza la base de datos
     */
    public void actualizarDB() {

        final Builder mBuilder = Notificaciones.notificacionBaseDatos(getApplicationContext(), Notificaciones.NOTIFICACION_BD_INICIAL, null, null);

        LoadActualizarBDAsyncTaskResponder loadActualizarBDAsyncTaskResponder = new LoadActualizarBDAsyncTaskResponder() {
            public void ActualizarBDLoaded(String respuesta) {

                if (respuesta.equals("true")) {
                    getContentResolver().update(BuscadorLineasProvider.CONTENT_URI, null, null, null);

                    PreferencesUtil.putUpdateInfo(getApplicationContext(), respuesta, "");

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_descarga_actualizacion), Toast.LENGTH_SHORT).show();

                    Notificaciones.notificacionBaseDatos(getApplicationContext(), Notificaciones.NOTIFICACION_BD_ERROR, mBuilder, null);
                }

            }

        };

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new ActualizarBDAsyncTask(loadActualizarBDAsyncTaskResponder).execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();

            Notificaciones.notificacionBaseDatos(getApplicationContext(), Notificaciones.NOTIFICACION_BD_ERROR, mBuilder, null);

        }

    }

}
