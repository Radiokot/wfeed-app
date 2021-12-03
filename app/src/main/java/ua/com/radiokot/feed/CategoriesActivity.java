package ua.com.radiokot.feed;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

import ua.com.radiokot.feed.adapters.CategoriesListAdapter;
import ua.com.radiokot.feed.model.Category;
import ua.com.radiokot.feed.tasks.CategoriesLoadingTask;

public class CategoriesActivity extends BaseActivity
{
    ArrayList<Category> categories = new ArrayList<>();
    CategoriesLoadingTask loader;
    ProgressWheel progressWheel;
    ImageButton buttonConfirm;
    boolean needUpdateFeed;

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_categories;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ListView listCategories = (ListView) findViewById(R.id.listCategories);
        progressWheel = (ProgressWheel) findViewById(R.id.progress);
        buttonConfirm = (ImageButton) findViewById(R.id.buttonConfirm);

        CategoriesListAdapter adapter = new CategoriesListAdapter(this, categories);
        listCategories.setAdapter(adapter);

        loader = new CategoriesLoadingTask(adapter, this);
        loader.execute();

        needUpdateFeed = getIntent().getBooleanExtra("needUpdateFeed", false);

        buttonConfirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateSelectedCategoriesIfNeeded();

                if (needUpdateFeed)
                    FeedActivity.updateFeed(true);

                finish();
            }
        });

        if (needUpdateFeed)
        {
            Drawable arrow = Spark.resources.getDrawable(R.drawable.ic_arrow_back);
            arrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            toolbar.setNavigationIcon(arrow);
        }
    }

    public void showProcess()
    {
        progressWheel.setVisibility(View.VISIBLE);
    }

    public void hideProcess()
    {
        progressWheel.setVisibility(View.INVISIBLE);
    }

    public void showConfirmButton()
    {
        buttonConfirm.setVisibility(View.VISIBLE);
    }

    public void hideConfirmButton()
    {
        buttonConfirm.setVisibility(View.INVISIBLE);
    }

    public void updateSelectedCategories()
    {
        Feed.selectedCategoriesIds.clear();
        for (Category category : categories)
            if (category.isSelected)
                Feed.selectedCategoriesIds.add(category.id);
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
        updateSelectedCategoriesIfNeeded();
        super.onBackPressed();
        overridePendingTransition(0, R.anim.fade_out);
    }

    private void updateSelectedCategoriesIfNeeded() {
        // Если достаточно категорий для сохранения, то сохраним.
        if (buttonConfirm.isShown())
        {
            setResult(RESULT_OK);
            updateSelectedCategories();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FlurryAgent.onStartSession(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        loader.cancel(true);
    }
}