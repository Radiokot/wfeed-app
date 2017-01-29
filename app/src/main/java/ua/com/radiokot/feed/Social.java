package ua.com.radiokot.feed;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageButton;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import ua.com.radiokot.feed.model.PhotoAttachment;
import ua.com.radiokot.feed.model.Post;
import ua.com.radiokot.feed.tasks.VkPhotoSaveTask;
import ua.com.radiokot.feed.tasks.VkPostCopyTask;
import ua.com.radiokot.feed.tasks.VkPostLikeSwitchTask;
import ua.com.radiokot.feed.tasks.VkUserInitTask;

/**
 * Содержит всякие социальные штуки
 */
public class Social
{
    public static String tokenVk;
    public static String userName;
    public static boolean authorizedVk = false;

    // Смена лайка поста в ВК.
    public static void switchPostLikeVk(final Post post, final ImageButton button)
    {
        if (authorizedVk)
            new VkPostLikeSwitchTask(post, button, tokenVk).execute();
        else
        {
            authVk(button.getContext(), new Runnable()
            {
                @Override
                public void run()
                {
                    if (authorizedVk)
                    {
                        switchPostLikeVk(post, button);

                        FeedActivity.notifyAccountChanged();
                    }
                }
            });
        }
    }

    // Сохранение фотки в ВК.
    public static void savePhotoVk(final PhotoAttachment photo, final Activity context)
    {
        if (authorizedVk)
            new VkPhotoSaveTask(photo, tokenVk).execute();
        else
        {
            authVk(context, new Runnable()
            {
                @Override
                public void run()
                {
                    if (authorizedVk)
                    {
                        savePhotoVk(photo, context);

                        FeedActivity.notifyAccountChanged();
                    }
                }
            });
        }
    }

    // Репост в ВК.
    public static void copyPostVk(final Post post, final Activity context)
    {
        if (authorizedVk)
            new VkPostCopyTask(post, tokenVk).execute();
        else
        {
            authVk(context, new Runnable()
            {
                @Override
                public void run()
                {
                    if (authorizedVk)
                    {
                        copyPostVk(post, context);

                        FeedActivity.notifyAccountChanged();
                    }
                }
            });
        }
    }

    // Авторизация в ВК.
    public static void authVk(Context context, Runnable callback)
    {
        AuthActivity.launch(context, callback);
    }

    // Выход из аккаунта ВК.
    public static void logoutVk()
    {
        onNewTokenVk(null);
        Feed.likedPostsIds.clear();
        FeedActivity.notifyAccountChanged();
    }

    public static void onNewTokenVk(String token)
    {
        tokenVk = token;
        authorizedVk = (tokenVk != null);
    }

    // Инициализация пользователя в ВК с обновлением навигации.
    public static void initUserVk(CircleImageView imagePhoto, TextView textName)
    {
        if (!authorizedVk)
        {
            imagePhoto.setImageDrawable(null);//Spark.resources.getDrawable(R.drawable.ic_launcher));
            textName.setText(R.string.navigation_unauthorized_name);

            return;
        }

        new VkUserInitTask(imagePhoto, textName, tokenVk).execute();
    }
}
