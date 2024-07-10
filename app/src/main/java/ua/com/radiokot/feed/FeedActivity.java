package ua.com.radiokot.feed;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import androidx.appcompat.widget.PopupMenu;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import ua.com.radiokot.feed.adapters.FeedListAdapter;
import ua.com.radiokot.feed.model.NavigationListItem;
import ua.com.radiokot.feed.util.FloatingToolbar;
import ua.com.radiokot.feed.util.NavigationDrawer;

public class FeedActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    public static int DISPLAY_WIDTH, DISPLAY_HEIGHT;
    public static int HALF_DP;

    // Список ленты.
    private ObservableListView listFeed;
    // Навигация.
    private static NavigationDrawer navigationDrawer;
    // "Плавающий" тулбар
    private FloatingToolbar floatingToolbar;
    // Флаг перезагрузки.
    private boolean isReloading = false;
    // Адаптер ленты.
    public static FeedListAdapter feedListAdapter;
    // ID рекламы.
    private static final String MOPUB_UNIT_ID = "035a5d4d270e4bd492beee82d1cf0f0c";

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_feed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Сделаем обычный тулбар плавающим.
        floatingToolbar = new FloatingToolbar(toolbar);

        // Получим метрику экрана.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        DISPLAY_WIDTH = metrics.widthPixels;
        DISPLAY_HEIGHT = metrics.heightPixels;
        HALF_DP = (int) (0.5 * metrics.density + 0.5f);
        if (HALF_DP == 0)
            HALF_DP = 1;

        // Настроим навигацию.
        initNavigation();

        // Инициализируем пользователя в ВК.
        notifyAccountChanged();

        // Настроим список.
        listFeed = (ObservableListView) findViewById(R.id.listFeed);

        feedListAdapter = new FeedListAdapter(FeedActivity.this, Feed.posts);
        /*ViewBinder viewBinder = new ViewBinder.Builder(R.layout.list_post_ad)
                .mainImageId(R.id.imageAdMain)
                .iconImageId(R.id.imageAdIcon)
                .titleId(R.id.textAdTitle)
                .textId(R.id.textAd)
                .addExtra("Type_" + Spark.resources.getString(R.string.locale),
                    R.id.textAdSponsored)
                .build();

        // Set up the positioning behavior your ads should have.
        MoPubNativeAdPositioning.MoPubServerPositioning adPositioning =
                MoPubNativeAdPositioning.serverPositioning();
        MoPubStaticNativeAdRenderer adRenderer = new MoPubStaticNativeAdRenderer(viewBinder);

        // Set up the MoPubAdAdapter
        mopubAdAdapter = new MoPubAdAdapter(this, feedListAdapter, adPositioning);
        mopubAdAdapter.registerAdRenderer(adRenderer);*/

        listFeed.setAdapter(feedListAdapter);
        listFeed.setScrollViewCallbacks(this);

        // Повесим подгрузку ленты по мере скролла.
        listFeed.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if (firstVisibleItem == (totalItemCount - 20))
                    updateFeed(false);
            }

            // А это не дает картинкам загружаться пока идет прокрутка.
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                // остановим загрузку картинок во время прокрутки
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
                {
                    Spark.imageLoader.pause();
                }
                else
                {
                    Spark.imageLoader.resume();
                }
            }
        });

        // Добавим первую пачку постов.
        updateFeed(true);
    }

    // Инициализация меню навигации.
    private void initNavigation()
    {//(navigationWidth <= 10 * 64 * HALF_DP) ? navigationWidth : 10 * 64 * HALF_DP
        int navigationWidth = DISPLAY_WIDTH - (int) Spark.resources.getDimension(R.dimen.toolbar_height);
        navigationDrawer = new NavigationDrawer(this, (FrameLayout) findViewById(R.id.layoutNavContent),
                Math.min(navigationWidth, 320 * 2 * HALF_DP));

        Intent categoriesIntent = new Intent(this, CategoriesActivity.class);
        categoriesIntent.putExtra("needUpdateFeed", true);
        navigationDrawer.addItem(new NavigationListItem(Spark.resources.getDrawable(R.drawable.ic_filter_variant),
                Spark.resources.getString(R.string.navigation_categories_caption), false,
                categoriesIntent));

        navigationDrawer.addItem(new NavigationListItem(Spark.resources.getDrawable(R.drawable.ic_star),
                Spark.resources.getString(R.string.navigation_favorite_caption), true,
                new Intent(this, FavoritesActivity.class)));

        navigationDrawer.addItem(new NavigationListItem(Spark.resources.getDrawable(R.drawable.ic_thumb_up_outline),
                Spark.resources.getString(R.string.navigation_rate_caption), false,
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Spark.longToast(Spark.resources.getString(R.string.toast_rate_wish));
                        String appPackageName = getPackageName();
                        try
                        {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" + appPackageName)));
                        }
                        catch (android.content.ActivityNotFoundException nfe)
                        {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                }));

        navigationDrawer.addItem(new NavigationListItem(Spark.resources.getDrawable(R.drawable.ic_comment_plus_outline),
                Spark.resources.getString(R.string.navigation_suggest_caption), false,
                new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://feed.radiokot.com.ua/suggest?hl="
                                + Spark.resources.getString(R.string.locale)))));

        // Добавим клик на имя.
        navigationDrawer.textNavUserName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Social.authorizedVk)
                {
                    showAuthMenu(navigationDrawer.textNavUserName);
                }
                else
                {
                    navigationDrawer.hide();

                    Social.authVk(FeedActivity.this, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Social.initUserVk(navigationDrawer.imageNavUserPhoto,
                                    navigationDrawer.textNavUserName);
                        }
                    });
                }
            }
        });

        // Покажем таким странным способом кнопку в тулбар.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_navigation_menu);
    }

    // Показать всплывающее меню аккаунта.
    private void showAuthMenu(View view)
    {
        final PopupMenu authMenu = new PopupMenu(this, view);
        authMenu.inflate(R.menu.menu_nav_auth);

        authMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.action_auth_change:
                        navigationDrawer.hide();
                        Social.authVk(FeedActivity.this, new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                notifyAccountChanged();
                            }
                        });
                        break;
                    case R.id.action_auth_out:
                        Social.logoutVk();
                        break;
                }

                return true;
            }
        });

        authMenu.show();
    }

    // Обновление информации о пользователе.
    public static void notifyAccountChanged()
    {
        if (navigationDrawer != null)
            Social.initUserVk(navigationDrawer.imageNavUserPhoto, navigationDrawer.textNavUserName);
    }

    public static void updateFeed(boolean clear)
    {
        Feed.updateAsync(feedListAdapter, clear);
    }

    public void reload()
    {
        isReloading = true;
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    // Сообщение тулбару о скроле.
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
            navigationDrawer.show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (navigationDrawer.isOpen())
            navigationDrawer.hide();
        else
            super.onBackPressed();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Spark.writePrefs();
    }

    // При закрытии не через вычеркивание тотально выпилить себя.
    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (!isReloading)
            System.exit(0);

        isReloading = false;
    }
}
