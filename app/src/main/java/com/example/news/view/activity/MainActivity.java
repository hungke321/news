package com.example.news.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.adapter.GridCategoriesAdapter;
import com.example.news.adapter.ItemSelectedCategories;
import com.example.news.adapter.ListCategoriesAdapter;
import com.example.news.adapter.OnItemClick;
import com.example.news.condition.Contain;
import com.example.news.condition.ManageNews;
import com.example.news.database.DataBase;
import com.example.news.model.Categories;
import com.example.news.model.PlayList;
import com.example.news.preferences.ManagerPreferences;
import com.example.news.utils.ConnectionUtils;
import com.example.news.utils.PermissionUtil;
import com.example.news.view.dialog.ConfrimDialog;
import com.example.news.view.dialog.DialogModeRead;
import com.example.news.view.dialog.OnCheckDialogMode;
import com.example.news.view.fragment.Fragment_History_News;
import com.example.news.view.fragment.Fragment_History_Video;
import com.example.news.view.fragment.Fragment_Mark_News;
import com.example.news.view.fragment.Fragment_News;
import com.example.news.view.fragment.Fragment_Personal;
import com.example.news.view.fragment.Fragment_Read_Offline;
import com.example.news.view.fragment.Fragment_Video;
import com.example.news.view.fragment.Fragment_Video_Love;
import com.example.news.view.fragment.OnChangeFragment;
import com.example.news.view.fragment.RefreshListData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        ConfrimDialog.OnAceptListener, View.OnClickListener, OnItemClick,
        ItemSelectedCategories, OnCheckDialogMode, OnChangeFragment {

    private SQLiteDatabase mDb;

    private Toolbar mToolbar;

    private DrawerLayout mDrawer;

    private NavigationView mNaviLeft;

    private BottomNavigationView mBottomNavigationView;

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE
    };

    private Fragment mFragment;

    private ImageView mImgPersonal;

    //    declare view for Navigation Left
    private View mViewNavigationLeft;

    private ImageView mImgCloseLeft;

    private ImageView mImgHomeLeft;

    private Button mBtnListBig;

    private Button mBtnListSmall;

    private RecyclerView mRecycleCategories;

    private ImageView mImgChangeCategries;

    private GridCategoriesAdapter mGridCategoriesAdapter;

    private ListCategoriesAdapter mListCategoriesAdapter;

    private ManageNews mManageNews;

    public static List<Categories> mListCategories;

    private TextView mTvTitleCategories;

    private ItemSelectedCategories mItemSelectedCategories;

    private RefreshListData mRefreshListData;

    private ImageView mImgModeRead;

    private ManagerPreferences mManagerPreferences;

    public static MainActivity INSTANCE;

    public static List<PlayList> mListPlayList = new ArrayList<>();

    public static List<String> mListTitlePlayList = new ArrayList<>();

    private OnChangeNetWork mOnChangeNetWork;

    private static final String GET_PLAYLIST = "https://www.googleapis.com/youtube/v3/playlists?part=" + Contain.PART_PLAYLIST + "&channelId=" + Contain.CHANNEL_ID + "&maxResults=" + Contain.RESULTS_MAX + "" +
            "&key=" + Contain.KEY_API;


    public static synchronized MainActivity getInsstance() {
        if (INSTANCE == null)
            INSTANCE = new MainActivity();
        return INSTANCE;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (!PermissionUtil.hasPermissions(this, PERMISSIONS)) {
                PermissionUtil.requestPermission(this, PERMISSIONS, 0);
            }
        }

        mDb = DataBase.initDatabase(this, Contain.DATABASE_NAME);

        mManageNews = ManageNews.getInstance(this);

        mManagerPreferences = new ManagerPreferences(this);

        mListCategories = mManageNews.getListCategories();

        //init View
        initView();

        mFragment = new Fragment_News();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_content, mFragment).commit();

        setTitleNavigationleft(mManagerPreferences.getChangeCategories());
        setBackgroundBtnSizeList(mManagerPreferences.getChangeSizeList());

        if (ConnectionUtils.isNetworkConnected(this)) {
            new LoadPlayList().execute(GET_PLAYLIST);
        }
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityNetWork, intentFilter);
        super.onStart();
    }

    BroadcastReceiver connectivityNetWork = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectionUtils.isNetworkConnected(MainActivity.this)) {
                if (mOnChangeNetWork != null) {
                    mOnChangeNetWork.OnChangeNetWork(ConnectionUtils.isNetworkConnected(MainActivity.this));
                    new LoadPlayList().execute(GET_PLAYLIST);
                }
            } else {
                Toast.makeText(context, "Mất kết nối mạng! Chuyển sang đọc tin offline!", Toast.LENGTH_SHORT).show();
                mFragment = new Fragment_Read_Offline();
                changeFragment(mFragment);
                setSelectedItem(R.id.item_personal);
            }
        }
    };

    @Override
    protected void onStop() {
        unregisterReceiver(connectivityNetWork);
        super.onStop();
    }

    private void setTitleNavigationleft(final boolean flag) {
        mImgChangeCategries.setImageResource(flag ? R.drawable.ic_format_list_bulleted_black_24dp : R.drawable.ic_view_module_black_24dp);
        mTvTitleCategories.setText(flag ? "Hiển thị dạng lưới" : "Hiển thị dạng danh sách");
    }

    /**
     * @param flag
     */
    private void setBackgroundBtnSizeList(final boolean flag) {
        mBtnListBig.setBackgroundResource(flag ? R.drawable.shape_button_seletec_list : R.drawable.shape_button_unselect);
        mBtnListSmall.setBackgroundResource(flag ? R.drawable.shape_button_unselect : R.drawable.shape_button_seletec_list);
    }

    private void initView() {
//        init view for main_activity
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitleToolBar(getResources().getString(R.string.news));

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainActivity.this, mDrawer, mToolbar, Contain.NUMBER_ZERO, Contain.NUMBER_ZERO);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        mImgPersonal = findViewById(R.id.img_personal_toolbar);
        mImgPersonal.setOnClickListener(this);

        initViewNavigationLeft();

        updateListCategories(mManagerPreferences.getPostionPage());

        if (mManagerPreferences.getChangeCategories()) {
            setAdapterGridCategories();

        } else {
            setAdaterListCategories();
        }
    }

    @Override
    public void setOnCheckDialogMode(boolean value) {
        changeListNews(value);
    }

    private void initViewNavigationLeft() {
        //       Declare for view navigation left
        mNaviLeft = findViewById(R.id.nav_view_left);

        mViewNavigationLeft = findViewById(R.id.layout_navigation_left);

        mImgCloseLeft = mViewNavigationLeft.findViewById(R.id.img_close_left);
        mImgCloseLeft.setOnClickListener(this);

        mImgHomeLeft = mViewNavigationLeft.findViewById(R.id.img_Home_left);
        mImgHomeLeft.setOnClickListener(this);

        mBtnListBig = mViewNavigationLeft.findViewById(R.id.btn_list_big);
        mBtnListBig.setOnClickListener(this);

        mBtnListSmall = mViewNavigationLeft.findViewById(R.id.btn_list_small);
        mBtnListSmall.setOnClickListener(this);

        mImgChangeCategries = mViewNavigationLeft.findViewById(R.id.img_change_categories);
        mImgChangeCategries.setOnClickListener(this);

        mImgModeRead = mViewNavigationLeft.findViewById(R.id.img_mode_read_left);
        mImgModeRead.setOnClickListener(this);

        mTvTitleCategories = mViewNavigationLeft.findViewById(R.id.tv_content_news_small);

        mRecycleCategories = mViewNavigationLeft.findViewById(R.id.recycle_grid_navigation_left);

    }

    private void setTextTitleCategories(final String value) {
        mTvTitleCategories.setText(value);
    }

    private void setAdaterListCategories() {
        mListCategoriesAdapter = new ListCategoriesAdapter(this, mListCategories);
        mRecycleCategories.setAdapter(mListCategoriesAdapter);
        mListCategoriesAdapter.setOnItemClick(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycleCategories.setLayoutManager(layoutManager);
        mListCategoriesAdapter.notifyDataSetChanged();
    }

    private void setAdapterGridCategories() {
        mGridCategoriesAdapter = new GridCategoriesAdapter(this, mListCategories);
        mRecycleCategories.setAdapter(mGridCategoriesAdapter);
        mGridCategoriesAdapter.setOnItemClick(this);
        mRecycleCategories.setLayoutManager(new GridLayoutManager(MainActivity.this,
                Contain.GRID_COLUMN, LinearLayoutManager.VERTICAL, false));
        mGridCategoriesAdapter.notifyDataSetChanged();
    }

    /**
     * @param title
     */
    private void setTitleToolBar(final String title) {
        getSupportActionBar().setTitle(title);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            backStackFragment();
        }
    }


    /**
     * @param fragment
     */
    private void changeFragment(Fragment fragment) {
        if (!fragment.getClass().getName().toString().
                equals(getActiveFragment().getClass().getName().toString())) {
            getSupportFragmentManager().beginTransaction().
                    setAllowOptimization(true).
                    replace(R.id.frame_content, fragment, fragment.getClass().getName()).
                    addToBackStack(fragment.getClass().getName()).commit();
        }
    }

    public Fragment getActiveFragment() {
        Fragment f = null;
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            f = new Fragment_News();
        } else {
            String tag = getSupportFragmentManager().
                    getBackStackEntryAt((getSupportFragmentManager().
                            getBackStackEntryCount()) - 1).getName();
            f = getSupportFragmentManager().findFragmentByTag(tag);
        }
        return f;
    }

    private void backStackFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActiveFragment() instanceof Fragment_News) {
                        setSelectedItem(R.id.item_news);
                        setTitleToolBar(getResources().getString(R.string.news));
                    } else {
                        if (getActiveFragment() instanceof Fragment_Video) {
                            setSelectedItem(R.id.item_video);
                            setTitleToolBar(getResources().getString(R.string.video));
                        } else {
                            if (getActiveFragment() instanceof Fragment_Personal) {
                                setSelectedItem(R.id.item_personal);
                                setTitleToolBar(getResources().getString(R.string.personal));
                            } else {
                                if (getActiveFragment() instanceof Fragment_Mark_News) {
                                    setTitleToolBar("Tin đánh dấu");
                                } else {
                                    if (getActiveFragment() instanceof Fragment_History_News) {
                                        setTitleToolBar("Tin đã đọc");
                                    } else {
                                        if (getActiveFragment() instanceof Fragment_Read_Offline) {
                                            setTitleToolBar("Tin tức Offline");
                                        } else {
                                            if (getActiveFragment() instanceof Fragment_Video_Love) {
                                                setTitleToolBar("Video yêu thích");
                                            } else if (getActiveFragment() instanceof Fragment_History_Video) {
                                                setTitleToolBar("Video đã xem");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }, Contain.TIME_DELAY_BACK_FRAGMENT);
        } else {
            ConfrimDialog confrimDialog = new ConfrimDialog(this);
            confrimDialog.show();
        }
    }

    @SuppressLint("RestrictedApi")
    private void setSelectedItem(int actionId) {
        Menu menu = mBottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem menuItem = menu.getItem(i);
            ((MenuItemImpl) menuItem).setExclusiveCheckable(false);
            menuItem.setChecked(menuItem.getItemId() == actionId);
            ((MenuItemImpl) menuItem).setExclusiveCheckable(true);
        }
    }

    /**
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.item_news:
                mFragment = new Fragment_News();
                setTitleToolBar(getResources().getString(R.string.news));
                changeFragment(mFragment);
                break;
            case R.id.item_video:
                mFragment = new Fragment_Video();
                setTitleToolBar(getResources().getString(R.string.video));
                changeFragment(mFragment);
                break;
            case R.id.item_personal:
                mFragment = new Fragment_Personal();
                setTitleToolBar(getResources().getString(R.string.personal));
                changeFragment(mFragment);
                break;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void setOnAceptListener() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_close_left:
                ConfrimDialog confrimDialog = new ConfrimDialog(this);
                confrimDialog.show();
                break;
            case R.id.img_Home_left:
                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                    mDrawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.img_change_categories:
                mManagerPreferences.setChangeCategories(mManagerPreferences.getChangeCategories() ? false : true);

                mImgChangeCategries.setImageResource(mManagerPreferences.getChangeCategories() ? R.drawable.ic_format_list_bulleted_black_24dp : R.drawable.ic_view_module_black_24dp);

                if (mManagerPreferences.getChangeCategories()) {
                    setAdapterGridCategories();
                    setTextTitleCategories(getResources().getString(R.string.show_grid));
                } else {
                    setAdaterListCategories();
                    setTextTitleCategories(getResources().getString(R.string.show_list));
                }
                break;
            case R.id.btn_list_big:
                changeListNews(true);
                mFragment = new Fragment_News();
                setSelectedItem(R.id.item_news);
                changeFragment(mFragment);
                break;
            case R.id.btn_list_small:
                changeListNews(false);
                mFragment = new Fragment_News();
                setSelectedItem(R.id.item_news);
                changeFragment(mFragment);
                break;

            case R.id.img_personal_toolbar:
                mFragment = new Fragment_Personal();
                changeFragment(mFragment);
                setSelectedItem(R.id.item_personal);
                break;
            case R.id.img_mode_read_left:
                DialogModeRead dialogModeRead = new DialogModeRead(this);
                dialogModeRead.setmOnCheckDialogMode(this);
                dialogModeRead.show();
                break;
        }
    }

    /**
     * @param flag
     */
    private void changeListNews(final boolean flag) {
        setBackgroundBtnSizeList(flag);
        mManagerPreferences.setChangeSizeList(flag);
        mRefreshListData.setOnRefreshListData(flag);
        closeDrawerLeft();
    }

    private void closeDrawerLeft() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * @param i
     */
    @Override
    public void onClickListListener(int i) {
        closeDrawerLeft();
        updateListCategories(i);
        setOnItemSelectedItemCategories(i);

        if (!(mFragment instanceof Fragment_News)) {
            mFragment = new Fragment_News();
            changeFragment(mFragment);
            setSelectedItem(R.id.item_news);
        }
        if (mItemSelectedCategories != null) {
            mItemSelectedCategories.setOnItemSelectedItemCategories(i);
        }

    }

    @Override
    public void onClickGridListener(int i) {

        closeDrawerLeft();

        updateListCategories(i);
        setOnItemSelectedItemCategories(i);
        if (!(mFragment instanceof Fragment_News)) {
            mFragment = new Fragment_News();
            changeFragment(mFragment);
            setSelectedItem(R.id.item_news);
        }
        if (mItemSelectedCategories != null) {
            mItemSelectedCategories.setOnItemSelectedItemCategories(i);
        }

    }

    /**
     * @param postion
     */
    public void updateListCategories(final int postion) {
        for (int i = 0; i < mListCategories.size(); i++) {
            Categories categories = mListCategories.get(i);
            if (postion == i) {
                categories.setSelect(true);
            } else {
                categories.setSelect(false);
            }
            mListCategories.set(i, categories);
        }
    }

    /**
     *
     */
    @Override
    public void setOnItemSelectedItemCategories(final int postion) {
        if (mGridCategoriesAdapter != null) {
            mGridCategoriesAdapter.notifyDataSetChanged();
        }
        if (mListCategoriesAdapter != null) {
            mListCategoriesAdapter.notifyDataSetChanged();
        }
        mRecycleCategories.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecycleCategories.scrollToPosition(postion);
            }
        }, Contain.TIME_DELAY_SCROLL_RECYCLE);
        mRecycleCategories.scrollToPosition(postion);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (mFragment instanceof Fragment_News) {
            if (mItemSelectedCategories == null)
                mItemSelectedCategories = (ItemSelectedCategories) fragment;
            if (mOnChangeNetWork == null)
                mOnChangeNetWork = (OnChangeNetWork) fragment;
            if (mRefreshListData == null)
                mRefreshListData = (RefreshListData) fragment;
        } else {
            if (mFragment instanceof Fragment_Video) {
                mOnChangeNetWork = (OnChangeNetWork) fragment;
            }
        }
        super.onAttachFragment(fragment);
    }

    @Override
    public void setOnChangeFragment(final Fragment fragment) {
        if (fragment instanceof Fragment_Mark_News) {
            setTitleToolBar("Tin đánh dấu");
            changeFragment(fragment);
        } else {
            if (fragment instanceof Fragment_History_News) {
                changeFragment(fragment);
                setTitleToolBar("Tin đã đọc");
            } else {
                if (fragment instanceof Fragment_Read_Offline) {
                    changeFragment(fragment);
                    setTitleToolBar("Tin tức Offline");
                } else {
                    if (fragment instanceof Fragment_Video_Love) {
                        changeFragment(fragment);
                        setTitleToolBar("Video yêu thích");
                    } else {
                        if (fragment instanceof Fragment_History_Video) {
                            changeFragment(fragment);
                            setTitleToolBar("Video đã xem");
                        }
                    }
                }
            }
        }
        this.mFragment = fragment;

    }

    class LoadPlayList extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return readDataFromInternet(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonItemObject = jsonArray.getJSONObject(i);
                    String id = jsonItemObject.getString("id");
                    JSONObject jsonObjectSnippet = jsonItemObject.getJSONObject("snippet");
                    String chanelId = jsonObjectSnippet.getString("channelId");
                    String title = jsonObjectSnippet.getString("title");
                    String description = jsonObjectSnippet.getString("description");
                    JSONObject jsonObjectThumnail = jsonObjectSnippet.getJSONObject("thumbnails");
                    JSONObject jsonObjectThumnailHigh = jsonObjectThumnail.getJSONObject("high");
                    String url = jsonObjectThumnailHigh.getString("url");
                    mListPlayList.add(new PlayList(id, chanelId, title, description, url));
                    mListTitlePlayList.add(title);
                }

            } catch (Exception ex) {

            }
            super.onPostExecute(s);
        }

    }

    /**
     * @param link
     * @return
     */
    private String readDataFromInternet(final String link) {
        StringBuilder stringBuilder = new StringBuilder();

        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        URLConnection urlConnection = null;
        try {
            URL url = new URL(link);
            urlConnection = url.openConnection();

            inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            Log.d("TAG", exception.toString());
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }

}
