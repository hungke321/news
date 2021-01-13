package com.example.news.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class ManagerPreferences {
    private Context mContext;

    private static final String KEY_CATEGORIES = "key_1";
    private static final String KEY_SIZE_LIST = "key_2";
    private static final String KEY_SIZE_TEXT_WEBVIEW = "key_3";
    private static final String KEY_NOT_IMAGE = "key_4";
    private static final String KEY_AUTO_LOAD_IMAGE = "key_5";
    private static final String KEY_MODE_NIGHT = "key_6";
    private static final String KEY_INDEX_PAGE = "key_7";
    private static final String NAME_PREFERENCES = "News";
    private SharedPreferences mSharedPreferences;

    /**
     * @param context
     */
    public ManagerPreferences(Context context) {
        this.mContext = context;
        init();
    }

    public void init() {
        mSharedPreferences = mContext.getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * @param value
     */
    public void setChangeCategories(boolean value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(KEY_CATEGORIES, value).commit();
    }

    public boolean getChangeCategories() {
        return mSharedPreferences.getBoolean(KEY_CATEGORIES, false);
    }

    /**
     * @param value
     */
    public void setChangeSizeList(boolean value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(KEY_SIZE_LIST, value).commit();
    }

    public boolean getChangeSizeList() {
        return mSharedPreferences.getBoolean(KEY_SIZE_LIST, false);
    }

    /**
     * @param textSize
     */
    public void setTextSizeWebView(final int textSize) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(KEY_SIZE_TEXT_WEBVIEW, textSize).commit();
    }

    public int getTextSizeWebView() {
        return mSharedPreferences.getInt(KEY_SIZE_TEXT_WEBVIEW, 0);
    }

    /**
     * @param value
     */
    public void setSettingNotImage(final boolean value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(KEY_NOT_IMAGE, value).commit();
    }

    public boolean getSettingNotImage() {
        return mSharedPreferences.getBoolean(KEY_NOT_IMAGE, false);
    }

    /**
     * @param value
     */
    public void setSettingAutoLoadImage(final boolean value) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(KEY_AUTO_LOAD_IMAGE, value).commit();
    }

    public boolean getSettingAutoLoadImage() {
        return mSharedPreferences.getBoolean(KEY_AUTO_LOAD_IMAGE, false);
    }


    /**
     * @param index
     */
    public void setPostionPage(final int index) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(KEY_INDEX_PAGE, index).commit();
    }

    public int getPostionPage() {
        return mSharedPreferences.getInt(KEY_INDEX_PAGE, 0);
    }

    public void clearAll() {
        mSharedPreferences.edit().clear().commit();
    }


}
