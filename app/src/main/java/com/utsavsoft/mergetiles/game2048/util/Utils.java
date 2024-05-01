package com.utsavsoft.mergetiles.game2048.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.utsavsoft.mergetiles.game2048.R;

public class Utils {

    public static final String SHAREDPREFERENCEFILENAME = "2048";

    private Context context;
    private SharedPreferences preferences;
    private Activity activity;
    public static String mute_music = "mute_music";
    public static String mute_sounds = "mute_sounds";
    public static String tutorial_played = "tutorial_played";
    public static final String TOP_SCORE = "TopScore";

    public Utils(Activity activity) {
        this.activity = activity;
        context = activity;
        preferences = context.getSharedPreferences(SHAREDPREFERENCEFILENAME, Context.MODE_PRIVATE);
    }

    public void setBooleanValue(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBooleanValue(String key) {
        return preferences.getBoolean(key, false);
    }

    public void setStringValue(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getStringValue(String key) {
        return preferences.getString(key, "");
    }


    public void setInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }


    public void setLong(String key, long value) {
        preferences.edit().putLong(key, value).apply();
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }


}
