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
package alberapps.android.tiempobus.mapas;

import java.util.ArrayList;

import alberapps.android.tiempobus.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapasItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	private Context context;

	public MapasItemizedOverlay(Drawable defaultMarker) {

		super(boundCenterBottom(defaultMarker));

	}

	public MapasItemizedOverlay(Drawable defaultMarker, Context context) {

		this(defaultMarker);
		this.context = context;

	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return mOverlays.get(arg0);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected boolean onTap(int arg0) {

		OverlayItem item = mOverlays.get(arg0);

		final int lat = item.getPoint().getLatitudeE6();
		final int lg = item.getPoint().getLongitudeE6();

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);

		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.setIcon(R.drawable.busstop_blue);

		((MapasActivity) context).setParadaSeleccionada(item.getTitle());

		dialog.setPositiveButton(R.string.mapa_ir_parada, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				((MapasActivity) context).irParadaSeleccionada();
			}

		});

		dialog.setNeutralButton(R.string.streetview_boton, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				try {

					// -0.489838 38.346242
					// 38.344707,-0.483464
					// String longitud = "-0.483464";
					// String latitud = "38.344707";

					double glat = (double) (lat / 1E6);
					double glng = (double) (lg / 1E6);

					String latitud = Double.toString(glat);
					String longitud = Double.toString(glng);

					Intent streetView = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.streetview:cbll=" + latitud + "," + longitud + "&cbp=1,180,,0,1.0"));
					// streetView.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					context.startActivity(streetView);

				} catch (Exception ex) {

					Toast.makeText(context, context.getString(R.string.streetview_ko), Toast.LENGTH_LONG).show();

				}

			}

		});

		dialog.show();

		return true;

	}

}
