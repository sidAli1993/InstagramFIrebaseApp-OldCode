package com.myvuapp.socialapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "bookc";
    private static final String TAGTOKEN = "token";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void clearlogout(Context c) {
        pref = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit().clear().commit();
    }


    public void setTagtoken(String tagtoken){
        editor.putString(TAGTOKEN,tagtoken);
        editor.commit();
    }


    //    Getter Functions

    public String getTagtoken() {
        return pref.getString(TAGTOKEN, "");
    }

}
