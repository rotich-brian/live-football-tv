package com.bottom.footballtv.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bottom.footballtv.R;
import com.bottom.footballtv.adapters.EventAdapter;
import com.bottom.footballtv.databinding.FragmentEventsBinding;
import com.bottom.footballtv.models.LiveEvent;
import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.services.SelectListener;
import com.bottom.footballtv.ui.viewmodel.EventViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment implements SelectListener {
    private static final String TAG = "EVENT_FRAGMENT_TAG";

    private List<com.bottom.footballtv.models.Room.Event> events;
    private EventAdapter eventAdapter;
    private EventViewModel eventViewModel;

    private FragmentEventsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(inflater,container,false);

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        Bundle args = getArguments();
        if (args != null) {
            com.bottom.footballtv.models.Room.Eventcat eventcat = (com.bottom.footballtv.models.Room.Eventcat) args.getSerializable("eventcat");

            if (eventcat != null){
                binding.catTitle.setText(eventcat.getCategory());

                eventViewModel.init(eventcat.getCategory());

                eventViewModel.listenForEvents(eventcat.getCatId(), eventcat.getCategory());

                try {
                    Picasso.get()
                            .load(eventcat.getThumbnail())
                            .resize(150, 150)
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .into(binding.catThumb);
                } catch (Exception e){
                    binding.catThumb.setImageResource(R.mipmap.ic_launcher);
                }

                Log.d(TAG, "onCreateView: eventcat"+ eventcat.getCatId());
            }
        }

        setupRecyclerViews();
        observeViewModelData();
        setListeners();

        return binding.getRoot();
    }

    private void setupRecyclerViews() {

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        eventAdapter = new EventAdapter(new ArrayList<>(),requireContext(),requireActivity(),this, "event");
        binding.recyclerView.setAdapter(eventAdapter);
    }

    private void observeViewModelData() {

        eventViewModel.getEventsByCategory().observe(getViewLifecycleOwner(), events -> {
            eventAdapter.updateEvents(events);
        });
    }

    private void setListeners() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            eventViewModel.listenForEventCategories(); // Refresh categories
            binding.swipeRefresh.setRefreshing(false);
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                manager.popBackStackImmediate();
            }
        });
    }

    @Override
    public void onCatItemClick(com.bottom.footballtv.models.Room.Eventcat eventcat) {

    }

    @Override
    public void onEventClick(com.bottom.footballtv.models.Room.Event event,int i) {
        LiveEvent liveEvent = new LiveEvent();

        liveEvent.setEventId(event.getEventId());
        if (i==3) {
            liveEvent.setStream(event.getLink3());
            liveEvent.setOrigin(event.getOrigin3());
            liveEvent.setReferrer(event.getReferrer3());
            liveEvent.setUser_Agent(event.getUser_Agent3());
        } else if(i==2) {
            liveEvent.setStream(event.getLink2());
            liveEvent.setOrigin(event.getOrigin2());
            liveEvent.setReferrer(event.getReferrer2());
            liveEvent.setUser_Agent(event.getUser_Agent2());
        } else {
            liveEvent.setStream(event.getLink1());
            liveEvent.setOrigin(event.getOrigin1());
            liveEvent.setReferrer(event.getReferrer1());
            liveEvent.setUser_Agent(event.getUser_Agent1());
        }

        openStream(liveEvent);
    }

    private void openStream(LiveEvent event) {

        Intent intent = new Intent(requireContext(), StreamActivity.class);
        intent.putExtra("event",event);
        requireContext().startActivity(intent);
    }
}