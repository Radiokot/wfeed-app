package ua.com.radiokot.feed.util;

import android.app.Activity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ua.com.radiokot.feed.R;
import ua.com.radiokot.feed.adapters.NavigationDrawerAdapter;
import ua.com.radiokot.feed.model.NavigationListItem;

/**
 * Велосипед для NavigationDrawer.
 */
public class NavigationDrawer
{
    private ArrayList<NavigationListItem> items = new ArrayList<>();
    private NavigationDrawerAdapter adapter;

    public DrawerLayout layoutDrawer;
    public Activity displayActivity;
    public TextView textNavUserName;
    public CircleImageView imageNavUserPhoto;

    // Конструктор с подгонкой размеров.
    public NavigationDrawer(Activity displayActivity, FrameLayout layoutNavContent, int width)
    {
        this.displayActivity = displayActivity;

        ListView listNavItems = (ListView) layoutNavContent.findViewById(R.id.listNavItems);
        textNavUserName = (TextView) layoutNavContent.findViewById(R.id.textNavUserName);
        imageNavUserPhoto = (CircleImageView) layoutNavContent.findViewById(R.id.imageNavUserPhoto);

        // Список-меню и адаптер.
        adapter = new NavigationDrawerAdapter(this, items);
        listNavItems.setAdapter(adapter);

        // Установим заданную ширину и высоту хедера (16:9).
        DrawerLayout.LayoutParams layoutNavContentParams =
                (DrawerLayout.LayoutParams) layoutNavContent.getLayoutParams();
        layoutNavContentParams.width = width;
        layoutNavContent.setLayoutParams(layoutNavContentParams);

		/*LinearLayout.LayoutParams layoutHeaderParams =
                (LinearLayout.LayoutParams) layoutNavHeader.getLayoutParams();
		layoutHeaderParams.height = width * 9/16;
		layoutNavHeader.setLayoutParams(layoutHeaderParams);*/

        layoutDrawer = (DrawerLayout) layoutNavContent.getParent();
        layoutDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
    }

    public void addItem(NavigationListItem item)
    {
        for (NavigationListItem i : items)
        {
            if (i.caption == item.caption)
                return;
        }

        adapter.add(item);
    }

    public void show()
    {
        layoutDrawer.openDrawer(GravityCompat.START);
    }

    public void hide()
    {
        layoutDrawer.closeDrawer(GravityCompat.START);
    }

    public boolean isOpen()
    {
        return layoutDrawer.isDrawerOpen(GravityCompat.START);
    }
}
