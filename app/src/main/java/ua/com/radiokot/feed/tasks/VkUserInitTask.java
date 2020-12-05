package ua.com.radiokot.feed.tasks;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.Social;
import ua.com.radiokot.feed.Spark;

/**
 * Задача инициализации пользователя ВК.
 */
public class VkUserInitTask extends AsyncTask<Void, Void, JSONObject>
{
	CircleImageView imagePhoto;
	TextView textName;
	String token;

	public VkUserInitTask(CircleImageView imagePhoto, TextView textName, String token)
	{
		super();
		this.imagePhoto = imagePhoto;
		this.textName = textName;
		this.token = token;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		textName.setText(Social.userName);
	}

	@Override
	protected JSONObject doInBackground(Void... params)
	{
		String url = String.format("https://api.vk.com/method/execute.initUser?v=5.126&lang=%s&access_token=%s",
				Spark.resources.getString(R.string.locale), token);

		try
		{
			JSONObject json = Spark.getJSON(url);
			return json;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	@Override
	protected void onPostExecute(JSONObject result)
	{
		super.onPostExecute(result);

		if (result != null)
		{
			if (!result.isNull("response"))
			{
				result = result.optJSONObject("response");
				Spark.imageLoader.displayImage(result.optString("photo_200"), imagePhoto, Spark.nofadeDisplayOption);
				Social.userName = result.optString("first_name") + " " + result.optString("last_name");
			}
			// Если отказано в доступе.
			else if (!result.isNull("error"))
			{
				int errorCode = result.optJSONObject("error").optInt("error_code");
				if (errorCode == 5 || errorCode == 10)
				{
					Spark.shortToast(Spark.resources.getString(R.string.toast_vk_auth_fail));
					Social.logoutVk();
				}

				return;
			}
		}
		textName.setText(Social.userName);
	}
}
