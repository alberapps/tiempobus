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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.analytics.GoogleAnalytics;

import alberapps.android.tiempobus.R;
import alberapps.java.tram.UtilidadesTRAM;

/**
 * Informacion de la app
 */
public class AppInfoActivity extends ActionBarActivity {

    SharedPreferences preferencias = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        if (!UtilidadesTRAM.ACTIVADO_TRAM) {
            setContentView(R.layout.infoapp_3_bus);
        } else {
            setContentView(R.layout.infoapp_3);
        }


		/*ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
			actionBar.setDisplayHomeAsUpEnabled(true);
		}*/


        ImageView botonGpl = (ImageView) findViewById(R.id.boton_gpl);
        botonGpl.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View arg0) {

                Uri uri = Uri.parse("http://www.gnu.org/licenses/gpl-3.0-standalone.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        Button botonLink = (Button) findViewById(R.id.buttonLinks);
        botonLink.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                links();

            }
        });

        if (UtilidadesTRAM.ACTIVADO_TRAM) {
            ImageView botonFGV = (ImageView) findViewById(R.id.imageLogoFGV);
            botonFGV.setOnClickListener(new ImageButton.OnClickListener() {
                public void onClick(View arg0) {

                    Uri uri = Uri.parse("http://www.fgv.es");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                }
            });

            ImageView botonTram = (ImageView) findViewById(R.id.imageLogoTram);
            botonTram.setOnClickListener(new ImageButton.OnClickListener() {
                public void onClick(View arg0) {

                    Uri uri = Uri.parse("http://www.tramalicante.es");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                }
            });

        }

    }

    private void links() {

        final CharSequence[] items = {"zgzBus\nhttp://code.google.com/p/zgzbus", "ksoap2-android\nhttp://code.google.com/p/ksoap2-android/", "ZXing Team\nhttp://code.google.com/p/zxing",
                "Iconspedia(Yankoa)\nhttp://www.iconspedia.com/community/yankoa", "Mapicons\nhttp://mapicons.nicolasmollet.com", "JSoup\nhttp://jsoup.org", "google-gson\nhttp://code.google.com/p/google-gson",
                "twitter4j\nhttp://twitter4j.org"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.referencias);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {

                } else if (item == 1) {

                }

            }
        });

        AlertDialog alert = builder.create();

        alert.show();

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
            // EasyTracker.getInstance(this).activityStart(this);
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }

    }

    @Override
    protected void onStop() {

        if (preferencias.getBoolean("analytics_on", true)) {
            // EasyTracker.getInstance(this).activityStop(this);
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }

        super.onStop();

    }

}
