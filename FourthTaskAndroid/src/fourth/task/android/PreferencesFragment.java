package fourth.task.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

/**
 * Fragment used to present user useful preferences stored in SharedPreferences
 */
public class PreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	public static final String defaultCycleTimeInMinutes = "10";
	public static final String PREFERENCE_CYCLE_TIME = "preferences_autorefresh_edittext";
	public static final String PREFERENCE_AUTO_REFRESH_CONDITION = "preferences_autorefresh_checkbox";
	public static final String PREFERENCE_SERVERS = "preferences_weather_servers_list";
	private String SUMMARY_CYCLE_TIME;
	private String SUMMARY_SERVERS;
	
	private EditTextPreference editTextAutoRefreshTimeCycle;
	private ListPreference listWeatherServer;
	private SharedPreferences sharedPreferences;
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SUMMARY_CYCLE_TIME = getString(R.string.preferences_autorefresh_edittext_summary);
		SUMMARY_SERVERS = getString(R.string.preferences_weather_servers_list_summary);
		
		listWeatherServer = (ListPreference) getPreferenceScreen().findPreference(PREFERENCE_SERVERS);
		editTextAutoRefreshTimeCycle = (EditTextPreference) getPreferenceScreen().findPreference(PREFERENCE_CYCLE_TIME);
		EditText myEditText = editTextAutoRefreshTimeCycle.getEditText();
		myEditText.setKeyListener(DigitsKeyListener.getInstance(false, true));
		
		sharedPreferences = getPreferenceScreen().getSharedPreferences();
	}
	
	@Override public void onStart() {
		super.onStart();
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override public void onResume() {
		super.onResume();
		updateCycleTimeValue();
		updateCurrentWeatherServer();
	}
	
	/**
	 * When user changes some preference, summaries should be updated
	 * immediately to present the newest preferences data.
	 */
	@Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(PREFERENCE_CYCLE_TIME)) {
			updateCycleTimeValue();
		}
		if (key.equals(PREFERENCE_SERVERS)) {
			updateCurrentWeatherServer();
		}
	}
	
	/**
	 * Updates summary of CycleTimeValue TextView
	 */
	private void updateCycleTimeValue() {
		editTextAutoRefreshTimeCycle.setSummary(String.format(SUMMARY_CYCLE_TIME,
			sharedPreferences.getString(PREFERENCE_CYCLE_TIME, defaultCycleTimeInMinutes)));
	}
	
	/**
	 * Updates summary of WeatherServer TextView
	 */
	private void updateCurrentWeatherServer() {
		listWeatherServer.setSummary(String.format(
			SUMMARY_SERVERS,
			sharedPreferences.getString(PREFERENCE_SERVERS,
				getResources().getStringArray(R.array.preferences_weather_servers_list)[0])));
	}
	
	@Override public void onStop() {
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		super.onStop();
	}
}