package alberapps.android.tiempobus.principal.horariotram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContentResolverCompat;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.historial.HistorialDB;
import alberapps.android.tiempobus.historial.HistorialActivity;
import alberapps.android.tiempobus.infolineas.InfoLineaParadasAdapter;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.mapas.SpinnerItem;
import alberapps.android.tiempobus.principal.DatosPantallaPrincipal;
import alberapps.android.tiempobus.tasks.LoadHorariosTramAsyncTask;
import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tram.ProcesarTiemposTramPorHorarios;
import alberapps.java.tram.UtilidadesTRAM;
import alberapps.java.tram.horarios.DatosConsultaHorariosTram;
import alberapps.java.tram.horarios.HorarioItem;
import alberapps.java.tram.horarios.HorarioTram;
import alberapps.java.util.Utilidades;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrincipalHorarioTramFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrincipalHorarioTramFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrincipalHorarioTramFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private AsyncTask<Object, Void, HorarioTram> taskHorariosTram = null;

    private HorarioTram horarioTramActual;
    private Integer destinoActual;
    private String textoDestinoActual;

    private List<String> actualHoras;
    private DatosConsultaHorariosTram datosConsulta;

    private boolean cambioDestino = false;
    private boolean recargaExterna = true;

    private SharedPreferences preferencias = null;


    public PrincipalHorarioTramFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrincipalHorarioTramFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrincipalHorarioTramFragment newInstance(String param1, String param2) {
        PrincipalHorarioTramFragment fragment = new PrincipalHorarioTramFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, false);
        preferencias = PreferenceManager.getDefaultSharedPreferences(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_principal_horario_tram, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Para evitar limitacion en layouts


        setupFondoAplicacion();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {

            ImageButton botonHorarios = (ImageButton) view.findViewById(R.id.tarjeta_horario_ir);

            final MainActivity context = (MainActivity) getActivity();

            botonHorarios.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    context.detenerTodasTareas();

                    Intent i = new Intent(context, InfoLineasTabsPager.class);
                    i.putExtra("HORARIOS", "TRAM");

                    StringBuilder dato = new StringBuilder(150);
                    dato.append(datosConsulta.getCodEstacionDestino());
                    dato.append(";;");
                    dato.append(datosConsulta.getCodEstacionOrigen());
                    dato.append(";;");
                    dato.append(datosConsulta.getEstacionDestinoSeleccion());
                    dato.append(";;");
                    dato.append(datosConsulta.getEstacionOrigenSeleccion());
                    dato.append(";;");
                    dato.append(datosConsulta.getHoraDesde());
                    dato.append(";;");
                    dato.append(datosConsulta.getHoraHasta());

                    i.putExtra("HORARIOSDATA", dato.toString());

                    context.startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                }
            });


            ImageButton botonCompartir = (ImageButton) view.findViewById(R.id.tarjeta_horario_compartir);


            botonCompartir.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    TextView datosHoras = (TextView) getView().findViewById(R.id.datos_horas);
                    TextView datosInfo = (TextView) getView().findViewById(R.id.datos_info);

                    if (!datosInfo.getText().equals("")) {
                        context.datosPantallaPrincipal.shareHorario(datosHoras.getText().toString(), datosInfo.getText().toString());
                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.main_no_items), Toast.LENGTH_LONG).show();
                    }

                }
            });


            // Botones
            ImageView alertaText = (ImageView) view.findViewById(R.id.tiempos_alerta_img);

            alertaText.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    TextView datosHoras = (TextView) getView().findViewById(R.id.datos_horas);

                    BusLlegada bus = new BusLlegada();
                    bus.setErrorServicio(false);
                    bus.setSinDatos(false);
                    bus.setTiempoReal(false);


                    bus.setLinea("TRAM");

                    bus.setProximo("sinestimacion;sinestimacion");

                    boolean ok = calcularTiempoPorHoras(datosHoras.getText().toString(), bus);

                    if (ok) {
                        if (bus != null) {

                            try {
                                // Texto para receiver
                                String textoReceiver = context.gestionarAlarmas.prepararReceiver(bus, context.paradaActual);

                                // Activar alarma y mostrar modal
                                context.gestionarAlarmas.mostrarModalTiemposAlerta(bus, context.paradaActual, textoReceiver);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context.getApplicationContext(), context.getApplicationContext().getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context.getApplicationContext(), context.getApplicationContext().getString(R.string.alarma_auto_error), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(), getContext().getString(R.string.err_bus_sin), Toast.LENGTH_LONG).show();
                    }

                }

            });

            boolean opcionTR = preferencias.getBoolean("tram_opcion_tr", false);

            //Botones ida y vuelta
            //final SwitchCompat botonTR = (SwitchCompat) view.findViewById(R.id.switchTiempoReal);


            if (opcionTR) {
                //botonTR.setChecked(true);
                SharedPreferences.Editor editor = preferencias.edit();
                editor.putBoolean("tram_opcion_tr", false);
                editor.apply();

            }

            /*botonTR.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    SharedPreferences.Editor editor = preferencias.edit();
                    editor.putBoolean("tram_opcion_tr", botonTR.isChecked());
                    editor.apply();

                    recargaExterna = false;
                    cargaInicial();

                }
            });*/


            cargaInicial();


        /*BusLlegada bus = new BusLlegada();
        bus.setLinea();
        context.datosPantallaPrincipal.cantarLinea(bus);
        */

        } else {

            AppCompatTextView botonHorarios = (AppCompatTextView) view.findViewById(R.id.tarjeta_horario_ir);

            final MainActivity context = (MainActivity) getActivity();

            botonHorarios.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View arg0) {

                    context.detenerTodasTareas();

                    Intent i = new Intent(context, InfoLineasTabsPager.class);
                    i.putExtra("HORARIOS", "TRAM");


                    context.startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                }
            });

        }
    }


    private boolean calcularTiempoPorHoras(String horasList, BusLlegada ida) {


        if (horasList != null && !horasList.equals("")) {

            Date hoy = new Date();

            String[] horas = horasList.split(" ");

            //Calcular el tiempo a partir de la hora
            String hora1 = horas[0].trim();
            String hora2 = "";
            Date hora1Fecha = Utilidades.getFechaActualConHora(hora1);

            if (hora1Fecha == null) {
                return false;
            }

            Date hora2Fecha = null;
            String minutosTren2 = "";

            String minutosTren1 = Utilidades.getMinutosDiferencia(hoy, hora1Fecha);

            ida.cambiarProximo(Integer.parseInt(minutosTren1) + 1);

            if (horas.length > 1) {

                hora2 = horas[1];
                hora2Fecha = Utilidades.getFechaActualConHora(hora2);
                minutosTren2 = Utilidades.getMinutosDiferencia(hoy, hora2Fecha);

                ida.cambiarSiguiente(Integer.parseInt(minutosTren2) + 1);

            }

            return true;

        } else {
            return false;
        }


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onCambioDestino(Integer destino, String textoDestino) {
        if (mListener != null && !recargaExterna) {
            cambioDestino = true;
            mListener.onFragmentInteraction(destino, textoDestino);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Integer destino, String textoDestino);
    }

    public void recargarHorarios() {

        recargaExterna = true;

        //cargarHorarios(destinoActual);

        //Desactivado para recargar primero horarios
        if (!cambioDestino) {
            cargaInicial();
        } else {
            cambioDestino = false;
        }

    }


    private DatosConsultaHorariosTram getDatosConsulta(int destino) {

        Date hoy = new Date();

        //Codigo teniendo en cuenta si es de la L2
        int codEstacionOrigen = UtilidadesTRAM.getParadaParaHorarios(((MainActivity) getActivity()).paradaActual);

        DatosConsultaHorariosTram datosConsulta = new DatosConsultaHorariosTram();
        datosConsulta.setCodEstacionOrigen(codEstacionOrigen);
        datosConsulta.setCodEstacionDestino(destino);
        datosConsulta.setDiaDate(hoy);

        datosConsulta.setHoraDesde(Utilidades.getHoraString(hoy));
        Calendar calendar = Calendar.getInstance(UtilidadesUI.getLocaleUsuario());
        int day1 = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        int day2 = calendar.get(Calendar.DAY_OF_MONTH);
        //Control cambio de dia
        if (day1 == day2) {
            datosConsulta.setHoraHasta(Utilidades.getHoraString(calendar.getTime()));
        } else {
            datosConsulta.setHoraHasta("23:59");
        }

        //Origen
        List datosList = new ArrayList(Arrays.asList(UtilidadesTRAM.HORARIOS_COD_ESTACION));
        int select = datosList.lastIndexOf(codEstacionOrigen);
        datosConsulta.setEstacionOrigenSeleccion(select);

        return datosConsulta;

    }


    private void cargarHorarios(int destino, int seleccionado) {

        cargarDatos(null, true);

        datosConsulta = getDatosConsulta(destino);
        datosConsulta.setEstacionDestinoSeleccion(seleccionado);


        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            AsyncTask<Object, Void, HorarioTram> taskHorariosTram = new LoadHorariosTramAsyncTask(loadHorariosTramAsyncTaskResponder).execute(datosConsulta, getContext());
        } else {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
        }

    }

    private void cargarDatos(HorarioTram datos, boolean recarga) {

        HorarioItem horas = null;

        try {

            View horariosPaso2 = getView().findViewById(R.id.horarios_paso2);
            View horariosPaso3 = getView().findViewById(R.id.horarios_paso3);
            View datosLineaC = getView().findViewById(R.id.datos_linea_c);
            View datosLineasPosibles = getView().findViewById(R.id.datos_lineas_posibles);

            if (getView() != null) {
                TextView datosHoras = (TextView) getView().findViewById(R.id.datos_horas);
                TextView datosInfo = (TextView) getView().findViewById(R.id.datos_info);

                TextView datosInfoDestinos = (TextView) getView().findViewById(R.id.datos_info_destinos);


                if (recarga) {
                    datosHoras.setText(getString(R.string.aviso_recarga));
                    datosInfo.setText("");
                    datosInfoDestinos.setText("");
                    horariosPaso2.setVisibility(View.GONE);
                    horariosPaso3.setVisibility(View.GONE);
                    datosLineasPosibles.setVisibility(View.GONE);
                    return;
                }

                if (datos != null && datos.getHorariosItemCombinados() != null && !datos.getHorariosItemCombinados().isEmpty() && datos.getHorariosItemCombinados().size() > 1) {

                    actualHoras = getActualHoras(datos, 0);
                    horas = datos.getHorariosItemCombinados(0).get(1);

                    String stringHoras = getStringHoras(actualHoras);
                    datosHoras.setText(stringHoras);
                    datosInfo.setText(horas.getDatoInfo().replace("Paso ", ""));

                    String aux = datos.getDatosTransbordos().get(0).getTrenesDestino();
                    String[] auxList = null;
                    String aux2 = "";
                    if(aux != null){
                        auxList = aux.split(":");
                    }

                    if(auxList.length > 0){
                        aux2 = auxList[1].trim();
                    }else {
                        aux2 = aux.trim();
                    }

                    datosInfoDestinos.setText(aux2);

                    if (datos.getHorariosItemCombinados(1) != null && datos.getHorariosItemCombinados(1).size() > 1) {

                        //datosLineaC.setVisibility(View.VISIBLE);

                        //TextView datosLinea = (TextView) getView().findViewById(R.id.datos_linea);
                        //datosLinea.setText(datos.getHorariosItemCombinados(0).get(1).getLinea());
                        //Formato colores
                        //DatosPantallaPrincipal.formatoLinea(getActivity(), datosLinea, datos.getHorariosItemCombinados(0).get(1).getLinea(), false);

                        TextView datosLinea2 = (TextView) getView().findViewById(R.id.datos_linea2);
                        datosLinea2.setText(datos.getHorariosItemCombinados(1).get(1).getLinea());
                        //Formato colores
                        DatosPantallaPrincipal.formatoLinea(getActivity(), datosLinea2, datos.getHorariosItemCombinados(1).get(1).getLinea(), false);

                        List<String> actualHorasPaso2 = getActualHoras(datos, 1);
                        HorarioItem horasPaso2 = datos.getHorariosItemCombinados(1).get(1);

                        String stringHorasPaso2 = getStringHoras(actualHorasPaso2);

                        TextView datosHoras2 = (TextView) getView().findViewById(R.id.datos_horas_2);
                        TextView datosInfo2 = (TextView) getView().findViewById(R.id.datos_info_2);
                        TextView datosInfoDestinos2 = (TextView) getView().findViewById(R.id.datos_info_destinos_2);
                        datosHoras2.setText(stringHorasPaso2);
                        datosInfo2.setText(horasPaso2.getDatoInfo().replace("Paso ", ""));

                        aux = datos.getDatosTransbordos().get(1).getTrenesDestino();
                        auxList = null;
                        aux2 = "";
                        if(aux != null){
                            auxList = aux.split(":");
                        }

                        if(auxList.length > 0){
                            aux2 = auxList[1].trim();
                        }else {
                            aux2 = aux.trim();
                        }

                        datosInfoDestinos2.setText(aux2);


                        horariosPaso2.setVisibility(View.VISIBLE);

                        if (datos.getHorariosItemCombinados(2) != null && datos.getHorariosItemCombinados(2).size() > 1) {

                            TextView datosLinea3 = (TextView) getView().findViewById(R.id.datos_linea3);
                            datosLinea3.setText(datos.getHorariosItemCombinados(2).get(1).getLinea());
                            //Formato colores
                            DatosPantallaPrincipal.formatoLinea(getActivity(), datosLinea3, datos.getHorariosItemCombinados(2).get(1).getLinea(), false);

                            List<String> actualHorasPaso3 = getActualHoras(datos, 2);
                            HorarioItem horasPaso3 = datos.getHorariosItemCombinados(2).get(1);

                            String stringHorasPaso3 = getStringHoras(actualHorasPaso3);

                            TextView datosHoras3 = (TextView) getView().findViewById(R.id.datos_horas_3);
                            TextView datosInfo3 = (TextView) getView().findViewById(R.id.datos_info_3);
                            TextView datosInfoDestinos3 = (TextView) getView().findViewById(R.id.datos_info_destinos_3);
                            datosHoras3.setText(stringHorasPaso3);
                            datosInfo3.setText(horasPaso3.getDatoInfo().replace("Paso ", ""));


                            aux = datos.getDatosTransbordos().get(2).getTrenesDestino();
                            auxList = null;
                            aux2 = "";
                            if(aux != null){
                                auxList = aux.split(":");
                            }

                            if(auxList.length > 0){
                                aux2 = auxList[1].trim();
                            }else {
                                aux2 = aux.trim();
                            }

                            datosInfoDestinos3.setText(aux2);

                            horariosPaso3.setVisibility(View.VISIBLE);

                        } else {
                            horariosPaso3.setVisibility(View.GONE);
                        }


                    } else {
                        horariosPaso2.setVisibility(View.GONE);
                        datosLineaC.setVisibility(View.GONE);

                        //String lineas = ProcesarTiemposTramPorHorarios.getLineasPorNombreDestino(datos, ((MainActivity) getActivity()).paradaActual);

                        //Lineas posibles
                        /*if (!lineas.equals("") && lineas.contains(",")) {
                            datosLineasPosibles.setVisibility(View.VISIBLE);
                            InfoLineaParadasAdapter.mostrarLineasParada(getContext(), datosLineasPosibles, lineas);
                        } else if (!lineas.equals("")) {
                            datosLineaC.setVisibility(View.VISIBLE);
                            TextView datosLinea = (TextView) getView().findViewById(R.id.datos_linea);
                            datosLinea.setText(lineas);
                            //Formato colores
                            DatosPantallaPrincipal.formatoLinea(getActivity(), datosLinea, lineas, false);
                        }*/

                    }

                } else {

                    datosHoras.setText(getString(R.string.main_no_items));
                    datosInfo.setText("");

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private List<String> getActualHoras(HorarioTram datos, int paso) {


        List<String> actualHorasList = new ArrayList<>();

        HorarioItem horas = datos.getHorariosItemCombinados(paso).get(1);

        for (int i = 1; i < datos.getHorariosItemCombinados(paso).size(); i++) {

            if (datos.getHorariosItemCombinados(paso).get(i).getDatoInfo().equals(horas.getDatoInfo())) {

                actualHorasList.addAll(Arrays.asList(datos.getHorariosItemCombinados(paso).get(i).getHoras().split(" ")));

                if (actualHorasList.size() > 5) {
                    actualHorasList = actualHorasList.subList(0, 5);
                    break;
                }

            } else {
                break;
            }
        }

        return actualHorasList;

    }

    private String getStringHoras(List<String> actualHorasList) {

        String stringHoras = "";

        for (int j = 0; j < actualHorasList.size(); j++) {

            if (stringHoras.length() > 0) {
                stringHoras = stringHoras + " ";
            }

            stringHoras = stringHoras + actualHorasList.get(j);
        }

        return stringHoras;

    }

    /**
     * Sera llamado cuando la tarea de cargar buses termine
     */
    LoadHorariosTramAsyncTask.LoadHorariosTramAsyncTaskResponder loadHorariosTramAsyncTaskResponder = new LoadHorariosTramAsyncTask.LoadHorariosTramAsyncTaskResponder() {

        @Override
        public void datosHorariosTramLoaded(HorarioTram datos) {
            if (datos != null) {

                horarioTramActual = datos;
                cargarDatos(datos, false);

            } else {

                horarioTramActual = null;
                cargarDatos(null, false);

            }


        }
    };


    private boolean filtroTramRecargar = true;

    private void cargaInicial() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {

            // Combo de seleccion de destino
            LinkedList<SpinnerItem> listaSpinner = new LinkedList<>();
            String[] a1 = getResources().getStringArray(R.array.estaciones_tram);
            for (int i = 0; i < a1.length; i++) {
                listaSpinner.add(new SpinnerItem(i, a1[i]));
            }
            final ArrayAdapter<SpinnerItem> adapterEstDest = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_horario_principal, listaSpinner);

            final Spinner spinnerEstDest = (Spinner) getView().findViewById(R.id.spinner_estacion_destino);
            //final ArrayAdapter<SpinnerItem> adapterEstDest = ArrayAdapter.createFromResource(getActivity(), R.array.estaciones_tram, R.layout.spinner_item_horario);
            adapterEstDest.setDropDownViewResource(R.layout.spinner_item_horario_lista);
            spinnerEstDest.setAdapter(adapterEstDest);

            //Cargar si esta disponible, el ultimo destino seleccionado para esta parada
            String paradaHist = cargarHorarioParadaHistorial(((MainActivity) getActivity()).paradaActual);
            int destino = UtilidadesTRAM.HORARIOS_COD_ESTACION[spinnerEstDest.getSelectedItemPosition()];

            if (paradaHist != null && !paradaHist.equals("")) {
                destino = Integer.parseInt(paradaHist);

                // Seleccion inicial

                List datosList = new ArrayList(Arrays.asList(UtilidadesTRAM.HORARIOS_COD_ESTACION));
                int select = datosList.lastIndexOf(destino);
                spinnerEstDest.setSelection(select);

            }

            //cargarHorarios(destino);

            final AppCompatEditText textoBuscar = (AppCompatEditText) getView().findViewById(R.id.texto_buscar);


            // Seleccion
            spinnerEstDest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                    if (!textoBuscar.isFocused()) {

                        int id = ((SpinnerItem) arg0.getItemAtPosition(arg2)).getId();

                        String destino = ((SpinnerItem) arg0.getItemAtPosition(arg2)).toString();

                        estacionSeleccionada (id, destino);
                    } else if(adapterEstDest.getCount() == 1) {

                        estacionSeleccionada (adapterEstDest.getItem(0).getId(), adapterEstDest.getItem(0).toString());

                    }


                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }

            });




            // Busqueda


            textoBuscar.addTextChangedListener(new TextWatcher() {

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    adapterEstDest.getFilter().filter(s);

                    /*if(adapterEstDest.getCount() == 1) {
                        estacionSeleccionada (adapterEstDest.getItem(0).getId(), adapterEstDest.getItem(0).toString());
                    }*/

                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }



                public void afterTextChanged(Editable s) {



                }
            });

            //Control desde teclado

            textoBuscar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_GO) {

                        if (spinnerEstDest.getSelectedItem() == null) {
                            return true;
                        }

                        int id = ((SpinnerItem) spinnerEstDest.getSelectedItem()).getId();

                        Integer codigoEstacion = UtilidadesTRAM.HORARIOS_COD_ESTACION[id];

                        if (destinoActual != null && !destinoActual.equals(codigoEstacion)) {
                            recargaExterna = false;
                        }

                        destinoActual = codigoEstacion;

                        ((MainActivity) getActivity()).datosPantallaPrincipal.gestionarHistorial(((MainActivity) getActivity()).paradaActual, codigoEstacion.toString());

                        textoDestinoActual = ((SpinnerItem) spinnerEstDest.getSelectedItem()).toString();

                        cargarHorarios(codigoEstacion, id);

                        onCambioDestino(destinoActual, textoDestinoActual);


                        return false;
                    }

                    return false;
                }
            });

        }
    }


    private void estacionSeleccionada (int id, String destino) {

        Integer codigoEstacion = UtilidadesTRAM.HORARIOS_COD_ESTACION[id];
        //actividad.consultaHorarioTram.setCodEstacionDestino(codigoEstacion);
        //actividad.consultaHorarioTram.setEstacionDestinoSeleccion(arg2);

        if (destinoActual != null && !destinoActual.equals(codigoEstacion)) {
            recargaExterna = false;
        }

        destinoActual = codigoEstacion;

        ((MainActivity) getActivity()).datosPantallaPrincipal.gestionarHistorial(((MainActivity) getActivity()).paradaActual, codigoEstacion.toString());

        textoDestinoActual = destino;

        cargarHorarios(codigoEstacion, id);

        onCambioDestino(destinoActual, textoDestinoActual);

    }

    /**
     * Consultar si la parada ya esta en el historial
     *
     * @param parada
     * @return
     */
    public String cargarHorarioParadaHistorial(Integer parada) {

        if (parada == null) {
            return null;
        }

        try {

            String parametros[] = {Integer.toString(parada)};

            //Cursor cursor = getActivity().managedQuery(HistorialDB.Historial.CONTENT_URI_ID_PARADA, HistorialActivity.PROJECTION, null, parametros, null);
            Cursor cursor = ContentResolverCompat.query(getActivity().getContentResolver(), HistorialDB.Historial.CONTENT_URI_ID_PARADA, HistorialActivity.PROJECTION, null, parametros, null, null);

            if (cursor != null) {

                cursor.moveToFirst();
                String value = cursor.getString(cursor.getColumnIndex(HistorialDB.Historial.HORARIO_SELECCIONADO));

                cursor.close();

                return value;

            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Seleccion del fondo de la galeria en el arranque
     */
    private void setupFondoAplicacion() {

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = getActivity().findViewById(R.id.contenedor_fragment_horario_tram);

        if (contenedor_principal != null) {
            UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());
        }

    }

}
