package com.bottom.footballtv.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bottom.footballtv.R;
import com.bottom.footballtv.databinding.ActivityStreamBinding;
import com.bottom.footballtv.models.Event;
import com.bottom.footballtv.tools.CustomHttpDataSourceFactory;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.HttpDataSource;

public class StreamActivity extends AppCompatActivity {
    private static final String TAG = "STREAM_ACTIVITY_TAG";
    private static final long CHECK_INTERVAL_MS = 5000; // Check every 5 seconds

    private PlayerView playerView;
    private ExoPlayer player;
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private ImageView fullScreen;
    boolean isFullScreen = false;

//    private final Handler handler = new Handler();
//    private final Runnable checkPlaybackRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (player != null && !player.isPlaying() && player.getPlaybackState() == Player.STATE_READY) {
//                Log.d(TAG, "Playback is ready but not playing. Restarting playback.");
//                player.play();
//            }
//            handler.postDelayed(this, CHECK_INTERVAL_MS);
//        }
//    };

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

        Event event = (Event) getIntent().getSerializableExtra("event");

        if (event != null) {
            Log.d(TAG, "Eventcat received: " + event.getCategory());
            Log.d(TAG, "Eventcat received: " + event.getReferrer());

            referer = event.getReferrer();
            origin = event.getOrigin();
            userAgent = event.getUser_Agent();

            hlsUrl = event.getLink1() != null ? event.getLink1() : "//";
        } else {
            Log.d(TAG, "No Eventcat data received.");
            hlsUrl = "//";
        }

        playerView = binding.playerView;
        progressBar = binding.progressbar;
        seekBar = findViewById(R.id.seekbar);
        seekBar.setOnTouchListener((v, motionEvent) -> true);

        if (hlsUrl.equals("//") || hlsUrl.isEmpty()) {
            Log.d(TAG, "Invalid stream URL. Displaying error message.");
//            playerView.setVisibility(View.GONE);
            Toast.makeText(this, "Stream is unavailable. Please try again later.", Toast.LENGTH_LONG).show();
        } else {
            initializePlayer();
            playStream(hlsUrl);
//            startPeriodicPlaybackCheck();
        }

        fullScreen = playerView.findViewById(R.id.exo_fullscreen_icon);
        fullScreen.setOnClickListener(v -> {
            if (isFullScreen) {
                exitFullScreen();
            } else {
                enterFullScreen();
            }
        });
    }

    private void initializePlayer() {
        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                        5000,  // Minimum buffer before playback starts (5 seconds)
                        30000, // Maximum buffer to fill (30 seconds)
                        1500,  // Buffer required for playback to start or resume after a pause (1.5 seconds)
                        2000)  // Buffer required after a rebuffering event (2 seconds)
                .setPrioritizeTimeOverSizeThresholds(true) // Prioritize time-based buffering over size-based thresholds
                .build();

        player = new SimpleExoPlayer.Builder(this).build();
//        player = new SimpleExoPlayer.Builder(this).setLoadControl(loadControl).build();
        playerView.setPlayer(player);
    }

    public void playStream(String live_url) {
        if (player == null) {
            initializePlayer();
        }

        HttpDataSource.Factory dataSourceFactory = new CustomHttpDataSourceFactory(userAgent, referer, origin);
        HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(live_url));

        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (player == null) return;

                switch (state) {
                    case Player.STATE_READY:
                        progressBar.setVisibility(View.GONE);
                        playerView.setKeepScreenOn(true);
                        break;
                    case Player.STATE_BUFFERING:
                        progressBar.setVisibility(View.VISIBLE);
                        playerView.setKeepScreenOn(true);
                        break;
                    case Player.STATE_ENDED:
                        player.seekTo(0);
                        player.setPlayWhenReady(false);
                        Toast.makeText(StreamActivity.this, "Playback has ended", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                            if (player != null) {
                                player.setPlayWhenReady(true);
                                player.seekTo(0);
                            }
                        }, 3000);
                        break;
                    case Player.STATE_IDLE:
                        Log.d("ExoPlayer", "Playback state changed: Preparing and playing");
                        player.prepare();
                        player.play();
                        break;
                    default:
                        Log.d(TAG, "Unknown state: " + state);
                        break;
                }
            }
        });

    }

    private void enterFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(params);

        isFullScreen = true;
    }

    private void exitFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
        playerView.setLayoutParams(params);

        isFullScreen = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }
}