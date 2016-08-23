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
public class GetUserGalleryDao extends QueryManager<GsonClassGallery> {

    private String user_id;

    public GetUserGalleryDao(String user_id) {

        this.user_id = user_id;

/*UserId*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", user_id)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL + "getgallery").post(formBody).build();

        return request;
    }

    @Override
    protected GsonClassGallery parseResponse(String jsonResponse) {

        Log.e("", "responce --- " + jsonResponse);

        GsonClassGallery agents = null;
        try {
            Gson gson = new GsonBuilder().create();
            agents = gson.fromJson(jsonResponse, GsonClassGallery.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return agents;
    }

}
