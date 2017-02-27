package com.hardcoding.hardcoding;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{
    @BindString(R.string.error_user_not_found) String strErrorUserNotFound;
    @BindString(R.string.error_server) String strServerError;
    @BindString(R.string.login_welcome) String strLoginWelcome;

    @BindView(R.id.et_email) EditText etEmail;
    @BindView(R.id.il_email) TextInputLayout ilEmail;
    @BindView(R.id.et_password) EditText etPassword;
    @BindView(R.id.il_password) TextInputLayout ilPassword;
    @BindView(R.id.login_container) ViewGroup loginContainer;
    @BindView(R.id.loading_progressbar) ProgressBar progressBar;

    private Gson gson = new Gson();
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        session = new SessionManager(getApplicationContext());

    }

    private void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        loginContainer.setVisibility(View.INVISIBLE);
    }

    private void hideLoading(){
        progressBar.setVisibility(View.GONE);
        loginContainer.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_login)
    void buttonLogin(){
        showLoading();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        //TODO: validations

        HashMap<String,String> params = new HashMap<>();
        params.put(RequestSender.PARAMS_EMAIL,email);
        params.put(RequestSender.PARAMS_PASSWORD,password);
        RequestSender.make("LoginActivity", RequestSender.LOGIN_SERVICE, params, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Hard.d("Response:" + response.toString());
                try {
                    String loginMessage;
                    if (response.isNull(0)){
                        loginMessage = strErrorUserNotFound;
                    }else{
                        LoginResponse loginResponse = gson.fromJson(response.getJSONObject(0).toString(),LoginResponse.class);
                        session.createLoginSession(loginResponse);
                        loginMessage = strLoginWelcome;
                    }
                    Snackbar.make(etEmail,loginMessage,Snackbar.LENGTH_SHORT).show();
                    hideLoading();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Hard.d("Error:" + error.toString());
                Snackbar.make(etEmail,strServerError,Snackbar.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }

}

