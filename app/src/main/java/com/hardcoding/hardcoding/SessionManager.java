package com.hardcoding.hardcoding;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by davidgarza on 27/02/17.
 */
public class SessionManager {
    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "loged_in";
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TOKENS = "tokens";
    private Context context;
    private SharedPreferences pref;
    private Editor editor;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(LoginResponse userData){
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_ID, userData.getId());
        editor.putString(KEY_USERNAME, userData.getUsername());
        editor.putString(KEY_EMAIL, userData.getEmail());
        editor.putInt(KEY_TOKENS, userData.getTokens());
        editor.commit();
    }

}
