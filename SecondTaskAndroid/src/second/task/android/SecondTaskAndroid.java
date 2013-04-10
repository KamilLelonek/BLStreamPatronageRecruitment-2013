package second.task.android;

import java.util.List;

import second.task.android.items.Item;
import second.task.android.items.ItemAdapter;
import second.task.android.items.ItemModel;
import second.task.android.utils.FragmentDialogAddEdit;
import second.task.android.utils.PreferencesManager;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class SecondTaskAndroid extends FragmentActivity implements FragmentDialogAddEdit.NoticeDialogListener {
	private final String STRING_ADD_ITEM = "add_item";
	private final String STRING_EDIT_ITEM = "edit_item";
	public static final String STRING_CURRENT_ITEM = "current_item";
	public static final String STRING_CHECKED_ITEMS = "checked_items";
	
	private ListView listView;
	private List<Item> items;
	private ItemAdapter itemAdapter;
	
	private PreferencesManager preferencesManager;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_task_android);
		
		preferencesManager = new PreferencesManager(this);
		
		/* List initialization */
		listView = (ListView) findViewById(R.id.ListView);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> adapter, View v, int clickedPosition, long id) {
				itemAdapter.checkItem(clickedPosition);
			}
		});
	}
	
	/* Serializing items */
	@Override protected void onResume() {
		super.onResume();
		if (!preferencesManager.isFirstRun()) {
			items = preferencesManager.deserializeQuotes();
		}
		else {
			items = ItemModel.getItems();
			preferencesManager.setFirstRunFalse();
		}
		itemAdapter = new ItemAdapter(this, items);
		listView.setAdapter(itemAdapter);
	}
	
	/* Deserializing items */
	@Override protected void onPause() {
		itemAdapter.revertData();
		preferencesManager.serializeQuotes(itemAdapter.getItems());
		super.onPause();
	}
	
	@Override public void onDialogPositiveClick(Item newItem, Item oldItem) {
		if (oldItem != null) {
			itemAdapter.remove(oldItem);
		}
		itemAdapter.add(newItem);
	}
	
	// TODO maybe I should create button in ActionBar and connect it with this method?
	public void showMapClick(View w) {
		Intent showMapIntent = new Intent(SecondTaskAndroid.this, CompassActivity.class);
		showMapIntent.putParcelableArrayListExtra(STRING_CHECKED_ITEMS, itemAdapter.getCheckedItems());
		startActivity(showMapIntent);
	}
	
	/**
	 * Context menu initialization. ContextMenu is used to edit or remove item
	 * from list.
	 */
	@Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.menu_context_list, menu);
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
		
		DialogFragment newFragment = new FragmentDialogAddEdit();
		newFragment.setArguments(itemBundle);
		newFragment.show(getFragmentManager(), STRING_EDIT_ITEM);
		
		return true;
	}
	
	/**
	 * Options menu initialization. OptionsMenu is used to add new item to list.
	 */
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_second_task_android, menu);
		return intitializeOptionsMenu(menu);
	}
	
	private boolean intitializeOptionsMenu(Menu menu) {
		MenuItem searchMenuItem = menu.findItem(R.id.menu_filter);
		searchMenuItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override public boolean onMenuItemActionCollapse(MenuItem item) {
				itemAdapter.revertData();
				return true;
			}
			
			@Override public boolean onMenuItemActionExpand(MenuItem item) {
				return true;
			}
		});
		
		SearchView filterTextView = (SearchView) searchMenuItem.getActionView();
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
		
		return true;
	}
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_add:
				DialogFragment newFragment = new FragmentDialogAddEdit();
				newFragment.show(getFragmentManager(), STRING_ADD_ITEM);
		}
		return super.onOptionsItemSelected(item);
	}
}