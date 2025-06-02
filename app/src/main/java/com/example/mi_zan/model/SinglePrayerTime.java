package com.example.mi_zan.model;

public class SinglePrayerTime {
    private String prayerName;
    private String prayerTime;
    private String dateDisplay;
    private String rawDate;
    private boolean isActive;

    public SinglePrayerTime(String prayerName, String prayerTime, String dateDisplay, String rawDate) {
        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
        this.dateDisplay = dateDisplay;
        this.rawDate = rawDate;
        this.isActive = true;
    }

    // Getters
    public String getPrayerName() {
        return prayerName;
    }

    public String getPrayerTime() {
        return prayerTime;
    }

    public String getDateDisplay() {
        return dateDisplay;
    }

    public String getRawDate() {
        return rawDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}