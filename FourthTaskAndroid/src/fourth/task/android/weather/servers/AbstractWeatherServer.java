package fourth.task.android.weather.servers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import fourth.task.android.FourthTaskAndroid;
import fourth.task.android.cities.City;
import fourth.task.android.weather.parsers.IWeatherParser;

public abstract class AbstractWeatherServer implements IWeatherServer {
	private Geocoder geocoder;
	private IWeatherParser weatherParser;
	private ArrayList<AsyncTask<City, Void, Void>> asyncTasksQueue;
	
	protected String connectionString;
	
	public AbstractWeatherServer(Context context, IWeatherParser weatherParser) {
		this.weatherParser = weatherParser;
		asyncTasksQueue = new ArrayList<AsyncTask<City, Void, Void>>();
		geocoder = new Geocoder(context);
		
	}
	
	@Override public void downloadData(City city) {
		downloadData(Arrays.asList(new City[] { city }));
	}
	
	@Override public void downloadData(List<City> cities) {
		for (City city : cities) {
			city.setConnectionString(String.format(connectionString, city.getLatitude(), city.getLongitude()));
			asyncTasksQueue.add(new WeatherDataFetcher().execute(city));
		}
		
		/* This section is specially prepared for join all started AsyncTasks.
		 * There is a reasonable need to wait until all tasks are completed
		 * because only then citys' list should be updated and PowerLock should
		 * be released. */
		for (AsyncTask<City, Void, Void> asyncTask : asyncTasksQueue) {
			try {
				asyncTask.get(); // thread join
			}
			catch (InterruptedException e) {
				Log.e(FourthTaskAndroid.STRING_LOG_TAG, "AsyncTask InterruptedException");
			}
			catch (ExecutionException e) {
				Log.e(FourthTaskAndroid.STRING_LOG_TAG, "AsyncTask ExecutionException");
			}
		}
	}
	
	private class WeatherDataFetcher extends AsyncTask<City, Void, Void> {
		private City city;
		
		@Override protected Void doInBackground(City... params) {
			city = params[0];
			String cityConnectionString = city.getConnectionString();
			
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet request = new HttpGet(cityConnectionString);
				HttpResponse response = httpClient.execute(request);
				@SuppressWarnings("resource")
				InputStream data = response.getEntity().getContent();
				weatherParser.parseData(data, city);
			}
			catch (IOException e) {
				Log.e(FourthTaskAndroid.STRING_LOG_TAG, "HttpClient exception. Device's became offline.");
			}
			
			return null;
		}
	}
	
	protected String getLocationName(double latitude, double longitude) {
		List<Address> addresses = null;
		try {
			// Call the synchronous getFromLocation() method by passing in the lat/long values.
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		}
		catch (IOException e) {
			Log.e(FourthTaskAndroid.STRING_LOG_TAG, "GeoCoder exception.");
		}
		
		if (addresses != null && addresses.size() > 0) {
			Address address = addresses.get(0);
			return address.getLocality();
		}
		
		return null;
	}
}