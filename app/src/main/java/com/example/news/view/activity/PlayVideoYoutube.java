package com.example.news.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.news.R;
import com.example.news.condition.Contain;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class PlayVideoYoutube extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private String mId = "";
    private int REQUESTVIDEO = 100;
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_youtube);

        Intent intent = getIntent();

        this.mId = intent.getStringExtra("data");

        youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        getSupportFragmentManager().
                beginTransaction().
                add(R.id.frame_youtube, youTubePlayerFragment).
                commit();

        youTubePlayerFragment.initialize(Contain.KEY_API, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.loadVideo(mId);
        this.youTubePlayer = youTubePlayer;
        youTubePlayer.setFullscreen(true);
        youTubePlayer.setShowFullscreenButton(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTVIDEO) {
            youTubePlayerFragment.initialize(Contain.KEY_API, this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, REQUESTVIDEO);
        } else {
            Toast.makeText(this, "Erro!", Toast.LENGTH_SHORT).show();
        }
    }
}
