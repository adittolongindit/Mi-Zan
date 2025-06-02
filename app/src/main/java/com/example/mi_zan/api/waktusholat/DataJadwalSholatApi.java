package com.example.mi_zan.api.waktusholat;

import com.google.gson.annotations.SerializedName;

public class DataJadwalSholatApi {
    @SerializedName("status")
    private String status;
    @SerializedName("pesan")
    private String pesan;
    @SerializedName("data")
    private WaktuJadwalSholatApi data;

    public String getStatus() { return status; }
    public String getPesan() { return pesan; }
    public WaktuJadwalSholatApi getData() { return data; }
}