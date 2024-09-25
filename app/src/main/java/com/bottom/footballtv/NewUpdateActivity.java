package com.bottom.footballtv;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bottom.footballtv.databinding.ActivityNewUpdateBinding;
import com.bottom.footballtv.models.AppData;

public class NewUpdateActivity extends AppCompatActivity {

    private ActivityNewUpdateBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppData appData = (AppData) getIntent().getSerializableExtra("appData");

        if (appData != null) {
            if (appData.isShow_later()){
                binding.laterButton.setVisibility(View.VISIBLE);
            }

            binding.updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateOptions(appData.getVersion_name(), appData.getMessage(),
                            appData.getUpdate_url(), appData.getTelegram_user(), appData.isFrom_telegram(), appData.isShow_later(), appData.isFrom_playStore());
                }
            });
        } else {
            binding.laterButton.setVisibility(View.VISIBLE);
        }

        binding.laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.loadingProgressBar.setVisibility(View.VISIBLE);

                Intent intent = new Intent(NewUpdateActivity.this, MainActivity.class);
                intent.putExtra("appData",appData);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateOptions(String versionName, String message, String updateUrl, String telegramUser, boolean fromTelegram, boolean showLater, boolean fromPlayStore) {

        if (fromPlayStore){

            // Redirect to the app's page in Google Play Store
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } else if (fromTelegram){

            //Redirect to the app's page in TelegramChannel
            goTelegramLink(telegramUser);
        } else {

            // Redirect to the app's page in Website
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl)));
        }
    }

    private void goTelegramLink(String tg) {

        Intent intent;
        try {
            try {
                getPackageManager().getPackageInfo("org.telegram.messenger", 0);
            } catch (PackageManager.NameNotFoundException e) {
                getPackageManager().getPackageInfo("org.thunderdog.challegram", 0);
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain="+tg));
            startActivity(intent);

        } catch (PackageManager.NameNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/"+tg));
            startActivity(intent);
        }
    }
}