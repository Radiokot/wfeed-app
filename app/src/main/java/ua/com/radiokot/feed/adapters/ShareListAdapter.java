package ua.com.radiokot.feed.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.model.NavigationListItem;

/**
 * Адаптер списка вариантов поделиться постом.
 */
public class ShareListAdapter extends ArrayAdapter<NavigationListItem>
{
	private ArrayList<NavigationListItem> itemsArrayList;
	private LayoutInflater inflater;

	public ShareListAdapter(Activity context, ArrayList<NavigationListItem> itemsArrayList)
	{
		super(context, R.layout.navigation_item, itemsArrayList);

		this.itemsArrayList = itemsArrayList;
		this.inflater =  context.getLayoutInflater();
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
				if (item.clickAction == NavigationListItem.ClickAction.INTENT)
					getContext().startActivity(item.target);
				else if (item.clickAction == NavigationListItem.ClickAction.RUNNABLE)
					item.runnable.run();
			}
		});

		return rowView;
	}
}
