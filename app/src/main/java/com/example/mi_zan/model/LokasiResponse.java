package com.example.mi_zan.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LokasiResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("data")
    private List<LokasiItem> data;

    public boolean isStatus() {
        return status;
    }

    public List<LokasiItem> getData() {
        return data;
    }
}