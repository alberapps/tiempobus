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
package alberapps.java.noticias.tw;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Respuesta {

public List<Resultado> results;
    
    @SerializedName("max_id")
    public long maxId;
    
    @SerializedName("since_id")
    public int sinceId;
    
    @SerializedName("refresh_url")
    public String refreshUrl;
    
    @SerializedName("next_page")
    public String nextPage;
    
    @SerializedName("results_per_page")
    public int resultsPerPage;
    
    public int page;
    
    @SerializedName("completed_in")
    public double completedIn;
    
    @SerializedName("since_id_str")
    public String sinceIdStr;
    
    @SerializedName("max_id_str")
    public String maxIdStr;
    
    public String query;


	
	////[{"created_at":"Fri Jun 14 18:40:59 +0000 2013","id":345611846021181440,"id_str":"345611846021181440","text":"\u00bfMontar en un Ferrari F1 en la Playa de San Juan de #Alicante? Del 14 al 16 de Junio puedes https:\/\/t.co\/ORC2kiYbWo","source":"web","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":246985468,"id_str":"246985468","name":"Turismo Alicante","screen_name":"Alicante_City","location":"Alicante","description":"Cuenta Oficial del Patronato Municipal de Turismo y Playas de Alicante (Organismo encargado de la promoci\u00f3n y difusi\u00f3n tur\u00edstica de la ciudad de Alicante)","url":"http:\/\/t.co\/S6kErxdnun","entities":{"url":{"urls":[{"url":"http:\/\/t.co\/S6kErxdnun","expanded_url":"http:\/\/www.alicanteturismo.com","display_url":"alicanteturismo.com","indices":[0,22]}]},"description":{"urls":[]}},"protected":false,"followers_count":3083,"friends_count":466,"listed_count":98,"created_at":"Thu Feb 03 21:26:38 +0000 2011","favourites_count":7,"utc_offset":-36000,"time_zone":"Hawaii","geo_enabled":true,"verified":false,"statuses_count":2594,"lang":"es","contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/a0.twimg.com\/profile_background_images\/823343874\/65f9a163bd68e597220d18eb995b9403.jpeg","profile_background_image_url_https":"https:\/\/si0.twimg.com\/profile_background_images\/823343874\/65f9a163bd68e597220d18eb995b9403.jpeg","profile_background_tile":true,"profile_image_url":"http:\/\/a0.twimg.com\/profile_images\/2767302305\/2917b0c18821b142a6da450a5247c593_normal.jpeg","profile_image_url_https":"https:\/\/si0.twimg.com\/profile_images\/2767302305\/2917b0c18821b142a6da450a5247c593_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/246985468\/1353929991","profile_link_color":"0084B4","profile_sidebar_border_color":"000000","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":1,"favorite_count":1,"entities":{"hashtags":[{"text":"Alicante","indices":[52,61]}],"symbols":[],"urls":[{"url":"https:\/\/t.co\/ORC2kiYbWo","expanded_url":"https:\/\/www.facebook.com\/photo.php?fbid=521208914595157&set=a.460997367282979.93697.161157567266962&type=1","display_url":"facebook.com\/photo.php?fbid\u2026","indices":[92,115]}],"user_mentions":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"lang":"es"},{"created_at":"Fri Jun 14 18:30:22 +0000 2013","id":345609172244635649,"id_str":"345609172244635649","text":"En media hora: Preg\u00f3n de las Hogueras de San Juan 2013 desde la plaza del Ayto. Que empiece la fiesta! http:\/\/t.co\/Hg4O1PL3Wv","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":246985468,"id_str":"246985468","name":"Turismo Alicante","screen_name":"Alicante_City","location":"Alicante","description":"Cuenta Oficial del Patronato Municipal de Turismo y Playas de Alicante (Organismo encargado de la promoci\u00f3n y difusi\u00f3n tur\u00edstica de la ciudad de Alicante)","url":"http:\/\/t.co\/S6kErxdnun","entities":{"url":{"urls":[{"url":"http:\/\/t.co\/S6kErxdnun","expanded_url":"http:\/\/www.alicanteturismo.com","display_url":"alicanteturismo.com","indices":[0,22]}]},"description":{"urls":[]}},"protected":false,"followers_count":3083,"friends_count":466,"listed_count":98,"created_at":"Thu Feb 03 21:26:38 +0000 2011","favourites_count":7,"utc_offset":-36000,"time_zone":"Hawaii","geo_enabled":true,"verified":false,"statuses_count":2594,"lang":"es","contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/a0.twimg.com\/profile_background_images\/823343874\/65f9a163bd68e597220d18eb995b9403.jpeg","profile_background_image_url_https":"https:\/\/si0.twimg.com\/profile_background_images\/823343874\/65f9a163bd68e597220d18eb995b9403.jpeg","profile_background_tile":true,"profile_image_url":"http:\/\/a0.twimg.com\/profile_images\/2767302305\/2917b0c18821b142a6da450a5247c593_normal.jpeg","profile_image_url_https":"https:\/\/si0.twimg.com\/profile_images\/2767302305\/2917b0c18821b142a6da450a5247c593_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/246985468\/1353929991","profile_link_color":"0084B4","profile_sidebar_border_color":"000000","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":6,"favorite_count":0,"entities":{"hashtags":[],"symbols":[],"urls":[{"url":"http:\/\/t.co\/Hg4O1PL3Wv","expanded_url":"http:\/\/youtu.be\/1k8bYVlz8f8","display_url":"youtu.be\/1k8bYVlz8f8","indices":[103,125]}],"user_mentions":[]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"lang":"es"},{"created_at":"Thu Jun 13 21:46:00 +0000 2013","id":345296017199079425,"id_str":"345296017199079425","text":"En 15 minutos empieza la 9\u00aa sinfon\u00eda de Caballer en la explanada del ADDA. A\u00fan llegas! http:\/\/t.co\/MkPfBOqfgt","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":246985468,"id_str":"246985468","name":"Turismo Alicante","screen_name":"Alicante_City","location":"Alicante","description":"Cuenta Oficial del Patronato Municipal de Turismo y Playas de Alicante (Organismo encargado de la promoci\u00f3n y difusi\u00f3n tur\u00edstica de la ciudad de Alicante)","url":"http:\/\/t.co\/S6kErxdnun","entities":{"url":{"urls":[{"url":"http:\/\/t.co\/S6kErxdnun","expanded_url":"http:\/\/www.alicanteturismo.com","display_url":"alicanteturismo.com","indices":[0,22]}]},"description":{"urls":[]}},"protected":false,"followers_count":3083,"friends_count":466,"listed_count":98,"created_at":"Thu Feb 03 21:26:38 +0000 2011","favourites_count":7,"utc_offset":-36000,"time_zone":"Hawaii","geo_enabled":true,"verified":false,"statuses_count":2594,"lang":"es","contributors_enabled":false,"is_translator":false,"profile_background_color":"C0DEED","profile_background_image_url":"http:\/\/a0.twimg.com\/profile_background_images\/823343874\/65f9a163bd68e597220d18eb995b9403.jpeg","profile_background_image_url_https":"https:\/\/si0.twimg.com\/profile_background_images\/823343874\/65f9a163bd68e597220d18eb995b9403.jpeg","profile_background_tile":true,"profile_image_url":"http:\/\/a0.twimg.com\/profile_images\/2767302305\/2917b0c18821b142a6da450a5247c593_normal.jpeg","profile_image_url_https":"https:\/\/si0.twimg.com\/profile_images\/2767302305\/2917b0c18821b142a6da450a5247c593_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/246985468\/1353929991","profile_link_color":"0084B4","profile_sidebar_border_color":"000000","profile_sidebar_fill_color":"DDEEF6","profile_text_color":"333333","profile_use_background_image":true,"default_profile":false,"default_profile_image":false,"following":null,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":4,"favorite_count":0,"entities":{"hashtags":[],"symbols":[],"urls":[],"user_mentions":[],"media":[{"id":345296017203273729,"id_str":"345296017203273729","indices":[87,109],"media_url":"http:\/\/pbs.twimg.com\/media\/BMq85e_CIAEZvuA.jpg","media_url_https":"https:\/\/pbs.twimg.com\/media\/BMq85e_CIAEZvuA.jpg","url":"http:\/\/t.co\/MkPfBOqfgt","display_url":"pic.twitter.com\/MkPfBOqfgt","expanded_url":"http:\/\/twitter.com\/Alicante_City\/status\/345296017199079425\/photo\/1","type":"photo","sizes":{"large":{"w":645,"h":800,"resize":"fit"},"small":{"w":340,"h":422,"resize":"fit"},"medium":{"w":600,"h":744,"resize":"fit"},"thumb":{"w":150,"h":150,"resize":"crop"}}}]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"lang":"es"},{"created_at":"Thu Jun 13 20:24:02 +0000 2013","id":345275388022358017,"id_str":"345275388022358017","text":"@miguelgcos La verdad es que el mar ayuda :) Que lo pases muy bien!!!","source":"\u003ca href=\"http:\/\/twitter.com\/download\/iphone\" rel=\"nofollow\"\u003eTwitter for iPhone\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":345272336511016961,"in_reply_to_status_id_str":"345272336511016961","in_reply_to_user_id":273463178,"in_reply_to_user_id_str":"273463178","in_reply_to_screen_name":"miguelgcos","user":{"id":246985468,"id_str":"246985468","name":"Turismo Alicante","screen_name":"Alicante_City","location":"Alicante","description":"Cuenta Oficial del Patronato Municipal de Turismo y Playas de Alicante (Organismo encargado de la promoci\u00f3n y difusi\u00f3n tur\u00edstica de la ciudad de Alicante)","url":"http:\/\/t.co\/S6kErxdnun","entities":{"url":{"urls":[{"url":"http:\/\/t.co\/S6kErxdnun","expanded_url":"http:\/\/www.alicanteturismo.com","display_url":"alicanteturismo.com","indices":[0,22]}]},"description":{"urls":[]}},"protected":false,"followers_count":3083,"friends_count":466,"listed_count":98,"created_at":"Thu Feb 03 21:26:38 +0000 2011","favourites_count":7,"utc_offset":-36000,"time_zone":"Hawaii","geo_enabled":true,"verified":false,"statuses_count":2594,"lang":"es","contributors_enabled":false,"is_translator"...

	
}
