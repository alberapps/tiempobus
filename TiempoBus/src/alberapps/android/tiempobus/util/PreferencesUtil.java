package alberapps.android.tiempobus.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class PreferencesUtil {

	/**
	 * Recuperar aviso
	 * @param context
	 * @return
	 */
	public static String getAlertaInfo(Context context){
		
		SharedPreferences preferenciasAlertas = null;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			preferenciasAlertas = context.getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);
		
		}else{
			
			preferenciasAlertas = context.getSharedPreferences("prefalertas", 0);
			
		}
		
		
		String aviso = preferenciasAlertas.getString("alerta", "");
	
		
		return aviso;
		
	}
	
	
	public static void clearAlertaInfo(Context context){
		
		SharedPreferences preferenciasAlertas = null;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			preferenciasAlertas = context.getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);
		
		}else{
			
			preferenciasAlertas = context.getSharedPreferences("prefalertas", 0);
			
		}
		
		//Quitar info de la alarma
	    SharedPreferences.Editor editor = preferenciasAlertas.edit();
		editor.putString("alerta", "");
		editor.commit();
		
	}
	
	public static void putAlertaInfo(Context context, String info){
		
		SharedPreferences preferenciasAlertas = null;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			preferenciasAlertas = context.getSharedPreferences("prefalertas", Context.MODE_MULTI_PROCESS);
		
		}else{
			
			preferenciasAlertas = context.getSharedPreferences("prefalertas", 0);
			
		}
		
		//Quitar info de la alarma
	    SharedPreferences.Editor editor = preferenciasAlertas.edit();
	    editor.putString("alerta", info);
		editor.commit();
		
	}
	
	
}
