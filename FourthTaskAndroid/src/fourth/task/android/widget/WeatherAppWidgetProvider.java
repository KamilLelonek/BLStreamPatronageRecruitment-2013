package fourth.task.android.widget;

import java.util.Arrays;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import fourth.task.android.FourthTaskAndroid;
import fourth.task.android.R;
import fourth.task.android.items.Item;
import fourth.task.android.utils.PreferencesManager;
import fourth.task.android.weather.servers.WorldWeatherOnlineServer;

public class WeatherAppWidgetProvider extends AppWidgetProvider {
	private PreferencesManager preferencesManager;
	private AppWidgetManager appWidgetManager;
	private Context context;
	
	@Override public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		this.context = context;
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
		Item item = preferencesManager.readItemFromFile(appWidgetId);
		if (item != null) {
			updateItem(item, appWidgetId);
			views.setTextViewText(R.id.textViewWidgetItemName, item.getName());
			views.setTextViewText(R.id.textViewWidgetItemTemperature, item.getTemperature());
			views.setImageViewBitmap(R.id.imageButtonWidgetWeather, item.getBitmap());
		}
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}
	
	private void updateItem(Item item, int appWidgetId) {
		new WorldWeatherOnlineServer(context).downloadData(Arrays.asList(new Item[] { item }));
		preferencesManager.saveItemToFile(item, appWidgetId);
	}
	
	@Override public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		PreferencesManager preferencesManager = new PreferencesManager(context);
		preferencesManager.deleteItemFile(appWidgetIds[0]);
	}
}