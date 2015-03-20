package alberapps.java.tram.horarios;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by albert on 07/03/15.
 */
public class TablaHoras {

    private LinkedHashMap<String, List<String>> datosHoras;


    public LinkedHashMap<String, List<String>> getDatosHoras() {
        return datosHoras;
    }

    public void setDatosHoras(LinkedHashMap<String, List<String>> datosHoras) {
        this.datosHoras = datosHoras;
    }
}
