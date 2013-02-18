package fourth.task.android.weather.servers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import fourth.task.android.items.Item;
import fourth.task.android.weather.parsers.IWeatherParser;

public abstract class AbstractWeatherServer implements IWeatherServer {
	private Geocoder geocoder;
	private IWeatherParser weatherParser;
	private ArrayList<AsyncTask<Item, Void, Void>> asyncTasksQueue;
	
	protected String connectionString;
	
	public AbstractWeatherServer(Context context, IWeatherParser weatherParser) {
		this.weatherParser = weatherParser;
		asyncTasksQueue = new ArrayList<AsyncTask<Item, Void, Void>>();
		geocoder = new Geocoder(context);
		
	}
	
	@Override public void downloadData(List<Item> items) {
		for (Item item : items) {
			asyncTasksQueue.add(new WeatherDataFetcher().execute(item));
		}
		
		/* This section is specially prepared for join all started AsyncTasks.
		 * There is a reasonable need to wait until all tasks are completed
		 * because only then items' list should be updated and PowerLock should
		 * be released. */
		for (AsyncTask<Item, Void, Void> asyncTask : asyncTasksQueue) {
			try {
				asyncTask.get(); // thread join
			}
			catch (InterruptedException e) {}
			catch (ExecutionException e) {}
		}
	}
	
	private class WeatherDataFetcher extends AsyncTask<Item, Void, Void> {
		
		@Override protected Void doInBackground(Item... params) {
			Item item = params[0];
			String itemConnectionString = item.getConnectionString();
			
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet request = new HttpGet(itemConnectionString);
				HttpResponse response = httpClient.execute(request);
				@SuppressWarnings("resource")
				InputStream data = response.getEntity().getContent();
				weatherParser.parseData(data, item);
			}
			catch (IOException e) {}
			
			return null;
		}
	}
	
	protected String getLocationName(double latitude, double longitude) {
		List<Address> addresses = null;
		try {
			// Call the synchronous getFromLocation() method by passing in the lat/long values.
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
		}
		catch (IOException e) {}
		
		if (addresses != null && addresses.size() > 0) {
			Address address = addresses.get(0);
			return address.getLocality();
		}
		
		return null;
	}
}