/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2014 Alberto Montiel
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
package alberapps.java.actualizador;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import alberapps.java.util.Conectividad;

/**
 * Descarga de actualizaciones de la base de datos
 */
public class DescargarActualizaBD {

    public static final String URL_CONTROL_ACTUALIZA = "https://github.com/alberapps/tiempobus/raw/master/TiempoBus/res/raw/infoupdate";

    public static final String URL_PRECARGA_INFOLINEAS = "https://github.com/alberapps/tiempobus/raw/master/TiempoBus/res/raw/precargainfolineas";
    public static final String URL_PRECARGA_INFOLINEAS_RECORRIDO_1 = "https://github.com/alberapps/tiempobus/raw/master/TiempoBus/res/raw/precargainfolineasrecorrido";
    public static final String URL_PRECARGA_INFOLINEAS_RECORRIDO_2 = "https://github.com/alberapps/tiempobus/raw/master/TiempoBus/res/raw/precargainfolineasrecorrido2";

    public static final String PRECARGA_INFOLINEAS = "precargainfolineas_dw";
    public static final String PRECARGA_INFOLINEAS_RECORRIDO_1 = "precargainfolineasrecorrido_dw";
    public static final String PRECARGA_INFOLINEAS_RECORRIDO_2 = "precargainfolineasrecorrido2_dw";

    public static final String RUTA_BACKUP = "/data/alberapps.android.tiempobus/backup/";

    public static final String BD_DESCARGA = "descarga";

    /**
     * Iniciar el proceso de actualizacion
     *
     * @return boolean
     */
    public static boolean iniciarActualizacion() {

        boolean resultado = descargarArchivos();

        return resultado;

    }

    /**
     * Descargar todos los archivos de actualizacion
     *
     * @return boolean
     */
    public static boolean descargarArchivos() {

        // precargainfolineas
        if (descargarArchivoActualizacion(URL_PRECARGA_INFOLINEAS, PRECARGA_INFOLINEAS) &&
                // precargainfolineasrecorrido
                descargarArchivoActualizacion(URL_PRECARGA_INFOLINEAS_RECORRIDO_1, PRECARGA_INFOLINEAS_RECORRIDO_1) &&
                // precargainfolineasrecorrido2
                descargarArchivoActualizacion(URL_PRECARGA_INFOLINEAS_RECORRIDO_2, PRECARGA_INFOLINEAS_RECORRIDO_2)) {

            return true;

        } else {
            return false;
        }

    }

    /**
     * Descargar archivo
     *
     * @param url
     * @param archivo
     * @return boolean
     */
    public static boolean descargarArchivoActualizacion(String url, String archivo) {

        boolean resultado = false;

        InputStream is = null;

        FileOutputStream fileExport = null;

        try {

            is = Conectividad.conexionGetIsoStream(url);

            if (is != null) {

                // Copiar fichero al sistema de archivos
                // directorio de memoria interna
                File directorio = new File(Environment.getDataDirectory() + RUTA_BACKUP);
                directorio.mkdirs();

                File fileEx = null;
                fileEx = new File(Environment.getDataDirectory() + RUTA_BACKUP, archivo);

                fileEx.createNewFile();

                fileExport = new FileOutputStream(fileEx);

                copyFileI(is, fileExport);

                fileExport.flush();

                resultado = true;

            } else {

                resultado = false;

            }

        } catch (Exception e) {

            resultado = false;

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {

            }

            if (fileExport != null) {
                try {

                    fileExport.close();

                    fileExport = null;

                } catch (IOException e) {

                }
            }

        }

        return resultado;

    }

    /**
     * Borrar archivos de actualizacion
     */
    public static void borrarArchivosLineas() {

        File file1 = new File(Environment.getDataDirectory() + RUTA_BACKUP, PRECARGA_INFOLINEAS);

        //Borrar
        file1.delete();

    }


    /**
     * Borrar archivos de actualizacion
     */
    public static void borrarArchivosRecorridos() {

        File file2 = new File(Environment.getDataDirectory() + RUTA_BACKUP, PRECARGA_INFOLINEAS_RECORRIDO_1);
        File file3 = new File(Environment.getDataDirectory() + RUTA_BACKUP, PRECARGA_INFOLINEAS_RECORRIDO_2);

        //Borrar
        file2.delete();
        file3.delete();

    }


    /**
     * Copiar archivo
     *
     * @param in
     * @param out
     * @throws IOException
     */
    private static void copyFileI(InputStream in, FileOutputStream out) throws IOException {

        byte[] buffer = new byte[1024];
        int read;

        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

    }

    /**
     * inputStream infolineas
     *
     * @return is
     */
    public static InputStream inputStreamInfolineas() {

        FileInputStream fileEXIE = null;

        File fileEx = null;

        try {

            fileEx = new File(Environment.getDataDirectory() + RUTA_BACKUP, PRECARGA_INFOLINEAS);

            fileEXIE = new FileInputStream(fileEx);

        } catch (IOException e) {

        }

        return fileEXIE;

    }

    /**
     * inputStream infolineasrecorrido1
     *
     * @return is
     */
    public static InputStream inputStreamInfolineasRecorrido1() {

        FileInputStream fileEXIE = null;

        File fileEx = null;

        try {

            fileEx = new File(Environment.getDataDirectory() + RUTA_BACKUP, PRECARGA_INFOLINEAS_RECORRIDO_1);

            fileEXIE = new FileInputStream(fileEx);

        } catch (IOException e) {

        }

        return fileEXIE;

    }

    /**
     * nputStream infolineasrecorrido2
     *
     * @return is
     */
    public static InputStream inputStreamInfolineasRecorrido2() {

        FileInputStream fileEXIE = null;

        File fileEx = null;

        try {

            fileEx = new File(Environment.getDataDirectory() + RUTA_BACKUP, PRECARGA_INFOLINEAS_RECORRIDO_2);

            fileEXIE = new FileInputStream(fileEx);

        } catch (IOException e) {

        }

        return fileEXIE;

    }


    public static String controlActualizacion() {

        String actualizar = null;

        InputStream is = null;

        try {

            is = Conectividad.conexionGetUtf8Stream(URL_CONTROL_ACTUALIZA);

            if (is != null) {

                BufferedReader input = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));


                String l = "";


                int linea = 0;

                while ((l = input.readLine()) != null) {

                    //Si actualizar
                    if (linea == 0 && !l.equals("true")) {
                        break;
                    } else if (linea == 1) {

                        actualizar = l;

                    } else if (linea > 1) {
                        break;
                    }

                    linea++;

                }


            } else {
                actualizar = null;
            }

        } catch (Exception e) {

            actualizar = null;

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {

            }
        }

        return actualizar;
    }

}
