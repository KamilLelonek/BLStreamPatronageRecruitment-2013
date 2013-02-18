package fourth.task.android.items;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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
 * Simple class used for placing Item object to list view. It also enables
 * filtering by containing words/characters in item's name.
 */
public class ItemAdapter extends BaseAdapter implements Filterable {
	private List<Item> items;
	private ItemFilter mFilter;
	private LayoutInflater inflater;
	
	public ItemAdapter(Activity activity, List<Item> items) {
		this.items = items;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void add(Item item) {
		if (mFilter != null) {
			List<Item> originalValues = mFilter.getOriginalValues();
			originalValues.add(item);
		}
		else {
			items.add(item);
		}
		notifyDataSetChanged();
	}
	
	public void remove(Item item) {
		items.remove(item);
		if (mFilter != null) {
			mFilter.getOriginalValues().remove(item);
		}
		notifyDataSetChanged();
	}
	
	@Override public int getCount() {
		return items.size();
	}
	
	@Override public Item getItem(int position) {
		return items.get(position);
	}
	
	public int getPosition(Item item) {
		return items.indexOf(item);
	}
	
	@Override public long getItemId(int position) {
		return position;
	}
	
	/**
	 * @return current items collection
	 */
	public List<Item> getItems() {
		return items;
	}
	
	/**
	 * @return current items collection as ArrayList
	 */
	public ArrayList<Item> getAllItems() {
		return new ArrayList<Item>(getItems());
	}
	
	/**
	 * Replaces current items collection with new one
	 * 
	 * @param items new Items list
	 */
	public void setItems(List<Item> items) {
		this.items.clear();
		this.items.addAll(items);
		notifyDataSetChanged();
	}
	
	@Override public View getView(int position, View convertView, ViewGroup parent) {
		View listItem = convertView;
		if (convertView == null) {
			listItem = inflater.inflate(R.layout.item_text_view, null);
		}
		ViewHolder viewHolder = (ViewHolder) listItem.getTag();
		
		/* A little bit of optimization by using modified ViewHolder pattern */
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			
			viewHolder.textViewName = (TextView) listItem.findViewById(R.id.textViewName);
			viewHolder.textViewTemperature = (TextView) listItem.findViewById(R.id.textViewTemperature);
			viewHolder.imageViewWeather = (ImageView) listItem.findViewById(R.id.imageViewWeather);
			
			listItem.setTag(viewHolder);
			viewHolder.imageViewWeather.setTag(position);
		}
		
		// Keeping views in item tag allows to avoid invoking findViewById
		// every time. It makes rendering faster.
		Item currentItem = getItem(position);
		viewHolder.textViewName.setText(currentItem.getName());
		viewHolder.textViewTemperature.setText(currentItem.getTemperature());
		
		if (currentItem.getBitmap() != null) {
			viewHolder.imageViewWeather.setImageBitmap(currentItem.getBitmap());
		}
		
		// It works but doesn't look nice
		// TODO Maybe implement (uncomment) it later
		// listItem.setBackgroundColor(Color.parseColor(currentItem.getColor()));
		
		return listItem;
	}
	
	private class ViewHolder {
		TextView textViewName;
		TextView textViewTemperature;
		ImageView imageViewWeather;
	}
	
	/**
	 * When filter has changed displayed data we still want to serialize
	 * original source before filtering. This method allows us to do it by
	 * reverting previous view.
	 */
	public void revertData() {
		// filter has been used already, data in listView may be changed
		if (mFilter != null) {
			List<Item> originalValues = ((ItemFilter) getFilter()).getOriginalValues();
			if (originalValues != null) {
				items = originalValues;
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
	 * Inner class used to filtering list of items
	 */
	private class ItemFilter extends Filter {
		// A copy of the original items array, initialized from and then used
		// instead as soon as the mFilter ArrayFilter is used. items will then
		// only contain the filtered values.
		private List<Item> mOriginalValues;
		
		public List<Item> getOriginalValues() {
			return mOriginalValues;
		}
		
		@Override protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();
			if (mOriginalValues == null) {
				mOriginalValues = new ArrayList<Item>(items);
			}
			
			if (prefix == null || prefix.length() == 0) {
				ArrayList<Item> list;
				list = new ArrayList<Item>(mOriginalValues);
				results.values = list;
				results.count = list.size();
			}
			else {
				String prefixString = prefix.toString().toLowerCase();
				
				ArrayList<Item> values;
				values = new ArrayList<Item>(mOriginalValues);
				
				final int count = values.size();
				final ArrayList<Item> newValues = new ArrayList<Item>();
				
				for (int i = 0; i < count; i++) {
					final Item value = values.get(i);
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
			items = (List<Item>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			}
			else {
				notifyDataSetInvalidated();
			}
		}
	}
}