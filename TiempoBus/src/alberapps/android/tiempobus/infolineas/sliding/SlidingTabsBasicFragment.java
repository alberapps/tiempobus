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

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.infolineas.FragmentIda;
import alberapps.android.tiempobus.infolineas.FragmentLineas;
import alberapps.android.tiempobus.infolineas.FragmentVuelta;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.infolineas.horariosTram.FragmentHorariosTram;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.android.tiempobus.view.SlidingTabLayout;

/**
 * A basic sample which shows how to use
 * to display a custom {@link ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    /**
     * Inflates the {@link android.view.View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.infolinea_fragment_pager, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)

    /**
     * This is called after the {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)} has finished.
     * Here we can pick out the {@link android.view.View}s we need to configure from the content view.
     * <p/>
     * We set the {@link ViewPager}'s adapter to be an instance of {@link alberapps.android.tiempobus.infolineas.sliding.SlidingTabsBasicFragment.InfoLineaFragmentPagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);


        InfoLineaFragmentPagerAdapter mTabsAdapter = new InfoLineaFragmentPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());


        if (((InfoLineasTabsPager) getActivity()).modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE || ((InfoLineasTabsPager) getActivity()).modoRed == InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {
            mTabsAdapter.addTab(getString(R.string.linea).toUpperCase(), FragmentLineas.class);
            mTabsAdapter.addTab(getString(R.string.ida).toUpperCase(), FragmentIda.class);
            mTabsAdapter.addTab(getString(R.string.vuelta).toUpperCase(), FragmentVuelta.class);
        } else if (((InfoLineasTabsPager) getActivity()).modoRed == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
            mTabsAdapter.addTab(getString(R.string.linea).toUpperCase(), FragmentLineas.class);
            mTabsAdapter.addTab(getString(R.string.parada_tram).toUpperCase(), FragmentIda.class);
            mTabsAdapter.addTab(getString(R.string.infolinea_horarios).toUpperCase(), FragmentHorariosTram.class);
        }


        mViewPager.setAdapter(mTabsAdapter);

        ((InfoLineasTabsPager) getActivity()).mViewPager = mViewPager;


        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.mi_material_blue_principal));

        //Al entrar a horarios directamente
        InfoLineasTabsPager actividad = ((InfoLineasTabsPager) getActivity());

        if (actividad.modoHorario != null && actividad.modoHorario.equals("TRAM")) {

            if (((InfoLineasTabsPager) getActivity()).mViewPager != null && !UtilidadesUI.pantallaTabletHorizontal(getActivity())) {
                ((InfoLineasTabsPager) getActivity()).mViewPager.setCurrentItem(2);
            }

            actividad.modoHorario = null;

        }


        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)


}

