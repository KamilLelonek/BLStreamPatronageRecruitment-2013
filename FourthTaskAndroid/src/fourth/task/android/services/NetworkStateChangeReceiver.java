package fourth.task.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;
import fourth.task.android.R;

/**
 * NetworkStateChangeReceiver listens all Internet connection changes.
 */
public class NetworkStateChangeReceiver extends BroadcastReceiver {
	private final String LOG_TAG = "NetworkStateChangeReceiver";
	private ConnectivityManager connectivityManager;
	private NetworkInfo networkInfoActive;
	private NetworkInfo networkInfoMobile;
	private NetworkInfo networkInfoWifi;
	
	private boolean isServiceManagerRunning;
	
	@Override public void onReceive(Context context, Intent intent) {
		if (connectivityManager == null) {
			connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			networkInfoActive = connectivityManager.getActiveNetworkInfo();
			networkInfoMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			networkInfoWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		}
		
		if (networkInfoActive != null) {
			Log.d(LOG_TAG, "Active Network Type : " + networkInfoActive.getTypeName());
		}
		
		showNetworkState(networkInfoMobile);
		showNetworkState(networkInfoWifi);
		
		controlServiceManager(context);
	}
	
	/* When there's no active Internet connection ServiceManager is being
	 * notified about that in order to turn off updates. In the other case it
	 * receives message that phone is connected to active network and service
	 * should start to update cities data. */
	private void controlServiceManager(Context context) {
		State networkInfoMobileState = networkInfoMobile.getState();
		// networkInfoMobileState == DISCONNECTED	=> Not connected
		// networkInfoMobileState == UNKNOWN		=> No SIM card
		
		// when device is off-line:
		if ((networkInfoMobileState.equals(NetworkInfo.State.DISCONNECTED) || networkInfoMobileState
			.equals(NetworkInfo.State.UNKNOWN)) && networkInfoWifi.getState().equals(NetworkInfo.State.DISCONNECTED)) {
			Log.d(LOG_TAG, context.getResources().getString(R.string.alert_internet_connection_title));
			
			context.stopService(new Intent(context, ServiceManager.class));
			isServiceManagerRunning = false;
		}
		/* device is online but it has to be checked if:
		 * WIFI and MOBILE had been both online but only one of them was turned off
		 * so in that case there is no need to start ServiceManager one again
		 */
		else if (!isServiceManagerRunning) {
			context.startService(new Intent(context, ServiceManager.class));
			isServiceManagerRunning = true;
		}
	}
	
	private void showNetworkState(NetworkInfo networkInfo) {
		if (networkInfo != null) {
			Log.d(LOG_TAG, networkInfo.getTypeName() + " network state: " + networkInfo.getState());
		}
	}
}