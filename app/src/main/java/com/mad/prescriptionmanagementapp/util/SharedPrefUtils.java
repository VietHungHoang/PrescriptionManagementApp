package com.mad.prescriptionmanagementapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtils {


    private final Context context;
    private final SharedPreferences prefs;
    private static final String PREF_NAME = "user_token_name";

    public SharedPrefUtils(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token, String name) {
        this.prefs.edit().putString("jwt_token", token).apply();
        this.prefs.edit().putString("user_name", name).apply();
    }

    public void saveName(String name) {
        this.prefs.edit().putString("user_name", name).apply();
    }

    public String getToken() {
        return this.prefs.getString("jwt_token", null);
    }

    public String getName() {
        return this.prefs.getString("user_name", null);
    }
}