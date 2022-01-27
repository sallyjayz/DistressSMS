package com.sallyjayz.distresssms.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sallyjayz.distresssms.model.DistressContact;


@Database(entities = {DistressContact.class}, version = 1, exportSchema = false)
public abstract class DistressAppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "distress_database";
    private static DistressAppDatabase INSTANCE;

    static DistressAppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DistressAppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DistressAppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ContactDao contactDao();

}
