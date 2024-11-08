package com.bottom.footballtv.ui.more;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bottom.footballtv.MainActivity;
import com.bottom.footballtv.NewUpdateActivity;
import com.bottom.footballtv.R;
import com.bottom.footballtv.SplashActivity;
import com.bottom.footballtv.databinding.FragmentNotificationBinding;

public class NotificationFragment extends Fragment {

    private FragmentNotificationBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater,container,false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                manager.popBackStackImmediate();
            }
        });

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.swipeRefresh.setRefreshing(false);
                    }
                },2000);
            }
        });

        if (MainActivity.newUpdate){
            binding.newUpdateCard.setVisibility(View.VISIBLE);
        }

        binding.newUpdateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), NewUpdateActivity.class);
                intent.putExtra("appData",MainActivity.appDataM);
                requireActivity().startActivity(intent);
            }
        });

        binding.watchPCCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Redirect to the app's page in Website
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livesports808.top/")));
            }
        });

        return binding.getRoot();
    }
}