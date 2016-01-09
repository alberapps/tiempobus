/**
 * TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 * Copyright (C) 2012 Alberto Montiel
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
package alberapps.java.util;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import alberapps.android.tiempobus.util.UtilidadesUI;

/**
 * Clase de utilidades
 */
public class Utilidades {

    /**
     * @param inputStream
     * @return
     */
    public static String obtenerStringDeStream(InputStream inputStream) {

        String datos = "";

        Scanner s = new Scanner(inputStream, "ISO-8859-1").useDelimiter("\\A");
        datos = s.hasNext() ? s.next() : "";

        return datos;
    }

    /**
     * @param inputStream
     * @return
     */
    public static String obtenerStringDeStreamUTF8(InputStream inputStream) {

        String datos = "";

        Scanner s = new Scanner(inputStream, "UTF-8").useDelimiter("\\A");
        datos = s.hasNext() ? s.next() : "";

        return datos;
    }

    /**
     * Date desde string
     *
     * @param fecha
     * @return
     */
    public static Date getFechaDate(String fecha) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        Date fechaDate = null;

        if (fecha != null) {
            try {
                fechaDate = df.parse(fecha);

                return fechaDate;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    /**
     * Date desde string
     *
     * @param fecha
     * @return
     */
    public static Date getFechaDateCorta(String fecha) {

        DateFormat df = new SimpleDateFormat("dd/MM/yy", Locale.US);

        Date fechaDate = null;

        if (fecha != null && !fecha.equals("")) {
            try {
                fechaDate = df.parse(fecha);

                return fechaDate;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    /**
     * String desde date
     *
     * @param fecha
     * @return
     */
    public static String getFechaString(Date fecha) {

        DateFormat df = new SimpleDateFormat("EEE dd MMM yyyy HH:mm", UtilidadesUI.getLocaleUsuario());

        String fechaString = null;

        if (fecha != null) {

            fechaString = df.format(fecha);

            return fechaString;

        }

        return null;

    }

    /**
     * String desde date
     *
     * @param fecha
     * @return
     */
    public static String getHoraString(Date fecha) {

        DateFormat df = new SimpleDateFormat("HH:mm", UtilidadesUI.getLocaleUsuario());

        String fechaString = null;

        if (fecha != null) {

            fechaString = df.format(fecha);

            return fechaString;

        }

        return null;

    }

    /**
     * String desde date
     *
     * @param fecha
     * @return
     */
    public static String getFechaStringSinHora(Date fecha) {

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, UtilidadesUI.getLocaleUsuario());

        String fechaString = null;

        if (fecha != null) {

            fechaString = df.format(fecha);

            return fechaString;

        }

        return null;

    }

    /**
     * String desde date
     *
     * @param fecha
     * @return
     */
    public static String getFechaSQL(Date fecha) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        String fechaString = null;

        if (fecha != null) {

            fechaString = df.format(fecha);

            return fechaString;

        }

        return null;

    }

    public static String getFechaControl() {

        Date fecha = new Date();

        DateFormat df = new SimpleDateFormat("ddMMyyyy", Locale.US);

        String fechaString = null;

        if (fecha != null) {

            fechaString = df.format(fecha);

            return fechaString;

        }

        return null;

    }


    public static String getFechaES(Date fecha) {

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        String fechaString = null;

        if (fecha != null) {

            fechaString = df.format(fecha);

            return fechaString;

        }

        return null;

    }

    /**
     * Fecha de hoy con la hora indicada
     *
     * @param hora
     * @return
     */
    public static Date getFechaActualConHora(String hora) {

        String[] horas = hora.split(":");

        Calendar calendar = Calendar.getInstance(UtilidadesUI.getLocaleUsuario());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horas[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(horas[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();

    }

    /**
     * Diferencia en minutos entre dos fechas
     *
     * @param fecha1
     * @param fecha2
     * @return
     */
    public static String getMinutosDiferencia(Date fecha1, Date fecha2) {

        /*long secs = (fecha2.getTime() - fecha1.getTime()) / 1000;
        long hours = secs / 3600;
        secs = secs % 3600;
        long mins = secs / 60;
        secs = secs % 60;*/

        long diff = fecha2.getTime() - fecha1.getTime();
        long diffMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);

        return Long.toString(diffMinutes);

    }

    /**
     * String desde date
     *
     * @param fechaControl
     * @param fechaPreferencias
     * @return
     */
    public static boolean isFechaControl(String fechaControl, String fechaPreferencias) {

        Date fechaDateControl = null;
        Date fechaDatePref = null;


        DateFormat df = new SimpleDateFormat("ddMMyyyy", Locale.US);


        if (fechaControl != null && !fechaControl.equals("") && fechaPreferencias != null && !fechaPreferencias.equals("")) {
            try {
                fechaDateControl = df.parse(fechaControl);
                fechaDatePref = df.parse(fechaPreferencias);

                if (fechaDateControl.after(fechaDatePref)) {
                    return true;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        return false;


    }

    /**
     * Aleatorio
     *
     * @return int
     */
    public static boolean ipRandom() {

        int min = 0;
        int max = 1;

        Random rand = new Random();

        int random = rand.nextInt((max - min) + 1) + min;

        Log.d("RANDOM", "RANDOM: " + random);

        if (random == 0) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * Escribir outputstream
     *
     * @param out
     * @param str
     * @throws IOException
     */
    public static void writeIt(OutputStream out, String str) throws IOException {

        out.write(str.getBytes("UTF-8"));

        out.flush();

    }

    /**
     * De string a stream
     *
     * @param str
     * @return
     */
    public static InputStream stringToStream(String str) {

        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        return stream;

    }

    /**
     * De string a stream
     *
     * @param str
     * @return
     */
    public static InputStream stringToStreamIso(String str) {

        InputStream stream = null;
        try {
            stream = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        return stream;

    }


    /**
     * Verificar si se recibe un archivo comprimido en zip
     *
     * @param is
     * @return boolean
     */
    public static boolean isZipFile(InputStream is) {

        boolean esZip = false;

        DataInputStream entrada = new DataInputStream(is);

        try {
            int verificar = entrada.readInt();

            //if(verificar == 0x504b0304){
            if (verificar == 1347093252) {
                esZip = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                entrada.close();

                is.reset();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        return esZip;
    }


    /**
     * Devuelve un inputstream del contenido del zip
     *
     * @param isZip
     * @return
     */
    public static InputStream zipToInputStream(InputStream isZip) {

        InputStream is = null;

        try {


            ZipInputStream zis = new ZipInputStream(isZip);

            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int count;
                while ((count = zis.read(buffer)) != -1) {
                    baos.write(buffer, 0, count);
                }
                String filename = ze.getName();

                if (filename.equals("doc.kml")) {

                    byte[] bytes = baos.toByteArray();
                    // do something with 'filename' and 'bytes'...

                    is = new ByteArrayInputStream(bytes);

                }

            }


        } catch (Exception e) {

            e.printStackTrace();


        }

        return is;

    }


    public static String DEFAULT_USERAGENT = "Mozilla/5.0 (Linux; Android 4.4.2; Android SDK built for x86 Build/KK) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";

    /**
     * User Agent por defecto
     *
     * @param contexto
     * @return
     */
    public static String getAndroidUserAgent(Context contexto) {

        String userAgent = "";

        try {

            if (Build.VERSION.SDK_INT >= 17) {
                userAgent = WebSettings.getDefaultUserAgent(contexto);
            } else {
                userAgent = new WebView(contexto).getSettings().getUserAgentString();
            }

        } catch (Exception e) {
            e.printStackTrace();

            //Para algunas versiones que dan error al recupera el useragent

            userAgent = DEFAULT_USERAGENT;

        }

        return userAgent;
    }


}
