package fourth.task.android.network;

import java.util.List;

import fourth.task.android.items.Item;

public interface IWeatherServer {
	public void downloadData(List<Item> items);
	
	public void cancelDownload();
}