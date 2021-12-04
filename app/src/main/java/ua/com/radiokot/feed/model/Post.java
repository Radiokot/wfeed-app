package ua.com.radiokot.feed.model;

import android.annotation.SuppressLint;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import ua.com.radiokot.feed.Feed;
import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.Spark;

/**
 * Пост в ленте.
 */
public class Post
{
	public String id, apiId, text, url;
	public transient String info;
	public int authorId, date;
	public Author author;
	public ArrayList<PhotoAttachment> photos;
	public transient boolean isExpanded = false, isLiked = false, isFavorite = false; //для лайка, звезды и т.п.
	public Type displayType = Type.POST;

	public enum Type
	{
		POST, LOADER, INFO
	}

	public Post(JSONObject json)
	{
		this.id = json.optString("id");
		this.apiId = json.optString("apiId");
		//текст с заменой встроенных ссылок ВК и удалением пустых строк
		this.text = json.optString("text")
				.replaceAll("\\[(id|club)\\d+\\|([^\\]]+)\\]", "$2")
				.replaceAll("(?m)^[ \t]*\r?\n", "");

		this.url = json.optString("url");
		this.authorId = json.optInt("authorId");
		this.date = json.optInt("date");

		this.author = Feed.authors.get(this.authorId);

		// Лайкали уже?
		this.isLiked = Feed.likedPostsIds.contains(this.id);
		// А в избранное добавлен?
		this.isFavorite = Feed.favoritePostsIds.contains(this.id);

		//разделим вложения на фото и видео
		photos = new ArrayList<>();
		ArrayList<Attachment> feedAttachments = Feed.atts.get(this.id);
		if (feedAttachments != null) {
			for (Attachment a : feedAttachments) {
				if (a instanceof PhotoAttachment)
					photos.add((PhotoAttachment) a);
			}
			Collections.sort(photos);
		}

		this.info = makeInfoString(this.date, this.author.siteName);
	}

	@SuppressLint("SimpleDateFormat")
    public String makeInfoString(int date, String siteName)
	{
		String dateString;
		int delta = (int) ((System.currentTimeMillis() / 1000) - date) / 60;

		if (delta == 0)
			dateString = Spark.resources.getString(R.string.post_date_now);
		else if (delta == 1)
			dateString = Spark.resources.getString(R.string.post_date_minute);
		else if (delta < 60)
			dateString = String.valueOf(delta) + Spark.getNumEnding(delta,
					Spark.resources.getString(R.string.post_date_minutes).split("/"))
					+ Spark.resources.getString(R.string.post_date_ago);
		else if (delta >= 60 && delta < 120)
			dateString = Spark.resources.getString(R.string.post_date_hour);
		else if (delta < 1440)
			dateString = String.valueOf(delta / 60) + Spark.getNumEnding(delta / 60,
					Spark.resources.getString(R.string.post_date_hours).split("/"))
					+ Spark.resources.getString(R.string.post_date_ago);
		else
			dateString = new SimpleDateFormat("d MMMM, HH:mm").format(new Date(date * 1000L));

		return dateString + Spark.resources.getString(R.string.post_info_through) + siteName;
	}

	// Пустой конструктор для наследования.
	public Post()
	{

	}
}
