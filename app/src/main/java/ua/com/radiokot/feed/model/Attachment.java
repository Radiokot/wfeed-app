package ua.com.radiokot.feed.model;

import org.json.JSONObject;

/**
 * Вложение из поста.
 */
public class Attachment implements Comparable<Attachment>
{
	public String id, VkAttId;
	private Integer i;

	Attachment(String id, int i)
	{
		//определим тип
		this.id = id;
		this.i = i;
	}

	public static Attachment create(JSONObject json)
	{
		String type = json.optString("type");

		if (type.equals("photo"))
			return new PhotoAttachment(json);
		else
			return null;
	}

	@Override
	public int compareTo(Attachment a) {
		return this.i.compareTo(a.i);
	}
}
