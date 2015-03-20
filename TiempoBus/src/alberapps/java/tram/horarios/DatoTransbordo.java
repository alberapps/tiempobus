package alberapps.java.tram.horarios;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albert on 06/03/15.
 */
public class DatoTransbordo {


    private String datoPaso;

    private String trenesDestino;

    private TablaHoras tablaHoras;

    private boolean errorServicio;

    private boolean sinDatos;

    public boolean isSinDatos() {
        return sinDatos;
    }

    public void setSinDatos(boolean sinDatos) {
        this.sinDatos = sinDatos;
    }

    public boolean isErrorServicio() {
        return errorServicio;
    }

    public void setErrorServicio(boolean errorServicio) {
        this.errorServicio = errorServicio;
    }

    public TablaHoras getTablaHoras() {
        return tablaHoras;
    }

    public void setTablaHoras(TablaHoras tablaHoras) {
        this.tablaHoras = tablaHoras;
    }

    public String getDatoPaso() {
        return datoPaso;
    }

    public void setDatoPaso(String datoPaso) {
        this.datoPaso = datoPaso;
    }

    public String getTrenesDestino() {
        return trenesDestino;
    }

    public void setTrenesDestino(String trenesDestino) {
        this.trenesDestino = trenesDestino;
    }


    public List<HorarioItem> getHorariosItem(){

        List<HorarioItem> horarioList = null;

        if(isSinDatos()){
            horarioList = new ArrayList<HorarioItem>();
            HorarioItem horario = new HorarioItem();
            horario.setSinDatos(true);
            horarioList.add(horario);

        } else if(isErrorServicio()){
            horarioList = new ArrayList<HorarioItem>();
            HorarioItem horario = new HorarioItem();
            horario.setErrorServicio(true);
            horarioList.add(horario);
        }
        else if (getTablaHoras() != null && getTablaHoras().getDatosHoras() != null) {

            horarioList = new ArrayList<HorarioItem>();

            HorarioItem horarioItem = null;

            List<String> keys = new ArrayList<String>();
            keys.addAll(getTablaHoras().getDatosHoras().keySet());

            List<String> listaHoras = null;

            for (int i = 0; i < keys.size(); i++) {

                horarioItem = new HorarioItem();

                horarioItem.setDatoInfo(datoPaso);

                //Grupo
                horarioItem.setGrupoHora(keys.get(i));

                //Horas
                listaHoras = getTablaHoras().getDatosHoras().get(keys.get(i));

                StringBuffer sb = new StringBuffer("");

                for (int j = 0; j < listaHoras.size(); j++) {

                    if (listaHoras.get(j) != null && !listaHoras.get(j).equals("") && !listaHoras.get(j).equals("---")) {

                        if (j > 0) {
                            sb.append(" ");
                        }

                        sb.append(listaHoras.get(j));

                    }


                }

                horarioItem.setHoras(sb.toString());

                horarioList.add(horarioItem);


            }





        }


        return horarioList;

    }



}
