/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.android.tiempobus.favoritos;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.app.NavUtils;
import androidx.core.content.ContentResolverCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.data.TiempoBusDb;
import alberapps.android.tiempobus.database.BuscadorLineasProvider;
import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.android.tiempobus.database.Parada;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.data.backup.DatosDriveBackup;

/**
 * Guarda un nuevo favorito
 */
public class FavoritoNuevoActivity extends AppCompatActivity {
    private EditText guiDescripcion;
    private EditText guiTitulo;

    private String numParada;

    SharedPreferences preferencias = null;

    private String datosHorario;

    /**
     * OnCreate....
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configuramos la vista

        setContentView(R.layout.favorito_nuevo);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

        }


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        setupView();
    }

    /**
     * Si no hay numParada cerramos la actividad
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if ((numParada == null) || numParada.equals("")) {
            Toast.makeText(FavoritoNuevoActivity.this, R.string.no_poste, Toast.LENGTH_SHORT).show();

            finish();
        }
    }

    /**
     * Configura la vista
     */
    private void setupView() {
        guiTitulo = (EditText) findViewById(R.id.titulo);
        guiDescripcion = (EditText) findViewById(R.id.descripcion);

        // Fondo
        setupFondoAplicacion();

        // Comprobamos si nos estan pasando como parametro el numParada y la
        // descripcion

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            //Favorito tipo horario
            if (extras.containsKey("HTRAM")) {

                numParada = "0";

                String[] desc = extras.getString("DESCRIPCION").split("::");
                datosHorario = desc[1];
                guiDescripcion.setText("" + desc[0]);

                setTitle(getString(R.string.menu_save));

            } else {

                numParada = "" + extras.getInt("POSTE");

                // Buscar datos para la descripcion
                Parada parada = cargarDescripcionBD();

                if (parada != null) {

                    StringBuffer desc = new StringBuffer();

                    if (parada.getDireccion() != null && !parada.getDireccion().trim().equals("")) {
                        desc.append(getString(R.string.localizacion));
                        desc.append(": ");
                        desc.append(parada.getDireccion());
                        desc.append("\n");
                    }

                    if (parada.getConexion() != null && !parada.getConexion().trim().equals("")) {
                        desc.append(getString(R.string.conexiones));
                        desc.append(": ");
                        desc.append(parada.getConexion());
                        desc.append("\n");
                    }

                    if (!desc.toString().trim().equals("")) {
                        guiDescripcion.setText(desc.toString());
                    } else {
                        guiDescripcion.setText("" + extras.getString("DESCRIPCION"));
                    }

                } else {
                    guiDescripcion.setText("" + extras.getString("DESCRIPCION"));
                }

                setTitle(String.format(getString(R.string.tit_guardar), numParada));

            }
        }



        /*
         * Asignamos el comprotamiento de los botones
         */
        Button guiGo = (Button) findViewById(R.id.boton_go);
        guiGo.setOnClickListener(guiGoOnClickListener);


    }

    /**
     * Escuchar el boton de guardar
     */
    OnClickListener guiGoOnClickListener = new OnClickListener() {
        public void onClick(View v) {

            if (!guiTitulo.getText().toString().equals("")) {

                ContentValues values = new ContentValues();

                values.put(TiempoBusDb.Favoritos.TITULO, guiTitulo.getText().toString());

                //Para horarios tram
                String desc = "";
                if (datosHorario != null && !datosHorario.equals("")) {
                    desc = guiDescripcion.getText().toString() + "::" + datosHorario;
                } else {
                    desc = guiDescripcion.getText().toString();
                }

                values.put(TiempoBusDb.Favoritos.DESCRIPCION, desc);
                values.put(TiempoBusDb.Favoritos.POSTE, Integer.valueOf(numParada));

                getContentResolver().insert(TiempoBusDb.Favoritos.CONTENT_URI, values);

                //Datos para copia de seguridad
                Date fechaDB = DatosDriveBackup.datosArchivoDB();
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putLong("drive_local_db", fechaDB.getTime());
                editor.apply();
                ////


                Intent intent = new Intent();
                setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent);
                finish();

            } else {
                Toast.makeText(FavoritoNuevoActivity.this, R.string.obligatorio, Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = findViewById(R.id.contenedor_nuevo);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

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

    /**
     * Carga la descripcion desde la base de datos
     *
     * @return parada
     */
    private Parada cargarDescripcionBD() {

        Cursor cursor = null;

        try {

            String parametros[] = {numParada};

            //Cursor cursor = managedQuery(BuscadorLineasProvider.DATOS_PARADA_URI, null, null, parametros, null);
            cursor = ContentResolverCompat.query(getContentResolver(), BuscadorLineasProvider.DATOS_PARADA_URI, null, null, parametros, null, null);

            if (cursor != null) {
                List<Parada> listaParadas = new ArrayList<>();

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    Parada par = new Parada();

                    par.setLineaNum(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_NUM)));
                    par.setLineaDesc(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_LINEA_DESC)));
                    par.setConexion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_CONEXION)));
                    par.setCoordenadas(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_COORDENADAS)));
                    par.setDestino(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DESTINO)));
                    par.setDireccion(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_DIRECCION)));
                    par.setLatitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LATITUD)));
                    par.setLongitud(cursor.getInt(cursor.getColumnIndex(DatosLineasDB.COLUMN_LONGITUD)));
                    par.setParada(cursor.getString(cursor.getColumnIndex(DatosLineasDB.COLUMN_PARADA)));

                    listaParadas.add(par);
                }

                return listaParadas.get(0);

            } else {
                return null;

            }

        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

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
