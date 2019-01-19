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

package alberapps.android.tiempobus.noticias.sliding;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.infolineas.sliding.InfoLineaFragmentPagerAdapter;
import alberapps.android.tiempobus.view.SlidingTabLayout;
import alberapps.android.tiempobus.noticias.FragmentNoticias;
import alberapps.android.tiempobus.noticias.FragmentNoticiasRss;
import alberapps.android.tiempobus.noticias.FragmentTwitter;
import alberapps.android.tiempobus.noticias.NoticiasTabsPager;
import alberapps.java.tram.UtilidadesTRAM;

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
     * A {@link ViewPager} which will be used in conjunction with the {@link alberapps.android.tiempobus.infolineas.sliding.view.SlidingTabLayout} above.
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
     * We set the {@link ViewPager}'s adapter to be an instance of {@link alberapps.android.tiempobus.noticias.sliding.SlidingTabsBasicFragment.InfoLineaFragmentPagerAdapter}. The
     * {@link alberapps.android.tiempobus.infolineas.sliding.view.SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);


        InfoLineaFragmentPagerAdapter mTabsAdapter = new InfoLineaFragmentPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());

        mTabsAdapter.addTab(getString(R.string.tab_noticias).toUpperCase(), FragmentNoticias.class);

        if (UtilidadesTRAM.ACTIVADO_TRAM) {
            mTabsAdapter.addTab(getString(R.string.rss_tram).toUpperCase(), FragmentNoticiasRss.class);
        }

        mTabsAdapter.addTab(getString(R.string.tab_tw).toUpperCase(), FragmentTwitter.class);



        mViewPager.setAdapter(mTabsAdapter);

        ((NoticiasTabsPager) getActivity()).mViewPager = mViewPager;


        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.mi_material_blue_principal));

        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)


}

