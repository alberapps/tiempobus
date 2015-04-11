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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

import com.google.android.gms.analytics.GoogleAnalytics;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.tasks.LoadDetalleNoticiaAsyncTask;
import alberapps.android.tiempobus.tasks.LoadDetalleNoticiaAsyncTask.LoadDetalleNoticiaAsyncTaskResponder;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.Noticias;
import alberapps.java.util.Utilidades;

/**
 * Detalle de la noticia
 */
public class DetalleNoticiaActivity extends ActionBarActivity {

    private ProgressDialog dialog;

    WebView mWebView;

    Noticias noticia;

    String link;

    AsyncTask<Object, Void, Noticias> taskDetalle = null;

    SharedPreferences preferencias = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_noticia);


        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);

        }


        dialog = ProgressDialog.show(DetalleNoticiaActivity.this, "", getString(R.string.dialogo_espera), true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            noticia = (Noticias) extras.get("NOTICIA_SELECCIONADA");

            int posicion = extras.getInt("POSICION_LINK");

            link = noticia.getLinks().get(posicion);

        }

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

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            String userAgentDefault = Utilidades.getAndroidUserAgent(this);

            taskDetalle = new LoadDetalleNoticiaAsyncTask(loadDetalleNoticiaAsyncTaskResponder).execute(link, userAgentDefault);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }


        TextView zoomMas = (TextView) findViewById(R.id.noticia_zoom_aumenta);

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


        TextView zoomMenos = (TextView) findViewById(R.id.noticia_zoom_reduc);

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

        TextView cabFecha = (TextView) findViewById(R.id.cabeceraFecha);
        TextView cabTitulo = (TextView) findViewById(R.id.cabeceraTitulo);
        TextView cabLinea = (TextView) findViewById(R.id.cabeceraLinea);

        cabFecha.setText(noticia.getFechaCabecera());
        cabTitulo.setText(noticia.getTituloCabecera());

        String noticiaLineas = "";

        if (noticia.getLineaCabecera().length() > 400) {
            noticiaLineas = noticia.getLineaCabecera().substring(0, 400) + "...";
        } else {
            noticiaLineas = noticia.getLineaCabecera();
        }

        cabLinea.setText(noticiaLineas);

        TextView accederNoticia = (TextView) findViewById(R.id.accederNoticia);

        accederNoticia.setLinksClickable(true);
        accederNoticia.setAutoLinkMask(Linkify.WEB_URLS);

        accederNoticia.setText(link);

        if (noticia.getContenidoHtml() != null) {
            mWebView = (WebView) findViewById(R.id.webViewDetalle);

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

        TextView cabFecha = (TextView) findViewById(R.id.cabeceraFecha);
        TextView cabTitulo = (TextView) findViewById(R.id.cabeceraTitulo);
        TextView cabLinea = (TextView) findViewById(R.id.cabeceraLinea);

        cabFecha.setText(Utilidades.getFechaStringSinHora(noticia.getFecha()));
        cabTitulo.setText(noticia.getNoticia());
        cabLinea.setText("");

        TextView accederNoticia = (TextView) findViewById(R.id.accederNoticia);

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

    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = findViewById(R.id.main);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

    }

}
