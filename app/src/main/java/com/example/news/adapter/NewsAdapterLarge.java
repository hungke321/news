package com.example.news.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.news.R;
import com.example.news.model.News;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapterLarge extends RecyclerView.Adapter<NewsAdapterLarge.NewsViewHoldrLarge> {

    private Context mContext;

    private List<News> mList;

    private OnItemNews mOnItemNews;

    public void setOnItemNews(OnItemNews mOnItemNews) {
        this.mOnItemNews = mOnItemNews;
    }

    public NewsAdapterLarge(Context mContext, List<News> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public NewsViewHoldrLarge onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.item_layout_news_large, viewGroup, false);
        return new NewsViewHoldrLarge(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHoldrLarge newsViewHoldrLarge, @SuppressLint("RecyclerView") final int i) {

        News news = mList.get(i);

        Picasso.with(mContext).load(news.getImage()).fit().centerCrop()
                .placeholder(R.drawable.ic_loading).into(newsViewHoldrLarge.mImg);

        newsViewHoldrLarge.mTvTitle.setText(news.getTitle());

        newsViewHoldrLarge.mTvContent.setText(news.getContent());

        newsViewHoldrLarge.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemNews != null) {
                    mOnItemNews.setOnItemNewsLarge(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class NewsViewHoldrLarge extends RecyclerView.ViewHolder {

        private LinearLayout mLayout;

        private ImageView mImg;

        private TextView mTvTitle;

        private TextView mTvContent;

        public NewsViewHoldrLarge(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.item_layout_news_large);

            mImg = itemView.findViewById(R.id.item_img_news_large);

            mTvTitle = itemView.findViewById(R.id.item_tv_title_news_large);

            mTvContent = itemView.findViewById(R.id.item_tv_content_news_large);
        }
    }
}
