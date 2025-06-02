package com.example.mi_zan.model;

import com.google.gson.annotations.SerializedName;

public class JadwalResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private JadwalData data;

    public boolean isStatus() {
        return status;
    }

    public JadwalData getData() {
        return data;
    }
}