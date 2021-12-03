package ua.com.radiokot.feed.tasks;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;

import ua.com.radiokot.feed.Feed;
import ua.com.radiokot.feed.adapters.FeedListAdapter;
import ua.com.radiokot.feed.model.ServicePostItem;

/**
 * Задача обновления ленты.
 */
public class FeedUpdateTask extends AsyncTask<Void, Void, Void>
{
	FeedListAdapter adapter;
	boolean clear = false;

	public FeedUpdateTask(FeedListAdapter adapter, boolean clear)
	{
		super();
		this.adapter = adapter;
		this.clear = clear;
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();

		if (clear)
			Feed.clearFeed();

		adapter.addServiceItem(new ServicePostItem.LoaderItem());
		adapter.notifyDataSetChanged();

		Feed.nowUpdating = true;
	}

	@Override
	protected Void doInBackground(Void... params)
	{
		try
		{
			Feed.updateAll();
		}
		catch (UnknownHostException e)
		{
			adapter.addServiceItem(new ServicePostItem.InfoItem().displayNoConnection());
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			adapter.addServiceItem(new ServicePostItem.InfoItem().displayNoData());
			return null;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
		super.onPostExecute(result);

		Feed.nowUpdating = false;

		if (adapter.serviceItem.getClass() == ServicePostItem.LoaderItem.class)
			adapter.removeServiceItem();

		adapter.notifyDataSetChanged();
	}
}
