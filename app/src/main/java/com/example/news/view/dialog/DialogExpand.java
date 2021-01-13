package com.example.news.view.dialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.condition.Contain;
import com.example.news.database.DataBase;
import com.example.news.model.News;
import com.example.news.preferences.ManagerPreferences;

public class DialogExpand extends AlertDialog implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
    private Context mContext;

    private RelativeLayout mLayoutTurned;

    private ImageView mImgTuned;

    private AppCompatCheckBox mCheckBox;

    private SeekBar mSeekBar;

    private RelativeLayout mLayoutReport;

    private Button mBtnClose;

    private SQLiteDatabase mDb;

    private TextView mTvTextSize;

    private OnChangeSeekBar mOnChangeSeekBar;

    private String TEXTCOLOR[] = new String[]{"#03A9F4", "#2196F3", "#FFC107", "#FF9800", "#FF2323"};

    private String TEXTSIZE[] = new String[]{"Nhỏ nhất", "Nhỏ hơn", "Bình thường", "Lớn", "Lớn nhất"};

    private ManagerPreferences mManagerPreferences;

    private News news;

    public void setOnChangeSeekBar(OnChangeSeekBar mOnChangeSeekBar) {
        this.mOnChangeSeekBar = mOnChangeSeekBar;
    }

    public DialogExpand(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public DialogExpand(@NonNull Context context, final News news) {
        super(context);
        this.mContext = context;
        this.news = news;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_expand_news);

        mDb = DataBase.initDatabase(mContext, Contain.DATABASE_NAME);


        mManagerPreferences = new ManagerPreferences(mContext);
        setCancelable(false);

        mLayoutTurned = findViewById(R.id.layout_dialog_turned);
        mLayoutTurned.setOnClickListener(this);

        mLayoutReport = findViewById(R.id.layout_dialog_report);
        mLayoutReport.setOnClickListener(this);

        mImgTuned = findViewById(R.id.img_dialog_turned_news);

        mSeekBar = findViewById(R.id.seekBar_dialog_text_size);
        mSeekBar.setOnSeekBarChangeListener(this);

        mCheckBox = findViewById(R.id.checkbox_dialog_turned);
        mCheckBox.setOnCheckedChangeListener(this);

        mBtnClose = findViewById(R.id.btn_dialog_close);
        mBtnClose.setOnClickListener(this);

        mTvTextSize = findViewById(R.id.tv_dialog_size_news);
        mTvTextSize.setText(TEXTSIZE[mManagerPreferences.getTextSizeWebView()]);
        mTvTextSize.setTextColor(Color.parseColor(TEXTCOLOR[mManagerPreferences.getTextSizeWebView()]));
        mSeekBar.setProgress(mManagerPreferences.getTextSizeWebView());

        Cursor cursor = mDb.rawQuery("Select * From tbl_news where title like ?", new String[]{news.getTitle()});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            mCheckBox.setChecked(true);
            mImgTuned.setImageResource(R.drawable.ic_turned_in_select_24dp);
        } else {
            mCheckBox.setChecked(false);
            mImgTuned.setImageResource(R.drawable.ic_turned_in_unselect_24dp);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_dialog_report:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "Gứi đến", Contain.EMAIL_ADMIN, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Báo cáo nội dung xấu]");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                mContext.startActivity(Intent.createChooser(emailIntent, "Gửi email..."));
                dismiss();
                break;
            case R.id.layout_dialog_turned:
                if (mCheckBox.isChecked()) {
                    mImgTuned.setImageResource(R.drawable.ic_turned_in_unselect_24dp);
                    mCheckBox.setChecked(false);
                    if (mDb.delete("tbl_news", "title like ?", new String[]{news.getTitle()}) > 0) {
                        mCheckBox.setChecked(false);
                        Toast.makeText(mContext, "Thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        mCheckBox.setChecked(true);
                        Toast.makeText(mContext, "Thất bại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mImgTuned.setImageResource(R.drawable.ic_turned_in_select_24dp);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("title", news.getTitle());
                    contentValues.put("content", news.getContent());
                    contentValues.put("link", news.getLink());
                    contentValues.put("image", news.getImage());
                    contentValues.put("isLove", false);

                    if (mDb.insert("tbl_news", null, contentValues) > 0) {
                        Toast.makeText(mContext, "Thành công!", Toast.LENGTH_SHORT).show();
                        mCheckBox.setChecked(true);
                    } else {
                        Toast.makeText(mContext, "Thất bại!", Toast.LENGTH_SHORT).show();
                        mCheckBox.post(new Runnable() {
                            @Override
                            public void run() {
                                mCheckBox.setChecked(false);
                            }
                        });
                    }
                }
                break;
            case R.id.btn_dialog_close:
                dismiss();
                break;
        }
    }

    /**
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mImgTuned.setImageResource(isChecked ? R.drawable.ic_turned_in_select_24dp
                : R.drawable.ic_turned_in_unselect_24dp);
    }

    /**
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mTvTextSize.setTextColor(Color.parseColor(TEXTCOLOR[progress]));
        mTvTextSize.setText(TEXTSIZE[progress]);
        mOnChangeSeekBar.setOnChangeSeekBar(progress);
        mManagerPreferences.setTextSizeWebView(progress);
    }

    /**
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    /**
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
