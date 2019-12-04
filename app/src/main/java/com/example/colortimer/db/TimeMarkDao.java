package com.example.colortimer.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.colortimer.model.TimeMark;

import java.util.List;


@Dao
public interface TimeMarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTimeMark(TimeMark timeMark);

    @Delete
    void deleteTimeMark(TimeMark timeMark);

    @Update
    void updateTimeMark(TimeMark timeMark);

    @Query("SELECT * FROM timeMark")
    List<TimeMark> getTimeMark();

    @Query("SELECT * FROM timeMark WHERE startTime BETWEEN :start AND :end ")
    List<TimeMark> getTimeMarkFromTo(long start, long end);

    @Query("SELECT * FROM timeMark WHERE workState LIKE 'work'")
    List<TimeMark> getWorkTime();

    @Query("SELECT * FROM timeMark WHERE workState LIKE 'rest'")
    List<TimeMark> getRestMark();

    @Query("DELETE FROM timeMark WHERE startTime LIKE :startTime")
    void deleteTimeMarkByStartTime(long startTime);
}
