package com.live.programming.an21_functionalapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LocalSession {
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public LocalSession(Context ctx)
    {
        sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor = sp.edit();
    }

    public void storeData(String key, String data)
    {
        editor.putString(key, data);
        editor.commit();
    }

    public String readData(String key)
    {
        return sp.getString(key, "");
    }

    public void deleteAll()
    {
        editor.clear();
        editor.commit();
    }

    public void deleteOne(String key){
        editor.remove(key);
        editor.commit();
    }
}
