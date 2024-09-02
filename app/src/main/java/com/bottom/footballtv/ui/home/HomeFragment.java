package com.bottom.footballtv.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SelectListener {
    private static final String TAG = "HOME_FRAGMENT_TAG";

    private List<Eventcat> eventcats;
    private EventCatAdapter eventCatAdapter;
    private List<Event> events;
    private EventAdapter eventAdapter;

    private FirebaseFirestore db;
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

        db = FirebaseFirestore.getInstance();

        setTopCatData();

        binding.recyclerViewCat.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
        eventCatAdapter = new EventCatAdapter(eventcats, requireContext(), "", this);
        binding.recyclerViewCat.setAdapter(eventCatAdapter);

        binding.recyclerViewTop.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        eventAdapter = new EventAdapter(events,requireContext(),this);
        binding.recyclerViewTop.setAdapter(eventAdapter);

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

    private void setTop1CatData() {

        eventcats = new ArrayList<>();
        events = new ArrayList<>();

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

                            setTopEventData();
                            Log.d(TAG, "Category Events: " + eventcats);
                        }
                    }
                });
    }

    private void setTopEventData() {
        events.clear();

        for (Eventcat eventcat : eventcats) {
            String catId = eventcat.getCatId();
            String category = eventcat.getCategory();

            db.collection("eventcat")
                    .document(catId)
                    .collection(category)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            if (value != null) {
                                // Create a temporary list to hold the events for this category
                                List<Event> tempEvents = new ArrayList<>();

                                for (QueryDocumentSnapshot doc : value) {
                                    Event event = new Event();
                                    event.setEventId(doc.getId());
                                    event.setMatch(doc.getString("match"));
                                    event.setThumbnail(doc.getString("thumbnail"));
                                    event.setLink1(doc.getString("stream"));
                                    event.setOrigin(doc.getString("origin"));
                                    event.setReferrer(doc.getString("referer"));
                                    event.setUser_Agent(doc.getString("user-agent"));

                                    // Check if 'top' field is true
                                    Boolean top = doc.getBoolean("top");
                                    if (top != null && top) {
                                        tempEvents.add(event);
                                    }
                                }

                                // Update the events list and notify adapter
//                                events.clear();
                                events.addAll(tempEvents);
                                eventAdapter.notifyDataSetChanged();
                                Log.d(TAG, "Category Events: " + events);
                            }
                        }
                    });
        }
    }

    private void setTopCatData() {
        eventcats = new ArrayList<>();
        events = new ArrayList<>();

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

                            // Start fetching events for categories
                            fetchEventsForCategories();
                            Log.d(TAG, "Category Events: " + eventcats);
                        }
                    }
                });
    }

    private void fetchEventsForCategories() {
        // Clear events list before adding new data
        events.clear();

        for (Eventcat eventcat : eventcats) {
            String catId = eventcat.getCatId();
            String category = eventcat.getCategory();

            db.collection("eventcat")
                    .document(catId)
                    .collection(category)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            if (value != null) {
                                List<Event> tempEvents = new ArrayList<>();

                                for (QueryDocumentSnapshot doc : value) {
                                    Event event = new Event();
                                    event.setEventId(doc.getId());
                                    event.setMatch(doc.getString("match"));
                                    event.setThumbnail(doc.getString("thumbnail"));
                                    event.setLink1(doc.getString("stream"));
                                    event.setOrigin(doc.getString("origin"));
                                    event.setReferrer(doc.getString("referer"));
                                    event.setUser_Agent(doc.getString("user-agent"));

                                    Boolean top = doc.getBoolean("top");
                                    if (top != null && top) {
                                        tempEvents.add(event);
                                    }
                                }

                                // Update the events list and notify adapter
                                events.addAll(tempEvents);
                                eventAdapter.notifyDataSetChanged();
                                Log.d(TAG, "Category Events: " + events);
                            }
                        }
                    });
        }
    }


    @Override
    public void onCatItemClick(Eventcat eventcat) {
        openEventsFragment(eventcat);
    }

    @Override
    public void onEventClick(Event event) {
        openStream(event);
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

    private void openStream(Event event) {
        Intent intent = new Intent(requireContext(), StreamActivity.class);
        intent.putExtra("event",event);
        requireContext().startActivity(intent);
    }
}