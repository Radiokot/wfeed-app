package ua.com.radiokot.feed.model;

import java.util.ArrayList;

/**
 * Фото-мозаика.
 */
public class PhotoMosaic
{
    public ArrayList<PhotoLine> photoLines;

    public int width;
    public int margin;

    public PhotoMosaic(ArrayList<PhotoAttachment> photos, int width, int margin, int minLineHeight)
    {
        this.width = width;
        this.margin = margin;

        photoLines = new ArrayList<>();
        photoLines.add(new PhotoLine(width, margin, minLineHeight));

        int currentPhotoIndex = 0;
        int currentPhotoLineIndex = 0;

        while (currentPhotoIndex < photos.size())
        {
            PhotoLine currentPhotoLine = photoLines.get(currentPhotoLineIndex);
            PhotoAttachment currentPhoto = photos.get(currentPhotoIndex);

            if (currentPhotoLine.canAddPhoto(currentPhoto) ||
                    (currentPhotoLine.photos.size() == 0 && currentPhotoIndex == photos.size() - 1))
            {
                currentPhotoLine.addPhoto(currentPhoto);
                currentPhotoIndex++;
            }
            else
            {
                photoLines.add(new PhotoLine(width, margin, minLineHeight));
                currentPhotoLineIndex++;
            }
        }
    }
}
