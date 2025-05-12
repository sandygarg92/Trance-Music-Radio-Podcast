package com.onlineradiofm.trancemusicradio;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceClass {
    Context context;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    public final static String PREFS_NAME = "radioapp";

    public PreferenceClass(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, 0);
        editor = prefs.edit();
    }

    public void setIsFirst(boolean value) {
        editor.putBoolean("isFirst", value);
        editor.apply();
    }

    public boolean isFirst() {
        return prefs.getBoolean("isFirst", false);
    }
}