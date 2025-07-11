/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2014 Alberto Montiel
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
package alberapps.java.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import alberapps.android.tiempobus.util.Comunes;

/**
 * Acceso a la red. Conexiones para distintas versiones de Android
 */
public class Conectividad {

    public static final String USER_AGENT = "TiempoBus/4.0 (http://alberapps.blogspot.com; alberapps@gmail.com)";

    /**
     * Conexion con post y codificacion UTF-8
     * <p/>
     * Sin cache
     *
     * @param urlPost
     * @param post
     * @return
     */
    public static String conexionPostUtf8(String urlPost, String post, Boolean cacheTiempos) throws Exception {


        // Abrir Conexion
        HttpURLConnection urlConnection = null;

        String datos = null;

        InputStream in = null;

        try {


            // Crear url
            URL url = new URL(urlPost);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            // urlConnection.setChunkedStreamingMode(0);
            urlConnection.setFixedLengthStreamingMode(post.length());

            urlConnection.setReadTimeout(Comunes.READ_TIMEOUT);
            urlConnection.setConnectTimeout(Comunes.CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);

            urlConnection.setRequestProperty("User-Agent", USER_AGENT);

            urlConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");


            urlConnection.addRequestProperty("Cache-Control", "no-cache");

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            Utilidades.writeIt(out, post);

            in = new BufferedInputStream(urlConnection.getInputStream());
            datos = Utilidades.obtenerStringDeStreamUTF8(in);
        } catch (IOException e) {

            e.printStackTrace();

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }

            throw new Exception("Error al acceder al servicio");

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }
        }


        return datos;

    }

    /**
     * Conexion con get y codificacion UTF-8
     * <p/>
     * Sin cache
     *
     * @param url
     * @return
     */
    public static String conexionGetUtf8(String url) throws Exception {

        return conexionGetIso(url, false, null, true);

    }

    /**
     * Conexion desactivando el keepAlive
     * Prueba para evitar los problemas del servidor
     *
     * @param urlPost
     * @param post
     * @param cacheTiempos
     * @return
     * @throws Exception
     */
    public static String conexionPostUtf8NoKeepAlive(String urlPost, String post, Boolean cacheTiempos) throws Exception {


        // Abrir Conexion
        HttpURLConnection urlConnection = null;

        String datos = null;

        InputStream in = null;

        try {


            // Crear url
            URL url = new URL(urlPost);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);

            urlConnection.setFixedLengthStreamingMode(post.length());

            urlConnection.setReadTimeout(Comunes.READ_TIMEOUT);
            urlConnection.setConnectTimeout(Comunes.CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);

            urlConnection.setRequestProperty("User-Agent", USER_AGENT);

            urlConnection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");


            urlConnection.addRequestProperty("Cache-Control", "no-cache");


            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            Utilidades.writeIt(out, post);

            in = new BufferedInputStream(urlConnection.getInputStream());
            datos = Utilidades.obtenerStringDeStreamUTF8(in);
        } catch (IOException e) {

            e.printStackTrace();

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }

            throw new Exception("Error al acceder al servicio");

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }

        }


        return datos;

    }

    /**
     * Conexion con get y codificacion ISO
     *
     * @param urlGet
     * @param usarCache
     * @return string
     */
    public static String conexionGetIso(String urlGet, boolean usarCache, String userAgent, boolean utf8) throws Exception {

        // Abrir Conexion
        HttpURLConnection urlConnection = null;

        String datos = null;

        InputStream in = null;

        try {

            // Crear url
            URL url = new URL(urlGet);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setReadTimeout(Comunes.READ_TIMEOUT);
            urlConnection.setConnectTimeout(Comunes.CONNECT_TIMEOUT);

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            if (userAgent == null) {
                urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            } else {
                urlConnection.setRequestProperty("User-Agent", userAgent);
            }

            if (!usarCache) {
                urlConnection.addRequestProperty("Cache-Control", "no-cache");
                Log.d("CONEXION", "Sin cache");
            } else {
                Log.d("CONEXION", "Con cache");
            }

            int res = urlConnection.getResponseCode();

            in = new BufferedInputStream(urlConnection.getInputStream());



            if (utf8) {
                datos = Utilidades.obtenerStringDeStreamUTF8(in);
            } else {
                datos = Utilidades.obtenerStringDeStream(in);
            }

        }
        catch (SSLHandshakeException ex) {
            ex.printStackTrace();

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex2) {

            }

            throw ex;
        }

        catch (IOException e) {

            e.printStackTrace();

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }

            throw new Exception("Error al acceder al servicio");


        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }

        }

        //Log.d("CONEXION", datos);

        return datos;

    }


    /**
     * Establecer conexion con certificado
     *
     * @param urlGet
     * @param usarCache
     * @param userAgent
     * @param utf8
     * @param contextParam
     * @return
     * @throws Exception
     */
    public static String conexionGetWebBusCert(String urlGet, boolean usarCache, String userAgent, boolean utf8, Context contextParam) throws Exception {

        HttpsURLConnection urlConnection = null;

        String datos = null;

        InputStream in = null;

        try {

            // Crear url
            urlGet = urlGet + "/";
            URL url = new URL(urlGet);

            urlConnection =
                    (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(getSSlSocketFactory(urlGet, contextParam));

            urlConnection.setReadTimeout(Comunes.READ_TIMEOUT);
            urlConnection.setConnectTimeout(Comunes.CONNECT_TIMEOUT);

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            if (userAgent == null) {
                urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            } else {
                urlConnection.setRequestProperty("User-Agent", userAgent);
            }

            if (!usarCache) {
                urlConnection.addRequestProperty("Cache-Control", "no-cache");
                Log.d("CONEXION", "Sin cache");
            } else {
                Log.d("CONEXION", "Con cache");
            }

            in = new BufferedInputStream(urlConnection.getInputStream());

            if (utf8) {
                datos = Utilidades.obtenerStringDeStreamUTF8(in);
            } else {
                datos = Utilidades.obtenerStringDeStream(in);
            }

        } catch (IOException e) {

            e.printStackTrace();

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }

            throw new Exception("Error al acceder al servicio");

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }

        }

        //Log.d("CONEXION", datos);

        return datos;

    }


    public static SSLSocketFactory getSSlSocketFactory(String urlGet, Context contextParam) {

        try {
            // Load CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            InputStream caInput = null;

            if (urlGet.contains("tramalacant.es")) {
                caInput = new BufferedInputStream(contextParam.getAssets().open("tramalacant.pem"));
            } else if (urlGet.contains("vectalia.es")){
                caInput = new BufferedInputStream(contextParam.getAssets().open("vectalia.pem"));
            }


            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } finally {
                caInput.close();
            }


            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);


            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            return context.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Devuelve inputstream
     *
     * @param urlGet
     * @return stream
     */
    public static InputStream conexionGetIsoStream(String urlGet) throws Exception {

        return Utilidades.stringToStreamIso(conexionGetIso(urlGet, true, null, false));

    }

    /**
     * @param urlGet
     * @return
     */
    public static String conexionGetIsoString(String urlGet) throws Exception {

        return conexionGetIso(urlGet, true, null, false);

    }


    public static InputStream conexionGetUtf8Stream(String urlGet) throws Exception {

        return Utilidades.stringToStreamIso(conexionGetIso(urlGet, true, null, true));

    }

    /**
     * @param urlGet
     * @return
     */
    public static String conexionGetUtf8String(String urlGet, Boolean usarCache) throws Exception {

        return conexionGetIso(urlGet, usarCache, null, true);

    }

    public static String conexionGetUtf8String(String urlGet, Boolean usarCache, Context context) throws Exception {

        return conexionGetWebBusCert(urlGet, usarCache, null, true, context);

    }


    public static String conexionGetUtf8StringUserAgent(String urlGet, Boolean usarCache, String userAgent) throws Exception {

        return conexionGetIso(urlGet, usarCache, userAgent, true);

    }

    public static String conexionGetUtf8StringUserAgent(String urlGet, Boolean usarCache, String userAgent, Context context) throws Exception {

        return conexionGetWebBusCert(urlGet, usarCache, userAgent, true, context);


    }


    /**
     * Conexion indicando si hay que usar cache
     *
     * @param urlGet
     * @param usarCache
     * @return stream
     */
    public static InputStream conexionGetIsoStream(String urlGet, Boolean usarCache, String userAgent) throws Exception {

        return Utilidades.stringToStreamIso(conexionGetIso(urlGet, usarCache, userAgent, false));

    }


    /**
     * Devuelve inputstream sin usar cache en conexion
     *
     * @param urlGet
     * @return stream
     */
    public static InputStream conexionGetIsoStreamNoCache(String urlGet) throws Exception {

        return Utilidades.stringToStreamIso(conexionGetIso(urlGet, false, null, false));

    }

    /**
     * Conexion sencilla con urlconnection
     *
     * @param urlEntrada
     * @return stream
     */
    public static InputStream recuperarStreamConexionSimple(String urlEntrada) throws Exception {

        InputStream is = null;

        try {

            URL url = new URL(urlEntrada);

            URLConnection con = url.openConnection();

            // timeout
            con.setReadTimeout(Comunes.CONNECT_TIMEOUT);
            con.setConnectTimeout(Comunes.READ_TIMEOUT);

            is = con.getInputStream();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

            throw new Exception("Error al acceder al servicio");

        }

        return is;

    }


    /**
     * Activar el uso de cache si la plataforma lo permite
     *
     * @param context
     */
    @SuppressLint("NewApi")
    public static void activarCache(Context context, SharedPreferences preferencias) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            boolean cacheActiva = preferencias.getBoolean("conectividad_cache", true);

            boolean cacheEliminada = preferencias.getBoolean("conectividad_cache_eliminada", false);

            if (cacheActiva) {

                // Activar la cache

                try {
                    File httpCacheDir = new File(context.getCacheDir(), "http");
                    long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
                    HttpResponseCache.install(httpCacheDir, httpCacheSize);
                    Log.i("Conectividad", "Cache activa");

                    Log.d("Conectividad", "Request count: " + HttpResponseCache.getInstalled().getRequestCount());
                    Log.d("Conectividad", "Network count: " + HttpResponseCache.getInstalled().getNetworkCount());
                    Log.d("Conectividad", "Hit count: " + HttpResponseCache.getInstalled().getHitCount());

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("cache_eliminada", false);
                    editor.apply();

                } catch (IOException e) {
                    Log.i("Conectividad", "HTTP response cache installation failed:" + e);
                }

            } else if (!cacheEliminada) {

                // Si se ha decidido eliminar la cache

                HttpResponseCache cache = HttpResponseCache.getInstalled();

                if (cache != null) {

                    try {
                        cache.delete();

                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.putBoolean("cache_eliminada", true);
                        editor.apply();

                        Log.i("Conectividad", "Cache eliminada");

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }

        }

    }

    /**
     * Asegurar guardado de la cache al salir
     */
    @SuppressLint("NewApi")
    public static void flushCache() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            HttpResponseCache cache = HttpResponseCache.getInstalled();
            if (cache != null) {
                cache.flush();

                Log.i("Conectividad", "flush de cache");

            }
        }

    }


    public static InputStream conexionGetStream(String urlGet, String userAgent) throws Exception {


        // Abrir Conexion
        HttpURLConnection urlConnection = null;

        BufferedInputStream in = null;

        InputStream in2 = null;

        try {

            // Crear url
            URL url = new URL(urlGet);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setReadTimeout(Comunes.READ_TIMEOUT + 20000);
            urlConnection.setConnectTimeout(Comunes.CONNECT_TIMEOUT);

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            if (userAgent == null) {
                urlConnection.setRequestProperty("User-Agent", USER_AGENT);
            } else {
                urlConnection.setRequestProperty("User-Agent", userAgent);
            }

            /*if (!usarCache) {
                urlConnection.addRequestProperty("Cache-Control", "no-cache");
                Log.d("CONEXION", "Sin cache");
            } else {
                Log.d("CONEXION", "Con cache");
            }*/

            in = new BufferedInputStream(urlConnection.getInputStream());


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count;

            while ((count = in.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }


            byte[] bytes = baos.toByteArray();
            // do something with 'filename' and 'bytes'...

            in2 = new ByteArrayInputStream(bytes);


            //String datos = Utilidades.obtenerStringDeStreamUTF8(in);

            //in2 = new BufferedInputStream(Utilidades.stringToStream(datos));

            //in = urlConnection.getInputStream();


        } catch (IOException e) {

            e.printStackTrace();

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }

            throw new Exception("Error al acceder al servicio");

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }

        }

        return in2;

    }


    public static InputStream doGetJSoup(String url, String userAgent) throws Exception {

        InputStream result = null;
        try {
            result = Jsoup.connect(url).method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .header("Cache-Control", "no-cache")
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("Accept-Encoding", "gzip, deflate, br, zstd")

                    .header("Connection", "keep-alive")

                    .userAgent(userAgent)
                    .execute().bodyStream();

        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Error al acceder al servicio");
        }

        return result;

    }


}
