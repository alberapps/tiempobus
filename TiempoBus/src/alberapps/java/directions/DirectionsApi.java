/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
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
package alberapps.java.directions;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.util.Conectividad;

/**
 * Consulta de google maps directions
 */
public class DirectionsApi {


    private final static String LOG_TAG = DirectionsApi.class.getSimpleName();

    public static Direction getDirections(String origen, String destino, String medio, Context context) throws Exception {

        try {

            //https://maps.googleapis.com/maps/api/directions/output?parameters
            //http://maps.googleapis.com/maps/api/directions/json?origin=San%20Vicente%20del%20Raspeig&destination=Alicante&region=es&mode=transit&alternatives=true&sensor=false

            String idioma = UtilidadesUI.getIdiomaRutas();

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https").authority("maps.googleapis.com").appendPath("maps").appendPath("api")
                    .appendPath("directions")
                    .appendPath("json")
                    .appendQueryParameter("origin", origen)
                    .appendQueryParameter("destination", destino)
                    .appendQueryParameter("region", "es")
                    .appendQueryParameter("mode", "transit")
                    .appendQueryParameter("alternatives", "true")
                    .appendQueryParameter("sensor", "false")
                    .appendQueryParameter("language", idioma)
                    .appendQueryParameter("key", context.getString(R.string.api_key_directions));


            Uri url = builder.build();

            Direction datos = new Direction();

            String jsonDirection = Conectividad.conexionGetUtf8String(url.toString(), true);

            datos = parsea(jsonDirection);

            return datos;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(e);
        }

    }


