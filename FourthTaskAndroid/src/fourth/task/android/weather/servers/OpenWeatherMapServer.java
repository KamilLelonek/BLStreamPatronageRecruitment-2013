package fourth.task.android.weather.servers;

import android.content.Context;
import fourth.task.android.weather.parsers.OpenWeatherMapParser;

public class OpenWeatherMapServer extends AbstractWeatherServer {
	
	public OpenWeatherMapServer(Context context) {
		super(context, new OpenWeatherMapParser());
		connectionString = "http://api.openweathermap.org/data/2.1/find/city?lat=%f&lon=%f&cnt=1";
		/* More accurate but does not provide detailed weather information:
		 * connectionString =
		 * "http://api.openweathermap.org/data/2.1/find/station?lat=%f&lon=%f&cnt=1" */
	}
}