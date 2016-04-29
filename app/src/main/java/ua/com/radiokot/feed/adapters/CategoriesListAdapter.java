package ua.com.radiokot.feed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ua.com.radiokot.feed.CategoriesActivity;
import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.Spark;
import ua.com.radiokot.feed.model.Category;

/**
 * Адаптер списка категорий.
 */
public class CategoriesListAdapter extends ArrayAdapter<Category>
{
	CategoriesActivity displayActivity;
	ArrayList<Category> itemsArrayList;
	LayoutInflater inflater;

	public CategoriesListAdapter(CategoriesActivity displayActivity, ArrayList<Category> itemsArrayList)
	{
		super(displayActivity, R.layout.list_category, itemsArrayList);

		this.displayActivity = displayActivity;
		this.itemsArrayList = itemsArrayList;
		this.inflater = displayActivity.getLayoutInflater();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View rowView = inflater.inflate(R.layout.list_category, parent, false);

		LinearLayout layoutCategory = (LinearLayout) rowView.findViewById(R.id.layoutCategory);
		ImageView imageThumb = (ImageView) rowView.findViewById(R.id.imageThumb);
		TextView textDescription = (TextView) rowView.findViewById(R.id.textDescription);
        TextView textAuthorsCount = (TextView) rowView.findViewById(R.id.textAuthorsCount);
		final CheckBox checkboxSelected = (CheckBox) rowView.findViewById(R.id.checkboxSelected);

		final Category category = itemsArrayList.get(position);

		textDescription.setText(category.description);
        textAuthorsCount.setText(String.valueOf(category.authorsCount) +
                Spark.getNumEnding(category.authorsCount,
                    Spark.resources.getString(R.string.category_authors_count).split("/")));
		Spark.imageLoader.displayImage(category.thumb, imageThumb);

		layoutCategory.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				checkboxSelected.setChecked(!checkboxSelected.isChecked());
			}
		});

		checkboxSelected.setChecked(category.isSelected);
		checkboxSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				category.isSelected = isChecked;
				checkSelectedCategories();
			}
		});

		return rowView;
	}

	@Override
	public void add(Category object)
	{
		itemsArrayList.add(object);
	}

	@Override
	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
		checkSelectedCategories();
	}

	private void checkSelectedCategories()
	{
		int selectedCount = 0;

		for (Category category : itemsArrayList)
			if (category.isSelected)
				selectedCount += 1;

		if (selectedCount > 0)
			displayActivity.showConfirmButton();
		else
			displayActivity.hideConfirmButton();
	}
}
