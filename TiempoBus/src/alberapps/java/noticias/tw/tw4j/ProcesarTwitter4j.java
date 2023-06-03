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
package alberapps.java.noticias.tw.tw4j;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.noticias.tw.Constantes;
import alberapps.java.noticias.tw.TwResultado;
import alberapps.java.util.Conectividad;
/*import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserList;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;*/

/**
 * Acceso a la api de twitter mediante la libreria twitter4j
 */
public class ProcesarTwitter4j {

    //private ConfigurationBuilder builder;

    //private Twitter twitter;

    /**
     * Inicializar
     */
    public void setUp() {
/*
        builder = new ConfigurationBuilder();
        // builder.setUseSSL(true);
        builder.setApplicationOnlyAuthEnabled(true);

        try {

            twitter = new TwitterFactory(builder.build()).getInstance();

            twitter.setOAuthConsumer(Constantes.ck, Constantes.cs);

            // Para que cargue
            OAuth2Token token = twitter.getOAuth2Token();

        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
    }

    /**
     * Recuperar el limite
     */
    public void recuperarRateLimit() {
/*
        try {

            Map<String, RateLimitStatus> rateLimit = twitter.getRateLimitStatus("search");

            RateLimitStatus searchTweetsRateLimit = rateLimit.get("/search/tweets");

        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
    }

    /**
     * Buscar
     *
     * @return lista
     */
    public List<TwResultado> recuperarSearch() {

        List<TwResultado> listaResultados = null;
/*
        try {

            Query query = new Query("from:Alicante_City");

            QueryResult resultados = twitter.search(query);

            List<Status> timeline = resultados.getTweets();

            listaResultados = new ArrayList<>();

            TwResultado resultado = null;

            for (int i = 0; i < timeline.size(); i++) {

                resultado = new TwResultado();

                resultado.setId(Long.toString(timeline.get(i).getId()));
                resultado.setFechaDate(timeline.get(i).getCreatedAt());
                resultado.setNombreCompleto(timeline.get(i).getUser().getName());
                resultado.setUsuario(timeline.get(i).getUser().getScreenName());
                resultado.setMensaje(timeline.get(i).getText());
                resultado.setImagen(timeline.get(i).getUser().getMiniProfileImageURLHttps());

                // Imagen de perfil
                resultado.setImagenBitmap(recuperaImagen(resultado.getImagen()));

                resultado.setUrl("");

                listaResultados.add(resultado);

            }

        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
        return listaResultados;

    }

    /**
     * Recuperar el timeline del usuario indicado
     *
     * @param twuser
     * @param url
     * @param elementos
     * @return listado
     */
    public List<TwResultado> recuperarTimeline(String twuser, String url, int elementos) throws Exception {

        List<TwResultado> listaResultados = new ArrayList<>();
/*

        Paging pagina = new Paging();

        pagina.setCount(elementos);

        ResponseList<Status> timeline = twitter.getUserTimeline(twuser, pagina);

        listaResultados = new ArrayList<>();

        Status linea = timeline.get(0);

        TwResultado resultado = null;

        for (int i = 0; i < timeline.size(); i++) {

            resultado = new TwResultado();

            resultado.setId(Long.toString(timeline.get(i).getId()));
            resultado.setFechaDate(timeline.get(i).getCreatedAt());

            resultado.setFecha(formatearFechaTw(resultado.getFechaDate()));

            resultado.setNombreCompleto(timeline.get(i).getUser().getName());
            resultado.setUsuario("@" + timeline.get(i).getUser().getScreenName());
            resultado.setMensaje(timeline.get(i).getText());
            resultado.setImagen(timeline.get(i).getUser().getBiggerProfileImageURL());

            resultado.setRetweet(timeline.get(i).isRetweet());

            // Imagen de perfil
            // resultado.setImagenBitmap(recuperaImagen(resultado.getImagen()));

            resultado.setUrl(url);

            resultado.setRespuestaId(timeline.get(i).getInReplyToUserId());

            Log.d("twitter", "resp: " + resultado.getRespuestaId());

            listaResultados.add(resultado);

        }


        //throw new Exception("prueba");
*/
        return listaResultados;

    }


    /**
     * Recuperar la lista indicada
     *
     * @param twuser
     * @param url
     * @param elementos
     * @return listado
     */
    public List<TwResultado> recuperarListaUsuario(String twuser, String url, int elementos) throws Exception {

        List<TwResultado> listaResultados = new ArrayList<>();
/*

        Paging pagina = new Paging(1, 25);

        //pagina.setCount(elementos);

        UserList userList = twitter.showUserList(twuser, "tiempobuslist");


        ResponseList<Status> timeline = twitter.getUserListStatuses(userList.getId(), pagina);


        listaResultados = new ArrayList<>();

        Status linea = timeline.get(0);

        TwResultado resultado = null;

        for (int i = 0; i < timeline.size(); i++) {

            resultado = new TwResultado();

            resultado.setId(Long.toString(timeline.get(i).getId()));
            resultado.setFechaDate(timeline.get(i).getCreatedAt());

            resultado.setFecha(formatearFechaTw(resultado.getFechaDate()));

            resultado.setNombreCompleto(timeline.get(i).getUser().getName());
            resultado.setUsuario("@" + timeline.get(i).getUser().getScreenName());
            resultado.setMensaje(timeline.get(i).getText());
            resultado.setImagen(timeline.get(i).getUser().getBiggerProfileImageURL());

            resultado.setRetweet(timeline.get(i).isRetweet());

            resultado.setUrl(url);

            resultado.setRespuestaId(timeline.get(i).getInReplyToUserId());

            Log.d("twitter", "resp: " + resultado.getRespuestaId());

            listaResultados.add(resultado);

        }


        //throw new Exception("prueba");
*/
        return listaResultados;

    }


    /**
     * Recuperar la imagen
     *
     * @param urlParam
     * @return imagen
     */
    public static Bitmap recuperaImagen(String urlParam) {

        InputStream st = null;

        Bitmap bm = null;

        try {

            st = Conectividad.conexionGetIsoStream(urlParam);

            bm = BitmapFactory.decodeStream(st);

        } catch (Exception e) {

            bm = null;

        } finally {

            try {
                if (st != null) {
                    st.close();
                }
            } catch (IOException e) {

            }

        }

        return bm;

    }

    /**
     * Formatear la fecha devuelta por tw
     *
     * @param fecha
     * @return string
     */
    private static String formatearFechaTw(Date fecha) {

        if (fecha != null) {

            final String nuevaFechaP = "EEE dd MMM yyyy HH:mm";

            SimpleDateFormat sfNueva = new SimpleDateFormat(nuevaFechaP, UtilidadesUI.getLocaleUsuario());

            return sfNueva.format(fecha);

        } else {

            return null;
        }

    }

}
