package fourth.task.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fourth.task.android.items.Item;
import fourth.task.android.services.WeatherService;
import fourth.task.android.utils.ApplicationObject;

public class MapViewFragment extends Fragment {
	private final int ONE_SEC_IN_MILISEC = 1000;
	private final String STRING_LOG_TAG = "MapViewFragment";
	
	private Activity activity;
	private GoogleMap mMap;
	private LocalBroadcastManager localBroadcastManager;
	private BroadcastReceiver broadcastReceiver;
	private ApplicationObject applicationObject;
	
	// For saving view state.
	private View viewHolder;
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		broadcastReceiver = new BroadcastReceiver() {
			@Override public void onReceive(Context context, Intent intent) {
				// Refresh map view
				mMap.clear();
				addItemsToMap();
			}
		};
	}
	
	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		localBroadcastManager = LocalBroadcastManager.getInstance(activity);
		applicationObject = (ApplicationObject) activity.getApplication();
	}
	
	@Override public void onResume() {
		super.onResume();
		localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(WeatherService.INTENT_FILTER));
	}
	
	@Override public void onPause() {
		localBroadcastManager.unregisterReceiver(broadcastReceiver);
		super.onPause();
	}
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/* viewHolder allows to cache initialized map view. Once we've inflated
		 * MapViewFragment it doesn't need to be inflated any more. */
		if (viewHolder == null) {
			viewHolder = inflater.inflate(R.layout.fragment_map, container, false);
		}
		return viewHolder;
	}
	
	@Override public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setUpMapIfNeeded();
	}
	
	/**
	 * Setting up map: enabling compass, zooming buttons and current location.
	 * 
	 * @param view
	 */
	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				Log.d(STRING_LOG_TAG, "Preparing map for first use.");
				
				mMap.setMyLocationEnabled(true);
				/* Setting up location listener for map */
				mMap.setLocationSource(new FollowMeLocationSource());
				mMap.setIndoorEnabled(true);
				
				/* UI Settings */
				UiSettings uiSettings = mMap.getUiSettings();
				uiSettings.setCompassEnabled(true);
				uiSettings.setMyLocationButtonEnabled(true);
			}
			
			/* Filling map with points */
			addItemsToMap();
		}
	}
	
	/**
	 * Simple items managing.
	 */
	/* Puts markers on map from items list */
	private void addItemsToMap() {
		ArrayList<Item> items = applicationObject.getItemAdapter().getAllItems();
		ArrayList<MarkerOptions> markers = createMarkerList(items);
		for (MarkerOptions marker : markers) {
			addMarkerToMap(marker);
		}
	}
	
	/* Creates marker list from item list */
	private ArrayList<MarkerOptions> createMarkerList(ArrayList<Item> items) {
		ArrayList<MarkerOptions> markers = new ArrayList<MarkerOptions>();
		for (Item item : items) {
			markers.add(item.getMarker());
		}
		return markers;
	}
	
	/* Adds one marker to map */
	private void addMarkerToMap(MarkerOptions marker) {
		mMap.addMarker(marker);
	}
	
	/**
	 * Inner class for managing location. It handles location changes and
	 * notifies about them. This class also draw lines on map to selected
	 * points.
	 */
	private class FollowMeLocationSource implements LocationSource, LocationListener {
		private OnLocationChangedListener onLocationChangedListener;
		private LocationManager locationManager;
		
		private FollowMeLocationSource() {
			locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		}
		
		@Override public void activate(OnLocationChangedListener onLocationChangedListener) {
			Log.d(STRING_LOG_TAG, "Activating location's changes listening.");
			
			this.onLocationChangedListener = onLocationChangedListener;
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, ONE_SEC_IN_MILISEC, 1, this);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ONE_SEC_IN_MILISEC, 1, this);
		}
		
		@Override public void deactivate() {
			Log.d(STRING_LOG_TAG, "Deactivating location's changes listening.");
			
			this.onLocationChangedListener = null;
			locationManager.removeUpdates(this);
		}
		
		/* When location is changes camera is moving and lines are being redrawn */
		@Override public void onLocationChanged(Location location) {
			Log.d(STRING_LOG_TAG, "Location has been changed.");
			
			onLocationChangedListener.onLocationChanged(location);
			mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
		}
		
		/* It is not necessary to use this methods yet */
		@Override public void onProviderDisabled(String provider) {}
		
		@Override public void onProviderEnabled(String provider) {}
		
		@Override public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
}