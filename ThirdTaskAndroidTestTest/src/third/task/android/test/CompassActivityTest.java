package third.task.android.test;

import java.util.ArrayList;

import third.task.android.CompassActivity;
import third.task.android.compass.CompassRadar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Parcelable;
import android.test.ActivityInstrumentationTestCase2;

public class CompassActivityTest extends ActivityInstrumentationTestCase2<CompassActivity> {
	
	public CompassActivityTest() {
		super(CompassActivity.class);
	}
	
	@Override protected void setUp() throws Exception {
		super.setUp();
		Intent showMapIntent = new Intent(getInstrumentation().getTargetContext(), CompassActivity.class);
		showMapIntent.putParcelableArrayListExtra("checked_items", new ArrayList<Parcelable>());
		setActivityIntent(showMapIntent);
	}
	
	public void testPreConditions() {
		Activity activity = getActivity();
		assertNotNull(activity);
	}
	
	public void testInitializingView() {
		CompassRadar compassRadar = (CompassRadar) getActivity().findViewById(third.task.android.R.id.compassRadar);
		assertNotNull(compassRadar);
	}
	
	public void testRadarDirection() {
		CompassRadar compassRadar = (CompassRadar) getActivity().findViewById(third.task.android.R.id.compassRadar);
		
		assertEquals(0, compassRadar.getDirection());
		
		Point point = new Point();
		int direction = 20;
		
		compassRadar.setDirection(direction, point);
		assertEquals(direction, compassRadar.getDirection());
	}
	
	public void testShowAndHideRadar() {
		CompassRadar compassRadar = (CompassRadar) getActivity().findViewById(third.task.android.R.id.compassRadar);
		assertFalse(compassRadar.isShown());
	}
}