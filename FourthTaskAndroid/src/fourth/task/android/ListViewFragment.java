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
import fourth.task.android.items.Item;
import fourth.task.android.items.ItemAdapter;
import fourth.task.android.utils.ApplicationObject;
import fourth.task.android.utils.DialogFragmentAddEdit;
import fourth.task.android.utils.PreferencesManager;

public class ListViewFragment extends ListFragment implements DialogFragmentAddEdit.NoticeDialogListener {
	private static final long serialVersionUID = 1L;
	public static final String STRING_CURRENT_ITEM = "current_item";
	
	private FourthTaskAndroid activity;
	private PreferencesManager preferencesManager;
	private ItemAdapter itemAdapter;
	
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
	
	/* Deserializing items */
	@Override public void onStart() {
		super.onStart();
		if (itemAdapter == null) {
			readItems();
		}
		ApplicationObject app = ((ApplicationObject) activity.getApplication());
		app.setItemAdapter(itemAdapter);
	}
	
	private void readItems() {
		List<Item> items = preferencesManager.readListFromFile();
		itemAdapter = new ItemAdapter(activity, items);
		setListAdapter(itemAdapter);
	}
	
	/* Serializing items */
	@Override public void onStop() {
		itemAdapter.revertData();
		preferencesManager.saveListToFile(itemAdapter.getItems());
		super.onStop();
	}
	
	@Override public void onDialogPositiveClick(Item newItem, Item oldItem) {
		if (oldItem != null) {
			itemAdapter.remove(oldItem);
		}
		itemAdapter.add(newItem);
	}
	
	/**
	 * Context menu initialization. ContextMenu is used to edit or remove item
	 * from list.
	 */
	@Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		activity.getMenuInflater().inflate(R.menu.menu_context_list_fragment, menu);
	}
	
	@Override public boolean onContextItemSelected(MenuItem item) {
		int itemPosition = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
		Item selectedItem = itemAdapter.getItem(itemPosition);
		
		switch (item.getItemId()) {
			case R.id.menu_edit:
				return editItem(selectedItem);
			case R.id.menu_delete:
				itemAdapter.remove(selectedItem);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}
	
	/**
	 * Helper method which edits selected item by showing dialog with filled
	 * views from selected item.
	 * 
	 * @param itemToEdit item that need to be edited
	 * @return true to handle context menu for selected item in list view
	 */
	private boolean editItem(Item itemToEdit) {
		Bundle itemBundle = new Bundle();
		itemBundle.putSerializable(STRING_CURRENT_ITEM, itemToEdit);
		
		DialogFragment newFragment = new DialogFragmentAddEdit();
		newFragment.setArguments(itemBundle);
		newFragment.show(getFragmentManager(), "edit_item");
		
		return true;
	}
	
	/**
	 * Options menu initialization. OptionsMenu is used to add new item to list.
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
				itemAdapter.getFilter().filter(query);
				return false;
			}
			
			@Override public boolean onQueryTextChange(String newText) {
				itemAdapter.getFilter().filter(newText);
				return false;
			}
		});
		
		MenuItem menuItem = menu.findItem(R.id.menu_filter);
		menuItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override public boolean onMenuItemActionCollapse(MenuItem item) {
				itemAdapter.revertData();
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
				new DialogFragmentAddEdit().show(getFragmentManager(), "add_item");
		}
		return super.onOptionsItemSelected(item);
	}
}