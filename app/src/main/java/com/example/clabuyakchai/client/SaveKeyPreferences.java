package com.example.clabuyakchai.client;

import android.content.Context;
import android.preference.PreferenceManager;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Clabuyakchai on 13.04.2018.
 */

public class SaveKeyPreferences {
    private static final String PRE_SECRET_KEY = "secretKey";

    public static String getSecretKey(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PRE_SECRET_KEY, null);
    }

    public static void setSecretKey(Context context, String key){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PRE_SECRET_KEY, key)
                .apply();
    }
}
