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
package alberapps.android.tiempobus.principal;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.BusLlegada;

/**
 * Gestion de acceso al widget
 */
public class GestionarWidget {

    /**
     * Cotexto principal
     */
    private MainActivity context;

    private SharedPreferences preferencias;

    public GestionarWidget(MainActivity contexto, SharedPreferences preferencia) {

        context = contexto;

        preferencias = preferencia;

    }


    /**
     * Enviar la parada al widget
     */
    public void enviarAWidget(BusLlegada busSeleccionado, int paradaActual) {

        if (UtilidadesUI.verificarWidgetInstalado(context)) {

            Intent intent = new Intent();

            intent.setComponent(new ComponentName(UtilidadesUI.WIDGET_PACKAGE, UtilidadesUI.WIDGET_ACTIVITY));

            // 24,2902;10,2902

            intent.putExtra("datos_linea", busSeleccionado.getLinea() + "," + paradaActual + "," + busSeleccionado.getDestino());

            context.startActivity(intent);

        } else {

            AlertDialog.Builder downloadDialog = new AlertDialog.Builder(context);
            downloadDialog.setTitle(context.getString(R.string.menu_widget));
            downloadDialog.setMessage(context.getString(R.string.widget_instalar));
            downloadDialog.setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialogInterface, int i) {
                    Uri uri = Uri.parse("market://details?id=" + UtilidadesUI.WIDGET_PACKAGE);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException anfe) {

                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.widget_market), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            downloadDialog.setNegativeButton(context.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

            downloadDialog.show();

        }

    }


}
