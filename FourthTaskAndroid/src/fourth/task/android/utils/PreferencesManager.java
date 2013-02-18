package fourth.task.android.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import fourth.task.android.R;
import fourth.task.android.items.Item;

/**
 * This class allows to manage application shared preferences and also managing
 * file system by list serialization.
 */
public class PreferencesManager {
	private final String STRING_CITIES = "cities";
	private final String STRING_FIRST_RUN = "isFirstRun";
	private final String STRING_FILE_DIRECTORY = "/BLStream";
	private final String STRING_FILE_NAME = "savedList.ext";
	private final String STRING_PATH = "path";
	private final String STRING_LOG_TAG = "PreferencesManager";
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor sharedPreferencesEditor;
	private Context context;
	
	public PreferencesManager(Context context) {
		this.context = context;
		sharedPreferences = context.getSharedPreferences(STRING_CITIES, Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
	}
	
	/**
	 * Checks if it is the first time when application is started
	 * 
	 * @return application first run state
	 */
	public boolean isFirstRun() {
		return sharedPreferences.getBoolean(STRING_FIRST_RUN, true);
	}
	
	/**
	 * After first application run "isFirstRun" flag is set to false
	 */
	public void setFirstRunFalse() {
		sharedPreferencesEditor.putBoolean(STRING_FIRST_RUN, false);
		sharedPreferencesEditor.commit();
	}
	
	/**
	 * Creates specific application folder and file to sore data
	 * 
	 * @return file to keep application data
	 */
	private File getSaveFileDirectory() {
		File savedLocationsDirectory = new File(Environment.getExternalStorageDirectory().getPath()
			.concat(STRING_FILE_DIRECTORY));
		savedLocationsDirectory.mkdirs();
		return new File(savedLocationsDirectory, STRING_FILE_NAME);
	}
	
	/**
	 * Keeps path of file where data will be stored. When path doesn't exits it
	 * means that neither the file. Then file must be created and it's path is
	 * stored in preferences.
	 * 
	 * @return file path to store application data
	 */
	private String getFilePath() {
		String path = sharedPreferences.getString(STRING_PATH, "");
		if (path.length() == 0) {
			path = getSaveFileDirectory().getAbsolutePath(); // should be /storage/sdcard0/BLStream/savedList.ext
			sharedPreferencesEditor.putString(STRING_PATH, path);
			sharedPreferencesEditor.commit();
		}
		return path;
	}
	
	/**
	 * Serializes list of items
	 * 
	 * @param list list of items to serialize
	 */
	public void serializeQuotes(List<Item> list) {
		Log.d(STRING_LOG_TAG, "Saving items to file.");
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFilePath()));
			oos.writeObject(list);
			oos.close();
		}
		catch (IOException e) {
			Toast.makeText(context, R.string.alert_saving_error, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Deserializes list of items
	 * 
	 * @return deserialized list of items
	 */
	@SuppressWarnings("unchecked") public List<Item> deserializeQuotes() {
		Log.d(STRING_LOG_TAG, "Reading items from file.");
		
		List<Item> list = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getFilePath()));
			list = (List<Item>) ois.readObject();
			ois.close();
		}
		catch (Exception e) {
			Toast.makeText(context, R.string.alert_reading_error, Toast.LENGTH_SHORT).show();
		}
		return list == null ? new ArrayList<Item>() : list;
	}
}