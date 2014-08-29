/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2012 Alberto Montiel
 *
 *  based on code by ZgzBus Copyright (C) 2010 Francho Joven
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
package alberapps.java.tam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase contenedora de la estructura de datos para cada una de las llegadas de
 * un poste
 */
public class BusLlegada implements Comparable<BusLlegada> {
    /**
     * Número de línea
     */
    private String linea;
    /**
     * Dirección
     */
    private String destino;
    /**
     * Próximo bus en...
     */
    private String proximo;

    private BusLlegada segundoTram;

    private BusLlegada segundoBus;

    private boolean sinDatos = false;

    private boolean consultaInicial = false;

    private boolean errorServicio = false;

    private boolean tarjetaFijada = false;

    public boolean isTarjetaFijada() {
        return tarjetaFijada;
    }

    public void setTarjetaFijada(boolean tarjetaFijada) {
        this.tarjetaFijada = tarjetaFijada;
    }

    public boolean isErrorServicio() {
        return errorServicio;
    }

    public void setErrorServicio(boolean errorServicio) {
        this.errorServicio = errorServicio;
    }

    public BusLlegada() {

    }

    /**
     * Constructor
     *
     * @param linea   bus
     * @param destino dirección de de destino
     * @param proximo próximo bus en
     */
    public BusLlegada(String linea, String destino, String proximo) {
        this.linea = linea;
        this.destino = destino;
        this.proximo = proximo;
    }

