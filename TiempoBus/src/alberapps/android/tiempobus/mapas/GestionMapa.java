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
package alberapps.android.tiempobus.mapas;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.mapas.streetview.StreetViewActivity;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;

/**
 * Gestion del mapa
 */
public class GestionMapa {


    private MapasActivity context;

    private SharedPreferences preferencias;

    public GestionMapa(MapasActivity contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }


    /**
     * Parada seleccionada, mostrar datos
     *
     * @param marker
     */
    public void seleccionInfoParada(Marker marker) {

        final LatLng posicion = marker.getPosition();
        final String titulo = marker.getTitle();
        final String mensaje = marker.getSnippet();

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(marker.getTitle());
        dialog.setMessage(marker.getSnippet());

        int codigo;

        try {
            codigo = Integer.parseInt(marker.getTitle().substring(1, 5));
        } catch (Exception e) {

            // Por si es tram
            int c1 = marker.getTitle().indexOf("[");
            int c2 = marker.getTitle().indexOf("]");

            codigo = Integer.parseInt(marker.getTitle().substring(c1 + 1, c2));

        }

        if (DatosPantallaPrincipal.esTram(Integer.toString(codigo))) {
            //dialog.setIcon(R.drawable.tramway_2);
            dialog.setIcon(R.drawable.ic_tram1);
        } else {
            //dialog.setIcon(R.drawable.bus);
            dialog.setIcon(R.drawable.ic_bus_blue1);
        }

        context.setParadaSeleccionada(marker.getTitle());

        dialog.setPositiveButton(R.string.mapa_ir_parada, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                irParadaSeleccionada();
            }

        });

        dialog.setNeutralButton(R.string.streetview_boton, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                try {


                    Intent i = new Intent(context, StreetViewActivity.class);
                    i.putExtra("LATITUD", posicion.latitude);
                    i.putExtra("LONGITUD", posicion.longitude);

                    i.putExtra("DATOS_LINEA", context.lineaSeleccionadaDesc);
                    i.putExtra("DATOS_TITULO", titulo);
                    i.putExtra("DATOS_MENSAJE", mensaje);

                    context.startActivity(i);

                } catch (Exception ex) {

                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.streetview_ko), Toast.LENGTH_LONG).show();

                }

            }

        });

        dialog.show();

    }


    /**
     * Ir a la parada seleccionada y cerra mapa
     */
    public void irParadaSeleccionada() {

        int codigo;

        if (context.paradaSeleccionada != null) {
            try {
                codigo = Integer.parseInt(context.paradaSeleccionada.substring(1, 5));
            } catch (Exception e) {

                // Por si es tram
                int c1 = context.paradaSeleccionada.indexOf("[");
                int c2 = context.paradaSeleccionada.indexOf("]");

                codigo = Integer.parseInt(context.paradaSeleccionada.substring(c1 + 1, c2));

                Log.d("", "mapa:" + codigo);

            }


            //Devolver nueva parada
            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putInt("POSTE", codigo);
            intent.putExtras(b);

            context.setResult(MainActivity.SUB_ACTIVITY_RESULT_OK, intent);
            context.finish();

        }


    }


}
