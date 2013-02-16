package fourth.task.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

public class PreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
	private final String PREFERENCE_CYCLE_TIME = "preferences_autorefresh_edittext";
	private String SUMMARY_CYCLE_TIME;
	
	private EditTextPreference editTextAutoRefreshTimeCycle;
	private SharedPreferences sharedPreferences;
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SUMMARY_CYCLE_TIME = getString(R.string.preferences_autorefresh_edittext_summary);
		
		editTextAutoRefreshTimeCycle = (EditTextPreference) getPreferenceScreen().findPreference(PREFERENCE_CYCLE_TIME);
		EditText myEditText = editTextAutoRefreshTimeCycle.getEditText();
		myEditText.setKeyListener(DigitsKeyListener.getInstance(false, true));
		
		sharedPreferences = getPreferenceManager().getSharedPreferences();
	}
	
	@Override public void onResume() {
		super.onResume();
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		updateCycleTimeValue();
	}
	
	@Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(PREFERENCE_CYCLE_TIME)) {
			updateCycleTimeValue();
		}
	}
	
	private void updateCycleTimeValue() {
		editTextAutoRefreshTimeCycle.setSummary(String.format(SUMMARY_CYCLE_TIME,
			sharedPreferences.getString(PREFERENCE_CYCLE_TIME, "10")));
	}
	
	@Override public void onPause() {
		sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}
}