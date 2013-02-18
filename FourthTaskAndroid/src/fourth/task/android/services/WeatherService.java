package fourth.task.android.services;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import fourth.task.android.PreferencesFragment;
import fourth.task.android.R;
import fourth.task.android.items.Item;
import fourth.task.android.weather.servers.AccuWeatherServer;
import fourth.task.android.weather.servers.ForecaServer;
import fourth.task.android.weather.servers.IWeatherServer;
import fourth.task.android.weather.servers.OpenWeatherMap;

public class WeatherService extends IntentService implements OnSharedPreferenceChangeListener {
	private IWeatherServer weatherServer;
	private List<Item> items;
	private LocalBroadcastManager localBroadcastManager;
	
	public static final String INTENT_FILTER = "update-list";
	
	public WeatherService() {
		super("WeatherService"); // name for the worker thread
	}
	
	@Override public void onCreate() {
		super.onCreate();
		weatherServer = getWeatherServer(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
		localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
	}
	
	@Override protected void onHandleIntent(Intent intent) {
		Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: Intent received!");
		updateWeatherData();
	}
	
	private void updateWeatherData() {
		Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: Starting to update data.");
		try {
			if (items != null && !items.isEmpty()) {
				PowerLockManager.acquireLock(getApplicationContext());
				weatherServer.downloadData(items);
			}
		}
		finally {
			PowerLockManager.relaseLock();
			Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: Data succesfuly updated!");
			localBroadcastManager.sendBroadcast(new Intent(INTENT_FILTER));
		}
	}
	
	@Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(PreferencesFragment.PREFERENCE_SERVERS)) {
			weatherServer = getWeatherServer(sharedPreferences);
		}
	}
	
	private IWeatherServer getWeatherServer(SharedPreferences sharedPreferences) {
		String[] availableServers = getResources().getStringArray(R.array.preferences_weather_servers_list);
		String serverName = sharedPreferences.getString(PreferencesFragment.PREFERENCE_SERVERS, availableServers[0]);
		
		if (serverName.equals(availableServers[1])) return new ForecaServer();
		else if (serverName.equals(availableServers[2])) return new AccuWeatherServer();
		return new OpenWeatherMap(getApplicationContext());
	}
	
	/***************************************************
	 ***************** Binding section *****************
	 ***************************************************/
	private final IBinder mBinder = new WeatherBinder();
	
	public class WeatherBinder extends Binder {
		public WeatherService getService() {
			// Return this instance of WeatherService so clients can call public methods
			return WeatherService.this;
		}
	}
	
	@Override public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	/* methods for clients */
	public void setData(List<Item> items) {
		this.items = items;
	}
}