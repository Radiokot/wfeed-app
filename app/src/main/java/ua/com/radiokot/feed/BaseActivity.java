package ua.com.radiokot.feed;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public abstract class BaseActivity extends ActionBarActivity
{
	protected Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getLayoutResource());

		//подтягивание тулбара
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null)
		{
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		}
	}

	protected abstract int getLayoutResource();
}
