package com.example.news.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.preferences.ManagerPreferences;

public class DialogModeRead extends AlertDialog implements View.OnClickListener {

    private Context mContext;

    private AppCompatRadioButton mCheckListLarge;
    private AppCompatRadioButton mCheckListSmall;

    private Button mBtnCancel;
    private Button mBtnConfirm;

    private ManagerPreferences mManagerPreferences;

    private boolean isCheck = false;

    private OnCheckDialogMode mOnCheckDialogMode;

    public void setmOnCheckDialogMode(OnCheckDialogMode mOnCheckDialogMode) {
        this.mOnCheckDialogMode = mOnCheckDialogMode;
    }

    public DialogModeRead(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mode_read);

        mManagerPreferences = new ManagerPreferences(mContext);


        setCancelable(false);

        mCheckListSmall = findViewById(R.id.check_list_small);
        mCheckListSmall.setOnClickListener(this);
        mCheckListLarge = findViewById(R.id.check_list_large);
        mCheckListLarge.setOnClickListener(this);

        mBtnCancel = findViewById(R.id.btn_cancel_dialog_mode);
        mBtnCancel.setOnClickListener(this);

        mBtnConfirm = findViewById(R.id.btn_confirm_dialog_mode);
        mBtnConfirm.setOnClickListener(this);

        if (mManagerPreferences.getChangeSizeList()) {
            mCheckListLarge.setChecked(true);
        } else {
            mCheckListSmall.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_dialog_mode:
                dismiss();
                break;
            case R.id.btn_confirm_dialog_mode:
                mOnCheckDialogMode.setOnCheckDialogMode(isCheck);
                dismiss();
                break;
            case R.id.check_list_large:
                isCheck = true;
                break;
            case R.id.check_list_small:
                isCheck = false;
                break;
        }
    }
}
