package com.example.mi_zan.api.waktusholat;

import com.google.gson.annotations.SerializedName;

public class WaktuSholatApiKotaItem {
    @SerializedName("id")
    private String id;
    @SerializedName("nama")
    private String nama;

    public String getId() { return id; }
    public String getNama() { return nama; }
}