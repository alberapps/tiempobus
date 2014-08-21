package alberapps.java.localizacion;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by albert on 21/08/14.
 */
public class GeocoderInfo {


    public static Localizacion getDatosLocalizacion(String lat, String lon, Context mContext){


        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addresses = null;

        Localizacion localiza = null;

        try {

            Double glat = (Integer.parseInt(lat) / 1E6);
            Double glon = (Integer.parseInt(lon) / 1E6);

            //Recupera direcciones
            addresses = geocoder.getFromLocation(glat,glon, 1);


            if(addresses != null && !addresses.isEmpty()) {

                //Datos para una direccion
                Address address = addresses.get(0);

                localiza = new Localizacion();

                localiza.setDireccion(address.getMaxAddressLineIndex() > 0 ?
                        address.getAddressLine(0) : "");

                localiza.setLocalidad(address.getLocality());

                localiza.setPais(address.getCountryName());


            }else{
                return null;
            }


        } catch (IOException e1) {
            Log.e("GeocoderInfo",
                    "IO Exception in getFromLocation()");
            e1.printStackTrace();

            return null;

        } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments " + lat +
                    " , " +
                    lon +
                    " passed to address service";
            Log.e("GeocoderInfo", errorString);
            e2.printStackTrace();

            return null;
        }









        return localiza;

    }



}
