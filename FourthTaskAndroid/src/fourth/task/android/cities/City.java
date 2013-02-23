package fourth.task.android.cities;

import java.io.Serializable;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import fourth.task.android.utils.BitmapManager;

/**
 * Simple class to represent particular item. Implements Serializable for saving
 * in file.
 */
public class City implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String DEGREE_CELCIUS = " \u2103";
	@SuppressWarnings("unused") private static final String DEGREE_FAHRENHEIT = " \u2109";
	
	private String name;
	private String color;
	private String temperature;
	private double latitude;
	private double longitude;
	
	private String connectionString;
	private byte[] bitmapArray;
	
	public City(String name, double latitude, double longitude, String color) {
		this(name, latitude, longitude, "", color, null);
	}
	
	public City(String name, double latitude, double longitude, String temperature, String color, byte[] bitmapArray) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.temperature = temperature;
		this.color = color;
		this.bitmapArray = bitmapArray;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public String getTemperature() {
		return this.temperature;
	}
	
	public void setTemperature(String temperature) {
		this.temperature = temperature + DEGREE_CELCIUS;
	}
	
	public String getConnectionString() {
		return connectionString;
	}
	
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	
	public byte[] getBitmapArray() {
		return this.bitmapArray;
	}
	
	public void setBitmapArray(byte[] bitmapArray) {
		this.bitmapArray = bitmapArray;
	}
	
	public Bitmap getBitmap() {
		return BitmapManager.deserializeBitmap(bitmapArray);
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmapArray = BitmapManager.serializeBitmap(bitmap);
	}
	
	/* Returns simple marker to draw on map. */
	public MarkerOptions getMarker() {
		return new MarkerOptions().position(new LatLng(latitude, longitude)).title(name).snippet(getTemperature())
			.icon(BitmapDescriptorFactory.defaultMarker());
	}
	
	/* toString and equals added to make renaming, deleting and adding operation easier */
	@Override public String toString() {
		return getName();
	}
	
	@Override public boolean equals(Object o) {
		if (o == null || !(o instanceof City)) return false;
		return this.name.equals(((City) o).name);
	}
	
	@Override public int hashCode() {
		return this.name.hashCode();
	}
}