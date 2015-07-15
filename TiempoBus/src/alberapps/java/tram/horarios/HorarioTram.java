package alberapps.java.tram.horarios;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by albert on 06/03/15.
 */
public class HorarioTram {

    private String estacionOrigen;

    private String estacionDestino;

    private String horas;

    private String dia;

    private String duracion;

    private String tipoBillete;

    private String transbordos;

    private List<String> lineasTransbordos;

    private List<DatoTransbordo> datosTransbordos;

    public List<String> getLineasTransbordos() {
        return lineasTransbordos;
    }

    public void setLineasTransbordos(List<String> lineasTransbordos) {
        this.lineasTransbordos = lineasTransbordos;
    }





    public List<DatoTransbordo> getDatosTransbordos() {
        return datosTransbordos;
    }

    public void setDatosTransbordos(List<DatoTransbordo> datosTransbordos) {
        this.datosTransbordos = datosTransbordos;
    }





    public String getEstacionOrigen() {
        return estacionOrigen;
    }

    public void setEstacionOrigen(String estacionOrigen) {
        this.estacionOrigen = estacionOrigen;
    }

    public String getEstacionDestino() {
        return estacionDestino;
    }

    public void setEstacionDestino(String estacionDestino) {
        this.estacionDestino = estacionDestino;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getTipoBillete() {
        return tipoBillete;
    }

    public void setTipoBillete(String tipoBillete) {
        this.tipoBillete = tipoBillete;
    }

    public String getTransbordos() {
        return transbordos;
    }

    public void setTransbordos(String transbordos) {
        this.transbordos = transbordos;
    }


    public List<HorarioItem> getHorariosItemCombinados(){

        List<HorarioItem> listado = new ArrayList<HorarioItem>();

        if(datosTransbordos != null && !datosTransbordos.isEmpty() && !datosTransbordos.get(0).isErrorServicio() && !datosTransbordos.get(0).isSinDatos()) {

            HorarioItem info = new HorarioItem();
            info.setInfoRecorrido(new ArrayList<String>());

            String[] duracion1 = duracion.split(":");
            String[] tipoBillete1 = tipoBillete.split(":");

            if(duracion1.length > 1) {
                info.getInfoRecorrido().add(duracion1[1].trim());
            }
            if(tipoBillete1.length > 1) {
                info.getInfoRecorrido().add(tipoBillete1[1].trim());
            }



            if(lineasTransbordos != null && !lineasTransbordos.isEmpty()){

                StringBuffer transbordos = new StringBuffer("");

                for(int i = 0;i<lineasTransbordos.size();i++){

                    if(i > 0){
                        transbordos.append(" -> ");
                    }

                    transbordos.append(lineasTransbordos.get(i));

                }

                info.getInfoRecorrido().add(transbordos.toString());

            }



            listado.add(info);

        }

        for(int i = 0; i < datosTransbordos.size();i++){

            List<HorarioItem> items = datosTransbordos.get(i).getHorariosItem();

            for(int j = 0;j<items.size();j++){
                if(lineasTransbordos != null && !lineasTransbordos.isEmpty() && lineasTransbordos.size() > i) {
                    items.get(j).setLinea(lineasTransbordos.get(i));
                }else{
                    items.get(j).setLinea("");
                }
            }

            listado.addAll(items);

        }


        return listado;


    }





}
