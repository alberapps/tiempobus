/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 * 
 *  based on code by The Android Open Source Project
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
package alberapps.android.tiempobus.infolineas;

import java.util.ArrayList;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.actionbar.ActionBarActivityFragments;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.webservice.estructura.GetLineasResult;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI that
 * switches between tabs and also allows the user to perform horizontal flicks
 * to move between the tabs.
 */
public class InfoLineasTabsPager extends ActionBarActivityFragments {
	TabHost mTabHost;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;

	BusLinea linea = null;

	String sentidoIda = "";
	String sentidoVuelta = "";

	GetLineasResult lineasMapas = null;

	DatosMapa datosIda = null;
	DatosMapa datosVuelta = null;

	boolean modoOffline = false;

	SharedPreferences preferencias = null;

	public BusLinea getLinea() {
		return linea;
	}

	public void setLinea(BusLinea linea) {
		this.linea = linea;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Control de modo offline
		modoOffline = this.getIntent().getBooleanExtra("MODO_OFFLINE", false);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		setContentView(R.layout.fragment_tabs_pager);

		// Fondo
		// setupFondoAplicacion();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mViewPager = (ViewPager) findViewById(R.id.pager);

		mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

		mTabsAdapter.addTab(mTabHost.newTabSpec("lineas").setIndicator(getString(R.string.linea)), FragmentLineas.class, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("ida").setIndicator(getString(R.string.ida)), FragmentIda.class, null);
		mTabsAdapter.addTab(mTabHost.newTabSpec("vuelta").setIndicator(getString(R.string.vuelta)), FragmentVuelta.class, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	public void cambiarTab() {

		mTabHost.setCurrentTabByTag("ida");

	}

	public void seleccionarParadaIda(int posicion) {

		Intent i = new Intent(this, InfoLineasDatosParadaActivity.class);
		i.putExtra("DATOS_PARADA", datosIda.getPlacemarks().get(posicion));
		i.putExtra("DATOS_LINEA", linea);
		startActivity(i);

	}

	public void seleccionarParadaVuelta(int posicion) {

		Intent i = new Intent(this, InfoLineasDatosParadaActivity.class);
		i.putExtra("DATOS_PARADA", datosVuelta.getPlacemarks().get(posicion));
		i.putExtra("DATOS_LINEA", linea);
		startActivity(i);

	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public static class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mTabHost = tabHost;
			mViewPager = pager;
			mTabHost.setOnTabChangedListener(this);
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mContext));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);
			mTabs.add(info);
			mTabHost.addTab(tabSpec);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			return Fragment.instantiate(mContext, info.clss.getName(), info.args);
		}

		public void onTabChanged(String tabId) {
			int position = mTabHost.getCurrentTab();
			mViewPager.setCurrentItem(position);
		}

		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		public void onPageSelected(int position) {
			// Unfortunately when TabHost changes the current tab, it kindly
			// also takes care of putting focus on it when not in touch mode.
			// The jerk.
			// This hack tries to prevent this from pulling focus out of our
			// ViewPager.
			TabWidget widget = mTabHost.getTabWidget();
			int oldFocusability = widget.getDescendantFocusability();
			widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
			mTabHost.setCurrentTab(position);
			widget.setDescendantFocusability(oldFocusability);
		}

		public void onPageScrollStateChanged(int state) {
		}
	}

	/**
	 * Seleccion del fondo de la galeria en el arranque
	 */
	private void setupFondoAplicacion() {

		String fondo_galeria = preferencias.getString("image_galeria", "");

		View contenedor_principal = findViewById(R.id.contenedor_tabs);

		UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.infolineas, menu);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			searchView.setIconifiedByDefault(false);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		case R.id.menu_search:

			onSearchRequested();

			break;

		case R.id.menu_search_online:

			Intent intent2 = getIntent();
			intent2.putExtra("MODO_OFFLINE", false);
			finish();
			startActivity(intent2);

			break;
		case R.id.menu_search_offline:

			Intent intent3 = getIntent();
			intent3.putExtra("MODO_OFFLINE", true);
			finish();
			startActivity(intent3);

			break;

		}

		return super.onOptionsItemSelected(item);

	}

	public boolean isModoOffline() {
		return modoOffline;
	}

	public void setModoOffline(boolean modoOffline) {
		this.modoOffline = modoOffline;
	}

	

}
