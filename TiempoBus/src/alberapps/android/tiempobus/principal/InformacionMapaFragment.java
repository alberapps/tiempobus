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
package alberapps.android.tiempobus.principal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.mapas.GestionarLineas;

/**
 * Mapa lite tarjeta info
 */
public class InformacionMapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    public GoogleMap mMap;

    private String latitud = null;

    private String longitud = null;

    MainActivity contexto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getMapAsync(this);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        contexto = (MainActivity) getActivity();

        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.getUiSettings().setAllGesturesEnabled(false);

        if (contexto.latitudInfo != null && contexto.longitudInfo != null) {
            latitud = contexto.latitudInfo;
            longitud = contexto.longitudInfo;
        }

        if (latitud != null && longitud != null) {

            LatLng nueva = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nueva, 17));

            BitmapDescriptor marker = null;

            if (contexto.datosPantallaPrincipal.esTram(contexto.paradaActual)) {
                marker = GestionarLineas.markerTram();
                //BitmapDescriptorFactory.fromResource(R.drawable.tramway);
            } else {
                marker = GestionarLineas.markerBusAzul();
                //BitmapDescriptorFactory.fromResource(R.drawable.busstop_blue);
            }


            mMap.addMarker(new MarkerOptions().position(nueva).icon(marker));

        } else {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(38.386058, -0.51001810), 17));

        }


    }

    /**
     * Actualizar la posicion en caso de cambiar de parada
     */
    public void actualizarPosicion() {

        if (mMap != null) {

            if (contexto.latitudInfo != null && contexto.longitudInfo != null) {
                latitud = contexto.latitudInfo;
                longitud = contexto.longitudInfo;
            }

            LatLng nueva = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));

            mMap.clear();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nueva, 17));

            BitmapDescriptor marker = null;

            if (contexto.datosPantallaPrincipal.esTram(contexto.paradaActual)) {
                marker = GestionarLineas.markerTram();
                //BitmapDescriptorFactory.fromResource(R.drawable.tramway);
            } else {
                marker = GestionarLineas.markerBusAzul();
                //BitmapDescriptorFactory.fromResource(R.drawable.busstop_blue);
            }

            mMap.addMarker(new MarkerOptions().position(nueva).icon(marker));

        }

    }

}
