package com.example.hostelfinderandroidapp.controlers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hostelfinderandroidapp.Constants;
import com.example.hostelfinderandroidapp.model.User;
import com.google.gson.Gson;

public class MyPrefLocalStorage {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public static void saveCurrentUserData(Context context, User user ){

        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user); // user - instance of User
        editor.putString(Constants.SHARED_PREF_USER_KEY, json).apply();

    }

    public static User getCurrentUserData(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = sharedPreferences.getString(Constants.SHARED_PREF_USER_KEY, new Gson().toJson(new User()));
        return gson.fromJson(json, User.class);
    }

    public static void clearPreferences(){
        editor.clear().apply();
    }

}
