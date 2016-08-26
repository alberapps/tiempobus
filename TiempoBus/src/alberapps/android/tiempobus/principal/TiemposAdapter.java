/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.android.tiempobus.principal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import alberapps.android.tiempobus.MainActivity;
import alberapps.android.tiempobus.R;
import alberapps.android.tiempobus.mapas.MapasActivity;
import alberapps.java.tam.BusLlegada;

/**
 * Adaptador Tiempos
 */
public class TiemposAdapter extends ArrayAdapter<BusLlegada> {

    private Context contexto;

    private ArrayList<BusLlegada> buses;

    private String paradaActual = "";

    public String getParadaActual() {
        return paradaActual;
    }

    public void setParadaActual(String paradaActual) {
        this.paradaActual = paradaActual;
    }

    /**
     * Cache de datos para mostrar en caso de error
     *
     * @param parada
     * @return
     */
    public ArrayList<BusLlegada> getBuses(String parada) {

        if (parada.equals(paradaActual)) {
            return buses;
        } else {
            buses = new ArrayList<>();
            paradaActual = "";
        }

        return buses;

    }

    /**
     * Cache de datos para mostrar en caso de error
     *
     * @param buses
     * @param paradaActual
     */
    public void setBuses(ArrayList<BusLlegada> buses, int paradaActual) {
        this.buses = buses;
        this.paradaActual = Integer.toString(paradaActual);
    }

