package ua.com.radiokot.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pnikosis.materialishprogress.ProgressWheel;

public class AuthActivity extends BaseActivity
{
    WebView webviewAuth;
    ProgressWheel progress;
    String token = null;

    public static Runnable callbackAuthVK;

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_auth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        progress = (ProgressWheel) findViewById(R.id.progressWheel);
        webviewAuth = (WebView) findViewById(R.id.webviewAuth);
        webviewAuth.clearCache(true);
        webviewAuth.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webviewAuth.getSettings().setJavaScriptEnabled(true);

        // чтобы получать уведомления об окончании загрузки страницы
        webviewAuth.setWebViewClient(new VKWebViewClient());

        CookieSyncManager.createInstance(this);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        String url = "https://oauth.vk.com/authorize?client_id=4712158&scope=offline,wall,photos" +
                "&redirect_uri=https://oauth.vk.com/blank.html" +
                "&display=mobile&v=5.126&response_type=token";
        webviewAuth.loadUrl(url);
    }

    class VKWebViewClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);
            progress.setVisibility(View.VISIBLE);

            // распарсим url
            parseUrl(url);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            progress.setVisibility(View.GONE);

            //очистим кэш
            webviewAuth.clearCache(true);
        }
    }

    // Получение токена из url.
    private void parseUrl(String url)
    {
        try
        {
            if (url == null)
                return;
            if (url.startsWith("https://oauth.vk.com/blank.html"))
            {
                Uri parsedUri = Uri.parse(url.replace('#', '?'));
                token = parsedUri.getQueryParameter("access_token");

                finish();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Завершение авторизации перед выходом.
    private void processToken(String token)
    {
        if (token != null)
        {
            Social.onNewTokenVk(token);

            // Раз пользователь новый, почистим список лайков.
            Feed.likedPostsIds.clear();

			/*// Сохраняем все.
            Spark.writePrefs();

			// И читаем сразу.
			Spark.readPrefs();*/

            // Выполним колбек, например, лайк после авторизации.
            if (callbackAuthVK != null)
                callbackAuthVK.run();
        }
    }

    // Запуск с передачей отклика.
    public static void launch(Context context, Runnable callback)
    {
        callbackAuthVK = callback;
        context.startActivity(new Intent(context, AuthActivity.class));
    }

    // Закрытие.
    @Override
    public void finish()
    {
        processToken(token);
        setResult(Activity.RESULT_OK, new Intent().putExtra("token", token));

        super.finish();
    }
}
