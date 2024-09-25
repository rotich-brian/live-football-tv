package com.bottom.footballtv.services.Room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.models.Room.Eventcat;

@Database(entities = {Event.class, Eventcat.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {
    private static volatile LocalDatabase instance;

    public abstract EventsDao eventDao();

    public static LocalDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    LocalDatabase.class, "EventDatabase")
                            .fallbackToDestructiveMigration()  // Optional: handle database schema changes
                            .build();
                }
            }
        }
        return instance;
    }
}