package com.example.mi_zan.api.waktusholat;

import com.google.gson.annotations.SerializedName;

public class DataJadwalSholatResponseApi {
    @SerializedName("status")
    private String status;
    @SerializedName("query")
    private QueryJadwal query;
    @SerializedName("jadwal")
    private DataJadwalSholatApi jadwal; // Menggunakan PrayerTimeApiScheduleData

    public String getStatus() { return status; }
    public QueryJadwal getQuery() { return query; }
    public DataJadwalSholatApi getJadwal() { return jadwal; }

    public static class QueryJadwal {
        @SerializedName("kota")
        private String kota;
        @SerializedName("tanggal")
        private String tanggal;
        public String getKota() { return kota; }
        public String getTanggal() { return tanggal; }
    }
}