package fourth.task.android.weather.servers;

import android.content.Context;
import fourth.task.android.weather.parsers.WorldWeatherOnlineParser;

public class WorldWeatherOnlineServer extends AbstractWeatherServer {
	
	public WorldWeatherOnlineServer(Context context) {
		super(context, new WorldWeatherOnlineParser());
		connectionString = "http://free.worldweatheronline.com/feed/weather.ashx?q=%s,%s&format=xml&key=55e3af02fa160634131902";
	}
}