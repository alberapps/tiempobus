/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.android.tiempobus.appinfo;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tram.UtilidadesTRAM;
import androidx.core.app.NavUtils;

/**
 * Informacion de la app
 */
public class AppInfoActivity extends AppCompatActivity {

    private SharedPreferences preferencias = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);


        setContentView(R.layout.infoapp_3);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ImageView botonGpl = (ImageView) findViewById(R.id.boton_gpl);
        assert botonGpl != null;
        botonGpl.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View arg0) {

                UtilidadesUI.openWebPage(AppInfoActivity.this, "http://www.gnu.org/licenses/gpl-3.0-standalone.html");

            }
        });


        if (UtilidadesTRAM.ACTIVADO_TRAM) {
            TextView botonFGV = (TextView) findViewById(R.id.imageLogoFGV);
            assert botonFGV != null;
            botonFGV.setOnClickListener(new ImageButton.OnClickListener() {
                public void onClick(View arg0) {

                    UtilidadesUI.openWebPage(AppInfoActivity.this, "http://www.fgv.es");

                }
            });

            TextView botonTram = (TextView) findViewById(R.id.imageLogoTram);
            assert botonTram != null;
            botonTram.setOnClickListener(new ImageButton.OnClickListener() {
                public void onClick(View arg0) {

                    UtilidadesUI.openWebPage(AppInfoActivity.this, "http://www.tramalicante.es");

                }
            });

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
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onStop() {

        super.onStop();

    }

}
