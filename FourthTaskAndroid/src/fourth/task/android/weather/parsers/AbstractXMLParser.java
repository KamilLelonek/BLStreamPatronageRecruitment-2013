package fourth.task.android.weather.parsers;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import fourth.task.android.FourthTaskAndroid;
import fourth.task.android.cities.City;

public abstract class AbstractXMLParser implements IWeatherParser {
	private XmlPullParser xpp;
	
	@Override public void parseData(InputStream is, City city) {
		try {
			xpp = XmlPullParserFactory.newInstance().newPullParser();
			xpp.setInput(is, "UTF-8");
			updateWeatherData(city, xpp);
		}
		catch (XmlPullParserException e) {
			Log.e(FourthTaskAndroid.STRING_LOG_TAG, "XmlPullParser exception.");
		}
	}
	
	public abstract void updateWeatherData(City city, XmlPullParser xpp);
}