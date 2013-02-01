package third.task.android.items;

import java.util.ArrayList;

/**
 * This is one-time-use model for initialize items list when application is
 * started for the first time. Then it is not used anymore in application.
 */
public class ItemModel {
	/* Creates simple ArrayList containing sample items. */
	public static ArrayList<Item> getItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		
		items.add(new Item("Wroc³aw", 51.107777, 17.038642, "blue"));
		items.add(new Item("Kraków", 50.062308, 19.938028, "green"));
		items.add(new Item("Warszawa", 52.235274, 21.00841, "red"));
		items.add(new Item("Poznañ", 52.40822, 16.9335365, "magenta"));
		items.add(new Item("Szczecin", 53.42715, 14.53711, "cyan"));
		items.add(new Item("Gdañsk", 54.351869, 18.646316, "yellow"));
		items.add(new Item("£ódŸ", 51.759062, 19.455747, "white"));
		
		return items;
	}
}