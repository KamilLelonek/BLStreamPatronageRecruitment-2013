package second.task.android.items;

import java.util.ArrayList;

public class ItemModel {
	
	public static ArrayList<Item> getItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		
		items.add(new Item("Wroc�aw", 51.07, 17.02, "blue"));
		items.add(new Item("Krak�w", 50.03, 19.57, "green"));
		items.add(new Item("Warszawa", 52.12, 21.02, "red"));
		items.add(new Item("Pozna�", 52.25, 16.55, "magenta"));
		items.add(new Item("Szczecin", 53.26, 14.34, "cyan"));
		items.add(new Item("Gda�sk", 54.22, 18.38, "yellow"));
		items.add(new Item("��d�", 51.47, 19.28, "white"));
		
		return items;
	}
}