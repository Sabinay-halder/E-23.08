package com.widevision.quemvaita.dao;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widevision.quemvaita.util.Constant;

import java.io.File;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by mercury-one on 7/4/16.
 */
public class UpdateProfileDao extends QueryManager<GsonClass> {


    String user_id, first_name, last_name, email, gender, profile_pic, facebook_id, device_token;
    RequestBody formBody;
    File profile;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public UpdateProfileDao(String user_id, String first_name, String last_name, String email, String gender, String profile_pic, String facebook_id, String device_token,String about, String interest) {

        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.gender = gender;
        this.profile_pic = profile_pic;
        this.facebook_id = facebook_id;
        this.device_token = device_token;
/*(first_name,last_name,email,gender,profile_pic,facebook_id,device_type,device_token)*/
        /*(first_name,last_name,email,gender,profile_pic, user_id)*/
        formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("first_name", first_name)
                .addFormDataPart("last_name", last_name)
                .addFormDataPart("email", email)
                .addFormDataPart("gender", gender)
                .addFormDataPart("profile_pic", profile_pic)
                .addFormDataPart("device_type", "0")
                .addFormDataPart("device_token", device_token)
                .addFormDataPart("interest", interest)
                .addFormDataPart("about", about)
                .build();
    }

    public UpdateProfileDao(String user_id, String first_name, String last_name, String email, String gender, File profile_pic, String facebook_id, String device_token, String about, String interest) {

        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.gender = gender;
        this.profile = profile_pic;
        this.facebook_id = facebook_id;
        this.device_token = device_token;
/*(first_name,last_name,email,gender,profile_pic,facebook_id,device_type,device_token)*/
        /*(first_name,last_name,email,gender,profile_pic, user_id)*/
        formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("first_name", first_name)
                .addFormDataPart("last_name", last_name)
                .addFormDataPart("email", email)
                .addFormDataPart("gender", gender)
                .addFormDataPart("device_type", "0")
                .addFormDataPart("device_token", device_token)
                .addFormDataPart("interest", interest)
                .addFormDataPart("about", about)
                .addFormDataPart("img_file", "profile_pic.png", RequestBody.create(MEDIA_TYPE_PNG, profile_pic))
                .build();
    }


    @Override
    protected Request.Builder buildRequest() {


        Request.Builder request = new Request.Builder();
        request.url(Constant.URL + "updateprofile").post(formBody).build();

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
