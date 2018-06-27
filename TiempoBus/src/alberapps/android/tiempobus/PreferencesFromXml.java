/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p>
 * based on the Copyright (C) 2011 The Android Open Source Project
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
package alberapps.android.tiempobus;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
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
public class PreferencesFromXml extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
        }*/

        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        //bindPreferenceSummaryToValue(findPreference("noticias_tono"));
        //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key_list)));


    }

    @Override
    public void finish() {

        Intent intent = new Intent();
        setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent);

        super.finish();

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
        if (preference instanceof ListPreference) {
// For list preferences, look up the correct display value in
// the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
// For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
// Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);
// Trigger the listener immediately with the preference's
// current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    @Override
    @Deprecated
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preference != null && preference.getKey() != null) {

            if (preference.getKey().equals("reiniciar_db")) {

                reiniciarDB();

            } else if (preference.getKey().equals("actualizar_db")) {

                actualizarDB();

            } else if (preference.getKey().equals("channel_settings_alarmas")) {

                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, Notificaciones.CHANNEL_ALERTABUS);
                startActivity(intent);

            } else if (preference.getKey().equals("channel_settings_noticias")) {

                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, Notificaciones.CHANNEL_NOTICIAS);
                startActivity(intent);
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
