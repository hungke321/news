package com.example.news.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.news.R;
import com.example.news.model.Categories;

import java.util.List;

public class ListCategoriesAdapter extends RecyclerView.Adapter<ListCategoriesAdapter.ListCategoriesHolder> {

    private Context mContext;
    private List<Categories> mList;

    private OnItemClick mOnItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.mOnItemClick = onItemClick;
    }

    public ListCategoriesAdapter(Context mContext, List<Categories> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ListCategoriesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_categories, viewGroup, false);
        return new ListCategoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCategoriesHolder listCategoriesHolder, final int i) {

        Categories categories = mList.get(i);

        listCategoriesHolder.mTvTitle.setText(categories.getName());

        listCategoriesHolder.mTvNumber.setText((i + 1) + "");

        listCategoriesHolder.mImg.setImageResource(categories.getResoure());

        listCategoriesHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClick != null) {
                    mOnItemClick.onClickListListener(i);
                }
            }
        });
        if (categories.isSelect()) {
            listCategoriesHolder.mLayout.setBackgroundColor(mContext.
                    getResources().getColor(R.color.light_blue));
        } else {
            listCategoriesHolder.mLayout.setBackgroundColor(mContext.
                    getResources().getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ListCategoriesHolder extends RecyclerView.ViewHolder {

        private TextView mTvNumber, mTvTitle;
        private ImageView mImg;

        private RelativeLayout mLayout;

        public ListCategoriesHolder(@NonNull View itemView) {
            super(itemView);
            mTvNumber = itemView.findViewById(R.id.item_tv_list_categories_number);
            mTvTitle = itemView.findViewById(R.id.item_categories_list);
            mImg = itemView.findViewById(R.id.img_list_categories);
            mLayout = itemView.findViewById(R.id.layout_list_item_categories);
        }
    }
}
