package alberapps.android.tiempobus.principal.horariotram;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.database.historial.HistorialDB;
import alberapps.android.tiempobus.historial.HistorialActivity;
import alberapps.android.tiempobus.infolineas.InfoLineasTabsPager;
import alberapps.android.tiempobus.tasks.LoadHorariosTramAsyncTask;
import alberapps.android.tiempobus.util.UtilidadesUI;
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

    private List<String> actualHoras;
    private DatosConsultaHorariosTram datosConsulta;


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

        setupFondoAplicacion();

        ImageButton botonHorarios = (ImageButton) view.findViewById(R.id.tarjeta_horario_ir);

        final MainActivity context = (MainActivity) getActivity();

        botonHorarios.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {

                context.detenerTodasTareas();

                Intent i = new Intent(context, InfoLineasTabsPager.class);
                i.putExtra("HORARIOS", "TRAM");

                StringBuffer dato = new StringBuffer("");
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

                context.datosPantallaPrincipal.shareHorario(datosHoras.getText().toString(), datosInfo.getText().toString());

            }
        });


        cargaInicial();


        /*BusLlegada bus = new BusLlegada();
        bus.setLinea();
        context.datosPantallaPrincipal.cantarLinea(bus);
        */


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }

    public void recargarHorarios(){

        //cargarHorarios(destinoActual);

        cargaInicial();

    }


    private DatosConsultaHorariosTram getDatosConsulta(int destino){

        Date hoy = new Date();

        //Codigo teniendo en cuenta si es de la L2
        int codEstacionOrigen = UtilidadesTRAM.getParadaParaHorarios(((MainActivity)getActivity()).paradaActual);

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
            AsyncTask<Object, Void, HorarioTram> taskHorariosTram = new LoadHorariosTramAsyncTask(loadHorariosTramAsyncTaskResponder).execute(datosConsulta);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_red), Toast.LENGTH_LONG).show();
        }

    }

    private void cargarDatos(HorarioTram datos, boolean recarga) {

        HorarioItem horas = null;

        if(getView() != null) {
            TextView datosHoras = (TextView) getView().findViewById(R.id.datos_horas);
            TextView datosInfo = (TextView) getView().findViewById(R.id.datos_info);

            if (recarga) {
                datosHoras.setText(getString(R.string.aviso_recarga));
                datosInfo.setText("");
                return;
            }

            if (datos != null && datos.getHorariosItemCombinados() != null && !datos.getHorariosItemCombinados().isEmpty() && datos.getHorariosItemCombinados().size() > 1) {

                actualHoras = new ArrayList<>();

                horas = datos.getHorariosItemCombinados().get(1);

                for (int i = 1; i < datos.getHorariosItemCombinados().size(); i++) {

                    if (datos.getHorariosItemCombinados().get(i).getDatoInfo().equals(horas.getDatoInfo())) {

                        actualHoras.addAll(Arrays.asList(datos.getHorariosItemCombinados().get(i).getHoras().split(" ")));

                        if (actualHoras.size() > 5) {
                            actualHoras = actualHoras.subList(0, 5);
                            break;
                        }

                    } else {
                        break;
                    }
                }

                String stringHoras = "";

                for (int j = 0; j < actualHoras.size(); j++) {

                    if (stringHoras.length() > 0) {
                        stringHoras = stringHoras + " ";
                    }

                    stringHoras = stringHoras + actualHoras.get(j);
                }

                datosHoras.setText(stringHoras);


                datosInfo.setText(horas.getDatoInfo());

            } else {

                datosHoras.setText(getString(R.string.aviso_error_datos));
                datosInfo.setText("");

            }

        }
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


    private void cargaInicial() {

        // Combo de seleccion de destino
        final Spinner spinnerEstDest = (Spinner) getView().findViewById(R.id.spinner_estacion_destino);
        ArrayAdapter<CharSequence> adapterEstDest = null;
        adapterEstDest = ArrayAdapter.createFromResource(getActivity(), R.array.estaciones_tram, android.R.layout.simple_spinner_item);
        adapterEstDest.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstDest.setAdapter(adapterEstDest);

        //Cargar si esta disponible, el ultimo destino seleccionado para esta parada
        String paradaHist = cargarHorarioParadaHistorial(((MainActivity)getActivity()).paradaActual);
        int destino = UtilidadesTRAM.HORARIOS_COD_ESTACION[spinnerEstDest.getSelectedItemPosition()];

        if(paradaHist != null && !paradaHist.equals("")){
            destino = Integer.parseInt(paradaHist);

            // Seleccion inicial

            List datosList = new ArrayList(Arrays.asList(UtilidadesTRAM.HORARIOS_COD_ESTACION));
            int select = datosList.lastIndexOf(destino);
            spinnerEstDest.setSelection(select);

        }

        //cargarHorarios(destino);



        // Seleccion
        spinnerEstDest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                Integer codigoEstacion = UtilidadesTRAM.HORARIOS_COD_ESTACION[arg2];
                //actividad.consultaHorarioTram.setCodEstacionDestino(codigoEstacion);
                //actividad.consultaHorarioTram.setEstacionDestinoSeleccion(arg2);

                destinoActual = codigoEstacion;

                ((MainActivity) getActivity()).datosPantallaPrincipal.gestionarHistorial(((MainActivity) getActivity()).paradaActual, codigoEstacion.toString());

                cargarHorarios(codigoEstacion, arg2);

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });





    }


    /**
     * Consultar si la parada ya esta en el historial
     *
     * @param parada
     * @return
     */
    public String cargarHorarioParadaHistorial(Integer parada) {

        if(parada == null){
            return null;
        }

        try {

            String parametros[] = {Integer.toString(parada)};

            Cursor cursor = getActivity().managedQuery(HistorialDB.Historial.CONTENT_URI_ID_PARADA, HistorialActivity.PROJECTION, null, parametros, null);

            if (cursor != null) {

                cursor.moveToFirst();

                return cursor.getString(cursor.getColumnIndex(HistorialDB.Historial.HORARIO_SELECCIONADO));

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

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String fondo_galeria = preferencias.getString("image_galeria", "");

        View contenedor_principal = getActivity().findViewById(R.id.contenedor_fragment_horario_tram);

        if(contenedor_principal != null) {
            UtilidadesUI.setupFondoAplicacion(fondo_galeria, contenedor_principal, getActivity());
        }

    }

}
