package fourth.task.android.network;

import java.util.List;
import java.util.Random;

import fourth.task.android.items.Item;

public class YahooServer implements IWeatherServer {
	
	@Override public void cancelDownload() {
		
	}
	
	@Override public void downloadData(List<Item> items) {
		for (Item i : items) {
			i.setTemperature(String.valueOf(Math.random()));
			i.setWeather(new String[] { "Sunny", "Windy", "Rainy", "Foggy", "Frostily", "Cloudy" }[new Random()
				.nextInt(5)]);
		}
	}
}