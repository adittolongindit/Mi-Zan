package com.example.mi_zan.model;

import java.util.List;

public class SearchNearbyRequestBody {
    private List<String> includedTypes;
    private int maxResultCount;
    private LocationRestriction locationRestriction;


    public SearchNearbyRequestBody(List<String> includedTypes, int maxResultCount, LocationRestriction locationRestriction) {
        this.includedTypes = includedTypes;
        this.maxResultCount = maxResultCount;
        this.locationRestriction = locationRestriction;
    }

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