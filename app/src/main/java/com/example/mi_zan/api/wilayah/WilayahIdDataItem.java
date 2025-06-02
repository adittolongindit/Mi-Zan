package com.example.mi_zan.api.wilayah;

import com.google.gson.annotations.SerializedName;

public class WilayahIdDataItem {
    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;

    public String getCode() { return code; }
    public String getName() { return name; }
}