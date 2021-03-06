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
package alberapps.android.tiempobus.noticias;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.TextSize;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.tasks.LoadDetalleNoticiaAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDetalleNoticiaAsyncTask.LoadDetalleNoticiaAsyncTaskResponder;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.Noticias;
import alberapps.java.util.Utilidades;

/**
 * Detalle de la noticia
 */
public class DetalleNoticiaActivity extends AppCompatActivity {

    private ProgressDialog dialog;

    WebView mWebView;

    Noticias noticia;

    String link;

    AsyncTask<Object, Void, Noticias> taskDetalle = null;

    SharedPreferences preferencias = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticias_detalle);


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

        }

        //Status bar color init
        UtilidadesUI.initStatusBar(this);



        dialog = ProgressDialog.show(DetalleNoticiaActivity.this, "", getString(R.string.dialogo_espera), true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            noticia = (Noticias) extras.get("NOTICIA_SELECCIONADA");

            int posicion = extras.getInt("POSICION_LINK");


            // Fondo
            setupFondoAplicacion();

            /**
             * Sera llamado cuando la tarea de cargar las noticias
             */
            LoadDetalleNoticiaAsyncTaskResponder loadDetalleNoticiaAsyncTaskResponder = new LoadDetalleNoticiaAsyncTaskResponder() {
                public void detalleNoticiaLoaded(Noticias noticia) {

                    if (noticia != null) {

                        cargarDetalle(noticia);
                        dialog.dismiss();

                    } else {
                        // Error al recuperar datos

                        cargarDetalleError();

                        Toast.makeText(getApplicationContext(), getString(R.string.ko_noticia), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                }
            };


            if (posicion >= 0) {
                link = noticia.getLinks().get(posicion);


                // Control de disponibilidad de conexion
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {

                    String userAgentDefault = Utilidades.getAndroidUserAgent(this);

                    taskDetalle = new LoadDetalleNoticiaAsyncTask(loadDetalleNoticiaAsyncTaskResponder).execute(link, userAgentDefault, getApplicationContext());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

            } else {
                link = noticia.getLinks().get(0);

                cargarDetalle(noticia);
                dialog.dismiss();

            }

        }


        TextView zoomMas = findViewById(R.id.noticia_zoom_aumenta);

        assert zoomMas != null;
        zoomMas.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                if (mWebView != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                        if (mWebView.getSettings().getTextZoom() < 200) {

                            mWebView.getSettings().setTextZoom(mWebView.getSettings().getTextZoom() + 10);

                        }

                    } else {

                        if (mWebView.getSettings().getTextSize().equals(TextSize.SMALLEST)) {
                            mWebView.getSettings().setTextSize(TextSize.SMALLER);
                        } else if (mWebView.getSettings().getTextSize().equals(TextSize.SMALLER)) {
                            mWebView.getSettings().setTextSize(TextSize.NORMAL);
                        } else if (mWebView.getSettings().getTextSize().equals(TextSize.NORMAL)) {
                            mWebView.getSettings().setTextSize(TextSize.LARGER);
                        } else if (mWebView.getSettings().getTextSize().equals(TextSize.LARGER)) {
                            mWebView.getSettings().setTextSize(TextSize.LARGEST);
                        }


                    }


                }

            }

        });


        TextView zoomMenos = findViewById(R.id.noticia_zoom_reduc);

        assert zoomMenos != null;
        zoomMenos.setOnClickListener(new OnClickListener() {

            public void onClick(View view) {

                if (mWebView != null) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

                        Log.d("Detalle Noticias", "Zoom: " + mWebView.getSettings().getTextZoom());

                        if (mWebView.getSettings().getTextZoom() > 50) {

                            mWebView.getSettings().setTextZoom(mWebView.getSettings().getTextZoom() - 10);

                        }

                    } else {

                        if (mWebView.getSettings().getTextSize().equals(TextSize.LARGEST)) {
                            mWebView.getSettings().setTextSize(TextSize.LARGER);
                        } else if (mWebView.getSettings().getTextSize().equals(TextSize.LARGER)) {
                            mWebView.getSettings().setTextSize(TextSize.NORMAL);
                        } else if (mWebView.getSettings().getTextSize().equals(TextSize.NORMAL)) {
                            mWebView.getSettings().setTextSize(TextSize.SMALLER);
                        } else if (mWebView.getSettings().getTextSize().equals(TextSize.SMALLER)) {
                            mWebView.getSettings().setTextSize(TextSize.SMALLEST);
                        }

                    }


                }

            }

        });


    }

    @Override
    protected void onDestroy() {

        detenerTareas();

        super.onDestroy();
    }

    public void detenerTareas() {

        if (taskDetalle != null && taskDetalle.getStatus() == Status.RUNNING) {

            taskDetalle.cancel(true);

            Log.d("NOTICIAS", "Cancelada task detalle");

        }

    }

    /**
     * Cargar el detallet
     *
     * @param noticia
     */
    public void cargarDetalle(Noticias noticia) {

        TextView cabFecha = findViewById(R.id.cabeceraFecha);
        TextView cabTitulo = findViewById(R.id.cabeceraTitulo);
        TextView cabLinea = findViewById(R.id.cabeceraLinea);

        if (noticia.getFechaCabecera() != null && !noticia.getFechaCabecera().trim().equals("")) {
            assert cabFecha != null;
            cabFecha.setText(noticia.getFechaCabecera());
        } else {
            assert cabFecha != null;
            cabFecha.setText(getString(R.string.sin_fecha));
        }
        assert cabTitulo != null;
        cabTitulo.setText(noticia.getTituloCabecera());

        String noticiaLineas = "";

        if (noticia.getLineaCabecera().length() > 400) {
            noticiaLineas = noticia.getLineaCabecera().substring(0, 400) + "...";
        } else {
            noticiaLineas = noticia.getLineaCabecera();
        }

        assert cabLinea != null;
        cabLinea.setText(noticiaLineas);

        TextView accederNoticia = findViewById(R.id.accederNoticia);

        assert accederNoticia != null;
        accederNoticia.setLinksClickable(true);
        accederNoticia.setAutoLinkMask(Linkify.WEB_URLS);

        accederNoticia.setText(link);

        if (noticia.getContenidoHtml() != null) {
            mWebView = findViewById(R.id.webViewDetalle);

            //Color de fondo
            if(UtilidadesUI.isNightModeOn(this)){
                mWebView.setBackgroundColor(ContextCompat.getColor(this, R.color.webview_color));
            }

            assert mWebView != null;
            WebSettings settings = mWebView.getSettings();
            settings.setDefaultTextEncodingName("utf-8");

            //mWebView.loadData(noticia.getContenidoHtml(), "text/html", "utf-8");

            mWebView.loadDataWithBaseURL(null, noticia.getContenidoHtml(), "text/html", "utf-8", null);

        }

    }

    /**
     * Cargar informacion minima en caso de error
     */
    public void cargarDetalleError() {

        TextView cabFecha = findViewById(R.id.cabeceraFecha);
        TextView cabTitulo = findViewById(R.id.cabeceraTitulo);
        TextView cabLinea = findViewById(R.id.cabeceraLinea);

        assert cabFecha != null;
        cabFecha.setText(Utilidades.getFechaStringSinHora(noticia.getFecha()));
        assert cabTitulo != null;
        cabTitulo.setText(noticia.getNoticia());
        assert cabLinea != null;
        cabLinea.setText("");

        TextView accederNoticia = findViewById(R.id.accederNoticia);

        assert accederNoticia != null;
        accederNoticia.setLinksClickable(true);
        accederNoticia.setAutoLinkMask(Linkify.WEB_URLS);

        accederNoticia.setText(link);

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

    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = findViewById(R.id.main);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

    }

}
