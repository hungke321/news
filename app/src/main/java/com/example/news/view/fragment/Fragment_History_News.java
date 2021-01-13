package com.example.news.view.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.news.R;
import com.example.news.adapter.NewsAdapterSmall;
import com.example.news.adapter.OnItemNews;
import com.example.news.condition.Contain;
import com.example.news.database.DataBase;
import com.example.news.model.News;
import com.example.news.view.activity.ReadNewsActivity;

import java.util.ArrayList;
import java.util.List;

public class Fragment_History_News extends Fragment implements OnItemNews {

    private View mView;

    private SQLiteDatabase mDb;

    private RecyclerView mRecycleNews;

    private NewsAdapterSmall mAdapter;

    private LinearLayout mLayoutNotifyNotItem;

    private List<News> mList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mDb = DataBase.initDatabase(getContext(), Contain.DATABASE_NAME);
        mList = new ArrayList<>();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_history_news, container, false);
        mRecycleNews = mView.findViewById(R.id.recycle_news_history);
        mLayoutNotifyNotItem = mView.findViewById(R.id.layout_notify_not_item_news_history);

        new LoadData().execute();

        mAdapter = new NewsAdapterSmall(getContext(), mList);
        mRecycleNews.setAdapter(mAdapter);
        mAdapter.setOnItemNews(this);
        mRecycleNews.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mAdapter.notifyDataSetChanged();
        return mView;
    }

    @Override
    public void setOnItemNewsSmall(int i) {
        Intent intent = new Intent(getContext(), ReadNewsActivity.class);
        intent.putExtra("data", mList.get(i));
        startActivity(intent);
    }

    class LoadData extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Cursor cursor = mDb.rawQuery("Select * From tbl_history_news", null);
            if (cursor.moveToFirst()) {
                do {
                    mList.add(new News(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            cursor.getString(3), Boolean.parseBoolean(cursor.getString(4))));
                } while (cursor.moveToNext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyData(mList);
            super.onPostExecute(aVoid);
        }
    }

    /**
     * @param mList
     */
    private void notifyData(final List<News> mList) {
        mLayoutNotifyNotItem.setVisibility(mList.isEmpty() ? View.VISIBLE : View.GONE);
        mRecycleNews.setVisibility(mList.isEmpty() ? View.GONE : View.VISIBLE);
    }


    @Override
    public void setOnItemNewsLarge(int i) {

    }
}
