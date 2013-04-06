package alberapps.java.datos;

import java.util.ArrayList;
import java.util.List;

public class GestionarDatos {

	public static List<Datos> listaDatos(String lista) {

		String[] listaS = lista.split(";");

		List<Datos> datosNuevo = new ArrayList<Datos>();
		Datos dato = null;

		for (int i = 0; i < listaS.length; i++) {

			String[] datos = listaS[i].split(",");

			dato = new Datos();
			dato.setLinea(datos[0]);
			dato.setParada(datos[1]);

			datosNuevo.add(dato);

		}

		return datosNuevo;

	}

	public static String getStringDeLista(List<Datos> lista) {

		StringBuffer datos = new StringBuffer();

		for (int i = 0; i < lista.size(); i++) {

			if (datos.length() > 0) {
				datos.append(";");
			}

			datos.append(lista.get(i).getLinea());

			datos.append(",");

			datos.append(lista.get(i).getParada());

		}

		return datos.toString();

	}
	
	
	
	

}
