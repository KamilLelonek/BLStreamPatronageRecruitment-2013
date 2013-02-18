package fourth.task.android.weather.servers;

import java.util.List;

import fourth.task.android.items.Item;

public interface IWeatherServer {
	public void downloadData(List<Item> items);
}