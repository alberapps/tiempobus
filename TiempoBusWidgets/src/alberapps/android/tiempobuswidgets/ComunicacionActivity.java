package alberapps.android.tiempobuswidgets;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * 
 * 
 * 
 */
public class ComunicacionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    //setContentView(R.layout.main);

	    // Get the intent that started this activity
	    Intent intent = getIntent();
	    //Uri data = intent.getData();

	   //if (intent.getType().equals("text/plain")) {
	        // Handle intents with text ...
		   
		   
		   
	    //}
	    
	    String datos = intent.getExtras().getString("datos_linea");
	    
	    
	    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
		
		String lineasParada = preferencias.getString("lineas_parada", "");
		
		String nuevo = lineasParada;
		
		if(!nuevo.equals("")){
			nuevo = nuevo.concat(";");
		}
		
		nuevo = nuevo.concat(datos);
		
		SharedPreferences.Editor editor = preferencias.edit();
		editor.putString("lineas_parada", nuevo);
		editor.commit();
	    
	    
	    Toast.makeText(this, "prueba: " + datos, Toast.LENGTH_SHORT).show();
	    
	    
	}

}
