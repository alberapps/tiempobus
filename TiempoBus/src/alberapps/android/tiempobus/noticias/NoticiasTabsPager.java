/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * based on code by The Android Open Source Project
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

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.List;

import alberapps.android.tiempobus.PreferencesFromXml;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.noticias.sliding.SlidingTabsBasicFragment;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.tasks.LoadAvisosTramAsyncTask;
import alberapps.android.tiempobus.tasks.LoadAvisosTramAsyncTask.LoadAvisosTramAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadNoticiasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadNoticiasAsyncTask.LoadNoticiasAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadNoticiasRssAsyncTask;
import alberapps.android.tiempobus.tasks.LoadNoticiasRssAsyncTask.LoadNoticiasRssAsyncTaskResponder;
import alberapps.android.tiempobus.tasks.LoadTwitterAsyncTask;
import alberapps.android.tiempobus.tasks.LoadTwitterAsyncTask.LoadTwitterAsyncTaskResponder;
import alberapps.android.tiempobus.util.Notificaciones;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.Noticias;
import alberapps.java.noticias.rss.NoticiaRss;
import alberapps.java.noticias.tw.ProcesarTwitter;
import alberapps.java.noticias.tw.TwResultado;
import alberapps.java.tam.BusLinea;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.tram.avisos.Aviso;
import alberapps.java.tram.avisos.AvisosTram;
import alberapps.java.tram.avisos.ProcesarAvisosTram;
import alberapps.java.util.Utilidades;

/**
 * Noticias con tabs
 */
public class NoticiasTabsPager extends AppCompatActivity {

    public ViewPager mViewPager;

    BusLinea linea = null;

    SharedPreferences preferencias = null;

    private ListView noticiasView;

    List<Noticias> noticiasRecuperadas;

    NoticiasAdapter noticiasAdapter;

    private ListView listTwWiew;

    private ListView noticiasRssView;

    List<TwResultado> avisosRecuperados;

    List<NoticiaRss> noticiasRss;

    TwAdapter twAdapter;

    TwAdapter twTramAdapter;

    NoticiasRssAdapter noticiasRssAdapter;

    private ProgressDialog dialog;

    MenuItem refresh = null;

    public boolean twSinResultados = false;

    AsyncTask<Object, Void, List<Noticias>> loadNoticiasTask = null;

    AsyncTask<Object, Void, List<TwResultado>> loadTwTask = null;

    AsyncTask<Object, Void, List<NoticiaRss>> loadNoticiasRssTask = null;

    public BusLinea getLinea() {
        return linea;
    }

    public void setLinea(BusLinea linea) {
        this.linea = linea;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);

