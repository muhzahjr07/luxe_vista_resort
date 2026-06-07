package com.example.luxevistaresort;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "luxevista_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences prefs;

    public SessionManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void login(long userId, String email) {
        prefs.edit()
                .putLong(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public boolean isLoggedIn() {
        return getUserId() > 0;
    }

    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public void logout() {
        prefs.edit().clear().apply();
    }
}
