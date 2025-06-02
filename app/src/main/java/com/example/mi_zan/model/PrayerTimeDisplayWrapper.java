package com.example.mi_zan.model;

public class PrayerTimeDisplayWrapper implements PrayerScheduleDisplayItem {
    private SinglePrayerTime singlePrayerTime;

    public PrayerTimeDisplayWrapper(SinglePrayerTime singlePrayerTime) {
        this.singlePrayerTime = singlePrayerTime;
    }

    public SinglePrayerTime getSinglePrayerTime() {
        return singlePrayerTime;
    }

    @Override
    public int getItemType() {
        return VIEW_TYPE_PRAYER;
    }
}