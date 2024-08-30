package com.bottom.footballtv.ui.games;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends Fragment implements SelectListener {
    private static final String TAG = "GAMES_FRAGMENT_TAG";

    private List<Eventcat> eventcats;
    private EventCatAdapter eventCatAdapter;

    private FirebaseFirestore db;
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

        db = FirebaseFirestore.getInstance();
        eventcats = new ArrayList<>();
        setCatData();

        binding.recyclerViewCat.setLayoutManager(new GridLayoutManager(requireContext(),2));
        eventCatAdapter = new EventCatAdapter(eventcats, requireContext(), "games", this);
        binding.recyclerViewCat.setAdapter(eventCatAdapter);

        binding.swipeRefresh.setRefreshing(false);
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

        binding.notifyBtn.setOnClickListener(view -> openNotificationFragment());

       return binding.getRoot();
    }

    private void setCatData() {

        db.collection("eventcat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        // Clear the list before adding new data
                        eventcats.clear();

                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                Eventcat eventcat = new Eventcat();
                                eventcat.setCatId(doc.getId());
                                eventcat.setCategory(doc.getString("competition"));
                                eventcat.setThumbnail(doc.getString("thumbnail"));

                                eventcats.add(eventcat);
                            }

                            // Notify the adapter once after processing all documents
                            eventCatAdapter.notifyDataSetChanged();

                            Log.d(TAG, "Category Events: " + eventcats);
                        }
                    }
                });
    }

    @Override
    public void onCatItemClick(Eventcat eventcat) {
        openEventsFragment(eventcat);
    }

    @Override
    public void onEventClick(Event event) {

    }

    private void openEventsFragment(Eventcat eventcat) {
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