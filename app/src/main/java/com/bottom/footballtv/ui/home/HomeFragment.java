package com.bottom.footballtv.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bottom.footballtv.R;
import com.bottom.footballtv.adapters.EventAdapter;
import com.bottom.footballtv.adapters.EventCatAdapter;
import com.bottom.footballtv.databinding.FragmentHomeBinding;
import com.bottom.footballtv.models.Event;
import com.bottom.footballtv.models.Eventcat;
import com.bottom.footballtv.tools.SelectListener;
import com.bottom.footballtv.ui.EventsFragment;
import com.bottom.footballtv.ui.StreamActivity;
import com.bottom.footballtv.ui.more.NotificationFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeFragment extends Fragment implements SelectListener {

    private List<Eventcat> eventcats;
    private EventCatAdapter eventCatAdapter;
    private List<Event> events;
    private EventAdapter eventAdapter;

    private FragmentManager manager;
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        manager = getParentFragmentManager();

        if (manager.getBackStackEntryCount() != 0) {
            manager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        events = new ArrayList<>();

        eventcats = new ArrayList<>();

        // Generate 5 Eventcat items
        for (int i = 0; i < 5; i++) {
            Eventcat eventcat = new Eventcat();
            eventcat.setCatId(UUID.randomUUID().toString());
            eventcat.setCategory("England - Premier League " + (i + 1));
            eventcat.setThumbnail("https://example.com/thumbnail_cat" + (i + 1) + ".jpg");

            // Add the eventcat to the eventcats list
            eventcats.add(eventcat);
        }

        binding.recyclerViewCat.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        eventCatAdapter = new EventCatAdapter(eventcats, requireContext(), "", this);
        binding.recyclerViewCat.setAdapter(eventCatAdapter);

        // Generate 5 event items
        for (int i = 0; i < 10; i++) {
            Event event = new Event();
            event.setEventId(UUID.randomUUID().toString());
            event.setMatch("Match " + (i + 1) + ": Team A vs Team B");
            event.setCategory("Category " + (i + 1));
            event.setThumbnail("https://example.com/thumbnail" + (i + 1) + ".jpg");
            event.setLink1("https://example.com/link1_" + (i + 1));
            event.setLink2("https://example.com/link2_" + (i + 1));
            event.setLink3("https://example.com/link3_" + (i + 1));
            event.setOrigin("https://example.com");
            event.setReferrer("https://referrer.com");
            event.setUser_Agent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");

            // Add the event to the events list
            events.add(event);
        }

        binding.recyclerViewTop.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        eventAdapter = new EventAdapter(events,requireContext(),this);
        binding.recyclerViewTop.setAdapter(eventAdapter);

        binding.notifyBtn.setOnClickListener(view -> openNotificationFragment());

        return binding.getRoot();
    }

    @Override
    public void onCatItemClick(Eventcat eventcat) {
        openEventsFragment();
    }

    @Override
    public void onEventClick(Event event) {
        openStream();
    }

    private void openEventsFragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, new EventsFragment());
        transaction.addToBackStack("events"); // Optional: to allow going back
        transaction.commit();
    }

    private void openNotificationFragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, new NotificationFragment());
        transaction.addToBackStack("notification"); // Optional: to allow going back
        transaction.commit();
    }

    private void openStream() {
        Intent intent = new Intent(requireContext(), StreamActivity.class);
        requireContext().startActivity(intent);
    }
}