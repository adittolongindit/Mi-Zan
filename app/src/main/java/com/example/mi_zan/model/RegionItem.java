package com.example.mi_zan.model;

public class RegionItem {
    private String id;
    private String name;

    public RegionItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() { return name; }
}