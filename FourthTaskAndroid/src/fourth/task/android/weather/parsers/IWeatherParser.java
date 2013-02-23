package fourth.task.android.weather.parsers;

import java.io.InputStream;

import fourth.task.android.cities.City;

public interface IWeatherParser {
	public void parseData(InputStream is, City city);
}