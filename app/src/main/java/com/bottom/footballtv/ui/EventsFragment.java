package com.bottom.footballtv.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bottom.footballtv.R;
import com.bottom.footballtv.adapters.EventAdapter;
import com.bottom.footballtv.databinding.FragmentEventsBinding;
import com.bottom.footballtv.models.Event;
import com.bottom.footballtv.models.Eventcat;
import com.bottom.footballtv.tools.SelectListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventsFragment extends Fragment implements SelectListener {

    private List<Event> events;
    private EventAdapter eventAdapter;

    private OnBackPressedCallback callback;
    private FragmentEventsBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(inflater,container,false);

        events = new ArrayList<>();

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

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        eventAdapter = new EventAdapter(events,requireContext(),this);
        binding.recyclerView.setAdapter(eventAdapter);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                manager.popBackStackImmediate();
            }
        });

//        callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                FragmentManager manager = getFragmentManager();
//                manager.popBackStackImmediate();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);

        return binding.getRoot();
    }

    @Override
    public void onCatItemClick(Eventcat eventcat) {

    }

    @Override
    public void onEventClick(Event event) {
        openStream();
    }

    private void openStream() {
        Intent intent = new Intent(requireContext(), StreamActivity.class);
        requireContext().startActivity(intent);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        callback.remove();  // Clean up callback when fragment is destroyed
//}
}