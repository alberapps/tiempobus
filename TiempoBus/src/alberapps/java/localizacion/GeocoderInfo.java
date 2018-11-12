/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2014 Alberto Montiel
 * <p>
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package alberapps.java.localizacion;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import alberapps.android.tiempobus.R;

/**
 * Recuperar la direccion de las coordenadas de la parada empleando Geocoder
 */
public class GeocoderInfo {


    public static Localizacion getDatosLocalizacion(String lat, String lon, Context mContext) {


        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

        List<Address> addresses = null;

        Localizacion localiza = null;

        try {

            Double glat = (Integer.parseInt(lat) / 1E6);
            Double glon = (Integer.parseInt(lon) / 1E6);

            //Recupera direcciones
            addresses = geocoder.getFromLocation(glat, glon, 1);


            if (addresses != null && !addresses.isEmpty()) {

                //Datos para una direccion
                Address address = addresses.get(0);

                localiza = new Localizacion();

                String direc = "";

                /*if (address.getFeatureName() != null && !address.getFeatureName().equals("") && !address.getFeatureName().equals(address.getLocality())) {
                    direc = address.getFeatureName();
                    localiza.setLocalidad(address.getLocality());
                } else if (address.getThoroughfare() != null && !address.getThoroughfare().equals("")) {
                    direc = address.getThoroughfare();

                    if (address.getSubThoroughfare() != null && !address.getSubThoroughfare().equals("")) {

                        if (direc.length() > 0) {
                            direc += ", ";
                        }

                        direc += address.getSubThoroughfare();

                    }
                    localiza.setLocalidad(address.getLocality());

                } else*/

                if (address.getAddressLine(0) != null && !address.getAddressLine(0).equals("")) {

                    direc = address.getAddressLine(0);

                    if (direc.contains(", " + address.getCountryName())) {
                        direc = direc.substring(0, direc.indexOf(", " + address.getCountryName()));
                    }
                    if (direc.contains(", " + address.getSubAdminArea())) {
                        direc = direc.substring(0, direc.indexOf(", " + address.getSubAdminArea()));
                    }

                }

                //direc += address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";

                localiza.setDireccion(direc);


                if ((localiza.getDireccion() == null || localiza.getDireccion().equals("")) &&
                        (localiza.getLocalidad() == null || localiza.getLocalidad().equals(""))) {

                    localiza.setDireccion(mContext.getString(R.string.main_no_items));

                }

                localiza.setPais(address.getCountryName());


            } else {
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
