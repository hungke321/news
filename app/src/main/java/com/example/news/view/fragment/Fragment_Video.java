package com.example.news.view.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.adapter.ListVideoAdapter;
import com.example.news.adapter.OnCheckData;
import com.example.news.adapter.OnItemClick;
import com.example.news.condition.Contain;
import com.example.news.database.DataBase;
import com.example.news.model.News;
import com.example.news.model.Video;
import com.example.news.utils.ConnectionUtils;
import com.example.news.view.activity.MainActivity;
import com.example.news.view.activity.OnChangeNetWork;
import com.example.news.view.activity.PlayVideoYoutube;
import com.example.news.view.dialog.DialogLoading;
import com.google.android.youtube.player.internal.e;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Video extends Fragment
        implements AdapterView.OnItemSelectedListener,
        OnItemClick, OnChangeNetWork, OnCheckData {

    private static final String TAG = Fragment_Video.class.getSimpleName();

    private View mView;

    private Spinner mSpinnerPlayList;

    private ArrayAdapter<String> mAdapterPlayList;

    private List<Video> mListVideo;

    private RecyclerView mRecycleVideo;

    private ListVideoAdapter mAdapterVideo;

    private LinearLayout mLayoutNotifyNotItem;

    private SQLiteDatabase mDb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mListVideo = new ArrayList<>();
        mDb = DataBase.initDatabase(getContext(), Contain.DATABASE_NAME);
        super.onCreate(savedInstanceState);
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_video, container, false);

        mLayoutNotifyNotItem = mView.findViewById(R.id.layout_notify_not_item);

        mSpinnerPlayList = mView.findViewById(R.id.spinner_playlist);
        mSpinnerPlayList.setOnItemSelectedListener(this);
        mAdapterPlayList = new ArrayAdapter<>(getContext(), R.layout.item_text_spinner, MainActivity.mListTitlePlayList);
        mAdapterPlayList.setDropDownViewResource(R.layout.item_text_drop_down);
        mSpinnerPlayList.setAdapter(mAdapterPlayList);

        mRecycleVideo = mView.findViewById(R.id.recycle_list_video);

        setAdaterVideo();
        if (ConnectionUtils.isNetworkConnected(getContext())) {
            new LoadVideo().execute(getUrl(MainActivity.mListPlayList.get(0).getId(), Contain.KEY_API));
        } else {
            notifyData(mListVideo);
            //invisible spinner when not networking
            mSpinnerPlayList.setVisibility(View.GONE);
        }


        return mView;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.mListVideo.clear();
        if (ConnectionUtils.isNetworkConnected(getContext())) {
            String playListId = MainActivity.mListPlayList.get(position).getId();
            new LoadVideo().execute(getUrl(playListId, Contain.KEY_API));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private String getUrl(String playListId, String keyAPI) {
        return "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" + playListId + "&maxResults=50&key=" + keyAPI;
    }

    @Override
    public void onClickListListener(final int i) {
        addHistoryVideo(mListVideo.get(i));
        sendIdVideo(mListVideo.get(i).getId());
    }

    @Override
    public void onClickGridListener(int i) {

    }

    private void sendIdVideo(final String id) {
        Intent intent = new Intent(getContext(), PlayVideoYoutube.class);
        intent.putExtra("data", id);
        startActivity(intent);
    }

    private boolean addHistoryVideo(final Video video) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", video.getId());
        contentValues.put("publishedAt", video.getPublishedAt());
        contentValues.put("channelId", video.getChannelId());
        contentValues.put("playListId", video.getPlayListId());
        contentValues.put("title", video.getTitle());
        contentValues.put("description", video.getDescription());
        contentValues.put("thumbnail", video.getThumbnail());
        contentValues.put("isLove", video.isLove());
        return mDb.insert("tbl_history_video", null, contentValues) > 0;
    }

    @Override
    public void OnChangeNetWork(boolean isNetWork) {
        if (isNetWork) {
            String playListId = MainActivity.mListPlayList.get(0).getId();
            new LoadVideo().execute(getUrl(playListId, Contain.KEY_API));
        }
    }

    @Override
    public void setOnCheckData(List<Video> list) {
        notifyData(list);
    }

    class LoadVideo extends AsyncTask<String, Void, String> {

        private DialogLoading mDialogLoading;

        @Override
        protected void onPreExecute() {
            mDialogLoading = new DialogLoading(getContext());
            mDialogLoading.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return readDataFromInternet(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    //jsonObject item
                    JSONObject jsonItemObject = jsonArray.getJSONObject(i);
                    //JsonObject snippet
                    JSONObject jsonSnippet = jsonItemObject.getJSONObject("snippet");
                    //item publishedAt
                    String publishedAt = jsonSnippet.getString("publishedAt");
                    //item channelId
                    String channelId = jsonSnippet.getString("channelId");
                    //item title
                    String title = jsonSnippet.getString("title");
                    //item description
                    String description = jsonSnippet.getString("description");
                    //jsonObject Thumnail
                    String thumbnail = "";
                    if (jsonSnippet.toString().contains("thumbnails")) {
                        JSONObject jsonThumnail = jsonSnippet.getJSONObject("thumbnails");
                        if (jsonThumnail != null) {
                            //jsonObject json high
                            JSONObject jsonStandard = jsonThumnail.getJSONObject("standard");
                            //item thunail
                            thumbnail = jsonStandard.getString("url");
                        }
                    } else {
                        thumbnail = "data";
                    }
                    //JsonObject reSourceIdl
                    JSONObject jsonResourceId = jsonSnippet.getJSONObject("resourceId");
                    //item idl
                    String id = jsonResourceId.getString("videoId");

                    String playListId = jsonSnippet.getString("playlistId");
                    mListVideo.add(new Video(id, publishedAt, channelId, playListId, title, description, thumbnail, false));
                }
                mDialogLoading.dismiss();
                notifyData(mListVideo);
                setNotifiDataChangeVideo();
            } catch (Exception ex) {
                Log.d(TAG, ex.toString());
            } finally {
            }
            super.onPostExecute(s);
        }
    }

    private void notifyData(final List<Video> mList) {
        mLayoutNotifyNotItem.setVisibility(mList.isEmpty() ? View.VISIBLE : View.GONE);
        mRecycleVideo.setVisibility(mList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setNotifiDataChangeVideo() {
        mAdapterVideo.notifyDataSetChanged();
    }

    private void setAdaterVideo() {
        mAdapterVideo = new ListVideoAdapter(getContext(), mListVideo, 0);
        mRecycleVideo.setAdapter(mAdapterVideo);
        mAdapterVideo.setOnItemClick(this);
        mAdapterVideo.setOnCheckData(this);
        mRecycleVideo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    /**
     * @param link
     * @return
     */
    private String readDataFromInternet(final String link) {
        StringBuilder stringBuilder = new StringBuilder();

        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        URLConnection urlConnection = null;
        try {
            URL url = new URL(link);
            urlConnection = url.openConnection();

            inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            Log.d("TAG", exception.toString());
        } finally {
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }


}
