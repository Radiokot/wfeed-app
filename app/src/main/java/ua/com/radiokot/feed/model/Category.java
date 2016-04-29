package ua.com.radiokot.feed.model;

import org.json.JSONObject;

import ua.com.radiokot.feed.Feed;

/**
 * Категория контента.
 */
public class Category
{
	public String description, thumb;
	public int id, authorsCount;
	public boolean isSelected;

	public Category(JSONObject json, String locale)
	{
		this.id = json.optInt("id");

		switch (locale)
		{
			case "ru":
				this.description = json.optString("descriptionRu");
				break;
			case "uk":
				this.description = json.optString("descriptionUk");
				break;
			case "en":
				this.description = json.optString("descriptionEn");
				break;
		}

		this.thumb = json.optString("thumb");
		this.isSelected = Feed.selectedCategoriesIds.contains(id);
        this.authorsCount = json.optInt("authorsCount", 0);
	}
}
