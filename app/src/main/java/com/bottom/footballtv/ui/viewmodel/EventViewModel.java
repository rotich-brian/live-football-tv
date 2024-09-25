package com.bottom.footballtv.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Room;

import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.models.Room.Eventcat;
import com.bottom.footballtv.services.Room.EventsDao;
import com.bottom.footballtv.services.Room.LocalDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventViewModel extends AndroidViewModel {
    private static final String TAG = "EVENT_VIEW_MODEL_TAG";
    private final FirebaseFirestore dbF = FirebaseFirestore.getInstance();
    private LocalDatabase dbL;
    private EventsDao eventDao;
    private LiveData<List<Eventcat>> _catEvents;
    private LiveData<List<Event>> _topEvents;
    private LiveData<List<Event>> _eventsByCategory;
    private LiveData<Eventcat> _eventcat;

    private ListenerRegistration eventCategoriesListener;
    private ListenerRegistration eventsListener;

    private boolean userClickedEvent = false; // Flag to track user clicks

    public void setUserClickedEvent(boolean clicked) {
        this.userClickedEvent = clicked;
    }

    public boolean isUserClickedEvent() {
        return userClickedEvent;
    }

    public EventViewModel(@NonNull Application application) {
        super(application);
        dbL = LocalDatabase.getInstance(application);
        eventDao = dbL.eventDao();

        _catEvents = eventDao.getAllCategories();
        _topEvents = eventDao.getTopEvents();
    }

    public void init(String category){
        _eventsByCategory = eventDao.getEventsByCategory(category);
    }

    public LiveData<List<Eventcat>> getEventCategories() {
        return _catEvents;
    }

    public LiveData<Eventcat> getEventCategory(String catId) {
        return eventDao.getCategoryById(catId);
    }

    public Eventcat getEventCategoryByCatId(String catId) {
        return eventDao.getCategoryById(catId).getValue();
    }

    public LiveData<List<Event>> getTopEvents() {
        return _topEvents;
    }

    public LiveData<List<Event>> getEventsByCategory() {
        return _eventsByCategory;
    }

    // Fetch event categories with a real-time listener
    public void listenForEventCategories() {
        eventCategoriesListener = dbF.collection("eventcat")
                .orderBy("priority")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Firestore error: ", error);
                        return;
                    }

                    if (value != null) {
                        List<Eventcat> categories = new ArrayList<>();
                        ExecutorService executor = Executors.newSingleThreadExecutor();

                        for (QueryDocumentSnapshot doc : value) {
                            Eventcat eventcat = new Eventcat();
                            eventcat.setCatId(doc.getId());
                            eventcat.setCategory(doc.getString("competition"));
                            eventcat.setThumbnail(doc.getString("thumbnail"));
                            eventcat.setPriority(doc.getLong("priority"));
                            categories.add(eventcat);
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.REMOVED) {// A document was removed from Firestore
                                String deletedCatId = dc.getDocument().getId();

                                executor.execute(() -> {
                                    dbL.eventDao().deleteCategoryById(deletedCatId);
                                    dbL.eventDao().deleteEventsByCategoryId(deletedCatId);
                                });

                            }
                        }

                        executor.execute(() -> {
                            dbL.eventDao().insertEventCats(categories);
                        });

                        executor.shutdown(); // Shutdown the executor when done
                        Log.d(TAG, "Category Events: " + categories);

                    }
                });
    }

    // Fetch events for a specific category with a real-time listener
    public void listenForEvents(String catId, String category) {

        Log.d(TAG, "listenForEvents: catId"+catId);
        eventsListener = dbF.collection("eventcat")
                .document(catId)
                .collection(category)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        return;
                    }

                    if (value != null) {
                        List<Event> events = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Event event = new Event();
                            event.setEventId(doc.getId());
                            event.setMatch(doc.getString("match"));
                            event.setCategory(doc.getString("category"));
                            event.setThumbnail(doc.getString("thumbnail"));
                            event.setLink1(doc.getString("stream"));
                            event.setOrigin(doc.getString("origin"));
                            event.setReferrer(doc.getString("referer"));
                            event.setUser_Agent(doc.getString("user_agent"));
                            event.setTop(Boolean.TRUE.equals(doc.getBoolean("top")));
                            events.add(event);

                            Log.d(TAG, "listenForEvents: "+event.getCategory());
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.REMOVED) {// A document was removed from Firestore
                                String deletedId = dc.getDocument().getId();

                                Executors.newSingleThreadExecutor().execute(() -> {
                                    dbL.eventDao().deleteEventById(deletedId);
                                });
                            }
                        }

                        Executors.newSingleThreadExecutor().execute(() -> dbL.eventDao().insertEvents(events));
                        Log.d(TAG, "All Games Events: " + events);
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove listeners to prevent memory leaks
        if (eventCategoriesListener != null) {
            eventCategoriesListener.remove();
        }

        if (eventsListener != null) {
            eventsListener.remove();
        }
    }
}

