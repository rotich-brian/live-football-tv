package com.bottom.footballtv.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bottom.footballtv.R;
import com.bottom.footballtv.databinding.ActivityStreamBinding;
import com.bottom.footballtv.models.Event;
import com.bottom.footballtv.tools.CustomHttpDataSourceFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.HttpDataSource;

public class StreamActivity extends AppCompatActivity {
    private static final String TAG = "STREAM_ACTIVITY_TAG";

    private PlayerView playerView;
    private ExoPlayer player;
    private ProgressBar progressBar;
    private ImageView fullScreen;
    boolean isFullScreen = false;

    private String userAgent ="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36";
    private String referer ="https://quest4play.xyz/";
    private String origin ="https://quest4play.xyz";
    private String hlsUrl = "//";

    private ActivityStreamBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStreamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Event event = (Event) getIntent().getSerializableExtra("event");

        if (event != null) {
            // Use the Eventcat object as needed
            Log.d(TAG, "Eventcat received: " + event.getCategory());
            Log.d(TAG, "Eventcat received: " + event.getReferrer());

            referer = event.getReferrer();
            origin = event.getOrigin();
            userAgent = event.getUser_Agent();

            if (event.getLink1() != null)
                hlsUrl = event.getLink1();

        } else {
            Log.d(TAG, "No Eventcat data received.");
        }

        playerView = binding.playerView;
        progressBar = binding.progressbar;
        fullScreen = playerView.findViewById(R.id.exo_fullscreen_icon);

        playStream(hlsUrl);

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFullScreen){

                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                    if(getSupportActionBar() != null){
                        getSupportActionBar().show();
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);

                    isFullScreen = false;
                }else {

                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    if(getSupportActionBar() != null){
                        getSupportActionBar().hide();
                    }

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    playerView.setLayoutParams(params);

                    isFullScreen = true;
                }
            }
        });
    }

    public void playStream(String live_url){
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        HttpDataSource.Factory dataSourceFactory = new CustomHttpDataSourceFactory(userAgent, referer, origin);

        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(live_url));

        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if(state == Player.STATE_READY){
                    progressBar.setVisibility(View.GONE);
                    player.setPlayWhenReady(true);
                    playerView.setKeepScreenOn(true);
                }else if(state == Player.STATE_BUFFERING){
                    progressBar.setVisibility(View.VISIBLE);
                    playerView.setKeepScreenOn(true);
                }else {
                    player.setPlayWhenReady(true);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        player.seekToDefaultPosition();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onPause() {
        player.setPlayWhenReady(false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }
}