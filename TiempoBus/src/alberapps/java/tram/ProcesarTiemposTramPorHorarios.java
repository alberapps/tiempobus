/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2014 Alberto Montiel
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
package alberapps.java.tram;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import alberapps.android.tiempobus.util.UtilidadesUI;
import alberapps.java.exception.TiempoBusException;
import alberapps.java.tam.BusLlegada;
import alberapps.java.tram.horarios.DatosConsultaHorariosTram;
import alberapps.java.tram.horarios.HorarioItem;
import alberapps.java.tram.horarios.HorarioTram;
import alberapps.java.tram.horarios.ProcesarHorariosTram;
import alberapps.java.util.Utilidades;

/**
 * Consulta de tiempos TRAM
 */
public class ProcesarTiemposTramPorHorarios {

    /**
     * Procesa tiempos
     *
     * @param parada
     * @return
     * @throws Exception
     */
    public static ArrayList<BusLlegada> procesaTiemposLlegada(int parada, Integer destino, String textoDestino, Context context) throws Exception {

        ArrayList<BusLlegada> buses = new ArrayList<>();

        ArrayList<BusLlegada> busesList = new ArrayList<>();


        try {

            if (destino != null) {

                busesList.addAll(obtenerTiemposDesdeHorariosConDestino(parada, destino, textoDestino, context));

            } else {

                //Lineas de la parada
                //if (UtilidadesTRAM.esParadaL9(Integer.toString(parada))) {
                busesList.addAll(obtenerTiemposDesdeHorariosL9(parada, context));
                //}


                /*if (UtilidadesTRAM.esParadaL1(Integer.toString(parada))) {
                    busesList.addAll(obtenerTiemposDesdeHorariosL1(parada));
                }

                //Para otras lineas
                if (UtilidadesTRAM.esParadaL2(Integer.toString(parada))) {
                    busesList.addAll(obtenerTiemposDesdeHorariosL2(parada));
                }

                if (UtilidadesTRAM.esParadaL3(Integer.toString(parada))) {
                    busesList.addAll(obtenerTiemposDesdeHorariosL3(parada));
                }

                if (UtilidadesTRAM.esParadaL4(Integer.toString(parada))) {
                    busesList.addAll(obtenerTiemposDesdeHorariosL4(parada));
                }
                */
            }


        } catch (Exception e) {

            Log.d("TIEMPOS TRAM", "TRAM: " + e.getMessage());


            busesList = null;

            throw e;
        }

        if (busesList != null) {
            buses.addAll(busesList);
        }


        // Control errores del status
        if (buses == null || buses.isEmpty()) {

            throw new TiempoBusException(TiempoBusException.ERROR_STATUS_SERVICIO);

        }

        Collections.sort(buses);
        return buses;
    }


    /**
     * Obtener tiempos desde los datos de horarios
     *
     * @param parada
     * @return
     * @throws Exception
     */
    private static ArrayList<BusLlegada> obtenerTiemposDesdeHorariosL9(int parada, Context context) throws Exception {

        ArrayList<BusLlegada> listado = new ArrayList<>();

        //Lineas
        //if(UtilidadesTRAM.es)


        DatosConsultaHorariosTram datosConsulta = new DatosConsultaHorariosTram();

        Date hoy = new Date();


        datosConsulta.setCodEstacionOrigen(parada);
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

        //Ida
        if (parada != 50) {
            datosConsulta.setCodEstacionDestino(50);
            HorarioTram horarioTramIda = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada ida = new BusLlegada();
            ida.setDestino("DÉNIA");
            ida.setLinea("L9");
            ida.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasList = horarioTramIda.getHorariosItemCombinados();
            HorarioItem horas = horasList.get(1);

            if (horas.getHoras() != null && !horas.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasList, ida);
            }

            ida.setTiempoReal(false);

            listado.add(ida);
        }

        if (parada != 33) {
            //Vuelta
            datosConsulta.setCodEstacionDestino(33);
            HorarioTram horarioTramVuelta = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada vuelta = new BusLlegada();
            vuelta.setDestino("BENIDORM");
            vuelta.setLinea("L9");
            vuelta.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasListVuelta = horarioTramVuelta.getHorariosItemCombinados();
            HorarioItem horasVuelta = horasListVuelta.get(1);

            if (horasVuelta.getHoras() != null && !horasVuelta.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasListVuelta, vuelta);
            }

            vuelta.setTiempoReal(false);

