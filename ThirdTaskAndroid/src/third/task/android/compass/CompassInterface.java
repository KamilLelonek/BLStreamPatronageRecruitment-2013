package third.task.android.compass;

import android.graphics.Point;

public interface CompassInterface {
	// sets current anchor point and bearing
	public void setDirection(int direction, Point point);
	
	// checks if compass is displayed
	public boolean isShown();
	
	// shows or hides showing direction
	public void setVisibility(int visible);
}