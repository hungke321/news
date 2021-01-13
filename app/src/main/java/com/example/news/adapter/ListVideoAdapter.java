package com.example.news.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.condition.Contain;
import com.example.news.database.DataBase;
import com.example.news.model.Video;
import com.example.news.view.dialog.DialogShareData;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListVideoAdapter extends RecyclerView.Adapter<ListVideoAdapter.ViewHolderVideo> {

    private static final String TAG = ListVideoAdapter.class.getSimpleName();

    private Context mContext;

    private List<Video> mList;

    private SQLiteDatabase mDb;

    private int flag;

    private OnCheckData mOncheckData;

    public void setOnCheckData(OnCheckData mOncheckData) {
        this.mOncheckData = mOncheckData;
    }

    public ListVideoAdapter(Context mContext, List<Video> mList, int flag) {
        this.mContext = mContext;
        this.mList = mList;
        mDb = DataBase.initDatabase(mContext, Contain.DATABASE_NAME);
        this.flag = flag;
    }

    private OnItemClick mOnItemClick;

    public void setOnItemClick(OnItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    @NonNull
    @Override
    public ViewHolderVideo onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video, viewGroup, false);
        return new ViewHolderVideo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderVideo viewHolderVideo, final int i) {
        Video video = mList.get(i);
        try {
            if (!video.getThumbnail().equals("data")) {
                Picasso.with(mContext).load(video.getThumbnail()).fit()
                        .centerCrop().
                        placeholder(R.drawable.ic_video_library_black_24dp).
                        into(viewHolderVideo.mImgThumbnail);
            } else {
                viewHolderVideo.mImgThumbnail.setImageResource(R.drawable.ic_video_library_black_24dp);
            }
            viewHolderVideo.mTvTitleVideo.setText(video.getTitle());

            viewHolderVideo.mTvPublishedAt.setText("Phát hành: " + video.getPublishedAt().split("T")[0]);

            viewHolderVideo.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClick.onClickListListener(i);
                }
            });
            viewHolderVideo.mImgExpand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(mContext, v, i);
                }
            });
        } catch (Exception ex) {
            Log.d(TAG, "onBindViewHolder() called with: viewHolderVideo = [" + viewHolderVideo + "], i = [" + i + "]");
        }
    }

    /**
     * @param context
     * @param view
     * @param index
     */
    private void showPopupMenu(Context context, View view, final int index) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.item_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_not_care:
                        switch (flag) {
                            case 0:
                                mList.remove(index);
                                notifyDataSetChanged();
                                break;
                            case 1:
                                if (deleteRow("tbl_history_video", mList.get(index).getId())) {
                                    mList.remove(index);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext, "Thất bại!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2:
                                if (deleteRow("tbl_video", mList.get(index).getId())) {
                                    mList.remove(index);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext, "Thất bại!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        if (mOncheckData != null) {
                            mOncheckData.setOnCheckData(mList);
                        }
                        break;
                    case R.id.item_add_favorite:
                        if (addData(mList.get(index))) {
                            Toast.makeText(mContext, "Thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Video đã tồn tại trong danh sách!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.item_share:
                        String link = "https://www.youtube.com/watch?v=" + mList.get(index).getId();
                        DialogShareData.ShowShare(mContext, link);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private boolean deleteRow(final String table, final String id) {

        return mDb.delete(table, "id like ?", new String[]{id}) > 0;
    }

    private boolean addData(final Video video) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", video.getId());
        contentValues.put("publishedAt", video.getPublishedAt());
        contentValues.put("channelId", video.getChannelId());
        contentValues.put("playListId", video.getPlayListId());
        contentValues.put("title", video.getTitle());
        contentValues.put("description", video.getDescription());
        contentValues.put("thumbnail", video.getThumbnail());
        contentValues.put("isLove", video.isLove());
        return mDb.insert("tbl_video", null, contentValues) > 0;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolderVideo extends RecyclerView.ViewHolder {

        private LinearLayout mLayout;

        private ImageView mImgThumbnail;

        private TextView mTvTitleVideo;

        private TextView mTvPublishedAt;

        private ImageView mImgExpand;

        public ViewHolderVideo(@NonNull View itemView) {
            super(itemView);
            mLayout = itemView.findViewById(R.id.item_layout_list_video);
            mImgThumbnail = itemView.findViewById(R.id.img_item_thumnail_video);
            mTvTitleVideo = itemView.findViewById(R.id.txtv_item_title_video);
            mTvPublishedAt = itemView.findViewById(R.id.txtv_item_publicAt_video);
            mImgExpand = itemView.findViewById(R.id.item_expand_video);
        }
    }
}
