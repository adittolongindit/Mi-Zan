package com.example.mi_zan.model;

public class PlaceResult {
    private String id;
    private DisplayName displayName;
    private String formattedAddress;
    private PlaceLocation location;

    // Getters (dan Setters jika perlu)
    public String getId() {
        return id;
    }

    public DisplayName getDisplayName() {
        return displayName;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public PlaceLocation getLocation() {
        return location;
    }
}