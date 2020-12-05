package ua.com.radiokot.feed.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.Spark;
import ua.com.radiokot.feed.model.PhotoAttachment;
import ua.com.radiokot.feed.util.TouchImageView;

/**
 * Адаптер просмотра фотографий.
 */
public class PhotoPagerAdapter extends PagerAdapter
{
	Context context;
	public ArrayList<PhotoAttachment> photos;
	public TouchImageView[] imageViews;
	LayoutInflater inflater;
	ViewGroup container;

	public PhotoPagerAdapter(Context context, ArrayList<PhotoAttachment> photos, TouchImageView[] imageViews)
	{
		this.context = context;
		this.photos = photos;
		this.imageViews = imageViews;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position)
	{
		this.container = container;

		final TouchImageView imageView = imageViews[position];

		if (!photos.get(position).loadedBig)
		{
			imageView.invalidate();
			Spark.imageLoader.loadImage(photos.get(position).getUrl(), new SimpleImageLoadingListener()
			{
				@Override
				public void onLoadingStarted(String s, View view)
				{
					//imageViews[position].setImageBitmap(Bitmap.createBitmap(FeedActivity.DISPLAY_WIDTH,
					//		FeedActivity.DISPLAY_HEIGHT, Bitmap.Config.RGB_565));
					imageViews[position].setImageDrawable(Spark.resources.getDrawable(R.drawable.drawer_shadow));
				}

				@Override
				public void onLoadingComplete(String s, View view, Bitmap bitmap)
				{
					imageViews[position].setImageBitmap(bitmap);
				}

				@Override
				public void onLoadingFailed(String s, View view, FailReason failReason)
				{
					//Spark.shortToast(failReason.getCause().getMessage());
				}
			});
		}

		if (imageView.getParent() == null)
			container.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

		return imageView;
	}

	@Override
	public int getCount()
	{
		return photos.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object)
	{
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{

	}

	@Override
	public int getItemPosition(Object object)
	{
		return POSITION_NONE;
	}
}
