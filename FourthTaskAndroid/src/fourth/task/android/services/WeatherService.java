package fourth.task.android.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.IBinder;

public class WeatherService extends Service implements OnSharedPreferenceChangeListener {
	
	@Override public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
	}
}