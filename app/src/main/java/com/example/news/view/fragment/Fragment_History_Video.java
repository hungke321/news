package com.example.news.view.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

import com.example.news.R;
import com.example.news.adapter.ListVideoAdapter;
import com.example.news.adapter.OnCheckData;
import com.example.news.adapter.OnItemClick;
import com.example.news.condition.Contain;
import com.example.news.database.DataBase;
import com.example.news.model.Video;
import com.example.news.view.activity.PlayVideoYoutube;

import java.util.ArrayList;
import java.util.List;

public class Fragment_History_Video extends Fragment implements OnItemClick, OnCheckData {

    private View mView;

    private SQLiteDatabase mDb;

    private List<Video> mList;

    private ListVideoAdapter mAdapter;

    private LinearLayout mLayoutNotifyData;

    private RecyclerView mRecycleVideo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_history_video, container, false);
        mLayoutNotifyData = mView.findViewById(R.id.layout_notify_not_item_video_history);
        mRecycleVideo = mView.findViewById(R.id.recycle_video_history);

        mList = new ArrayList<>();
        mDb = DataBase.initDatabase(getContext(), Contain.DATABASE_NAME);
        Cursor cursor = mDb.rawQuery("Select * From tbl_history_video", null);
        if (cursor.getCount() == 0) {
            showNotify(mList);
        } else {
            if (cursor.moveToFirst()) {
                do {
                    mList.add(new Video(cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            Boolean.parseBoolean(cursor.getString(7))
                    ));
                    if (mList.size() == cursor.getCount()) {
                        showNotify(mList);
                    }
                } while (cursor.moveToNext());
            }
        }
        mAdapter = new ListVideoAdapter(getContext(), mList, 1);
        mRecycleVideo.setAdapter(mAdapter);
        mRecycleVideo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter.setOnItemClick(this);
        mAdapter.setOnCheckData(this);
        return mView;
    }

    private void showNotify(final List<Video> list) {
        mLayoutNotifyData.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        mRecycleVideo.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClickListListener(int i) {
        sendIdVideo(mList.get(i).getId());
    }

    private void sendIdVideo(final String id) {
        Intent intent = new Intent(getContext(), PlayVideoYoutube.class);
        intent.putExtra("data", id);
        startActivity(intent);
    }

    @Override
    public void onClickGridListener(int i) {

    }

    @Override
    public void setOnCheckData(List<Video> list) {
        showNotify(list);
    }
}
