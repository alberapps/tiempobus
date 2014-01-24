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

import com.google.android.gms.drive.DriveApi.ContentsResult;

/**
 * 
 * Gestion de copias de seguridad
 * 
 * 
 */
public class DatosDriveBackup {

	/**
	 * Exportar la base de datos
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
	/*public static boolean recuperar(boolean respaldo) {
		if (isSDCARDMounted()) {

			// Copia de respaldo para posible fallo
			exportar(true);

			FileInputStream fileEXIE = null;
			FileOutputStream baseDatosE = null;

			boolean control = false;

			try {
				File fileEx = null;

				// Copia de recuperacion
				if (!respaldo) {
					fileEx = new File(Environment.getExternalStorageDirectory() + "/Android/data/alberapps.android.tiempobus/backup/", "tiempoBusDB.db");
				} else {
					fileEx = new File(Environment.getExternalStorageDirectory() + "/Android/data/alberapps.android.tiempobus/backup/", "tiempoBusDB.restore.db");
				}

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

				// Recuperar respaldo
				recuperar(true);

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

		} else {
			return false;
		}

	}
*/
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
			// No valida
			return false;
		} finally {
			sqlDb.close();
			cursor.close();
		}

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

	private static void copyFileI(InputStream in, FileOutputStream out) throws IOException {

		byte[] buffer = new byte[1024];
		int read;

		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}

	}

	private static void copyFileDrive(FileInputStream in, OutputStream out) throws IOException {

		byte[] buffer = new byte[1024];
		int read;

		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}

	}

	/**
	 * tarjeta SD disponible
	 * 
	 * @return boolean
	 */
	private static boolean isSDCARDMounted() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

}