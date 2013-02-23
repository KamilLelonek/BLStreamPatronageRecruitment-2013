package fourth.task.android.cities;

import java.util.ArrayList;

/**
 * This is one-time-use model for initialize items list when application is
 * started for the first time. Then it is not used anymore in application.
 */
public class CityModel {
	/* Creates simple ArrayList containing sample items. */
	public static ArrayList<City> getCities() {
		ArrayList<City> cities = new ArrayList<City>();
		
		cities.add(new City("Wroc³aw", 51.107777, 17.038642, "blue"));
		cities.add(new City("Kraków", 50.062308, 19.938028, "green"));
		cities.add(new City("Warszawa", 52.235274, 21.00841, "red"));
		cities.add(new City("Poznañ", 52.40822, 16.9335365, "magenta"));
		cities.add(new City("Szczecin", 53.42715, 14.53711, "cyan"));
		cities.add(new City("Gdañsk", 54.351869, 18.646316, "yellow"));
		cities.add(new City("£ódŸ", 51.759062, 19.455747, "white"));
		
		return cities;
	}
}