/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
 * <p/>
 * based on the Copyright (C) 2011 The Android Open Source Project
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
package alberapps.android.tiempobuswidgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import alberapps.java.tam.UtilidadesTAM;

/**
 * This is the service that provides the factory to be bound to the collection
 * service.
 */
public class TiemposWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

/**
 * This is the factory that will provide data to the collection widget.
 */
class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        // Since we reload the cursor in onDataSetChanged() which gets called
        // immediately after
        // onCreate(), we do nothing here.
    }

    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    public int getCount() {
        return mCursor.getCount();
    }

    public RemoteViews getViewAt(int position) {
        // Get the data for this position from the content provider
        String linea = "";
        String tiempo = "";
        String tiempoPrimero = "";
        String destino = "";
        String parada = "";
        if (mCursor.moveToPosition(position)) {
            final int lineaColIndex = mCursor.getColumnIndex(TiemposDataProvider.Columns.LINEA);
            final int tiempoColIndex = mCursor.getColumnIndex(TiemposDataProvider.Columns.TIEMPO);
            final int destinoColIndex = mCursor.getColumnIndex(TiemposDataProvider.Columns.DESTINO);
            final int paradaColIndex = mCursor.getColumnIndex(TiemposDataProvider.Columns.PARADA);

            linea = mCursor.getString(lineaColIndex);
            //tiempo = controlAviso(mCursor.getString(tiempoColIndex), false);
            tiempo = controlAvisoNuevo(mCursor.getString(tiempoColIndex), false, true).trim();


            tiempoPrimero = controlAviso(mCursor.getString(tiempoColIndex), true);

            destino = mCursor.getString(destinoColIndex);


            parada = mCursor.getString(paradaColIndex);
        }

        // Return a proper item with the proper day and temperature
        // final String formatStr =
        // mContext.getResources().getString(R.string.item_format_string);
        // final int itemId = R.layout.widget_item;
        // RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        // rv.setTextViewText(R.id.widget_item, String.format(formatStr, temp,
        // day));

        final int itemId = R.layout.tiempos_item;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), itemId);
        rv.setTextViewText(R.id.bus_linea, linea);
        rv.setTextViewText(R.id.bus_destino, destino);
        rv.setTextViewText(R.id.bus_proximo, tiempo);
        rv.setTextViewText(R.id.bus_parada, parada);
        rv.setTextViewText(R.id.tiempo_principal, tiempoPrimero);

        //formatoLinea(mContext, rv..setba.busLinea, bus.getLinea());

        // Set the click intent so that we can handle it and show a toast
        // message
        final Intent fillInIntent = new Intent();
        final Bundle extras = new Bundle();
        extras.putInt(TiemposWidgetProvider.DATO_ID, position);
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.t_item, fillInIntent);

        return rv;
    }


    /**
     * Colores lineas
     *
     * @param contexto
     * @param busLinea
     * @param linea
     */
    public static void formatoLinea(Context contexto, TextView busLinea, String linea) {

        //Color circulo
        if (linea.trim().equals("L1")) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                busLinea.setBackground(contexto.getResources().getDrawable(R.drawable.circulo_l1));
            } else {
                busLinea.setBackgroundDrawable(contexto.getResources().getDrawable(R.drawable.circulo_l1));
            }
        } else if (linea.trim().equals("L2")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                busLinea.setBackground(contexto.getResources().getDrawable(R.drawable.circulo_l2));
            } else {
                busLinea.setBackgroundDrawable(contexto.getResources().getDrawable(R.drawable.circulo_l2));
            }

        } else if (linea.trim().equals("L3")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                busLinea.setBackground(contexto.getResources().getDrawable(R.drawable.circulo_l3));
            } else {
                busLinea.setBackgroundDrawable(contexto.getResources().getDrawable(R.drawable.circulo_l3));
            }
        } else if (linea.trim().equals("L4")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                busLinea.setBackground(contexto.getResources().getDrawable(R.drawable.circulo_l4));
            } else {
                busLinea.setBackgroundDrawable(contexto.getResources().getDrawable(R.drawable.circulo_l4));
            }
        } else if (UtilidadesTAM.isBusUrbano(linea.trim())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                busLinea.setBackground(contexto.getResources().getDrawable(R.drawable.circulo_rojo));
            } else {
                busLinea.setBackgroundDrawable(contexto.getResources().getDrawable(R.drawable.circulo_rojo));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                busLinea.setBackground(contexto.getResources().getDrawable(R.drawable.circulo_azul));
            } else {
                busLinea.setBackgroundDrawable(contexto.getResources().getDrawable(R.drawable.circulo_azul));
            }
        }


        //Size
        if (linea.length() > 2) {
            busLinea.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        } else {
            busLinea.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        }


    }


    /**
     * Modificación para traducir por idioma
     *
     * @param proximo
     * @return
     */
    private String controlAviso(String proximo, boolean primero) {

        String traducido = "";

        String nuevoLiteral = "";

        if (proximo != null && !proximo.trim().equals("")) {

            String[] procesa = proximo.split(";");

            // TODO para el TRAM
            if (procesa[0].equals("TRAM")) {
                return procesa[1];
            }

            String tiempo1 = "";
            String tiempo2 = "";

            if (procesa[0].equals("enlaparada")) {

                tiempo1 = mContext.getString(R.string.tiempo_m_1);

            } else if (procesa[0].equals("sinestimacion")) {

                tiempo1 = mContext.getString(R.string.tiempo_m_2);

            } else {

                //tiempo1 = String.format("%02d",Integer.parseInt(procesa[0]));

                tiempo1 = procesa[0];

            /*if(tiempo1.length() == 14){
                tiempo1 = "0".concat(tiempo1);
            }*/

            }

            if (procesa[1].equals("enlaparada")) {

                tiempo2 = mContext.getString(R.string.tiempo_m_1);

            } else if (procesa[1].equals("sinestimacion")) {

                tiempo2 = mContext.getString(R.string.tiempo_m_2);


            } else {

                //tiempo2 = String.format("%02d",Integer.parseInt(procesa[1]));

                tiempo2 = procesa[1];

            /*if(tiempo2.length() == 14){
                tiempo2 = "0".concat(tiempo2);
            }*/

            }


            if (primero) {

                traducido = tiempo1.replaceAll("min.", mContext.getString(R.string.literal_min)).replace("(", "- ").replace(")", "");

                nuevoLiteral = traducido;

            } else {


                traducido = tiempo1 + " " + mContext.getString(R.string.tiempo_m_3) + " " + tiempo2;

                //traducido = "> " + tiempo1 + "\n> " + tiempo2;

                // min.
                nuevoLiteral = traducido.replaceAll("min.", mContext.getString(R.string.literal_min));//.replace("(", "\"").replace(")", "\"");


            }

        } else {

            nuevoLiteral = mContext.getString(R.string.tiempo_m_2);

        }

        return nuevoLiteral;

    }

    /**
     * Modificación para traducir por idioma
     *
     * @param proximo
     * @return
     */
    private String controlAvisoNuevo(String proximo, boolean primero, boolean segundo) {

        String traducido = "";

        String[] procesa = proximo.split(";");

        // TODO para el TRAM
        if (procesa[0].equals("TRAM")) {
            return procesa[1];
        }

        String tiempo1 = "";
        String tiempo2 = "";

        if (procesa[0].equals("enlaparada")) {

            tiempo1 = (String) mContext.getResources().getText(R.string.tiempo_m_1);

        } else if (procesa[0].equals("sinestimacion")) {

            tiempo1 = (String) mContext.getResources().getText(R.string.tiempo_m_2);

        } else {

            tiempo1 = procesa[0];

        }

        if (procesa[1].equals("enlaparada")) {

            tiempo2 = (String) mContext.getResources().getText(R.string.tiempo_m_1);

        } else if (procesa[1].equals("sinestimacion")) {

            tiempo2 = (String) mContext.getResources().getText(R.string.tiempo_m_2);


        } else {

            tiempo2 = procesa[1];

        }

        String nuevoLiteral = "";

        if (primero) {

            traducido = tiempo1.replaceAll("min.", mContext.getString(R.string.literal_min)).replace("(", "- ").replace(")", "");

            nuevoLiteral = traducido;

        } else if (segundo) {

            traducido = tiempo2.replaceAll("min.", mContext.getString(R.string.literal_min)).replace("(", "- ").replace(")", "");

            nuevoLiteral = traducido;

        } else {


            traducido = tiempo1 + " " + mContext.getString(R.string.tiempo_m_3) + " " + tiempo2;

            // min.
            nuevoLiteral = traducido.replaceAll("min.", mContext.getString(R.string.literal_min));


        }

        return nuevoLiteral;

    }


    public RemoteViews getLoadingView() {
        // We aren't going to return a default loading view in this sample
        return null;
    }

    public int getViewTypeCount() {
        // Technically, we have two types of views (the dark and light
        // background views)
        return 2;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        // Refresh the cursor
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(TiemposDataProvider.CONTENT_URI, null, null, null, null);
    }
}
