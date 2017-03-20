package com.hardcoding.hardcoding;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by davidgarza on 26/02/17.
 */

public class Hard {
    public static final String YT_DEVELOPER_KEY = "AIzaSyBE0TZlProfjwfn9j5BGcwwSiQe0bXJ1so";

    public static void d(String s){
        Log.d("Hardcoding",s);
    }

    public static String getYoutubeVideoId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
