package ua.com.radiokot.feed.tasks;

import android.os.AsyncTask;

import org.json.JSONObject;

import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.Spark;
import ua.com.radiokot.feed.model.PhotoAttachment;

/**
 * Задача сохранения фотографии в "Сохраненные".
 */
public class VkPhotoSaveTask extends AsyncTask<Void, Void, Boolean>
{
	PhotoAttachment photo;
	String token;

	public VkPhotoSaveTask(PhotoAttachment photo, String token)
	{
		super();
		this.token = token;
		this.photo = photo;
	}

	@Override
	protected Boolean doInBackground(Void... params)
	{
		String[] splitted = photo.VkAttId.split("_");

		String url = String.format("https://api.vk.com/method/photos.copy?owner_id=%s&photo_id=%s&access_token=%s",
				splitted[0], splitted[1], token);

		try
		{
			JSONObject json = Spark.getJSON(url);
			return !json.isNull("response");
		}
		catch (Exception e)
		{
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result)
	{
		super.onPostExecute(result);

		if (result)
			Spark.shortToast(Spark.resources.getString(R.string.toast_photo_saved_vk));
		else
			Spark.shortToast(Spark.resources.getString(R.string.toast_photo_save_error));
	}
}
