package com.hardcoding.hardcoding;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by davidgarza on 14/03/17.
 */
public class LessonsRecyclerAdapter extends RecyclerView.Adapter<LessonsRecyclerAdapter.ViewHolder>{
    @BindString(R.string.title_buy_confirmation) String strTitleBuy;
    @BindString(R.string.message_buy_confirmation) String strMessageBuy;
    @BindString(R.string.go_to_lesson) String strGoToLesson;
    @BindString(R.string.buy) String strBuy;
    @BindString(R.string.error_no_tokens) String strErrorNoTokens;

    private Gson gson = new Gson();
    private OnTokensSpended onTokensSpendedListener;
    private Activity activity;
    private ArrayList<Lesson> lessons;
    private SessionManager sessionManager;

    public LessonsRecyclerAdapter(Activity activity, ArrayList<Lesson> lessons, OnTokensSpended onTokensSpendedListener) {
        this.activity = activity;
        this.lessons = lessons;
        this.onTokensSpendedListener = onTokensSpendedListener;
        sessionManager = new SessionManager(activity);
        ButterKnife.bind(this,activity);
    }

    @Override
    public LessonsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.lesson_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LessonsRecyclerAdapter.ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        final Lesson lesson = lessons.get(adapterPosition);

        holder.title.setText(lesson.getTitle());
        holder.description.setText(lesson.getDescription());
        holder.price.setText(activity.getResources().getString(R.string.tokens_sufix,lesson.getPrice()));
        Glide.with(activity).load("http://img.youtube.com/vi/"+ Hard.getYoutubeVideoId(lesson.getVideourl())+"/0.jpg").placeholder(R.drawable.video_placeholder)
                .crossFade().into(holder.thumbnail);
        if (sessionManager.getLessonsOwned().contains(lesson.getId())){
            holder.buttonBuy.setText(strGoToLesson);
            View.OnClickListener onClickListener  = new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(activity,LessonDetailActivity.class);
                    i.putExtra("lesson",gson.toJson(lesson));
                    activity.startActivity(i);
                }
            };
            holder.buttonBuy.setOnClickListener(onClickListener);
            holder.mainContainer.setOnClickListener(onClickListener);
        }else{
            holder.buttonBuy.setText(strBuy);
            View.OnClickListener onClickListener  = new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    if (sessionManager.getTokens() >= lesson.getPrice())
                        showConfirmation(lesson);
                    else
                        Snackbar.make(holder.mainContainer,strErrorNoTokens,Snackbar.LENGTH_SHORT).show();
                }
            };
            holder.buttonBuy.setOnClickListener(onClickListener);
            holder.mainContainer.setOnClickListener(onClickListener);
        }
    }

    private void showConfirmation(final Lesson lesson) {
        new AlertDialog.Builder(activity)
        .setTitle(strTitleBuy)
        .setMessage(strMessageBuy)
        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int which) {
                HashMap<String, String> paramsMap = new HashMap<>();
                paramsMap.put("userid",String.valueOf(sessionManager.getUserId()));
                paramsMap.put("lessonid",String.valueOf(lesson.getId()));
                RequestSender.make("LessonsRecyclerAdapter", RequestSender.BUY_SERVICE, paramsMap, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Hard.d("response" + response);
                        sessionManager.addLessonOwned(lesson);
                        onTokensSpendedListener.onTokensSpended();
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                    }
                });
            }
        })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
        .setIcon(R.drawable.ic_currency)
        .show();
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.thumbnail) ImageView thumbnail;
        @BindView(R.id.tvtitle) TextView title;
        @BindView(R.id.tvprice) TextView price;
        @BindView(R.id.tvdescription) TextView description;
        @BindView(R.id.btnbuy) TextView buttonBuy;
        @BindView(R.id.main_container) ViewGroup mainContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnTokensSpended{
        public void onTokensSpended();
    }

}
