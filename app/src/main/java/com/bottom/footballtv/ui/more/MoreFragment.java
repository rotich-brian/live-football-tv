package com.bottom.footballtv.ui.more;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bottom.footballtv.R;
import com.bottom.footballtv.databinding.FragmentMoreBinding;

import firebase.com.protolitewrapper.BuildConfig;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater,container,false);

        binding.joinTelegram.setOnClickListener(view -> goTelegramLink(requireContext(),"futball_liveapp"));
        binding.shareApp.setOnClickListener(view -> shareApp());
        binding.contactUs.setOnClickListener(view -> goTelegramLink(requireContext(),"briank_dev"));
        binding.rateUs.setOnClickListener(view -> rateUs());

        return binding.getRoot();
    }

    private void rateUs() {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, requireContext().getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        intent.addFlags(flags);
        return intent;
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Live Football TV HD");
            String shareMessage= "\nLet me recommend Live Football streaming App HD Free. Download now..\n\n";
//            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareMessage = shareMessage + "https://t.me/futball_liveapp";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "share via:"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    private void goTelegramLink(Context context, String tg) {

        Intent intent;
        try {
            try {
                context.getPackageManager().getPackageInfo("org.telegram.messenger", 0);
            } catch (PackageManager.NameNotFoundException e) {
                context.getPackageManager().getPackageInfo("org.thunderdog.challegram", 0);
            }
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain="+tg));
            context.startActivity(intent);

        } catch (PackageManager.NameNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/"+tg));
            context.startActivity(intent);
        }
    }
}