package alberapps.java.tram.avisos;

import java.util.List;

import alberapps.java.noticias.tw.TwResultado;

/**
 * Created by albert on 07/04/16.
 */
public class AvisosTram {

    private List<TwResultado> avisosTw;

    private List<Aviso> avisosWeb;


    public List<TwResultado> getAvisosTw() {
        return avisosTw;
    }

    public void setAvisosTw(List<TwResultado> avisosTw) {
        this.avisosTw = avisosTw;
    }

    public List<Aviso> getAvisosWeb() {
        return avisosWeb;
    }

    public void setAvisosWeb(List<Aviso> avisosWeb) {
        this.avisosWeb = avisosWeb;
    }
}
