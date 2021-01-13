package com.example.news.view.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.model.News;

public class ReadNewsOffLineActivity extends AppCompatActivity implements View.OnClickListener {

    private News mNews;

    private ImageView mImgBack;

    private TextView mTvTitle;

    private WebView mWv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news_off_line);
        Intent intent = getIntent();
        if (intent != null) {
            mNews = (News) intent.getSerializableExtra("data");
        }

        mImgBack = findViewById(R.id.img_back_right_read_news_offline);
        mImgBack.setOnClickListener(this);

        mTvTitle = findViewById(R.id.tv_content_read_news_offline);
        mTvTitle.setText(mNews.getContent());

        mWv = findViewById(R.id.wv_read_offline);


        mWv.getSettings().setJavaScriptEnabled(true);

        mWv.loadData(mNews.getLink(), "text/html; charset=UTF-8", null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_right_read_news_offline:
                onBackPressed();
                break;
        }
    }
}
