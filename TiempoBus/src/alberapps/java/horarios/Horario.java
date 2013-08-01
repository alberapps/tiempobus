package alberapps.java.horarios;

import java.util.ArrayList;
import java.util.List;

public class Horario {

	private String linkHorario = "";
	
	private String tituloHorario = "";
	
	private List<String> horarios = new ArrayList<String>();

	public String getLinkHorario() {
		return linkHorario;
	}

	public void setLinkHorario(String linkHorario) {
		this.linkHorario = linkHorario;
	}

	public String getTituloHorario() {
		return tituloHorario;
	}

	public void setTituloHorario(String tituloHorario) {
		this.tituloHorario = tituloHorario;
	}

	public List<String> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<String> horarios) {
		this.horarios = horarios;
	}
	
	

}
