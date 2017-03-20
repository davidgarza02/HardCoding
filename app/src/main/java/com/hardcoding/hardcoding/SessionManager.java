package com.hardcoding.hardcoding;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
    private static final String KEY_TRANSACTIONS = "transactions";
    private Context context;
    private SharedPreferences pref;
    private Editor editor;
    private Gson gson = new Gson();

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(LoginResponse loginResponse){
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_ID, loginResponse.getUser().getId());
        editor.putString(KEY_USERNAME, loginResponse.getUser().getUsername());
        editor.putString(KEY_EMAIL, loginResponse.getUser().getEmail());
        editor.putInt(KEY_TOKENS, loginResponse.getUser().getTokens());
        editor.putString(KEY_TRANSACTIONS, gson.toJson(loginResponse.getTransactions()));
        editor.commit();
    }

    public int getTokens(){
        return pref.getInt(KEY_TOKENS, 0);
    }

    public String getEmail(){
        return pref.getString(KEY_EMAIL, "");
    }

    public int getUserId(){
        return pref.getInt(KEY_ID, 0);
    }

    public List<Integer> getLessonsOwned(){
        List<Integer> transactionIdList = new ArrayList<>();
        String transactions = pref.getString(KEY_TRANSACTIONS,null);
        if (!transactions.equals("null")){
           ArrayList<Transaction> transactionArrayList = gson.fromJson(transactions,new TypeToken<Collection<Transaction>>(){}.getType());
            for (Transaction lessonId: transactionArrayList) {
                transactionIdList.add(lessonId.getIdlesson());
            }
        }
        return transactionIdList;
    }

    public void addLessonOwned(Lesson lesson){
        Transaction transaction = new Transaction();
        transaction.setIdlesson(lesson.getId());
        ArrayList<Transaction> currentTransactions = getTransactions() == null ? new ArrayList<Transaction>() : getTransactions();
        currentTransactions.add(transaction);
        int newAmount = getTokens() - lesson.getPrice();
        editor.putInt(KEY_TOKENS,newAmount);
        editor.putString(KEY_TRANSACTIONS, gson.toJson(currentTransactions));
        editor.commit();
    }

    public ArrayList<Transaction> getTransactions(){
        ArrayList<Transaction> transactionArrayList = new ArrayList<>();
        String transactions = pref.getString(KEY_TRANSACTIONS, "");
        if (!transactions.equals("null")){
            transactionArrayList = gson.fromJson(transactions,new TypeToken<Collection<Transaction>>(){}.getType());
        }
        return transactionArrayList;
    }

    public void logout() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putInt(KEY_ID, -1);
        editor.putString(KEY_USERNAME, null);
        editor.putString(KEY_EMAIL, null);
        editor.putInt(KEY_TOKENS, -1);
        editor.putString(KEY_TRANSACTIONS, null);
        editor.commit();
    }
}
