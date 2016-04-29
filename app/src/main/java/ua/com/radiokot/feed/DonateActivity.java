package ua.com.radiokot.feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.anjlab.android.iab.v3.BillingProcessor;


public class DonateActivity extends Activity
{
	public static final String SUPPORT_ITEM_ID = "feed_support";
	private static BillingProcessor billingProcessor;
	private static FeedActivity parentActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_donate);

		findViewById(R.id.buttonDonate).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				billingProcessor.purchase(parentActivity, SUPPORT_ITEM_ID);
				finish();
			}
		});
	}

	public static void launch(FeedActivity parent, BillingProcessor bp)
	{
		billingProcessor = bp;
		parentActivity = parent;
		parentActivity.startActivity(new Intent(parentActivity, DonateActivity.class));
	}

	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!billingProcessor.handleActivityResult(requestCode, resultCode, data))
			super.onActivityResult(requestCode, resultCode, data);
	}*/
}
