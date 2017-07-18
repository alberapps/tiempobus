/*
* Copyright (C) 2014 The Android Open Source Project
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

package alberapps.android.tiempobus.rutas;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import alberapps.android.tiempobus.R;
import alberapps.java.directions.Step;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class StepAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private List<Step> mDataSet;
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
            /*v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d(TAG, "Element " + getPosition() + " clicked.");

                    Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            });*/
            //textView = (TextView) v.findViewById(R.id.textView);
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
        //private final TextView datosDescripcion;

        public HeaderViewHolder(View v, final RutasActivity contexto) {
            super(v);

            ImageButton volver = (ImageButton)v.findViewById(R.id.volver_rutas);

            volver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d(TAG, "Element " + getPosition() + " clicked.");

                    contexto.cargarListadoRutasVuelta();

                }
            });
            //textView = (TextView) v.findViewById(R.id.textView);
            //datosDescripcion = (TextView) v.findViewById(R.id.datos_descripcion);*/
        }

        //public TextView getDatosDescripcion() {
            //return datosDescripcion;
        //}
    }


    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public StepAdapter(Context context, List<Step> dataSet) {

        this.contexto = (RutasActivity) context;

        mDataSet = dataSet;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = null;

        if(viewType == ITEM) {
            // Create a new view.
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_rutas_item, viewGroup, false);
            return new ItemViewHolder(v, contexto);
        }else if(viewType == CABECERA){
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_rutas_header, viewGroup, false);
            return new HeaderViewHolder(v, contexto);
        }


        return null;
    }

    @Override
    public int getItemViewType(int position) {

        if(position == 0){
            return CABECERA;
        }else{
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

            Step paso = mDataSet.get(position - 1);

            String texto;
            if (paso != null) {
                texto = paso.getHtmlInstructions();
            } else {
                texto = "sin datos";
            }
            ((ItemViewHolder) viewHolder).getDatosDescripcion().setText(texto);

            if(paso != null && paso.getSteps() != null && !paso.getSteps().isEmpty()){

                StringBuilder pasos = new StringBuilder("");
                for(int i = 0; i < paso.getSteps().size();i++){
                    pasos.append(i + 1);
                    pasos.append(". ");
                    pasos.append(Html.fromHtml(paso.getSteps().get(i).getHtmlInstructions()));
                    pasos.append("\n");
                }

                ((ItemViewHolder) viewHolder).getDatosPasos().setText(pasos.toString());

            }else if(paso != null && paso.getTransitDetails() != null && paso.getTransitDetails().getLine() != null){

                StringBuilder datos = new StringBuilder("");
                if(paso.getTransitDetails().getLine().getType() != null){
                    datos.append(paso.getTransitDetails().getLine().getType());
                }

                if(paso.getTransitDetails().getLine().getShortName() != null){

                    if(datos.length() > 0){
                        datos.append("\n");
                        datos.append(contexto.getString(R.string.linea));
                        datos.append(" ");
                    }

                    datos.append(paso.getTransitDetails().getLine().getShortName());
                }

                if(paso.getTransitDetails().getLine().getName() != null){

                    if(datos.length() > 0){
                        datos.append(": ");
                    }

                    datos.append(paso.getTransitDetails().getLine().getName());
                }

                if(paso.getTransitDetails().getHeadsign()!= null){

                    if(datos.length() > 0){
                        datos.append("\n");
                    }

                    datos.append(contexto.getString(R.string.destino));
                    datos.append(": ");
                    datos.append(paso.getTransitDetails().getHeadsign());

                }


                ((ItemViewHolder) viewHolder).getDatosPasos().setText(datos.toString());


            }


        }

    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size() + 1;
    }


    public void addAll(List<Step> mDataSet2){

        mDataSet.clear();
        mDataSet.addAll(mDataSet2);
        notifyDataSetChanged();

    }

}
