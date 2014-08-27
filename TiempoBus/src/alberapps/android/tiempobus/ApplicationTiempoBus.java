/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
 *
 *  based on code by The Android Open Source Project Copyright (C) 2013
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
package alberapps.android.tiempobus;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class ApplicationTiempoBus extends Application {

    public enum TrackerName {
        APP_TRACKER
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public ApplicationTiempoBus() {

        super();

    }

    synchronized Tracker getTracker(TrackerName trackerId) {

        Log.d("APP", "inicia tracker");

        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            /*
			 * Tracker t = (trackerId == TrackerName.APP_TRACKER) ?
			 * analytics.newTracker(PROPERTY_ID) : (trackerId ==
			 * TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(
			 * R.xml.global_tracker) :
			 * analytics.newTracker(R.xml.ecommerce_tracker);
			 */

            Tracker t = analytics.newTracker(R.xml.app_tracker);

            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
