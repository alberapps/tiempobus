/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 *
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

import android.content.Context;

import alberapps.java.tam.mapas.DatosMapa;

/**
 * Datos para informacion de las lineas
 */
public class DatosInfoLinea {

    private DatosMapa result;

    private DatosMapa resultIda;

    private DatosMapa resultVuelta;

    private FragmentIda fIda;

    private String url;

    private String linea;

    private String sublinea;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    public String getLinea() {
        return linea;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public String getSublinea() {
        return sublinea;
    }

    public void setSublinea(String sublinea) {
        this.sublinea = sublinea;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FragmentIda getfIda() {
        return fIda;
    }

    public void setfIda(FragmentIda fIda) {
        this.fIda = fIda;
    }

    public DatosMapa getResultIda() {
        return resultIda;
    }

    public void setResultIda(DatosMapa resultIda) {
        this.resultIda = resultIda;
    }

    public DatosMapa getResultVuelta() {
        return resultVuelta;
    }

    public void setResultVuelta(DatosMapa resultVuelta) {
        this.resultVuelta = resultVuelta;
    }

    public DatosMapa getResult() {
        return result;
    }

    public void setResult(DatosMapa result) {
        this.result = result;
    }

}
