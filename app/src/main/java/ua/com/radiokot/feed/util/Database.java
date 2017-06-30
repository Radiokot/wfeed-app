package ua.com.radiokot.feed.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import ua.com.radiokot.feed.Feed;
import ua.com.radiokot.feed.Spark;
import ua.com.radiokot.feed.model.Post;

/**
 * SQLite база данных.
 */
public class Database extends SQLiteOpenHelper
{
	public static final String DATABASE_NAME = "feedData";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_FAVORITES = "favorites";
	private static final String KEY_ID = "id";
	private static final String KEY_POST_ID = "postId";
	private static final String KEY_POST_JSON = "postJson";

	public Database(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE " + TABLE_FAVORITES + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_POST_ID + " TEXT,"
				+ KEY_POST_JSON + " TEXT"
				+ ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);

		onCreate(db);
	}

	// Получить посты избранного.
	public ArrayList<Post> getFavoritePosts()
	{
		ArrayList<Post> posts = new ArrayList<>();
		String selectQuery = "SELECT " + KEY_POST_JSON + " FROM " + TABLE_FAVORITES
				+ " ORDER BY ROWID DESC";

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst())
		{
			do
			{
				Post post;
				post = Spark.gson.fromJson(cursor.getString(0), Post.class);
				try
				{
					post.info = post.makeInfoString(post.date, post.author.siteName);
					post.isLiked = Feed.likedPostsIds.contains(post.id);
					post.isFavorite = true;
					posts.add(post);
				}
				catch (Exception e)
				{
					deleteFavoritePost(post.id);
					continue;
				}
			} while (cursor.moveToNext());
		}

		return posts;
	}

	// Получить id постов избранного.
	public ArrayList<String> getFavoritePostsIds()
	{
		ArrayList<String> ids = new ArrayList<>();
		String selectQuery = "SELECT " + KEY_POST_ID + " FROM " + TABLE_FAVORITES;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst())
		{
			do
			{
				ids.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}

		return ids;
	}

	// Добавить пост в избранное.
	public void addFavoritePost(Post post)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_POST_ID, post.id);
		values.put(KEY_POST_JSON, Spark.gson.toJson(post));

		try
		{
			db.insert(TABLE_FAVORITES, null, values);
		}
		catch (SQLiteFullException e)
		{
			// Если таблица каким-то образом переполнится, пересоздадим.
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
			onCreate(db);
			addFavoritePost(post);
		}
		db.close();

		BackupAgent.requestBackup(Spark.context);
	}

	// Убрать пост из избранного.
	public void deleteFavoritePost(String id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FAVORITES, KEY_POST_ID + " = '" + id + "'", null);
		db.close();

        BackupAgent.requestBackup(Spark.context);
	}
}
