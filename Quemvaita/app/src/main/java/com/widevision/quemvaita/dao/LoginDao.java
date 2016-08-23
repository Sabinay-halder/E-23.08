package com.widevision.quemvaita.dao;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import com.widevision.quemvaita.util.Constant;

/**
 * Created by mercury-one on 7/4/16.
 */
public class LoginDao  extends QueryManager<GsonClass> {


    String email, pin;

    public LoginDao(String email, String pin) {

        this.email = email;
        this.pin = pin;
/*login tag:login
login field:
email
password*/
    }

    @Override
    protected Request.Builder buildRequest() {

        RequestBody formBody = new FormBody.Builder()
                .add("tag", "login")
                .add("email", email)
                .add("password", pin)
                .build();
        Request.Builder request = new Request.Builder();
        request.url(Constant.URL).post(formBody).build();

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
