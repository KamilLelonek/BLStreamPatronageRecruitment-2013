package third.task.android.compass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * This class uses to represent radar graphic on map that baring to current
 * phone direction.
 */
public class CompassRadar extends ImageView implements CompassInterface {
	private Paint paint;
	private Bitmap radarBitmap;
	private Point point; // current location on map
	
	private int direction; // bearing direction
	private int drawingX;
	private int drawingY;
	
	public CompassRadar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// reading bitmap from drawable resource
		radarBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		paint = new Paint();
	}
	
	@Override public void setDirection(int direction, Point point) {
		// setting up drawing points
		this.point = point;
		this.drawingX = point.x - radarBitmap.getWidth() / 2;
		this.drawingY = point.y - radarBitmap.getHeight();
		
		this.direction = direction;
		this.invalidateDrawable(getDrawable());
	}
	
	/**
	 * This method is invoked by other one called "invalidate()" and causes that
	 * whole view is being redrawn.
	 * 
	 * @param canvas
	 *            canvas on which the background will be drawn
	 */
	@Override public void onDraw(Canvas canvas) {
		if (point != null) {
			// rotating image (by current location) to pointing specific direction
			canvas.rotate(direction, point.x, point.y);
			// drawing image starting from the middle point at the bottom of picture
			canvas.drawBitmap(radarBitmap, drawingX, drawingY, paint);
		}
		
		super.onDraw(canvas);
	}
}