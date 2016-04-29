package ua.com.radiokot.feed.util;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * Created by Radiokot on 23.02.2015.
 */
public class FadeInBitmapDisplayer implements BitmapDisplayer
{

	private final int durationMillis;

	private final boolean animateFromNetwork;
	private final boolean animateFromDisc;
	private final boolean animateFromMemory;

	/**
	 * @param durationMillis Duration of "fade-in" animation (in milliseconds)
	 */
	public FadeInBitmapDisplayer(int durationMillis)
	{
		this(durationMillis, true, true, true);
	}

	/**
	 * @param durationMillis     Duration of "fade-in" animation (in milliseconds)
	 * @param animateFromNetwork Whether animation should be played if image is loaded from network
	 * @param animateFromDisc    Whether animation should be played if image is loaded from disc cache
	 * @param animateFromMemory  Whether animation should be played if image is loaded from memory cache
	 */
	public FadeInBitmapDisplayer(int durationMillis, boolean animateFromNetwork, boolean animateFromDisc,
								 boolean animateFromMemory)
	{
		this.durationMillis = durationMillis;
		this.animateFromNetwork = animateFromNetwork;
		this.animateFromDisc = animateFromDisc;
		this.animateFromMemory = animateFromMemory;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom)
	{
		imageAware.setImageBitmap(bitmap);

		if ((animateFromNetwork && loadedFrom == LoadedFrom.NETWORK) ||
				(animateFromDisc && loadedFrom == LoadedFrom.DISC_CACHE) ||
				(animateFromMemory && loadedFrom == LoadedFrom.MEMORY_CACHE))
		{
			animate(imageAware.getWrappedView(), durationMillis);
		}
	}

	/**
	 * Animates {@link ImageView} with "fade-in" effect
	 *
	 * @param imageView      {@link ImageView} which display image in
	 * @param durationMillis The length of the animation in milliseconds
	 */
	public static void animate(View imageView, int durationMillis)
	{
		if (imageView != null)
		{
			AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
			fadeImage.setDuration(durationMillis);
			fadeImage.setInterpolator(new AccelerateInterpolator(0.8f));
			imageView.startAnimation(fadeImage);
		}
	}
}
