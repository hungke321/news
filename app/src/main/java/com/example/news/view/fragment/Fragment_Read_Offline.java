package com.example.news.view.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

import com.example.news.R;
import com.example.news.XMLDOMParser.XMLDOMParser;
import com.example.news.adapter.ListCategoriesOffLineAdapter;
import com.example.news.adapter.OnItemClick;
import com.example.news.condition.Contain;
import com.example.news.database.DataBase;
import com.example.news.model.CategoriesOffLine;
import com.example.news.view.activity.NewsOffLine;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fragment_Read_Offline extends Fragment
        implements ListCategoriesOffLineAdapter.OnProgressData,
        OnItemClick {

    private View mView;

    private RecyclerView mRecycleCategories;

    private ListCategoriesOffLineAdapter mAdapter;

    private SQLiteDatabase mDb;

    private List<CategoriesOffLine> mList;
    private Date mCurrentTime = null;
    private SimpleDateFormat sdf = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mList = new ArrayList<>();
        mDb = DataBase.initDatabase(getContext(), Contain.DATABASE_NAME);
        Cursor cursor = mDb.rawQuery("Select * From tbl_categories_offline", null);
        if (cursor.moveToFirst()) {
            do {
                mList.add(new CategoriesOffLine(cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        mCurrentTime = Calendar.getInstance().getTime();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_read_offline, container, false);

        mRecycleCategories = mView.findViewById(R.id.recycle_categories_offline);
        mAdapter = new ListCategoriesOffLineAdapter(getContext(), mList);
        mRecycleCategories.setAdapter(mAdapter);
        mRecycleCategories.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        mAdapter.setOnProgressData(this);
        mAdapter.setOnItemClick(this);
        mAdapter.notifyDataSetChanged();

        return mView;
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

    @Override
    public void onClickListListener(int i) {
        if (!mList.get(i).getTime().equals("Chưa tải dữ liệu")) {
            Intent intent = new Intent(getContext(), NewsOffLine.class);
            intent.putExtra("data", mList.get(i).getTitle());
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Chưa tải dữ liệu...!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClickGridListener(int i) {

    }

    @Override
    public void setOnProgressData(int index, String... data) {
        if (!data[0].isEmpty()) {
            new LoadData().execute(data[0], data[1], String.valueOf(index));
        }
    }

    class LoadData extends AsyncTask<String, Integer, String> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setMessage("Đang tải dữ liệu....");
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            final String rss = readDataFromInternet(strings[0]);
            try {
                XMLDOMParser parser = new XMLDOMParser();
                Document document = parser.getDocument(rss);
                NodeList nodeList = document.getElementsByTagName("item");
                NodeList nodeListdescription = document.getElementsByTagName("description");
                String title = "";
                String link = "";
                String image = "";
                String content = "";

                int total = nodeList.getLength();
                for (int i = 0; i < total; i++) {
                    int value = ((i + 1) * 100) / total;
                    onProgressUpdate(value);
                    Element element = (Element) nodeList.item(i);
                    title = parser.getValue(element, "title");
                    link = resultToHtml(parser.getValue(element, "link"));
                    Pattern p = Pattern.compile("<img[^>]+src=['\"]([^'\"]+)['\"][^>]*>");
                    String description = nodeListdescription.item(i + 1).getTextContent() + "";
                    Matcher matCher = p.matcher(description);
                    if (matCher.find()) {
                        if (i < 4)
                            image = matCher.group(1);
                        else {
                            Pattern pattern = Pattern.compile("<img[^>]+ data-original=['\"]([^'\"]+)['\"][^>]*>");
                            Matcher matcher1 = pattern.matcher(matCher.group());
                            if (matcher1.find()) {
                                image = matcher1.group(1);
                            }
                        }
                    }
                    int lenght = description.split("</br>").length;
                    try {
                        if (lenght > 1) {
                            content = description.split("</br>")[1];
                        }
                    } catch (Exception ex) {
                    }

                    saveData(title, strings[1], content, link, image, "false", sdf.format(mCurrentTime));
                }
            } catch (Exception ex) {
                Log.e("TAG", ex.toString());
            }
            return strings[2] + "," + strings[1];
        }

        /**
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            String data[] = s.split(",");
            mProgressDialog.dismiss();
            CategoriesOffLine categoriesOffLine = new CategoriesOffLine();
            categoriesOffLine.setTitle(data[1]);
            categoriesOffLine.setTime(sdf.format(mCurrentTime));
            mList.set(Integer.parseInt(data[0]), categoriesOffLine);
            mAdapter.notifyDataSetChanged();
            updateData(data[1], sdf.format(mCurrentTime));
            Toast.makeText(getContext(), "Thành công! ", Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }
    }

    /**
     * @param url
     * @return
     */
    private String resultToHtml(final String url) {
        org.jsoup.nodes.Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc.html();
    }

    /**
     * @param title
     * @param time
     */
    private void updateData(final String title, final String time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", time);
        mDb.update("tbl_categories_offline", contentValues, "title=?", new String[]{title});
    }

    /**
     * @param strings
     */
    private void saveData(String... strings) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", strings[0]);
        contentValues.put("categories", strings[1]);
        contentValues.put("content", strings[2]);
        contentValues.put("link", strings[3]);
        contentValues.put("image", strings[4]);
        contentValues.put("isLove", strings[5]);
        contentValues.put("time", strings[6]);
        mDb.insert("tbl_news_offline", null, contentValues);
    }
}
