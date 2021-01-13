package com.example.news.view.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.XMLDOMParser.XMLDOMParser;
import com.example.news.adapter.CategoriesAdapter;
import com.example.news.adapter.ItemSelectedCategories;
import com.example.news.adapter.NewsAdapterLarge;
import com.example.news.adapter.NewsAdapterSmall;
import com.example.news.adapter.OnCheckData;
import com.example.news.adapter.OnItemClick;
import com.example.news.adapter.OnItemNews;
import com.example.news.condition.Contain;
import com.example.news.database.DataBase;
import com.example.news.model.News;
import com.example.news.model.Video;
import com.example.news.preferences.ManagerPreferences;
import com.example.news.utils.ConnectionUtils;
import com.example.news.view.activity.MainActivity;
import com.example.news.view.activity.OnChangeNetWork;
import com.example.news.view.activity.ReadNewsActivity;
import com.example.news.view.dialog.DialogLoading;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fragment_News extends Fragment implements OnItemClick,
        ItemSelectedCategories, RefreshListData, OnItemNews, OnChangeNetWork {

    private static final String TAG = Fragment_News.class.getSimpleName();

    private View mView;

    private RecyclerView mRecycleCategories;

    private CategoriesAdapter mAdapter;

    private Context mContext;

    private ItemSelectedCategories mItemSelectedItemCategories;

    private List<News> mList;

    private RecyclerView mRecycleNews;

    private NewsAdapterSmall mAdapterNewsSmall;

    private static int MEMORIES_INDEX = 0;


    private NewsAdapterLarge mAdapterNewsLarge;


    private SQLiteDatabase mDb;

    private ManagerPreferences mManagerPreferences;

    private LinearLayout mLayoutNotifyNotItem;

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDb = DataBase.initDatabase(getContext(), Contain.DATABASE_NAME);

        mList = new ArrayList<>();

        mContext = getContext();
        mManagerPreferences = new ManagerPreferences(getContext());
    }

    private void setListNewsSmall() {
        mAdapterNewsSmall = new NewsAdapterSmall(getContext(), mList);
        mRecycleNews.setAdapter(mAdapterNewsSmall);
        mAdapterNewsSmall.setOnItemNews(this);
    }

    private void setListNewsLarge() {
        mAdapterNewsLarge = new NewsAdapterLarge(getContext(), mList);
        mRecycleNews.setAdapter(mAdapterNewsLarge);
        mAdapterNewsLarge.setOnItemNews(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_news, container, false);

        mLayoutNotifyNotItem = mView.findViewById(R.id.layout_notify_not_item_news);

        mRecycleCategories = mView.findViewById(R.id.recyle_categories);
        mAdapter = new CategoriesAdapter(getContext(), MainActivity.mListCategories);
        mRecycleCategories.setAdapter(mAdapter);
        mRecycleCategories.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));

        MainActivity.getInsstance().updateListCategories(mManagerPreferences.getPostionPage());

        mAdapter.notifyDataSetChanged();
        mAdapter.setOnItemClick(this);
        setSrollPostion(mManagerPreferences.getPostionPage());

        mRecycleNews = mView.findViewById(R.id.recycle_news);

        if (mManagerPreferences.getChangeSizeList()) {
            setListNewsLarge();
        } else {
            setListNewsSmall();
        }

        mRecycleNews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (ConnectionUtils.isNetworkConnected(getContext())) {
            loadData(mManagerPreferences.getPostionPage());
        } else {
            notifyData(mList);
            mRecycleCategories.setVisibility(View.GONE);
        }
        return mView;
    }

    /**
     * @param mList
     */
    private void notifyData(final List<News> mList) {
        mLayoutNotifyNotItem.setVisibility(mList.isEmpty() ? View.VISIBLE : View.GONE);
        mRecycleNews.setVisibility(mList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClickListListener(int i) {
        try {
            if (MEMORIES_INDEX != i) {
                setSrollPostion((i >= MEMORIES_INDEX && i < MainActivity.mListCategories.size() - 1) ?
                        i + 1 : (i < MEMORIES_INDEX && i > 1) ? i - 1 : i);
                MainActivity.getInsstance().updateListCategories(i);
                notifiDataSetChanged();
                mItemSelectedItemCategories.setOnItemSelectedItemCategories(i);
                if (ConnectionUtils.isNetworkConnected(getContext())) {
                    loadData(i);
                }
                MEMORIES_INDEX = i;
                mManagerPreferences.setPostionPage(i);
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    @Override
    public void onClickGridListener(int i) {
    }

    private void notifiDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        mItemSelectedItemCategories = (ItemSelectedCategories) activity;
        super.onAttach(activity);
    }

    @Override
    public void setOnItemSelectedItemCategories(int postion) {
        notifiDataSetChanged();
        try {
            if (MEMORIES_INDEX != postion) {
                setSrollPostion((postion >= MEMORIES_INDEX && postion < MainActivity.mListCategories.size() - 1) ?
                        postion + 1 : (postion < MEMORIES_INDEX && postion > 1) ? postion - 1 : postion);
                MainActivity.getInsstance().updateListCategories(postion);
                if (ConnectionUtils.isNetworkConnected(getContext())) {
                    loadData(postion);
                }
                MEMORIES_INDEX = postion;
                mManagerPreferences.setPostionPage(postion);
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    private void setSrollPostion(final int postion) {
        mRecycleCategories.scrollToPosition(postion);
    }

    private void notifyDataSetChange() {
        if (mManagerPreferences.getChangeSizeList()) {
            mAdapterNewsLarge.notifyDataSetChanged();
        } else {
            mAdapterNewsSmall.notifyDataSetChanged();
        }

    }


    private void loadData(final int i) {
        mList.clear();
        LoadData loadData = new LoadData(i);
        loadData.execute(MainActivity.mListCategories.get(i).getLink());
    }

    @Override
    public void setOnRefreshListData(boolean flag) {
        if (mManagerPreferences.getChangeSizeList()) {
            setListNewsLarge();
        } else {
            setListNewsSmall();
        }
    }

    @Override
    public void setOnItemNewsSmall(int i) {
        readNews(mList.get(i));
    }

    private void readNews(final News news) {
        if (news != null) {
            addDataBase(news);
            Intent intent = new Intent(getContext(), ReadNewsActivity.class);
            intent.putExtra("data", news);
            startActivity(intent);
        }
    }

    private void addDataBase(final News news) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", news.getTitle());
        contentValues.put("content", news.getContent());
        contentValues.put("link", news.getLink());
        contentValues.put("image", news.getImage());
        contentValues.put("isLove", news.isLove());
        mDb.insert("tbl_history_news", null, contentValues);
    }

    @Override
    public void setOnItemNewsLarge(int i) {
        readNews(mList.get(i));
    }

    @Override
    public void OnChangeNetWork(final boolean isNetWork) {
        if (isNetWork) {
            loadData(0);
        }
    }


    /**
     *
     */
    class LoadData extends AsyncTask<String, Void, String> {

        private int index = 0;

        private DialogLoading mDialogLoading;

        public LoadData(int index) {
            this.index = index;
        }

        @Override
        protected void onPreExecute() {

            mDialogLoading = new DialogLoading(getContext());
            mDialogLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return readDataFromInternet(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            mDialogLoading.dismiss();
            try {
                XMLDOMParser parser = new XMLDOMParser();
                Document document = parser.getDocument(s);
                NodeList nodeList = document.getElementsByTagName("item");
                NodeList nodeListdescription = document.getElementsByTagName("description");
                String title = "";
                String link = "";
                String image = "";
                String content = "";
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    title = parser.getValue(element, "title");
                    link = parser.getValue(element, "link");
                    Pattern p = Pattern.compile("<img[^>]+src=['\"]([^'\"]+)['\"][^>]*>");
                    String description = nodeListdescription.item(i + 1).getTextContent() + "";
                    Matcher matCher = p.matcher(description);
                    if (matCher.find()) {
                        if (i < 4)
                            image = matCher.group(1);
                        else {
                            Pattern pattern = Pattern.compile("<img[^>]+ data-original=['\"]([^'\"]+)['\"][^>]*>");
                            Matcher matcher1 = pattern.matcher(matCher.group());
                            if (matcher1.find()) {
                                image = matcher1.group(1);
                            }
                        }
                    }
                    int lenght = description.split("</br>").length;
                    try {
                        if (lenght > 1) {
                            content = description.split("</br>")[1];
                        }
                    } catch (Exception ex) {
                    }
                    mList.add(new News(title, content, link, image, false));

                }
                notifyData(mList);
                notifyDataSetChange();
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
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
}
