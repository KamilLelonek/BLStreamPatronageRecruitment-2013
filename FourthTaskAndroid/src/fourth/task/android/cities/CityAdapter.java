package fourth.task.android.cities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import fourth.task.android.R;

/**
 * Simple class used for placing City object to list view. It also enables
 * filtering by containing words/characters in item's name.
 */
public class CityAdapter extends BaseAdapter implements Filterable {
	private List<City> cities;
	private ItemFilter mFilter;
	private LayoutInflater inflater;
	
	public CityAdapter(Activity activity, List<City> cities) {
		this.cities = cities;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void add(City city) {
		if (mFilter != null) {
			List<City> originalValues = mFilter.getOriginalValues();
			originalValues.add(city);
		}
		else {
			cities.add(city);
		}
		notifyDataSetChanged();
	}
	
	public void remove(City city) {
		cities.remove(city);
		if (mFilter != null) {
			mFilter.getOriginalValues().remove(city);
		}
		notifyDataSetChanged();
	}
	
	@Override public int getCount() {
		return cities.size();
	}
	
	@Override public City getItem(int position) {
		return cities.get(position);
	}
	
	public int getPosition(City city) {
		return cities.indexOf(city);
	}
	
	@Override public long getItemId(int position) {
		return position;
	}
	
	/**
	 * @return current cities collection
	 */
	public List<City> getCities() {
		return cities;
	}
	
	/**
	 * @return current cities collection as ArrayList
	 */
	public ArrayList<City> getAllCities() {
		return new ArrayList<City>(getCities());
	}
	
	/**
	 * Replaces current cities collection with new one
	 * 
	 * @param cities new Items list
	 */
	public void setItems(List<City> cities) {
		this.cities.clear();
		this.cities.addAll(cities);
		notifyDataSetChanged();
	}
	
	@Override public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_text_view, null);
		}
		
		ViewHolder viewHolder = (ViewHolder) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			
			viewHolder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
			viewHolder.textViewTemperature = (TextView) convertView.findViewById(R.id.textViewTemperature);
			viewHolder.imageViewWeather = (ImageView) convertView.findViewById(R.id.imageViewWeather);
			viewHolder.imageViewWeather.setTag(position);
			
			convertView.setTag(viewHolder);
		}
		
		// Keeping views in item tag allows to avoid invoking findViewById
		// every time. It makes rendering faster.
		City currentItem = getItem(position);
		viewHolder.textViewName.setText(currentItem.getName());
		viewHolder.textViewTemperature.setText(currentItem.getTemperature());
		
		new GetViewTask(viewHolder, position).execute(currentItem); // rendering view moved to background
		
		return convertView;
	}
	
	private class ViewHolder {
		TextView textViewName;
		TextView textViewTemperature;
		ImageView imageViewWeather;
	}
	
	/**
	 * Background task for decoding bitmap from byte array. It usually takes
	 * some significant time and should be done in background.
	 */
	private class GetViewTask extends AsyncTask<City, Void, Bitmap> {
		private ViewHolder viewHolder;
		private int position;
		
		public GetViewTask(ViewHolder viewHolder, int position) {
			this.viewHolder = viewHolder;
			this.position = position;
		}
		
		@Override protected Bitmap doInBackground(City... params) {
			return params[0].getBitmap();
		}
		
		@Override protected void onPostExecute(Bitmap result) {
			int forPosition = (Integer) viewHolder.imageViewWeather.getTag();
			if (forPosition == this.position) { // to avoid switching views
				viewHolder.imageViewWeather.setImageBitmap(result);
			}
		}
	}
	
	/**
	 * When filter has changed displayed data we still want to serialize
	 * original source before filtering. This method allows us to do it by
	 * reverting previous view.
	 */
	public void revertData() {
		// filter has been used already, data in listView may be changed
		if (mFilter != null) {
			List<City> originalValues = ((ItemFilter) getFilter()).getOriginalValues();
			if (originalValues != null) {
				cities = originalValues;
				notifyDataSetChanged();
			}
		}
	}
	
	@Override public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ItemFilter();
		}
		return mFilter;
	}
	
	/**
	 * Inner class used to filtering list of cities
	 */
	private class ItemFilter extends Filter {
		// A copy of the original cities array, initialized from and then used
		// instead as soon as the mFilter ArrayFilter is used. cities will then
		// only contain the filtered values.
		private List<City> mOriginalValues;
		
		public List<City> getOriginalValues() {
			return mOriginalValues;
		}
		
		@Override protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			if (mOriginalValues == null) {
				mOriginalValues = new ArrayList<City>(cities);
			}
			
			if (prefix == null || prefix.length() == 0) {
				ArrayList<City> list;
				list = new ArrayList<City>(mOriginalValues);
				results.values = list;
				results.count = list.size();
			}
			else {
				String prefixString = prefix.toString().toLowerCase();
				
				ArrayList<City> values;
				values = new ArrayList<City>(mOriginalValues);
				
				final int count = values.size();
				final ArrayList<City> newValues = new ArrayList<City>();
				
				for (int i = 0; i < count; i++) {
					final City value = values.get(i);
					final String valueText = value.toString().toLowerCase();
					
					// First match against the whole, non-splitted value
					if (valueText.contains(prefixString)) {
						newValues.add(value);
					}
					else {
						final String[] words = valueText.split(" ");
						final int wordCount = words.length;
						
						// Start at index 0, in case valueText starts with space(s)
						for (int k = 0; k < wordCount; k++) {
							if (words[k].contains(prefixString)) {
								newValues.add(value);
								break;
							}
						}
					}
				}
				
				results.values = newValues;
				results.count = newValues.size();
			}
			return results;
		}
		
		@SuppressWarnings("unchecked") @Override protected void publishResults(CharSequence constraint,
			FilterResults results) {
			cities = (List<City>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			}
			else {
				notifyDataSetInvalidated();
			}
		}
	}
}