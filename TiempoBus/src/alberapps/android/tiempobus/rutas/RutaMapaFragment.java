/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
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
package alberapps.android.tiempobus.rutas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import alberapps.java.directions.Leg;

/**
 * Mapa lite con info de la ruta
 */
public class RutaMapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    public GoogleMap mMap;

    private String latitud = null;

    private String longitud = null;

    private String polyline = null;

    RutasActivity contexto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getMapAsync(this);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        contexto = (RutasActivity) getActivity();

        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.getUiSettings().setAllGesturesEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.386058, -0.51001810), 17));

        Leg leg = contexto.getLegActual();

        final List<LatLng> listaPuntos = new ArrayList<>();

        LatLng origen = new LatLng(Double.parseDouble(leg.getStartLat()), Double.parseDouble(leg.getStartLng()));
        mMap.addMarker(new MarkerOptions().position(origen));

        listaPuntos.add(origen);

        LatLng destino = new LatLng(Double.parseDouble(leg.getEndLat()), Double.parseDouble(leg.getEndLng()));
        mMap.addMarker(new MarkerOptions().position(destino));

        listaPuntos.add(destino);


        if (listaPuntos != null && !listaPuntos.isEmpty()) {


            LatLngBounds.Builder ltb = new LatLngBounds.Builder();

            for (int i = 0; i < listaPuntos.size(); i++) {
                ltb.include(listaPuntos.get(i));
            }

            LatLngBounds bounds = ltb.build();


            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));


        }


        //polyline = contexto.getPolyline();


        //PolylineOptions poly = new PolylineOptions().


    }


}
