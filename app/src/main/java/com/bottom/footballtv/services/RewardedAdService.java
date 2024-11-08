package com.bottom.footballtv.services;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bottom.footballtv.MainActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class RewardedAdService {
    private static final String TAG ="REWARDED_AD_SERVICE_TAG";
    private static RewardedAd mRewardedAd;

    public interface ShowRewardedAd{
        void onAdDismissed();
        void onAdNull();
    }

    public static void loadRewardedAd(Context context, String rewardedAdID){
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, rewardedAdID, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                Log.d(TAG, loadAdError.toString());
                mRewardedAd = null;
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);

                mRewardedAd = rewardedAd;
                Log.d(TAG, "onAdLoaded: AD LOADED SUCCESS");
            }
        });
    }

    public static void showRewardedAd(Context context, Activity activity, String rewardedAdId, ShowRewardedAd showRewardedAd){
        if (mRewardedAd != null) {
            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    mRewardedAd = null;
                    loadRewardedAd(context, rewardedAdId);

                    showRewardedAd.onAdDismissed();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);

                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    mRewardedAd = null;
                    loadRewardedAd(context, rewardedAdId);

                    showRewardedAd.onAdNull();
                }
            });

            mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    Toast.makeText(context, "HD link unlocked.", Toast.LENGTH_SHORT).show();
                    MainActivity.AdUnlock = 1;
                }
            });
        } else {
            Log.d("TAG", "The Rewarded ad wasn't ready yet.");
            showRewardedAd.onAdNull();

            loadRewardedAd(context, rewardedAdId);
        }
    }
}
