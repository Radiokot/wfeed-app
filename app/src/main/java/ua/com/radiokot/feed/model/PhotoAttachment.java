package ua.com.radiokot.feed.model;

import org.json.JSONObject;

/**
 * Вложение - фото.
 */
public class PhotoAttachment extends Attachment
{
	public int height, width;
	public String photo130, photo604, photo807, photo1280, photo2560;
	public transient boolean loadedBig = false;

	public PhotoAttachment(JSONObject json)
	{
		super(json.optString("id"), json.optInt("i", 0));

		this.height = json.optInt("photoHeight");
		this.width = json.optInt("photoWidth");

		this.photo130 = json.optString("photo130");
		this.photo604 = json.optString("photo604");
		this.photo807 = json.optString("photo807");
		this.photo1280 = json.optString("photo1280");
		this.photo2560 = json.optString("photo2560");

		this.VkAttId = json.optString("VkAttId");
	}

	//Пропорциональное изменение размера по новой высота
	public void setNewSize(double newHeight)
	{
		width = (int) Math.ceil(width * newHeight / height);
		height = (int) (newHeight);
	}

	//Получение подходящего url'а
	public String getUrl()
	{
		if (width <= 130)
			if (height <= 150 && !photo130.equals("0"))
				return photo130;
			else
				return photo604;
		else if (width <= 604)
			return photo604;
		else if (width <= 807)
			if (height <= 830 && !photo807.equals("0"))
				return photo807;
			else
				return photo604;
		else if (width <= 1280)
			if (height <= 1300 && !photo1280.equals("0"))
				return photo1280;
			else
				return photo604;
		else if (width <= 2560)
			if (height <= 2600 && !photo2560.equals("0"))
				return photo2560;
			else
				return photo604;
		else
			return photo604;
	}

	//Получение максимального url'a
	public String getMaxUrl()
	{
		return (!photo2560.equals("0")) ? photo2560 :
				(!photo1280.equals("0") ? photo1280 :
				(!photo807.equals("0") ? photo807 :
						(!photo604.equals("0") ? photo604 :
								photo130)));
	}
}
