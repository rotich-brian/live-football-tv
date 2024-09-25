package com.bottom.footballtv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bottom.footballtv.databinding.ActivitySplashBinding;
import com.bottom.footballtv.models.AppData;
import com.bottom.footballtv.services.AppUpdateService;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 1;
    private static final int SPLASH_DELAY_UPDATE = 1;

    private ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAppUpdate();
            }
        });

        checkAppUpdate();
    }

    private void checkAppUpdate() {
        AppUpdateService appUpdateService = new AppUpdateService(SplashActivity.this);
        appUpdateService.checkUpdate(new AppUpdateService.NewUpdateService() {
            @Override
            public void newUpdate(AppData appData) {
                openUpdateActivity(appData);
            }

            @Override
            public void noNewUpdate() {
                openMainActivity();
            }
        });
    }

    private void openMainActivity() {
        if (isNetworkAvailable()) {
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            binding.retryBtn.setVisibility(View.GONE);
            binding.gifImageView.setVisibility(View.GONE);
            binding.loadingTxt.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                }
            },SPLASH_DELAY);

        } else {
            binding.gifImageView.setVisibility(View.VISIBLE);
            binding.retryBtn.setVisibility(View.VISIBLE);
            binding.loadingProgressBar.setVisibility(View.GONE);
            binding.loadingTxt.setVisibility(View.GONE);
        }
    }

    private void openUpdateActivity(AppData appData) {
        if (isNetworkAvailable()) {
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            binding.retryBtn.setVisibility(View.GONE);
            binding.gifImageView.setVisibility(View.GONE);
            binding.loadingTxt.setVisibility(View.VISIBLE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, NewUpdateActivity.class);
                    intent.putExtra("appData",appData);
                    startActivity(intent);
                    finish();
                }
            },SPLASH_DELAY_UPDATE);

        } else {
            binding.gifImageView.setVisibility(View.VISIBLE);
            binding.retryBtn.setVisibility(View.VISIBLE);
            binding.loadingProgressBar.setVisibility(View.GONE);
            binding.loadingTxt.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}