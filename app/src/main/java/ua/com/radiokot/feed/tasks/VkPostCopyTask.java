package ua.com.radiokot.feed.tasks;

import android.os.AsyncTask;

import org.json.JSONObject;

import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.Spark;
import ua.com.radiokot.feed.model.Post;

/**
 * Задача репоста.
 */
public class VkPostCopyTask extends AsyncTask<Void, Void, Boolean>
{
	Post post;
	String token;

	public VkPostCopyTask(Post post, String token)
	{
		super();
		this.token = token;
		this.post = post;
	}

	@Override
	protected Boolean doInBackground(Void... params)
	{
		String url = String.format("https://api.vk.com/method/wall.repost?object=wall-%s" +
						"&message=%s&v=5.2&access_token=%s",
				post.author.apiId + "_" + post.apiId, "", token);

		try
		{
			JSONObject json = Spark.getJSON(url);
			return json.getJSONObject("response").getInt("success") == 1;
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
			Spark.shortToast(Spark.resources.getString(R.string.toast_post_copied));
		else
			Spark.shortToast(Spark.resources.getString(R.string.toast_post_copy_error));
	}
}
