package ua.com.radiokot.feed.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import ua.com.radiokot.feed.BaseActivity;
import ua.com.radiokot.feed.Feed;
import ua.com.radiokot.feed.FeedActivity;
import ua.com.radiokot.feed.PhotoActivity;
import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.ShareActivity;
import ua.com.radiokot.feed.Social;
import ua.com.radiokot.feed.Spark;
import ua.com.radiokot.feed.model.PhotoAttachment;
import ua.com.radiokot.feed.model.PhotoLine;
import ua.com.radiokot.feed.model.PhotoMosaic;
import ua.com.radiokot.feed.model.Post;
import ua.com.radiokot.feed.model.ServicePostItem;

/**
 * Адаптер ленты.
 */
public class FeedListAdapter extends ArrayAdapter<Post>
{
    private static Activity displayActivity;
    private final ArrayList<Post> itemsArrayList;
    private LayoutInflater inflater;
    public Post serviceItem;

    //Обработчик клика для раскрытия поста
    public static View.OnClickListener clickDotsExpand = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            //получим данные из тега
            TextView textPost = (TextView) v.getTag(R.id.textPost);
            LinearLayout layoutSocial = (LinearLayout) v.getTag(R.id.layoutSocial);
            Post post = (Post) v.getTag(R.id.layoutPost);

            //развернем текст
            textPost.setMaxLines(Integer.MAX_VALUE);
            textPost.setEllipsize(null);
            post.isExpanded = true;

            //скроем иконку лайка, если это не пост из ВК
            layoutSocial.getChildAt(0).setVisibility(post.author.siteId == 1 ? View.VISIBLE : View.GONE);

            //покажем социальные иконки
            layoutSocial.setVisibility(View.VISIBLE);

