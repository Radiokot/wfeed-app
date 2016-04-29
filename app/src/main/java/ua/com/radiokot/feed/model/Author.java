package ua.com.radiokot.feed.model;

import org.json.JSONObject;

/**
 * Автор в ленте.
 */
public class Author
{
	public String apiId, name, photo, siteName, siteUrl;
	public int id, siteId, categoryId;

	public Author(JSONObject json)
	{
		this.id = json.optInt("id");
		this.apiId = json.optString("apiId");
		this.name = json.optString("authorName");
		this.photo = json.optString("authorPhoto");
		this.siteId = json.optInt("siteId");
		this.categoryId = json.optInt("categoryId");
		this.siteName = json.optString("siteName");
		this.siteUrl = json.optString("siteUrl");
	}
}
