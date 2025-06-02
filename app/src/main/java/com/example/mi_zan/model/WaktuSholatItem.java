package com.example.mi_zan.model;

public class WaktuSholatItem {
    private String name;
    private String time;

    public WaktuSholatItem(String name, String time) {
        this.name = name;
        this.time = time;
    }
    public String getName() { return name; }
    public String getTime() { return time; }
}