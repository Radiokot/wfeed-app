package ua.com.radiokot.feed;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Process;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLSocketFactory;

import ua.com.radiokot.feed.util.BackupAgent;
import ua.com.radiokot.feed.util.Database;
import ua.com.radiokot.feed.util.FadeInBitmapDisplayer;
import ua.com.radiokot.feed.util.TLSSocketFactory;

/*
    Коробочка с силами.
*/

public class Spark extends Application
{
    public static Context context;
    public static PackageInfo packageInfo;
    public static Resources resources;
    public static SharedPreferences preferences;
    public static Gson gson;
    public static Database database;

    public static boolean isFirstLaunch;

    public static ImageLoader imageLoader = ImageLoader.getInstance();

    public static DisplayImageOptions fadeDisplayOption;

    public static DisplayImageOptions nofadeDisplayOption = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .build();

    private static SSLSocketFactory sslSocketFactory;

    public void onCreate()
    {
        super.onCreate();

        context = getApplicationContext();

        try
        {
            Spark.packageInfo = Spark.context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.e("Spark", e.toString());
        }
        resources = context.getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
        database = new Database(context);

        // Проинициализируем ImageLoader.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(fadeDisplayOption)
                .denyCacheImageMultipleSizesInMemory()
                .threadPriority(Process.THREAD_PRIORITY_FOREGROUND)
                .discCacheSize(16777216) //кэша на 16 мб
                .build();
		fadeDisplayOption = new DisplayImageOptions.Builder()
			.displayer(
					new FadeInBitmapDisplayer(Spark.resources.getInteger(android.R.integer.config_shortAnimTime)
							, true, true, false))
			.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.build();

        ImageLoader.getInstance().init(config);

        // Читаем настройки.
        readPrefs();

        // Включаем Flurry.
        FlurryAgent.setLogEnabled(true);
        FlurryAgent.setLogLevel(Log.INFO);
        FlurryAgent.init(context, "99RZSRRQ77MS8QXNQ5QD");
        FlurryAgent.onStartSession(context);

        // Modern TLS for bad devices.
        if (areGooglePlayServicesAvailable()) {
            try {
                ProviderInstaller.installIfNeeded(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Получить ответ в JSON.
    public static JSONObject getJSON(String url) throws IOException, JSONException
    {
        String response;
        JSONObject json;
        InputStream is;

        try
        {
            // Получаем поток.
            URLConnection conn = new URL(url).openConnection();

            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                SSLSocketFactory socketFactory = getSslSocketFactory();
                if (socketFactory != null) {
                    httpsConn.setSSLSocketFactory(socketFactory);
                }
            }

            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("User-Agent", "WFeed Android/" +
                    String.valueOf(packageInfo.versionCode) + " spirit111@ukr.net");
            is = conn.getInputStream();

            // Читаем его в строку.
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }
            response = sb.toString();
        }
        catch (SSLProtocolException ssl)
        {
            return getJSON(url.replace("https://", "http://"));
        }
        catch (IOException io) {
            // In case of Cloudflare issues this will allow non-https bypass mode.
            if (url.startsWith("https://")) {
                return getJSON(url.replace("https://", "http://"));
            } else {
                throw io;
            }
        }

        // Делаем JSON из строки.
        json = new JSONObject(response);
        is.close();
        return json;
    }

    private static @Nullable SSLSocketFactory getSslSocketFactory() {
        if (sslSocketFactory != null) {
            return sslSocketFactory;
        } else {
            try {
                sslSocketFactory = new TLSSocketFactory();
                return sslSocketFactory;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private boolean areGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        return resultCode == ConnectionResult.SUCCESS;
    }

    // Короткий Toast.
    public static void shortToast(Object arg)
    {
        Toast.makeText(context, String.valueOf(arg), Toast.LENGTH_SHORT).show();
    }

    // Длинный Toast.
    public static void longToast(Object arg)
    {
        Toast.makeText(context, String.valueOf(arg), Toast.LENGTH_LONG).show();
    }

    // Склонение слова под число.
    public static String getNumEnding(int iNumber, String[] aEndings)
    {
        String sEnding;
        int i;
        iNumber = iNumber % 100;
        if (iNumber >= 11 && iNumber <= 19)
        {
            sEnding = aEndings[2];
        }
        else
        {
            i = iNumber % 10;
            switch (i)
            {
                case (1):
                    sEnding = aEndings[0];
                    break;
                case (2):
                case (3):
                case (4):
                    sEnding = aEndings[1];
                    break;
                default:
                    sEnding = aEndings[2];
                    break;
            }
        }
        return sEnding;
    }

    // Чтение настроек.
    public static void readPrefs()
    {
        // Первый запуск.
        Spark.isFirstLaunch = Spark.preferences.getBoolean("isFirstLaunch", true);

        // Токен ВК.
        Social.onNewTokenVk(Spark.preferences.getString("tokenVk", null));

        // ID лайкнутых постов из json строки.
        Feed.likedPostsIds = new ArrayList<>();
        String stringLikedPosts = Spark.preferences.getString("likedPostsIds", null);

        // Если там не пустота переводим в likedPostsIds.
        if (stringLikedPosts != null)
        {
            Type arrayType = new TypeToken<ArrayList<String>>()
            {
            }.getType();
            Feed.likedPostsIds = gson.fromJson(stringLikedPosts, arrayType);

            // Если лайков стало больше 100, укоротим рамер массива до последних 50 постов.
            if (Feed.likedPostsIds.size() > 100)
                Feed.likedPostsIds.subList(50, Feed.likedPostsIds.size()).clear();
        }

        // ID выбранных категорий из json строки.
        Feed.selectedCategoriesIds = new ArrayList<>();
        String stringSelectedCategories = Spark.preferences.getString("selectedCategoriesIds", null);

        // Если там не пустота переводим в selectedCategoriesIds.
        if (stringSelectedCategories != null)
        {
            Type arrayType = new TypeToken<ArrayList<Integer>>()
            {
            }.getType();
            Feed.selectedCategoriesIds = gson.fromJson(stringSelectedCategories, arrayType);
        }

        // ID постов избранного.
        Feed.favoritePostsIds = database.getFavoritePostsIds();

        // Имя пользователя.
        Social.userName = Spark.preferences.getString("userName",
                Spark.resources.getString(R.string.navigation_unauthorized_name));
    }

    // Сохранение настроек.
    public static void writePrefs()
    {
        // ID лайкнутых постов в json строку.
        String stringLikedPosts = gson.toJson(Feed.likedPostsIds);

        // ID выбранных категорий в json строку.
        String stringSelectedCategories = gson.toJson(Feed.selectedCategoriesIds);

        // Сохраним все.
        Spark.preferences.edit()
                .putBoolean("isFirstLaunch", isFirstLaunch)
                .putString("tokenVk", Social.tokenVk)
                .putString("likedPostsIds", stringLikedPosts)
                .putString("selectedCategoriesIds", stringSelectedCategories)
                .putString("userName", Social.userName)
                .commit();

        BackupAgent.requestBackup(context);
    }
}
