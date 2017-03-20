package com.hardcoding.hardcoding;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LessonsRecyclerAdapter.OnTokensSpended{
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_lessons) RecyclerView recyclerView;
    @BindString(R.string.tokens_left) String strTokensLeft;

    SessionManager sessionManager;
    Gson gson = new Gson();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sessionManager = new SessionManager(this);
        setupToolbar();
        requestLessons();
    }

    private void requestLessons() {
        RequestSender.make("MainActivity", RequestSender.LESSONS_SERVICE,new HashMap<String, String>(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ArrayList<Lesson> lessons = null;
                try {
                    lessons = gson.fromJson(response.getJSONArray("lessons").toString(), new TypeToken<Collection<Lesson>>(){}.getType());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LessonsRecyclerAdapter lessonsRecyclerAdapter = new LessonsRecyclerAdapter(MainActivity.this,lessons,MainActivity.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(lessonsRecyclerAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Hard.d("response:" + error.toString());
            }
        });
    }

    private void setupToolbar() {
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));
        toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorAccent));
        toolbar.setTitle(strTokensLeft + " " + sessionManager.getTokens());
        setSupportActionBar(toolbar);
    }

    @Override
    public void onTokensSpended() {
        toolbar.setTitle(strTokensLeft + " " + sessionManager.getTokens());
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                sessionManager.logout();
                finish();
                Intent i = new Intent(this,LoginActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
