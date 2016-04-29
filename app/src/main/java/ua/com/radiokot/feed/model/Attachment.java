package ua.com.radiokot.feed.model;

import org.json.JSONObject;

/**
 * Вложение из поста.
 */
public class Attachment
{
	public enum Type
	{
		PHOTO, VIDEO
	}

	public Type type;
	public String id, VkAttId;

	public Attachment(String id, Type type)
	{
		//определим тип
		this.type = type;
		this.id = id;
	}

	public static Attachment create(JSONObject json)
	{
		String type = json.optString("type");

		if (type.equals("photo"))
			return new PhotoAttachment(json);
		else if (type.equals("video"))
			return new VideoAttachment(json);
		else
			return null;
	}
}
