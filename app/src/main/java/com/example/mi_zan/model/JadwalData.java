package com.example.mi_zan.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JadwalData {
    @SerializedName("id")
    private String id;

    @SerializedName("lokasi")
    private String lokasi;

    @SerializedName("daerah")
    private String daerah;

    @SerializedName("jadwal")
    private List<JadwalItem> jadwal;

    public String getId() {
        return id;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getDaerah() {
        return daerah;
    }

    public List<JadwalItem> getJadwal() {
        return jadwal;
    }
}