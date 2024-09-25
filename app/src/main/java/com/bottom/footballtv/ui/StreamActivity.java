package com.bottom.footballtv.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bottom.footballtv.R;
import com.bottom.footballtv.databinding.ActivityStreamBinding;
import com.bottom.footballtv.services.CustomHttpDataSourceFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.HttpDataSource;

public class StreamActivity extends AppCompatActivity {
    private static final String TAG = "STREAM_ACTIVITY_TAG";

    private enum Mode {
        NORMAL,
        FIT,
        ZOOM,
        STRETCH,
        FIXED_WIDTH
    }
    private Mode currentMode = Mode.NORMAL;

    private PlayerView playerView;
    private ExoPlayer player;
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private ImageView fullScreen;
    boolean isFullScreen = false;

    private String userAgent ="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36";
    private String referer ="https://quest4play.xyz/";
    private String origin ="https://quest4play.xyz";
    private String hlsUrl = "//";

    private ActivityStreamBinding binding;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFullScreen", isFullScreen);
        outState.putString("currentMode", currentMode.name());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFullScreen = savedInstanceState.getBoolean("isFullScreen", false);
        String modeString = savedInstanceState.getString("currentMode", Mode.NORMAL.name());
        currentMode = Mode.valueOf(modeString);

        if (isFullScreen) {
            switch (currentMode) {
                case FIT:
                    toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    break;
                case ZOOM:
                    toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    break;
                case STRETCH:
                    toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    break;
                case NORMAL:
                default:
                    exitFullScreen();
                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Restore fullscreen mode according to the current mode
            if (isFullScreen) {
                switch (currentMode) {
                    case FIT:
                        toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                        break;
                    case ZOOM:
                        toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                        break;
                    case STRETCH:
                        toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                        break;
                    default:
                        // If not in a specific mode, default to full screen
                        toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                        break;
                }
            } else {
                // Exit fullscreen if it was not in fullscreen
                exitFullScreen();
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exitFullScreen();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityStreamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        com.bottom.footballtv.models.Room.Event event = (com.bottom.footballtv.models.Room.Event) getIntent().getSerializableExtra("event");

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
            Toast.makeText(this, "Stream is unavailable. Please try again later.", Toast.LENGTH_LONG).show();
        } else {
            initializePlayer();
            playStream(hlsUrl);
        }

        fullScreen = playerView.findViewById(R.id.exo_fullscreen_icon);

        fullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullScreen.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fullScreen.setVisibility(View.VISIBLE);
                        switchMode();
                    }
                },30);
            }
        });
    }

    private void initializePlayer() {

        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
    }

    public void playStream(String live_url) {
        if (player == null) {
            initializePlayer();
        }

        Log.d(TAG, "playStream: Referrer: "+referer);
        Log.d(TAG, "playStream: Origin: "+origin);
        Log.d(TAG, "playStream:User Agent: "+userAgent);
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

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.e("ExoPlayer", "An error occurred: " + error.getMessage());
                // Handle the error, maybe restart the player or notify the user
            }
        });


    }

    private void switchMode() {
        switch (currentMode) {
            case NORMAL:
                currentMode = Mode.FIT;
                toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                break;
            case FIT:
                currentMode = Mode.ZOOM;
                toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                break;
            case ZOOM:
                currentMode = Mode.STRETCH;
                toggleFullScreenMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                break;
            case STRETCH:
                currentMode = Mode.NORMAL;
                exitFullScreen();
                break;
        }
    }

    private void toggleFullScreenMode(int resizeMode) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(params);
        playerView.setResizeMode(resizeMode);

        isFullScreen = true;
    }

    private void exitFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_IMMERSIVE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);

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