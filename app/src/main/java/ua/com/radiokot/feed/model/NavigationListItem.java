package ua.com.radiokot.feed.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Элемент навигации с возможностью запуска интента или произвольного кода.
 */
public class NavigationListItem
{
	public enum ClickAction
	{
		INTENT, RUNNABLE
	}

	public Intent target;
	public Runnable runnable;
	public Drawable icon;
	public String caption;
	public boolean divider;
	public ClickAction clickAction;

	public NavigationListItem(Drawable icon, String caption, boolean divider, Intent target)
	{
		this.target = target;
		this.clickAction = ClickAction.INTENT;
		this.icon = icon;
		this.caption = caption;
		this.divider = divider;
	}

	public NavigationListItem(Drawable icon, String caption, boolean divider, Runnable action)
	{
		this.runnable = action;
		this.clickAction = ClickAction.RUNNABLE;
		this.icon = icon;
		this.caption = caption;
		this.divider = divider;
	}
}
