package alberapps.android.tiempobuswidgets;

import java.util.List;

import alberapps.java.datos.Datos;
import alberapps.java.datos.GestionarDatos;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * 
 * 
 * 
 */
public class EliminarDatoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.eliminar);

		// Get the intent that started this activity
		Intent intent = getIntent();
		// Uri data = intent.getData();

		// if (intent.getType().equals("text/plain")) {
		// Handle intents with text ...

		// }

		final int datoEliminar = intent.getIntExtra("DATO", -1);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		final SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);

		// boton parada
		Button botonAceptar = (Button) findViewById(R.id.aceptar_alta);
		botonAceptar.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View arg0) {

				if (datoEliminar >= 0) {

					List<Datos> lineasParada = GestionarDatos.listaDatos(preferencias.getString("lineas_parada", "24,2902;10,2902"));

					lineasParada.remove(datoEliminar);

					String nuevo = GestionarDatos.getStringDeLista(lineasParada);

					SharedPreferences.Editor editor = preferencias.edit();
					editor.putString("lineas_parada", nuevo);
					editor.commit();

					Log.d("tag", " eliminado: " + datoEliminar);

					final String mostrar = getString(R.string.eliminar_ok);
					Toast.makeText(getApplicationContext(), mostrar, Toast.LENGTH_SHORT).show();
					
					actualizarWidget();

				}

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