    /**
     * Constructor
     *
     * @param context
     * @param textViewResourceId
     */
    public TiemposAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        this.contexto = context;

    }

    /**
     * Genera la vista de cada uno de los items
     */
    @Override
    public View getView(int position, View v, ViewGroup parent) {

        final MainActivity actividad = (MainActivity) contexto;

        final BusLlegada bus = getItem(position);

        if (!bus.isSinDatos() && !bus.isErrorServicio()) {

            TextView busLinea = null;

            if (v != null) {
                busLinea = (TextView) v.findViewById(R.id.bus_linea);
            }

            // Si no tenemos la vista de la fila creada componemos una
            if (v == null || busLinea == null) {
                Context ctx = this.getContext().getApplicationContext();
                LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = vi.inflate(R.layout.tiempos_item, null);

                v.setTag(new ViewHolder(v));

            }

            //animar
            v.setAlpha(0.0f);
            v.animate().alpha(1.0f);


            // Accedemos a la vista cacheada y la rellenamos
            ViewHolder tag = (ViewHolder) v.getTag();

            // BusLlegada bus = getItem(position);
            if (bus != null) {
                tag.busLinea.setText(bus.getLinea().trim());
                tag.busDestino.setText(bus.getDestino().trim());

                tag.tiempoPrincipal.setText(controlAviso(bus.getProximo(), true, false).trim());

                //Formato colores
                DatosPantallaPrincipal.formatoLinea(contexto, tag.busLinea, bus.getLinea(), true);


                /*if (bus.getLinea().equals("L9")) {

                    tag.busProximo.setText("");

                } else {*/

                if (bus.getSegundoTram() != null) {

                    tag.busProximo.setText(controlAviso(bus.getProximo(), false, true).trim() + "\n" + controlAviso(bus.getSegundoTram().getProximo(), false, false).trim());

                } else if (bus.getSegundoBus() != null) {

                    tag.busProximo.setText(controlAviso(bus.getProximo(), false, true).trim() + "\n" + controlAviso(bus.getSegundoBus().getProximo(), false, false).trim());

                } else {

                    tag.busProximo.setText(controlAviso(bus.getProximo(), false, true).trim());
                }
                // }


                //Mensaje tiempo real
                TextView textoTiempoReal = (TextView) v.findViewById(R.id.tiempo_aviso);
                if (bus.isTiempoReal()) {
                    textoTiempoReal.setVisibility(View.GONE);
                } else {
                    textoTiempoReal.setVisibility(View.VISIBLE);
                }


            }

            // Botones
            ImageView alertaText = (ImageView) v.findViewById(R.id.tiempos_alerta_img);

            alertaText.setOnClickListener(new OnClickListener() {

                public void onClick(View view) {

                    MainActivity actividad = (MainActivity) contexto;

                    // Texto para receiver
                    String textoReceiver = actividad.gestionarAlarmas.prepararReceiver(bus, actividad.paradaActual);

                    // Activar alarma y mostrar modal
                    actividad.gestionarAlarmas.mostrarModalTiemposAlerta(bus, actividad.paradaActual, textoReceiver);

                }

            });

            ImageView compartir = (ImageView) v.findViewById(R.id.compartir_img);

            compartir.setOnClickListener(new OnClickListener() {

                public void onClick(View view) {

                    MainActivity actividad = (MainActivity) contexto;

                    actividad.datosPantallaPrincipal.shareBus(bus, actividad.paradaActual);

                }

            });

            ImageView leer = (ImageView) v.findViewById(R.id.audio_img);

            leer.setOnClickListener(new OnClickListener() {

                public void onClick(View view) {

                    MainActivity actividad = (MainActivity) contexto;

                    actividad.datosPantallaPrincipal.cantarLinea(bus);

                }

            });

            ImageView mapa = (ImageView) v.findViewById(R.id.mapa_tarjeta);

            mapa.setOnClickListener(new OnClickListener() {

                public void onClick(View view) {

                    MainActivity actividad = (MainActivity) contexto;

                    if (actividad.datosPantallaPrincipal.servicesConnected()) {

                        Intent i = new Intent(actividad, MapasActivity.class);
                        i.putExtra("LINEA_MAPA", bus.getLinea());
                        i.putExtra("LINEA_MAPA_PARADA", Integer.toString(actividad.paradaActual));
                        actividad.startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                    }

                }

            });


            //Fijar
            final ImageView fijar = (ImageView) v.findViewById(R.id.fijar_img);


            if (bus.isTarjetaFijada()) {

                fijar.setImageResource(R.drawable.content_remove);

                fijar.setOnClickListener(new OnClickListener() {

                    public void onClick(View view) {

                        MainActivity actividad = (MainActivity) contexto;

                        actividad.datosPantallaPrincipal.eliminarTarjeta(bus);

                        actividad.buses.get(getPosition(bus)).setTarjetaFijada(false);

                        actividad.buses = actividad.datosPantallaPrincipal.ordenarTiemposPorTarjetaFija(actividad.buses);

                        actividad.handler.sendEmptyMessage(MainActivity.MSG_FRECUENCIAS_ACTUALIZADAS);

                        fijar.setImageResource(R.drawable.fijar);

                        notifyDataSetChanged();


                    }

                });


            } else {


                fijar.setOnClickListener(new OnClickListener() {

                    public void onClick(View view) {

                        MainActivity actividad = (MainActivity) contexto;

                        actividad.datosPantallaPrincipal.fijarTarjeta(bus);

                        actividad.buses.get(getPosition(bus)).setTarjetaFijada(true);

                        actividad.buses = actividad.datosPantallaPrincipal.ordenarTiemposPorTarjetaFija(actividad.buses);

                        actividad.handler.sendEmptyMessage(MainActivity.MSG_FRECUENCIAS_ACTUALIZADAS);

                        notifyDataSetChanged();

                    }

                });

            }


        } else if (bus.isConsultaInicial()) {

            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            v = vi.inflate(R.layout.tiempos_item_sin_datos, null);


            TextView text = (TextView) v.findViewById(R.id.txt_sin_datos);

            text.setText(ctx.getString(R.string.aviso_recarga));


        } else {

            Context ctx = this.getContext().getApplicationContext();
            LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (DatosPantallaPrincipal.esTram(Integer.toString(actividad.paradaActual))) {
                v = vi.inflate(R.layout.tiempos_item_sin_datos_tram, null);
            } else {
                v = vi.inflate(R.layout.tiempos_item_sin_datos, null);
            }

            TextView text = (TextView) v.findViewById(R.id.txt_sin_datos);


            if (bus != null && bus.isErrorServicio()) {
                text.setText(ctx.getString(R.string.error_tiempos));
            } else {
                text.setText(ctx.getString(R.string.main_no_items) + "\n" + ctx.getString(R.string.error_status));
            }

            TextView textAviso = (TextView) v.findViewById(R.id.txt_sin_datos_aviso);

            String aviso = "";


            if (DatosPantallaPrincipal.esTram(Integer.toString(actividad.paradaActual))) {
                aviso = ctx.getString(R.string.tlf_tram);


                // //Horarios

                /*AppCompatTextView botonHorarios = (AppCompatTextView) v.findViewById(R.id.boton_horarios);
                botonHorarios.setText(botonHorarios.getText().toString().toUpperCase());

                botonHorarios.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View arg0) {

                        actividad.detenerTodasTareas();

                        Intent i = new Intent(actividad, InfoLineasTabsPager.class);
                        i.putExtra("HORARIOS", "TRAM");

                        actividad.startActivityForResult(i, MainActivity.SUB_ACTIVITY_REQUEST_PARADA);

                    }
                });*/


            } else {
                aviso = ctx.getString(R.string.tlf_subus);
            }

            ImageView imagenAviso = (ImageView) v.findViewById(R.id.imageAviso);
            imagenAviso.setImageResource(R.drawable.alerts_warning);


            textAviso.setText(aviso);


        }

        //animar
        //v.setAlpha(1.0f);
        //v.animate().alpha(0.0f).setDuration(800);
        v.setAlpha(0.0f);
        v.animate().alpha(1.0f).setDuration(800);

        //Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.push_up);
        //v.startAnimation(animation);


        return v;
    }

    /**
     * Modificación para traducir por idioma
     *
     * @param proximo
     * @return
     */
    private String controlAviso(String proximo, boolean primero, boolean segundo) {

        String traducido = "";

        String[] procesa = proximo.split(";");

        // TODO para el TRAM
        if (procesa[0].equals("TRAM")) {
            return procesa[1];
        }

        String tiempo1 = "";
        String tiempo2 = "";

        if (procesa[0].equals("enlaparada")) {

            tiempo1 = (String) contexto.getResources().getText(R.string.tiempo_m_1);

        } else if (procesa[0].equals("sinestimacion")) {

            tiempo1 = (String) contexto.getResources().getText(R.string.tiempo_m_2);

        } else {

            //tiempo1 = String.format("%02d",Integer.parseInt(procesa[0]));

            tiempo1 = procesa[0];

            /*if(tiempo1.length() == 14){
                tiempo1 = "0".concat(tiempo1);
            }*/

        }

        if (procesa[1].equals("enlaparada")) {

            tiempo2 = (String) contexto.getResources().getText(R.string.tiempo_m_1);

        } else if (procesa[1].equals("sinestimacion")) {

            tiempo2 = (String) contexto.getResources().getText(R.string.tiempo_m_2);


        } else {

            //tiempo2 = String.format("%02d",Integer.parseInt(procesa[1]));

            tiempo2 = procesa[1];

            /*if(tiempo2.length() == 14){
                tiempo2 = "0".concat(tiempo2);
            }*/

        }

        String nuevoLiteral = "";

        if (primero) {

            traducido = tiempo1.replaceAll("min.", contexto.getString(R.string.literal_min)).replace("(", "- ").replace(")", "");

            nuevoLiteral = traducido;

        } else if (segundo) {

            traducido = tiempo2.replaceAll("min.", contexto.getString(R.string.literal_min)).replace("(", "- ").replace(")", "");

            nuevoLiteral = traducido;

        } else {


            traducido = tiempo1 + " " + contexto.getString(R.string.tiempo_m_3) + " " + tiempo2;

            //traducido = "> " + tiempo1 + "\n> " + tiempo2;

            // min.
            nuevoLiteral = traducido.replaceAll("min.", contexto.getString(R.string.literal_min));

            nuevoLiteral = nuevoLiteral.replaceAll("[(]", "- ").replaceAll("[)]", "");


        }

        return nuevoLiteral;

    }

    /*
     * Clase contendora de los elementos de la vista de fila para agilizar su
     * acceso
     */
    private class ViewHolder {
        TextView busLinea;
        TextView busDestino;
        TextView busProximo;
        TextView tiempoPrincipal;

        public ViewHolder(View v) {
            busLinea = (TextView) v.findViewById(R.id.bus_linea);
            busDestino = (TextView) v.findViewById(R.id.bus_destino);
            busProximo = (TextView) v.findViewById(R.id.bus_proximo);

            tiempoPrincipal = (TextView) v.findViewById(R.id.tiempo_principal);
        }

    }

}
