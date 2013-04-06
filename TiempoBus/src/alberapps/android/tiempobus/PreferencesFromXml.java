/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package alberapps.android.tiempobus;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

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

}
