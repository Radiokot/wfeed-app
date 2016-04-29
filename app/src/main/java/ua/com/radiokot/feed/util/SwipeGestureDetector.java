package ua.com.radiokot.feed.util;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;

import ua.com.radiokot.feed.FeedActivity;

/**
 * Created by Radiokot on 09.05.2015.
 */
public class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener
{
	Activity activity;

	public SwipeGestureDetector(Activity parentActivity)
	{
		activity = parentActivity;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2,
						   float velocityX, float velocityY)
	{

		if (Math.abs(e1.getY() - e2.getY()) < FeedActivity.DISPLAY_HEIGHT / 4)
			return false;

		if (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY()) == 1)
		{
			//activity.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			//PhotoActivity.hideSystemUI();
			activity.onBackPressed();
			//activity.finish();
			//activity.overridePendingTransition(0, R.anim.slide_out_top);
			return true;
		}
		return false;
	}

	private int getSlope(float x1, float y1, float x2, float y2)
	{
		Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
		if (angle > 45 && angle <= 135)
			// top
			return 1;
		if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
			// left
			return 2;
		if (angle < -45 && angle >= -135)
			// down
			return 3;
		if (angle > -45 && angle <= 45)
			// right
			return 4;
		return 0;
	}
}