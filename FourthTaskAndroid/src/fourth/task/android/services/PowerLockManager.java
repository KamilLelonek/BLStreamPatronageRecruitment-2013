package fourth.task.android.services;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class PowerLockManager {
	private static PowerManager.WakeLock wakeLock;
	
	/**
	 * acquireLock method locks CPU from turning off to keep downloading data
	 * even when screen is inactive.
	 * 
	 * @return if lock is held what means that data is being updated at the
	 *         moment
	 */
	public static synchronized boolean acquireLock(Context context) {
		if (wakeLock == null) {
			PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
		}
		if (!wakeLock.isHeld()) {
			wakeLock.acquire();
			return true;
		}
		
		Log.d(ServiceManager.SERVICE_LOG_TAG, "PowerLockReceiver: WakeLock acquired!");
		return false;
	}
	
	/**
	 * relaseLock causes that CPU lock is released what means that all data has
	 * been download and CPU may become dormant.
	 */
	public static synchronized void relaseLock() {
		if (wakeLock != null) {
			if (wakeLock.isHeld()) {
				wakeLock.release();
			}
			Log.d(ServiceManager.SERVICE_LOG_TAG, "PowerLockReceiver: WakeLock relased!");
		}
	}
}