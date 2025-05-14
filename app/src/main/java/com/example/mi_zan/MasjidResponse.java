package com.example.mi_zan;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MasjidResponse {
    @SerializedName("data")
    public List<Masjid> masjidList;

    public static class Masjid {
        @SerializedName("place_id")
        public String placeId;

        @SerializedName("name")
        public String name;

        @SerializedName("address")
        public String address;

        @SerializedName("location")
        public Location location;

        @SerializedName("distance")
        public String distance;

        @SerializedName("rating")
        public String rating;

        @SerializedName("user_ratings_total")
        public String userRatingsTotal;

        public static class Location {
            @SerializedName("lat")
            public String lat;

            @SerializedName("lng")
            public String lng;
        }
    }
}