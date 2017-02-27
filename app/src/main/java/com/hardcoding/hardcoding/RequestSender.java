package com.hardcoding.hardcoding;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hardcoding.hardcoding.AppController;
import com.hardcoding.hardcoding.Hard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RequestSender {
    private static final String BASE_URL = "http://40.74.252.246/";

    public static final String LOGIN_SERVICE = "login.php";
    public static final String PARAMS_EMAIL = "email";
    public static final String PARAMS_PASSWORD = "password";

    public static void make(String tag, String service, HashMap<String,String> params, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener){
        Hard.d("Params:" + params.toString());
        AppController.getInstance().addToRequestQueue(new CustomJsonArrayRequest(Request.Method.POST, BASE_URL + service, new JSONObject(params), responseListener, errorListener),tag);
    }

}