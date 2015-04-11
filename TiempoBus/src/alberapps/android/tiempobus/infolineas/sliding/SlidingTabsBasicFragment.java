/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package alberapps.android.tiempobus.infolineas.sliding;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
 * to display a custom {@link android.support.v4.view.ViewPager} title strip which gives continuous feedback to the user
 * when scrolling.
 */
public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";

    /**
     * A custom {@link android.support.v4.view.ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link android.support.v4.view.ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    /**
     * Inflates the {@link android.view.View} which will be displayed by this {@link android.support.v4.app.Fragment}, from the app's
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
     * We set the {@link android.support.v4.view.ViewPager}'s adapter to be an instance of {@link alberapps.android.tiempobus.infolineas.sliding.SlidingTabsBasicFragment.InfoLineaFragmentPagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link android.support.v4.view.ViewPager} so that it can populate itself.
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

        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.white));

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

