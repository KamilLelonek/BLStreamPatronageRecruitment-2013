package fourth.task.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class PowerLockReceiver extends BroadcastReceiver {
	public static final String SERVICE_START_INTENT = "fourth.task.android.DOWNLOAD_DATA";
	
	private Intent networkServiceIntent;
	private static PowerManager.WakeLock wakeLock;
	
	@Override public void onReceive(Context context, Intent intent) {
		Log.d(ServiceManager.SERVICE_LOG_TAG, "PowerLockReceiver: Intent received!");
		
		if (networkServiceIntent == null) {
			networkServiceIntent = new Intent(context, WeatherService.class);
		}
		acquireLock(context);
		context.startService(networkServiceIntent);
	}
	
	public static synchronized void acquireLock(Context context) {
		if (wakeLock == null) {
			PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		}
		if (!wakeLock.isHeld()) {
			wakeLock.acquire();
		}
		
		Log.d(ServiceManager.SERVICE_LOG_TAG, "PowerLockReceiver: WakeLock acquired!");
	}
	
	public static synchronized void relaseLock() {
		if (wakeLock != null) {
			if (wakeLock.isHeld()) {
				wakeLock.release();
			}
			Log.d(ServiceManager.SERVICE_LOG_TAG, "PowerLockReceiver: WakeLock relased!");
		}
	}
}