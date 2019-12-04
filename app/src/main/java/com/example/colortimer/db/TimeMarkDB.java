package com.example.colortimer.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.colortimer.model.TimeMark;


@Database(entities = TimeMark.class, version = 1)
public abstract class TimeMarkDB extends RoomDatabase {

    public abstract TimeMarkDao timeMarkDao();

    public static final String DATABASE_NAME = "timeMarkDb";
    private static TimeMarkDB instance;

    public static TimeMarkDB getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, TimeMarkDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public static void closeInstance(){

        if(instance != null){
            instance.getOpenHelper().close();
        }
    }
}
