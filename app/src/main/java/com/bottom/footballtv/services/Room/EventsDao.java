package com.bottom.footballtv.services.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.models.Room.Eventcat;

import java.util.List;

@Dao
public interface EventsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEventCats(List<Eventcat> eventcatList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvents(List<Event> events);

    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    LiveData<Event> getEventById(String eventId);

    @Query("SELECT * FROM Eventcat WHERE CatId = :catId")
    LiveData<Eventcat> getCategoryById(String catId);

    @Query("SELECT * FROM Eventcat WHERE CatId = :catId")
    Eventcat getCategoryByCatId(String catId);

    @Query("SELECT * FROM Event WHERE eventId = :eventId")
    LiveData<List<Event>> getEventsById(String eventId);

    @Query("SELECT * FROM Event WHERE Category = :category")
    LiveData<List<Event>> getEventsByCategory(String category);

    @Query("SELECT * FROM Eventcat")
    LiveData<List<Eventcat>> getAllCategories();

    @Query("DELETE FROM Eventcat WHERE catId = :catId")
    void deleteCategoryById(String catId);

    @Query("DELETE FROM Event WHERE EventId = :eventId")
    void deleteEventById(String eventId);

    @Query("DELETE FROM Event WHERE Category = :catId")
    void deleteEventsByCategoryId(String catId);

    @Query("SELECT * FROM Event WHERE isTop = 1 ")
    LiveData<List<Event>> getTopEvents();
}
