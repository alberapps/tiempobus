/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 *
 *  based on code by The Android Open Source Project
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
package alberapps.android.tiempobus.buscador;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContentResolverCompat;
import androidx.core.view.MenuItemCompat;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.mapas.MapasActivity;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.UtilidadesTAM;
import alberapps.java.tram.UtilidadesTRAM;

/**
 * Displays a word and its definition.
 */
public class DatosParadaActivity extends AppCompatActivity {

    String paradaSel = "";
    String lineaSel = "";

    SharedPreferences preferencias = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_parada);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        // Fondo
        setupFondoAplicacion();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

        }

        //Status bar color init
        UtilidadesUI.initStatusBar(this);

        Uri uri = getIntent().getData();
        //Cursor cursor = managedQuery(uri, null, null, null, null);
        Cursor cursor = ContentResolverCompat.query(getContentResolver(), uri, null, null, null, null, null);

        if (cursor == null) {
            finish();
        } else {
            cursor.moveToFirst();

            TextView parada = (TextView) findViewById(R.id.parada);
            TextView linea = (TextView) findViewById(R.id.linea);
            TextView destino = (TextView) findViewById(R.id.destino);
            TextView localizacion = (TextView) findViewById(R.id.localizacion);
            TextView conexiones = (TextView) findViewById(R.id.conexiones);

            TextView observaciones = (TextView) findViewById(R.id.observaciones);

            int paradaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_PARADA);
            int lineaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LINEA_DESC);
            int direccionIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_DIRECCION);
            int conexionesIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_CONEXION);
            int destinoIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_DESTINO);

            int numLineaIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_LINEA_NUM);

            int observacionesIndex = cursor.getColumnIndexOrThrow(DatosLineasDB.COLUMN_OBSERVACIONES);

            assert parada != null;
            parada.setText(cursor.getString(paradaIndex));

            paradaSel = cursor.getString(paradaIndex);
            lineaSel = cursor.getString(numLineaIndex);

            assert linea != null;
            linea.setText(cursor.getString(lineaIndex));
            assert destino != null;
            destino.setText(cursor.getString(destinoIndex));
            assert localizacion != null;
            localizacion.setText(cursor.getString(direccionIndex));
            assert conexiones != null;
            conexiones.setText(cursor.getString(conexionesIndex));

            assert observaciones != null;
            observaciones.setText(cursor.getString(observacionesIndex));

            cursor.close();
        }

        // boton parada
        TextView botonPoste = (TextView) findViewById(R.id.buttonT);
        assert botonPoste != null;
        botonPoste.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                int codigo = -1;

                try {
                    codigo = Integer.parseInt(paradaSel);

                } catch (Exception e) {

                }

                if (codigo != -1 && (paradaSel.length() == 4 || DatosPantallaPrincipal.esTram(paradaSel) || DatosPantallaPrincipal.esTramRt(paradaSel))) {

                    cargarTiempos(codigo);

                } else {

                    Toast.makeText(getApplicationContext(), getString(R.string.error_codigo), Toast.LENGTH_SHORT).show();

                }

            }
        });

        // boton mapa
        TextView botonMapa = (TextView) findViewById(R.id.buttonM);
        assert botonMapa != null;
        botonMapa.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {

                launchMapasSeleccion(lineaSel);

            }
        });

    }

    /**
     * Cargar los tiempos
     *
     * @param codigo
     */
    private void cargarTiempos(int codigo) {

        /*Intent intent = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();
        b.putInt("poste", codigo);
        intent.putExtras(b);

        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("parada_inicio", codigo);
        editor.apply();

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);*/

        //Devolver nueva parada
        Intent intent = new Intent();
        Bundle b = new Bundle();
        b.putInt("POSTE", codigo);
        intent.putExtras(b);

        setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent);
        finish();

    }

    /**
     * Acceder al mapa de la linea seleccionada
     *
     * @param linea
     */
    private void launchMapasSeleccion(String linea) {

        if (DatosPantallaPrincipal.servicesConnectedActivity(this)) {

            if (linea != null && !linea.equals("")) {

                try {
                    Intent i = new Intent(this, MapasActivity.class);
                    i.putExtra("LINEA_MAPA_FICHA", linea);

                    if (UtilidadesTRAM.esLineaTram(linea)) {

                        int pos = UtilidadesTRAM.getIdLinea(linea);

                        i.putExtra("LINEA_MAPA_FICHA_KML", "");
                        i.putExtra("LINEA_MAPA_FICHA_DESC", UtilidadesTRAM.DESC_LINEA[pos]);

                    } else {

                        int pos = UtilidadesTAM.getIdLinea(linea);

                        i.putExtra("LINEA_MAPA_FICHA_KML", "");
                        i.putExtra("LINEA_MAPA_FICHA_DESC", UtilidadesTAM.LINEAS_DESCRIPCION[pos]);

                    }

                    i.putExtra("LINEA_MAPA_PARADA", paradaSel);


                    //startActivity(i);
                    startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                } catch (Exception e) {

                    Toast.makeText(this, getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT).show();

                }

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == MainActivity.SUB_ACTIVITY_RESULT_OK) {
            switch (requestCode) {
                case MainActivity.SUB_ACTIVITY_REQUEST_PARADA:

                    if (data.getExtras() != null) {
                        Bundle b = data.getExtras();
                        if (b.containsKey("POSTE")) {

                            cargarTiempos(b.getInt("POSTE"));

                        }
                    }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buscador, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);

    }

    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = findViewById(R.id.datos_contenedor);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

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
