package com.hashedin.marchantapp.viewactivity.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfilePrefManager {

    Context context;

    public ProfilePrefManager(Context context) {
        this.context = context;
    }

    public void saveProfileDetails(String name,String phonenumber,String language,String gender,String DOB,String textsize) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("phonenumber", phonenumber);
        editor.putString("language", language);
        editor.putString("gender", gender);
        editor.putString("DOB", DOB);
        editor.putString("textsize", textsize);
        editor.apply();
    }


    public String getName(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name","");
    }public String getPhonenumber() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("phonenumber","");
    } public String getLanguage() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE);
        return sharedPreferences.getString("language","");
    } public String getGender() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gender", Context.MODE_PRIVATE);
        return sharedPreferences.getString("gender","");
    } public String getDOB() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("DOB", Context.MODE_PRIVATE);
        return sharedPreferences.getString("DOB","");
    }public String getTextsize() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("textsize", Context.MODE_PRIVATE);
        return sharedPreferences.getString("textsize","");
    }

    public boolean isUserProfileEmpty() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ProfileDetails", Context.MODE_PRIVATE);
        boolean isKeyEmpty = sharedPreferences.getString("name", "").isEmpty();
        return isKeyEmpty;
    }

    public boolean logout(){

        SharedPreferences preferences = context.getSharedPreferences("ProfileDetails",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        return true;
    }


}