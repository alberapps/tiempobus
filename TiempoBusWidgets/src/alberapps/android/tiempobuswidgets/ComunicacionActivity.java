package alberapps.android.tiempobuswidgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
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

	    setContentView(R.layout.comunicacion);

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
	    
	    
		actualizarWidget();
		
	    Toast.makeText(this, "prueba: " + datos, Toast.LENGTH_SHORT).show();
	    
	 // boton parada
	 		Button botonAceptar = (Button) findViewById(R.id.aceptar_alta);
	 		botonAceptar.setOnClickListener(new Button.OnClickListener() {
	 			public void onClick(View arg0) {
	 				finish();
	 			}
	    
	    
	 		});
	 		
	}
	
	
	private void actualizarWidget(){
		
		AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());
		
		//int[] awid= awm.getAppWidgetIds(new ComponentName(this,TiemposWidgetProvider.class));
		
		//if(awid.length > 0){
			
			Intent updateIntent = new Intent(getApplicationContext(), TiemposWidgetProvider.class);
			updateIntent.setAction(TiemposWidgetProvider.REFRESH_ACTION);
			
			getApplicationContext().sendBroadcast(updateIntent);
			
			//new TiemposWidgetProvider().actualizar(context, intent).onUpdate(this, awm, awid);
		//}
		
		
		
		
	}

}
