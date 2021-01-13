package com.example.news.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.news.R;
import com.example.news.model.News;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapterSmall extends RecyclerView.Adapter<NewsAdapterSmall.NewsViewHolder> {

    private static final String TAG = NewsAdapterSmall.class.getSimpleName();

    private Context mContext;

    private List<News> mList;

    private OnItemNews mOnItemNews;

    public void setOnItemNews(OnItemNews mOnItemNews) {
        this.mOnItemNews = mOnItemNews;
    }

    public NewsAdapterSmall(Context mContext, List<News> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override

    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout_news_small, viewGroup, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, final int i) {
        try {
            News news = mList.get(i);

            newsViewHolder.mTvTitle.setText(news.getTitle());

            newsViewHolder.mTvContent.setText(news.getContent());

            Picasso.with(mContext).load(news.getImage()).fit().centerCrop().placeholder(R.drawable.ic_loading).into(newsViewHolder.mImgThumnail);

            newsViewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemNews != null) {
                        mOnItemNews.setOnItemNewsSmall(i);
                    }
                }
            });
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImgThumnail;

        private TextView mTvTitle;

        private TextView mTvContent;

        private LinearLayout mLayout;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            mImgThumnail = itemView.findViewById(R.id.img_thumnail_news_small);

            mTvContent = itemView.findViewById(R.id.tv_content_news_small);

            mLayout = itemView.findViewById(R.id.item_layout_news);

            mTvTitle = itemView.findViewById(R.id.item_tv_title_news_small);
        }
    }
}
