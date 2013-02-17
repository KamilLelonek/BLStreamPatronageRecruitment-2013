package fourth.task.android.services;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import fourth.task.android.PreferencesFragment;

public class ServiceManager extends BroadcastReceiver implements OnSharedPreferenceChangeListener {
	public static final String SERVICE_LOG_TAG = "SOA";
	public static final String SERVICE_START_INTENT = "fourth.task.android.APPLICATION_STARTED";
	
	private SharedPreferences sharedPreferences;
	private AlarmManager alarmManager;
	private PendingIntent callPowerLockReceiverPendingIntent;
	
	private boolean initialized;
	
	@Override public void onReceive(Context context, Intent intent) {
		Log.d(SERVICE_LOG_TAG, "ServiceManager: Intent received!");
		
		if (!initialized) {
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
			alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			
			/* Creating intent which will be broadcasted to invoke
			 * PowerLockReceiver in the future. */
			Intent powerLockReceiverIntent = new Intent(context, PowerLockReceiver.class);
			callPowerLockReceiverPendingIntent = PendingIntent.getBroadcast(context, 0, powerLockReceiverIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
			
			initialized = true;
		}
		
		if (isAutoRefreshEnabled()) {
			Log.d(SERVICE_LOG_TAG, "ServiceManager: Starting new refreshing cycle (first app start or on demand).");
			startWeatherUpdateRequest();
		}
		else {
			Log.d(SERVICE_LOG_TAG, "ServiceManager: One-shot update request.");
			context.sendBroadcast(new Intent(PowerLockReceiver.SERVICE_START_INTENT));
		}
	}
	
	private boolean isAutoRefreshEnabled() {
		return sharedPreferences.getBoolean(PreferencesFragment.PREFERENCE_AUTO_REFRESH_CONDITION, false);
	}
	
	@Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (PreferencesFragment.PREFERENCE_CYCLE_TIME.equals(key)
			|| PreferencesFragment.PREFERENCE_AUTO_REFRESH_CONDITION.equals(key)) {
			if (isAutoRefreshEnabled()) {
				/* Refreshing cycle time has been changed and cycling refreshing
				 * is still enabled so weather update request must be restarted
				 * to change its data update period. */
				restartWeatherUpdateRequest();
			}
		}
	}
	
	private void restartWeatherUpdateRequest() {
		cancelWeatherUpdateRequest();
		startWeatherUpdateRequest();
	}
	
	private void cancelWeatherUpdateRequest() {
		alarmManager.cancel(callPowerLockReceiverPendingIntent);
	}
	
	private void startWeatherUpdateRequest() {
		startWeatherUpdateRequest(getAutoRefreshTimeCycle());
	}
	
	private int getAutoRefreshTimeCycle() {
		int autoRefreshTimeCycle;
		try {
			autoRefreshTimeCycle = Integer.parseInt(sharedPreferences.getString(
				PreferencesFragment.PREFERENCE_CYCLE_TIME, PreferencesFragment.defaultCycleTimeInMinutes));
		}
		catch (NumberFormatException e) {
			autoRefreshTimeCycle = Integer.parseInt(PreferencesFragment.defaultCycleTimeInMinutes);
		}
		return autoRefreshTimeCycle;
	}
	
	private void startWeatherUpdateRequest(int cycle) {
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
			minutesToMiliseconds(cycle), callPowerLockReceiverPendingIntent);
	}
	
	/**
	 * Converts provided by user (convenient to him) time to more accuracy for
	 * AlarmManager usage.
	 * */
	private int minutesToMiliseconds(int minutes) {
		return minutes * 60 * 1000;
	}
}