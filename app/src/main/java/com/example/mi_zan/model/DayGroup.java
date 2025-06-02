package com.example.mi_zan.model;

import java.util.ArrayList;
import java.util.List;

public class DayGroup implements PrayerScheduleDisplayItem {
    private String dayLabel;
    private String rawDate;
    private List<SinglePrayerTime> prayerTimes;
    private boolean isExpanded;

    public DayGroup(String dayLabel, String rawDate) {
        this.dayLabel = dayLabel;
        this.rawDate = rawDate;
        this.prayerTimes = new ArrayList<>();
        this.isExpanded = false;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public String getRawDate() { return rawDate; }

    public List<SinglePrayerTime> getPrayerTimes() {
        return prayerTimes;
    }

    public void addPrayerTime(SinglePrayerTime prayerTime) {
        this.prayerTimes.add(prayerTime);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public int getItemType() {
        return VIEW_TYPE_DAY;
    }
}