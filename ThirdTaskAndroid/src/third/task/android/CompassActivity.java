package third.task.android;

import java.util.ArrayList;

import third.task.android.compass.CompassInterface;
import third.task.android.items.Item;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * This class is used for representing GoogleMap and handling phone orientation
 * changes to show current bearing on map.
 */
public class CompassActivity extends Activity implements SensorEventListener {
	private final int ONE_SEC_IN_MILISEC = 1000;
	private final int SENSOR_TYPE_BEARING = 3;
	private final String STRING_LOG_TAG = "CompassActivity";
	
	/* Map section */
	private GoogleMap mMap;
	private LatLng currentLatLng;
	private GroundOverlay groundOverlay;
	
	/* Sensor section */
	private SensorManager sensorManager;
	private Sensor compass;
	
	private ArrayList<Item> checkedItems;
	private CompassInterface compassRadar;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass);
		compassRadar = (CompassInterface) findViewById(R.id.compassRadar);
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		compass = sensorManager.getDefaultSensor(SENSOR_TYPE_BEARING);
		
		setUpMapIfNeeded();
	}
	
	@Override protected void onResume() {
		super.onResume();
		connectAccelerometerAndShowCompass();
	}
	
	@Override protected void onPause() {
		disconnectAccelerometerAndHideCompass();
		super.onPause();
	}
	
	/**
	 * Method which allows to turn on/off radar bearing our facing.
	 */
	public void manageShowingRadarClick(@SuppressWarnings("unused") View w) {
		if (compassRadar.isShown()) {
			disconnectAccelerometerAndHideCompass();
		}
		else {
			connectAccelerometerAndShowCompass();
		}
	}
	
	/**
	 * Registers accelerometer listener and starts drawing map overlay bearing
	 * current position;
	 */
	private void connectAccelerometerAndShowCompass() {
		Log.d(STRING_LOG_TAG, "Turning on compass.");
		
		sensorManager.registerListener(this, compass, SensorManager.SENSOR_DELAY_GAME);
		compassRadar.setVisibility(View.VISIBLE);
		groundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions().image(
			BitmapDescriptorFactory.fromResource(R.drawable.ic_menu_popup)).position(new LatLng(0., 0.), 200));
	}
	
	/**
	 * Unregisters accelerometer listener and stops drawing map overlays
	 */
	private void disconnectAccelerometerAndHideCompass() {
		Log.d(STRING_LOG_TAG, "Turning off compass.");
		
		compassRadar.setVisibility(View.GONE);
		sensorManager.unregisterListener(this);
		groundOverlay.remove();
	}
	
	/**
	 * Setting up map: enabling compass, zooming buttons and current location.
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
			checkedItems = getIntent().getExtras().getParcelableArrayList(ThirdTaskAndroid.STRING_CHECKED_ITEMS);
			if (checkedItems != null) {
				addItemsToMap(checkedItems);
			}
		}
	}
	
	/**
	 * Simple items managing.
	 */
	/* Puts markers on map from items list */
	private void addItemsToMap(ArrayList<Item> items) {
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
	
	@Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	@Override public void onSensorChanged(SensorEvent event) {
		if (currentLatLng != null) {
			Point point = mMap.getProjection().toScreenLocation(currentLatLng);
			compassRadar.setDirection((int) event.values[0], point);
			
			/* Arrow overlay pined to current location */
			groundOverlay.setPosition(currentLatLng);
			groundOverlay.setBearing(event.values[0]);
		}
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
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
			drawLines(location);
			setCurrentLatLng(location);
			mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));
		}
		
		private void setCurrentLatLng(Location location) {
			currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		}
		
		/* Drawing lines form selected location to all points on map */
		private void drawLines(Location location) {
			for (Item item : checkedItems) {
				mMap.addPolyline(new PolylineOptions()
					.add(new LatLng(location.getLatitude(), location.getLongitude()),
						new LatLng(item.getLatitude(), item.getLongitude())).width(5)
					.color(Color.parseColor(item.getColor())));
			}
		}
		
		/* It is not necessary to use this methods yet */
		@Override public void onProviderDisabled(String provider) {}
		
		@Override public void onProviderEnabled(String provider) {}
		
		@Override public void onStatusChanged(String provider, int status, Bundle extras) {}
	}
}