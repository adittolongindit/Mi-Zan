package com.example.mi_zan.model; // Sesuaikan dengan package Anda

public class SinglePrayerTime {
    private String prayerName; // Contoh: "Subuh", "Dzuhur"
    private String prayerTime; // Contoh: "04:35"
    private String dateDisplay; // Contoh: "Minggu, 01/06/2025"
    private String rawDate;    // Contoh: "2025-06-01" (untuk referensi internal jika perlu)
    private boolean isActive;  // Untuk status aktif/nonaktif

    public SinglePrayerTime(String prayerName, String prayerTime, String dateDisplay, String rawDate) {
        this.prayerName = prayerName;
        this.prayerTime = prayerTime;
        this.dateDisplay = dateDisplay;
        this.rawDate = rawDate;
        this.isActive = true; // Default aktif saat pertama kali dimuat
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

    // Setter
    public void setActive(boolean active) {
        isActive = active;
    }
}