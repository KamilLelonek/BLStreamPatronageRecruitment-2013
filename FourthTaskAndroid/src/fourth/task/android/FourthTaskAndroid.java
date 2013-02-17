package fourth.task.android;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import fourth.task.android.services.PowerLockReceiver;
import fourth.task.android.services.ServiceManager;
import fourth.task.android.services.WeatherService;
import fourth.task.android.services.WeatherService.WeatherBinder;

public class FourthTaskAndroid extends Activity implements ActionBar.TabListener {
	public final static String STRING_LOG_TAG = "FourthTaskAndroid";
	
	private final Fragment listViewFragment = new ListViewFragment();
	private final Fragment mapViewFragment = new MapViewFragment();
	private FragmentManager fragmentManager;
	
	private ActionBar actionBar;
	private boolean isServiceBound;
	public WeatherService weatherService;
	
	private final ServiceConnection serviceConnection = new ServiceConnection() {
		/* After establishing a connection with service all items are passed
		 * there in case to update their weather data. Here cannot be used
		 * passing by intent or other way because we need to maintain strong
		 * reference to this collection. */
		@Override public void onServiceConnected(ComponentName name, IBinder service) {
			weatherService = ((WeatherBinder) service).getService();
			weatherService.setData(ListViewFragment.itemAdapter.getItems());
			isServiceBound = true;
			Log.d(STRING_LOG_TAG, "Service bound!");
		}
		
		@Override public void onServiceDisconnected(ComponentName name) {
			isServiceBound = false;
			Log.d(STRING_LOG_TAG, "Service unbound!");
		}
	};
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Specify that we will be displaying tabs in the action bar
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.label_list)).setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText(getString(R.string.label_map)).setTabListener(this));
		
		fragmentManager = getFragmentManager();
		fragmentManager.addOnBackStackChangedListener(new SmartBackStackListener());
		
		Log.d(STRING_LOG_TAG, "Calling for service");
		sendBroadcast(new Intent(ServiceManager.SERVICE_START_INTENT));
	}
	
	@Override protected void onStart() {
		super.onStart();
		bindService(new Intent(this, WeatherService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override protected void onStop() {
		super.onStop();
		if (isServiceBound) {
			unbindService(serviceConnection);
			isServiceBound = false;
		}
	}
	
	@Override public void onTabSelected(Tab tab, FragmentTransaction ft) {
		switch (tab.getPosition()) {
			case 0:
				ft.replace(android.R.id.content, listViewFragment);
				break;
			case 1:
				ft.replace(android.R.id.content, mapViewFragment);
				break;
		}
	}
	
	@Override public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
	@Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_fourth_task_android_activity, menu);
		return true;
	}
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				sendBroadcast(new Intent(PowerLockReceiver.SERVICE_START_INTENT));
				return true;
			case R.id.menu_preferences:
				fragmentManager.beginTransaction().replace(android.R.id.content, new PreferencesFragment())
					.addToBackStack(null).commit();
				actionBar.hide();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * SmartBackStackListener allows to smartly managing stack for fragments.
	 * Only one instance of PreferencesFragment is allowed to be kept on the
	 * stack (show on screen), any another is blocked. Additionally, action bar
	 * is not visible when PreferencesFragment is displayed, but is appears
	 * immediately after closing preferences.
	 */
	private class SmartBackStackListener implements OnBackStackChangedListener {
		private int backStackCounter; // counts number of fragments of stack
		
		@Override public void onBackStackChanged() {
			backStackCounter = fragmentManager.getBackStackEntryCount();
			
			if (backStackCounter == 0) { // PreferencesFragment is not on stack, actionBar can be shown
				actionBar.show();
			}
			// PreferencesFragment is on stack (backStackCounter is 1)
			// but user want to display it one again so this action should be blocked
			if (backStackCounter != 1) {
				fragmentManager.popBackStack();
			}
		}
	}
}