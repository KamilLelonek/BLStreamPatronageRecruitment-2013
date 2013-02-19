package fourth.task.android.weather.parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;
import fourth.task.android.FourthTaskAndroid;
import fourth.task.android.items.Item;

public abstract class AbstractJSONParser implements IWeatherParser {
	
	@Override public void parseData(InputStream is, Item item) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		StringBuilder stringBuilder = new StringBuilder();
		
		try {
			String line = bufferedReader.readLine();
			while (line != null) {
				stringBuilder.append(line);
				line = bufferedReader.readLine();
			}
		}
		catch (IOException e) {
			Log.e(FourthTaskAndroid.STRING_LOG_TAG, "BufferedReader reading InputStream exception.");
		}
		finally {
			try {
				bufferedReader.close();
			}
			catch (IOException e) {
				Log.e(FourthTaskAndroid.STRING_LOG_TAG, "BufferedReader closing stream exception.");
			}
		}
		
		updateWeatherData(item, stringBuilder.toString());
	}
	
	public abstract void updateWeatherData(Item item, String data);
}