package ua.com.radiokot.feed.model;

import java.util.ArrayList;

/**
 * Линия в фото-мозаике.
 */
public class PhotoLine
{
    public ArrayList<PhotoAttachment> photos;
    public int width;
    public int margin;
    private int minHeight;
    private int unscaledHeight;
    private double prevPartWidth;

    public PhotoLine(int width, int margin, int minHeight)
    {
        photos = new ArrayList<>();
        this.width = width;
        this.margin = margin;
        this.minHeight = minHeight;
    }

    public boolean canAddPhoto(PhotoAttachment photo)
    {
        return photos.size() == 0 || calcHeightWithPhoto(photo) >= minHeight;
    }

    private double calcHeightWithPhoto(PhotoAttachment photo)
    {
        photo.setNewSize(unscaledHeight);
        return unscaledHeight * (width / (prevPartWidth + photo.width));
    }

    private double calcHeight()
    {
        return unscaledHeight * (this.width / prevPartWidth);
    }

    public void addPhoto(PhotoAttachment photo)
    {
        photos.add(photo);
        if (photos.size() == 1)
        {
            unscaledHeight = photo.height;
            prevPartWidth = photo.width;
        }
        else
        {
            width -= margin;
            photo.setNewSize(unscaledHeight);
            prevPartWidth += photo.width;
        }

        double height = calcHeight();

        for (PhotoAttachment item : photos)
            item.setNewSize(height);
    }
}
