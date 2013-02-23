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
import android.util.Log;
import fourth.task.android.FourthTaskAndroid;
import fourth.task.android.items.Item;
import fourth.task.android.items.ItemModel;

/**
 * This class allows to manage application shared preferences and also managing
 * file system by list serialization.
 */
public class PreferencesManager {
	private final String STRING_CITIES = "cities";
	private final String STRING_FIRST_RUN = "isFirstRun";
	private final String STRING_FILE_NAME_LIST = "savedList.bls";
	private final String STRING_FILE_NAME_ITEM = "savedItem%d.bls";
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
	private File getApplicationFolder() {
		File savedLocationsDirectory = new File(context.getFilesDir().getPath());
		savedLocationsDirectory.mkdirs();
		return savedLocationsDirectory;
	}
	
	private File getFileForList() {
		return new File(getApplicationFolder(), STRING_FILE_NAME_LIST);
	}
	
	private File getFileForItem(int fileID) {
		return new File(getApplicationFolder(), String.format(STRING_FILE_NAME_ITEM, fileID));
	}
	
	private String getFilePath(File file) {
		return file.getAbsolutePath();
	}
	
	private String getListFilePath() {
		return getFilePath(getFileForList());
	}
	
	private String getItemFilePath(int fileID) {
		return getFilePath(getFileForItem(fileID));
	}
	
	/**
	 * Serializes list of items
	 * 
	 * @param list list of items to serialize
	 */
	public void saveListToFile(List<Item> list) {
		serializator(list, getListFilePath());
	}
	
	/**
	 * Serializes simple items
	 * 
	 * @param item item to serialize
	 */
	public void saveItemToFile(Item item, int fileID) {
		serializator(item, getItemFilePath(fileID));
	}
	
	public boolean deleteItemFile(int fileID) {
		return getFileForItem(fileID).delete();
	}
	
	/**
	 * General serializator object to file with specified path
	 * 
	 * @param object to serialize
	 * @param filePath where object will be serialized
	 */
	private void serializator(Object object, String filePath) {
		Log.d(STRING_LOG_TAG, "Serialization to: " + filePath);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath));
			oos.writeObject(object);
			oos.close();
		}
		catch (IOException e) {
			Log.w(STRING_LOG_TAG, "Can\'t write to internal storage.");
		}
	}
	
	/**
	 * Reads list from file. If it is first application run then list are
	 * obtained from data model, in the other case list is deserialized.
	 * 
	 * @return list of items
	 */
	public List<Item> readListFromFile() {
		if (!isFirstRun()) {
			Log.d(FourthTaskAndroid.STRING_LOG_TAG, "Resuming application state.");
			return deserializeList();
		}
		
		Log.d(FourthTaskAndroid.STRING_LOG_TAG, "First application run!");
		setFirstRunFalse();
		return ItemModel.getItems();
	}
	
	/**
	 * Deserializes list of items
	 * 
	 * @return deserialized list of items
	 */
	@SuppressWarnings("unchecked") private List<Item> deserializeList() {
		Log.d(STRING_LOG_TAG, "Reading items' list from file.");
		List<Item> list = (List<Item>) deserializator(getListFilePath());
		return list == null ? new ArrayList<Item>() : list;
	}
	
	/**
	 * Deserializes item
	 * 
	 * @return deserialized item
	 */
	public Item readItemFromFile(int fileID) {
		Log.d(STRING_LOG_TAG, "Reading item from file.");
		return (Item) deserializator(getItemFilePath(fileID));
	}
	
	/**
	 * General deserializator object from file with specified path
	 * 
	 * @param filePath where object has been serialized
	 */
	private Object deserializator(String filePath) {
		Log.d(STRING_LOG_TAG, "Deserialization from: " + filePath);
		
		Object object = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath));
			object = ois.readObject();
			ois.close();
		}
		catch (Exception e) {
			Log.w(STRING_LOG_TAG, "Can\'t read from internal storage.");
		}
		return object;
	}
}