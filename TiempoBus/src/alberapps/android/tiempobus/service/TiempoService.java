package alberapps.android.tiempobus.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class TiempoService extends IntentService {

	 private NotificationManager mNM;
	    private Method mSetForeground;
	    private Method mStartForeground;
	    private Method mStopForeground;
	    private Object[] mSetForegroundArgs = new Object[1];
	    private Object[] mStartForegroundArgs = new Object[2];
	    private Object[] mStopForegroundArgs = new Object[1];
	
	
	    void invokeMethod(Method method, Object[] args) {
	        try {
	            method.invoke(this, args);
	        } catch (InvocationTargetException e) {
	            // Should not happen.
	            Log.w("ApiDemos", "Unable to invoke method", e);
	        } catch (IllegalAccessException e) {
	            // Should not happen.
	            Log.w("ApiDemos", "Unable to invoke method", e);
	        }
	    }
	    
	/**
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public TiempoService() {
		super("TiempoIntentService");
	}

	
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns,
	 * IntentService stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		// Normally we would do some work here, like download a file.
		// For our sample, we just sleep for 5 seconds.
		long endTime = System.currentTimeMillis() + 5 * 1000;
		while (System.currentTimeMillis() < endTime) {
			synchronized (this) {
				try {
					Toast.makeText(this, "Servicio espera", Toast.LENGTH_SHORT).show();
					wait(endTime - System.currentTimeMillis());
					
					stopSelf();
					
				} catch (Exception e) {
				}
			}
		}
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "Servicio destruido", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
	
	
	 /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        // If we have the new startForeground API, then use it.
        if (mStartForeground != null) {
            mStartForegroundArgs[0] = Integer.valueOf(id);
            mStartForegroundArgs[1] = notification;
            invokeMethod(mStartForeground, mStartForegroundArgs);
            return;
        }
        
        // Fall back on the old API.
        mSetForegroundArgs[0] = Boolean.TRUE;
        invokeMethod(mSetForeground, mSetForegroundArgs);
        mNM.notify(id, notification);
    }
	
    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        // If we have the new stopForeground API, then use it.
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mStopForeground, mStopForegroundArgs);
            return;
        }
        
        // Fall back on the old API.  Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        mNM.cancel(id);
        mSetForegroundArgs[0] = Boolean.FALSE;
        invokeMethod(mSetForeground, mSetForegroundArgs);
    }
	
}
