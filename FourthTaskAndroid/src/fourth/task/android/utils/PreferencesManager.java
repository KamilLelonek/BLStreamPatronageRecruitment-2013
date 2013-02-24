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
import android.preference.PreferenceManager;
import android.util.Log;
import fourth.task.android.FourthTaskAndroid;
import fourth.task.android.PreferencesFragment;
import fourth.task.android.R;
import fourth.task.android.cities.City;
import fourth.task.android.cities.CityModel;
import fourth.task.android.weather.servers.IWeatherServer;
import fourth.task.android.weather.servers.OpenWeatherMapServer;
import fourth.task.android.weather.servers.WorldWeatherOnlineServer;

/**
 * This class allows to manage application shared preferences and also managing
 * file system by list serialization.
 */
public class PreferencesManager {
	private final String STRING_FIRST_RUN = "isFirstRun";
	private final String STRING_FILE_NAME_LIST = "savedList.bls";
	private final String STRING_FILE_NAME_CITY = "savedCity%d.bls";
	private final String STRING_LOG_TAG = "PreferencesManager";
	
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor sharedPreferencesEditor;
	private Context context;
	
	public PreferencesManager(Context context) {
		this.context = context;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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
	 * @return current weather server where to download data from
	 */
	public IWeatherServer getCurrentWeatherServer() {
		String[] availableServers = context.getResources().getStringArray(R.array.preferences_weather_servers_list);
		String serverName = sharedPreferences.getString(PreferencesFragment.PREFERENCE_SERVERS, availableServers[0]);
		
		if (serverName.equals(availableServers[1])) return new WorldWeatherOnlineServer(context);
		return new OpenWeatherMapServer(context);
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
	
	private File getFileForCity(int fileID) {
		return new File(getApplicationFolder(), String.format(STRING_FILE_NAME_CITY, fileID));
	}
	
	private String getFilePath(File file) {
		return file.getAbsolutePath();
	}
	
	private String getListFilePath() {
		return getFilePath(getFileForList());
	}
	
	private String getCityFilePath(int fileID) {
		return getFilePath(getFileForCity(fileID));
	}
	
	/**
	 * Serializes list of cities
	 * 
	 * @param list list of cities to serialize
	 */
	public void saveListToFile(List<City> list) {
		serializator(list, getListFilePath());
	}
	
	/**
	 * Serializes simple cities
	 * 
	 * @param city city to serialize
	 */
	public void saveCityToFile(City city, int fileID) {
		serializator(city, getCityFilePath(fileID));
	}
	
	public boolean deleteCityFile(int fileID) {
		return getFileForCity(fileID).delete();
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
	 * @return list of cities
	 */
	public List<City> readListFromFile() {
		if (!isFirstRun()) {
			Log.d(FourthTaskAndroid.STRING_LOG_TAG, "Resuming application state.");
			return deserializeList();
		}
		
		Log.d(FourthTaskAndroid.STRING_LOG_TAG, "First application run!");
		setFirstRunFalse();
		return CityModel.getCities();
	}
	
	/**
	 * Deserializes list of cities
	 * 
	 * @return deserialized list of cities
	 */
	@SuppressWarnings("unchecked") private List<City> deserializeList() {
		Log.d(STRING_LOG_TAG, "Reading cities' list from file.");
		List<City> list = (List<City>) deserializator(getListFilePath());
		return list == null ? new ArrayList<City>() : list;
	}
	
	/**
	 * Deserializes city
	 * 
	 * @return deserialized city
	 */
	public City readCityFromFile(int fileID) {
		Log.d(STRING_LOG_TAG, "Reading city from file.");
		return (City) deserializator(getCityFilePath(fileID));
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