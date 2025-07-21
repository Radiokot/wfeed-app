package ua.com.radiokot.feed;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager.widget.ViewPager;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ua.com.radiokot.feed.adapters.PhotoPagerAdapter;
import ua.com.radiokot.feed.model.Post;
import ua.com.radiokot.feed.util.SwipeGestureDetector;
import ua.com.radiokot.feed.util.TouchImageView;

public class PhotoActivity extends BaseActivity
{
	private static final int REQUEST_MEMORY_PERMISSION = 21325;

	private static Post post;

	private ViewPager pager;
	private GestureDetector gestureDetector;
	private PhotoPagerAdapter adapter;
	private TouchImageView[] imageViews;
	private String photoUrlToSave;
	private WindowInsetsControllerCompat windowInsetsController;

	//слушатель изменения страницы pager'a
	private ViewPager.SimpleOnPageChangeListener pageChangeListener =
			new ViewPager.SimpleOnPageChangeListener()
			{
				@Override
				public void onPageSelected(final int position)
				{
					//Log.i("Photo", "Page " + String.valueOf(position));
					if (position >= imageViews.length)
						return;
					if (imageViews[position].getParent() == null)
						adapter.instantiateItem(pager, position);
					Spark.imageLoader.loadImage(post.photos.get(position).getMaxUrl(), Spark.nofadeDisplayOption,
							new SimpleImageLoadingListener()
							{
								@Override
								public void onLoadingComplete(String s, View view, Bitmap bitmap)
								{
									post.photos.get(position).loadedBig = true;
									imageViews[position].setImageBitmap(bitmap);
								}
							});
				}
			};

	@Override
	protected int getLayoutResource()
	{
		return R.layout.activity_photo;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		windowInsetsController = new WindowInsetsControllerCompat(
                getWindow(),
                getWindow().getDecorView()
        );
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
			ActionBar actionBar = getSupportActionBar();
			if (actionBar == null) {
				return;
			}
			if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN) {
				actionBar.hide();
			} else {
				actionBar.show();
			}
        });

		//скроем название и покажем кнопку назад в тулбаре
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

		//получим DecorView и перерисуем интерфейс
		showSystemUI();

		//создадим view'ы для фотографий
		gestureDetector = new GestureDetector(this, new SwipeGestureDetector(this));
		imageViews = new TouchImageView[post.photos.size()];
		for (int i = 0; i < imageViews.length; i++)
		{
			imageViews[i] = new TouchImageView(this);

			//отключим кликающий звук и назначим скрытие интерфейса по клику
			imageViews[i].setSoundEffectsEnabled(false);
			imageViews[i].setOnClickListener(v -> {
                if (isUIVisible())
                    hideSystemUI();
                else
                    showSystemUI();
            });

			//настроим отлавливание свайпа
			imageViews[i].setOnTouchListener((v, event) ->
					((!((TouchImageView) v).isZoomed()) && (gestureDetector.onTouchEvent(event)))
			);
		}

		//настроим слайдер
		pager = (ViewPager) findViewById(R.id.pagerPhotos);
		adapter = new PhotoPagerAdapter(this, post.photos, imageViews);
		pager.setAdapter(adapter);

		//назначим слушатель изменения страницы
		pager.setOnPageChangeListener(pageChangeListener);

		//установим позицию на нужную фотографию
		pager.setCurrentItem(getIntent().getIntExtra("from", 0));
		pageChangeListener.onPageSelected(getIntent().getIntExtra("from", 0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_photo, menu);
		if (post.author.siteId != 1)
			menu.getItem(1).setVisible(false);
		return true;
	}

	@SuppressLint("NonConstantResourceId")
    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.menuPhotoSaveVK:
				Social.savePhotoVk(post.photos.get(pager.getCurrentItem()), this);
				return true;
			case R.id.menuPhotoSave:
				trySavePhoto(post.photos.get(pager.getCurrentItem()).getMaxUrl());
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Попытка сохранить фотографию с учетом разрешения на запись.
	private void trySavePhoto(String photoUrl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q
                && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				Spark.shortToast(Spark.resources.getString(R.string.toast_photo_save_permission));
			} else {
				// Le kostille.
				photoUrlToSave = photoUrl;
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						REQUEST_MEMORY_PERMISSION);
			}
		} else {
			savePhoto(photoUrl);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQUEST_MEMORY_PERMISSION: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					savePhoto(photoUrlToSave);
				} else {
					Spark.shortToast(Spark.resources.getString(R.string.toast_photo_save_permission));
					Spark.shortToast(Spark.resources.getString(R.string.toast_photo_save_error));
				}
				return;
			}
		}
	}

	// Сохранение фотографии в файл из кэша UIL.
	public void savePhoto(String photoUrl)
	{
		File cacheImage = Spark.imageLoader.getDiskCache().get(photoUrl);

		// Если фотографии нет в кэше, загрузим ее и попробуем снова.
		if (cacheImage == null)
		{
			Spark.imageLoader.loadImage(photoUrl,
					new SimpleImageLoadingListener()
					{
						@Override
						public void onLoadingCancelled(String imageUri, View view)
						{
							savePhoto(imageUri);
						}

						@Override
						public void onLoadingComplete(String s, View view, Bitmap bitmap)
						{
							savePhoto(s);
						}
					}
			);
			return;
		}

		String storagePath =
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
						.toString();
		File feedDir = new File(storagePath + "/WFeed");
		feedDir.mkdirs();

		String extension = MimeTypeMap.getFileExtensionFromUrl(photoUrl);
		if (extension == null) {
		    extension = "jpg";
        }
		String outImageFileName = "" + System.currentTimeMillis() + "." + extension;
		File outImage = new File(feedDir, outImageFileName);

		try
		{
            if (outImage.exists()) {
                if (!outImage.canWrite() || !outImage.delete()) {
                    throw new IllegalStateException("Failed to delete an existing file");
                }
            }

			InputStream inputStream = new FileInputStream(cacheImage);
			OutputStream outputStream = new FileOutputStream(outImage);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) > 0)
			{
				outputStream.write(buffer, 0, length);
			}

			inputStream.close();
			outputStream.flush();
			outputStream.close();
			updateMedia(outImage);
			Spark.shortToast(Spark.resources.getString(R.string.toast_photo_saved));
		}
		catch (IOException e)
		{
			Spark.shortToast(Spark.resources.getString(R.string.toast_photo_save_error));
			e.printStackTrace();
		}
	}

	private void updateMedia(File dest)
	{
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri contentUri = Uri.fromFile(dest);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	// Виден ли интерфейс?
	public boolean isUIVisible() {
        ActionBar actionBar = getSupportActionBar();
        return actionBar != null && actionBar.isShowing();
    }

	// Скрытие интерфейса
	public void hideSystemUI()
	{
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
    }

	//Показ интерфейса
	public void showSystemUI()
	{
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
	}

	//Запуск с передачей поста
	public static void launch(BaseActivity context, Post post, int from)
	{
		PhotoActivity.post = post;
		context.startActivity(new Intent(context, PhotoActivity.class).putExtra("from", from));
		context.overridePendingTransition(R.anim.fade_in, 0);
	}

	@Override
	public void onBackPressed()
	{
		// Наверное это "отпускает" слабый интернет.
		for (ImageView imageView : adapter.imageViews)
			Spark.imageLoader.cancelDisplayTask(imageView);

		super.onBackPressed();
		overridePendingTransition(0, R.anim.fade_out);
	}
}
