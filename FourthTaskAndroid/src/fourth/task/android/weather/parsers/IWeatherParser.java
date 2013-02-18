package fourth.task.android.weather.parsers;

import java.io.InputStream;

import fourth.task.android.items.Item;

public interface IWeatherParser {
	public void parseData(InputStream is, Item item);
}