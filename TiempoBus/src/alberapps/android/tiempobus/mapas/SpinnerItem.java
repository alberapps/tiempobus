package alberapps.android.tiempobus.mapas;

public class SpinnerItem {

	int id;
	String descripcion;
	
	public SpinnerItem(int idP, String descripcionP) {
		
		id= idP;
		descripcion = descripcionP;
		
	}
	
	@Override
	public String toString() {
		
		return descripcion;
	}

	public int getId() {
		return id;
	}
	
	

}
