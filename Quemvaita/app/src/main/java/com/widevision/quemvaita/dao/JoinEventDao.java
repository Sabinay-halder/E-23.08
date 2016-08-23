package com.widevision.quemvaita.dao;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widevision.quemvaita.util.Constant;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by mercury-one on 7/4/16.
 */
public class JoinEventDao extends QueryManager<GsonClass> {

    private String user_id, event_id, join_status;

    public JoinEventDao(String user_id, String event_id, String join_status) {

        this.user_id = user_id;
        this.event_id = event_id;
        this.join_status = join_status;
/*(user_id,event_id)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("event_id", event_id)
                .add("join_status", join_status)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL + "setlikeevent").post(formBody).build();

        return request;
    }

    @Override
    protected GsonClass parseResponse(String jsonResponse) {

        Log.e("", "responce --- " + jsonResponse);

        GsonClass agents = null;
        try {
            Gson gson = new GsonBuilder().create();
            agents = gson.fromJson(jsonResponse, GsonClass.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return agents;
    }

}
