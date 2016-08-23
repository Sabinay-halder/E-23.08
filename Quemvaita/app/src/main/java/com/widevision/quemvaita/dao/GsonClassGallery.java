package com.widevision.quemvaita.dao;

import java.util.ArrayList;

/**
 * Created by mercury-one on 7/4/16.
 */
public class GsonClassGallery {
    public String success;
    public String message;
    public ArrayList<Data> data;

    public class Data {
        public String position;
        public String imgurl;
    }
}