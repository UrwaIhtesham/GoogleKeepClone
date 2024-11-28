package com.example.l215404.googlekeep.SessionManager;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS_NAME = "LoginSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createSession(int userId, String email) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public  boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_USER_ID);
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
