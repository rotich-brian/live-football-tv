package com.bottom.footballtv.ui.games;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bottom.footballtv.MainActivity;
import com.bottom.footballtv.R;
import com.bottom.footballtv.adapters.EventCatAdapter;
import com.bottom.footballtv.databinding.FragmentGamesBinding;
import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.models.Room.Eventcat;
import com.bottom.footballtv.services.SelectListener;
import com.bottom.footballtv.ui.EventsFragment;
import com.bottom.footballtv.ui.more.NotificationFragment;
import com.bottom.footballtv.ui.viewmodel.EventViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment implements SelectListener {
    private static final String TAG = "GAMES_FRAGMENT_TAG";

    private List<Eventcat> eventcats;
    private EventCatAdapter eventCatAdapter;

    private EventViewModel eventViewModel;

    private FirebaseFirestore db;
    private FragmentManager manager;
    private FragmentGamesBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentGamesBinding.inflate(inflater,container,false);

        manager = getParentFragmentManager();
        if (manager.getBackStackEntryCount() != 0) {
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        setupRecyclerViews();
        observeViewModelData();
        setListeners();

        eventViewModel.listenForEventCategories();

       return binding.getRoot();
    }

    private void setupRecyclerViews() {

        binding.recyclerViewCat.setLayoutManager(new GridLayoutManager(requireContext(),2));
        eventCatAdapter = new EventCatAdapter(new ArrayList<>(), requireContext(),requireActivity(), "games", this);
        binding.recyclerViewCat.setAdapter(eventCatAdapter);
    }

    private void observeViewModelData() {
        eventViewModel.getEventCategories().observe(getViewLifecycleOwner(), eventcats -> {
            eventCatAdapter.updateEventCategories(eventcats); // Update the adapter
        });
    }

    private void setListeners() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            // Re-listen for real-time updates
            eventViewModel.listenForEventCategories();
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
    public void onEventClick(Event event) {

    }

    private void openEventsFragment(com.bottom.footballtv.models.Room.Eventcat eventcat) {
        // Create a new instance of the destination fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("eventcat", eventcat); // Use putParcelable if using Parcelable

        Fragment destinationFragment = new EventsFragment();
        destinationFragment.setArguments(bundle);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.gamesFragmentContainer, destinationFragment);
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