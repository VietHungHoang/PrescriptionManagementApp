package com.mad.prescriptionmanagementapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {


    private final Context context;
    private static final String PREF_NAME = "user_token_name";

    public SharedPrefUtils(Context context) {
        this.context = context;
    }

    public void saveToken(String token, String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString("jwt_token", token).apply();
        prefs.edit().putString("user_name", name).apply();
    }

    public String getToken() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString("jwt_token", null);
    }

    public String getName() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString("user_name", null);
    }
}