/**
 *  TiempoBus - Informacion sobre tiempos de paso de autobuses en Alicante
 *  Copyright (C) 2013 Alberto Montiel
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
package alberapps.android.tiempobus.database.historial;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Adaptador
 * 
 */
public class HistorialDB {
	public static final String AUTHORITY = "alberapps.android.tiempobus.historial.HistorialProvider";

	// This class cannot be instantiated
	private HistorialDB() {
	}

	/**
	 * Tabla de historial
	 */
	public static final class Historial implements BaseColumns {
		// This class cannot be instantiated
		private Historial() {
		}

		/**
		 * content:// estilo URL para esta tabla
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/historial");
		
		public static final Uri CONTENT_URI_ID_PARADA = Uri.parse("content://" + AUTHORITY + "/historial/parada");

		public static final String DEFAULT_SORT_ORDER = "fecha DESC";

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = "/vnd.alberapps.android.tiempobus.historial";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * note.
		 */
		public static final String CONTENT_ITEM_TYPE = "/vnd.alberapps.android.tiempobus.historial";

		/**
		 * Numero de parada
		 */
		public static final String PARADA = "parada";

		/**
		 * Titulo
		 */
		public static final String TITULO = "titulo";

		/**
		 * Descripcion
		 */
		public static final String DESCRIPCION = "descripcion";
		
		/**
		 * Fecha y hora
		 */
		public static final String FECHA = "fecha";

	}
}
