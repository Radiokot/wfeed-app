package ua.com.radiokot.feed.tasks;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.util.ArrayList;

import ua.com.radiokot.feed.CategoriesActivity;
import ua.com.radiokot.feed.Feed;
import ua.com.radiokot.feed.Spark;
import ua.com.radiokot.feed.adapters.CategoriesListAdapter;
import ua.com.radiokot.feed.model.Category;

/**
 * Задача загрузки категорий.
 */
public class CategoriesLoadingTask extends AsyncTask<Void, Void, Void>
{
	CategoriesListAdapter listAdapter;
	CategoriesActivity displayActivity;

	public CategoriesLoadingTask(CategoriesListAdapter listAdapter, CategoriesActivity displayActivity)
	{
		super();
		this.listAdapter = listAdapter;
		this.displayActivity = displayActivity;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		displayActivity.showProcess();
	}

	@Override
	protected Void doInBackground(Void... params)
	{
		while (!isCancelled())
		{
			try
			{
				ArrayList<Category> categories = Feed.getCategories();

				for (Category category : categories)
					listAdapter.add(category);
			}
			catch (Exception e)
			{
				try
				{
					Thread.sleep(5000);
					continue;
				}
				catch (Exception se)
				{
					return null;
				}
			}

			return null;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
		super.onPostExecute(result);

		listAdapter.notifyDataSetChanged();
		displayActivity.hideProcess();
	}
}
