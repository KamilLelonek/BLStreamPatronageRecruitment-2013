package first.task.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class FirstTaskAndroid extends FragmentActivity {
	private ItemAdapter itemAdapter;
	private PreferencesManager preferencesManager;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_task_android);
		
		preferencesManager = new PreferencesManager(this);
		
		/* List initialization */
		ListView listView = (ListView) findViewById(R.id.listView);
		registerForContextMenu(listView);
		itemAdapter = new ItemAdapter(this);
		listView.setAdapter(itemAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> adapter, View v, int clickedPosition, long id) {
				itemAdapter.checkItem(clickedPosition);
			}
		});
		
		final Button buttonAddNewItem = (Button) findViewById(R.id.buttonAddNewItem);
		EditText editTextFilter = (EditText) findViewById(R.id.editTextFilter);
		editTextFilter.addTextChangedListener(new TextWatcher() {
			@Override public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				buttonAddNewItem.setEnabled(cs.length() != 0 ? false : true);
				itemAdapter.getFilter().filter(cs);
			}
			
			@Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override public void afterTextChanged(Editable arg0) {}
		});
	}
	
	public void addNewItemClick(View v) {
		showDialog(new Item(""));
	}
	
	private void showDialog(final Item item) {
		View dialogView = getLayoutInflater().inflate(R.layout.fragment_add_dialog, null);
		final EditText editText = (EditText) dialogView.findViewById(R.id.editTextItemName);
		editText.setText(item.getName());
		editText.selectAll();
		
		new AlertDialog.Builder(this).setView(dialogView).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int id) {
				onDialogPositiveClick(editText.getText().toString(), item);
			}
		}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		}).create().show();
	}
	
	@Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.menu_context_list, menu);
	}
	
	@Override public boolean onContextItemSelected(MenuItem item) {
		int clickedPosition = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
		
		switch (item.getItemId()) {
			case R.id.menu_rename:
				showDialog(itemAdapter.getItem(clickedPosition));
				break;
			case R.id.menu_delete:
				itemAdapter.remove(itemAdapter.getItem(clickedPosition));
				break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void onDialogPositiveClick(String name, Item item) {
		if (name.length() == 0) {
			Toast.makeText(this, R.string.alert_name_not_correct, Toast.LENGTH_SHORT).show();
		}
		else if (itemAdapter.contains(name)) {
			Toast.makeText(this, R.string.alert_item_exists, Toast.LENGTH_SHORT).show();
		}
		else {
			itemAdapter.remove(item);
			itemAdapter.add(name);
		}
	}
	
	@Override protected void onResume() {
		super.onResume();
		if (!preferencesManager.isFirstRun()) {
			itemAdapter.setSource(preferencesManager.deserializeQuotes());
		}
		else {
			itemAdapter.setSourceFromArray(getResources().getStringArray(R.array.items));
			preferencesManager.setFirstRunFalse();
		}
	}
	
	@Override protected void onPause() {
		itemAdapter.revertData();
		preferencesManager.serializeQuotes(itemAdapter.getSource());
		super.onPause();
	}
}