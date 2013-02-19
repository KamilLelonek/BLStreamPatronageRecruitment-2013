package fourth.task.android.services;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;
import fourth.task.android.PreferencesFragment;

public class ServiceManager extends IntentService implements OnSharedPreferenceChangeListener {
	public static final String SERVICE_LOG_TAG = "SOA";
	public static final String SERVICE_START_INTENT = "fourth.task.android.APPLICATION_STARTED";
	public static final String SERVICE_STOP_INTENT = "fourth.task.android.STOP_SERVICE";
	
	private SharedPreferences sharedPreferences;
	private AlarmManager alarmManager;
	private PendingIntent startWeatherServicePendingIntent;
	private Context context;
	
	public ServiceManager() {
		super("ServiceManager");
	}
	
	@Override public void onCreate() {
		super.onCreate();
		
		this.context = getApplicationContext();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		/* Creating intent which will be broadcasted to invoke PowerLockReceiver
		 * in the future. */
		Intent powerLockReceiverIntent = new Intent(context, WeatherService.class);
		startWeatherServicePendingIntent = PendingIntent.getService(context, 0, powerLockReceiverIntent,
			PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	/**
	 * Depends on received intent manageServices can stop or start data updates.
	 */
	@Override protected void onHandleIntent(Intent intent) {
		Log.d(SERVICE_LOG_TAG, "ServiceManager: Intent received!");
		startWeatherUpdateRequest();
	}
	
	private void startWeatherUpdateRequest() {
		if (isAutoRefreshEnabled()) {
			Log.d(SERVICE_LOG_TAG, "ServiceManager: Starting new refreshing cycle (first app start or on demand).");
			startWeatherUpdateRequest(getAutoRefreshTimeCycle());
		}
		else {
			Log.d(SERVICE_LOG_TAG, "ServiceManager: One-shot update request.");
			startService(new Intent(context, WeatherService.class));
		}
	}
	
	private boolean isAutoRefreshEnabled() {
		return sharedPreferences.getBoolean(PreferencesFragment.PREFERENCE_AUTO_REFRESH_CONDITION, false);
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
			minutesToMiliseconds(cycle), startWeatherServicePendingIntent);
	}
	
	/**
	 * Converts provided by user (convenient to him) time to more accuracy for
	 * AlarmManager usage.
	 * */
	private int minutesToMiliseconds(int minutes) {
		return minutes * 60 * 1000;
	}
	
	@Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(SERVICE_LOG_TAG, "ServiceManager: onSharedPreferenceChanged");
		
		if (PreferencesFragment.PREFERENCE_CYCLE_TIME.equals(key)
			|| PreferencesFragment.PREFERENCE_AUTO_REFRESH_CONDITION.equals(key)) {
			/* Refreshing cycle time has been changed or cycling refreshing has
			 * been changer so weather update request must be restarted to
			 * change its data update period or been turned on/off. */
			restartWeatherUpdateRequest();
		}
	}
	
	private void restartWeatherUpdateRequest() {
		cancelWeatherUpdateRequest();
		startWeatherUpdateRequest();
	}
	
	@Override public void onDestroy() {
		cancelWeatherUpdateRequest();
		super.onDestroy();
	}
	
	private void cancelWeatherUpdateRequest() {
		alarmManager.cancel(startWeatherServicePendingIntent);
	}
}