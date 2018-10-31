package ua.com.radiokot.feed;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;

import ua.com.radiokot.feed.adapters.FeedListAdapter;
import ua.com.radiokot.feed.model.Post;
import ua.com.radiokot.feed.util.FloatingToolbar;


public class FavoritesActivity extends BaseActivity implements ObservableScrollViewCallbacks
{
	// Адаптер избранного.
	public FeedListAdapter favoritesListAdapter;
	// "Плавающий" тулбар
	private FloatingToolbar floatingToolbar;

	@Override
	protected int getLayoutResource()
	{
		return R.layout.activity_favorites;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Сделаем обычный тулбар плавающим.
		floatingToolbar = new FloatingToolbar(toolbar);

		// Настроим список.
		ObservableListView listFavorites = (ObservableListView) findViewById(R.id.listFavorites);

		ArrayList<Post> favoritePosts = Spark.database.getFavoritePosts();
		favoritesListAdapter = new FeedListAdapter(FavoritesActivity.this, favoritePosts);
		listFavorites.setAdapter(favoritesListAdapter);
		listFavorites.setScrollViewCallbacks(this);
		listFavorites.setOnScrollListener(new PauseOnScrollListener(Spark.imageLoader, true, true));

		// Надпись "Нет записей"
		findViewById(R.id.textEmpty).setVisibility(
				favoritePosts.isEmpty() ? View.VISIBLE : View.INVISIBLE);

		Drawable arrow = Spark.resources.getDrawable(R.drawable.ic_arrow_back);
		arrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
		toolbar.setNavigationIcon(arrow);
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging)
	{
		floatingToolbar.processScroll(scrollY, firstScroll);
	}

	@Override
	public void onDownMotionEvent()
	{
	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState)
	{
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();

		if (id == android.R.id.home)
			onBackPressed();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(0, R.anim.fade_out);
	}
}
