package fourth.task.android.utils;

import java.io.Serializable;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import fourth.task.android.ListViewFragment;
import fourth.task.android.R;
import fourth.task.android.cities.City;

/**
 * FragmentDialog representing add/edit city view.
 */
public class DialogFragmentAddEdit extends DialogFragment {
	private final String REGEX_FOR_SIGNED_DOUBLE_NUMBERS = "-?\\d+(.\\d+)?";
	
	private boolean isViewInitialized;
	
	private Activity activity;
	private AlertDialog alertDialog;
	private City currentCity;
	
	private View dialogView;
	private EditText nameEditText;
	private EditText latitudeEditText;
	private EditText longitudeEditText;
	private Button okButton;
	private Button cancelButton;
	private Spinner spinnerCityColor;
	
	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callback. Each method
	 * passes the DialogFragment in case the host needs to query it. */
	public interface NoticeDialogListener extends Serializable {
		void onDialogPositiveClick(City newCity, City oldCity);
	}
	
	// Use this instance of the interface to deliver action events
	NoticeDialogListener mListener;
	
	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		this.mListener = (ListViewFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
	}
	
	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Initialize particular views if not initialized yet
		if (!isViewInitialized) {
			dialogView = activity.getLayoutInflater().inflate(R.layout.fragment_add_dialog, null);
			nameEditText = (EditText) dialogView.findViewById(R.id.EditTextCityName);
			latitudeEditText = (EditText) dialogView.findViewById(R.id.EditTextCityLatitude);
			longitudeEditText = (EditText) dialogView.findViewById(R.id.EditTextCityLongitude);
			
			okButton = (Button) dialogView.findViewById(R.id.buttonOkAlertDialog);
			okButton.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					onOkButtonClick();
				}
			});
			
			cancelButton = (Button) dialogView.findViewById(R.id.buttonCancelAlertDialog);
			cancelButton.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					onCancelButtonClick();
				}
			});
			
			spinnerCityColor = (Spinner) dialogView.findViewById(R.id.SpinnerCityColor);
			
			isViewInitialized = true;
		}
		
		currentCity = getCurrentCity();
		if (currentCity != null) {
			nameEditText.setText(currentCity.getName());
			nameEditText.selectAll();
			latitudeEditText.setText(String.valueOf(currentCity.getLatitude()));
			longitudeEditText.setText(String.valueOf(currentCity.getLongitude()));
			spinnerCityColor.setSelection(Arrays.asList(getResources().getStringArray(R.array.colors)).indexOf(
				currentCity.getColor().toUpperCase()));
		}
		
		// Build custom alert dialog, create it and return as a result
		alertDialog = new AlertDialog.Builder(activity).setTitle(R.string.label_manageCity).setView(dialogView)
			.create();
		
		return alertDialog;
	}
	
	private City getCurrentCity() {
		City currentCity = null;
		Bundle cityBundle = getArguments();
		
		if (cityBundle != null) {
			currentCity = (City) cityBundle.getSerializable(ListViewFragment.STRING_CURRENT_CITY);
		}
		
		return currentCity;
	}
	
	public void onOkButtonClick() {
		if (!isInputDataValid()) {
			Toast.makeText(activity, R.string.alert_data_not_valid, Toast.LENGTH_SHORT).show();
		}
		else {
			if (mListener != null) {
				mListener.onDialogPositiveClick(
					new City(nameEditText.getText().toString(), Double.valueOf(latitudeEditText.getText().toString()),
						Double.valueOf(longitudeEditText.getText().toString()), currentCity.getTemperature(),
						spinnerCityColor.getSelectedItem().toString(), currentCity.getBitmapArray()), currentCity);
			}
			onCancelButtonClick();
		}
	}
	
	public void onCancelButtonClick() {
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.dismiss();
		}
	}
	
	/**
	 * Validates if provided data in EditTexts and Spinner is correct. Name
	 * should be non zero-length string, Latitude and Longitude must be positive
	 * or negative float numbers between <-90,90> and <-180, 180> respectively.
	 * 
	 * @return validating result
	 */
	private boolean isInputDataValid() {
		if (nameEditText.getText().toString().length() == 0) return false;
		
		String latitudeString = latitudeEditText.getText().toString();
		String longitudeString = longitudeEditText.getText().toString();
		
		if (latitudeString.length() == 0 || !latitudeString.matches(REGEX_FOR_SIGNED_DOUBLE_NUMBERS)) return false;
		if (longitudeString.length() == 0 || !longitudeString.matches(REGEX_FOR_SIGNED_DOUBLE_NUMBERS)) return false;
		
		float latitudeFloat = Float.parseFloat(longitudeString);
		float longitudeFloat = Float.parseFloat(longitudeString);
		
		if (latitudeFloat > 90 || latitudeFloat < -90) return false;
		if (longitudeFloat > 180 || longitudeFloat < -180) return false;
		
		return spinnerCityColor.getSelectedItemPosition() != 0;
	}
}