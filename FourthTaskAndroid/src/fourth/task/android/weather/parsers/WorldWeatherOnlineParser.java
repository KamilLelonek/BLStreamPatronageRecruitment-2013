package fourth.task.android.weather.parsers;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.Bitmap;
import fourth.task.android.items.Item;
import fourth.task.android.utils.BitmapManager;

public class WorldWeatherOnlineParser extends AbstractXMLParser {
	private XmlPullParser xpp;
	
	@Override public void updateWeatherData(Item item, XmlPullParser xpp) {
		this.xpp = xpp;
		try {
			skipToTag("temp_C");
			item.setTemperature(xpp.nextText());
			skipToTag("weatherIconUrl");
			Bitmap weatherIcon = BitmapManager.downloadBitmap(xpp.nextText());
			item.setBitmap(weatherIcon);
		}
		catch (XmlPullParserException e) {}
		catch (IOException e) {}
	}
	
	private void skipToTag(String tagName) throws XmlPullParserException, IOException {
		int event = xpp.getEventType();
		while (event != XmlPullParser.END_DOCUMENT && !tagName.equals(xpp.getName())) {
			event = xpp.next();
		}
	}
}