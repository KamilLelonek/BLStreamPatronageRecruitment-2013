package fourth.task.android.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * CustomViewPager is ViewPager with disabled swipe gestures, because second tab
 * presents MapFragment and it already involves swiping itself, so swiping tabs
 * will be strongly inadvisable there and may be annoying for end user.
 */
public class CustomViewPager extends ViewPager {
	public CustomViewPager(Context context, AttributeSet attrs) { super(context, attrs); }
	
	/* Returning false in both cases touch event indicates that ViewPager should
	 * not handle changing current tab. */
	@Override public boolean onTouchEvent(MotionEvent event) { return false; }
	@Override public boolean onInterceptTouchEvent(MotionEvent event) { return false; }
}