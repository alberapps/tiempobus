/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2015 Alberto Montiel
 * <p/>
 * based on Copyright (C) 2014 The Android Open Source Project
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
package alberapps.android.tiempobus.rutas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.java.directions.Leg;
import alberapps.java.directions.Route;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class RouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<Route> mDataSet;

    RutasActivity contexto;

    private static final int CABECERA = 0;
    private static final int ITEM = 1;


    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView datosDescripcion;
        private final TextView datosPasos;

        public ItemViewHolder(View v, final RutasActivity contexto) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d(TAG, "Element " + getPosition() + " clicked.");

                    contexto.cargarListadoPasosRuta(getAdapterPosition());

                }
            });
            datosDescripcion = (TextView) v.findViewById(R.id.datos_descripcion);
            datosPasos = (TextView) v.findViewById(R.id.datos_pasos);
        }

        public TextView getDatosDescripcion() {
            return datosDescripcion;
        }

        public TextView getDatosPasos() {
            return datosPasos;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView descOrigen;
        private final TextView descDestino;
        private final TextView descC;


        public HeaderViewHolder(View v, final RutasActivity contexto) {
            super(v);

            descOrigen = (TextView) v.findViewById(R.id.desc_origen);
            descDestino = (TextView) v.findViewById(R.id.desc_destino);
            descC = (TextView) v.findViewById(R.id.desc_c);
        }

        public TextView getDescDestino() {
            return descDestino;
        }

        public TextView getDescOrigen() {
            return descOrigen;
        }

        public TextView getDescC() {
            return descC;
        }

    }


    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public RouteAdapter(Context context, List<Route> dataSet) {

        this.contexto = (RutasActivity) context;

        mDataSet = dataSet;

    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = null;

        if (viewType == ITEM) {
            // Create a new view.
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.rutas_activity_item, viewGroup, false);
            return new ItemViewHolder(v, contexto);
        } else if (viewType == CABECERA) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.rutas_activity_inicial_header, viewGroup, false);
            return new HeaderViewHolder(v, contexto);
        }


        return null;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return CABECERA;
        } else {
            return ITEM;
        }

        //return super.getItemViewType(position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        //Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element


        if (viewHolder instanceof ItemViewHolder) {

            Leg leg = null;

            if (mDataSet.get(position - 1).getLegs() != null && !mDataSet.get(position - 1).getLegs().isEmpty()) {
                leg = mDataSet.get(position - 1).getLegs().get(0);
            }

            StringBuilder texto = new StringBuilder("");
            if (leg != null) {

                StringBuilder pasos = new StringBuilder("");

                for (int i = 0; i < leg.getResumen().size(); i++) {

                    if (pasos.length() > 0) {
                        pasos.append(" - ");
                    }


                    if (leg.getResumen().get(i).equals("WALKING")) {
                        pasos.append(contexto.getString(R.string.andar));
                        pasos.append(" (");
                        pasos.append(leg.getSteps().get(i).getDuration());
                        pasos.append(") ");
                    } else if (leg.getResumen().get(i).contains("HEAVY_RAIL")) {
                        pasos.append(leg.getResumen().get(i).replace("HEAVY_RAIL", contexto.getString(R.string.cercanias)));
                        pasos.append(" (");
                        pasos.append(leg.getSteps().get(i).getDuration());
                        pasos.append(" - ");
                        pasos.append(leg.getSteps().get(i).getTransitDetails().getDepartureTime());
                        pasos.append(") ");
                    } else {
                        pasos.append(leg.getResumen().get(i));
                        pasos.append(" (");
                        pasos.append(leg.getSteps().get(i).getDuration());
                        pasos.append(" - ");
                        pasos.append(leg.getSteps().get(i).getTransitDetails().getDepartureTime());
                        pasos.append(") ");

                    }

                }

                ((ItemViewHolder) viewHolder).getDatosPasos().setText(pasos.toString());

                texto.append(leg.getDuration());
                texto.append(" - ");
                texto.append(leg.getDistance());
            } else {
                texto.append("sin datos");
            }


            ((ItemViewHolder) viewHolder).getDatosDescripcion().setText(texto);

        } else if (viewHolder instanceof HeaderViewHolder) {

            Leg leg = null;

            if (mDataSet != null && !mDataSet.isEmpty()) {

                if (mDataSet.get(position).getLegs() != null && !mDataSet.get(position).getLegs().isEmpty()) {
                    leg = mDataSet.get(position).getLegs().get(0);
                }

                if (leg != null && leg.getStartArddress() != null) {
                    ((HeaderViewHolder) viewHolder).getDescOrigen().setText(leg.getStartArddress());
                }
                if (leg != null && leg.getEndAdress() != null) {
                    ((HeaderViewHolder) viewHolder).getDescDestino().setText(leg.getEndAdress());
                }
                if (mDataSet.get(position).getCopyrights() != null) {
                    ((HeaderViewHolder) viewHolder).getDescC().setText(mDataSet.get(position).getCopyrights());
                }
            }

        }


    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size() + 1;
    }


    /**
     * Actualizacion de datos
     *
     * @param mDataSet2
     */
    public void addAll(List<Route> mDataSet2) {

        mDataSet.clear();
        mDataSet.addAll(mDataSet2);
        notifyDataSetChanged();

    }

}
