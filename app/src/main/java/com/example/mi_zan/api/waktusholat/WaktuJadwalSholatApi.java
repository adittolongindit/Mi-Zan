package com.example.mi_zan.api.waktusholat;

import com.google.gson.annotations.SerializedName;

public class WaktuJadwalSholatApi {
    @SerializedName("imsak")
    private String imsak;
    @SerializedName("subuh")
    private String subuh;
    @SerializedName("terbit")
    private String terbit;
    @SerializedName("dhuha")
    private String dhuha;
    @SerializedName("dzuhur")
    private String dzuhur;
    @SerializedName("ashar")
    private String ashar;
    @SerializedName("maghrib")
    private String maghrib;
    @SerializedName("isya")
    private String isya;

    public String getImsak() { return imsak; }
    public String getSubuh() { return subuh; }
    public String getTerbit() { return terbit; }
    public String getDhuha() { return dhuha; }
    public String getDzuhur() { return dzuhur; }
    public String getAshar() { return ashar; }
    public String getMaghrib() { return maghrib; }
    public String getIsya() { return isya; }
}