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
package alberapps.android.tiempobus.appinfo;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.analytics.GoogleAnalytics;

import alberapps.android.tiempobus.R;

/**
 * Informacion de la app
 */
public class TraduccionesActivity extends ActionBarActivity {

    SharedPreferences preferencias = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traducciones);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);


		ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
			//actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

		}


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sin_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {

        super.onStart();

        if (preferencias.getBoolean("analytics_on", true)) {

            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }

    }

    @Override
    protected void onStop() {

        if (preferencias.getBoolean("analytics_on", true)) {

            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }

        super.onStop();


    }

}
