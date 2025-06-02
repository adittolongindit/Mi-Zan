package com.example.mi_zan.model;

import java.util.List;

public class SearchNearbyRequestBody {
    private List<String> includedTypes;
    private int maxResultCount;
    private LocationRestriction locationRestriction;
    // tambahkan rankPreference jika ingin mengurutkan berdasarkan jarak
    // private String rankPreference;


    public SearchNearbyRequestBody(List<String> includedTypes, int maxResultCount, LocationRestriction locationRestriction) {
        this.includedTypes = includedTypes;
        this.maxResultCount = maxResultCount;
        this.locationRestriction = locationRestriction;
    }

    // Getters (dan Setters jika perlu)
    public List<String> getIncludedTypes() {
        return includedTypes;
    }

    public int getMaxResultCount() {
        return maxResultCount;
    }

    public LocationRestriction getLocationRestriction() {
        return locationRestriction;
    }
}