package com.example.mychapapp.activities;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences sharedPreferences;
    public static final String USER_NAME = "userName";

    public SessionManager(Context context) {

                          sharedPreferences    = context.getSharedPreferences(USER_NAME,Context.MODE_PRIVATE);
    }


    public void UserName (String userName)
    {

        SharedPreferences.Editor editor =sharedPreferences.edit();
editor.putString(USER_NAME,userName);
editor.apply();
    }

    public String fetchUserName()
    {
        return sharedPreferences.getString(USER_NAME,"");
    }
}

