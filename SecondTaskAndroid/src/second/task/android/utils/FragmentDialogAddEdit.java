package second.task.android.utils;

import java.util.Arrays;

import second.task.android.R;
import second.task.android.SecondTaskAndroid;
import second.task.android.items.Item;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class FragmentDialogAddEdit extends DialogFragment {
	private final String REGEX_FOR_SIGNED_DOUBLE_NUMBERS = "-?\\d+(.\\d+)?";
	
	private Activity activity;
	private View dialogView;
	private EditText nameEditText;
	private EditText latitudeEditText;
	private EditText longitudeEditText;
	private Spinner spinnerItemColor;
	
	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callback. Each method
	 * passes the DialogFragment in case the host needs to query it. */
	public interface NoticeDialogListener {
		void onDialogPositiveClick(Item newItem, Item oldItem);
	}
	
	// Use this instance of the interface to deliver action events
	NoticeDialogListener mListener;
	
	// Override the Fragment.onAttach() method to instantiate the
	// NoticeDialogListener
	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (NoticeDialogListener) activity;
		}
		catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
		}
	}
	
	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Initialize particular views if not initialized yet
		if (dialogView == null) {
			dialogView = activity.getLayoutInflater().inflate(R.layout.fragment_add_dialog, null);
		}
		if (nameEditText == null) {
			nameEditText = (EditText) dialogView.findViewById(R.id.EditTextItemName);
		}
		if (latitudeEditText == null) {
			latitudeEditText = (EditText) dialogView.findViewById(R.id.EditTextItemLatitude);
		}
		if (longitudeEditText == null) {
			longitudeEditText = (EditText) dialogView.findViewById(R.id.EditTextItemLongitude);
		}
		if (spinnerItemColor == null) {
			spinnerItemColor = (Spinner) dialogView.findViewById(R.id.SpinnerItemColor);
		}
		
		final Item currentItem = getCurrentItem();
		if (currentItem != null) {
			nameEditText.setText(currentItem.getName());
			nameEditText.selectAll();
			latitudeEditText.setText(String.valueOf(currentItem.getLatitude()));
			longitudeEditText.setText(String.valueOf(currentItem.getLongitude()));
			spinnerItemColor.setSelection(Arrays.asList(getResources().getStringArray(R.array.colors)).indexOf(
					currentItem.getColor().toUpperCase()));
		}
		
		// Build custom alert dialog, create it and return as a result
		return new AlertDialog.Builder(activity).setTitle(R.string.label_manageItem).setView(dialogView)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int id) {
						if (!isInputDataValid()) {
							Toast.makeText(activity, R.string.alert_data_not_valid, Toast.LENGTH_SHORT).show();
							// TODO maybe I should show this windows
							// again?
						}
						else {
							mListener.onDialogPositiveClick(
									new Item(nameEditText.getText().toString(), Double.valueOf(latitudeEditText
											.getText().toString()), Double.valueOf(longitudeEditText.getText()
											.toString()), spinnerItemColor.getSelectedItem().toString()), currentItem);
						}
					}
				}).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				}).create();
	}
	
	private Item getCurrentItem() {
		Item currentItem = null;
		Bundle itemBundle = getArguments();
		
		if (itemBundle != null) {
			currentItem = (Item) itemBundle.getSerializable(SecondTaskAndroid.STRING_CURRENT_ITEM);
		}
		
		return currentItem;
	}
	
	/**
	 * Validates if provided data in EditTexts and Spinner is correct. Name
	 * should be non zero-length string, Latitude and Longitude must be positive
	 * or negative double numbers
	 * 
	 * @return validating result
	 */
	private boolean isInputDataValid() {
		if (nameEditText.getText().toString().length() == 0) return false;
		
		EditText[] editTextArray = new EditText[] { latitudeEditText, longitudeEditText };
		for (EditText editText : editTextArray) {
			String temp = editText.getText().toString();
			if (temp.length() == 0 || !temp.matches(REGEX_FOR_SIGNED_DOUBLE_NUMBERS)) return false;
		}
		return spinnerItemColor.getSelectedItemPosition() != 0;
	}
}