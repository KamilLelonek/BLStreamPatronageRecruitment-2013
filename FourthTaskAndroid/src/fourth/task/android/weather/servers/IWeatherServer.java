package fourth.task.android.weather.servers;

import java.util.List;

import fourth.task.android.cities.City;

public interface IWeatherServer {
	public void downloadData(List<City> cities);
	
	public void downloadData(City city);
}