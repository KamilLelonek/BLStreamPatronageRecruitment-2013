package fourth.task.android.services;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import fourth.task.android.cities.City;
import fourth.task.android.utils.ApplicationObject;
import fourth.task.android.utils.PreferencesManager;
import fourth.task.android.weather.servers.IWeatherServer;

/**
 * Service used to manage weather data updates.
 */
public class WeatherService extends IntentService {
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
		weatherServer = new PreferencesManager(getApplicationContext()).getCurrentWeatherServer();
		applicationObject = (ApplicationObject) getApplication();
		localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
	}
	
	/**
	 * Called after startService(context, WeatherService.class);
	 */
	@Override protected void onHandleIntent(Intent intent) {
		Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: Intent received!");
		updateWeatherData();
	}
	
	private void updateWeatherData() {
		Log.d(ServiceManager.SERVICE_LOG_TAG, "WeatherService: Starting to update data.");
		try {
			List<City> cities = applicationObject.getCityAdapter().getCities();
			// PowerLockManager prevents from multi-download data.
			if (cities != null && !cities.isEmpty() && PowerLockManager.acquireLock(getApplicationContext())) {
				localBroadcastManager.sendBroadcast(new Intent(INTENT_DOWNLOAD_STARTED));
				weatherServer.downloadData(cities);
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
}