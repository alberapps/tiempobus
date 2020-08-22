/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
 * <p/>
 * based on Copyright (C) 2013 The Android Open Source Project
 * <p/>
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
package alberapps.android.tiempobus.infolineas.sliding;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 */
public class InfoLineaFragmentPagerAdapter extends FragmentPagerAdapter {

    Context context;

    private final ArrayList<TabInfo> mTabs = new ArrayList<>();

    static final class TabInfo {
        private final String tag;
        private final Class<?> clss;
        //private final Bundle args;

        TabInfo(String _tag, Class<?> _class) {
            tag = _tag;
            clss = _class;
            //args = _args;
        }
    }

    public void addTab(String _tag, Class<?> _class) {
        TabInfo info = new TabInfo(_tag, _class);
        mTabs.add(info);
        notifyDataSetChanged();
    }


    public InfoLineaFragmentPagerAdapter(FragmentManager fm, Context context1) {
        super(fm);
        context = context1;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //return super.getPageTitle(position);

        return mTabs.get(position).tag;

    }

    @Override
    public Fragment getItem(int position) {
        //return ArrayListFragment.newInstance(position);

        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(context, info.clss.getName());


    }
}