        if (UtilidadesTRAM.ACTIVADO_TRAM) {
            setContentView(R.layout.noticias_contenedor);
        } else {
            setContentView(R.layout.noticias_contenedor_2);
        }


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setElevation(0);
        }


        if (!UtilidadesUI.pantallaTabletHorizontal(this)) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            transaction.replace(R.id.tabs_content_fragment, fragment);
            transaction.commit();


        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        finish();
        startActivity(getIntent());

    }

    @Override
    protected void onDestroy() {

        // Se cancelan las tareas en caso de volver sin terminar

        if (loadNoticiasTask != null && loadNoticiasTask.getStatus() == Status.RUNNING) {

            loadNoticiasTask.cancel(true);

            Log.d("noticias", "Cancelada task noticias");

        }

        if (loadTwTask != null && loadTwTask.getStatus() == Status.RUNNING) {

            loadTwTask.cancel(true);

            Log.d("noticias", "Cancelada task twitter");

        }

        if (loadNoticiasRssTask != null && loadNoticiasRssTask.getStatus() == Status.RUNNING) {

            loadNoticiasRssTask.cancel(true);

            Log.d("noticias", "Cancelada task rss");

        }

        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_noticias, menu);

        refresh = menu.findItem(R.id.menu_refresh);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.menu_refresh:

                recargarNoticias(false, false);

                break;

            case R.id.menu_preferencias:
                showPreferencias();
                break;

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Quitar notificacion
        // Get a reference to the notification manager
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(ns);
        mNotificationManager.cancel(Notificaciones.NOTIFICACION_NOTICIAS);
        mNotificationManager.cancel(Notificaciones.NOTIFICACION_NOTICIAS_TRAM);

        recargarNoticias(false, true);

    }

    /**
     * Lanza la subactivididad de preferencias
     */
    private void showPreferencias() {
        Intent i = new Intent(this, PreferencesFromXml.class);

        startActivity(i);

    }

    /**
     * Recarga de noticias
     */
    private void recargarNoticias(boolean bloqueo, boolean usarCache) {

        if (bloqueo) {
            dialog.show();

            dialog.setMessage(getString(R.string.carga_noticias_msg));

        } else {

            setRefreshActionItemState(true);
        }

        /**
         * Sera llamado cuando la tarea de cargar las noticias
         */
        LoadNoticiasAsyncTaskResponder loadNoticiasAsyncTaskResponder = new LoadNoticiasAsyncTaskResponder() {
            public void noticiasLoaded(List<Noticias> noticias) {

                if (noticias != null && !noticias.isEmpty()) {
                    noticiasRecuperadas = noticias;
                    cargarListado(noticias, true);

                } else {

                    noticiasRecuperadas = null;

                    Noticias noticia = new Noticias();
                    noticia.setErrorServicio(true);

                    noticiasRecuperadas = new ArrayList<>();
                    noticiasRecuperadas.add(noticia);

                    // Error al recuperar datos
                    //cargarListado(noticias, false);

                    cargarListado(noticiasRecuperadas, true);

                }

                // Quitar barra progreso inicial
                ProgressBar lpb = (ProgressBar) findViewById(R.id.progreso_noticias);
                if (lpb != null) {
                    lpb.clearAnimation();
                    lpb.setVisibility(View.INVISIBLE);
                }

                if (noticias == null || noticias.isEmpty()) {
                    TextView vacio = (TextView) findViewById(R.id.vacio_noticias);

                    if (noticiasView != null && vacio != null) {
                        noticiasView.setEmptyView(vacio);
                    }
                }

                // Inicia carga twitter
                recargarTw();

            }
        };

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            String userAgentDefault = Utilidades.getAndroidUserAgent(this);

            loadNoticiasTask = new LoadNoticiasAsyncTask(loadNoticiasAsyncTaskResponder).execute(usarCache, userAgentDefault);

        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();

            setRefreshActionItemState(false);

            if (dialog != null && dialog.isShowing()) {

                dialog.dismiss();

            }

        }

    }

    /**
     * Carga del listado
     *
     * @param noticiasList
     * @param ok
     */
    public void cargarListado(List<Noticias> noticiasList, boolean ok) {

        try {

            noticiasView = (ListView) findViewById(R.id.lista_noticias);

            if (noticiasView != null) {

                cargarHeaderNoticias();

                noticiasAdapter = new NoticiasAdapter(this, R.layout.noticias_item);

                if (ok) {

                    noticiasAdapter.addAll(noticiasList);

                    noticiasAdapter.notifyDataSetChanged();
                }

                noticiasView.setOnItemClickListener(noticiasClickedHandler);
                noticiasView.setAdapter(noticiasAdapter);
                View emptyView = findViewById(R.id.vacio_noticias);
                noticiasView.setEmptyView(emptyView);

            }

        } catch (Exception e) {

            // Para evitar fallos si se intenta volver antes de terminar

            e.printStackTrace();

        }

    }

    /**
     * Cargar cabecera listado
     */
    public void cargarHeaderNoticias() {

        if (noticiasView != null && noticiasView.getHeaderViewsCount() == 0) {

            LayoutInflater li2 = LayoutInflater.from(this);

            View vheader = li2.inflate(R.layout.noticias_header, null);

            TextView texto = (TextView) vheader.findViewById(R.id.txt_noticias_header);

            StringBuffer textoHeader = new StringBuffer();

            textoHeader.append(getString(R.string.aviso_noticias));
            textoHeader.append("\n");
            textoHeader.append(FragmentNoticias.noticiasURL);
            textoHeader.append("\n");
            textoHeader.append(getString(R.string.noticias_instrucciones));

            texto.setLinksClickable(true);
            texto.setAutoLinkMask(Linkify.WEB_URLS);

            texto.setText(textoHeader.toString());

            noticiasView = (ListView) findViewById(R.id.lista_noticias);

            noticiasView.addHeaderView(vheader);

        }

    }

    /**
     * Listener encargado de gestionar las pulsaciones sobre los items
     */
    private OnItemClickListener noticiasClickedHandler = new OnItemClickListener() {

        /**
         * @param l
         *            The ListView where the click happened
         * @param v
         *            The view that was clicked within the ListView
         * @param position_inicial
         *            The position of the view in the list
         * @param id
         *            The row id of the item that was clicked
         */
        public void onItemClick(AdapterView<?> l, View v, final int position_inicial, long id) {

            if (position_inicial == 0) {
                return;
            }

            // Para descartar la cabecera
            final int position = position_inicial - 1;

            if (noticiasRecuperadas.get(position).getLinks() != null && !noticiasRecuperadas.get(position).getLinks().isEmpty()) {

                if (noticiasRecuperadas.get(position).getLinks().size() > 1) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(NoticiasTabsPager.this);
                    builder.setTitle(R.string.noticias_links);

                    int size = noticiasRecuperadas.get(position).getDescLink().size();
                    CharSequence[] items = new CharSequence[size];

                    for (int i = 0; i < noticiasRecuperadas.get(position).getDescLink().size(); i++) {

                        items[i] = noticiasRecuperadas.get(position).getDescLink().get(i);

                    }

                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            Intent i = new Intent(NoticiasTabsPager.this, DetalleNoticiaActivity.class);
                            i.putExtra("NOTICIA_SELECCIONADA", noticiasRecuperadas.get(position));
                            i.putExtra("POSICION_LINK", item);
                            startActivity(i);

                        }
                    });

                    AlertDialog alert = builder.create();

                    alert.show();

                } else {

                    Intent i = new Intent(NoticiasTabsPager.this, DetalleNoticiaActivity.class);
                    i.putExtra("NOTICIA_SELECCIONADA", noticiasRecuperadas.get(position));
                    i.putExtra("POSICION_LINK", 0);
                    startActivity(i);

                }

            }
        }
    };


    /**
     * Cotrol de errores de twitter
     *
     * @param context
     * @param mensajes
     * @return
     */
    public static boolean errorTwitter(Context context, List<TwResultado> mensajes) {

        if (mensajes != null && !mensajes.isEmpty() && mensajes.size() == 1 && mensajes.get(0).getError() != null && !mensajes.get(0).getError().equals("100")) {

            Toast.makeText(context, context.getString(R.string.error_twitter) + ": " + mensajes.get(0).getMensajeError(), Toast.LENGTH_SHORT).show();

            return true;

        } else if (mensajes != null && !mensajes.isEmpty() && mensajes.size() == 1 && mensajes.get(0).getError() != null && mensajes.get(0).getError().equals("100")) {

            Toast.makeText(context, context.getString(R.string.error_twitter), Toast.LENGTH_SHORT).show();

            return true;

        }


        return false;

    }


    /**
     * Recarga de datos twitter
     */
    private void recargarTw() {

        if (dialog != null && dialog.isShowing()) {
            dialog.setMessage(getString(R.string.carga_tw_msg));
        }

        /**
         * Sera llamado cuando la tarea de cargar las noticias
         */
        LoadTwitterAsyncTaskResponder loadTwitterAsyncTaskResponder = new LoadTwitterAsyncTaskResponder() {
            public void TwitterLoaded(List<TwResultado> mensajes) {

                if (errorTwitter(getApplicationContext(), mensajes)) {

                    mensajes = null;

                    twSinResultados = true;

                }

                if (mensajes != null && !mensajes.isEmpty()) {

                    twSinResultados = false;

                    avisosRecuperados = mensajes;

                    cargarListadoTw();

                } else {

                    avisosRecuperados = null;
                    // Error al recuperar datos

                    cargarListadoTw();

                }

                if (UtilidadesTRAM.ACTIVADO_TRAM) {

                    recargarRss();

                } else {

                    setRefreshActionItemState(false);

                    if (dialog != null && dialog.isShowing()) {

                        dialog.dismiss();

                    }

                }

                if (listTwWiew != null) {
                    // Quitar barra progreso inicial
                    ProgressBar lpb = (ProgressBar) findViewById(R.id.tiempos_progreso_tw);
                    lpb.clearAnimation();
                    lpb.setVisibility(View.INVISIBLE);

                    if (mensajes == null || mensajes.isEmpty()) {
                        TextView vacio = (TextView) findViewById(R.id.vacio_tw);
                        listTwWiew.setEmptyView(vacio);
                    }


                }

            }
        };

        // Opcion de desactivar twitter
        if (!preferencias.getBoolean("tw_activar", true)) {

            setRefreshActionItemState(false);

            if (dialog != null && dialog.isShowing()) {

                dialog.dismiss();

            }

            listTwWiew = (ListView) findViewById(R.id.listatw);

            if (listTwWiew != null) {
                // Quitar barra progreso inicial
                ProgressBar lpb = (ProgressBar) findViewById(R.id.tiempos_progreso_tw);
                lpb.clearAnimation();
                lpb.setVisibility(View.INVISIBLE);

                TextView vacio = (TextView) findViewById(R.id.vacio_tw);
                listTwWiew.setEmptyView(vacio);

            }
            avisosRecuperados = null;

            cargarListadoTw();

            //Lanzar directamente TRAM

            if (UtilidadesTRAM.ACTIVADO_TRAM) {

                recargarRss();

            } else {

                setRefreshActionItemState(false);

                if (dialog != null && dialog.isShowing()) {

                    dialog.dismiss();

                }

            }


            return;
        }

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // Cargar lista de elementos a consultar
            List<Boolean> listaTW = new ArrayList<>();

            listaTW.add(preferencias.getBoolean("tw_2", true));
            listaTW.add(preferencias.getBoolean("tw_3", true));
            listaTW.add(preferencias.getBoolean("tw_4", true));
            listaTW.add(preferencias.getBoolean("tw_5", true));
            listaTW.add(preferencias.getBoolean("tw_6", true));

            String cantidad = preferencias.getString("tweets_maximos_v11", "3");

            loadTwTask = new LoadTwitterAsyncTask(loadTwitterAsyncTaskResponder).execute(listaTW, cantidad);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();

            setRefreshActionItemState(false);

            if (dialog != null && dialog.isShowing()) {

                dialog.dismiss();

            }
        }

    }

    /**
     * Carga el listado
     */
    public void cargarListadoTw() {

        try {

            listTwWiew = (ListView) findViewById(R.id.listatw);

            if (listTwWiew != null) {

                cargarHeaderTwitter();

                twAdapter = new TwAdapter(this, R.layout.avisostw_item);
                twAdapter.addAll(avisosRecuperados);
                listTwWiew.setAdapter(twAdapter);
                twAdapter.notifyDataSetChanged();

            }

        } catch (Exception e) {

            // Para evitar fallos en caso de volver antes de terminar

            e.printStackTrace();

        }

    }

    /**
     * Cargar cabecera listado
     */
    public void cargarHeaderTwitter() {

        if (listTwWiew != null && listTwWiew.getHeaderViewsCount() == 0) {

            LayoutInflater li2 = LayoutInflater.from(this);

            View vheader = li2.inflate(R.layout.noticias_header, null);

            TextView texto = (TextView) vheader.findViewById(R.id.txt_noticias_header);

            StringBuffer textoHeader = new StringBuffer();

            textoHeader.append(getString(R.string.dato_tw));
            textoHeader.append("\n");
            textoHeader.append(getString(R.string.tw1));
            textoHeader.append("\n");
            textoHeader.append(getString(R.string.twitter4j));

            texto.setText(textoHeader.toString());

            listTwWiew = (ListView) findViewById(R.id.listatw);

            listTwWiew.addHeaderView(vheader);

        }

    }

    /**
     * Listener encargado de gestionar las pulsaciones sobre los items
     */
    private OnItemClickListener twClickedHandler = new OnItemClickListener() {

        /**
         * @param l
         *            The ListView where the click happened
         * @param v
         *            The view that was clicked within the ListView
         * @param position
         *            The position of the view in the list
         * @param id
         *            The row id of the item that was clicked
         */
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {

            Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();

            String url = avisosRecuperados.get(position).getUrl();

            Intent i = new Intent(Intent.ACTION_VIEW);

            i.setData(Uri.parse(url));
            startActivity(i);

        }
    };

    // ///////RSS

    /**
     * Recarga de datos twitter
     */
    private void recargarRss() {

        if (dialog != null && dialog.isShowing()) {
            dialog.setMessage(getString(R.string.carga_rss_tram_msg));
        }

        /**
         * Sera llamado cuando la tarea de cargar las noticias
         */
        LoadNoticiasRssAsyncTaskResponder loadNoticiasRssAsyncTaskResponder = new LoadNoticiasRssAsyncTaskResponder() {
            public void noticiasRssLoaded(List<NoticiaRss> noticias) {

                if (noticias != null && !noticias.isEmpty()) {
                    noticiasRss = noticias;
                    cargarListadoRss();

                } else {

                    noticiasRss = null;
                    // Error al recuperar datos
                    cargarListadoRss();

                }

                setRefreshActionItemState(false);

                if (dialog != null && dialog.isShowing()) {

                    dialog.dismiss();

                }

                if (noticiasRssView != null) {
                    // Quitar barra progreso inicial
                    ProgressBar lpb = (ProgressBar) findViewById(R.id.progreso_rss);
                    lpb.clearAnimation();
                    lpb.setVisibility(View.INVISIBLE);

                    if (noticias == null || noticias.isEmpty()) {
                        TextView vacio = (TextView) findViewById(R.id.vacio_noticias_rss);
                        noticiasRssView.setEmptyView(vacio);
                    }
                }

            }
        };

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            loadNoticiasRssTask = new LoadNoticiasRssAsyncTask(loadNoticiasRssAsyncTaskResponder).execute();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();

            setRefreshActionItemState(false);

            if (dialog != null && dialog.isShowing()) {

                dialog.dismiss();

            }
        }

    }

    /**
     * Carga el listado
     */
    public void cargarListadoRss() {

        try {

            noticiasRssView = (ListView) findViewById(R.id.noticias_rss);

            noticiasRssAdapter = new NoticiasRssAdapter(this, R.layout.noticias_rss_item);

            if (noticiasRss != null) {

                cargarHeaderNoticiasRss();

                noticiasRssAdapter.addAll(noticiasRss);
                noticiasRssAdapter.notifyDataSetChanged();

            }

            noticiasRssView = (ListView) findViewById(R.id.noticias_rss);

            TextView vacio = (TextView) findViewById(R.id.vacio_noticias_rss);
            noticiasRssView.setEmptyView(vacio);

            // lineasView.setOnItemClickListener(twClickedHandler);

            noticiasRssView.setAdapter(noticiasRssAdapter);

        } catch (Exception e) {

            // Para evitar fallos en caso de volver antes de terminar
            e.printStackTrace();

        }

    }

    /**
     * Cargar cabecera listado
     */
    public void cargarHeaderNoticiasRss() {

        if (noticiasRssView != null && noticiasRssView.getHeaderViewsCount() == 0) {

            LayoutInflater li2 = LayoutInflater.from(this);

            View vheader = li2.inflate(R.layout.noticias_tram_header, null);

            TextView texto = (TextView) vheader.findViewById(R.id.txt_noticias_header);

            StringBuffer textoHeader = new StringBuffer();

            textoHeader.append(getString(R.string.aviso_noticias));
            textoHeader.append("\n");
            textoHeader.append(FragmentNoticiasRss.noticiasURL);
            textoHeader.append("\n");
            textoHeader.append(ProcesarTwitter.tw_tram_ruta);

            texto.setLinksClickable(true);
            texto.setAutoLinkMask(Linkify.WEB_URLS);

            texto.setText(textoHeader.toString());

            noticiasRssView = (ListView) findViewById(R.id.noticias_rss);

            noticiasRssView.addHeaderView(vheader);

            verificarNuevasNoticiasTram();

        }

    }

    /**
     * Ultimas noticias tram
     */
    public void cargarHeaderUltimasNoticiasTram(final List<TwResultado> noticias) {

        if (noticiasRssView != null && noticiasRssView.getHeaderViewsCount() == 1 && noticias == null) {

            // Cargar layout para noticias tram tw

            LayoutInflater li2 = LayoutInflater.from(this);
            View vheader = li2.inflate(R.layout.noticias_tram_ultimas_item, null);
            TextView titulo = (TextView) vheader.findViewById(R.id.titulo_ultima_tram);
            titulo.setText(getString(R.string.tab_tw) + ": @TramdeAlicante");

            // Link de acceso a twitter
            titulo.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    String url = ProcesarTwitter.tw_tram_ruta;

                    Intent i = new Intent(Intent.ACTION_VIEW);

                    i.setData(Uri.parse(url));
                    startActivity(i);

                }

            });


            TextView descripcion = (TextView) vheader.findViewById(R.id.descripcion_ultima_tram_1);
            StringBuffer textoHeader = new StringBuffer();
            textoHeader.append(getString(R.string.aviso_recarga));
            descripcion.setLinksClickable(true);
            descripcion.setAutoLinkMask(Linkify.WEB_URLS);

            descripcion.setText(textoHeader.toString());

            noticiasRssView = (ListView) findViewById(R.id.noticias_rss);

            noticiasRssView.addHeaderView(vheader);

        } else if (noticias != null && !noticias.isEmpty()) {

            // Carga el contenido de la noticia de tram tw

            View vheader = noticiasRssView.findViewById(R.id.layout_noticias_tram_tw);

            TextView fecha_1 = (TextView) vheader.findViewById(R.id.fecha_ultima_tram_1);
            TextView fecha_2 = (TextView) vheader.findViewById(R.id.fecha_ultima_tram_2);
            TextView descripcion_1 = (TextView) vheader.findViewById(R.id.descripcion_ultima_tram_1);
            TextView descripcion_2 = (TextView) vheader.findViewById(R.id.descripcion_ultima_tram_2);

            fecha_1.setText(noticias.get(0).getFecha());
            descripcion_1.setText(noticias.get(0).getMensaje());

            if (noticias.size() > 1) {
                fecha_2.setText(noticias.get(1).getFecha());
                descripcion_2.setText(noticias.get(1).getMensaje());
            }


            //Boton cargar todas las noticias
            Button BotonTodas = (Button) vheader.findViewById(R.id.boton_ver_todas);
            BotonTodas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cargarListadoTodasTram(noticias);

                }
            });

        }

    }


    /**
     * Cargar el listado de noticias tw del tram
     *
     * @param noticias
     */
    public void cargarListadoTodasTram(final List<TwResultado> noticias) {

        try {

            twTramAdapter = new TwAdapter(this, R.layout.avisostw_item);

            noticiasRssView = (ListView) findViewById(R.id.noticias_rss);

            TextView vacio = (TextView) findViewById(R.id.vacio_noticias_rss);
            noticiasRssView.setEmptyView(vacio);

            noticiasRssView.setAdapter(twTramAdapter);

            if (noticias != null) {

                twTramAdapter.addAll(noticias);
                twTramAdapter.quitarIniciales();
                twTramAdapter.notifyDataSetChanged();

            }


            View vheader = noticiasRssView.findViewById(R.id.layout_noticias_tram_tw);

            //Boton cargar todas las noticias
            final Button botonTodas = (Button) vheader.findViewById(R.id.boton_ver_todas);

            botonTodas.setText(getString(R.string.noticias_no_todas));

            botonTodas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cargarListadoRss();

                    botonTodas.setText(getString(R.string.noticias_todas));

                    //Boton cargar todas las noticias
                    botonTodas.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            cargarListadoTodasTram(noticias);

                        }
                    });

                }
            });

        } catch (Exception e) {

            // Para evitar fallos en caso de volver antes de terminar
            e.printStackTrace();

        }

    }

    /**
     * Verifica si hay nuevas noticias y muestra un aviso
     */
    public void verificarNuevasNoticiasTram() {

        // Cargar noticias tw
        cargarHeaderUltimasNoticiasTram(null);

        /**
         * Sera llamado cuando la tarea de cargar las noticias
         */
        LoadAvisosTramAsyncTaskResponder loadAvisosTramAsyncTaskResponder = new LoadAvisosTramAsyncTaskResponder() {
            public void AvisosTramLoaded(AvisosTram avisosTram) {

                List<TwResultado> noticias = avisosTram.getAvisosTw();

                if (errorTwitter(getApplicationContext(), noticias)) {

                    noticias = null;

                }


                if (noticias != null && !noticias.isEmpty()) {

                    // Cargar noticias tw
                    cargarHeaderUltimasNoticiasTram(noticias);

                    int nuevas = 0;

                    String fecha_ultima = "";
                    boolean lanzarAviso = false;

                    // Ver si se guardo la fecha de la ultima noticia
                    if (preferencias.contains("ultima_noticia_tram")) {
                        fecha_ultima = preferencias.getString("ultima_noticia_tram", "");

                        if (!fecha_ultima.equals(noticias.get(0).getFechaDate().toString())) {

                            lanzarAviso = true;

                            SharedPreferences.Editor editor = preferencias.edit();
                            editor.putString("ultima_noticia_tram", noticias.get(0).getFechaDate().toString());
                            editor.apply();

                        }

                    } else {

                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putString("ultima_noticia_tram", noticias.get(0).getFechaDate().toString());
                        editor.apply();

                    }

                    // Si se guardo la fecha y no coincide con la ultima, lanzar
                    // aviso
                    if (lanzarAviso) {

                        // Extendido

                        String[] extendido = new String[2];

                        extendido[0] = noticias.get(0).getFecha() + ": " + noticias.get(0).getMensaje();

                        if (noticias.size() > 1) {
                            extendido[1] = noticias.get(1).getFecha() + ": " + noticias.get(1).getMensaje();
                        } else {
                            extendido[1] = "";
                        }

                        Notificaciones.notificacionAvisosTram(getApplicationContext(), extendido);

                    }
                } else {


                    View vheader = noticiasRssView.findViewById(R.id.layout_noticias_tram_tw);

                    TextView descripcion = (TextView) vheader.findViewById(R.id.descripcion_ultima_tram_1);
                    TextView descripcion2 = (TextView) vheader.findViewById(R.id.descripcion_ultima_tram_2);
                    TextView fecha1 = (TextView) vheader.findViewById(R.id.fecha_ultima_tram_1);
                    TextView fecha2 = (TextView) vheader.findViewById(R.id.fecha_ultima_tram_2);

                    descripcion.setText(getString(R.string.main_no_items));
                    descripcion2.setText("");
                    fecha1.setText("");
                    fecha2.setText("");

                }


                cargarAvisosWebTram(avisosTram.getAvisosWeb());


            }
        };

        // Control de disponibilidad de conexion
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new LoadAvisosTramAsyncTask(loadAvisosTramAsyncTaskResponder).execute("TRAM_WEB");
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Cargar los avisos recuperados de la web del tram
     *
     * @param avisosWeb
     */
    private void cargarAvisosWebTram(List<Aviso> avisosWeb) {

        View vheader = noticiasRssView.findViewById(R.id.layout_avisos_tram);

        if (avisosWeb != null && !avisosWeb.isEmpty()) {

            String lineasAvisos = "";
            for (int i = 0; i < avisosWeb.size(); i++) {
                lineasAvisos = lineasAvisos + avisosWeb.get(i).getTitulo() + ",";
            }

            //String lineas = "L1,L2,L3,L4,L9";

            mostrarLineasAlerta(this, vheader, lineasAvisos, avisosWeb);


        } else {

            TextView descripcion = (TextView) vheader.findViewById(R.id.descripcion_aviso_tram);
            descripcion.setText(getString(R.string.sin_novedades));

        }


    }

    /**
     * Mostrar las lineas con avisos
     *
     * @param contexto
     * @param v
     * @param conexiones
     * @param avisos
     */
    private void mostrarLineasAlerta(Context contexto, View v, String conexiones, List<Aviso> avisos) {

        //Lineas con parada
        LinearLayout lineasParada = (LinearLayout) v.findViewById(R.id.lineas_parada);

        lineasParada.removeAllViews();


        String[] conexionesList = conexiones.split(",");

        int posicionMax = conexionesList.length;


        for (int i = 0; i < posicionMax; i++) {
            lineasParada.addView(incluirTexto(contexto, conexionesList[i], avisos.get(i)));
        }


    }


    /**
     * Visualizacion con estilos de las lineas con avisos
     *
     * @param contexto
     * @param conexion
     * @param aviso
     * @return
     */
    private FrameLayout incluirTexto(Context contexto, String conexion, final Aviso aviso) {

        FrameLayout fl = new FrameLayout(contexto);
        fl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        fl.setPadding(2, 5, 5, 2);

        AppCompatTextView texto = new AppCompatTextView(contexto);
        texto.setText(conexion.trim());
        texto.setTextAppearance(contexto, R.style.TextAppearance_AppCompat_Small);
        texto.setTextColor(contexto.getResources().getColor(R.color.abc_primary_text_disable_only_material_dark));

        int size50 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, contexto.getResources().getDisplayMetrics());

        texto.setLayoutParams(new ViewGroup.LayoutParams(size50, size50));
        texto.setGravity(Gravity.CENTER);
        texto.setTypeface(Typeface.DEFAULT_BOLD);

        DatosPantallaPrincipal.formatoLinea(contexto, texto, conexion, false);

        //Size
        if (conexion.trim().length() > 2) {
            texto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        } else {
            texto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }

        texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Noticias noticia = new Noticias();
                noticia.setTituloCabecera(getString(R.string.rss_tram));
                noticia.setContenidoHtml(aviso.getDescripcion());
                noticia.setLineaCabecera(aviso.getTitulo());

                List<String> link = new ArrayList<String>();
                link.add(ProcesarAvisosTram.URL_TRAM_AVISOS);
                noticia.setLinks(link);

                Intent i = new Intent(NoticiasTabsPager.this, DetalleNoticiaActivity.class);
                i.putExtra("NOTICIA_SELECCIONADA", noticia);
                i.putExtra("POSICION_LINK", -1);
                startActivity(i);

            }
        });


        fl.addView(texto);

        return fl;


    }

    /**
     * Listener encargado de gestionar las pulsaciones sobre los items
     */
    private OnItemClickListener noticiaRssClickedHandler = new OnItemClickListener() {

        /**
         * @param l
         *            The ListView where the click happened
         * @param v
         *            The view that was clicked within the ListView
         * @param position
         *            The position of the view in the list
         * @param id
         *            The row id of the item that was clicked
         */
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        }
    };

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

    /**
     * Progreso en barra superior
     *
     * @param show
     */
    public void setRefreshActionItemState(boolean show) {

        if (show && refresh != null) {
            MenuItemCompat.setActionView(refresh, R.layout.actionbar_indeterminate_progress);
        } else if (refresh != null) {
            MenuItemCompat.setActionView(refresh, null);
        }

    }

}
