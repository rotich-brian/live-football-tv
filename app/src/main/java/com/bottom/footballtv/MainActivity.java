package com.bottom.footballtv;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bottom.footballtv.databinding.ActivityMainBinding;
import com.bottom.footballtv.models.AppData;
import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.services.AppUpdateService;
import com.bottom.footballtv.services.InterstitialAdService;
import com.bottom.footballtv.services.RewardedAdService;
import com.bottom.footballtv.ui.games.GamesFragment;
import com.bottom.footballtv.ui.home.HomeFragment;
import com.bottom.footballtv.ui.more.MoreFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MAIN_ACTIVITY_TAG";
    public static int AdClick = 0;
    public static int AdUnlock = 0;
    public static boolean newUpdate = false;
    public static AppData appDataM = new AppData();

    public static Event SelectedItem = new Event();

    private RelativeLayout adContainer;
    private AdView adView;
    private Fragment activeFragment;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Thread(
                () -> {
                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(this, initializationStatus -> {});
                })
                .start();

        InterstitialAdService.loadInterstitialAdAd(MainActivity.this, getResources().getString(R.string.InterstitialAdId));
        RewardedAdService.loadRewardedAd(MainActivity.this, getResources().getString(R.string.RewardedAdId));

        BottomNavigationView navigationView = binding.navView;
        adContainer = binding.AdContainer;

        adView = new AdView(MainActivity.this);
        adView.setAdUnitId(getResources().getString(R.string.BannerAdId));
        adView.setAdSize(getAdSize());

        adContainer.removeAllViews();
        adContainer.addView(adView);

        loadBannerAd();

        Fragment homeFragment = new HomeFragment();
        Fragment gamesFragment = new GamesFragment();
        Fragment moreFragment = new MoreFragment();

        FragmentManager fm = getSupportFragmentManager();

        final Fragment[] active = {homeFragment};
        activeFragment = homeFragment;

        fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, moreFragment, "3").hide(moreFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, gamesFragment, "2").hide(gamesFragment).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment_activity_main, homeFragment, "1").commit();

        navigationView.setOnNavigationItemReselectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            } else if (id == R.id.navigation_games) {
                fm.beginTransaction().hide(active[0]).show(gamesFragment).commit();

            } else if (id == R.id.navigation_more) {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

        navigationView.setOnNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                fm.beginTransaction().hide(active[0]).show(homeFragment).commit();
                active[0] = homeFragment;

                activeFragment = homeFragment;
                return true;
            } else if (id == R.id.navigation_games) {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().hide(active[0]).show(gamesFragment).commit();
                active[0] = gamesFragment;

                activeFragment = gamesFragment;
                return true;
            } else if (id == R.id.navigation_more) {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.beginTransaction().hide(active[0]).show(moreFragment).commit();
                active[0] = moreFragment;

                activeFragment = moreFragment;
                return true;
            }

            return false;
        });


        checkAppUpdate();
    }

    private void checkAppUpdate() {

        AppData appData = (AppData) getIntent().getSerializableExtra("appData");

        if (appData != null) {
            newUpdate = true;
            appDataM = appData;
        }
    }

    private void loadBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                Log.d(TAG, "onAdFailedToLoad: AD FAILED TO LOAD- "+loadAdError.getMessage());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();

                Log.d(TAG, "onAdImpression: AD IMPRESSION");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

                Log.d(TAG, "onAdLoaded: AD LOADED SUCCESS");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
    }

    private AdSize getAdSize() {
        DisplayMetrics outMetrics = getResources().getDisplayMetrics();
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(MainActivity.this, adWidth);
    }

    @Override
    public void onBackPressed() {

        if (activeFragment instanceof MoreFragment) {
            binding.navView.setSelectedItemId(R.id.navigation_home);
        } else {
           super.onBackPressed();
        }
    }


    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}