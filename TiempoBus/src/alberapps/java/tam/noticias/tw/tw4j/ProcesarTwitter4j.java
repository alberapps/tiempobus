package alberapps.java.tam.noticias.tw.tw4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import alberapps.java.tam.noticias.tw.TwResultado;
import alberapps.java.tam.noticias.tw.v11.Constantes;
import alberapps.java.util.Utilidades;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ProcesarTwitter4j {

	private ConfigurationBuilder builder;

	public void setUp() {

		builder = new ConfigurationBuilder();
		builder.setUseSSL(true);
		builder.setApplicationOnlyAuthEnabled(true);

	}

	public void recuperarRateLimit() {

		try {
			Twitter twitter = new TwitterFactory(builder.build()).getInstance();

			twitter.setOAuthConsumer(Constantes.ck, Constantes.cs);

			OAuth2Token token = twitter.getOAuth2Token();

			Map<String, RateLimitStatus> rateLimit = twitter.getRateLimitStatus("search");

			RateLimitStatus searchTweetsRateLimit = rateLimit.get("/search/tweets");

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public List<TwResultado> recuperarSearch() {

		List<TwResultado> listaResultados = null;

		try {

			Twitter twitter = new TwitterFactory(builder.build()).getInstance();

			twitter.setOAuthConsumer(Constantes.ck, Constantes.cs);

			OAuth2Token token = twitter.getOAuth2Token();
			
			Query query = new Query("from:Alicante_City");			
			
			QueryResult resultados = twitter.search(query );
			
			List<Status> timeline = resultados.getTweets(); 

			listaResultados = new ArrayList<TwResultado>();

			
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

		return listaResultados;

	}
	
	public List<TwResultado> recuperarTimeline() {

		List<TwResultado> listaResultados = null;

		try {

			Twitter twitter = new TwitterFactory(builder.build()).getInstance();

			twitter.setOAuthConsumer(Constantes.ck, Constantes.cs);

			OAuth2Token token = twitter.getOAuth2Token();
			
			ResponseList<Status> timeline = twitter.getUserTimeline("alberapps");

			listaResultados = new ArrayList<TwResultado>();

			Status linea = timeline.get(0);

			TwResultado resultado = null;

			for (int i = 0; i < timeline.size(); i++) {

				resultado = new TwResultado();

				resultado.setId(Long.toString(timeline.get(i).getId()));
				resultado.setFechaDate(timeline.get(i).getCreatedAt());
				
				resultado.setFecha(resultado.getFechaDate().toString());
				
				resultado.setNombreCompleto(timeline.get(i).getUser().getName());
				resultado.setUsuario(timeline.get(i).getUser().getScreenName());
				resultado.setMensaje(timeline.get(i).getText());
				resultado.setImagen(timeline.get(i).getUser().getMiniProfileImageURLHttps());

				// Imagen de perfil
				resultado.setImagenBitmap(recuperaImagen(resultado.getImagen()));

				resultado.setUrl("http://twitter.com/Alicante_City");

				listaResultados.add(resultado);

			}

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listaResultados;

	}

	/**
	 * Recuperar la imagen
	 * 
	 * @param urlParam
	 * @return imagen
	 */
	private static Bitmap recuperaImagen(String urlParam) {

		InputStream st = null;

		Bitmap bm = null;

		try {

			st = Utilidades.recuperarStreamConexionSimple(urlParam);

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

}
