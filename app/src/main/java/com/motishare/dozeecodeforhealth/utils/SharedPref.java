package com.motishare.dozeecodeforhealth.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.motishare.dozeecodeforhealth.interfaces.Constants;

import org.jetbrains.annotations.NotNull;

public class SharedPref implements Constants {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String PREFS_NAME = APP;

    public SharedPref(@NotNull Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        editor = sharedPreferences.edit();
    }

    public void setBoolean(String key, boolean value) {
        Log.e(key, "" + value);
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setString(String key, String value) {
        Log.e(key, "" + value);
        editor.putString(key, value);
        editor.commit();
    }

    public void setLong(String key, Long value) {
        Log.e(key, "" + value);
        editor.putLong(key, value);
        editor.commit();
    }

    public void setInt(String key, int value) {
        Log.e(key, "" + value);
        editor.putInt(key, value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        Log.e(key, "" + sharedPreferences.getBoolean(key, false));
        return sharedPreferences.getBoolean(key, false);
    }

    public String getString(String key) {
        Log.e(key, "" + sharedPreferences.getString(key, ""));
        return sharedPreferences.getString(key, "");
    }

    public long getLong(String key) {
        Log.e(key, "" + sharedPreferences.getLong(key,0));
        return sharedPreferences.getLong(key, 0);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    /* to clear all data when UserModel logged out */
    public void clearData() {
        String token = getString(USER_PUSH_TOKEN);
        editor.clear();
        setString(USER_PUSH_TOKEN, token);
        editor.commit();
    }
}