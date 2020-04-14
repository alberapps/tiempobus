/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
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
package alberapps.android.tiempobus.infolineas.horariosTram;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContentResolverCompat;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.data.Favorito;
import alberapps.android.tiempobus.data.TiempoBusDb;
import alberapps.android.tiempobus.favoritos.FavoritoNuevoActivity;
import alberapps.android.tiempobus.favoritos.FavoritosActivity;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.tasks.LoadHorariosTramAsyncTask;
import alberapps.android.tiempobus.util.PreferencesUtil;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.tram.horarios.DatoTransbordo;
import alberapps.java.tram.horarios.DatosConsultaHorariosTram;
import alberapps.java.tram.horarios.HorarioTram;
import alberapps.java.util.Utilidades;

/**
 * Fragmento de lineas
 */
public class FragmentHorariosTram extends Fragment {

    InfoLineasTabsPager actividad;

    SharedPreferences preferencias;

    String datosHorario;

    /**
     * On Create
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actividad = (InfoLineasTabsPager) getActivity();

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());


    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        if (actividad.getModoRed() == InfoLineasTabsPager.MODO_RED_TRAM_OFFLINE) {
            iniciar();
        }


        super.onViewStateRestored(savedInstanceState);
    }

    private void iniciar() {

        setupFondoAplicacion();

        //Si viene de favoritos
        Bundle b = actividad.getIntent().getExtras();
        if (b != null && b.containsKey("HORARIOSDATA")) {
            actividad.datosHorariosTram = null;
        }


        // Consultar si es necesario, si ya lo tiene carga la lista
        if (actividad.datosHorariosTram != null && actividad.horariosTramView != null) {
            recargarListado();

        } else if (actividad.datosHorariosTram != null) {

            cargarListado();

        } else {

            ListView lineasVi = (ListView) getActivity().findViewById(R.id.infolinea_lista_horario_tram);
            TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_horario_tram_empty);
            lineasVi.setEmptyView(vacio);

            lineasVi.setOnScrollListener(new ScrollListenerAux());

            actividad.datosHorariosTram = new HorarioTram();
            actividad.datosHorariosTram.setDatosTransbordos(new ArrayList<DatoTransbordo>());
            DatoTransbordo dato = new DatoTransbordo();
            dato.setSinDatos(true);
            actividad.datosHorariosTram.getDatosTransbordos().add(dato);
            cargarListado();

            cargarHeaderHorarios();
        }


        FloatingActionButton imgCircularFavorito = (FloatingActionButton) actividad.findViewById(R.id.boton_circular_fav_h);

        // Para acceder a guardar favorito
        assert imgCircularFavorito != null;
        imgCircularFavorito.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {

                    //Cargar datos
                    StringBuffer guardar = new StringBuffer("");
                    guardar.append(actividad.consultaHorarioTram.getCodEstacionDestino());
                    guardar.append(";;");
                    guardar.append(actividad.consultaHorarioTram.getCodEstacionOrigen());
                    guardar.append(";;");
                    guardar.append(actividad.consultaHorarioTram.getEstacionDestinoSeleccion());
                    guardar.append(";;");
                    guardar.append(actividad.consultaHorarioTram.getEstacionOrigenSeleccion());
                    guardar.append(";;");
                    guardar.append(actividad.consultaHorarioTram.getHoraDesde());
                    guardar.append(";;");
                    guardar.append(actividad.consultaHorarioTram.getHoraHasta());
                    PreferencesUtil.putCache(actividad, "datos_horarios_tram", guardar.toString());
                    datosHorario = guardar.toString();


                    Intent i = new Intent(actividad, FavoritoNuevoActivity.class);

                    Bundle extras = new Bundle();
                    extras.putString("HTRAM", "TRAM");
                    // Preparamos una descripcion automatica para el
                    // favorito


                    String[] estaciones = actividad.getResources().getStringArray(R.array.estaciones_tram);

                    StringBuffer desc = new StringBuffer();
                    desc.append(estaciones[actividad.consultaHorarioTram.getEstacionOrigenSeleccion()]);
                    desc.append("\n");
                    desc.append(estaciones[actividad.consultaHorarioTram.getEstacionDestinoSeleccion()]);
                    desc.append("\n");
                    desc.append(actividad.consultaHorarioTram.getHoraDesde());
                    desc.append("->");
                    desc.append(actividad.consultaHorarioTram.getHoraHasta());
                    desc.append("::");
                    desc.append(datosHorario);

                    extras.putString("DESCRIPCION", desc.toString());

                    i.putExtras(extras);
                    actividad.startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_ADDFAV);

                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(actividad.getApplicationContext(), getString(R.string.aviso_error_datos), Toast.LENGTH_SHORT).show();

                }
            }
        });


        if (actividad.datosHorariosTram == null || actividad.datosHorariosTram.getHorariosItemCombinados() == null || actividad.datosHorariosTram.getHorariosItemCombinados().isEmpty()
                || (!actividad.datosHorariosTram.getHorariosItemCombinados().isEmpty() && actividad.datosHorariosTram.getHorariosItemCombinados().get(0).isSinDatos())) {
            consultarDatos();
        }


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.infolinea_horario_tram, container, false);
    }

    private void cargarHorarios() {

        if (actividad.dialog == null) {
            actividad.dialog = ProgressDialog.show(actividad, "", getString(R.string.dialogo_espera), true);
        } else {
            actividad.dialog.show();
        }

        ConnectivityManager connMgr = (ConnectivityManager) actividad.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            actividad.taskHorariosTram = new LoadHorariosTramAsyncTask(loadHorariosTramAsyncTaskResponder).execute(actividad.consultaHorarioTram, getContext());
        } else {
            Toast.makeText(actividad.getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
            actividad.dialog.dismiss();
        }

    }

    /**
     * Sera llamado cuando la tarea de cargar buses termine
     */
    LoadHorariosTramAsyncTask.LoadHorariosTramAsyncTaskResponder loadHorariosTramAsyncTaskResponder = new LoadHorariosTramAsyncTask.LoadHorariosTramAsyncTaskResponder() {

        @Override
        public void datosHorariosTramLoaded(HorarioTram datos) {
            if (datos != null && datos.getHorariosItemCombinados() != null) {

                try {

                    actividad.datosHorariosTram = datos;

                    cargarListado();

                } catch (Exception e) {

                    e.printStackTrace();

                    try {

                        datos = new HorarioTram();
                        datos.setDatosTransbordos(new ArrayList<DatoTransbordo>());
                        DatoTransbordo datoTransbordo = new DatoTransbordo();
                        datoTransbordo.setErrorServicio(true);
                        datos.getDatosTransbordos().add(datoTransbordo);

                        actividad.datosHorariosTram = datos;

                        cargarListado();

                        //Toast.makeText(actividad.getApplicationContext(), actividad.getString(R.string.error_tiempos), Toast.LENGTH_SHORT).show();

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                }


            } else {

                //Toast.makeText(actividad.getApplicationContext(), getString(R.string.error_tiempos), Toast.LENGTH_SHORT).show();

                datos = new HorarioTram();
                datos.setDatosTransbordos(new ArrayList<DatoTransbordo>());
                DatoTransbordo datoTransbordo = new DatoTransbordo();
                datoTransbordo.setErrorServicio(true);
                datos.getDatosTransbordos().add(datoTransbordo);

                actividad.datosHorariosTram = datos;

                cargarListado();

            }

            if(actividad.dialog.isShowing()) {
                actividad.dialog.dismiss();
            }
        }
    };

    /**
     * Cargar el listado de horarios
     */
    private void cargarListado() {


        try {

            if (actividad.datosHorariosTram != null) {

                actividad.horariosTramAdapter = new HorariosTramAdapter(getActivity(), R.layout.infolineas_horarios_item);


                actividad.horariosTramAdapter.addAll(actividad.datosHorariosTram.getHorariosItemCombinados());


                // Controlar pulsacion
                actividad.horariosTramView = (ListView) getActivity().findViewById(R.id.infolinea_lista_horario_tram);

                if (actividad.horariosTramView != null) {

                    TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_horario_tram_empty);
                    actividad.horariosTramView.setEmptyView(vacio);

                    cargarHeaderHorarios();

                    actividad.horariosTramView.setAdapter(actividad.horariosTramAdapter);

                }

            }

        } catch (Exception e) {
            try {
                Toast.makeText(actividad.getApplicationContext(), actividad.getString(R.string.error_tiempos), Toast.LENGTH_SHORT).show();
            } catch (Exception e2) {
                e2.printStackTrace();
            }

            e.printStackTrace();

        }


    }

