package fourth.task.android.weather.servers;

import java.util.List;

import android.content.Context;
import fourth.task.android.items.Item;
import fourth.task.android.weather.parsers.OpenWeatherMapParser;

public class OpenWeatherMap extends AbstractWeatherServer {
	
	public OpenWeatherMap(Context context) {
		super(context, new OpenWeatherMapParser());
		connectionString = "http://api.openweathermap.org/data/2.1/find/city?lat=%f&lon=%f&cnt=1";
		/* More accurate but does not provide detailed weather information:
		 * connectionString =
		 * "http://api.openweathermap.org/data/2.1/find/station?lat=%f&lon=%f&cnt=1" */
	}
	
	@Override public void downloadData(List<Item> items) {
		for (Item item : items) {
			item.setConnectionString(String.format(connectionString, item.getLatitude(), item.getLongitude()));
		}
		super.downloadData(items);
	}
	
	public void downloadMockData(List<Item> items) {
		for (Item i : items) {
			i.setTemperature(String.valueOf(Math.random()));
		}
	}
}