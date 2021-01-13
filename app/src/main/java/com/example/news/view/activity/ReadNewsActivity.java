package com.example.news.view.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.condition.Contain;
import com.example.news.database.DataBase;
import com.example.news.model.News;
import com.example.news.preferences.ManagerPreferences;
import com.example.news.utils.ConnectionUtils;
import com.example.news.view.dialog.DialogExpand;
import com.example.news.view.dialog.DialogLoading;
import com.example.news.view.dialog.DialogShareData;
import com.example.news.view.dialog.OnChangeSeekBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ReadNewsActivity extends AppCompatActivity
        implements View.OnClickListener, OnChangeSeekBar {


    private WebSettings webSettings = null;
    private WebSettings.TextSize TEXTSIZE[] = new WebSettings.TextSize[]{WebSettings.TextSize.SMALLEST,
            WebSettings.TextSize.SMALLER, WebSettings.TextSize.NORMAL, WebSettings.TextSize.LARGER,
            WebSettings.TextSize.LARGEST};

    private ImageView mImgBackHomeRight;

    private ImageView mImgShare;

    private ImageView mImgExpand;

    private TextView mTvTitle;

    private WebView mWvNews;

    private ManagerPreferences mManagerPreferences;

    private final boolean SETTING_NOT_IMAGE = false;

    private final boolean SETTING_AUTO_LOAD_IMAGE = false;

    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_news);


        Intent intent = getIntent();

        news = (News) intent.getSerializableExtra("data");

        mManagerPreferences = new ManagerPreferences(this);

        initView();


        mTvTitle.setText(news.getTitle());
        mWvNews.setWebChromeClient(new WebChromeClient());

        if (ConnectionUtils.isNetworkConnected(this)) {
            MyAsynTask myAsynTask = new MyAsynTask(news.getLink());
            myAsynTask.execute();
        } else {
        }
    }

    private void configWebView() {
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setBlockNetworkImage(mManagerPreferences.getSettingNotImage());
        webSettings.setLoadsImagesAutomatically(mManagerPreferences.getSettingAutoLoadImage());
        webSettings.setGeolocationEnabled(false);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSaveFormData(false);
    }

    /**
     *
     */
    private void initView() {
        mImgBackHomeRight = findViewById(R.id.img_back_right);
        mImgBackHomeRight.setOnClickListener(this);

        mImgShare = findViewById(R.id.img_share_right);
        mImgShare.setOnClickListener(this);

        mImgExpand = findViewById(R.id.img_expand_right);
        mImgExpand.setOnClickListener(this);

        mTvTitle = findViewById(R.id.tv_content_news_small);

        mWvNews = findViewById(R.id.wv_news);

        mWvNews.setWebViewClient(new MyWebViewClient());
        webSettings = mWvNews.getSettings();

        configWebView();

        webSettings.setTextSize(TEXTSIZE[mManagerPreferences.getTextSizeWebView()]);
    }

    @Override
    public void setOnChangeSeekBar(int position) {
        webSettings.setTextSize(TEXTSIZE[position]);
    }


    private class MyAsynTask extends AsyncTask<Void, Void, Document> {

        private String url;

        public MyAsynTask(String url) {
            this.url = url;
        }

        @Override
        protected Document doInBackground(Void... s) {

            Document document = null;

            try {
                document = Jsoup.connect(url).get();
                document.getElementsByClass("section m_header").remove();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return document;
        }

        @Override
        protected void onPostExecute(Document document) {
            super.onPostExecute(document);
            mWvNews.loadDataWithBaseURL(url, document.toString(), "text/html", "utf-8", "");
            mWvNews.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_right:
                super.onBackPressed();
                break;
            case R.id.img_share_right:
                DialogShareData.ShowShare(this, news.getLink());
                break;
            case R.id.img_expand_right:
                DialogExpand dialogExpand = new DialogExpand(this, news);
                dialogExpand.setOnChangeSeekBar(this);
                dialogExpand.show();
                break;

        }
    }

    class MyWebViewClient extends WebViewClient {

        private DialogLoading mDialogLoading;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mDialogLoading = new DialogLoading(ReadNewsActivity.this);
            mDialogLoading.show();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mDialogLoading.dismiss();
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
    }
}
