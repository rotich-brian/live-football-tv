package com.bottom.footballtv.ui.games;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bottom.footballtv.R;
import com.bottom.footballtv.adapters.EventCatAdapter;
import com.bottom.footballtv.databinding.FragmentGamesBinding;
import com.bottom.footballtv.models.Event;
import com.bottom.footballtv.models.Eventcat;
import com.bottom.footballtv.tools.SelectListener;
import com.bottom.footballtv.ui.EventsFragment;
import com.bottom.footballtv.ui.more.NotificationFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GamesFragment extends Fragment implements SelectListener {

    private List<Eventcat> eventcats;
    private EventCatAdapter eventCatAdapter;

    private FragmentManager manager;
    private FragmentGamesBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentGamesBinding.inflate(inflater,container,false);

        manager = getParentFragmentManager();

        if (manager.getBackStackEntryCount() != 0) {
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        eventcats = new ArrayList<>();

        // Generate 5 Eventcat items
        for (int i = 0; i < 15; i++) {
            Eventcat eventcat = new Eventcat();
            eventcat.setCatId(UUID.randomUUID().toString());
            eventcat.setCategory("UEFA Champions League" + (i + 1));
            eventcat.setThumbnail("https://example.com/thumbnail_cat" + (i + 1) + ".jpg");

            eventcats.add(eventcat);
        }

        binding.recyclerViewCat.setLayoutManager(new GridLayoutManager(requireContext(),2));
        eventCatAdapter = new EventCatAdapter(eventcats, requireContext(), "games", this);
        binding.recyclerViewCat.setAdapter(eventCatAdapter);

        binding.notifyBtn.setOnClickListener(view -> openNotificationFragment());

       return binding.getRoot();
    }

    @Override
    public void onCatItemClick(Eventcat eventcat) {
        openEventsFragment();
    }

    @Override
    public void onEventClick(Event event) {

    }

    private void openEventsFragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.gamesFragmentContainer, new EventsFragment());
        transaction.addToBackStack("events"); // Optional: to allow going back
        transaction.commit();
    }

    private void openNotificationFragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.gamesFragmentContainer, new NotificationFragment());
        transaction.addToBackStack("notification"); // Optional: to allow going back
        transaction.commit();
    }
}