    /**
     * Cargar cabecera listado
     */
    public void cargarHeaderHorarios() {

        if (actividad.horariosTramView != null && actividad.horariosTramView.getHeaderViewsCount() == 0) {

            LayoutInflater li2 = LayoutInflater.from(actividad);

            View vheader = li2.inflate(R.layout.infolinea_horarios_tram_header, null);

            // boton consultar
            Button botonConsultar = (Button) vheader.findViewById(R.id.boton_consultar);
            botonConsultar.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    consultarDatos();

                }
            });


            cargarSpinnerFavoritos(vheader);

            // Combo de seleccion de origen
            final Spinner spinnerEstOrigen = (Spinner) vheader.findViewById(R.id.spinner_estacion_origen);
            ArrayAdapter<CharSequence> adapter = null;
            adapter = ArrayAdapter.createFromResource(getActivity(), R.array.estaciones_tram, R.layout.spinner_item_horario);
            adapter.setDropDownViewResource(R.layout.spinner_item_horario_lista);
            spinnerEstOrigen.setAdapter(adapter);


            //Cargar datos iniciales
            if (actividad.consultaHorarioTram == null) {

                Calendar calendar = Calendar.getInstance(UtilidadesUI.getLocaleUsuario());

                actividad.consultaHorarioTram = new DatosConsultaHorariosTram();
                actividad.consultaHorarioTram.setDia(Utilidades.getFechaStringSinHora(calendar.getTime()));
                actividad.consultaHorarioTram.setDiaDate(calendar.getTime());


                String datosAnteriores = null;

                //Verificar si se viene desde favoritos
                Bundle b = actividad.getIntent().getExtras();
                if (b != null && b.containsKey("HORARIOSDATA")) {
                    datosAnteriores = b.getString("HORARIOSDATA");
                    b.remove("HORARIOSDATA");

                } else {

                    //Verificar si hay cache
                    datosAnteriores = PreferencesUtil.getCache(actividad, "datos_horarios_tram");

                }


                if (datosAnteriores != null && !datosAnteriores.equals("")) {

                    String[] datos = datosAnteriores.split(";;");

                    actividad.consultaHorarioTram.setHoraDesde(datos[4]);

                    actividad.consultaHorarioTram.setHoraHasta(datos[5]);

                    actividad.consultaHorarioTram.setCodEstacionOrigen(Integer.parseInt(datos[1]));
                    actividad.consultaHorarioTram.setEstacionOrigenSeleccion(Integer.parseInt(datos[3]));

                    actividad.consultaHorarioTram.setCodEstacionDestino(Integer.parseInt(datos[0]));
                    actividad.consultaHorarioTram.setEstacionDestinoSeleccion(Integer.parseInt(datos[2]));

                } else {

                    actividad.consultaHorarioTram.setHoraDesde(Utilidades.getHoraString(calendar.getTime()));

                    calendar.add(Calendar.HOUR_OF_DAY, 2);
                    actividad.consultaHorarioTram.setHoraHasta(Utilidades.getHoraString(calendar.getTime()));

                    actividad.consultaHorarioTram.setCodEstacionOrigen(UtilidadesTRAM.HORARIOS_COD_ESTACION[0]);
                    actividad.consultaHorarioTram.setEstacionOrigenSeleccion(0);

                    actividad.consultaHorarioTram.setCodEstacionDestino(UtilidadesTRAM.HORARIOS_COD_ESTACION[1]);
                    actividad.consultaHorarioTram.setEstacionDestinoSeleccion(1);
                }

            }


            // Seleccion inicial
            spinnerEstOrigen.setSelection(actividad.consultaHorarioTram.getEstacionOrigenSeleccion());

            // Seleccion
            spinnerEstOrigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                    Integer codigoEstacion = UtilidadesTRAM.HORARIOS_COD_ESTACION[arg2];
                    actividad.consultaHorarioTram.setCodEstacionOrigen(codigoEstacion);
                    actividad.consultaHorarioTram.setEstacionOrigenSeleccion(arg2);

                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }

            });

            // Combo de seleccion de destino
            final Spinner spinnerEstDest = (Spinner) vheader.findViewById(R.id.spinner_estacion_destino);
            ArrayAdapter<CharSequence> adapterEstDest = null;
            adapterEstDest = ArrayAdapter.createFromResource(getActivity(), R.array.estaciones_tram, R.layout.spinner_item_horario);
            adapterEstDest.setDropDownViewResource(R.layout.spinner_item_horario_lista);
            spinnerEstDest.setAdapter(adapterEstDest);

            // Seleccion inicial
            spinnerEstDest.setSelection(actividad.consultaHorarioTram.getEstacionDestinoSeleccion());

            // Seleccion
            spinnerEstDest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                    Integer codigoEstacion = UtilidadesTRAM.HORARIOS_COD_ESTACION[arg2];
                    actividad.consultaHorarioTram.setCodEstacionDestino(codigoEstacion);
                    actividad.consultaHorarioTram.setEstacionDestinoSeleccion(arg2);

                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }

            });


            // boton fecha

            TextView fecha = (TextView) vheader.findViewById(R.id.campo_fecha);

            fecha.setText(actividad.consultaHorarioTram.getDia());

            ImageButton botonFecha = (ImageButton) vheader.findViewById(R.id.boton_fecha);
            botonFecha.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(actividad.getSupportFragmentManager(), "datePicker");

                }
            });

            // boton hora desde
            TextView horaDesde = (TextView) vheader.findViewById(R.id.campo_hora_desde);
            horaDesde.setText(actividad.consultaHorarioTram.getHoraDesde());

            ImageButton botonHoraDesde = (ImageButton) vheader.findViewById(R.id.boton_hora_desde);
            botonHoraDesde.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    DialogFragment newFragment = new TimePickerDesdeFragment();
                    newFragment.show(actividad.getSupportFragmentManager(), "timeDesdePicker");

                }
            });

            // boton hora hasta
            TextView horaHasta = (TextView) vheader.findViewById(R.id.campo_hora_hasta);
            horaHasta.setText(actividad.consultaHorarioTram.getHoraHasta());

            ImageButton botonHoraHasta = (ImageButton) vheader.findViewById(R.id.boton_hora_hasta);
            botonHoraHasta.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    DialogFragment newFragment = new TimePickerHastaFragment();
                    newFragment.show(actividad.getSupportFragmentManager(), "timeHastaPicker");

                }
            });


            actividad.horariosTramView = (ListView) actividad.findViewById(R.id.infolinea_lista_horario_tram);

            actividad.horariosTramView.addHeaderView(vheader);

            cargarFooterHotrarios();

        }


    }


    /**
     * Pie del listado
     */
    public void cargarFooterHotrarios() {

        LayoutInflater li2 = LayoutInflater.from(actividad);

        View vheader = li2.inflate(R.layout.infolinea_horarios_tram_footer, null);

        actividad.horariosTramView.addFooterView(vheader);

    }


    /**
     * Spinner con favoritos de tipo horario
     *
     * @param vheader
     */
    private void cargarSpinnerFavoritos(final View vheader) {

        // Combo de seleccion de favorito
        final Spinner spinnerFavorito = (Spinner) vheader.findViewById(R.id.spinner_favoritos);

        List<Favorito> favoritos = cargarFavoritosBD();
        final List<Favorito> favoritosHorarios = new ArrayList<>();
        List<String> favoritosString = new ArrayList<>();

        favoritosString.add(actividad.getString(R.string.menu_favoritos));

        for (int i = 0; i < favoritos.size(); i++) {

            if (favoritos.get(i).getNumParada().equals("0")) {

                favoritosHorarios.add(favoritos.get(i));
                favoritosString.add(favoritos.get(i).getTitulo());

            }

        }


        ArrayAdapter<String> adapterFavoritos = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_horario, favoritosString);
        adapterFavoritos.setDropDownViewResource(R.layout.spinner_item_horario_lista);
        spinnerFavorito.setAdapter(adapterFavoritos);


        // Seleccion
        spinnerFavorito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {


                //Evitar primer elemento
                if (arg2 > 0) {

                    String[] datos = favoritosHorarios.get(arg2 - 1).getDescripcion().split("::")[1].split(";;");

                    actividad.consultaHorarioTram.setHoraDesde(datos[4]);

                    actividad.consultaHorarioTram.setHoraHasta(datos[5]);

                    actividad.consultaHorarioTram.setCodEstacionOrigen(Integer.parseInt(datos[1]));
                    actividad.consultaHorarioTram.setEstacionOrigenSeleccion(Integer.parseInt(datos[3]));

                    actividad.consultaHorarioTram.setCodEstacionDestino(Integer.parseInt(datos[0]));
                    actividad.consultaHorarioTram.setEstacionDestinoSeleccion(Integer.parseInt(datos[2]));

                    final Spinner spinnerEstOrigen = (Spinner) vheader.findViewById(R.id.spinner_estacion_origen);
                    spinnerEstOrigen.setSelection(actividad.consultaHorarioTram.getEstacionOrigenSeleccion());
                    final Spinner spinnerEstDest = (Spinner) vheader.findViewById(R.id.spinner_estacion_destino);
                    spinnerEstDest.setSelection(actividad.consultaHorarioTram.getEstacionDestinoSeleccion());
                    TextView horaDesde = (TextView) vheader.findViewById(R.id.campo_hora_desde);
                    horaDesde.setText(actividad.consultaHorarioTram.getHoraDesde());
                    TextView horaHasta = (TextView) vheader.findViewById(R.id.campo_hora_hasta);
                    horaHasta.setText(actividad.consultaHorarioTram.getHoraHasta());

                    consultarDatos();


                }


            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });


    }


    /**
     * Iniciar consulta de datos
     */
    private void consultarDatos() {

        cargarHorarios();
        cargarHeaderHorarios();

        //Guardar seleccion
        StringBuffer guardar = new StringBuffer("");
        guardar.append(actividad.consultaHorarioTram.getCodEstacionDestino());
        guardar.append(";;");
        guardar.append(actividad.consultaHorarioTram.getCodEstacionOrigen());
        guardar.append(";;");
        guardar.append(actividad.consultaHorarioTram.getEstacionDestinoSeleccion());
        guardar.append(";;");
        guardar.append(actividad.consultaHorarioTram.getEstacionOrigenSeleccion());
        guardar.append(";;");
        guardar.append(actividad.consultaHorarioTram.getHoraDesde());
        guardar.append(";;");
        guardar.append(actividad.consultaHorarioTram.getHoraHasta());

        PreferencesUtil.putCache(actividad, "datos_horarios_tram", guardar.toString());

        datosHorario = guardar.toString();


    }


    /**
     * Recarga al restaurar la vista
     */
    private void recargarListado() {

        if (actividad.horariosTramAdapter != null) {

            // Controlar pulsacion
            actividad.horariosTramView = (ListView) getActivity().findViewById(R.id.infolinea_lista_horario_tram);

            if (actividad.horariosTramView != null) {

                //actividad.horariosTramView.setOnItemClickListener(lineasClickedHandler);

                TextView vacio = (TextView) getActivity().findViewById(R.id.infolinea_horario_tram_empty);
                actividad.horariosTramView.setEmptyView(vacio);

                cargarHeaderHorarios();

                actividad.horariosTramView.setAdapter(actividad.horariosTramAdapter);

            }

        }

    }


    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = getActivity().findViewById(R.id.contenedor_infolinea_horario_tram);

        UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());

    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Date dia = ((InfoLineasTabsPager) getActivity()).consultaHorarioTram.getDiaDate();
            final Calendar c = Calendar.getInstance();
            c.setTime(dia);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            TextView fecha = (TextView) getActivity().findViewById(R.id.campo_fecha);

            Calendar calendar = Calendar.getInstance(UtilidadesUI.getLocaleUsuario());

            calendar.set(year, month, day);

            fecha.setText(Utilidades.getFechaStringSinHora(calendar.getTime()));

            ((InfoLineasTabsPager) getActivity()).consultaHorarioTram.setDia(Utilidades.getFechaStringSinHora(calendar.getTime()));
            ((InfoLineasTabsPager) getActivity()).consultaHorarioTram.setDiaDate(calendar.getTime());

        }
    }


    public static class TimePickerDesdeFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String horaDesde = ((InfoLineasTabsPager) getActivity()).consultaHorarioTram.getHoraDesde();
            String[] hd = horaDesde.split(":");
            int hour = Integer.parseInt(hd[0]);
            int minute = Integer.parseInt(hd[1]);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            TextView horaDesde = (TextView) getActivity().findViewById(R.id.campo_hora_desde);

            DecimalFormat nf = new DecimalFormat("00");
            String hora = nf.format(hourOfDay) + ":" + nf.format(minute);
            horaDesde.setText(hora);

            ((InfoLineasTabsPager) getActivity()).consultaHorarioTram.setHoraDesde(hora);

        }
    }

    public static class TimePickerHastaFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            String horaHasta = ((InfoLineasTabsPager) getActivity()).consultaHorarioTram.getHoraHasta();
            String[] hd = horaHasta.split(":");
            int hour = Integer.parseInt(hd[0]);
            int minute = Integer.parseInt(hd[1]);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            TextView horaHasta = (TextView) getActivity().findViewById(R.id.campo_hora_hasta);

            DecimalFormat nf = new DecimalFormat("00");
            String hora = nf.format(hourOfDay) + ":" + nf.format(minute);
            horaHasta.setText(hora);
            ((InfoLineasTabsPager) getActivity()).consultaHorarioTram.setHoraHasta(hora);

        }
    }


    /**
     * Listado de favoritos
     *
     * @return
     */
    public List<Favorito> cargarFavoritosBD() {

        List<Favorito> favoritosList = new ArrayList<>();

        Favorito favorito = null;

        try {


            //Cursor cursor = actividad.managedQuery(TiempoBusDb.Favoritos.CONTENT_URI, FavoritosActivity.PROJECTION, null, null, TiempoBusDb.Favoritos.DEFAULT_SORT_ORDER);
            Cursor cursor = ContentResolverCompat.query(getActivity().getContentResolver(), TiempoBusDb.Favoritos.CONTENT_URI, FavoritosActivity.PROJECTION, null, null, TiempoBusDb.Favoritos.DEFAULT_SORT_ORDER, null);

            if (cursor != null) {

                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                    favorito = new Favorito();

                    favorito.setNumParada(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.POSTE)));
                    favorito.setTitulo(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.TITULO)));
                    favorito.setDescripcion(cursor.getString(cursor.getColumnIndex(TiempoBusDb.Favoritos.DESCRIPCION)));
                    favoritosList.add(favorito);

                }

                cursor.close();

            }

            if (!favoritosList.isEmpty()) {

                return favoritosList;

            } else {
                //return null;
            }

        } catch (Exception e) {
            //return null;
        }


        return favoritosList;

    }

    public class ScrollListenerAux implements AbsListView.OnScrollListener {


        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

            if (SCROLL_STATE_TOUCH_SCROLL == i) {
                View cFocus = actividad.getCurrentFocus();
                if (cFocus != null) {
                    cFocus.clearFocus();
                }
            }

        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {

        }
    }

}
