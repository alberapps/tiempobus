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

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

/**
 * 
 * Pantalla de preferencias
 * 
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

}
