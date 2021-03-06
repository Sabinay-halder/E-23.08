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
public class SetDislikeDao extends QueryManager<GsonClass> {

    String user_id, sender_id;

    public SetDislikeDao(String user_id, String sender_id) {

        this.user_id = user_id;
        this.sender_id = sender_id;

/*user_id,block_id*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("block_id", sender_id)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL + "setdislikeuser").post(formBody).build();

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
