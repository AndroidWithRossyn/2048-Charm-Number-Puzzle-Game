package com.rossyn.blocktiles.game2048.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    public static final String SHARED_PREF_FILE_NAME = "2048";

    public static final String MUTE_MUSIC = "mute_music";
    public static final String MUTE_SOUNDS = "mute_sounds";
    public static final String TUTORIAL_PLAYED = "tutorial_played";
    public static final String TOP_SCORE = "TopScore";

    private final SharedPreferences preferences;

    public SharedPref(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void setBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public void setString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getString(String key) {
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
        return preferences.getLong(key, 0L);
    }

    /**
     * Removes the preference value for the given key.
     *
     * @param key The key whose value needs to be removed.
     */
    public void remove(String key) {
        preferences.edit().remove(key).apply();
    }

    /**
     * Clears all preferences.
     */
    public void clear() {
        preferences.edit().clear().apply();
    }
}
