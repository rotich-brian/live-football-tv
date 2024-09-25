package com.bottom.footballtv.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bottom.footballtv.MainActivity;
import com.bottom.footballtv.R;
import com.bottom.footballtv.adapters.EventAdapter;
import com.bottom.footballtv.adapters.EventCatAdapter;
import com.bottom.footballtv.databinding.FragmentHomeBinding;
import com.bottom.footballtv.models.Room.Eventcat;

import com.bottom.footballtv.services.SelectListener;
import com.bottom.footballtv.ui.EventsFragment;
import com.bottom.footballtv.ui.more.NotificationFragment;
import com.bottom.footballtv.ui.viewmodel.EventViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SelectListener {
    private static final String TAG = "HOME_FRAGMENT_TAG";

    private List<com.bottom.footballtv.models.Room.Eventcat> eventcats;
    private EventCatAdapter eventCatAdapter;
    private List<com.bottom.footballtv.models.Room.Event> events;
    private EventAdapter eventAdapter;

    private EventViewModel eventViewModel;

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

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        setupRecyclerViews();
        observeViewModelData();
        setListeners();

        eventViewModel.listenForEventCategories();

        return binding.getRoot();
    }


    private void setupRecyclerViews() {
        binding.recyclerViewCat.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        eventCatAdapter = new EventCatAdapter(new ArrayList<>(), requireContext(), requireActivity(), "", this);
        binding.recyclerViewCat.setAdapter(eventCatAdapter);

        binding.recyclerViewTop.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        eventAdapter = new EventAdapter(new ArrayList<>(), requireContext(), requireActivity(), this);
        binding.recyclerViewTop.setAdapter(eventAdapter);
    }

    private void observeViewModelData() {

        eventViewModel.getEventCategories().observe(getViewLifecycleOwner(), eventcats -> {
            eventCatAdapter.updateEventCategories(eventcats);
        });

        eventViewModel.getTopEvents().observe(getViewLifecycleOwner(), events -> {
            eventAdapter.updateEvents(events);
        });
    }

    private void setListeners() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            eventViewModel.listenForEventCategories(); // Refresh categories
            binding.swipeRefresh.setRefreshing(false);
        });

        binding.notifyBtn.setOnClickListener(view -> openNotificationFragment());

        if (MainActivity.newUpdate){
            binding.notifyDot.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onCatItemClick(com.bottom.footballtv.models.Room.Eventcat eventcat) {
        openEventsFragment(eventcat);
    }

    @Override
    public void onEventClick(com.bottom.footballtv.models.Room.Event event) {
        Log.d(TAG, "onEventClick: GET CATEGORY" );

        eventViewModel.setUserClickedEvent(true);
        eventViewModel.getEventCategory(event.getCategory()).observe(getViewLifecycleOwner(), new Observer<Eventcat>() {
            @Override
            public void onChanged(Eventcat eventcat) {

                if (eventViewModel.isUserClickedEvent()) {
                    if (eventcat != null) {
                        Log.d(TAG, "onEventClick: Fetched Category: " + eventcat.getCategory());
                        openEventsFragment(eventcat);
                    } else {
                        Log.d(TAG, "onEventClick: No category found for " + event.getCategory());
                    }

                    // Reset the user click flag to avoid fragment opening on future data updates
                    eventViewModel.setUserClickedEvent(false);
                }
            }
        });
    }

    private void openEventsFragment(Eventcat eventcat) {

        // Create a new instance of the destination fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("eventcat", eventcat); // Use putParcelable if using Parcelable

        Fragment destinationFragment = new EventsFragment();
        destinationFragment.setArguments(bundle);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, destinationFragment);
        transaction.addToBackStack("events"); // Optional: to allow going back
        transaction.commit();
    }

    private void openNotificationFragment() {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.homeFragmentContainer, new NotificationFragment());
        transaction.addToBackStack("notification"); // Optional: to allow going back
        transaction.commit();
    }
}