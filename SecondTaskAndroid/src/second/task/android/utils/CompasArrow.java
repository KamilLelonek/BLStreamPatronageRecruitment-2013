package second.task.android.utils;

import second.task.android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * This class uses to represent radar graphic on map that baring to current
 * phone direction.
 */
public class CompasArrow extends ImageView {
	private Paint paint;
	private Bitmap radarBitmap;
	private Point point;	// current location on map
	
	private int direction;	// bearing direction
	private int drawingX;
	private int drawingY;
	
	public CompasArrow(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// reading bitmap from drawable resource
		radarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.radar);
		paint = new Paint();
	}
	
	public void setDirection(int direction, Point point) {
		if (point != null) {
			// setting up drawing points
			this.point = point;
			this.drawingX = point.x - radarBitmap.getWidth() / 2;
			this.drawingY = point.y - radarBitmap.getHeight();
			
			this.direction = direction;
			this.invalidate();
		}
	}
	
	/**
	 * This method is invoked by other one called "invalidate()" and causes that
	 * whole view is being redrawn.
	 * 
	 * @param canvas the canvas on which the background will be drawn
	 */
	@Override public void onDraw(Canvas canvas) {
		if (point != null) {
			canvas.rotate(direction, point.x, point.y); // rotating image (by current location) to pointing specific direction
			canvas.drawBitmap(radarBitmap, drawingX, drawingY, paint); // drawing image starting from the middle point at the bottom of picture
		}
		
		super.onDraw(canvas);
	}
}