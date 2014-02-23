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
package alberapps.java.data.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.drive.DriveApi.ContentsResult;

/**
 * 
 * Gestion de copias de seguridad en Drive
 * 
 * 
 */
public class DatosDriveBackup {

	/**
	 * Exportar la base de datos a Drive
	 * 
	 * @return boolean
	 */
	public static boolean exportar(ContentsResult contentsResult) {

		boolean control = false;

		FileInputStream baseDatos = null;

		try {

			OutputStream outputStream = contentsResult.getContents().getOutputStream();

			// outputStream.write("Hello world".getBytes());

			// base de datos
			baseDatos = new FileInputStream(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/databases/zgzbus.db");

			copyFileDrive(baseDatos, outputStream);

			control = true;

		} catch (IOException e) {
			control = false;
		} finally {

			if (baseDatos != null) {
				try {
					baseDatos.close();
					baseDatos = null;
				} catch (IOException e) {

				}
			}

		}

		return control;

	}

	/**
	 * Sobreescribir la base de datos
	 * 
	 * @return boolean
	 */
	public static boolean recuperar(ContentsResult contentsResult) {

		// Copia de respaldo para posible fallo
		exportarRespaldo();

		InputStream fileDriveStream = null;
		FileOutputStream baseDatosE = null;
		FileInputStream fileEXIE = null;

		boolean control = false;

		FileOutputStream fileExport = null;

		try {

			// Copiar fichero de drive a la memoria para procesar
			// directorio de memoria interna
			File directorio = new File(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/backup/");
			directorio.mkdirs();

			File fileEx = null;
			fileEx = new File(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/backup/", "tiempoBusDB.drive.db");

			fileEx.createNewFile();

			fileExport = new FileOutputStream(fileEx);

			// Copiar desde drive a sd

			fileDriveStream = contentsResult.getContents().getInputStream();

			copyFileI(fileDriveStream, fileExport);

			fileExport.flush();

			if (!fileEx.exists()) {
				return false;
			}

			if (!verificarArchivoBD(fileEx)) {
				return false;
			}

			// Copiar de SD a la base de datos
			File baseDatos = new File(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/databases/zgzbus.db");

			baseDatos.createNewFile();

			baseDatosE = new FileOutputStream(baseDatos);

			fileEXIE = new FileInputStream(fileEx);

			copyFileI(fileEXIE, baseDatosE);

			baseDatosE.flush();

			control = true;

		} catch (Exception e) {

			// Recuperar respaldo
			recuperarRespaldo();

			control = false;
		} finally {
			if (fileDriveStream != null) {
				try {
					fileDriveStream.close();
					fileDriveStream = null;
				} catch (IOException e) {

				}
			}

			if (baseDatosE != null) {
				try {
					baseDatosE.close();
					baseDatosE = null;
				} catch (IOException e) {

				}
			}

			if (fileExport != null) {
				try {

					fileExport.close();

					fileExport = null;

				} catch (IOException e) {

				}
			}

			if (fileEXIE != null) {
				try {
					fileEXIE.close();
					fileEXIE = null;
				} catch (IOException e) {

				}
			}

		}

		return control;

	}

	/**
	 * Comprobaciones
	 * 
	 * @param db
	 * @return boolean
	 */
	public static boolean verificarArchivoBD(File db) {

		SQLiteDatabase sqlDb = null;
		Cursor cursor = null;

		try {
			sqlDb = SQLiteDatabase.openDatabase(db.getPath(), null, SQLiteDatabase.OPEN_READONLY);

			cursor = sqlDb.query(true, "favoritos", null, null, null, null, null, null, null);

			String[] columnas = { "poste", "titulo", "descripcion" };

			String s;
			for (int i = 0; i < columnas.length; i++) {
				s = columnas[i];
				cursor.getColumnIndexOrThrow(s);
			}

		} catch (Exception e) {

			Log.e("drive", "Error al verificar la base de datos");

			e.printStackTrace();

			// No valida
			return false;
		} finally {
			sqlDb.close();
			cursor.close();
		}

		Log.d("drive", "Base de datos OK");

		return true;
	}

	/**
	 * Copiar archivo
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private static void copyFile(FileInputStream in, FileOutputStream out) throws IOException {

		byte[] buffer = new byte[1024];
		int read;

		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
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

	/**
	 * Copiar archivo
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private static void copyFileDrive(FileInputStream in, OutputStream out) throws IOException {

		byte[] buffer = new byte[1024];
		int read;

		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}

	}

	/**
	 * Copia de respaldo por si hay error en el proceso
	 * 
	 * @return boolean
	 */
	public static boolean exportarRespaldo() {

		boolean control = false;

		// directorio al que copiar respaldo
		File directorio = new File(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/backup/");
		directorio.mkdirs();

		FileInputStream baseDatos = null;
		FileOutputStream fileExport = null;

		try {

			// fichero de db
			File fileEx = null;

			fileEx = new File(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/backup/", "tiempoBusDB.restore.db");

			fileEx.createNewFile();

			fileExport = new FileOutputStream(fileEx);

			// base de datos
			baseDatos = new FileInputStream(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/databases/zgzbus.db");

			copyFile(baseDatos, fileExport);

			fileExport.flush();

			control = true;

		} catch (IOException e) {
			control = false;
		} finally {

			if (baseDatos != null) {
				try {
					baseDatos.close();
					baseDatos = null;
				} catch (IOException e) {

				}
			}
			if (fileExport != null) {
				try {

					fileExport.close();

					fileExport = null;

				} catch (IOException e) {

				}
			}

		}

		return control;

	}

	/**
	 * Recuperar la copia de respaldo
	 * 
	 * @return boolean
	 */
	public static boolean recuperarRespaldo() {

		FileInputStream fileEXIE = null;
		FileOutputStream baseDatosE = null;

		boolean control = false;

		try {
			File fileEx = null;

			fileEx = new File(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/backup/", "tiempoBusDB.restore.db");

			if (!fileEx.exists()) {
				return false;
			}

			if (!verificarArchivoBD(fileEx)) {
				return false;
			}

			fileEXIE = new FileInputStream(fileEx);

			File baseDatos = new File(Environment.getDataDirectory() + "/data/alberapps.android.tiempobus/databases/zgzbus.db");

			baseDatos.createNewFile();

			baseDatosE = new FileOutputStream(baseDatos);

			copyFile(fileEXIE, baseDatosE);

			control = true;
		} catch (IOException e) {

			control = false;
		} finally {
			if (fileEXIE != null) {
				try {
					fileEXIE.close();
					fileEXIE = null;
				} catch (IOException e) {

				}
			}

			if (baseDatosE != null) {
				try {
					baseDatosE.close();
					baseDatosE = null;
				} catch (IOException e) {

				}
			}

		}

		return control;

	}

}