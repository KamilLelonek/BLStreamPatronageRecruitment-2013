package fourth.task.android.weather.parsers;

import java.io.IOException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import fourth.task.android.items.Item;

public class OpenWeatherMapParser extends AbstractJSONParser {
	
	@Override protected void updateWeatherData(Item item, String data) {
		try {
			JSONObject jsonObjectWrapper = new JSONObject(data);
			JSONObject jsonObjectData = jsonObjectWrapper.getJSONArray("list").getJSONObject(0);
			
			/* Temperature */
			JSONObject jsonObjectTemperature = jsonObjectData.getJSONObject("main");
			double celciusDegrees = jsonObjectTemperature.getInt("temp") - 273;
			item.setTemperature(String.valueOf(celciusDegrees));
			
			/* Icon */
			JSONObject jsonObjectWeather = jsonObjectData.getJSONArray("weather").getJSONObject(0);
			String weatherIconID = jsonObjectWeather.getString("icon");
			item.setWeatherIconURL("http://openweathermap.org/img/w/" + weatherIconID + ".png");
			
			try {
				URL weatherIconURL = new URL(item.getWeatherIconURL());
				Bitmap weatherIcon = BitmapFactory.decodeStream(weatherIconURL.openStream());
				item.setBitmap(weatherIcon);
			}
			catch (IOException e) {}
			
		}
		catch (JSONException e) {}
	}
	
	/**
	 * private class DownloadIconTask extends AsyncTask<String, Void, Bitmap> {
	 * 
	 * private ImageView imageView; private int position;
	 * 
	 * public DownloadIconTask(ImageView imageView, int position) {
	 * this.imageView = imageView; this.position = position; }
	 * 
	 * @Override protected Bitmap doInBackground(String... params) { try { URL
	 *           url = new URL(params[0]); return
	 *           BitmapFactory.decodeStream(url.openStream()); } catch
	 *           (IOException e) { return null; } }
	 * @Override protected void onPostExecute(Bitmap result) { int forPosition =
	 *           (Integer) imageView.getTag(); if (this.position == forPosition)
	 *           { imageView.setImageBitmap(result); } }
	 * 
	 *           }
	 */
}