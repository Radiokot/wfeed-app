package ua.com.radiokot.feed;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import ua.com.radiokot.feed.adapters.ShareListAdapter;
import ua.com.radiokot.feed.model.NavigationListItem;
import ua.com.radiokot.feed.model.Post;


public class ShareActivity extends AppCompatActivity
{
	private static Post sharingPost;

	private enum ShareAction
	{
		VK_WALL, TEXT, URL, BROWSER
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		ArrayList<NavigationListItem> variants = new ArrayList<>();
		ListView listShareVariants = (ListView) findViewById(R.id.listShareVariants);
		ShareListAdapter adapter = new ShareListAdapter(this, variants);
		listShareVariants.setAdapter(adapter);

		if (sharingPost.author.siteId == 1)
			variants.add(new NavigationListItem(Spark.resources.getDrawable(R.drawable.ic_vk),
					Spark.resources.getString(R.string.variant_vk), true,
					getShareActionIntent(ShareAction.VK_WALL)));

		variants.add(new NavigationListItem(Spark.resources.getDrawable(R.drawable.ic_link_variant),
				Spark.resources.getString(R.string.variant_url), true,
				getShareActionIntent(ShareAction.URL)));
		variants.add(new NavigationListItem(Spark.resources.getDrawable(R.drawable.ic_message_text_outline),
				Spark.resources.getString(R.string.variant_text), true,
				getShareActionIntent(ShareAction.TEXT)));
		variants.add(new NavigationListItem(Spark.resources.getDrawable(R.drawable.ic_earth),
				Spark.resources.getString(R.string.variant_browser), false,
				getShareActionIntent(ShareAction.BROWSER)));

		adapter.notifyDataSetChanged();
	}

	// Запуск с передачей поста.
	public static void launch(Activity parentActivity, Post post)
	{
		sharingPost = post;
		parentActivity.startActivity(new Intent(parentActivity, ShareActivity.class));
	}

	public Intent getShareActionIntent(ShareAction action)
	{
		return new Intent(this, ShareActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
				.putExtra("shareAction", action);
	}

	public void shareText(String caption, String subject, String text)
	{
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");

		shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		shareIntent.putExtra(Intent.EXTRA_TEXT, text);

		startActivity(Intent.createChooser(shareIntent, caption));
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		ShareAction shareAction = (ShareAction) intent.getSerializableExtra("shareAction");
		switch (shareAction)
		{
			case VK_WALL:
				Social.copyPostVk(sharingPost, this);
				finish();
				break;
			case URL:
				shareText(Spark.resources.getString(R.string.variant_url),
						Spark.resources.getString(R.string.app_name),
						sharingPost.url);
				finish();
				break;
			case TEXT:
				shareText(Spark.resources.getString(R.string.variant_text),
						Spark.resources.getString(R.string.app_name),
						sharingPost.text);
				finish();
				break;
			case BROWSER:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sharingPost.url)));
				finish();
				break;
		}
	}
}
