package ua.com.radiokot.feed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.model.NavigationListItem;
import ua.com.radiokot.feed.util.NavigationDrawer;

/**
 * Адаптер навигации.
 */
public class NavigationDrawerAdapter extends ArrayAdapter<NavigationListItem>
{
	private NavigationDrawer drawer;
	private ArrayList<NavigationListItem> itemsArrayList;
	private LayoutInflater inflater;

	public NavigationDrawerAdapter(NavigationDrawer drawer, ArrayList<NavigationListItem> itemsArrayList)
	{
		super(drawer.displayActivity, R.layout.navigation_item, itemsArrayList);

		this.drawer = drawer;
		this.itemsArrayList = itemsArrayList;
		this.inflater =  drawer.displayActivity.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = inflater.inflate(R.layout.navigation_item, parent, false);

		ImageView imageNavItemIcon = (ImageView) rowView.findViewById(R.id.imageNavItemIcon);
		TextView textNavItemCaption = (TextView) rowView.findViewById(R.id.textNavItemCaption);
		View divider = rowView.findViewById(R.id.divider);

		final NavigationListItem item = itemsArrayList.get(position);

		imageNavItemIcon.setImageDrawable(item.icon);
		textNavItemCaption.setText(item.caption);
		if (item.divider)
			divider.setVisibility(View.VISIBLE);

		rowView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				drawer.hide();
				if (item.clickAction == NavigationListItem.ClickAction.INTENT)
					drawer.displayActivity.startActivity(item.target);
				else if (item.clickAction == NavigationListItem.ClickAction.RUNNABLE)
					item.runnable.run();
			}
		});

		return rowView;
	}

	@Override
	public void add(NavigationListItem object)
	{
		itemsArrayList.add(object);
		notifyDataSetChanged();
	}
}
