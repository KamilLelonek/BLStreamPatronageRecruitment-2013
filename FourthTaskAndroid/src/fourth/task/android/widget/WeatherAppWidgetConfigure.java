package fourth.task.android.widget;

import java.util.List;

import android.app.ListActivity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RemoteViews;
import fourth.task.android.FourthTaskAndroid;
import fourth.task.android.R;
import fourth.task.android.items.Item;
import fourth.task.android.items.ItemAdapter;
import fourth.task.android.utils.PreferencesManager;

public class WeatherAppWidgetConfigure extends ListActivity {
	private PreferencesManager preferencesManager;
	private AppWidgetManager appWidgetManager;
	private RemoteViews views;
	private int mAppWidgetId;
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appWidgetManager = AppWidgetManager.getInstance(WeatherAppWidgetConfigure.this);
		preferencesManager = new PreferencesManager(WeatherAppWidgetConfigure.this);
		views = new RemoteViews(WeatherAppWidgetConfigure.this.getPackageName(), R.layout.widget_layout);
		
		Intent i = new Intent(WeatherAppWidgetConfigure.this, FourthTaskAndroid.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(WeatherAppWidgetConfigure.this, 0, i, 0);
		views.setOnClickPendingIntent(R.id.WidgetRelativeLayout, pendingIntent);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});
		
		readItems();
	}
	
	private void readItems() {
		List<Item> items = preferencesManager.readListFromFile();
		ItemAdapter itemAdapter = new ItemAdapter(WeatherAppWidgetConfigure.this, items);
		setListAdapter(itemAdapter);
	}
	
	private void selectItem(int position) {
		Item item = (Item) getListAdapter().getItem(position);
		
		views.setTextViewText(R.id.textViewWidgetItemName, item.getName());
		views.setTextViewText(R.id.textViewWidgetItemTemperature, item.getTemperature());
		views.setImageViewBitmap(R.id.imageButtonWidgetWeather, item.getBitmap());
		
		preferencesManager.saveItemToFile(item, mAppWidgetId);
		
		appWidgetManager.updateAppWidget(mAppWidgetId, views);
		acceptAndFinish();
	}
	
	private void acceptAndFinish() {
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
		setResult(RESULT_OK, resultValue);
		finish();
	}
}