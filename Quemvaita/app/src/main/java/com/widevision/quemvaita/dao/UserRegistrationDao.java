package com.widevision.quemvaita.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import com.widevision.quemvaita.util.Constant;

/**
 * Created by mercury-one on 7/4/16.
 */
public class UserRegistrationDao extends QueryManager<GsonClass> {
    String name, gender, about, email, password, address, activity;

    public UserRegistrationDao(String name, String gender, String about, String email, String password, String address, String activity) {
        this.name = name;
        this.gender = gender;
        this.about = about;
        this.email = email;
        this.password = password;
        this.address = address;
        this.activity = activity;
    }

    @Override
    protected Request.Builder buildRequest() {
        RequestBody formBody = new FormBody.Builder().add("tag", "event_registration").add("field_data", name)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL).post(formBody).build();

        return request;
    }

    @Override
    protected GsonClass parseResponse(String jsonResponse) {
        Gson gson = new GsonBuilder().create();
        try {
            GsonClass agents = gson.fromJson(jsonResponse, GsonClass.class);
            return agents;
        } catch (Exception e) {
            return null;
        }
    }
}

