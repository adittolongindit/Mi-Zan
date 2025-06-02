package com.example.mi_zan.model;

import java.util.ArrayList;
import java.util.List;

public class WeekGroup implements PrayerScheduleDisplayItem {
    private String weekLabel;
    private List<DayGroup> dayGroups;
    private boolean isExpanded;

    public WeekGroup(String weekLabel) {
        this.weekLabel = weekLabel;
        this.dayGroups = new ArrayList<>();
        this.isExpanded = false;
    }

    public String getWeekLabel() {
        return weekLabel;
    }

    public List<DayGroup> getDayGroups() {
        return dayGroups;
    }

    public void addDayGroup(DayGroup dayGroup) {
        this.dayGroups.add(dayGroup);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    @Override
    public int getItemType() {
        return VIEW_TYPE_WEEK;
    }
}