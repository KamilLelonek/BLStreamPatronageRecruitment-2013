package fourth.task.android;

import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import fourth.task.android.cities.City;
import fourth.task.android.cities.CityAdapter;
import fourth.task.android.utils.ApplicationObject;
import fourth.task.android.utils.DialogFragmentAddEdit;
import fourth.task.android.utils.PreferencesManager;

/**
 * Fragment presenting main application view with list of cites and its weather.
 */
public class ListViewFragment extends ListFragment implements DialogFragmentAddEdit.NoticeDialogListener {
	private static final long serialVersionUID = 1L;
	public static final String STRING_CURRENT_CITY = "current_city";
	
	private FourthTaskAndroid activity;
	private PreferencesManager preferencesManager;
	private CityAdapter cityAdapter;
	
	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (FourthTaskAndroid) activity;
		
		preferencesManager = new PreferencesManager(activity);
	}
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Indicate that fragment provide additional options menu
		setHasOptionsMenu(true);
	}
	
	@Override public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		registerForContextMenu(getListView());
	}
	
	/* Deserializing cities */
	@Override public void onStart() {
		super.onStart();
		if (cityAdapter == null) {
			readcities();
		}
		ApplicationObject app = ((ApplicationObject) activity.getApplication());
		app.setCityAdapter(cityAdapter);
	}
	
	private void readcities() {
		List<City> cities = preferencesManager.readListFromFile();
		cityAdapter = new CityAdapter(activity, cities);
		setListAdapter(cityAdapter);
	}
	
	/* Serializing cities */
	@Override public void onStop() {
		cityAdapter.revertData();
		preferencesManager.saveListToFile(cityAdapter.getCities());
		super.onStop();
	}
	
	@Override public void onDialogPositiveClick(City newCity, City oldCity) {
		if (oldCity != null) {
			cityAdapter.remove(oldCity);
		}
		cityAdapter.add(newCity);
	}
	
	/**
	 * Context menu initialization. ContextMenu is used to edit or remove city
	 * from list.
	 */
	@Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		activity.getMenuInflater().inflate(R.menu.menu_context_list_fragment, menu);
	}
	
	@Override public boolean onContextItemSelected(MenuItem city) {
		int cityPosition = ((AdapterContextMenuInfo) city.getMenuInfo()).position;
		City selectedCity = cityAdapter.getItem(cityPosition);
		
		switch (city.getItemId()) {
			case R.id.menu_edit:
				return editCity(selectedCity);
			case R.id.menu_delete:
				cityAdapter.remove(selectedCity);
				return true;
			default:
				return super.onContextItemSelected(city);
		}
	}
	
	/**
	 * Helper method which edits selected city by showing dialog with filled
	 * views from selected city.
	 * 
	 * @param cityToEdit city that need to be edited
	 * @return true to handle context menu for selected city in list view
	 */
	private boolean editCity(City cityToEdit) {
		Bundle cityBundle = new Bundle();
		cityBundle.putSerializable(STRING_CURRENT_CITY, cityToEdit);
		
		DialogFragment newFragment = new DialogFragmentAddEdit();
		newFragment.setArguments(cityBundle);
		newFragment.show(getFragmentManager(), "edit_city");
		
		return true;
	}
	
	/**
	 * Options menu initialization. OptionsMenu is used to add new city to list.
	 */
	
	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_options_list_fragment, menu);
		intitializeOptionsMenu(menu);
	}
	
	private boolean intitializeOptionsMenu(Menu menu) {
		SearchView filterTextView = (SearchView) menu.findItem(R.id.menu_filter).getActionView();
		filterTextView.setQueryHint(getString(R.string.label_filterHint));
		filterTextView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override public boolean onQueryTextSubmit(String query) {
				cityAdapter.getFilter().filter(query);
				return false;
			}
			
			@Override public boolean onQueryTextChange(String newText) {
				cityAdapter.getFilter().filter(newText);
				return false;
			}
		});
		
		MenuItem menuItem = menu.findItem(R.id.menu_filter);
		menuItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override public boolean onMenuItemActionCollapse(MenuItem item) {
				cityAdapter.revertData();
				return true;
			}
			
			@Override public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}
		});
		
		return true;
	}
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_add:
				new DialogFragmentAddEdit().show(getFragmentManager(), "add_city");
		}
		return super.onOptionsItemSelected(item);
	}
}