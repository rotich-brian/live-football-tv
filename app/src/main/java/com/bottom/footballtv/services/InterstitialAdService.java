package com.bottom.footballtv.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class InterstitialAdService {
    private static final String TAG ="INTERSTITIAL_AD_SERVICE_TAG";
    private static InterstitialAd mInterstitialAd;

    public interface ShowInterstitialAd{
        void onAdDismissed();
        void onAdNull();
    }

    public static void loadInterstitialAdAd(Context context, String interstitialAdId){
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context,interstitialAdId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.d(TAG, "onAdLoaded: AD LOADED SUCCESS");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;

//                        showInterstitialAd.onAdNull();
                    }
                });
    }

    public static void showInterstitialAd(Context context, Activity activity, String interstitialAdId, ShowInterstitialAd showInterstitialAd){
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    mInterstitialAd = null;
                    loadInterstitialAdAd(context, interstitialAdId);

                    showInterstitialAd.onAdDismissed();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    mInterstitialAd = null;
                    loadInterstitialAdAd(context, interstitialAdId);

                    showInterstitialAd.onAdNull();
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });

            mInterstitialAd.show(activity);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
            showInterstitialAd.onAdNull();

            loadInterstitialAdAd(context, interstitialAdId);
        }
    }
}
