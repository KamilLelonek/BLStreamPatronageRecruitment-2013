package fourth.task.android.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import fourth.task.android.R;

/**
 * Alert dialog displayed when Internet connection is down.
 */
public class DialogFragmentInternetConnection extends DialogFragment {
	
	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity()).setPositiveButton(R.string.menu_settings, new OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(Settings.ACTION_SETTINGS));
				dialog.dismiss();
			}
		}).setNegativeButton(android.R.string.cancel, new OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setTitle(R.string.alert_internet_connection_title).setMessage(R.string.alert_internet_connection_body)
			.setIcon(0).create();
	}
}