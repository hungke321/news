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

public class GridCategoriesAdapter extends RecyclerView.Adapter<GridCategoriesAdapter.GridCategoriesHolder> {

    private Context mContext;
    private List<Categories> mList;

    private OnItemClick mOnItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.mOnItemClick = onItemClick;
    }

    public GridCategoriesAdapter(Context mContext, List<Categories> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public GridCategoriesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_grid_categories, viewGroup, false);
        return new GridCategoriesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GridCategoriesHolder gridCategoriesHolder, final int i) {

        Categories categories = mList.get(i);

        gridCategoriesHolder.mImg.setImageResource(categories.getResoure());

        gridCategoriesHolder.mTv.setText(categories.getName());

        gridCategoriesHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClick != null) {
                    mOnItemClick.onClickGridListener(i);
                }
            }
        });
        if (categories.isSelect()) {
            gridCategoriesHolder.mLayout.
                    setBackgroundColor(mContext.getResources().
                            getColor(R.color.light_blue));
        } else {
            gridCategoriesHolder.mLayout.
                    setBackgroundColor(mContext.getResources()
                            .getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class GridCategoriesHolder extends RecyclerView.ViewHolder {

        private ImageView mImg;

        private TextView mTv;

        private RelativeLayout mLayout;

        public GridCategoriesHolder(@NonNull View itemView) {
            super(itemView);

            mImg = itemView.findViewById(R.id.img_grid_categories);

            mTv = itemView.findViewById(R.id.tv_grid_categories);

            mLayout = itemView.findViewById(R.id.layout_grid_categories);
        }
    }
}
