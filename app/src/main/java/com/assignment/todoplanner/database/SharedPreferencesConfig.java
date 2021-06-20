package com.assignment.todoplanner.database;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesConfig {
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesConfig(Context context) {
        sharedPreferences = context.getSharedPreferences("com.assignment.todoplanner.Data_preferences", Context.MODE_PRIVATE);
    }

    public void writeLogInStatus(boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("logInStatus", status);
        editor.apply();
    }

    public boolean readLogInStatus() {
        return sharedPreferences.getBoolean("logInStatus", false);
    }

    public void writeUserEmail(String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", email);
        editor.apply();
    }

    public String readUserEmail() {
        return sharedPreferences.getString("userEmail", "email");
    }

    public void writeUserName(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", name);
        editor.apply();
    }

    public String readUserName() {
        return sharedPreferences.getString("userName", "name");
    }
}
