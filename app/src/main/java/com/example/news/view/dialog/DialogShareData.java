package com.example.news.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class DialogShareData {
    public static void ShowShare(Context context, String url) {
        Intent shareText = new Intent(Intent.ACTION_SEND);
        shareText.setType("text/plain");
        shareText.putExtra(Intent.EXTRA_TEXT, url);
        context.startActivity(Intent.createChooser(shareText, "Chia sẻ"));
    }
}