    public int compareTo(BusLlegada bus2) {
        Integer min1 = getProximoMinutos();
        Integer min2 = bus2.getProximoMinutos();
        return min1.compareTo(min2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((destino == null) ? 0 : destino.hashCode());
        result = prime * result + ((linea == null) ? 0 : linea.hashCode());
        result = prime * result + ((this.getProximoMinutos() == null) ? 0 : getProximoMinutos().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BusLlegada other = (BusLlegada) obj;
        if (destino == null) {
            if (other.destino != null)
                return false;
        } else if (!destino.equals(other.destino))
            return false;
        if (linea == null) {
            if (other.linea != null)
                return false;
        } else if (!linea.equals(other.linea))
            return false;
        if (proximo == null) {
            if (other.proximo != null)
                return false;
        } else if (!proximo.equals(other.proximo))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return linea + " Dirección: " + destino + " en " + proximo;
    }

    /**
     * @return the linea
     */
    public String getLinea() {
        return linea;
    }

    /**
     * @param linea the linea to set
     */
    public void setLinea(String linea) {
        this.linea = linea;
    }

    /**
     * @return the destino
     */
    public String getDestino() {
        return destino;
    }

    /**
     * @param destino the destino to set
     */
    public void setDestino(String destino) {
        this.destino = destino;
    }

    /**
     * @return the proximo
     */
    public String getProximo() {
        return proximo;
    }

    /**
     * @param proximo the proximo to set
     */
    public void setProximo(String proximo) {
        this.proximo = proximo;
    }

    public Integer getProximoMinutos() {
        Integer minutos = 1000;

        String[] procesa = this.proximo.split(";");

        if (procesa[0].equals("enlaparada")) {
            minutos = 0;
        } else if (procesa[0].equals("sinestimacion")) {
            minutos = 9999;
        } else {
            Pattern p = Pattern.compile("([0-9]+) min.");
            Matcher m = p.matcher(procesa[0]);
            if (m.find()) {
                minutos = Integer.valueOf(m.group(1));
            }
        }

        return minutos;
    }

    public void cambiarProximo(Integer proximoMinutosNuevo) {

        String[] procesa = this.proximo.split(";");

        StringBuffer proximoNuevo = new StringBuffer();

        if (proximoMinutosNuevo.equals(0)) {
            proximoNuevo.append("enlaparada");
        } else if (proximoMinutosNuevo.equals(9999)) {
            proximoNuevo.append("sinestimacion");
        } else {
            proximoNuevo.append(getFormatoTiempoEspera(Integer.toString(proximoMinutosNuevo)));
            // proximoNuevo.append(" ");
            // int pos = procesa[0].indexOf("m");

            // proximoNuevo.append(procesa[0].substring(pos));

        }

        proximoNuevo.append(";");
        proximoNuevo.append(procesa[1]);

        proximo = proximoNuevo.toString();

    }

    public Integer getSiguienteMinutos() {
        Integer minutos = 1000;

        String[] procesa = this.proximo.split(";");

        if (procesa[1].equals("enlaparada")) {
            minutos = 0;
        } else if (procesa[1].equals("sinestimacion")) {
            minutos = 9999;
        } else {
            Pattern p = Pattern.compile("([0-9]+) min.");
            Matcher m = p.matcher(procesa[1]);
            if (m.find()) {
                minutos = Integer.valueOf(m.group(1));
            }
        }

        return minutos;
    }

    public void cambiarSiguiente(Integer siguienteMinutosNuevo) {

        String[] procesa = this.proximo.split(";");

        StringBuffer siguienteNuevo = new StringBuffer();

        siguienteNuevo.append(procesa[0]);
        siguienteNuevo.append(";");

        if (siguienteMinutosNuevo.equals(0)) {
            siguienteNuevo.append("enlaparada");
        } else if (siguienteMinutosNuevo.equals(9999)) {
            siguienteNuevo.append("sinestimacion");
        } else {
            siguienteNuevo.append(getFormatoTiempoEspera(Integer.toString(siguienteMinutosNuevo)));

            // siguienteNuevo.append(" ");

            // int pos = procesa[1].indexOf("m");

            // siguienteNuevo.append(procesa[1].substring(pos));

        }

        proximo = siguienteNuevo.toString();

    }

    /**
     * Tiempos con formato tram
     *
     * @return tiempo
     */
    public Integer getProximoMinutosTRAM() {
        Integer minutos = 1000;

        String[] procesa = this.proximo.split(";");

        if (procesa[1].trim().charAt(0) == '<') {
            minutos = 0;
        } else if (procesa[1].trim().charAt(0) == '>') {
            minutos = 9999;
        } else if (procesa[1].trim().charAt(0) == '-') {
            return 9999;
        } else if (procesa[1].trim().charAt(0) == 'E') {
            return 9999;

        } else {
            Pattern p = Pattern.compile("([0-9]+) min.");
            Matcher m = p.matcher(procesa[1]);
            if (m.find()) {
                minutos = Integer.valueOf(m.group(1));
            } else {
                minutos = 9999;
            }
        }

        return minutos;
    }

    public BusLlegada getSegundoTram() {
        return segundoTram;
    }

    public void setSegundoTram(BusLlegada segundoTram) {
        this.segundoTram = segundoTram;
    }

    /**
     * Forma string con los minutos faltantes y la hora aproximada de llegada
     *
     * @param minutosLlegada
     * @return
     */
    private String getFormatoTiempoEspera(String minutosLlegada) {

        String formatoMinHora = "";

        GregorianCalendar cl = new GregorianCalendar();
        cl.setTimeInMillis((new Date()).getTime());
        cl.add(Calendar.MINUTE, Integer.parseInt(minutosLlegada));

        SimpleDateFormat sf = new SimpleDateFormat("HH:mm", Locale.US);
        String horaString = sf.format(cl.getTime());

        formatoMinHora = minutosLlegada + " min. (" + horaString + ")";

        return formatoMinHora;

    }

    public boolean isSinDatos() {
        return sinDatos;
    }

    public void setSinDatos(boolean sinDatos) {
        this.sinDatos = sinDatos;
    }

    public BusLlegada getSegundoBus() {
        return segundoBus;
    }

    public void setSegundoBus(BusLlegada segundoBus) {
        this.segundoBus = segundoBus;
    }

    public boolean isConsultaInicial() {
        return consultaInicial;
    }

    public void setConsultaInicial(boolean consultaInicial) {
        this.consultaInicial = consultaInicial;
    }

}
