package com.example.mi_zan.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "MiZanAppPrefs";
    private static final String KEY_LOCATION_ID = "location_id";
    private static final String KEY_LOCATION_NAME = "location_name";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveLocation(String id, String nama) {
        editor.putString(KEY_LOCATION_ID, id);
        editor.putString(KEY_LOCATION_NAME, nama);
        editor.apply();
    }

    public String getSavedLocationId() {
        return pref.getString(KEY_LOCATION_ID, "1301");
    }

    public String getSavedLocationName() {
        return pref.getString(KEY_LOCATION_NAME, "KOTA JAKARTA");
    }
}