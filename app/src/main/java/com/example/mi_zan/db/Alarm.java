package com.example.mi_zan.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarms")
public class Alarm {
    @PrimaryKey
    @NonNull
    private String id; // Contoh: "2025-06-17-Subuh"
    private String prayerName;
    private long triggerTimeMillis;
    private boolean isEnabled;

    // Constructor, Getters, and Setters
    public Alarm(@NonNull String id, String prayerName, long triggerTimeMillis, boolean isEnabled) {
        this.id = id;
        this.prayerName = prayerName;
        this.triggerTimeMillis = triggerTimeMillis;
        this.isEnabled = isEnabled;
    }

    @NonNull
    public String getId() { return id; }
    public String getPrayerName() { return prayerName; }
    public long getTriggerTimeMillis() { return triggerTimeMillis; }
    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }
}