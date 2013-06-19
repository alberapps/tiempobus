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

import com.google.gson.annotations.SerializedName;

public class Resultado {

	@SerializedName("from_user_id_str")
    public String fromUserIdStr;
    
    @SerializedName("profile_image_url")
    public String profileImageUrl;
    
    @SerializedName("created_at")
    public String createdAt;
    
    @SerializedName("from_user")
    public String fromUser;
    
    @SerializedName("id_str")
    public String idStr;
    
    public Metadatos metadata;
    
    @SerializedName("to_user_id")
    public String toUserId;
    
    public String text;
    
    public long id;
    
    @SerializedName("from_user_id")
    public String from_user_id;

    @SerializedName("iso_language_code")
    public String isoLanguageCode;

    @SerializedName("to_user_id_str")
    public String toUserIdStr;

    public String source;

    @SerializedName("from_user_name")
    public String fromUserName;
    
	
}
