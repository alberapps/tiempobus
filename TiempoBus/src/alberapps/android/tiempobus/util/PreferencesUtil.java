package alberapps.android.tiempobus.util;

import alberapps.android.tiempobus.database.DatosLineasDB;
import alberapps.java.util.Utilidades;
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
	
	
	
	public static String getUpdateInfo(Context context){
		
		SharedPreferences preferenciasAlertas = null;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			preferenciasAlertas = context.getSharedPreferences("prefupdate", Context.MODE_MULTI_PROCESS);
		
		}else{
			
			preferenciasAlertas = context.getSharedPreferences("prefupdate", 0);
			
		}
		
		
		String aviso = preferenciasAlertas.getString("update", "");
	
		if(aviso.equals("true")){
			return "";
		}
		
		
		return aviso;
		
	}
	
	public static String getUpdateIgnorarInfo(Context context){
		
		SharedPreferences preferenciasAlertas = null;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			preferenciasAlertas = context.getSharedPreferences("prefupdate", Context.MODE_MULTI_PROCESS);
		
		}else{
			
			preferenciasAlertas = context.getSharedPreferences("prefupdate", 0);
			
		}
		
		
		String aviso = preferenciasAlertas.getString("ignorar", "");
	
		
		return aviso;
		
	}
	
	
	public static void clearUpdateInfo(Context context){
		
		SharedPreferences preferenciasAlertas = null;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			preferenciasAlertas = context.getSharedPreferences("prefupdate", Context.MODE_MULTI_PROCESS);
		
		}else{
			
			preferenciasAlertas = context.getSharedPreferences("prefupdate", 0);
			
		}
		
		//Quitar info de la alarma
	    SharedPreferences.Editor editor = preferenciasAlertas.edit();
		editor.putString("update", "");
		editor.putString("ignorar", "");
		editor.commit();
		
	}
	
	public static void putUpdateInfo(Context context, String info, String ignorar){
		
		SharedPreferences preferenciasAlertas = null;
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		
			preferenciasAlertas = context.getSharedPreferences("prefupdate", Context.MODE_MULTI_PROCESS);
		
		}else{
			
			preferenciasAlertas = context.getSharedPreferences("prefupdate", 0);
			
		}
		
		//Quitar info de la alarma
	    SharedPreferences.Editor editor = preferenciasAlertas.edit();
	    
	    if(info.equals("true")){
	    	info = Utilidades.getFechaControl();
	    }
	    
	    editor.putString("update", info);
	    
	    
	    
	    editor.putString("ignorar", ignorar);
		editor.commit();
		
	}
	
	
	
}
