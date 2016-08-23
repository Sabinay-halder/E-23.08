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
public class GetEventSelectedMemberDao extends QueryManager<GsonEventMemberClass> {

    String user_id;

    public GetEventSelectedMemberDao(String user_id) {

        this.user_id = user_id;

/*l(user_id)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL+"getiselectedevent").post(formBody).build();

        return request;
    }

    @Override
    protected GsonEventMemberClass parseResponse(String jsonResponse) {

        Log.e("", "responce --- " + jsonResponse);

        GsonEventMemberClass agents = null;
        try {
            Gson gson = new GsonBuilder().create();
            agents = gson.fromJson(jsonResponse, GsonEventMemberClass.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return agents;
    }

}
