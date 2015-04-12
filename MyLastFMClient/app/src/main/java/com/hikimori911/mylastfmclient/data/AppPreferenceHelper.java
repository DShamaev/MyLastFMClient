package com.hikimori911.mylastfmclient.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hikimori911 on 30.03.2015.
 */
public class AppPreferenceHelper {

    private static final String KEY_STORAGE = "mylastfmclient";
    private static final String AUTH_NAME = "AUTH_NAME";

    public static synchronized String getUserName(final Context context) {
        SharedPreferences pref = context.getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE);
        return pref.getString(AUTH_NAME, null);
    }

    public static void saveUserName(final Context context, final String name) {
        SharedPreferences pref = context.getSharedPreferences(KEY_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(AUTH_NAME, name);
        editor.commit();
    }
}