            //анимируем их
            final ScaleAnimation animScale = new ScaleAnimation(0f, 1f, 0f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animScale.setInterpolator(new DecelerateInterpolator());
            animScale.setDuration(320);

            //попутно зададим клики и теги
            for (int i = 0; i < layoutSocial.getChildCount(); i++)
            {
                final View child = layoutSocial.getChildAt(i);

                child.setTag(R.id.layoutPost, post);
                child.setTag(R.id.action_settings, ExpandAction.values()[i]);
                child.setOnClickListener(clickSocialButton);

                if (child.getVisibility() == View.VISIBLE)
                    child.startAnimation(animScale);
            }

            //скроем точки
            v.setVisibility(View.GONE);
        }
    };

    // Обработчик клика по фотографии.
    public static View.OnClickListener clickPhoto = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            //получим данные из тега
            Post post = (Post) v.getTag(R.id.layoutPost);
            int from = (int) v.getTag(R.id.home);

            PhotoActivity.launch((BaseActivity) v.getContext(), post, from);
        }
    };

    // Обработчик длинного клика по фотографии (для лайка).
    public static View.OnLongClickListener longClickPhoto = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View view)
        {
            //получим данные из тега
            Post post = (Post) view.getTag(R.id.layoutPost);

            if (post.author.siteId != 1)
                return true;

            ImageButton likeButton = (ImageButton) view.getTag(R.id.buttonLike);

            if (!post.isLiked)
                Social.switchPostLikeVk(post, likeButton);

            showLikeToast((Activity) view.getContext());

            return true;
        }
    };

    // Обработчик клика по раскрывающимся кнопкам.
    public static View.OnClickListener clickSocialButton = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            // Получим данные из тега.
            Post post = (Post) v.getTag(R.id.layoutPost);
            ExpandAction expandAction = (ExpandAction) v.getTag(R.id.action_settings);

            if (expandAction == ExpandAction.LIKE)
                Social.switchPostLikeVk(post, (ImageButton) v);
            else if (expandAction == ExpandAction.FAVORITE)
            {
                if (!post.isFavorite)
                {
                    Feed.addPostToFavorite(post, (ImageButton) v);
                    Spark.shortToast(Spark.resources.getString(R.string.toast_favorites_add));
                }
                else
                {
                    Feed.removePostFromFavorite(post, (ImageButton) v);
                    Spark.shortToast(Spark.resources.getString(R.string.toast_favorites_del));
                }
            }
            else if (expandAction == ExpandAction.SHARE)
                ShareActivity.launch(displayActivity, post);
        }
    };

    // Показать анимацию лайка поста.
    public static void showLikeToast(Activity context)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_like,
                (ViewGroup) context.findViewById(R.id.toast_like_root));

        ImageView animLike = (ImageView) layout.findViewById(R.id.animLike);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

        ((AnimationDrawable) animLike.getBackground()).start();
    }

    public enum ExpandAction
    {
        LIKE, FAVORITE, SHARE
    }

    public FeedListAdapter(Activity context, ArrayList<Post> itemsArrayList)
    {
        super(context, R.layout.list_post, itemsArrayList);

        displayActivity = context;
        this.itemsArrayList = itemsArrayList;
        this.inflater = displayActivity.getLayoutInflater();
    }

    //Holder поста
    static class ViewHolder
    {
        public RelativeLayout layoutPost;
        public TextView textAuthorName;
        public TextView textPostInfo;
        public TextView textPost;
        public ImageView imageAuthorPhoto;
        public LinearLayout layoutPostContent;
        public LinearLayout layoutSocial;
        public TextView dotsExpand;

        public ImageButton buttonLike;
        public ImageButton buttonFavorite;
        public ImageButton buttonShare;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        View rowView = convertView;
        Post post = itemsArrayList.get(position);

        // Логика для кружка загрузки.
        if (itemsArrayList.get(position).displayType == Post.Type.LOADER)
        {
            rowView = inflater.inflate(R.layout.list_loader, parent, false);

            rowView.setOnClickListener(null);
            rowView.setOnLongClickListener(null);
            rowView.setLongClickable(false);

            return rowView;
        }
        else if (itemsArrayList.get(position).displayType == Post.Type.INFO)
        {
            rowView = inflater.inflate(R.layout.list_info, parent, false);

            rowView.setOnClickListener(null);
            rowView.setOnLongClickListener(null);
            rowView.setLongClickable(false);

            ServicePostItem.InfoItem info = (ServicePostItem.InfoItem) itemsArrayList.get(position);

            ((TextView) rowView.findViewById(R.id.textInfoCaption)).setText(info.caption);
            ((TextView) rowView.findViewById(R.id.textInfoMessage)).setText(info.message);
            ((ImageView) rowView.findViewById(R.id.imageInfo)).setImageDrawable(info.image);

            return rowView;
        }

        if (rowView == null || rowView.getTag() == null)
        {
            holder = new ViewHolder();
            rowView = inflater.inflate(R.layout.list_post, parent, false);

            holder.layoutPost = (RelativeLayout) rowView.findViewById(R.id.layoutPost);
            holder.textAuthorName = (TextView) rowView.findViewById(R.id.textAuthorName);
            holder.textPost = (TextView) rowView.findViewById(R.id.textPost);
            holder.textPostInfo = (TextView) rowView.findViewById(R.id.textPostInfo);
            holder.imageAuthorPhoto = (ImageView) rowView.findViewById(R.id.imageAuthorPhoto);
            holder.layoutPostContent = (LinearLayout) rowView.findViewById(R.id.layoutPostContent);
            holder.layoutSocial = (LinearLayout) rowView.findViewById(R.id.layoutSocial);
            holder.dotsExpand = (TextView) rowView.findViewById(R.id.dotsExpand);

            holder.buttonLike = (ImageButton) rowView.findViewById(R.id.buttonLike);
            holder.buttonFavorite = (ImageButton) rowView.findViewById(R.id.buttonFavorite);
            holder.buttonShare = (ImageButton) rowView.findViewById(R.id.buttonShare);

            rowView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) rowView.getTag();
        }

        //установка свойств

        //сначала решим, показывать ли нам пост раскрытым
        if (post.isExpanded)
        {
            holder.textPost.setMaxLines(Integer.MAX_VALUE);
            holder.layoutSocial.setVisibility(View.VISIBLE);
            holder.dotsExpand.setVisibility(View.GONE);
        }
        else
        {
            holder.textPost.setMaxLines(2);
            holder.layoutSocial.setVisibility(View.GONE);
            holder.dotsExpand.setVisibility(View.VISIBLE);
        }

        // В зависимости от лайка ставим картинку иконке. Это важно делать здесь.
        ((ImageView) holder.layoutSocial.getChildAt(0)).setImageDrawable(post.isLiked ?
                ContextCompat.getDrawable(Spark.context, R.drawable.ic_like_done) :
                ContextCompat.getDrawable(Spark.context, R.drawable.ic_like));

        // С избранным так же.
        ((ImageView) holder.layoutSocial.getChildAt(1)).setImageDrawable(post.isFavorite ?
                ContextCompat.getDrawable(Spark.context, R.drawable.ic_favorite_done) :
                ContextCompat.getDrawable(Spark.context, R.drawable.ic_favorite));

        //ссылка на элементы лейаута в тег точек раскрытия поста
        holder.dotsExpand.setTag(R.id.textPost, holder.textPost);
        holder.dotsExpand.setTag(R.id.layoutSocial, holder.layoutSocial);
        holder.dotsExpand.setTag(R.id.layoutPost, post);

        //клик для раскрытия
        holder.dotsExpand.setOnClickListener(FeedListAdapter.clickDotsExpand);

        // цвет по четности
        holder.layoutPost.setBackgroundColor((position % 2 == 0) ? Color.TRANSPARENT : Spark.resources.getColor(R.color.post_dark));

        //имя
        holder.textAuthorName.setText(post.author.name);

        //информиция
        holder.textPostInfo.setText(post.info);

        //текст
        if (!post.text.equals(""))
        { //если есть текст
            holder.textPost.setVisibility(View.VISIBLE);
            holder.textPost.setText(post.text);
        }
        else
            holder.textPost.setVisibility(View.GONE);

        //аватар
        if (holder.imageAuthorPhoto.getTag() == null ||
                !holder.imageAuthorPhoto.getTag().equals(post.author.photo))
        {
            Spark.imageLoader.displayImage(post.author.photo, holder.imageAuthorPhoto, Spark.nofadeDisplayOption);
            holder.imageAuthorPhoto.setTag(post.author.photo);
        }

        //вложения

        if (holder.layoutPostContent.getTag() == null ||
                !holder.layoutPostContent.getTag().equals(post.id))
        {
            PhotoMosaic photoMosaic = new PhotoMosaic(post.photos, FeedActivity.DISPLAY_WIDTH,
                    FeedActivity.HALF_DP * 2, (int) Math.ceil(FeedActivity.DISPLAY_HEIGHT * 0.27));
            dispayPhotoMosaic(post, photoMosaic, holder.layoutPostContent, holder.buttonLike);
            holder.layoutPostContent.setTag(post.id);
        }

        return rowView;
    }

    private void dispayPhotoMosaic(Post post, PhotoMosaic photoMosaic, LinearLayout layout,
                                   ImageButton postLikeButton)
    {
        int placedPhotos = 0;
        layout.removeAllViews();
        for (PhotoLine photoLine : photoMosaic.photoLines)
        {
            LinearLayout line = new LinearLayout(displayActivity);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                    photoMosaic.width,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lineParams.topMargin = photoMosaic.margin;
            line.setLayoutParams(lineParams);
            line.setLongClickable(false);
            line.setClickable(false);

            layout.addView(line);

            int i = 0;
            for (PhotoAttachment photo : photoLine.photos)
            {
                ImageView image = new ImageView(displayActivity);

                LinearLayout.LayoutParams imageParams =
                        new LinearLayout.LayoutParams(photo.width, photo.height);
                imageParams.leftMargin = (i != 0) ? photoMosaic.margin : 0;
                image.setLayoutParams(imageParams);
                //Log.i("FeedListAdapter", "Photo width " + photo.width);

                //пост, номер и текущее изображение в тег
                image.setTag(R.id.layoutPost, post);
                image.setTag(R.id.home, placedPhotos);
                image.setTag(R.id.buttonLike, postLikeButton);

                image.setScaleType(ImageView.ScaleType.CENTER_CROP);

                line.addView(image);

                image.setBackgroundColor(Color.WHITE);
                Spark.imageLoader.displayImage(photo.getUrl(), image, Spark.fadeDisplayOption, new SimpleImageLoadingListener()
                {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                    {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        view.setBackgroundColor(Color.TRANSPARENT);
                    }
                });

                image.setOnClickListener(clickPhoto);
                image.setOnLongClickListener(longClickPhoto);

                i++;
                placedPhotos++;
            }
        }
    }

    // Добавление в ленту служебного элемента (загрузчик и т.д.).
    public void addServiceItem(Post item)
    {
        removeServiceItem();
        serviceItem = item;
        itemsArrayList.add(serviceItem);
    }

    // Удаление служебного элемента.
    public void removeServiceItem()
    {
        if (serviceItem != null)
            itemsArrayList.remove(serviceItem);
    }
}
