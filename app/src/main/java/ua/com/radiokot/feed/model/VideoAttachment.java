package ua.com.radiokot.feed.model;

import org.json.JSONObject;

/**
 * Вложение - видео.
 */
public class VideoAttachment extends Attachment
{
	public String name, preview, video240, video360, video480, video720, video1080;

	public VideoAttachment(JSONObject json)
	{
		super(json.optString("id"), Type.VIDEO);

		this.name = json.optString("videoName");
		this.preview = json.optString("videoPreview");
		this.video240 = json.optString("video240");
		this.video360 = json.optString("video360");
		this.video480 = json.optString("video480");
		this.video720 = json.optString("video720");
		this.video1080 = json.optString("video1080");

		this.VkAttId = json.optString("VkAttId");
	}
}
