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
public class UploadGalleryImageDao extends QueryManager<GsonClassGallery> {

    private RequestBody formBody;
    private File img_file;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public UploadGalleryImageDao(String user_id, String img_url, String position) {

/*(user_id,img_url,img_file)*/

        formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("img_url", img_url)
                .addFormDataPart("img_file", "")
                .addFormDataPart("position", position)
                .build();
    }

    public UploadGalleryImageDao(String user_id, File img_file, String position) {

/*(user_id,img_url,img_file)*/

        formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("img_url", "")
                .addFormDataPart("position", position)
                .addFormDataPart("img_file", "profile_pic.png", RequestBody.create(MEDIA_TYPE_PNG, img_file))
                .build();
    }

    @Override
    protected Request.Builder buildRequest() {

        Request.Builder request = new Request.Builder();
        request.url(Constant.URL + "setgallery").post(formBody).build();

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
