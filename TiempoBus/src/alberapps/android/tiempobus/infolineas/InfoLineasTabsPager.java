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
import alberapps.android.tiempobus.tasks.LoadHorariosInfoLineasAsyncTask;
import alberapps.android.tiempobus.tasks.LoadHorariosInfoLineasAsyncTask.LoadHorariosInfoLineasAsyncTaskResponder;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.horarios.DatosHorarios;
import alberapps.java.horarios.ProcesarHorarios;
import alberapps.java.tam.BusLinea;
import alberapps.java.tam.mapas.DatosMapa;
import alberapps.java.tam.mapas.PlaceMark;
import alberapps.java.tam.webservice.estructura.GetLineasResult;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

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

	DatosHorarios datosHorarios = null;

	ProgressDialog dialog = null;

	View vistaPieHorarioIda = null;
	View vistaPieHorarioVuelta = null;
	View vistaPieAviso = null;

	// Red a usar 0(subus online) 1(subus local) 2(tram)
	public static int MODO_RED_SUBUS_ONLINE = 0;
	public static int MODO_RED_SUBUS_OFFLINE = 1;
	public static int MODO_RED_TRAM_OFFLINE = 2;

	int modoRed = MODO_RED_SUBUS_ONLINE;

	SharedPreferences preferencias = null;

	AsyncTask<Object, Void, DatosHorarios> taskHorarios = null;
	AsyncTask<String, Void, ArrayList<BusLinea>> taskBuses = null;
	AsyncTask<DatosInfoLinea, Void, DatosInfoLinea> taskDatosLinea = null;
	AsyncTask<DatosInfoLinea, Void, DatosInfoLinea> taskInfoLineaIda = null;
	
	public BusLinea getLinea() {
		return linea;
	}

	public void setLinea(BusLinea linea) {
		this.linea = linea;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		// Control de modo de red
		modoRed = this.getIntent().getIntExtra("MODO_RED", 0);

		if (this.getIntent().getExtras() == null || (this.getIntent().getExtras() != null && !this.getIntent().getExtras().containsKey("MODO_RED"))) {

			modoRed = preferencias.getInt("infolinea_modo", 0);

		}

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

		if (modoRed == MODO_RED_SUBUS_ONLINE || modoRed == MODO_RED_SUBUS_OFFLINE) {
			mTabsAdapter.addTab(mTabHost.newTabSpec("lineas").setIndicator(getString(R.string.linea)), FragmentLineas.class, null);
			mTabsAdapter.addTab(mTabHost.newTabSpec("ida").setIndicator(getString(R.string.ida)), FragmentIda.class, null);
			mTabsAdapter.addTab(mTabHost.newTabSpec("vuelta").setIndicator(getString(R.string.vuelta)), FragmentVuelta.class, null);
		} else if (modoRed == MODO_RED_TRAM_OFFLINE) {
			mTabsAdapter.addTab(mTabHost.newTabSpec("lineas").setIndicator(getString(R.string.linea)), FragmentLineas.class, null);
			mTabsAdapter.addTab(mTabHost.newTabSpec("ida").setIndicator(getString(R.string.parada_tram)), FragmentIda.class, null);

		}

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

	}
	
	@Override
	protected void onDestroy() {
		
		detenerTareas();
		
		
		super.onDestroy();
	}
	
	public void detenerTareas() {

		if (taskHorarios != null && taskHorarios.getStatus() == Status.RUNNING) {

			taskHorarios.cancel(true);

			Log.d("INFOLINEA", "Cancelada task horarios");

		}
		
		if (taskBuses != null && taskBuses.getStatus() == Status.RUNNING) {

			taskBuses.cancel(true);

			Log.d("INFOLINEA", "Cancelada task buses");

		}
		
		if (taskDatosLinea != null && taskDatosLinea.getStatus() == Status.RUNNING) {

			taskDatosLinea.cancel(true);

			Log.d("INFOLINEA", "Cancelada task datos linea");

		}
		
		if (taskInfoLineaIda != null && taskInfoLineaIda.getStatus() == Status.RUNNING) {

			taskInfoLineaIda.cancel(true);

			Log.d("INFOLINEA", "Cancelada task linea ida");

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

	public void cargarTiempos(int codigo) {

		Intent intent = new Intent(this, MainActivity.class);
		Bundle b = new Bundle();
		b.putInt("poste", codigo);
		intent.putExtras(b);

		SharedPreferences.Editor editor = preferencias.edit();
		editor.putInt("parada_inicio", codigo);
		editor.commit();

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		startActivity(intent);

	}

	public void irInformacion(PlaceMark datosParada) {

		Intent i = new Intent(this, InfoLineasDatosParadaActivity.class);
		i.putExtra("DATOS_PARADA", datosParada);
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

		}

		return super.onOptionsItemSelected(item);

	}

	public int getModoRed() {
		return modoRed;
	}

	public void setModoRed(int modoRed) {
		this.modoRed = modoRed;
	}

	/* HORARIOS */

	public void cargarHorarios(BusLinea linea, int index) {

		// We can display everything in-place with fragments, so update
		// the list to highlight the selected item and show the data.
		ListView lineasView = (ListView) findViewById(R.id.infolinea_lista_lineas);

		lineasView.setItemChecked(index, true);

		lineasMapas = null;
		sentidoIda = null;
		sentidoVuelta = null;
		datosHorarios = null;

		dialog = ProgressDialog.show(this, "", getString(R.string.dialogo_espera), true);

		loadHorarios(linea);

		// Control para el nuevo modo offline
		/*
		 * if (actividad.getModoRed() ==
		 * InfoLineasTabsPager.MODO_RED_SUBUS_ONLINE) {
		 * 
		 * loadDatosMapa(); } else if (actividad.getModoRed() ==
		 * InfoLineasTabsPager.MODO_RED_SUBUS_OFFLINE) {
		 * 
		 * loadDatosMapaOffline(); } else if (actividad.getModoRed() ==
		 * InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
		 * 
		 * loadDatosMapaTRAMOffline(); }
		 */

	}

	/**
	 * Carga los horarios
	 */
	private void loadHorarios(BusLinea datosLinea) {

		// Control de disponibilidad de conexion
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			taskHorarios = new LoadHorariosInfoLineasAsyncTask(loadHorariosInfoLineasAsyncTaskResponder).execute(datosLinea);
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

	/**
	 * Se llama cuando las paradas hayan sido cargadas
	 */
	LoadHorariosInfoLineasAsyncTaskResponder loadHorariosInfoLineasAsyncTaskResponder = new LoadHorariosInfoLineasAsyncTaskResponder() {
		public void datosHorariosInfoLineasLoaded(DatosHorarios datos) {

			if (datos != null) {

				datosHorarios = datos;

				cargarListadoHorarioIda();

				cambiarTab();

			} else {

				datosHorarios = null;

				Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.aviso_error_datos) + " www.subus.es", Toast.LENGTH_SHORT);
				toast.show();
				dialog.dismiss();

			}

			dialog.dismiss();

		}
	};

	/**
	 * Carga lista con los horarios de ida
	 */
	public void cargarListadoHorarioIda() {

		TextView titIda = (TextView) findViewById(R.id.tituloIda);

		titIda.setText(datosHorarios.getTituloSalidaIda());

		InfoLineaHorariosAdapter infoLineaHorariosAdapter = new InfoLineaHorariosAdapter(this, R.layout.infolineas_horarios_item);

		infoLineaHorariosAdapter.addAll(datosHorarios.getHorariosIda());

		ListView idaView = (ListView) findViewById(R.id.infolinea_lista_ida);
		// idaView.setOnItemClickListener(idaClickedHandler);

		if (idaView.getFooterViewsCount() == 0) {

			LayoutInflater li = LayoutInflater.from(this);

			vistaPieHorarioIda = li.inflate(R.layout.infolineas_horarios_item, null);

			TextView descHorario = (TextView) vistaPieHorarioIda.findViewById(R.id.desc_horario);

			descHorario.setText(getString(R.string.observaciones));

			idaView.addFooterView(vistaPieHorarioIda);

			// Pie aviso
			LayoutInflater li2 = LayoutInflater.from(this);
			vistaPieAviso = li2.inflate(R.layout.infolineas_horarios_item, null);
			TextView descHorario2 = (TextView) vistaPieAviso.findViewById(R.id.desc_horario);
			TextView datosHorario2 = (TextView) vistaPieAviso.findViewById(R.id.datos_horario);
			descHorario2.setText(getString(R.string.aviso_noticia));
			datosHorario2.setAutoLinkMask(Linkify.ALL);
			datosHorario2.setLinksClickable(true);
			datosHorario2.setText(ProcesarHorarios.URL_SUBUS + datosHorarios.getHorariosIda().get(0).getLinkHorario());

			idaView.addFooterView(vistaPieAviso);

		}

		TextView datosHorario = (TextView) vistaPieHorarioIda.findViewById(R.id.datos_horario);

		StringBuffer comentarios = new StringBuffer("");

		if (datosHorarios.getComentariosIda() != null && !datosHorarios.getComentariosIda().equals("")) {
			comentarios.append(datosHorarios.getComentariosIda());
			comentarios.append("\n");
		}

		if (datosHorarios.getValidezHorarios() != null) {
			comentarios.append(datosHorarios.getValidezHorarios());
		}

		datosHorario.setText(comentarios.toString());

		idaView.setAdapter(infoLineaHorariosAdapter);

		infoLineaHorariosAdapter.notifyDataSetChanged();

	}

	/**
	 * Carga lista con los horarios de vuelta
	 */
	public void cargarListadoHorarioVuelta() {

		InfoLineaHorariosAdapter infoLineaHorariosAdapter = new InfoLineaHorariosAdapter(this, R.layout.infolineas_horarios_item);

		infoLineaHorariosAdapter.addAll(datosHorarios.getHorariosVuelta());

		ListView vueltaView = (ListView) findViewById(R.id.infolinea_lista_vuelta);
		// idaView.setOnItemClickListener(idaClickedHandler);

		if (vueltaView.getFooterViewsCount() == 0) {

			LayoutInflater li = LayoutInflater.from(this);
			vistaPieHorarioVuelta = li.inflate(R.layout.infolineas_horarios_item, null);

			TextView descHorario = (TextView) vistaPieHorarioVuelta.findViewById(R.id.desc_horario);

			descHorario.setText(getString(R.string.observaciones));

			vueltaView.addFooterView(vistaPieHorarioVuelta);

			// Pie aviso
			LayoutInflater li2 = LayoutInflater.from(this);
			vistaPieAviso = li2.inflate(R.layout.infolineas_horarios_item, null);
			TextView descHorario2 = (TextView) vistaPieAviso.findViewById(R.id.desc_horario);
			TextView datosHorario2 = (TextView) vistaPieAviso.findViewById(R.id.datos_horario);
			descHorario2.setText(getString(R.string.aviso_noticia));
			datosHorario2.setAutoLinkMask(Linkify.ALL);
			datosHorario2.setLinksClickable(true);
			datosHorario2.setText(ProcesarHorarios.URL_SUBUS + datosHorarios.getHorariosIda().get(0).getLinkHorario());

			vueltaView.addFooterView(vistaPieAviso);

		}

		TextView datosHorario = (TextView) vistaPieHorarioVuelta.findViewById(R.id.datos_horario);

		StringBuffer comentarios = new StringBuffer("");

		if (datosHorarios.getComentariosVuelta() != null && !datosHorarios.getComentariosVuelta().equals("")) {
			comentarios.append(datosHorarios.getComentariosVuelta());
			comentarios.append("\n");
		}

		if (datosHorarios.getValidezHorarios() != null) {
			comentarios.append(datosHorarios.getValidezHorarios());
		}

		datosHorario.setText(comentarios);

		vueltaView.setAdapter(infoLineaHorariosAdapter);

		infoLineaHorariosAdapter.notifyDataSetChanged();

	}

	/**
	 * Eliminar datos horarios
	 */
	public void limpiarHorariosIda() {

		datosHorarios = null;

		ListView idaView = (ListView) findViewById(R.id.infolinea_lista_ida);

		if (idaView.getFooterViewsCount() > 0) {
			idaView.removeFooterView(vistaPieHorarioIda);
			vistaPieHorarioIda = null;
		}

	}

	/**
	 * Eliminar datos horarios
	 */
	public void limpiarHorariosVuelta() {

		datosHorarios = null;

		ListView vueltaView = (ListView) findViewById(R.id.infolinea_lista_vuelta);

		if (vueltaView.getFooterViewsCount() > 0) {
			vueltaView.removeFooterView(vistaPieHorarioVuelta);
			vueltaView.removeFooterView(vistaPieAviso);
			vistaPieHorarioVuelta = null;
		}

	}

}
