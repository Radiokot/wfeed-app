package ua.com.radiokot.feed.model;

import android.graphics.drawable.Drawable;

import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.Spark;

/**
 * Сервисные элементы для ленты, наследуемые от поста.
 */
public class ServicePostItem
{
	// Кружок загрузки.
	public static class LoaderItem extends Post
	{
		public LoaderItem()
		{
			super();
			displayType = Type.LOADER;
		}
	}

	// Карточка со служебной информацией.
	public static class InfoItem extends Post
	{
		public String caption, message;
		public Drawable image;

		public InfoItem()
		{
			displayType = Type.INFO;
		}

		InfoItem(String caption, String message, Drawable image)
		{
			super();

			this.caption = caption;
			this.message = message;
			this.image = image;
		}

		public InfoItem displayNoData()
		{
			caption = Spark.resources.getString(R.string.info_no_data_caption);
			message = Spark.resources.getString(R.string.info_no_data_message);
			image = Spark.resources.getDrawable(R.drawable.sketch_cloud);

			return this;
		}

		public InfoItem displayNoConnection()
		{
			caption = Spark.resources.getString(R.string.info_no_connection_caption);
			message = Spark.resources.getString(R.string.info_no_connection_message);
			image = Spark.resources.getDrawable(R.drawable.sketch_pigeon);

			return this;
		}
	}
}
