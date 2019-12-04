package com.example.colortimer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "timeMark")
public class TimeMark {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "num")
    private int num;
    @ColumnInfo(name = "startTime")
    private long startTime;
    @ColumnInfo(name = "endTime")
    private long endTime;
    @ColumnInfo(name = "usedTime")
    private long usedTime;
    @ColumnInfo(name = "workState")
    private String workState;
    @ColumnInfo(name = "isUpload")
    private boolean isUpload;



    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }
}
