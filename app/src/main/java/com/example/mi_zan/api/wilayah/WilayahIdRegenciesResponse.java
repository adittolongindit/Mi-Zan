package com.example.mi_zan.api.wilayah;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WilayahIdRegenciesResponse {
    @SerializedName("data")
    private List<WilayahIdDataItem> data;
    public List<WilayahIdDataItem> getData() { return data; }
}