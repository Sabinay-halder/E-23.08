package com.widevision.quemvaita.dao;

import android.location.Location;
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
public class SetProfileDao extends QueryManager<GsonClass> {


    String first_name, last_name, email, gender, profile_pic, facebook_id, device_token;
    private Location location;

    public SetProfileDao(String first_name, String last_name, String email, String gender, String profile_pic, String facebook_id, String device_token, Location location) {

        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.gender = gender;
        this.profile_pic = profile_pic;
        this.facebook_id = facebook_id;
        this.device_token = device_token;
        this.location = location;
/*(first_name,last_name,email,gender,profile_pic,facebook_id,device_type,device_token)*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("first_name", first_name)
                .add("last_name", last_name)
                .add("email", email)
                .add("gender", gender)
                .add("profile_pic", profile_pic)
                .add("facebook_id", facebook_id)
                .add("device_type", "0")
                .add("device_token", device_token)
                .add("lat", "" + location.getLatitude())
                .add("long", "" + location.getLongitude())
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.SET_PROFILE_URL).post(formBody).build();

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
