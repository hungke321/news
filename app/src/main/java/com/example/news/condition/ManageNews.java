package com.example.news.condition;

import android.content.Context;

import com.example.news.R;
import com.example.news.model.Categories;

import java.util.ArrayList;
import java.util.List;

public class ManageNews {

    private Context mContext;
    private static ManageNews INSTANCE = null;

    private int RESOURCES_CATEGORIES[] = new int[]{R.drawable.ic_dashboard_black_24dp, R.drawable.ic_assignment_black_24dp,
            R.drawable.ic_public_black_24dp, R.drawable.ic_monetization_on_black_24dp, R.drawable.ic_nature_people_black_24dp,
            R.drawable.ic_theaters_black_24dp, R.drawable.ic_pool_black_24dp, R.drawable.ic_justice,
            R.drawable.ic_local_library_black_24dp, R.drawable.ic_add_box_black_24dp, R.drawable.ic_local_cafe_black_24dp,
            R.drawable.ic_local_airport_black_24dp, R.drawable.ic_opacity_black_24dp, R.drawable.ic_settings_system_daydream_black_24dp,
            R.drawable.ic_car, R.drawable.ic_wb_incandescent_black_24dp, R.drawable.ic_textsms_black_24dp,
            R.drawable.ic_sentiment_satisfied_black_24dp};

    private List<Categories> mListCategories;

    private String NAME_CATEGORIES[] = null;
    private String LINK_CATEGORIES[] = null;

    public ManageNews(Context context) {
        this.mContext = context;
        NAME_CATEGORIES = mContext.getResources().getStringArray(R.array.categories);
        LINK_CATEGORIES = mContext.getResources().getStringArray(R.array.rss);
    }

    public static synchronized ManageNews getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ManageNews(context);
        }
        return INSTANCE;
    }

    public List<Categories> getListCategories() {
        mListCategories = new ArrayList<>();
        for (int i = 0; i < RESOURCES_CATEGORIES.length; i++) {
            mListCategories.add(new Categories(RESOURCES_CATEGORIES[i], NAME_CATEGORIES[i], LINK_CATEGORIES[i], i == 0 ? true : false));
        }
        return mListCategories;
    }

    public String[] getArrayNameCategories() {
        return this.NAME_CATEGORIES;
    }

    public String[] getArrayLinkCategories() {
        return this.LINK_CATEGORIES;
    }
}
