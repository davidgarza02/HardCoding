package com.hardcoding.hardcoding;

import android.content.Context;

/**
 * Created by davidgarza on 28/02/17.
 */
public class Lesson {
    int id;
    String title;
    String description;
    String videourl;
    int price;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVideourl() {
        return videourl;
    }

    public int getPrice() {
        return price;
    }

//    public boolean getIsAvailable(Context context){
//        SessionManager sessionManager = new SessionManager(context);
//        sessionManager.getLessons
//    }
}
