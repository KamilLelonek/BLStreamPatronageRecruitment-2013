package third.task.android.test;

import third.task.android.CompassActivity;
import third.task.android.R;
import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class CompassActivityTest extends ActivityUnitTestCase<CompassActivity> {
	
	public CompassActivityTest() {
		super(CompassActivity.class);
	}
	
	public void testTruth() {
		assertTrue(true);
	}
	
	public void testPreConditions() {
		startActivity(new Intent(getInstrumentation().getTargetContext(), CompassActivity.class), null, null);
		Activity activity = getActivity();
		assertNull(activity.findViewById(R.id.compasRadar));
	}
}