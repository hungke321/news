package com.example.news.view.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.adapter.NewsAdapterSmall;
import com.example.news.adapter.OnItemClick;
import com.example.news.adapter.OnItemNews;
import com.example.news.condition.Contain;
import com.example.news.database.DataBase;
import com.example.news.model.News;

import java.util.ArrayList;
import java.util.List;

public class NewsOffLine extends AppCompatActivity implements View.OnClickListener,
        OnItemNews {

    private String title = "";

    private SQLiteDatabase mDb;

    private ImageView mImgBack;

    private List<News> mList;

    private RecyclerView mRecycleNews;

    private NewsAdapterSmall mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_off_line);

        mList = new ArrayList<>();
        mDb = DataBase.initDatabase(this, Contain.DATABASE_NAME);

        Intent intent = getIntent();
        if (intent != null) {
            title = intent.getStringExtra("data");
        }
        Cursor cursor = mDb.rawQuery("Select * From tbl_news_offline where categories like ?", new String[]{title});
        if (cursor.moveToFirst()) {
            do {
                mList.add(new News(cursor.getString(0),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        false));
            } while (cursor.moveToNext());
        }

        mImgBack = findViewById(R.id.img_back_right_news_offline);
        mImgBack.setOnClickListener(this);

        mRecycleNews = findViewById(R.id.recycle_news_offline_2);
        mAdapter = new NewsAdapterSmall(this, mList);
        mRecycleNews.setAdapter(mAdapter);
        mAdapter.setOnItemNews(this);
        mRecycleNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_right_news_offline:
                onBackPressed();
                break;
        }
    }

    @Override
    public void setOnItemNewsSmall(int i) {
        Intent intent = new Intent(this, ReadNewsOffLineActivity.class);
        intent.putExtra("data", mList.get(i));
        startActivity(intent);
    }

    @Override
    public void setOnItemNewsLarge(int i) {

    }
}
