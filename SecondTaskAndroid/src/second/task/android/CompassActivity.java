package second.task.android;

import java.util.ArrayList;

import second.task.android.items.Item;
import android.app.Activity;
import android.os.Bundle;

public class CompassActivity extends Activity {
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compass);
		
		ArrayList<Item> checkedItems = getIntent().getExtras().getParcelableArrayList(SecondTaskAndroid.STRING_CHECKED_ITEMS);
	}
}