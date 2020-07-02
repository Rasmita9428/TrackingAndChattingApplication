package com.rasmitap.tailwebs_assigment2.utils;

/**
 * Created by administrator on 4/12/17.
 */

import android.content.Context;
import android.content.SharedPreferences;


public class Utility {

    public static Context appContext;
    private static String PREFERENCE;
    private static Utility mInstance;
    private static Context mCtx;
    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_TOKEN = "tagtoken";


    public Utility(Context context) {
        mCtx = context;

    }

    public static synchronized Utility getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Utility(context);
        }
        return mInstance;
    }

    public static void setStringSharedPreference(Context context, String name, String value) {
        appContext = context;
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }


    public static String getStringSharedPreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        return settings.getString(name, "");
    }


    public static void clearPreference(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
        editor.apply();
    }
}