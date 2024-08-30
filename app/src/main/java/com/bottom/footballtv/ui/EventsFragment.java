package com.bottom.footballtv.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bottom.footballtv.adapters.EventAdapter;
import com.bottom.footballtv.databinding.FragmentEventsBinding;
import com.bottom.footballtv.models.Event;
import com.bottom.footballtv.models.Eventcat;
import com.bottom.footballtv.tools.SelectListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment implements SelectListener {
    private static final String TAG = "EVENT_FRAGMENT_TAG";

    private List<Event> events;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;

    private OnBackPressedCallback callback;
    private FragmentEventsBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(inflater,container,false);

        events = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            Eventcat eventcat = (Eventcat) args.getSerializable("eventcat");
            setTopEventData(eventcat);

            binding.catTitle.setText(eventcat.getCategory());

            Log.d(TAG, "onCreateView: eventcat"+ eventcat);
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

    private void setTopEventData(Eventcat eventcat) {

        db.collection("eventcat")
                .document(eventcat.getCatId())
                .collection(eventcat.getCategory())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (value != null) {

//                            events.clear();
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

                                tempEvents.add(event);
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

    @Override
    public void onCatItemClick(Eventcat eventcat) {

    }

    @Override
    public void onEventClick(Event event) {
        openStream(event);
    }

    private void openStream(Event event) {
        Intent intent = new Intent(requireContext(), StreamActivity.class);
        intent.putExtra("event",event);
        requireContext().startActivity(intent);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        callback.remove();  // Clean up callback when fragment is destroyed
//}
}