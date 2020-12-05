package ua.com.radiokot.feed;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity
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
