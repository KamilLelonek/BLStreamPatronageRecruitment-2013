package first.task.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * Simple class used for representing Item object to list view. It also enables
 * filtering by containing words/characters in item's name.
 */
public class ItemAdapter extends ArrayAdapter<Item> implements Filterable {
	private List<Item> items;
	private ItemFilter mFilter;
	
	public ItemAdapter(Context context) {
		super(context, R.layout.checked_text_view, android.R.id.text1, new ArrayList<Item>());
		items = new ArrayList<Item>();
	}
	
	@Override public void add(Item item) {
		items.add(item);
		updateFilter();
		notifyDataSetChanged();
	}
	
	public void add(String name) {
		add(new Item(name));
	}
	
	@Override public void remove(Item item) {
		items.remove(item);
		updateFilter();
		notifyDataSetChanged();
	}
	
	/**
	 * Causes that filter has the newest data immediately after any changes. It
	 * is needed for reverting because
	 */
	private void updateFilter() {
		if (mFilter != null) {
			mFilter.updateOriginalValues(items);
		}
	}
	
	@Override public int getCount() {
		return items.size();
	}
	
	@Override public Item getItem(int position) {
		return items.get(position);
	}
	
	@Override public int getPosition(Item item) {
		return items.indexOf(item);
	}
	
	public boolean contains(String itemName) {
		return items.contains(new Item(itemName));
	}
	
	public void setSource(List<Item> list) {
		items.clear();
		items.addAll(list);
		notifyDataSetChanged();
	}
	
	public void setSourceFromArray(String[] strings) {
		List<Item> items = new ArrayList<Item>();
		for (String itemName : strings) {
			items.add(new Item(itemName));
		}
		setSource(items);
	}
	
	public List<Item> getSource() {
		return items;
	}
	
	/**
	 * Checks item's text box
	 */
	public void checkItem(int clickedPosition) {
		getItem(clickedPosition).switchChecked();
		notifyDataSetChanged();
	}
	
	/**
	 * When filter has changed displayed data we still want to serialize
	 * original source before filtering. This method allows us to do it by
	 * reverting previous view.
	 */
	public void revertData() {
		if (mFilter != null) {	// filter has been used already, data in listView may be changed
			List<Item> originalValues = ((ItemFilter) getFilter()).getOriginalValues();
			if (originalValues != null) {
				items = originalValues;
				notifyDataSetChanged();
			}
		}
	}
	
	@Override public View getView(int position, View convertView, ViewGroup parent) {
		View listItem = super.getView(position, convertView, parent);
		CheckedTextView checkBox = (CheckedTextView) listItem.getTag();
		
		/* A little bit of optimization by using modified ViewHolder pattern */
		if (checkBox == null) {
			checkBox = (CheckedTextView) listItem.findViewById(android.R.id.text1);
			listItem.setTag(checkBox);
		}
		
		// keeping checkBox in item tag allows to avoid invoking findViewById
		// every time when it is needed to make rendering faster
		Item currentItem = getItem(position);
		checkBox.setChecked(currentItem.isChecked());
		checkBox.setText(currentItem.getName());
		return listItem;
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
		private List<Item> mOriginalValues;
		
		public List<Item> getOriginalValues() {
			return mOriginalValues;
		}
		
		public void updateOriginalValues(List<Item> items) {
			if (mOriginalValues != null) {
				mOriginalValues = new ArrayList<Item>(items);
			}
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
				String prefixItem = prefix.toString().toLowerCase(Locale.getDefault());
				
				ArrayList<Item> values;
				values = new ArrayList<Item>(mOriginalValues);
				
				final int count = values.size();
				final ArrayList<Item> newValues = new ArrayList<Item>();
				
				for (int i = 0; i < count; i++) {
					final Item value = values.get(i);
					final String valueText = value.toString().toLowerCase(Locale.getDefault());
					
					if (valueText.contains(prefixItem)) {
						newValues.add(value);
					}
					else {
						final String[] words = valueText.split(" ");
						final int wordCount = words.length;
						
						for (int k = 0; k < wordCount; k++) {
							if (words[k].contains(prefixItem)) {
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
		
		@SuppressWarnings("unchecked") @Override protected void publishResults(CharSequence constraint, FilterResults results) {
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