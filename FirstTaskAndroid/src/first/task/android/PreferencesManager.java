package first.task.android;

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
import android.widget.Toast;

/**
 * This class allows to manage application shared preferences and also managing
 * file system by list serialization
 */
public class PreferencesManager {
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor sharedPreferencesEditor;
	private Context context;
	
	public PreferencesManager(Context context) {
		this.context = context;
		sharedPreferences = context.getSharedPreferences("cities", Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
	}
	
	public boolean isFirstRun() {
		return sharedPreferences.getBoolean("isFirstRun", true);
	}
	
	public void setFirstRunFalse() {
		sharedPreferencesEditor.putBoolean("isFirstRun", false);
		sharedPreferencesEditor.commit();
	}
	
	/**
	 * Creates specific application folder and file to sore data
	 * 
	 * @return file to keep application data
	 */
	private File getSaveFileDirectory() {
		File savedLocationsDirectory = new File(Environment.getExternalStorageDirectory().getPath().concat("/FirstProject"));
		savedLocationsDirectory.mkdirs();
		return new File(savedLocationsDirectory, "savedList.ext");
	}
	
	/**
	 * Keeps path of file where data will be stored. When path doesn't exits it
	 * means that neither the file. Then file must be created and it's path is
	 * stored in preferences.
	 * 
	 * @return file path to store application data
	 */
	private String getFilePath() {
		String path = sharedPreferences.getString("path", "");
		if (path.length() == 0) {
			path = getSaveFileDirectory().getAbsolutePath(); 	// should be /storage/sdcard0/FirstProject/savedList.ext
			sharedPreferencesEditor.putString("path", path);
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