package com.example.mi_zan.model;

import java.util.List;

public class SearchNearbyResponseBody {
    private List<PlaceResult> places;

    // Getters (dan Setters jika perlu)
    public List<PlaceResult> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlaceResult> places) {
        this.places = places;
    }
}