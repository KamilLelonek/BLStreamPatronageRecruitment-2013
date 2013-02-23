package fourth.task.android.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import fourth.task.android.FourthTaskAndroid;
import fourth.task.android.R;
import fourth.task.android.cities.City;
import fourth.task.android.utils.PreferencesManager;
import fourth.task.android.weather.servers.IWeatherServer;

public class WeatherAppWidgetProvider extends AppWidgetProvider {
	private PreferencesManager preferencesManager;
	private AppWidgetManager appWidgetManager;
	
	@Override public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		this.appWidgetManager = appWidgetManager;
		this.preferencesManager = new PreferencesManager(context);
		
		// Create an Intent to launch FourthTaskAndroid
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
			new Intent(context, FourthTaskAndroid.class), 0);
		
		// Perform this loop procedure for each App Widget that belongs to this provider
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			// Get the layout for the App Widget and attach an on-click listener to it
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			views.setOnClickPendingIntent(R.id.WidgetRelativeLayout, pendingIntent);
			updateRemoteViews(views, appWidgetIds[i]);
		}
	}
	
	private void updateRemoteViews(RemoteViews views, int appWidgetId) {
		// Tell the AppWidgetManager to perform an update on the current app widget
		City city = preferencesManager.readCityFromFile(appWidgetId);
		if (city != null) {
			updateCity(city, appWidgetId);
			views.setTextViewText(R.id.textViewWidgetCityName, city.getName());
			views.setTextViewText(R.id.textViewWidgetCityTemperature, city.getTemperature());
			views.setImageViewBitmap(R.id.imageButtonWidgetWeather, city.getBitmap());
		}
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
	
	private void updateCity(City city, int appWidgetId) {
		IWeatherServer weatherServer = preferencesManager.getCurrentWeatherServer();
		weatherServer.downloadData(city);
		preferencesManager.saveCityToFile(city, appWidgetId);
	}
	
	@Override public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		PreferencesManager preferencesManager = new PreferencesManager(context);
		preferencesManager.deleteCityFile(appWidgetIds[0]);
	}
}