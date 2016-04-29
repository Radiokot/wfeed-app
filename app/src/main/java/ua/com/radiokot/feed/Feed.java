package ua.com.radiokot.feed;

import android.util.Log;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import ua.com.radiokot.feed.adapters.FeedListAdapter;
import ua.com.radiokot.feed.model.Attachment;
import ua.com.radiokot.feed.model.Author;
import ua.com.radiokot.feed.model.Category;
import ua.com.radiokot.feed.model.Post;
import ua.com.radiokot.feed.tasks.FeedUpdateTask;

/**
 * Содержит данные и методы взаимодействия с лентой.
 */
public class Feed
{
	// Хранится здесь, потому что не палится декомпилятором.
	public static final String GOOGLE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmDjJVqlnCVUl9OUogyIVfNt16j8/m5k"+"MMI2c1udwHhrC3TN7hGtqT0ZmiZ3aoxkkJtQ7zrrVXbA5p75SAMPmUALsMpiv3HFQGvLAxnDiK7X/kPMlFk8vqgiW90dZdi1uEDPRf6MCC6smDSU6Nas"+"JLxxdhIW2TcMyr9MqxWtDa4TPIiKgWVpIyDwEkoN4Z7VCJ8cvn43wqA6MkSn77+JlnHg46zIPqzMrbDOKCtGjaBxr1DIKS4sWk/pVlTnBRz4GFknoXN7q"+"ROp4kqOMVgCBwGDCan3w1DGEz6RZg5A0PfJKc3QDOYGV2Es/jObsXEkAWh32ZBbpvAnZlZnoNKYhSQIDAQAB";
	public static final String FEED_API = "https://feed.radiokot.com.ua/cgi-bin/";
	//"https://feed-radiokot-com-ua-ng8whhe3an0d.runscope.net/cgi-bin/";

	// Данные-объекты.
	// Загрузчик ленты.
	public static FeedUpdateTask feedUpdater;

	// Данные - массивы.
	// Авторы по ключу ID.
	public static HashMap<Integer, Author> authors = new HashMap<>();
	// Посты списком.
	public static ArrayList<Post> posts = new ArrayList<>();
	// Списки вложений по ключу ID.
	public static HashMap<String, ArrayList<Attachment>> atts = new HashMap<>();
	// ID лайкнутых постов.
	public static ArrayList<String> likedPostsIds = new ArrayList<>();
	// ID выбранных категорий.
	public static ArrayList<Integer> selectedCategoriesIds = new ArrayList<>();
	// Избранные посты по ключу ID.
	public static ArrayList<String> favoritePostsIds = new ArrayList<>();

	// Данные - переменные.
	// Дата последнего поста.
	static int lastPostDate = 0;
	// Выполняется ли обновление ленты.
	public static boolean nowUpdating = false;

	// Методы.
	// Запуск задачи обновления ленты.
	public static void updateAsync(FeedListAdapter adapter, boolean clear)
	{
		if (!nowUpdating)
		{
			feedUpdater = new FeedUpdateTask(adapter, clear);
			feedUpdater.execute();
		}
	}

	// Обновление ленты.
	public static void updateAll() throws IOException, JSONException
	{
		// Получим строку id категорий.
		StringBuilder categoriesStringBuilder = new StringBuilder();
		if (selectedCategoriesIds.size() != 0)
		{
			for (int id : selectedCategoriesIds)
			{
				categoriesStringBuilder.append(id);
				categoriesStringBuilder.append(",");
			}
			categoriesStringBuilder.setLength(categoriesStringBuilder.length() - 1);
		}
		else
			categoriesStringBuilder.append(0);

		String url = String.format(FEED_API + "get?count=%s&categories=" +
				categoriesStringBuilder.toString() + "&from_date=%s&version=%s",
				50, lastPostDate, Spark.packageInfo.versionCode);

		JSONObject json = Spark.getJSON(url);

		//для проверки без интернета
		/*JSONObject json = new JSONObject();
		try {
            json = new JSONObject(Spark.resources.getString(R.string.test_feed));
        } catch (Exception e) {}*/

		if (json == null)
			return;

		updateAuthors(json.optJSONArray("authors"));
		updateAdds(json.optJSONArray("atts"));
		updatePosts(json.optJSONArray("posts"));

		//запомним дату последнего поста
		lastPostDate = posts.get(posts.size() - 1).date;

		Log.i("Feed", "Update completed: " + json.optJSONObject("info").toString() +
				"\nlastPostDate: " + String.valueOf(lastPostDate));
	}

	// Очистка ленты.
	public static void clearFeed()
	{
		posts.clear();
		authors.clear();
		atts.clear();
		lastPostDate = 0;
	}

	// Обновление авторов в списке.
	private static void updateAuthors(JSONArray items)
	{
		int length = items.length();

		//для каждого автора создадим объект и добавим в список, если его там еще нет
		for (int i = 0; i < length; i++)
		{
			JSONObject item = items.optJSONObject(i);

			int id = item.optInt("id");

			//проверим наличие автора в списке по id
			if (!authors.containsKey(id))
				authors.put(id, new Author(item));
		}

		Log.i("Feed", "Authors updated");
	}

	// Добавление постов в ленту.
	private static void updatePosts(JSONArray items)
	{
		int length = items.length();
        posts.ensureCapacity(posts.size() + items.length());

		//для каждого поста создадим объект и добавим в ленту
		for (int i = 0; i < length; i++)
		{
			JSONObject item = items.optJSONObject(i);

			posts.add(new Post(item));
		}

		Log.i("Feed", "Posts updated");
	}

	// Добавление вложений в список.
	private static void updateAdds(JSONArray items)
	{
		int length = items.length();

		//для каждой группы вложений создадим список в atts
		for (int i = 0; i < length; i++)
		{
			JSONObject item = items.optJSONObject(i);

			String id = item.optString("id");

			//если этой группы еще нет, добавляем пустой список
			if (!atts.containsKey(id))
				atts.put(id, new ArrayList<Attachment>());

			//теперь мы можем добавить в группу новое вложение, пускай даже единственное
			atts.get(id).add(Attachment.create(item));
		}

		Log.i("Feed", "Attachments updated");
	}

	// Получение категорий.
	public static ArrayList<Category> getCategories() throws IOException, JSONException
	{
		JSONArray json = Spark.getJSON(FEED_API + "categories")
				.getJSONArray("categories");

		ArrayList<Category> response = new ArrayList<>();
		String locale = Spark.resources.getString(R.string.locale);
		for (int i = 0; i < json.length(); i++)
			response.add(new Category(json.getJSONObject(i), locale));

		return response;
	}


	// Добавление поста в избранное.
	public static void addPostToFavorite(Post post, ImageButton addButton)
	{
		post.isFavorite = true;
		favoritePostsIds.add(post.id);
		Spark.database.addFavoritePost(post);
		addButton.setImageDrawable(Spark.resources.getDrawable(R.drawable.ic_favorite_done));
	}

	// Удаление поста из избранного.
	public static void removePostFromFavorite(Post post, ImageButton addButton)
	{
		post.isFavorite = false;
		favoritePostsIds.remove(post.id);
		Spark.database.deleteFavoritePost(post.id);
		addButton.setImageDrawable(Spark.resources.getDrawable(R.drawable.ic_favorite));
	}
}
