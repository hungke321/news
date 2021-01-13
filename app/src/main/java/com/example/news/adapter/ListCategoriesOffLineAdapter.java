package com.example.news.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.model.Categories;
import com.example.news.model.CategoriesOffLine;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class ListCategoriesOffLineAdapter extends RecyclerView.Adapter<ListCategoriesOffLineAdapter
        .ViewHolderOffLine> {

    private Context mContext;

    private List<CategoriesOffLine> mList;

    private OnProgressData mOnProgressData;

    private OnItemClick mOnItemClick;

    public void setOnItemClick(OnItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    public void setOnProgressData(OnProgressData mOnProgressData) {
        this.mOnProgressData = mOnProgressData;
    }

    public ListCategoriesOffLineAdapter(Context mContext, List<CategoriesOffLine> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolderOffLine onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_offline_categories, viewGroup, false);
        return new ViewHolderOffLine(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolderOffLine viewHolderOffLine, final int i) {
        final CategoriesOffLine categories = mList.get(i);
        viewHolderOffLine.mTvNumber.setText("" + (i + 1));
        viewHolderOffLine.mTvTitle.setText(categories.getTitle());
        viewHolderOffLine.mTvNotify.setText(categories.getTime());
        viewHolderOffLine.mBtn.setText(categories.getTime().equals("Chưa tải dữ liệu") ?
                "Tải tin" : "Cập nhật");

        viewHolderOffLine.mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnProgressData != null)
                    mOnProgressData.setOnProgressData(i, categories.getLink(),
                            categories.getTitle());
            }
        });

        viewHolderOffLine.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClick != null) {
                    mOnItemClick.onClickListListener(i);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public interface OnProgressData {
        void setOnProgressData(final int index, final String... data);
    }

    class ViewHolderOffLine extends RecyclerView.ViewHolder {

        private TextView mTvNumber;

        private TextView mTvTitle;

        private TextView mTvNotify;

        private Button mBtn;

        private RelativeLayout mLayout;

        public ViewHolderOffLine(@NonNull View itemView) {
            super(itemView);
            mTvNumber = itemView.findViewById(R.id.item_number);
            mTvTitle = itemView.findViewById(R.id.item_categories_offline);
            mTvNotify = itemView.findViewById(R.id.item_notify_offline);
            mBtn = itemView.findViewById(R.id.btn_categories);
            mLayout = itemView.findViewById(R.id.layout_list_item_categories);
        }
    }
}
