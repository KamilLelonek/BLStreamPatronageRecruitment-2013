package fourth.task.android.services;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import fourth.task.android.PreferencesFragment;
import fourth.task.android.R;
import fourth.task.android.items.Item;
import fourth.task.android.utils.ApplicationObject;
import fourth.task.android.weather.servers.IWeatherServer;
import fourth.task.android.weather.servers.OpenWeatherMapServer;
import fourth.task.android.weather.servers.WorldWeatherOnlineServer;

public class WeatherService extends IntentService implements OnSharedPreferenceChangeListener {
	private IWeatherServer weatherServer;
	private LocalBroadcastManager localBroadcastManager;
	private ApplicationObject applicationObject;
	
	public static final String INTENT_DOWNLOAD_COMPLETED = "fourth.task.android.services.DOWNLOAD_COMPLETED";
	public static final String INTENT_DOWNLOAD_STARTED = "fourth.task.android.services.DOWNLOAD_STARTED";
	
	public WeatherService() {
		super("WeatherService"); // name for the worker thread
	}
	
	@Override public void onCreate() {
		super.onCreate();
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		weatherServer = getWeatherServer(sharedPreferences);
		
		applicationObject = (ApplicationObject) getApplication();
		localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
	}
	
	@Override protected void onHandleIntent(Intent intent) {
		Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: Intent received!");
		updateWeatherData();
	}
	
	private void updateWeatherData() {
		Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: Starting to update data.");
		try {
			List<Item> items = applicationObject.getItemAdapter().getItems();
			// PowerLockManager prevents from multi-download data.
			if (items != null && !items.isEmpty() && PowerLockManager.acquireLock(getApplicationContext())) {
				localBroadcastManager.sendBroadcast(new Intent(INTENT_DOWNLOAD_STARTED));
				weatherServer.downloadData(items);
			}
		}
		catch (Exception e) {
			Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: Updating data interrupted!");
		}
		finally {
			PowerLockManager.relaseLock();
			Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: Download finished!");
			localBroadcastManager.sendBroadcast(new Intent(INTENT_DOWNLOAD_COMPLETED));
		}
	}
	
	@Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: onSharedPreferenceChanged");
		
		if (key.equals(PreferencesFragment.PREFERENCE_SERVERS)) {
			weatherServer = getWeatherServer(sharedPreferences);
		}
	}
	
	private IWeatherServer getWeatherServer(SharedPreferences sharedPreferences) {
		String[] availableServers = getResources().getStringArray(R.array.preferences_weather_servers_list);
		String serverName = sharedPreferences.getString(PreferencesFragment.PREFERENCE_SERVERS, availableServers[0]);
		
		if (serverName.equals(availableServers[1])) return new WorldWeatherOnlineServer(getApplicationContext());
		return new OpenWeatherMapServer(getApplicationContext());
	}
}