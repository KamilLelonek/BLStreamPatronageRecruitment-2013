package fourth.task.android.utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import fourth.task.android.items.ItemAdapter;

/**
 * ApplicationObject keeps information about current network state.
 */
public class ApplicationObject extends Application {
	/**************************************************
	 ************** Item Adapter Manager **************
	 **************************************************/
	ItemAdapter itemAdapter;
	
	public ItemAdapter getItemAdapter() {
		return this.itemAdapter;
	}
	
	public void setItemAdapter(ItemAdapter itemAdapter) {
		this.itemAdapter = itemAdapter;
	}
	
	/**************************************************
	 *************** Networking Manager ***************
	 **************************************************/
	private WifiManager wifiManager;
	private ConnectivityManager connectivityManager;
	
	private WifiManager getWifiManager() {
		if (wifiManager == null) {
			wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		}
		return wifiManager;
	}
	
	private ConnectivityManager getConnectivityManager() {
		if (connectivityManager == null) {
			connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return connectivityManager;
	}
	
	public boolean isWiFiEnabled() {
		return getWifiManager().isWifiEnabled()
			&& getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
	}
	
	public boolean isMobileDataEnabled() {
		return getConnectivityManager().getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
	}
	
	public boolean isConnectedToInternet() {
		return isWiFiEnabled() || isMobileDataEnabled();
	}
}