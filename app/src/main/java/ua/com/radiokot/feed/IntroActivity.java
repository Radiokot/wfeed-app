package ua.com.radiokot.feed;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;


public class IntroActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		if (!Spark.isFirstLaunch)
		{
			startActivity(new Intent(this, FeedActivity.class));
			super.onCreate(savedInstanceState);
			finish();
			return;
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);

		final View logoLayout = findViewById(R.id.logoLayout);
		final View rootView = findViewById(R.id.rootView);

		rootView.post(new Runnable() {
			@Override
			public void run() {
				int height = rootView.getMeasuredHeight();
				ViewGroup.LayoutParams layoutParams = logoLayout.getLayoutParams();
				layoutParams.height = (int) (height * 3f / 12)
						+ getResources().getDimensionPixelSize(R.dimen.double_margin);
				logoLayout.setLayoutParams(layoutParams);
			}
		});

		// Анимировать логотип.
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				animateLogo(540);
			}
		}, 50);

		findViewById(R.id.buttonIntroGo).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivityForResult(new Intent(IntroActivity.this, CategoriesActivity.class), 1);
			}
		});

        try {
            ua.com.radiokot.feed.util.BackupAgent.requestRestore(this);
        } catch (Exception e) {e.printStackTrace();}
	}

	private void animateLogo(final int duration)
	{
		final ImageView imageLayerCircle = (ImageView) findViewById(R.id.imageLayerCircle);
		final ImageView imageLayerShadow = (ImageView) findViewById(R.id.imageLayerShadow);
		final ImageView imageLayerSmoke = (ImageView) findViewById(R.id.imageLayerSmoke);
		final ImageView imageLayerCup = (ImageView) findViewById(R.id.imageLayerCup);

		// Прозрачность кружка фона.
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(duration);
		alphaAnimation.setInterpolator(new AccelerateInterpolator());
		imageLayerCircle.startAnimation(alphaAnimation);
		imageLayerCircle.setVisibility(View.VISIBLE);

		// Появление чашки.
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation.setDuration(duration);
				scaleAnimation.setInterpolator(new OvershootInterpolator(0.9f));
				imageLayerCup.startAnimation(scaleAnimation);
				imageLayerCup.setVisibility(View.VISIBLE);
			}
		}, duration / 2);

		// Появление тени.
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
				alphaAnimation.setDuration(duration);
				alphaAnimation.setInterpolator(new AccelerateInterpolator());

				imageLayerShadow.startAnimation(alphaAnimation);
				imageLayerShadow.setVisibility(View.VISIBLE);
			}
		}, duration / 2 + duration / 4);

		// Появление дыма.
		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
				alphaAnimation.setDuration(duration);
				alphaAnimation.setInterpolator(new DecelerateInterpolator());

				imageLayerSmoke.startAnimation(alphaAnimation);
				imageLayerSmoke.setVisibility(View.VISIBLE);
			}
		}, duration + duration / 2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			Spark.isFirstLaunch = false;
			startActivity(new Intent(IntroActivity.this, FeedActivity.class));
			finish();
		}
	}
}
