package second.task.android.items;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Simple class to represent particular item
 */
/* implementing Serializable for saving in file and Parcelable for passing
 * between activities */
public class Item implements Serializable, Parcelable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String color;
	private double latitude;
	private double longitude;
	private boolean checked;
	
	public Item(String name, double latitude, double longitude, String color) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.color = color;
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
	
	public boolean isChecked() {
		return checked;
	}
	
	/**
	 * Changing item check state.
	 */
	/* if (checked == true) checked = false; else checked = true; */
	public void switchChecked() {
		this.checked ^= true;
	}
	
	/* Returns simple marker to draw on map. */
	public MarkerOptions getMarker() {
		// TODO add specific icon when needed:
		// .icon(BitmapDescriptorFactory.fromResource(R.drawable.point));
		return new MarkerOptions().position(new LatLng(latitude, longitude)).title(name).icon(BitmapDescriptorFactory.defaultMarker());
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
	
	/**
	 * Parcelable section
	 */
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
		this(item.name, item.latitude, item.longitude, item.color);
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