    /**
     * Parsea los datos json recibidos
     *
     * @param json
     * @return
     */
    public static Direction parsea(String json) {

        Direction data = new Direction();


        try {


            JSONObject jsonObject = new JSONObject(json);

            data.setStatus(jsonObject.getString("status"));

            data.setRoutes(new ArrayList<Route>());
            JSONArray routes = jsonObject.getJSONArray("routes");

            //Rutas alternativas
            for (int i = 0; i < routes.length(); i++) {
                Route route1 = new Route();
                data.getRoutes().add(route1);
                JSONObject route = routes.getJSONObject(i);

                route1.setCopyrights(route.getString("copyrights"));

                if (route.has("overview_polyline") && route.getJSONObject("overview_polyline").has("points")) {
                    route1.setPolyline(route.getJSONObject("overview_polyline").getString("points"));
                }

                if (route.has("warnings")) {
                    JSONArray warn = route.getJSONArray("warnings");

                    if (warn != null && warn.length() > 0) {
                        route1.setWarning(new ArrayList<String>());
                        for (int k = 0; k < warn.length(); k++) {
                            route1.getWarning().add(warn.getString(k));
                        }
                    }
                }


                JSONArray legs = route.getJSONArray("legs");


                if (legs != null && legs.length() > 0) {

                    route1.setLegs(new ArrayList<Leg>());

                    for (int j = 0; j < legs.length(); j++) {
                        Leg leg1 = new Leg();
                        leg1.setResumen(new ArrayList<String>());
                        route1.getLegs().add(leg1);
                        //Datos leg
                        JSONObject leg = legs.getJSONObject(j);

                        leg1.setArrivalTime(leg.getJSONObject("arrival_time").getString("text"));
                        leg1.setDepartureTime(leg.getJSONObject("departure_time").getString("text"));
                        leg1.setDistance(leg.getJSONObject("distance").getString("text"));
                        leg1.setDuration(leg.getJSONObject("duration").getString("text"));
                        leg1.setEndAdress(leg.getString("end_address"));
                        //String endLocation = leg.getJSONObject("end_location").getString("text");
                        //String endLocation = leg.getJSONObject("end_location").getString("text");
                        leg1.setStartArddress(leg.getString("start_address"));

                        if (leg.has("start_location") && leg.getJSONObject("start_location").has("lat")) {
                            leg1.setStartLat(leg.getJSONObject("start_location").getString("lat"));
                        }

                        if (leg.has("start_location") && leg.getJSONObject("start_location").has("lng")) {
                            leg1.setStartLng(leg.getJSONObject("start_location").getString("lng"));
                        }

                        if (leg.has("end_location") && leg.getJSONObject("end_location").has("lat")) {
                            leg1.setEndLat(leg.getJSONObject("end_location").getString("lat"));
                        }

                        if (leg.has("end_location") && leg.getJSONObject("end_location").has("lng")) {
                            leg1.setEndLng(leg.getJSONObject("end_location").getString("lng"));
                        }

                        //Steps
                        JSONArray steps = leg.getJSONArray("steps");
                        if (steps != null && steps.length() > 0) {

                            leg1.setSteps(new ArrayList<Step>());

                            for (int k = 0; k < steps.length(); k++) {

                                Step step1 = new Step();
                                leg1.getSteps().add(step1);

                                JSONObject step = steps.getJSONObject(k);
                                step1.setDistance(step.getJSONObject("distance").getString("text"));
                                step1.setDuration(step.getJSONObject("duration").getString("text"));
                                if (step.has("html_instructions")) {
                                    step1.setHtmlInstructions(step.getString("html_instructions"));
                                }

                                if (step.has("transit_details")) {

                                    step1.setTransitDetails(new TransitDetails());
                                    JSONObject transitDetail = step.getJSONObject("transit_details");

                                    if (transitDetail.has("headsign")) {
                                        step1.getTransitDetails().setHeadsign(transitDetail.getString("headsign"));
                                    }

                                    if (transitDetail.has("arrival_stop") && !transitDetail.getJSONObject("arrival_stop").isNull("name")) {
                                        step1.getTransitDetails().setArrivalStop(transitDetail.getJSONObject("arrival_stop").getString("name"));
                                    }

                                    if (transitDetail.has("departure_stop") && !transitDetail.getJSONObject("departure_stop").isNull("name")) {
                                        step1.getTransitDetails().setDepartureStop(transitDetail.getJSONObject("departure_stop").getString("name"));
                                    }

                                    if (transitDetail.has("arrival_time")) {
                                        step1.getTransitDetails().setArrivalTime(transitDetail.getJSONObject("arrival_time").getString("text"));
                                    }

                                    if (transitDetail.has("departure_time")) {
                                        step1.getTransitDetails().setDepartureTime(transitDetail.getJSONObject("departure_time").getString("text"));
                                    }

                                    if (transitDetail.has("line")) {
                                        step1.getTransitDetails().setLine(new Line());

                                        JSONObject line1 = transitDetail.getJSONObject("line");
                                        step1.getTransitDetails().getLine().setType(line1.getJSONObject("vehicle").getString("type"));
                                        if(!line1.isNull("name")) {
                                            step1.getTransitDetails().getLine().setName(line1.getString("name"));
                                        }

                                        if (line1.has("short_name")) {
                                            step1.getTransitDetails().getLine().setShortName(line1.getString("short_name"));
                                        }


                                    }

                                }


                                step1.setTravelMode(step.getString("travel_mode"));
                                if (step1.getTravelMode().equals("TRANSIT")) {
                                    leg1.getResumen().add(step1.getTransitDetails().getLine().getType() + ": " + step1.getTransitDetails().getLine().getShortName());
                                } else {
                                    leg1.getResumen().add(step1.getTravelMode());
                                }


                                //Segundo nivel de steps
                                if (step.has("steps")) {
                                    JSONArray steps2 = step.getJSONArray("steps");
                                    if (steps2 != null && steps2.length() > 0) {

                                        step1.setSteps(new ArrayList<Step>());

                                        for (int l = 0; l < steps2.length(); l++) {
                                            Step step2 = new Step();
                                            step1.getSteps().add(step2);

                                            JSONObject stepB = steps2.getJSONObject(l);
                                            step2.setDistance(stepB.getJSONObject("distance").getString("text"));
                                            step2.setDuration(stepB.getJSONObject("duration").getString("text"));
                                            if (stepB.has("html_instructions")) {
                                                step2.setHtmlInstructions(stepB.getString("html_instructions"));
                                            }
                                            if (stepB.has("transit_details")) {

                                                step2.setTransitDetails(new TransitDetails());
                                                JSONObject transitDetail2 = stepB.getJSONObject("transit_details");

                                                if (transitDetail2.has("headsign")) {
                                                    step2.getTransitDetails().setHeadsign(transitDetail2.getString("headsign"));
                                                }

                                                if (transitDetail2.has("arrival_stop")) {
                                                    step2.getTransitDetails().setArrivalStop(transitDetail2.getJSONObject("arrival_stop").getString("name"));
                                                }

                                                if (transitDetail2.has("departure_stop")) {
                                                    step2.getTransitDetails().setDepartureStop(transitDetail2.getJSONObject("departure_stop").getString("name"));
                                                }

                                                if (stepB.has("line")) {
                                                    step2.getTransitDetails().setLine(new Line());

                                                    JSONObject lineB = transitDetail2.getJSONObject("line");
                                                    step2.getTransitDetails().getLine().setType(lineB.getJSONObject("vehicle").getString("type"));
                                                    step2.getTransitDetails().getLine().setName(lineB.getString("name"));

                                                    if (lineB.has("short_name")) {
                                                        step2.getTransitDetails().getLine().setShortName(lineB.getString("short_name"));
                                                    }


                                                }

                                            }


                                            step2.setTravelMode(stepB.getString("travel_mode"));


                                        }

                                    }

                                }

                            }
                        }
                    }
                }

            }

            Log.d(LOG_TAG, "Resultados: " + data.getRoutes().size());

            return data;


        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }


    }
}
