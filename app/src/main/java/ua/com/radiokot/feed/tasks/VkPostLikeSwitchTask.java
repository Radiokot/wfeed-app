package ua.com.radiokot.feed.tasks;

import android.os.AsyncTask;
import androidx.core.content.ContextCompat;

import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONObject;

import ua.com.radiokot.feed.Feed;
import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.Spark;
import ua.com.radiokot.feed.model.Post;

/**
 * Задача лайка поста.
 */
public class VkPostLikeSwitchTask extends AsyncTask<Void, Void, Boolean>
{
	ImageView button;
	Post post;
	String token;

	public VkPostLikeSwitchTask(Post post, ImageButton button, String token)
	{
		super();
		this.button = button;
		this.post = post;
		this.token = token;
	}

	@Override
	protected Boolean doInBackground(Void... params)
	{
		String url = String.format("https://api.vk.com/method/execute.switchLike?" +
						"prev_state=%s&owner_id=-%s&item_id=%s&v=5.126&access_token=%s",
				(post.isLiked ? 1 : 0), post.author.apiId, post.apiId, token);

		try
		{
			JSONObject json = Spark.getJSON(url);
			return json.getInt("response") == 1;
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

		post.isLiked = result;

		if (post.isLiked)
		{
            if (button != null)
			    button.setImageDrawable(ContextCompat.getDrawable(Spark.context, R.drawable.ic_like_done));

			if (!Feed.likedPostsIds.contains(post.id))
				Feed.likedPostsIds.add(post.id);
		}
		else
		{
            if (button != null)
			    button.setImageDrawable(ContextCompat.getDrawable(Spark.context, R.drawable.ic_like));

			if (Feed.likedPostsIds.contains(post.id))
				Feed.likedPostsIds.remove(post.id);
		}
	}
}
