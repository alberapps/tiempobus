/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
 *
 *  based on code by http://www.ibm.com/developerworks/java/library/j-coordconvert/: 2007 Sami Salkosuo
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
package alberapps.android.tiempobus.mapas;

import java.math.BigDecimal;

/**
 * Utilidades para transformar las coordenadas utm en lat y long
 */
public class UtilidadesGeo {

    /**
     * Coordenadas para Alicante
     *
     * @param y
     * @param x
     * @return
     */
    public static String getLatLongUTMBus(double y, double x) {

        return utmToLatLong("N", 30, y, x);

    }

    /**
     * Transforma las coordenadas
     *
     * @param hemisphere
     * @param zone
     * @param northing
     * @param easting
     * @return coordenadas
     */
    private static String utmToLatLong(String hemisphere, int zone, double northing, double easting) {

        if (hemisphere.equals("S")) {
            northing = 10000000 - northing;
        }

        double a = 6378137;
        double e = 0.081819191;
        double e1sq = 0.006739497;
        double k0 = 0.9996;

        double arc = northing / k0;
        double mu = arc / (a * (1 - Math.pow(e, 2) / 4.0 - 3 * Math.pow(e, 4) / 64.0 - 5 * Math.pow(e, 6) / 256.0));

        double ei = (1 - Math.pow((1 - e * e), (1 / 2.0))) / (1 + Math.pow((1 - e * e), (1 / 2.0)));

        double ca = 3 * ei / 2 - 27 * Math.pow(ei, 3) / 32.0;

        double cb = 21 * Math.pow(ei, 2) / 16 - 55 * Math.pow(ei, 4) / 32;
        double cc = 151 * Math.pow(ei, 3) / 96;
        double cd = 1097 * Math.pow(ei, 4) / 512;
        double phi1 = mu + ca * Math.sin(2 * mu) + cb * Math.sin(4 * mu) + cc * Math.sin(6 * mu) + cd * Math.sin(8 * mu);

        double n0 = a / Math.pow((1 - Math.pow((e * Math.sin(phi1)), 2)), (1 / 2.0));

        double r0 = a * (1 - e * e) / Math.pow((1 - Math.pow((e * Math.sin(phi1)), 2)), (3 / 2.0));
        double fact1 = n0 * Math.tan(phi1) / r0;

        double a1 = 500000 - easting;
        double dd0 = a1 / (n0 * k0);
        double fact2 = dd0 * dd0 / 2;

        double t0 = Math.pow(Math.tan(phi1), 2);
        double Q0 = e1sq * Math.pow(Math.cos(phi1), 2);
        double fact3 = (5 + 3 * t0 + 10 * Q0 - 4 * Q0 * Q0 - 9 * e1sq) * Math.pow(dd0, 4) / 24;

        double fact4 = (61 + 90 * t0 + 298 * Q0 + 45 * t0 * t0 - 252 * e1sq - 3 * Q0 * Q0) * Math.pow(dd0, 6) / 720;

        double lof1 = a1 / (n0 * k0);
        double lof2 = (1 + 2 * t0 + Q0) * Math.pow(dd0, 3) / 6.0;
        double lof3 = (5 - 2 * Q0 + 28 * t0 - 3 * Math.pow(Q0, 2) + 8 * e1sq + 24 * Math.pow(t0, 2)) * Math.pow(dd0, 5) / 120;
        double a2 = (lof1 - lof2 + lof3) / Math.cos(phi1);
        double a3 = a2 * 180 / Math.PI;

        double latitude = 180 * (phi1 - fact1 * (fact2 + fact3 + fact4)) / Math.PI;

        double zoneCM;

        if (zone > 0) {
            zoneCM = 6 * zone - 183.0;
        } else {
            zoneCM = 3.0;
        }

        double longitude = zoneCM - a3;

        if (hemisphere.equals("S")) {
            latitude = -latitude;
        }

        //Redondeo
        //longitude = round(longitude, 6);
        //latitude = round(latitude, 6);

        //Log.d("GEOPOSICION", "lat: " + latitude + " long: " + longitude);

        return longitude + "," + latitude;

    }


    public static double round(double value, int places) {

		/*long factor = (long) Math.pow(10,  places);
        value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;*/

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);

        return bd.doubleValue();


    }


    /**
     * Coordenadas con correccion
     *
     * @param coord
     * @return
     */
    public static String getCoordenadasCorreccion(String coord) {

        String[] coordenadas = coord.split(",");

        double lat = Double.parseDouble(coordenadas[1]); // 38.386058;
        double lng = Double.parseDouble(coordenadas[0]); // -0.510018;

        int glat = (int) (lat * 1E6);
        int glng = (int) (lng * 1E6);

        // Calibrado
        // https://github.com/Sloy/SeviBus
        // Rafa Vazquez (Sloy)
        glat = glat - 200 * 10;
        glng = glng - 130 * 10;
        //

        double latB = glat / 1E6;
        double longB = glng / 1E6;


        String modificado = longB + "," + latB + ",0";


        return modificado;

    }


}
