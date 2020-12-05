package ua.com.radiokot.feed.util;

import androidx.appcompat.widget.Toolbar;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.ValueAnimator;

import ua.com.radiokot.feed.Spark;

/**
 * Методы для скрываемого скроллом тулбара.
 */
public class FloatingToolbar
{
	private int prevScrollY = 0;
	private int scrollPath = 0;
	private boolean toolbarVisible = true;
	private Toolbar toolbar;

	public FloatingToolbar(Toolbar toolbar)
	{
		this.toolbar = toolbar;
	}

	public void processScroll(int scrollY, boolean firstScroll)
	{
		if (!firstScroll)
		{
			// Определим смещение.
			int delta = scrollY - prevScrollY;

			prevScrollY = scrollY;

			// Выйдем, если нужная анимация для текущего направления скролла уже отработала.
			if ((delta >= 0 && !toolbarVisible) || (delta <= 0 && toolbarVisible))
				return;

			scrollPath += delta;

			if (scrollPath > 300)
			{
				scrollPath = 0;
				moveToolbar(0, -toolbar.getHeight());
				toolbarVisible = !toolbarVisible;
			} else if (scrollPath < -300)
			{
				scrollPath = 0;
				moveToolbar(-toolbar.getHeight(), 0);
				toolbarVisible = !toolbarVisible;
			}
		}
	}

	public void moveToolbar(float marginFrom, float marginTo)
	{
		ValueAnimator animator = ValueAnimator.ofFloat(marginFrom, marginTo).setDuration(
				Spark.resources.getInteger(android.R.integer.config_mediumAnimTime));
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				int translationY = (int) (float) animation.getAnimatedValue();
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toolbar.getLayoutParams();
				lp.topMargin = translationY;
				toolbar.requestLayout();
			}
		});
		animator.start();
	}

}
