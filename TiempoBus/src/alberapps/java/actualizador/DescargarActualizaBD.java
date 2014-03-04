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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.protocol.HTTP;

import alberapps.java.tam.lineas.DatosLinea;
import alberapps.java.util.Conectividad;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;


/**
 * 
 * Procesa los datos recuperados de las lineas
 * 
 */
public class DescargarActualizaBD {

	public static final String URL_DATOS = "https://raw.github.com/alberapps/tiempobus/master/TiempoBus/res/raw/precargainfolineas";

	
	/**
	 * Parsear datos lineas
	 * 
	 * @param url
	 * @return
	 */
	public static void descargarArchivo() {

		
		

		InputStream is = null;

		FileOutputStream fileExport = null;
		
		try {

			is = Conectividad.conexionGetUtf8Stream(URL_DATOS);

			if (is != null) {

				
				
				// Copiar fichero al sistema de archivos
				// directorio de memoria interna
				File directorio = new File(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/backup/");
				directorio.mkdirs();
				
				File fileEx = null;
				fileEx = new File(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/backup/", "precargainfolineas_dw");

				fileEx.createNewFile();

				fileExport = new FileOutputStream(fileEx);
				
				copyFileI(is, fileExport);

				fileExport.flush();
				

			} else {
				
			}

		} catch (Exception e) {

			
			
		} finally {
			try {
				if(is != null){
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
	
}
