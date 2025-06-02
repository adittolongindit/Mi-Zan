package com.example.mi_zan.model;

public interface PrayerScheduleDisplayItem {
    int getItemType();

    int VIEW_TYPE_WEEK = 0;
    int VIEW_TYPE_DAY = 1;
    int VIEW_TYPE_PRAYER = 2;
}