            listado.add(vuelta);
        }


        if (listado != null && !listado.isEmpty()) {
            return listado;
        } else {
            return null;
        }

    }

    /**
     * Obtener tiempos desde los datos de horarios
     *
     * @param parada
     * @return
     * @throws Exception
     */
    private static ArrayList<BusLlegada> obtenerTiemposDesdeHorariosL2(int parada, Context context) throws Exception {

        ArrayList<BusLlegada> listado = new ArrayList<>();

        DatosConsultaHorariosTram datosConsulta = new DatosConsultaHorariosTram();

        Date hoy = new Date();


        datosConsulta.setCodEstacionOrigen(parada);
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

        //Ida
        if (parada != 2) {
            datosConsulta.setCodEstacionDestino(2);
            HorarioTram horarioTramIda = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada ida = new BusLlegada();
            ida.setDestino("LUCEROS");
            ida.setLinea("L2");
            ida.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasList = horarioTramIda.getHorariosItemCombinados();
            HorarioItem horas = horasList.get(1);

            if (horas.getHoras() != null && !horas.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasList, ida);
            }

            ida.setTiempoReal(false);

            listado.add(ida);
        }

        if (parada != 124) {
            //Vuelta
            datosConsulta.setCodEstacionDestino(124);
            HorarioTram horarioTramVuelta = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada vuelta = new BusLlegada();
            vuelta.setDestino("SANT VICENT");
            vuelta.setLinea("L2");
            vuelta.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasListVuelta = horarioTramVuelta.getHorariosItemCombinados();
            HorarioItem horasVuelta = horasListVuelta.get(1);

            if (horasVuelta.getHoras() != null && !horasVuelta.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasListVuelta, vuelta);
            }

            vuelta.setTiempoReal(false);

            listado.add(vuelta);
        }


        if (listado != null && !listado.isEmpty()) {
            return listado;
        } else {
            return null;
        }

    }

    /**
     * Obtener tiempos desde los datos de horarios
     *
     * @param parada
     * @return
     * @throws Exception
     */
    private static ArrayList<BusLlegada> obtenerTiemposDesdeHorariosL3(int parada, Context context) throws Exception {

        ArrayList<BusLlegada> listado = new ArrayList<>();

        DatosConsultaHorariosTram datosConsulta = new DatosConsultaHorariosTram();

        Date hoy = new Date();


        datosConsulta.setCodEstacionOrigen(parada);
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

        //Ida
        if (parada != 2) {
            datosConsulta.setCodEstacionDestino(2);
            HorarioTram horarioTramIda = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada ida = new BusLlegada();
            ida.setDestino("LUCEROS");
            ida.setLinea("L3");
            ida.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasList = horarioTramIda.getHorariosItemCombinados();
            HorarioItem horas = horasList.get(1);

            if (horas.getHoras() != null && !horas.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasList, ida);
            }

            ida.setTiempoReal(false);

            listado.add(ida);
        }

        if (parada != 17) {
            //Vuelta
            datosConsulta.setCodEstacionDestino(17);
            HorarioTram horarioTramVuelta = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada vuelta = new BusLlegada();
            vuelta.setDestino("EL CAMPELLO");
            vuelta.setLinea("L3");
            vuelta.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasListVuelta = horarioTramVuelta.getHorariosItemCombinados();
            HorarioItem horasVuelta = horasListVuelta.get(1);

            if (horasVuelta.getHoras() != null && !horasVuelta.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasListVuelta, vuelta);
            }

            vuelta.setTiempoReal(false);

            listado.add(vuelta);
        }


        if (listado != null && !listado.isEmpty()) {
            return listado;
        } else {
            return null;
        }

    }

    /**
     * Obtener tiempos desde los datos de horarios
     *
     * @param parada
     * @return
     * @throws Exception
     */
    private static ArrayList<BusLlegada> obtenerTiemposDesdeHorariosL1(int parada, Context context) throws Exception {

        ArrayList<BusLlegada> listado = new ArrayList<>();

        DatosConsultaHorariosTram datosConsulta = new DatosConsultaHorariosTram();

        Date hoy = new Date();


        datosConsulta.setCodEstacionOrigen(parada);
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

        //Ida
        if (parada != 2) {
            datosConsulta.setCodEstacionDestino(2);
            HorarioTram horarioTramIda = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada ida = new BusLlegada();
            ida.setDestino("LUCEROS");
            ida.setLinea("L1");
            ida.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasList = horarioTramIda.getHorariosItemCombinados();
            HorarioItem horas = horasList.get(1);

            if (horas.getHoras() != null && !horas.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasList, ida);
            }

            ida.setTiempoReal(false);

            listado.add(ida);
        }

        if (parada != 33) {
            //Vuelta
            datosConsulta.setCodEstacionDestino(33);
            HorarioTram horarioTramVuelta = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada vuelta = new BusLlegada();
            vuelta.setDestino("BENIDORM");
            vuelta.setLinea("L1");
            vuelta.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasListVuelta = horarioTramVuelta.getHorariosItemCombinados();
            HorarioItem horasVuelta = horasListVuelta.get(1);

            if (horasVuelta.getHoras() != null && !horasVuelta.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasListVuelta, vuelta);
            }

            vuelta.setTiempoReal(false);

            listado.add(vuelta);
        }


        if (listado != null && !listado.isEmpty()) {
            return listado;
        } else {
            return null;
        }

    }

    /**
     * Obtener tiempos desde los datos de horarios
     *
     * @param parada
     * @return
     * @throws Exception
     */
    private static ArrayList<BusLlegada> obtenerTiemposDesdeHorariosL4(int parada, Context context) throws Exception {

        ArrayList<BusLlegada> listado = new ArrayList<>();

        DatosConsultaHorariosTram datosConsulta = new DatosConsultaHorariosTram();

        Date hoy = new Date();


        datosConsulta.setCodEstacionOrigen(parada);
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

        //Ida
        if (parada != 2) {
            datosConsulta.setCodEstacionDestino(2);
            HorarioTram horarioTramIda = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada ida = new BusLlegada();
            ida.setDestino("LUCEROS");
            ida.setLinea("L4");
            ida.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasList = horarioTramIda.getHorariosItemCombinados();
            HorarioItem horas = horasList.get(1);

            if (horas.getHoras() != null && !horas.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasList, ida);
            }

            ida.setTiempoReal(false);

            listado.add(ida);
        }

        if (parada != 113) {
            //Vuelta
            datosConsulta.setCodEstacionDestino(113);
            HorarioTram horarioTramVuelta = ProcesarHorariosTram.getHorarios(datosConsulta, context);

            BusLlegada vuelta = new BusLlegada();
            vuelta.setDestino("HOLANDA");
            vuelta.setLinea("L4");
            vuelta.setProximo("sinestimacion;sinestimacion");

            List<HorarioItem> horasListVuelta = horarioTramVuelta.getHorariosItemCombinados();
            HorarioItem horasVuelta = horasListVuelta.get(1);

            if (horasVuelta.getHoras() != null && !horasVuelta.getHoras().equals("")) {
                calcularTiempoPorHora(hoy, horasListVuelta, vuelta);
            }

            vuelta.setTiempoReal(false);

            listado.add(vuelta);
        }


        if (listado != null && !listado.isEmpty()) {
            return listado;
        } else {
            return null;
        }

    }

    /**
     * Calcular el tiempo que queda en funcion de las horas
     *
     * @param hoy
     * @param horasList
     * @param ida
     */
    private static void calcularTiempoPorHora(Date hoy, List<HorarioItem> horasList, BusLlegada ida) {

        HorarioItem horas = horasList.get(1);

        String[] horasArray = horas.getHoras().split(" ");

        //Calcular el tiempo a partir de la hora
        String hora1 = horasArray[0];
        String hora2 = "";
        Date hora1Fecha = Utilidades.getFechaActualConHora(hora1);
        Date hora2Fecha = null;
        String minutosTren2 = "";

        String minutosTren1 = Utilidades.getMinutosDiferencia(hoy, hora1Fecha);

        ida.cambiarProximo(Integer.parseInt(minutosTren1));

        if (horasArray.length > 2) {

            hora2 = horasArray[1];
            hora2Fecha = Utilidades.getFechaActualConHora(hora2);
            minutosTren2 = Utilidades.getMinutosDiferencia(hoy, hora2Fecha);

            ida.cambiarSiguiente(Integer.parseInt(minutosTren2));

        } else if (horasList.size() > 2) {

            horas = horasList.get(2);
            horasArray = horas.getHoras().split(" ");
            hora1 = horasArray[0];
            hora1Fecha = Utilidades.getFechaActualConHora(hora1);
            minutosTren1 = Utilidades.getMinutosDiferencia(hoy, hora1Fecha);
            ida.cambiarSiguiente(Integer.parseInt(minutosTren1));

        }


    }

    private static void calcularTiempoPorListaHoras(Date hoy, List<String> horasList, BusLlegada ida) {


        //Calcular el tiempo a partir de la hora
        String hora1 = horasList.get(0);
        String hora2 = "";
        Date hora1Fecha = Utilidades.getFechaActualConHora(hora1);
        Date hora2Fecha = null;
        String minutosTren2 = "";

        String minutosTren1 = Utilidades.getMinutosDiferencia(hoy, hora1Fecha);

        ida.cambiarProximo(Integer.parseInt(minutosTren1) + 1);

        if (horasList.size() > 1) {

            hora2 = horasList.get(1);
            hora2Fecha = Utilidades.getFechaActualConHora(hora2);
            minutosTren2 = Utilidades.getMinutosDiferencia(hoy, hora2Fecha);

            ida.cambiarSiguiente(Integer.parseInt(minutosTren2) + 1);

        }


    }


    /**
     * Obtener tiempos desde los datos de horarios
     *
     * @param parada
     * @return
     * @throws Exception
     */
    private static ArrayList<BusLlegada> obtenerTiemposDesdeHorariosConDestino(int parada, int destino, String textoDestino, Context context) throws Exception {

        ArrayList<BusLlegada> listado = new ArrayList<>();

        DatosConsultaHorariosTram datosConsulta = new DatosConsultaHorariosTram();

        Date hoy = new Date();

        //Codigo teniendo en cuenta si es de la L2
        int codEstacionOrigen = UtilidadesTRAM.getParadaParaHorarios(parada);

        datosConsulta.setCodEstacionOrigen(codEstacionOrigen);
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

        datosConsulta.setCodEstacionDestino(destino);
        HorarioTram horarioTramIda = ProcesarHorariosTram.getHorarios(datosConsulta, context);


        BusLlegada ida = new BusLlegada();
        if (horarioTramIda.getDatosTransbordos() != null && !horarioTramIda.getDatosTransbordos().isEmpty()) {
            String d1 = horarioTramIda.getDatosTransbordos().get(0).getTrenesDestino().split(":")[1].trim();
            ida.setDestino(d1);

            if (horarioTramIda.getLineasTransbordos() != null) {
                ida.setLinea(horarioTramIda.getLineasTransbordos().get(0));
            } else {

                String lineas = ProcesarTiemposTramPorHorarios.getLineasPorNombreDestino(horarioTramIda, parada);

                //Lineas posibles
                if (!lineas.equals("") && lineas.contains(",")) {
                    ida.setLinea("-");
                } else if (!lineas.equals("")) {
                    ida.setLinea(lineas);
                } else {
                    ida.setLinea("-");
                }


            }

        } else {
            ida.setDestino(textoDestino);
            ida.setLinea("-");
        }


        ida.setProximo("sinestimacion;sinestimacion");


        List<String> horariosList = cargarDatos(horarioTramIda);


        if (horariosList != null && !horariosList.isEmpty()) {
            calcularTiempoPorListaHoras(hoy, horariosList, ida);
        }

        ida.setTiempoReal(false);

        listado.add(ida);


        if (listado != null && !listado.isEmpty()) {
            return listado;
        } else {
            return null;
        }

    }


    private static List<String> cargarDatos(HorarioTram datos) {

        HorarioItem horas = null;

        List<String> actualHoras = null;

        List<HorarioItem> datosCombinados = datos.getHorariosItemCombinados();

        if (datos != null && datosCombinados != null && !datosCombinados.isEmpty() && datosCombinados.size() > 1) {

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


        }

        return actualHoras;
    }


    public static String getLineasPorNombreDestino(HorarioTram datos, int paradaActual) {

        String lineas = "";

        if (datos.getDatosTransbordos() != null && !datos.getDatosTransbordos().isEmpty()) {

            String[] grupo1 = datos.getDatosTransbordos().get(0).getTrenesDestino().split(":");
            if (grupo1.length == 2) {
                String[] grupo2 = grupo1[1].trim().split(",");

                if (grupo2.length > 0) {
                    for (int i = 0; i < grupo2.length; i++) {

                        if (!lineas.equals("")) {
                            lineas += ",";
                        }

                        lineas += UtilidadesTRAM.getLineaHorario(grupo2[i].trim(), paradaActual);



                    }
                }

            }

        }



        return lineas;

    }

}
