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
public class UpdateGalleryDao extends QueryManager<GsonClass> {

    String user_id, gallery_data;

    public UpdateGalleryDao(String user_id, String gallery_data) {

        this.user_id = user_id;
        this.gallery_data = gallery_data;
/*(user_id, gallery_data)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .add("gallery_data", gallery_data)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL + "updategallery").post(formBody).build();

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
