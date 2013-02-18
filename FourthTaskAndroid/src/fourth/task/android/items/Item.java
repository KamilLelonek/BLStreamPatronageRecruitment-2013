package fourth.task.android.items;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Simple class to represent particular item. Implements Serializable for saving
 * in file and Parcelable for passing between activities.
 */
public class Item implements Serializable, Parcelable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String color;
	private String temperature;
	private double latitude;
	private double longitude;
	
	private String connectionString;
	private byte[] bitmapArray;
	
	public Item(String name, double latitude, double longitude, String color) {
		this(name, latitude, longitude, "", color, null);
	}
	
	public Item(String name, double latitude, double longitude, String temperature, String color, byte[] bitmapArray) {
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
		this.temperature = temperature;
	}
	
	public String getConnectionString() {
		return connectionString;
	}
	
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	
	public Bitmap getBitmap() {
		return BitmapSerializator.deserializeBitmap(bitmapArray);
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmapArray = BitmapSerializator.serializeBitmap(bitmap);
	}
	
	/* Returns simple marker to draw on map. */
	public MarkerOptions getMarker() {
		// TODO add specific icon when needed:
		// .icon(BitmapDescriptorFactory.fromResource(R.drawable.point));
		return new MarkerOptions().position(new LatLng(latitude, longitude)).title(name)
			.snippet(getTemperature() + " Celcius grads").icon(BitmapDescriptorFactory.defaultMarker());
	}
	
	/* toString and equals added to make renaming, deleting and adding operation
	 * easier */
	@Override public String toString() {
		return getName();
	}
	
	@Override public boolean equals(Object o) {
		if (o == null || !(o instanceof Item)) return false;
		return this.name.equals(((Item) o).name);
	}
	
	@Override public int hashCode() {
		return this.name.hashCode();
	}
	
	/********************************************
	 ************ Parcelable section ************
	 ********************************************/
	@Override public int describeContents() {
		return 0;
	}
	
	/* Writes this object (which is also Serializable) to Parcel */
	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeSerializable(this);
	}
	
	/* Reading item form parcel and passing it to another constructor */
	private Item(Parcel in) {
		this((Item) in.readSerializable());
	}
	
	/* Construtor form ready item read from parcel */
	private Item(Item item) {
		this(item.name, item.latitude, item.longitude, item.temperature, item.color, item.bitmapArray);
	}
	
	public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
		@Override public Item createFromParcel(Parcel in) {
			return new Item(in);
		}
		
		@Override public Item[] newArray(int size) {
			return new Item[size];
		}
	};
}