/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class VehiculosItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	private Context context;

	public VehiculosItemizedOverlay(Drawable defaultMarker) {

		super(boundCenterBottom(defaultMarker));

	}

	public VehiculosItemizedOverlay(Drawable defaultMarker, Context context) {

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

		AlertDialog.Builder dialog = new AlertDialog.Builder(context);

		dialog.setTitle(context.getString(R.string.mapas_vehiculo_titulo));
		// dialog.setMessage(item.getSnippet());
		dialog.setMessage(item.getTitle());
		dialog.setIcon(R.drawable.bus);

		dialog.show();

		return true;

	}

}
