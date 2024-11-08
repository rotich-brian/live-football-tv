package com.bottom.footballtv.services.Room;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.bottom.footballtv.models.Room.Event;
import com.bottom.footballtv.models.Room.Eventcat;
import com.bottom.footballtv.services.Converters;

@Database(entities = {Event.class, Eventcat.class}, version = 3, exportSchema = false)
@TypeConverters({Converters.class})  // Register the converter here
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
