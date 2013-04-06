package alberapps.android.tiempobus.database;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class BuscadorLineasObserver extends ContentObserver{

	public BuscadorLineasObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}
	
	// Implement the onChange(boolean) method to delegate the change notification to
	 // the onChange(boolean, Uri) method to ensure correct operation on older versions
	 // of the framework that did not have the onChange(boolean, Uri) method.
	 @Override
	 public void onChange(boolean selfChange) {
	     onChange(selfChange, null);
	 }

	 // Implement the onChange(boolean, Uri) method to take advantage of the new Uri argument.
	 
	 public void onChange(boolean selfChange, Uri uri) {
	     // Handle change.
	 }
	

}
