package com.example.mi_zan.api.waktusholat;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WaktuSholatKotaResponseApi {
    @SerializedName("status")
    private String status;
    @SerializedName("query")
    private QueryData query;
    @SerializedName("kota")
    private List<WaktuSholatApiKotaItem> kota; // Menggunakan PrayerTimeApiCityItem

    public String getStatus() { return status; }
    public QueryData getQuery() { return query; }
    public List<WaktuSholatApiKotaItem> getKota() { return kota; }

    public static class QueryData {
        @SerializedName("nama")
        private String nama;
        public String getNama() { return nama; }
    }
}