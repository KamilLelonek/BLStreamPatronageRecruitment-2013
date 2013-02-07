package third.task.android.test;

import third.task.android.CompassActivity;
import third.task.android.compass.CompassRadar;
import android.content.Intent;
import android.graphics.Point;
import android.test.ActivityUnitTestCase;

public class CompassActivityTest extends ActivityUnitTestCase<CompassActivity> {
	private CompassActivity activity;
	private CompassRadar compassRadar;
	
	public CompassActivityTest() {
		super(CompassActivity.class);
	}
	
	@Override protected void setUp() throws Exception {
		startActivity(new Intent(getInstrumentation().getTargetContext(), CompassActivity.class), null, null);
		activity = getActivity();
		compassRadar = (CompassRadar) activity.findViewById(third.task.android.R.id.compassRadar);
	}
	
	public void testInitializingView() {
		assertNotNull(compassRadar);
	}
	
	public void testRadarDirection() {
		assertEquals(0, compassRadar.getDirection());
		
		Point point = new Point();
		int direction = 20;
		
		compassRadar.setDirection(direction, point);
		assertEquals(direction, compassRadar.getDirection());
	}
	
	public void testShowAndHideRadar() {
		assertTrue(compassRadar.isShown());
		activity.manageShowingRadarClick(null);
		assertFalse(compassRadar.isShown());
	}
}