package com.hardcoding.hardcoding;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
    @BindString(R.string.error_invalid_email) String strErrorInvalidEmail;
    @BindString(R.string.error_pass_short) String strErrorPasswordShort;
    @BindString(R.string.error_pass_empty) String strErrorPasswordEmpty;
    @BindString(R.string.error_pass_must_match) String strErrorPasswordsMustMatch;
    @BindString(R.string.error_username_short) String strUsernameShort;

    @BindView(R.id.et_email) TextInputEditText etEmail;
    @BindView(R.id.il_email) TextInputLayout ilEmail;
    @BindView(R.id.et_password) TextInputEditText etPassword;
    @BindView(R.id.il_password) TextInputLayout ilPassword;
    @BindView(R.id.login_container) ViewGroup loginContainer;
    @BindView(R.id.loading_progressbar) ProgressBar progressBar;

    @BindView(R.id.signin_container) ViewGroup signinContainer;
    @BindView(R.id.et_username_si) TextInputEditText etUsernameSi;
    @BindView(R.id.il_username_si) TextInputLayout ilUsernameSi;
    @BindView(R.id.et_email_si) TextInputEditText etEmailSi;
    @BindView(R.id.il_email_si) TextInputLayout ilEmailSi;
    @BindView(R.id.et_password_si) TextInputEditText etPasswordSi;
    @BindView(R.id.il_password_si) TextInputLayout ilPasswordSi;
    @BindView(R.id.et_password_confirmation_si) TextInputEditText etConfirmationSi;
    @BindView(R.id.il_password_confirmation_si) TextInputLayout ilConfirmationSi;

    private Gson gson = new Gson();
    private SessionManager session;
    private boolean showLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        session = new SessionManager(getApplicationContext());
    }

    private void showLoading(boolean login){
        progressBar.setVisibility(View.VISIBLE);
        if (login)
            loginContainer.setVisibility(View.INVISIBLE);
        else
            signinContainer.setVisibility(View.INVISIBLE);
    }

    private void hideLoading(boolean login){
        progressBar.setVisibility(View.GONE);
        if (login)
            loginContainer.setVisibility(View.VISIBLE);
        else
            signinContainer.setVisibility(View.VISIBLE);
    }

    private void toogleLoginSignIn(){
        showLogin = !showLogin;
        if (showLogin){
            loginContainer.setVisibility(View.VISIBLE);
            signinContainer.setVisibility(View.GONE);
            etEmail.setText("");
            etPassword.setText("");
        }else{
            loginContainer.setVisibility(View.GONE);
            signinContainer.setVisibility(View.VISIBLE);
            etEmailSi.setText("");
            etPasswordSi.setText("");
            etConfirmationSi.setText("");
            etPasswordSi.setText("");
        }
    }

    @OnClick(R.id.btn_send_sign_in)
    void buttonSendSignin(){
        String username = etUsernameSi.getText().toString().trim();
        String email = etEmailSi.getText().toString().trim();
        String password = etPasswordSi.getText().toString().trim();
        String passwordConfirmation = etConfirmationSi.getText().toString().trim();

        showLoading(false);
        boolean error = false;
        if (username.length() < 3){
            ilUsernameSi.setError(strUsernameShort);
            error = true;
        }else{
            ilUsernameSi.setError(null);
        }
        if (!isValidEmail(email) || email.length() < 5){
            ilEmailSi.setError(strErrorInvalidEmail);
            error = true;
        }else{
            ilEmailSi.setError(null);
        }
        if (password.isEmpty()){
            ilPasswordSi.setError(strErrorPasswordEmpty);
            error = true;
        }else{
            ilPasswordSi.setError(null);
        }
        if (password.length() < 4){
            ilPasswordSi.setError(strErrorPasswordShort);
            error = true;
        }else{
            ilPasswordSi.setError(null);
        }
        if (!password.equals(passwordConfirmation)){
            ilPasswordSi.setError(strErrorPasswordsMustMatch);
            ilConfirmationSi.setError(strErrorPasswordsMustMatch);
            error = true;
        }else{
            ilPasswordSi.setError(null);
            ilConfirmationSi.setError(null);
        }

        if (!error) {
            HashMap<String, String> params = new HashMap<>();
            params.put(RequestSender.PARAMS_USERNAME, username);
            params.put(RequestSender.PARAMS_EMAIL, email);
            params.put(RequestSender.PARAMS_PASSWORD, password);
            RequestSender.make("LoginActivity", RequestSender.REGISTER_SERVICE, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    Hard.d("Response:" + response.toString());
                    String registerMessage;
                    try {
                        if (response.get("user") != JSONObject.NULL) {
                            LoginResponse loginResponse = gson.fromJson(response.toString(), LoginResponse.class);
                            session.createLoginSession(loginResponse);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            registerMessage = strServerError;
                            Snackbar.make(etEmail, registerMessage, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideLoading(false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(etEmail, strServerError, Snackbar.LENGTH_SHORT).show();
                    hideLoading(false);
                }
            });
        }else{
            hideLoading(false);
        }
    }

    @OnClick(R.id.btn_cancel_sign_in)
    void buttonCancelSignin(){
        toogleLoginSignIn();
    }

    @OnClick(R.id.btn_sign_in)
    void buttonSignin(){
        if (showLogin)
            toogleLoginSignIn();
    }

    @OnClick(R.id.btn_login)
    void buttonLogin(){
        showLoading(true);
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        boolean error = false;
        if (!isValidEmail(email) || email.length() < 5){
            ilEmail.setError(strErrorInvalidEmail);
            error = true;
        }else{
            ilEmail.setError(null);
        }
        if (password.isEmpty()){
            ilPassword.setError(strErrorPasswordEmpty);
            error = true;
        }else{
            ilPassword.setError(null);
        }
        if (password.length() < 4){
            ilPassword.setError(strErrorPasswordShort);
            error = true;
        }else{
            ilPassword.setError(null);
        }

        if (!error) {
            HashMap<String, String> params = new HashMap<>();
            params.put(RequestSender.PARAMS_EMAIL, email);
            params.put(RequestSender.PARAMS_PASSWORD, password);
            RequestSender.make("LoginActivity", RequestSender.LOGIN_SERVICE, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Hard.d("Response:" + response.toString());
                    String loginMessage;
                    try {
                        if (response.get("user") != JSONObject.NULL) {
                            LoginResponse loginResponse = gson.fromJson(response.toString(), LoginResponse.class);
                            session.createLoginSession(loginResponse);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            loginMessage = strErrorUserNotFound;
                            Snackbar.make(etEmail, loginMessage, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    hideLoading(true);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Hard.d("Error:" + error.toString());
                    Snackbar.make(etEmail, strServerError, Snackbar.LENGTH_SHORT).show();
                    hideLoading(true);
                }
            });
        }else{
            hideLoading(true);
        }

    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public void onBackPressed() {
        if (showLogin)
            super.onBackPressed();
        else
            toogleLoginSignIn();
    }
}

