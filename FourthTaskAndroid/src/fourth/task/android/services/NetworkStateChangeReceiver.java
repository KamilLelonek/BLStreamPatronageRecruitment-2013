package fourth.task.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
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
	 * should start to update items data. */
	private void controlServiceManager(Context context) {
		if (networkInfoMobile.getState().equals(NetworkInfo.State.DISCONNECTED)
			&& networkInfoWifi.getState().equals(NetworkInfo.State.DISCONNECTED)) {
			Toast.makeText(context, R.string.alert_internet_connection_title, Toast.LENGTH_SHORT).show();
			
			context.stopService(new Intent(context, ServiceManager.class));
			isServiceManagerRunning = false;
		}
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